package bibliotecame.back;

import bibliotecame.back.Book.BookController;
import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookRepository;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyRepository;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.Tag.TagModel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookTests {

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
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    String authorForSavedBook;
    String publisherForSavedBook;

    Authentication authentication;
    SecurityContext securityContext;

    UserModel admin;

    @BeforeAll
    void setUp() {
        copyService = new CopyService(copyRepository);
        tagService = new TagService(tagRepository);
        bookService = new BookService(bookRepository, tagService);
        userService = new UserService(userRepository, bookService);
        bookController = new BookController(bookService, tagService, copyService, userService);

        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        authorForSavedBook = RandomStringGenerator.getAlphabeticString(20);
        publisherForSavedBook = RandomStringGenerator.getAlphabeticString(20);

        bookService.saveBook(new BookModel("papap", 2000, authorForSavedBook, publisherForSavedBook));

        tagService.saveTag(new TagModel("tag1"));
        tagService.saveTag(new TagModel("tag2"));

        admin = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        admin.setAdmin(true);
        userRepository.save(admin);

    }

    @Test
    void testAddBook(){

        String randomName1 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);
        String randomName2 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);
        String randomName3 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);

        List<GrantedAuthority> auths = new ArrayList<>();

        User securityUser = new User(admin.getEmail(), admin.getPassword(), auths);

        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String author = RandomStringGenerator.getAlphabeticString(15);
        String publisher = RandomStringGenerator.getAlphabeticString(15);

        TagModel tag1 = tagService.findTagByName("tag1");
        TagModel tag2 = tagService.findTagByName("tag2");

        List<TagModel> tagList = new ArrayList<>();
        tagList.add(tag1);
        tagList.add(tag2);

        assertThat(bookController.createBook(new BookModel(randomName1, 2010, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(bookController.createBook(new BookModel(randomName2, 2010, author, publisher, tagList)).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        assertThat(bookService.exists(randomName1, author, publisher, 2010)).isTrue();
        assertThat(bookController.getBookModel(bookService.findByAttributeCombination(randomName1, author, publisher, 2010).getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        assertThat(bookService.exists(randomName2, author, publisher, 2010)).isTrue();
        assertThat(bookController.getBookModel(bookService.findByAttributeCombination(randomName2, author, publisher, 2010).getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        tagList.add(new TagModel("Ingenieria"));

        assertThat(bookController.createBook(new BookModel(randomName3, 2010, author, publisher, tagList)).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(bookService.exists(randomName3, author, publisher, 2010)).isTrue();
        assertThat(bookController.getBookModel(bookService.findByAttributeCombination(randomName2, author, publisher, 2010).getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);


    }

//    @Test
//    //asserts failure for: not admin user creating book
//    void testUnauthorized(){
//
//        UserModel user = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
//        user.setAdmin(false);
//
//        Mockito.when(authentication.getPrincipal()).thenReturn(user);
//        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        assertThat(bookController.createBook(new BookModel("Las calles", 2012, RandomStringGenerator.getAlphaNumericString(10), RandomStringGenerator.getAlphaNumericString(10))).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
//
//    }

    @Test
    //asserts failure for:
    // empty name
    // non valid year
    // non recognised author
    // non recognised publisher
    // non recognised tag
    void testBadRequest(){

        List<GrantedAuthority> auths = new ArrayList<>();

        User securityUser = new User(admin.getEmail(), admin.getPassword(), auths);

        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String author = RandomStringGenerator.getAlphaNumericString(20);
        String publisher = RandomStringGenerator.getAlphaNumericString(20);

        assertThat(bookController.createBook(new BookModel("", 2012, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", 700, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", LocalDate.now().getYear()+1, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", 2012, "", publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", 2012, author, "")).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    //asserts failure for: creating already existing book
    void testNotAcceptable(){

        List<GrantedAuthority> auths = new ArrayList<>();

        User securityUser = new User(admin.getEmail(), admin.getPassword(), auths);

        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThat(bookController.createBook(new BookModel("papap", 2000, authorForSavedBook, publisherForSavedBook)).getStatusCode()).isEqualByComparingTo(HttpStatus.NOT_ACCEPTABLE);

    }

    @Test
    void testGetAllAndGetOnlyActives(){

        String randomName = RandomStringGenerator.getAlphaNumericStringWithSymbols(20);

        List<GrantedAuthority> auths = new ArrayList<>();

        User securityUser = new User(admin.getEmail(), admin.getPassword(), auths);

        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String author = RandomStringGenerator.getAlphabeticString(22);
        String publisher = RandomStringGenerator.getAlphabeticString(22);

        TagModel tag1 = tagService.findTagByName("tag1");
        TagModel tag2 = tagService.findTagByName("tag2");

        List<TagModel> tagList = new ArrayList<>();
        tagList.add(tag1);
        tagList.add(tag2);

        assertThat(bookController.createBook(new BookModel(randomName, 2010, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(bookController.createBook(new BookModel("hola", 2010, author, publisher, tagList)).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        BookModel lasMarias = bookService.findByAttributeCombination(randomName,author,publisher,2010);

        List<BookModel> result = new ArrayList<>();
        bookService.findAll().iterator().forEachRemaining(result::add);
        assertThat(result.contains(lasMarias));

        lasMarias.setActive(false);
        bookService.saveBook(lasMarias);

        result = new ArrayList<>();
        bookService.findAllActive().iterator().forEachRemaining(result::add);
        assertThat(!result.contains(lasMarias));

    }

//    @Test
//    void testUnauthorizedForDeactivate() {
//
//        UserModel user = new UserModel("khalil@mail.austral.edu.ar", "password", "Khalil", "Stessens", "12341234");
//        user.setAdmin(false);
//
//        Mockito.when(authentication.getPrincipal()).thenReturn(user);
//        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        assertThat(bookController.deactivateBook(new BookModel("Las calles",
//                                                    2012,
//                                                    RandomStringGenerator.getAlphabeticString(18),
//                                                    RandomStringGenerator.getAlphabeticString(22))
//                                                    .getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
//
//    }

    @Test
    void testDeactivatingNonexistentBookReturnsBadRequest() {

        List<GrantedAuthority> auths = new ArrayList<>();

        User securityUser = new User(admin.getEmail(), admin.getPassword(), auths);

        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThat(bookController.deactivateBook(new BookModel("Las calles", 2012, RandomStringGenerator.getAlphabeticString(17), RandomStringGenerator.getAlphabeticString(17)).getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    void testDeactivatingBook() {

        String randomName = RandomStringGenerator.getAlphaNumericStringWithSymbols(20);

        List<GrantedAuthority> auths = new ArrayList<>();

        User securityUser = new User(admin.getEmail(), admin.getPassword(), auths);

        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        BookModel book = new BookModel(randomName, 2012, RandomStringGenerator.getAlphabeticString(20), RandomStringGenerator.getAlphabeticString(20));
        bookService.saveBook(book);

        assertThat(bookController.deactivateBook(book.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(!bookService.findBookById(book.getId()).isActive());

    }

    @Test
    public void testBookModificationOk() {
        String randomName1 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);
        String randomName2 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);

        List<GrantedAuthority> auths = new ArrayList<>();

        User securityUser = new User(admin.getEmail(), admin.getPassword(), auths);

        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String author = RandomStringGenerator.getAlphabeticString(10);
        String publisher = RandomStringGenerator.getAlphabeticString(10);

        TagModel tag1 = tagService.findTagByName("tag1");
        TagModel tag2 = tagService.findTagByName("tag2");

        List<TagModel> tagList = new ArrayList<>();
        tagList.add(tag1);
        tagList.add(tag2);

        BookModel book = new BookModel(randomName1, 2010,  author,  publisher, tagList);

        ResponseEntity<BookModel> response = bookController.createBook(book);

        BookModel saved = response.getBody();

        List<CopyModel> copies = new ArrayList<>();
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));

        assert saved != null;
        testTitleModification(book, saved, HttpStatus.OK, randomName2);
        testYearModification(book, saved, HttpStatus.OK, 2007);
        testAuthorModification(book, saved, HttpStatus.OK, "new Author");
        testPublisherModification(book, saved, HttpStatus.OK, "new Publsher");
        testModificationWithNewCopies(book, saved, HttpStatus.OK, copies);


        List<TagModel> replacementTags = new ArrayList<>();
        replacementTags.add(new TagModel(RandomStringGenerator.getAlphabeticString(5)));
        testTagModification(book, saved, HttpStatus.OK, replacementTags);
    }

//    @Test
//    public void testBookModificationUnAuthorized() {
//        String randomName1 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);
//        String randomName2 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);
//        UserModel user = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
//        user.setAdmin(true);
//
//        Mockito.when(authentication.getPrincipal()).thenReturn(user);
//        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        TagModel tag1 = tagService.findTagByName("tag1");
//        TagModel tag2 = tagService.findTagByName("tag2");
//
//        List<TagModel> tagList = new ArrayList<>();
//        tagList.add(tag1);
//        tagList.add(tag2);
//
//        BookModel book = new BookModel(randomName1, 2010, RandomStringGenerator.getAlphabeticString(10), RandomStringGenerator.getAlphaNumericString(10), tagList);
//
//        ResponseEntity<BookModel> response = bookController.createBook(book);
//
//        BookModel saved = response.getBody();
//
//        user.setAdmin(false);
//
//        List<CopyModel> copies = new ArrayList<>();
//        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
//        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
//        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
//        copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(15)));
//
//        assert saved != null;
//        testTitleModification(book, saved, HttpStatus.UNAUTHORIZED, randomName2);
//        testYearModification(book, saved, HttpStatus.UNAUTHORIZED, 2007);
//        testAuthorModification(book, saved, HttpStatus.UNAUTHORIZED, "new Author");
//        testPublisherModification(book, saved, HttpStatus.UNAUTHORIZED, "new Publisher");
//        testModificationWithNewCopies(book, saved, HttpStatus.UNAUTHORIZED, copies);
//
//
//        List<TagModel> replacementTags = new ArrayList<>();
//        replacementTags.add(tag1);
//        testTagModification(book, saved, HttpStatus.UNAUTHORIZED, replacementTags);
//    }

    @Test
    public void testBookModificationBadRequest() {
        String randomName1 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);

        List<GrantedAuthority> auths = new ArrayList<>();

        User securityUser = new User(admin.getEmail(), admin.getPassword(), auths);

        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        TagModel tag1 = tagService.findTagByName("tag1");
        TagModel tag2 = tagService.findTagByName("tag2");

        List<TagModel> tagList = new ArrayList<>();
        tagList.add(tag1);
        tagList.add(tag2);

        BookModel book = new BookModel(randomName1, 2010,  RandomStringGenerator.getAlphabeticString(10),  RandomStringGenerator.getAlphabeticString(10), tagList);

        ResponseEntity<BookModel> response = bookController.createBook(book);

        BookModel saved = response.getBody();
        assert saved != null;
        testTitleModification(book, saved, HttpStatus.BAD_REQUEST, null);
        testYearModification(book, saved, HttpStatus.BAD_REQUEST, 799);
        testYearModification(book, saved, HttpStatus.BAD_REQUEST, 2021);
        testAuthorModification(book, saved, HttpStatus.BAD_REQUEST, null);
        testPublisherModification(book, saved, HttpStatus.BAD_REQUEST, null);
        testTagModification(book, saved, HttpStatus.BAD_REQUEST, null);

    }

    private void testTitleModification(BookModel book, BookModel saved, HttpStatus status, String newValue) {
        book.setTitle(newValue);

        ResponseEntity<BookModel> responseEntity = bookController.updateBook(saved.getId(), book);
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);
        if (status == HttpStatus.OK) {
            assertThat(Objects.requireNonNull(responseEntity.getBody()).getTitle()).isEqualTo(newValue);
        }
    }

    private void testYearModification(BookModel book,BookModel saved, HttpStatus status, int newValue) {
        book.setYear(newValue);

        ResponseEntity<BookModel> responseEntity = bookController.updateBook(saved.getId(), book);
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);
        if (status == HttpStatus.OK) {
            assertThat(Objects.requireNonNull(responseEntity.getBody()).getYear()).isEqualTo(newValue);
        }
    }

    private void testAuthorModification(BookModel book,BookModel saved, HttpStatus status, String newAuthor) {
        book.setAuthor(newAuthor);

        ResponseEntity<BookModel> responseEntity = bookController.updateBook(saved.getId(), book);
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);

        if (status == HttpStatus.OK) {
            String responseAuthor = Objects.requireNonNull(responseEntity.getBody()).getAuthor();
            assertThat(responseAuthor).isEqualTo(newAuthor);
        }
    }

    private void testPublisherModification(BookModel book, BookModel saved, HttpStatus status, String newPublisher) {
        book.setPublisher(newPublisher);

        ResponseEntity<BookModel> responseEntity = bookController.updateBook(saved.getId(), book);
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);

        if (status == HttpStatus.OK) {
            String responsePublisher = Objects.requireNonNull(responseEntity.getBody()).getPublisher();
            assertThat(responsePublisher).isEqualTo(newPublisher);
        }
    }

    private void testModificationWithNewCopies(BookModel book,BookModel saved, HttpStatus status, List<CopyModel> copies) {

        List<CopyModel> oldCopies = saved.getCopies();
        oldCopies.addAll(copies);
        book.setCopies(oldCopies);
        ResponseEntity<BookModel> responseEntity = bookController.updateBook(saved.getId(), book);
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);

        if(status == HttpStatus.OK){
            for(CopyModel copy : copies){
                assertThat(bookService.containsCopy(book, copy)).isTrue();
            }
        }

        book.setCopies(new ArrayList<>());
    }

    private void testTagModification(BookModel book,BookModel saved, HttpStatus status, List<TagModel> newTags) {
        book.setTags(newTags);

        ResponseEntity<BookModel> responseEntity = bookController.updateBook(saved.getId(), book);
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);

        if (status == HttpStatus.OK) {
            List<TagModel> responseTags = Objects.requireNonNull(responseEntity.getBody()).getTags();

            for (TagModel newTag : newTags) {
                boolean isContained = false;
                for (TagModel tag : responseTags) {
                    if (newTag.getName().equals(tag.getName())) {
                        isContained = true;
                        break;
                    }

                }
                assertThat(isContained).isEqualTo(true);
            }

            for (TagModel newTag : newTags) {
                boolean isContained = false;
                for (TagModel tag : responseTags) {
                    if (tag.getName().equals(newTag.getName())) {
                        isContained = true;
                        break;
                    }
                }
                assertThat(isContained).isEqualTo(true);
            }
        }
    }

    @Test
    public void testModificationWithDisabledCopies() {

        List<GrantedAuthority> auths = new ArrayList<>();

        User securityUser = new User(admin.getEmail(), admin.getPassword(), auths);

        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        BookModel book = new BookModel("ElSeñorDeLasCopias",2020,"J. R. R. Testien","La comarca del testeo");
        bookService.saveBook(book);
        List<CopyModel> copies = new ArrayList<>();
        CopyModel copyModel = new CopyModel("T5T-001");
        copies.add(copyModel);
        book.setCopies(copies);
        bookController.updateBook(book.getId(),book);

        book.getCopies().get(0).setActive(false);
        ResponseEntity<BookModel> responseEntity = bookController.updateBook(book.getId(),book);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertFalse(Objects.requireNonNull(responseEntity.getBody()).getCopies().get(0).getActive());
    }

    @Test
    public void testFilterPagedAdminAndNonAdmin() {
        List<GrantedAuthority> auths = new ArrayList<>();

        User securityUser = new User(admin.getEmail(), admin.getPassword(), auths);

        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        BookModel book = new BookModel("ElSeñorDeLasCopias",2020,"J. R. R. Testien","La comarca del testeo");
        bookService.saveBook(book);

        ResponseEntity<Page<BookModel>> responseEntity = bookController.getAllByTitleOrAuthorOrPublisherOrTag(0,10,"ElSeñor");
        assertThat(responseEntity.getBody().getTotalElements()).isEqualTo(1);
        book.setActive(false);
        bookService.saveBook(book);
        responseEntity = bookController.getAllByTitleOrAuthorOrPublisherOrTag(0,10,"ElSeñor");
        assertThat(responseEntity.getBody().getTotalElements()).isEqualTo(1);
        admin.setAdmin(false);
        userRepository.save(admin);
        responseEntity = bookController.getAllByTitleOrAuthorOrPublisherOrTag(0,10,"ElSeñor");
        assertThat(responseEntity.getBody().getTotalElements()).isEqualTo(0);
    }
}
