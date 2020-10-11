package bibliotecame.back;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookRepository;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyRepository;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.Extension.*;
import bibliotecame.back.Loan.LoanController;
import bibliotecame.back.Loan.LoanModel;
import bibliotecame.back.Loan.LoanRepository;
import bibliotecame.back.Loan.LoanService;
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
public class ExtensionTests {

    @Mock
    private ExtensionController extensionController;
    @Mock
    private ExtensionService extensionService;
    @Autowired
    private ExtensionRepository extensionRepository;

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

    Authentication authentication;
    SecurityContext securityContext;

    String authorForSavedBook;
    String publisherForSavedBook;

    BookModel book1;
    BookModel book2;
    BookModel book3;
    BookModel book4;

    UserModel notAdmin1;
    UserModel notAdmin2;
    UserModel notAdmin3;
    UserModel admin;

    LoanModel loan1;
    LoanModel loan2;
    LoanModel loan3;
    LoanModel loan4;

    ExtensionModel extensionModel1;
    ExtensionModel extensionModel2;

    @BeforeAll
    void setUp(){
        copyService = new CopyService(copyRepository);
        tagService = new TagService(tagRepository);
        bookService = new BookService(bookRepository, tagService);
        userService = new UserService(userRepository, bookService);
        loanService = new LoanService(loanRepository, bookService, userService);
        extensionService = new ExtensionService(extensionRepository, loanService, userService);
        loanController = new LoanController(loanService, userService, bookService, copyService, extensionService);
        extensionController = new ExtensionController(extensionService, userService);

        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        admin = new UserModel(RandomStringGenerator.getAlphaNumericString(20)+"@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        admin.setAdmin(true);
        userRepository.save(admin);

        authorForSavedBook = RandomStringGenerator.getAlphabeticString(20);
        publisherForSavedBook = RandomStringGenerator.getAlphabeticString(20);

        book1 = bookService.saveBook(new BookModel("book 1", 2000, authorForSavedBook, publisherForSavedBook));
        book2 = bookService.saveBook(new BookModel("book 2", 2015, authorForSavedBook, publisherForSavedBook));
        book3 = bookService.saveBook(new BookModel("book 3", 2015, authorForSavedBook, publisherForSavedBook));
        book4 = bookService.saveBook(new BookModel("book 4", 2015, authorForSavedBook, publisherForSavedBook));

        List<CopyModel> copies1 = new ArrayList<>();
        copies1.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));

        book1.setCopies(copies1);
        bookService.updateBook(book1.getId(), book1);

        List<CopyModel> copies2 = new ArrayList<>();
        copies2.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));

        book2.setCopies(copies2);
        bookService.updateBook(book2.getId(), book2);


        List<CopyModel> copies3 = new ArrayList<>();
        copies3.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));

        book3.setCopies(copies3);
        bookService.updateBook(book3.getId(), book3);

        List<CopyModel> copies4 = new ArrayList<>();
        copies4.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));

        book4.setCopies(copies4);
        bookService.updateBook(book4.getId(), book4);

        notAdmin1 = new UserModel("facundo@mail.austral.edu.ar", "password", "Facundo", "Bocalandro", "12341234");
        notAdmin1.setAdmin(false);
        userRepository.save(notAdmin1);

        notAdmin2 = new UserModel("facundo@ing.austral.edu.ar", "password", "Facu", "Bocalandro", "12341234");
        notAdmin2.setAdmin(false);
        userRepository.save(notAdmin2);

        notAdmin3 = new UserModel("someone@mail.austral.edu.ar", "password", "Facundo", "Bocalandro", "12341234");
        notAdmin3.setAdmin(false);
        userRepository.save(notAdmin3);


        setSecurityContext(notAdmin1);
        loan1 = (LoanModel)loanController.createLoan(book1.getId()).getBody();

        setSecurityContext(notAdmin2);
        loan2 = (LoanModel)loanController.createLoan(book2.getId()).getBody();

        setSecurityContext(notAdmin3);
        loan3 = (LoanModel)loanController.createLoan(book3.getId()).getBody();
        loan4 = (LoanModel)loanController.createLoan(book4.getId()).getBody();

        extensionModel1 = (ExtensionModel)extensionController.createExtension(loan3.getId()).getBody();
        extensionModel2 = (ExtensionModel)extensionController.createExtension(loan4.getId()).getBody();
    }

    private void setSecurityContext(UserModel user){
        List<GrantedAuthority> auths = new ArrayList<>();
        User securityUser = new User(user.getEmail(), user.getPassword(), auths);
        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateExtensionOK(){
        setSecurityContext(notAdmin1);

        assertThat(extensionController.createExtension(loan1.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        setSecurityContext(notAdmin2);

        assertThat(extensionController.createExtension(loan2.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    @Test
    void testCreateExtensionUNAUTHORIZED(){
        setSecurityContext(admin);

        assertThat(extensionController.createExtension(loan1.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
        assertThat(extensionController.createExtension(loan2.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testCreateExtensionBAD_REQUESTLoanHasExtension(){
        setSecurityContext(notAdmin1);

        extensionController.createExtension(loan1.getId());

        assertThat(extensionController.createExtension(loan1.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testCreateExtensionBAD_REQUESTUserHasDelayedLoan(){
        loan1.setReservationDate(LocalDate.now().minus(Period.ofDays(10)));
        loan1.setExpirationDate(LocalDate.now().minus(Period.ofDays(5)));
        loanService.saveLoan(loan1);

        setSecurityContext(notAdmin1);

        assertThat(extensionController.createExtension(loan1.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testCreateExtensionBAD_REQUESTUserIsNotOwnerOfLoan(){
        setSecurityContext(notAdmin1);

        assertThat(extensionController.createExtension(loan2.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);

        setSecurityContext(notAdmin2);

        assertThat(extensionController.createExtension(loan1.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testModifyExtensionOK(){

        ExtensionModel extension1 = extensionModel1;

        ExtensionModel extension2 = extensionModel2;

        setSecurityContext(admin);

        assertThat(extensionController.approveExtension(extension1.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(extensionController.rejectExtension(extension2.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

    }

    @Test
    void testModifyExtensionUNAUTHORIZED(){

        setSecurityContext(notAdmin3);

        ExtensionModel extension1 = extensionModel1;

        ExtensionModel extension2 = extensionModel2;

        assertThat(extensionController.approveExtension(extension1.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
        assertThat(extensionController.rejectExtension(extension2.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    void testModifyExtensionBAD_REQUEST(){

        ExtensionModel extension2 = extensionModel2;

        extension2.setStatus(ExtensionStatus.REJECTED);
        extensionService.saveExtension(extension2);

        setSecurityContext(admin);

        assertThat(extensionController.approveExtension(extension2.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);

        extension2.setStatus(ExtensionStatus.PENDING_APPROVAL);
        extensionService.saveExtension(extension2);

    }

}
