package bibliotecame.back.Loan;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.Email.Email;
import bibliotecame.back.Email.EmailSender;
import bibliotecame.back.ErrorMessage;
import bibliotecame.back.Extension.ExtensionService;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.InternetAddress;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/loan")
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;
    private final BookService bookService;
    private final CopyService copyService;
    private final ExtensionService extensionService;

    @Autowired
    public LoanController(LoanService loanService, UserService userService, BookService bookService, CopyService copyService, ExtensionService extensionService) {
        this.loanService = loanService;
        this.userService = userService;
        this.bookService = bookService;
        this.copyService = copyService;
        this.extensionService = extensionService;
    }

    @GetMapping(value = "/history")
    public ResponseEntity getAllReturnedLoans(
            @Valid @RequestParam(value = "page") int page,
            @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @Valid @RequestParam(value = "search") String search
            ) {

        if (size == 0) size = 10;
        UserModel user = userService.findLogged();

        if(user.isAdmin()) return unauthorizedActionError();

        Page<LoanDisplay> loans = loanService.getReturnedLoansPage(page, size, user, search.toLowerCase());

        return ResponseEntity.ok(loans);
    }

    @PostMapping("/{bookId}")
    public ResponseEntity createLoan(@PathVariable Integer bookId){

        UserModel user = userService.findLogged();
        if(user.isAdmin()) return unauthorizedActionError();

        if(!bookService.exists(bookId)) return unexistingBookError();
        BookModel book = bookService.findBookById(bookId);

        return checkAndCreateLoan(user, book);
    }

    public ResponseEntity checkAndCreateLoan(UserModel user, BookModel book){

        List<CopyModel> copies = bookService.getAvailableCopies(book);
        if(userService.hasLoanOfBook(user, book)) return  new ResponseEntity<>(new ErrorMessage("¡Usted ya tiene solicitado un prestamo de este libro!"),HttpStatus.NOT_ACCEPTABLE);
        if(copies.isEmpty()) return new ResponseEntity<>(new ErrorMessage("¡Lo sentimos! ¡Este libro ya no tiene ejemplates disponibles!"),HttpStatus.EXPECTATION_FAILED);
        if(userService.getActiveLoans(user).size() >= 5) return new ResponseEntity<>(new ErrorMessage("¡No se pudo realizar el préstamo ya que tiene demasiados prestamos activos!"),HttpStatus.TOO_MANY_REQUESTS);
        if(!userService.getDelayedLoans(user).isEmpty()) return  new ResponseEntity<>(new ErrorMessage("¡Debe devolver sus prestamos atrasados antes de solicitar nuevos!"),HttpStatus.BAD_REQUEST);

        CopyModel copyToLoan = copies.get(0);
        copyToLoan.setBooked(true);
        copyService.saveCopy(copyToLoan);

        LocalDate today = LocalDate.now();
        LoanModel loan = new LoanModel(copyToLoan, today, today.plus(Period.ofDays(5)));
        LoanModel savedLoanModel = loanService.saveLoan(loan);

        userService.addLoan(user, savedLoanModel);

        return ResponseEntity.ok(savedLoanModel);
    }

    @GetMapping("/actives")
    public ResponseEntity getAllActiveLoans(){
        if(getLogged().isAdmin()) return unauthorizedActionError();
        List<LoanModel> loans = getLogged().getLoans().stream().filter(loanModel -> loanModel.getReturnDate()==null)
                .sorted(Comparator.comparing(LoanModel::getExpirationDate))
                .collect(Collectors.toList());
        List<LoanDisplay> loansDisplay = new ArrayList<>();
        for (LoanModel loan : loans){
            LoanDisplay loanDisplay = loanService.turnLoanModalToDisplay(loan, Optional.empty(), true);

            loansDisplay.add(loanService.setLoanDisplayStatus(loan, loanDisplay));
        }
        return new ResponseEntity<>(loansDisplay,HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<Page<LoanDisplay>> getAllLoansAdmin(
            @Valid @RequestParam(value = "page") int page,
            @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @Valid @RequestParam(value = "search") String search
    ){
        if (size == 0) size = 10;
        if(!getLogged().isAdmin()) return unauthorizedActionError();
        search=search.toLowerCase();
        Page<LoanDisplay> loans = loanService.getLoansPage(page,size,search);
        return new ResponseEntity<>(loans,HttpStatus.OK);
    }

    private UserModel getLogged(){
        return userService.findLogged();
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity setWithdrawDate(@PathVariable Integer id){
        if(!getLogged().isAdmin()) return unauthorizedActionError();
        return setWithdrawalPostAdminCheck(id);
    }

    public ResponseEntity setWithdrawalPostAdminCheck(Integer id){
        LoanModel loanModel;
        try{
            loanModel = loanService.getLoanById(id);
            if(loanModel.getReturnDate()!=null) return new ResponseEntity<>(new ErrorMessage("¡Este prestamo ya fué devuelto!"),HttpStatus.BAD_REQUEST);
            if(loanModel.getWithdrawalDate()!=null) return new ResponseEntity<>(new ErrorMessage("¡Este prestamo ya fué retirado!"),HttpStatus.BAD_REQUEST);
            loanModel.setWithdrawalDate( LocalDate.now() );
            loanService.saveLoan(loanModel);
            return new ResponseEntity<>(loanModel,HttpStatus.OK);
        }
        catch (NotFoundException n) { return unexistingLoanError(); }
    }

    @PutMapping("/{id}/return")
    public ResponseEntity setReturnDate(@PathVariable Integer id){
        if(!getLogged().isAdmin()) return unauthorizedActionError();
        return setReturnPostAdminCheck(id);
    }

    public ResponseEntity setReturnPostAdminCheck(Integer id){
        LoanModel loanModel;
        try{
            loanModel = loanService.getLoanById(id);
            if(loanModel.getReturnDate()!=null) return new ResponseEntity<>(new ErrorMessage("¡Este prestamo ya fué devuelto!"),HttpStatus.BAD_REQUEST);
            if(loanModel.getWithdrawalDate()==null) return new ResponseEntity<>(new ErrorMessage("¡Este prestamo aún no fué retirado!"),HttpStatus.BAD_REQUEST);
            loanModel.setReturnDate( LocalDate.now() );
            if(loanModel.getExtension()!=null){
                loanModel.getExtension().setActive(false);
                extensionService.saveExtension(loanModel.getExtension());
            }
            loanModel.getCopy().setBooked(false);
            copyService.saveCopy(loanModel.getCopy());
            loanService.saveLoan(loanModel);
            return new ResponseEntity<>(loanModel,HttpStatus.OK);
        }
        catch (NotFoundException n) { return unexistingLoanError(); }
    }



    private ResponseEntity unauthorizedActionError(){
        return new ResponseEntity<>(new ErrorMessage("¡Usted no está autorizado a realizar esta acción!"),HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity unexistingLoanError(){
        return new ResponseEntity<>(new ErrorMessage("¡El prestamo solicitado no existe!"),HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity unexistingBookError(){
        return new ResponseEntity<>(new ErrorMessage("¡El libro solicitado no existe!"),HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/user/clear")
    public ResponseEntity expiredLoansClearer(){

        UserModel user = userService.findLogged();
        if(user.isAdmin()) return unauthorizedActionError();

        loanService.deleteExpirationLoansOfUsers(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/admin/clear")
    public ResponseEntity everyExpiredLoanClearer(){

        UserModel user = userService.findLogged();
        if(!user.isAdmin()) return unauthorizedActionError();

        loanService.deleteEveryExpiredLoan();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/delayed/check")
    public ResponseEntity checkDelayedLoans(){

        if(!getLogged().isAdmin()) return unauthorizedActionError();

        return new ResponseEntity(loanService.getDelayedLoans().size()>0,HttpStatus.OK);
    }

    @GetMapping("/delayed/notify")
    public ResponseEntity notifyDelayedLoans(){

        if(!getLogged().isAdmin()) return unauthorizedActionError();
        EmailSender sender = new EmailSender();

        List<DelayedLoanDetails> delayedLoans = new ArrayList<>();
        loanService.getDelayedLoans().forEach(loanModel -> delayedLoans.add(loanService.turnLoanModalToDelayedDetails(loanModel)));

        List<String> deliveredEmails = new ArrayList<>();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Email> emailsToSend = new ArrayList<>();

        delayedLoans.forEach(lm -> {
            if(!deliveredEmails.contains(lm.getUserEmail())) {
                String body = "Estimado " + userService.findUserByEmail(lm.getUserEmail()).getFirstName() + ": <br>" +
                        "Le recordamos que actualmente tiene los siguientes prestamos ATRASADOS: <ul>";
                List<DelayedLoanDetails> userLoans = delayedLoans.stream().filter(loan -> loan.getUserEmail().equals(lm.getUserEmail())).collect(Collectors.toList());
                for (DelayedLoanDetails loan : userLoans) {
                    body += "<br><li><strong>Libro:</strong> <i>\""+loan.getBookTitle()+"\"</i></li>" +
                            "<li><strong>Fecha de Retiro:</strong> "+loan.getWithdrawDate().format(formatters)+"</li>"+
                            "<li><strong>Fecha de Vencimiento:</strong> "+loan.getReturnDate().format(formatters)+"</li>";
                }
                body += "</ul><br>Atte, Administración Bibliotecame.";
                try {
                    emailsToSend.add(new Email(new InternetAddress(lm.getUserEmail(),lm.getUserName()),"Prestamos atrasados",body));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                deliveredEmails.add(lm.getUserEmail());
            }
        });

        emailsToSend.forEach(sender::notifyWithGmail);
        //The responseEntity holds how many mails it sent in case notification/validation is needed in the future.
        return new ResponseEntity(emailsToSend.size(),HttpStatus.OK);
    }
}
