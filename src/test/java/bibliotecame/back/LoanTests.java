package bibliotecame.back;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookRepository;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyRepository;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.Loan.*;
import bibliotecame.back.Tag.TagRepository;
import bibliotecame.back.Tag.TagService;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserRepository;
import bibliotecame.back.User.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoanTests {


    @Mock
    private LoanController loanController;
    @Mock
    private LoanService loanService;
    @Autowired
    private LoanRepository loanRepository;

    @Mock
    public BookService bookService;
    @Autowired
    private BookRepository bookRepository;

    @Mock
    private TagService tagService;
    @Autowired
    private TagRepository tagRepository;

    @Mock
    private CopyService copyService;
    @Autowired
    private CopyRepository copyRepository;

    @Mock
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    String authorForSavedBook;
    String publisherForSavedBook;

    Authentication authentication;
    SecurityContext securityContext;

    UserModel admin;

    BookModel theBook;

    @BeforeAll
    void setUp() {
        copyService = new CopyService(copyRepository);
        tagService = new TagService(tagRepository);
        bookService = new BookService(bookRepository, tagService);
        userService = new UserService(userRepository, bookService);
        loanService = new LoanService(loanRepository);
        loanController = new LoanController(loanService, userService, bookService, copyService);

        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        authorForSavedBook = RandomStringGenerator.getAlphabeticString(20);
        publisherForSavedBook = RandomStringGenerator.getAlphabeticString(20);

        theBook = bookService.saveBook(new BookModel("theBook", 2000, authorForSavedBook, publisherForSavedBook));

        admin = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        admin.setAdmin(true);
        userRepository.save(admin);

    }

    private void setSecurityContext(UserModel user){
        List<GrantedAuthority> auths = new ArrayList<>();
        User securityUser = new User(user.getEmail(), user.getPassword(), auths);
        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testCreateLoanOK(){

        UserModel notAdmin = new UserModel(RandomStringGenerator.getAlphaNumericString(10) + "@mail.austral.edu.ar", "password", "Name", "Surname", "12341234");
        userRepository.save(notAdmin);
        setSecurityContext(notAdmin);

        BookModel interBook = bookService.saveBook(new BookModel(RandomStringGenerator.getAlphabeticString(10), 2000, authorForSavedBook, publisherForSavedBook));

        List<CopyModel> copies = new ArrayList<>();
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        interBook.setCopies(copies);
        bookService.updateBook(interBook.getId(), interBook);

        assertThat(loanController.createLoan(interBook.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    @Test
    public void testCreateLoanUNAUTHORIZED(){

        setSecurityContext(admin);
        BookModel interBook = bookService.saveBook(new BookModel(RandomStringGenerator.getAlphabeticString(10), 2000, authorForSavedBook, publisherForSavedBook));

        List<CopyModel> copies = new ArrayList<>();
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        interBook.setCopies(copies);
        bookService.updateBook(interBook.getId(), interBook);

        assertThat(loanController.createLoan(interBook.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testCreateLoanEPECTATION_FAILEDForNoCopies(){

        UserModel notAdmin = new UserModel(RandomStringGenerator.getAlphaNumericString(10) + "@mail.austral.edu.ar", "password", "Name", "Surname", "12341234");
        userRepository.save(notAdmin);
        setSecurityContext(notAdmin);

        assertThat(loanController.createLoan(theBook.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    public void testCreateLoanNOT_ACCEPTABLEForRepeatedBook(){

        UserModel notAdmin = new UserModel(RandomStringGenerator.getAlphaNumericString(10) + "@mail.austral.edu.ar", "password", "Name", "Surname", "12341234");
        userRepository.save(notAdmin);
        BookModel bookModel = new BookModel(RandomStringGenerator.getAlphabeticString(7), 1999, authorForSavedBook, publisherForSavedBook);

        List<CopyModel> copies = new ArrayList<>();
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        bookModel.setCopies(copies);
        bookService.saveBook(bookModel);

        List<LoanModel> loans = new ArrayList<>();
        loans.add(new LoanModel(copies.get(0), LocalDate.now(), LocalDate.now().plus(Period.ofDays(5))));
        notAdmin.setLoans(loans);
        userService.saveUser(notAdmin);

        setSecurityContext(notAdmin);

        assertThat(loanController.createLoan(bookModel.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void testCreateLoanBAD_REQUESTForDelayedLoans(){
        UserModel notAdmin = new UserModel(RandomStringGenerator.getAlphaNumericString(10) + "@mail.austral.edu.ar", "password", "Name", "Surname", "12341234");
        userRepository.save(notAdmin);

        BookModel bookModel = new BookModel(RandomStringGenerator.getAlphabeticString(7), 1999, authorForSavedBook, publisherForSavedBook);

        List<CopyModel> copies = new ArrayList<>();
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        bookModel.setCopies(copies);
        bookService.saveBook(bookModel);

        List<LoanModel> loans = new ArrayList<>();
        loans.add(new LoanModel(copies.get(0), LocalDate.now(), LocalDate.now().minus(Period.ofDays(5))));
        notAdmin.setLoans(loans);
        userService.saveUser(notAdmin);

        setSecurityContext(notAdmin);
        assertThat(loanController.createLoan(bookModel.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testCreateLoanTOO_MANY_REQUESTSForTooManyLoans(){

        UserModel notAdmin = new UserModel(RandomStringGenerator.getAlphaNumericString(10) + "@mail.austral.edu.ar", "password", "Name", "Surname", "12341234");
        userRepository.save(notAdmin);
        BookModel bookModel1 = new BookModel(RandomStringGenerator.getAlphabeticString(7), 1999, authorForSavedBook, publisherForSavedBook);
        BookModel bookModel2 = new BookModel(RandomStringGenerator.getAlphabeticString(7), 1999, authorForSavedBook, publisherForSavedBook);
        BookModel bookModel3 = new BookModel(RandomStringGenerator.getAlphabeticString(7), 1999, authorForSavedBook, publisherForSavedBook);
        BookModel bookModel4 = new BookModel(RandomStringGenerator.getAlphabeticString(7), 1999, authorForSavedBook, publisherForSavedBook);
        BookModel bookModel5 = new BookModel(RandomStringGenerator.getAlphabeticString(7), 1999, authorForSavedBook, publisherForSavedBook);
        BookModel bookModel6 = new BookModel(RandomStringGenerator.getAlphabeticString(7), 1999, authorForSavedBook, publisherForSavedBook);

        List<CopyModel> copies6 = new ArrayList<>();
        copies6.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        bookModel6.setCopies(copies6);
        bookService.saveBook(bookModel6);

        List<CopyModel> all = new ArrayList<>();

        List<CopyModel> copies1 = new ArrayList<>();
        copies1.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        bookModel1.setCopies(copies1);
        bookService.saveBook(bookModel1);
        all.add(bookModel1.getCopies().get(0));


        List<CopyModel> copies2 = new ArrayList<>();
        copies2.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        bookModel2.setCopies(copies2);
        bookService.saveBook(bookModel2);
        all.add(bookModel2.getCopies().get(0));

        List<CopyModel> copies3 = new ArrayList<>();
        copies3.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        bookModel3.setCopies(copies3);
        bookService.saveBook(bookModel3);
        all.add(bookModel3.getCopies().get(0));

        List<CopyModel> copies4 = new ArrayList<>();
        copies4.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        bookModel4.setCopies(copies4);
        bookService.saveBook(bookModel4);
        all.add(bookModel4.getCopies().get(0));

        List<CopyModel> copies5 = new ArrayList<>();
        copies5.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        bookModel5.setCopies(copies5);
        bookService.saveBook(bookModel5);
        all.add(bookModel5.getCopies().get(0));


        List<LoanModel> loans = new ArrayList<>();
        for(CopyModel copy : all){
            loans.add(new LoanModel(copy, LocalDate.now(), LocalDate.now().plus(Period.ofDays(5))));
        }
        notAdmin.setLoans(loans);
        userService.saveUser(notAdmin);

        setSecurityContext(notAdmin);

        assertThat(loanController.createLoan(bookModel6.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.TOO_MANY_REQUESTS);

    }


    @Test
    void testGetHistory(){

        UserModel notAdmin = new UserModel(RandomStringGenerator.getAlphaNumericString(10) + "@mail.austral.edu.ar", "password", "Name", "Surname", "12341234");
        userRepository.save(notAdmin);
        setSecurityContext(notAdmin);

        BookModel interBook = bookService.saveBook(new BookModel(RandomStringGenerator.getAlphabeticString(10), 2000, authorForSavedBook, publisherForSavedBook));

        List<CopyModel> copies = new ArrayList<>();
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        interBook.setCopies(copies);
        bookService.updateBook(interBook.getId(), interBook);

        LoanModel loan = loanController.createLoan(interBook.getId()).getBody();

        ResponseEntity<Page<LoanDisplay>> loans = loanController.getAllReturnedLoans(0,0);

        assertThat(loans.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(loans.getBody()).getTotalElements()).isEqualTo(0);

        assert loan != null;
        loan.setReturnDate(LocalDate.now().plus(Period.ofDays(1)));
        loanService.saveLoan(loan);

        loans = loanController.getAllReturnedLoans(0,0);

        assertThat(loans.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(loans.getBody()).getTotalElements()).isEqualTo(1);
    }

    @Test
    public void assertThatLoansGetReturnedInOrderAndReturnedLoansDontAppearOnTheList(){
        UserModel user = new UserModel("khalilTesteandoLoans@mail.austral.edu.ar","khalil1234","khalil","LoanTester","1111111");
        userService.saveUser(user);
        setSecurityContext(user);

        BookModel bookModeltoLoan1 = new BookModel(RandomStringGenerator.getAlphabeticString(7), 1999, authorForSavedBook, publisherForSavedBook);
        BookModel bookModeltoLoan2 = new BookModel(RandomStringGenerator.getAlphabeticString(7), 1999, authorForSavedBook, publisherForSavedBook);
        bookService.saveBook(bookModeltoLoan1);
        bookService.saveBook(bookModeltoLoan2);

        List<CopyModel> copiestoLoan1 = new ArrayList<>();
        copiestoLoan1.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        List<CopyModel> copiestoLoan2 = new ArrayList<>();
        copiestoLoan2.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        bookModeltoLoan1.setCopies(copiestoLoan1);
        bookModeltoLoan2.setCopies(copiestoLoan2);
        bookService.saveBook(bookModeltoLoan1);
        bookService.saveBook(bookModeltoLoan2);

        //After all the setup, we create a loan for each book

        loanController.createLoan(bookModeltoLoan1.getId());
        loanController.createLoan(bookModeltoLoan2.getId());

        LoanModel loan = userService.findLogged().getLoans().get(0);
        loan.setWithdrawalDate(LocalDate.now());
        loanService.saveLoan(loan);

        //We edit the first loan, so by default it will be at the END of the list
        //But as the controller returns it by date, it should still be first

        assertThat(loan.getId()).isEqualTo(loanController.getAllActiveLoans().getBody().get(0).getId());

        //It should be returning both loans though, because neither was returned

        assertThat(loanController.getAllActiveLoans().getBody().size()).isEqualTo(2);

        //Finally, if we return one of the loans, it should not be in the active list

        loan.setReturnDate(LocalDate.now());
        loanService.saveLoan(loan);
        assertThat(loanController.getAllActiveLoans().getBody().size()).isEqualTo(1);
    }
}