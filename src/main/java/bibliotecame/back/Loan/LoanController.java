package bibliotecame.back.Loan;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/loan")
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;
    private final BookService bookService;
    private final CopyService copyService;

    @Autowired
    public LoanController(LoanService loanService, UserService userService, BookService bookService, CopyService copyService) {
        this.loanService = loanService;
        this.userService = userService;
        this.bookService = bookService;
        this.copyService = copyService;
    }

    @GetMapping(value = "/history")
    public ResponseEntity<Page<LoanDisplay>> getAllReturnedLoans(
            @Valid @RequestParam(value = "page") int page,
            @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ) {
        if (size == 0) size = 10;
        UserModel user = userService.findLogged();

        Page<LoanDisplay> loanPage;
        List<LoanModel> loans = userService.getReturnedLoansPage(page, size, user);
        List<LoanDisplay> loansDisplay = new ArrayList<>();

        for (LoanModel loan : loans){
            loansDisplay.add(userService.turnModalToDisplay(loan));
        }

        loanPage = new PageImpl<>(loansDisplay);

        return ResponseEntity.ok(loanPage);
    }

    @PostMapping("/{bookId}")
    public ResponseEntity createLoan(@PathVariable Integer bookId){

        UserModel user = userService.findLogged();
        if(user.isAdmin()) return unauthorizedActionError();

        BookModel book = bookService.findBookById(bookId);

        return checkAndCreateLoan(user, book);
    }

    public ResponseEntity checkAndCreateLoan(UserModel user, BookModel book){

        List<CopyModel> copies = bookService.getAvailableCopies(book);
        if(copies.isEmpty()) return new ResponseEntity<>("¡Lo sentimos! ¡Este libro ya no tiene ejemplates disponibles!",HttpStatus.EXPECTATION_FAILED);
        if(userService.getActiveLoans(user).size() >= 5) return new ResponseEntity<>("¡No se pudo realizar el préstamo ya que tiene demasiados prestamos activos!",HttpStatus.TOO_MANY_REQUESTS);
        if(!userService.getDelayedLoans(user).isEmpty()) return  new ResponseEntity<>("¡Debe devolver sus prestamos atrasados antes de solicitar nuevos!",HttpStatus.BAD_REQUEST);
        if(userService.hasLoanOfBook(user, book)) return  new ResponseEntity<>("¡Usted ya tiene solicitado un prestamo de este libro!",HttpStatus.NOT_ACCEPTABLE);

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
            LoanDisplay loanDisplay = userService.turnModalToDisplay(loan);

            if(loan.getExtension() != null)loanDisplay.setLoanStatus(LoanStatus.getFromInt(loan.getExtension().getStatus().ordinal()));
            else if(loan.getExpirationDate().isBefore(LocalDate.now())) loanDisplay.setLoanStatus(LoanStatus.DELAYED);
            else if(loan.getWithdrawalDate() != null) loanDisplay.setLoanStatus(LoanStatus.WITHDRAWN);
            else loanDisplay.setLoanStatus(LoanStatus.READY_FOR_WITHDRAWAL);

            loansDisplay.add(loanDisplay);
        }
        return new ResponseEntity<>(loansDisplay,HttpStatus.OK);
    }

    private UserModel getLogged(){
        return userService.findLogged();
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity setWithdrawDate(@PathVariable Integer id){
        if(!getLogged().isAdmin()) return unauthorizedActionError();
        LoanModel loanModel;
        try{
            loanModel = loanService.getLoanById(id);
            if(loanModel.getReturnDate()!=null) return new ResponseEntity<>("¡Este prestamo ya fué devuelto!",HttpStatus.BAD_REQUEST);
            if(loanModel.getWithdrawalDate()!=null) return new ResponseEntity<>("¡Este prestamo ya fué retirado!",HttpStatus.BAD_REQUEST);
            loanModel.setWithdrawalDate( LocalDate.now() );
            loanService.saveLoan(loanModel);
            return new ResponseEntity<>(loanModel,HttpStatus.OK);
        }
        catch (NotFoundException n) { return unexistingLoanError(); }
    }

    @PutMapping("/{id}/return")
    public ResponseEntity setReturnDate(@PathVariable Integer id){
        if(!getLogged().isAdmin()) return unauthorizedActionError();
        LoanModel loanModel;
        try{
            loanModel = loanService.getLoanById(id);
            if(loanModel.getReturnDate()!=null) return new ResponseEntity<>("¡Este prestamo ya fué devuelto!",HttpStatus.BAD_REQUEST);
            if(loanModel.getWithdrawalDate()==null) return new ResponseEntity<>("¡Este prestamo aún no fué retirado!",HttpStatus.BAD_REQUEST);
            loanModel.setReturnDate( LocalDate.now() );
            loanService.saveLoan(loanModel);
            return new ResponseEntity<>(loanModel,HttpStatus.OK);
        }
        catch (NotFoundException n) { return unexistingLoanError(); }
    }

    private ResponseEntity unauthorizedActionError(){
        return new ResponseEntity<>("¡No estás autorizado a realizar esta acción!",HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity unexistingLoanError(){
        return new ResponseEntity<>("¡El prestamo solicitado no existe!",HttpStatus.BAD_REQUEST);
    }

}
