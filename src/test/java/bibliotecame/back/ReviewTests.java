package bibliotecame.back;

import bibliotecame.back.Book.BookController;
import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookRepository;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyRepository;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.Extension.ExtensionService;
import bibliotecame.back.Loan.LoanController;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
    @Mock
    private LoanController loanController;

    @Mock
    private ExtensionService extensionService;


    Authentication authentication;
    SecurityContext securityContext;

    UserModel admin;
    UserModel studentUser;
    UserModel studentUser2;
    BookModel bookModel;
    CopyModel bookModelCopy;
    CopyModel bookModelCopy2;
    List<CopyModel> copies;

    @BeforeAll
    void setUp() {
        copyService = new CopyService(copyRepository);
        tagService = new TagService(tagRepository);
        bookService = new BookService(bookRepository, tagService);
        userService = new UserService(userRepository, bookService);
        bookController = new BookController(bookService, tagService, copyService, userService);
        reviewService = new ReviewService(reviewRepository);
        loanService = new LoanService(loanRepository, bookService, userService, reviewService, copyService);
        reviewController = new ReviewController(reviewService,userService,bookService);
        loanController = new LoanController(loanService,userService,bookService,copyService,extensionService);

        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        admin = new UserModel("admin@austral.edu.ar","admin123","admin","admin","1113334444");
        admin.setAdmin(true);
        userService.saveUser(admin);

        studentUser = new UserModel("BBruno@austral.edu.ar","stickyfingers","Bruno","Bucciarati","1113334444");
        studentUser2 = new UserModel("facundo@austral.edu.ar","stickyfingers","Facundo","Bocalandro","1113334444");
        bookModel = new BookModel("GioGio's Bizzarre Adventure",1995,"Araki Hirohiko","Weekly Shonen Jump");
        bookModelCopy = new CopyModel("GG-001");
        bookModelCopy2 = new CopyModel("GG-002");

        bookService.saveBook(bookModel);

        copies = new ArrayList<>();
        copies.add(bookModelCopy);
        copies.add(bookModelCopy2);
        bookModel.setCopies(copies);

        userService.saveUser(studentUser);
        setSecurityContext(studentUser);
        userService.saveUser(studentUser2);

        copyService.saveCopy(bookModelCopy);
        copyService.saveCopy(bookModelCopy2);
        bookService.saveBook(bookModel);

            bookModelCopy.setBooked(true);
            copyService.saveCopy(bookModelCopy);
            LocalDate today = LocalDate.now();
            LoanModel loan = new LoanModel(bookModelCopy, today, today.plus(Period.ofDays(5)));
            LoanModel savedLoanModel = loanService.saveLoan(loan);
            userService.addLoan(studentUser, savedLoanModel);



        bookModelCopy2.setBooked(true);
        copyService.saveCopy(bookModelCopy2);
        LoanModel loan2 = new LoanModel(bookModelCopy2, today, today.plus(Period.ofDays(5)));
        LoanModel savedLoanModel2 = loanService.saveLoan(loan2);
        userService.addLoan(studentUser2, savedLoanModel2);

        setSecurityContext(admin);
        loanController.setWithdrawDate(loan.getId());
        loanController.setReturnDate(loan.getId());
        loanController.setWithdrawDate(loan2.getId());
        loanController.setReturnDate(loan2.getId());
        setSecurityContext(studentUser);

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
        BookModel bm = new BookModel("New Book again",1995,"Araki Hirohiko","Weekly Shonen Jump");
        CopyModel cm = new CopyModel("NBA1231");
        bookService.saveBook(bm);
        copies = new ArrayList<>();
        copies.add(cm);
        bm.setCopies(copies);

        setSecurityContext(studentUser);
        copyService.saveCopy(cm);
        bookService.saveBook(bm);
        cm.setBooked(true);
        copyService.saveCopy(cm);
        LocalDate today = LocalDate.now();
        LoanModel loan = new LoanModel(cm, today, today.plus(Period.ofDays(5)));
        LoanModel savedLoanModel = loanService.saveLoan(loan);
        userService.addLoan(studentUser, savedLoanModel);
        setSecurityContext(admin);
        loanController.setWithdrawDate(loan.getId());
        loanController.setReturnDate(loan.getId());


        setSecurityContext(studentUser);
        ReviewModel review = new ReviewModel("It was breathtaking!",5,userService.findLogged());
        assertThat(reviewController.createReview(review,bm.getId()).getStatusCode()).isEqualTo(HttpStatus.OK);
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
        ReviewModel review = reviewService.findAllByUserModel(studentUser).get(0);
        if (review == null) {
            ReviewModel reviewModel = new ReviewModel("Great", 5, userService.findLogged());
            review = (ReviewModel) reviewController.createReview(reviewModel, bookModel.getId()).getBody();
        }

        assertThat(reviewController.getReviewModel(review.getId()).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testStudentCantGetAnotherStudentsReview(){
        setSecurityContext(studentUser2);
        ReviewModel review = getStudentFirstReview(studentUser, bookModel);
        if (review == null) {
            ReviewModel reviewModel = new ReviewModel("It was breathtaking!", 5, userService.findLogged());
            review = (ReviewModel)reviewController.createReview(reviewModel, bookModel.getId()).getBody();
        }

        setSecurityContext(studentUser);
        assertThat(reviewController.getReviewModel(review.getId()).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    ReviewModel getStudentFirstReview(UserModel user, BookModel book){
        List<Integer> reviewsIds = reviewService.findAllByUserModel(user).stream().map(ReviewModel::getId).collect(Collectors.toList());
        List<ReviewModel> bookReviews = bookService.findBookById(book.getId()).getReviews();
        return bookReviews.stream().filter(reviewModel -> reviewsIds.contains(reviewModel.getId())).findFirst().orElse(null);
    }

    @Test
    public void userCanUpdateAReviewItDid(){
        setSecurityContext(studentUser);
        BookModel bookModelII = new BookModel("GioGio's Bizzarre Adventure Part II",1999,"Araki Hirohiko","Weekly Shonen Jump");
        CopyModel bookModelCopyII = new CopyModel("GG2-001");
        List<CopyModel> copies = new ArrayList<>();
        bookService.saveBook(bookModelII);

        copies.add(bookModelCopyII);
        bookModelII.setCopies(copies);
        bookService.saveBook(bookModelII);

        bookModelCopyII.setBooked(true);
        copyService.saveCopy(bookModelCopyII);

        LocalDate today = LocalDate.now();
        LoanModel loan = new LoanModel(bookModelCopyII, today, today.plus(Period.ofDays(5)));
        LoanModel savedLoanModel = loanService.saveLoan(loan);
        userService.addLoan(studentUser, savedLoanModel);

        setSecurityContext(admin);
        loanController.setWithdrawDate(savedLoanModel.getId());
        loanController.setReturnDate(savedLoanModel.getId());
        setSecurityContext(studentUser);


        ReviewModel review = new ReviewModel("It was breathtaking!",5,userService.findLogged());
        assertThat(reviewController.createReview(review,bookModelII.getId()).getStatusCode()).isEqualTo(HttpStatus.OK);
        review.setDescription("It was even better than just breathtaking!");
        assertThat(reviewController.updateReview(review.getId(),review).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void userCantUpdateAReviewItDidntPost(){
        setSecurityContext(studentUser);
        BookModel bookModelIII = new BookModel("GioGio's Bizzarre Adventure Part III",2003,"Araki Hirohiko","Weekly Shonen Jump");
        CopyModel bookModelCopyIII = new CopyModel("GG3-001");
        List<CopyModel> copies = new ArrayList<>();
        bookService.saveBook(bookModelIII);

        copies.add(bookModelCopyIII);
        bookModelIII.setCopies(copies);
        bookService.saveBook(bookModelIII);

        bookModelCopyIII.setBooked(true);
        copyService.saveCopy(bookModelCopyIII);

        LocalDate today = LocalDate.now();
        LoanModel loan = new LoanModel(bookModelCopyIII, today, today.plus(Period.ofDays(5)));
        LoanModel savedLoanModel = loanService.saveLoan(loan);
        userService.addLoan(studentUser, savedLoanModel);

        ReviewModel review = new ReviewModel("It was breathtaking!",5,userService.findLogged());

        setSecurityContext(admin);
        loanController.setWithdrawDate(savedLoanModel.getId());
        loanController.setReturnDate(savedLoanModel.getId());
        setSecurityContext(studentUser);

        assertThat(reviewController.createReview(review,bookModelIII.getId()).getStatusCode()).isEqualTo(HttpStatus.OK);
        review.setDescription("It was even better than just breathtaking!");
        setSecurityContext(studentUser2);
        assertThat(((ErrorMessage)reviewController.updateReview(review.getId(),review).getBody()).getMessage()).isEqualTo("¡No puedes modificar una reseña escrita por otro alumno!");
    }

    @Test
    public void userCantDeleteReviewItDidntPost(){
        setSecurityContext(studentUser);
        BookModel bookModelIV = new BookModel("GioGio's Bizzarre Adventure Part IV",2003,"Araki Hirohiko","Weekly Shonen Jump");
        CopyModel bookModelCopyIV = new CopyModel("GG4-001");
        List<CopyModel> copies = new ArrayList<>();
        bookService.saveBook(bookModelIV);

        copies.add(bookModelCopyIV);
        bookModelIV.setCopies(copies);
        bookService.saveBook(bookModelIV);

        bookModelCopyIV.setBooked(true);
        copyService.saveCopy(bookModelCopyIV);

        LocalDate today = LocalDate.now();
        LoanModel loan = new LoanModel(bookModelCopyIV, today, today.plus(Period.ofDays(5)));
        LoanModel savedLoanModel = loanService.saveLoan(loan);
        userService.addLoan(studentUser, savedLoanModel);

        ReviewModel review = new ReviewModel("It was breathtaking!",5,userService.findLogged());

        setSecurityContext(admin);
        loanController.setWithdrawDate(savedLoanModel.getId());
        loanController.setReturnDate(savedLoanModel.getId());
        setSecurityContext(studentUser);

        assertThat(reviewController.createReview(review,bookModelIV.getId()).getStatusCode()).isEqualTo(HttpStatus.OK);

        setSecurityContext(studentUser2);
        assertThat(((ErrorMessage)reviewController.deleteReview(review.getId()).getBody()).getMessage()).isEqualTo("Solo el alumno que realizó esta reseña puede eliminarla");
    }

    @Test
    public void userCanDeleteReviewItDidPost(){
        setSecurityContext(studentUser);
        BookModel bookModelV = new BookModel("GioGio's Bizzarre Adventure Part V",2003,"Araki Hirohiko","Weekly Shonen Jump");
        CopyModel bookModelCopyV = new CopyModel("GG5-001");
        List<CopyModel> copies = new ArrayList<>();
        bookService.saveBook(bookModelV);

        copies.add(bookModelCopyV);
        bookModelV.setCopies(copies);
        bookService.saveBook(bookModelV);

        bookModelCopyV.setBooked(true);
        copyService.saveCopy(bookModelCopyV);

        LocalDate today = LocalDate.now();
        LoanModel loan = new LoanModel(bookModelCopyV, today, today.plus(Period.ofDays(5)));
        LoanModel savedLoanModel = loanService.saveLoan(loan);
        userService.addLoan(studentUser, savedLoanModel);

        ReviewModel review = new ReviewModel("Great!",5,userService.findLogged());

        setSecurityContext(admin);
        loanController.setWithdrawDate(savedLoanModel.getId());
        loanController.setReturnDate(savedLoanModel.getId());

        setSecurityContext(studentUser);

        assertThat(reviewController.createReview(review,bookModelV.getId()).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reviewController.deleteReview(review.getId()).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void requestingUnexistingReviewFails(){
        assertThat(reviewController.getReviewModel(-1).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUserCantCreateTwoReviewsForSameBook(){
        BookModel bookModelAgain = new BookModel("GioGio's Bizzarre Adventure III",1995,"Araki Hirohiko","Weekly Shonen Jump");
        CopyModel bookModelCopyAgain = new CopyModel("GGIII-001");
        CopyModel bookModelCopy2Again = new CopyModel("GGIII-002");
        bookService.saveBook(bookModelAgain);
        copies = new ArrayList<>();
        copies.add(bookModelCopyAgain);
        copies.add(bookModelCopy2Again);
        bookModelAgain.setCopies(copies);

        setSecurityContext(studentUser);
        copyService.saveCopy(bookModelCopyAgain);
        copyService.saveCopy(bookModelCopy2Again);
        bookService.saveBook(bookModelAgain);
        bookModelCopyAgain.setBooked(true);
        copyService.saveCopy(bookModelCopyAgain);
        LocalDate today = LocalDate.now();
        LoanModel loan = new LoanModel(bookModelCopyAgain, today, today.plus(Period.ofDays(5)));
        LoanModel savedLoanModel = loanService.saveLoan(loan);
        userService.addLoan(studentUser, savedLoanModel);

        setSecurityContext(admin);
        loanController.setWithdrawDate(loan.getId());
        loanController.setReturnDate(loan.getId());
        setSecurityContext(studentUser);

        ReviewModel review = new ReviewModel("It was breathtaking!",5,userService.findLogged());
        assertThat(reviewController.createReview(review,bookModelAgain.getId()).getStatusCode()).isEqualTo(HttpStatus.OK);

        ReviewModel review2 = new ReviewModel("It was breathtaking!",1,userService.findLogged());
        assertThat(reviewController.createReview(review2,bookModelAgain.getId()).getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

    @Test
    public void testUpdateReviewUnsuccesfullCalls(){
        BookModel bookieModel = new BookModel("GioGio's IV",1995,"Araki Hirohiko","Weekly Shonen Jump");
        CopyModel bookieModelCopy = new CopyModel("GGIV-1");
        bookService.saveBook(bookieModel);
        copies = new ArrayList<>();
        copies.add(bookieModelCopy);
        bookieModel.setCopies(copies);

        setSecurityContext(studentUser);
        copyService.saveCopy(bookieModelCopy);
        bookService.saveBook(bookieModel);
        bookieModelCopy.setBooked(true);
        copyService.saveCopy(bookieModelCopy);
        LocalDate today = LocalDate.now();
        LoanModel loan = new LoanModel(bookieModelCopy, today, today.plus(Period.ofDays(5)));
        LoanModel savedLoanModel = loanService.saveLoan(loan);
        userService.addLoan(studentUser, savedLoanModel);
        setSecurityContext(admin);
        loanController.setWithdrawDate(loan.getId());
        loanController.setReturnDate(loan.getId());
        setSecurityContext(studentUser);
        ReviewModel review = new ReviewModel("It was breathtaking!",5,userService.findLogged());
        assertThat(reviewController.createReview(review,bookieModel.getId()).getStatusCode()).isEqualTo(HttpStatus.OK);

        setSecurityContext(studentUser2);
        assertThat(reviewController.updateReview(-1,new ReviewModel()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        setSecurityContext(admin);
        assertThat(reviewController.updateReview(review.getId(),new ReviewModel()).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        setSecurityContext(studentUser);
        assertThat(reviewController.updateReview(review.getId(),new ReviewModel()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    public void testUnexistingReviewResponses(){
        setSecurityContext(admin);
        assertThat(reviewController.getReviewModel(-1).getStatusCode()).isNotEqualTo(HttpStatus.OK);
        assertThat(reviewController.deleteReview(-1).getStatusCode()).isNotEqualTo(HttpStatus.OK);
        assertThat(reviewController.createReview(new ReviewModel(3,admin),-1).getStatusCode()).isNotEqualTo(HttpStatus.OK);
    }

    @Test
    public void reviewModelCanGetItsIDchanged(){
        ReviewModel review = new ReviewModel(3,new UserModel());
        review.setId(5);
        assertThat(review.getId()).isEqualTo(5);
    }

    @Test
    public void reviewServiceReturnsFalseForUnexistingReview(){
        assertFalse(reviewService.exists(-1));
    }

}