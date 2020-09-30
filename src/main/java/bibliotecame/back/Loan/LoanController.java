package bibliotecame.back.Loan;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
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

    @GetMapping(value = "/loans")
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

}
