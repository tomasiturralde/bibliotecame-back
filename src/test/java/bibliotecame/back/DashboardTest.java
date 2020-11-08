package bibliotecame.back;

import bibliotecame.back.Book.BookController;
import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Dashboard.DashboardController;
import bibliotecame.back.Dashboard.DashboardInformation;
import bibliotecame.back.Loan.LoanController;
import bibliotecame.back.Loan.LoanModel;
import bibliotecame.back.Loan.LoanService;
import bibliotecame.back.Review.ReviewService;
import bibliotecame.back.Sanction.SanctionController;
import bibliotecame.back.Sanction.SanctionService;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DashboardTest {

    @Autowired
    private DashboardController dashboardController;

    @Autowired
    private LoanService loanService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private BookController bookController;

    @Autowired
    private UserService userService;

    Authentication authentication;
    SecurityContext securityContext;
    UserModel admin;

    @BeforeAll
    void setUp(){
        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        admin = new UserModel(RandomStringGenerator.getAlphabeticString(7)+"@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        admin.setAdmin(true);
        userService.saveUser(admin);
    }

    private void setSecurityContext(UserModel user){
        List<GrantedAuthority> auths = new ArrayList<>();
        User securityUser = new User(user.getEmail(), user.getPassword(), auths);
        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void getDashboardInfoOK(){
        setSecurityContext(admin);

        BookModel book1 = (BookModel) bookController.createBook(new BookModel("book1", 2010, "author " + RandomStringGenerator.getAlphabeticString(4), "publisher" + RandomStringGenerator.getAlphabeticString(4))).getBody();
        BookModel book2 = (BookModel) bookController.createBook(new BookModel("book2", 2014, "author " + RandomStringGenerator.getAlphabeticString(4), "publisher" + RandomStringGenerator.getAlphabeticString(4))).getBody();

        UserModel user1 = new UserModel(RandomStringGenerator.getAlphabeticString(7)+"@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        userService.saveUser(user1);

        List<CopyModel> copies1 = new ArrayList<>();
        copies1.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies1.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies1.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies1.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));

        assert book1 != null;
        book1.setCopies(copies1);
        bookController.checkAndUpdateBook(book1.getId(), book1);

        List<CopyModel> copies2 = new ArrayList<>();
        copies2.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies2.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies2.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies2.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));

        assert book2 != null;
        book2.setCopies(copies2);
        bookController.checkAndUpdateBook(book2.getId(), book2);

        LoanModel delayed = new LoanModel(book1.getCopies().get(0), LocalDate.now().minus(Period.ofDays(7)), LocalDate.now().minus(Period.ofDays(1)));
        delayed.setWithdrawalDate(LocalDate.now().minus(Period.ofDays(4)));
        loanService.saveLoan(delayed);

        LoanModel withdrawn = new LoanModel(book1.getCopies().get(0), LocalDate.now().minus(Period.ofDays(3)), LocalDate.now().plus(Period.ofDays(1)));
        withdrawn.setWithdrawalDate(LocalDate.now().minus(Period.ofDays(1)));
        loanService.saveLoan(withdrawn);

        List<LoanModel> loans = new ArrayList<>();
        loans.add(delayed);
        loans.add(withdrawn);

        user1.setLoans(loans);
        userService.saveUser(user1);

        assertThat(dashboardController.getInformation().getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        DashboardInformation info = (DashboardInformation) dashboardController.getInformation().getBody();

        assertThat(info.getAmountOfBooks()).isGreaterThan(1);
        assertThat(info.getAmountOfStudents()).isGreaterThan(0);
        assertThat(info.getDelayedLoans()).isGreaterThan(0);
        assertThat(info.getWithdrawnLoans()).isGreaterThan(0);

    }


    @Test
    public void getDashboardInfoUNAUTHORIZED(){

        UserModel user1 = new UserModel(RandomStringGenerator.getAlphabeticString(7)+"@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        user1.setAdmin(false);
        userService.saveUser(user1);
        setSecurityContext(admin);

        BookModel book1 = (BookModel) bookController.createBook(new BookModel("book1", 2010, "author " + RandomStringGenerator.getAlphabeticString(4), "publisher" + RandomStringGenerator.getAlphabeticString(4))).getBody();
        BookModel book2 = (BookModel) bookController.createBook(new BookModel("book2", 2014, "author " + RandomStringGenerator.getAlphabeticString(4), "publisher" + RandomStringGenerator.getAlphabeticString(4))).getBody();



        List<CopyModel> copies1 = new ArrayList<>();
        copies1.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies1.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies1.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies1.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));

        assert book1 != null;
        book1.setCopies(copies1);
        bookController.checkAndUpdateBook(book1.getId(), book1);

        List<CopyModel> copies2 = new ArrayList<>();
        copies2.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies2.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies2.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies2.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));

        assert book2 != null;
        book2.setCopies(copies2);
        bookController.checkAndUpdateBook(book2.getId(), book2);

        LoanModel delayed = new LoanModel(book1.getCopies().get(0), LocalDate.now().minus(Period.ofDays(7)), LocalDate.now().minus(Period.ofDays(1)));
        delayed.setWithdrawalDate(LocalDate.now().minus(Period.ofDays(4)));
        loanService.saveLoan(delayed);

        LoanModel withdrawn = new LoanModel(book1.getCopies().get(0), LocalDate.now().minus(Period.ofDays(3)), LocalDate.now().plus(Period.ofDays(1)));
        withdrawn.setWithdrawalDate(LocalDate.now().minus(Period.ofDays(1)));
        loanService.saveLoan(withdrawn);

        List<LoanModel> loans = new ArrayList<>();
        loans.add(delayed);
        loans.add(withdrawn);

        user1.setLoans(loans);
        userService.saveUser(user1);

        setSecurityContext(user1);

        assertThat(dashboardController.getInformation().getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

    }


}
