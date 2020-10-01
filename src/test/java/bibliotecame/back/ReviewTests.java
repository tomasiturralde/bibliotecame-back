package bibliotecame.back;

import bibliotecame.back.Book.BookController;
import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookRepository;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyRepository;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.Loan.LoanModel;
import bibliotecame.back.Loan.LoanRepository;
import bibliotecame.back.Loan.LoanService;
import bibliotecame.back.Review.ReviewController;
import bibliotecame.back.Review.ReviewModel;
import bibliotecame.back.Review.ReviewRepository;
import bibliotecame.back.Review.ReviewService;
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

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReviewTests {

    @Mock
    public BookController bookController;
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
    public ReviewController reviewController;
    @Mock
    private ReviewService reviewService;
    @Autowired
    private ReviewRepository reviewRepository;

    @Mock
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Mock
    private LoanService loanService;
    @Autowired
    private LoanRepository loanRepository;


    Authentication authentication;
    SecurityContext securityContext;

    UserModel studentUser;
    UserModel studentUser2;
    BookModel bookModel;
    CopyModel bookModelCopy;
    List<CopyModel> copies;

    @BeforeAll
    void setUp() {
        copyService = new CopyService(copyRepository);
        tagService = new TagService(tagRepository);
        bookService = new BookService(bookRepository, tagService);
        userService = new UserService(userRepository, bookService);
        bookController = new BookController(bookService, tagService, copyService, userService);
        reviewService = new ReviewService(reviewRepository);
        loanService = new LoanService(loanRepository);
        reviewController = new ReviewController(reviewService,userService,bookService);

        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        studentUser = new UserModel("BBruno@austral.edu.ar","stickyfingers","Bruno","Bucciarati","1113334444");
        studentUser2 = new UserModel("facundo@austral.edu.ar","stickyfingers","Facundo","Bocalandro","1113334444");
        bookModel = new BookModel("GioGio's Bizzarre Adventure",1995,"Araki Hirohiko","Weekly Shonen Jump");
        bookModelCopy = new CopyModel("GG-001");

        bookService.saveBook(bookModel);

        copies = new ArrayList<>();
        copies.add(bookModelCopy);
        bookModel.setCopies(copies);

        userService.saveUser(studentUser);
        setSecurityContext(studentUser);
        userService.saveUser(studentUser2);

        copyService.saveCopy(bookModelCopy);
        bookService.saveBook(bookModel);

            bookModelCopy.setBooked(true);
            copyService.saveCopy(bookModelCopy);
            LocalDate today = LocalDate.now();
            LoanModel loan = new LoanModel(bookModelCopy, today, today.plus(Period.ofDays(5)));
            LoanModel savedLoanModel = loanService.saveLoan(loan);
            userService.addLoan(studentUser, savedLoanModel);

    }

    private void setSecurityContext(UserModel user){
        List<GrantedAuthority> auths = new ArrayList<>();
        User securityUser = new User(user.getEmail(), user.getPassword(), auths);
        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testBeforeAllWorks(){
        setSecurityContext(studentUser);
        assertThat(userService.findLogged().getFirstName()).isEqualTo("Bruno");
        assertThat(userService.findLogged().getLoans().size()).isEqualTo(1);
    }

    @Test
    void testUserCanCreateAReview(){
        setSecurityContext(studentUser);
        ReviewModel review = new ReviewModel("It was breathtaking!",5,userService.findLogged());
        assertThat(reviewController.createReview(review,bookModel.getId()).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookRepository.findById(bookModel.getId()).get().getReviews().size()).isEqualTo(1);
    }

    @Test
    void testAdminCantCreateAReview(){
        setSecurityContext(studentUser);
        studentUser.setAdmin(true);
        userService.saveUser(studentUser);
        ReviewModel review = new ReviewModel("I'm an admin!",5,userService.findLogged());
        assertThat(reviewController.createReview(review,bookModel.getId()).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        studentUser.setAdmin(false);
        userService.saveUser(studentUser);
    }

    @Test
    void testUserCantCreateAReviewOfABookItDidntLoan(){
        setSecurityContext(studentUser);
        BookModel differentBook = new BookModel("Trust me it's a different book",1999,"Author","Publisher");
        bookService.saveBook(differentBook);
        ReviewModel review = new ReviewModel("I haven't read it!",1,userService.findLogged());
        assertThat(reviewController.createReview(review,differentBook.getId()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(bookRepository.findById(differentBook.getId()).get().getReviews().size()).isEqualTo(0);
    }

    @Test
    void reviewCantHaveAValueOutsideOfOneToFive(){
        setSecurityContext(studentUser);
        BookModel differentBook2 = new BookModel("I'm a different book",1999,"Author","Publisher");
        bookService.saveBook(differentBook2);
        ReviewModel review = new ReviewModel("It blew my mind!",999,userService.findLogged());
        assertThat(reviewController.createReview(review,differentBook2.getId()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ReviewModel review2 = new ReviewModel("It sucked!",-999,userService.findLogged());
        assertThat(reviewController.createReview(review2,differentBook2.getId()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testStudentCanGetItsOwnReview(){
        setSecurityContext(studentUser);
        ReviewModel reviewModel = new ReviewModel("Great",5,userService.findLogged());
        ReviewModel review = reviewController.createReview(reviewModel, bookModel.getId()).getBody();
        assertThat(reviewController.getReviewModel(review.getId()).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testStudentCantGetAnotherStudentsReview(){
        setSecurityContext(studentUser);
        ReviewModel reviewModel = new ReviewModel("It was breathtaking!",5,userService.findLogged());
        ReviewModel review = reviewController.createReview(reviewModel, bookModel.getId()).getBody();

        setSecurityContext(studentUser2);
        assertThat(reviewController.getReviewModel(review.getId()).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

}