package bibliotecame.back.Loan;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
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

    @PostMapping("loan/{bookId}")
    public ResponseEntity<LoanModel> createLoan(@PathVariable Integer bookId){

        UserModel user = userService.findLogged();
        if(user.isAdmin()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        BookModel book = bookService.findBookById(bookId);

        return checkAndCreateLoan(user, book);
    }

    public ResponseEntity<LoanModel> checkAndCreateLoan(UserModel user, BookModel book){

        List<CopyModel> copies = bookService.getAvailableCopies(book);
        if(copies.isEmpty()) return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        if(userService.getActiveLoans(user).size() >= 5) return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        if(!userService.getDelayedLoans(user).isEmpty()) return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(userService.hasLoanOfBook(user, book)) return  new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        CopyModel copyToLoan = copies.get(0);
        copyToLoan.setBooked(true);
        copyService.saveCopy(copyToLoan);

        LocalDate today = LocalDate.now();
        LoanModel loan = new LoanModel(copyToLoan, today, today.plus(Period.ofDays(5)));
        LoanModel savedLoanModel = loanService.saveLoan(loan);

        userService.addLoan(user, savedLoanModel);

        return ResponseEntity.ok(savedLoanModel);
    }

    @PutMapping("loan/{id}/withdraw")
    public ResponseEntity<LoanModel> setWithdrawDate(@PathVariable Integer id){
        if(!userService.findLogged().isAdmin()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        LoanModel loanModel;
        try{
            loanModel = loanService.getLoanById(id);
            if(loanModel.getReturnDate()!=null || loanModel.getWithdrawalDate()!=null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            loanModel.setWithdrawalDate( LocalDate.now() );
            loanService.saveLoan(loanModel);
            return new ResponseEntity<>(loanModel,HttpStatus.OK);
        }
        catch (NotFoundException n) { return new ResponseEntity<>(HttpStatus.BAD_REQUEST); }
    }

    @PutMapping("loan/{id}/return")
    public ResponseEntity<LoanModel> setReturnDate(@PathVariable Integer id){
        if(!userService.findLogged().isAdmin()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        LoanModel loanModel;
        try{
            loanModel = loanService.getLoanById(id);
            if(loanModel.getReturnDate()!=null || loanModel.getWithdrawalDate()==null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            loanModel.setReturnDate( LocalDate.now() );
            loanService.saveLoan(loanModel);
            return new ResponseEntity<>(loanModel,HttpStatus.OK);
        }
        catch (NotFoundException n) { return new ResponseEntity<>(HttpStatus.BAD_REQUEST); }
    }

}
