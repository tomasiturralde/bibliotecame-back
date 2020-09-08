package bibliotecame.back;

import bibliotecame.back.Book.BookController;
import bibliotecame.back.Book.BookModel;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.Author.AuthorModel;
import bibliotecame.back.Publisher.*;
import bibliotecame.back.Author.AuthorRepository;
import bibliotecame.back.Book.BookRepository;
import bibliotecame.back.Publisher.PublisherRepository;
import bibliotecame.back.Tag.TagModel;
import bibliotecame.back.Tag.TagRepository;
import bibliotecame.back.Author.AuthorService;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Publisher.PublisherService;
import bibliotecame.back.Tag.TagService;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

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
    private AuthorService authorService;
    @Autowired
    private AuthorRepository authorRepository;

    @Mock
    private PublisherService publisherService;
    @Autowired
    private PublisherRepository publisherRepository;

    @Mock
    private TagService tagService;
    @Autowired
    private TagRepository tagRepository;

    Authentication authentication;
    SecurityContext securityContext;

    @BeforeAll
    void setUp() {
        authorService = new AuthorService(authorRepository);
        publisherService = new PublisherService(publisherRepository);
        tagService = new TagService(tagRepository);
        bookService = new BookService(bookRepository, authorService, publisherService);
        bookController = new BookController(bookService, tagService);

        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        AuthorModel author = authorService.saveAuthor(new AuthorModel("Rocio", "Ferreiro"));
        AuthorModel author2 = authorService.saveAuthor(new AuthorModel("Facundo", "Bocalandro"));
        PublisherModel publisher = publisherService.savePublisher(new PublisherModel("Ediciones"));
        PublisherModel publisher2 = publisherService.savePublisher(new PublisherModel("Ediciones 2"));

        bookService.saveBook(new BookModel("papap", 2000, author, publisher));

        tagService.saveTag(new TagModel("Historia"));
        tagService.saveTag(new TagModel("Fantasia"));

    }

    @Test
    void testAddBook(){

        String randomName1 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);
        String randomName2 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);
        UserModel user = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        TagModel tag1 = tagService.findTagByName("Historia");
        TagModel tag2 = tagService.findTagByName("Fantasia");

        List<TagModel> tagList = new ArrayList<>();
        tagList.add(tag1);
        tagList.add(tag2);

        assertThat(bookController.createBook(new BookModel(randomName1, 2010, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(bookController.createBook(new BookModel(randomName2, 2010, author, publisher, tagList)).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        assertThat(bookService.exists(randomName1, author, publisher, 2010)).isTrue();
        assertThat(bookController.getBookModel(bookService.findByAttributeCombination(randomName1, author, publisher, 2010).getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        assertThat(bookService.exists(randomName2, author, publisher, 2010)).isTrue();
        assertThat(bookController.getBookModel(bookService.findByAttributeCombination(randomName2, author, publisher, 2010).getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    @Test
    //asserts failure for: not admin user creating book
    void testUnauthorized(){

        UserModel user = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        user.setAdmin(false);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        assertThat(bookController.createBook(new BookModel("Las calles", 2012, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    //asserts failure for:
    // empty name
    // non valid year
    // non recognised author
    // non recognised publisher
    // non recognised tag
    void testBadRequest(){
        UserModel user = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        List<TagModel> tagList = new ArrayList<>();
        tagList.add(new TagModel("hola"));

        assertThat(bookController.createBook(new BookModel("", 2012, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", 700, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", LocalDate.now().getYear()+1, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", 2012, new AuthorModel(), publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", 2012, author, new PublisherModel("asd"))).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", 2012, author, publisher, tagList)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    //asserts failure for: creating already existing book
    void testNotAcceptable(){
        UserModel user = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        assertThat(bookController.createBook(new BookModel("papap", 2000, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.NOT_ACCEPTABLE);

    }

    @Test
    void testGetAllAndGetOnlyActives(){

        String randomName = RandomStringGenerator.getAlphaNumericStringWithSymbols(20);
        UserModel user = new UserModel("khalil@mail.austral.edu.ar", "password", "Khalil", "Stessens", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        TagModel tag1 = tagService.findTagByName("Historia");
        TagModel tag2 = tagService.findTagByName("Fantasia");

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

    @Test
    void testUnauthorizedForDeactivate() {

        UserModel user = new UserModel("khalil@mail.austral.edu.ar", "password", "Khalil", "Stessens", "12341234");
        user.setAdmin(false);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        assertThat(bookController.deactivateBook(new BookModel("Las calles", 2012, author, publisher).getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    void testDeactivatingNonexistentBookReturnsBadRequest() {

        UserModel user = new UserModel("khalil@mail.austral.edu.ar", "password", "Khalil", "Stessens", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        assertThat(bookController.deactivateBook(new BookModel("Las calles", 2012, author, publisher).getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    void testDeactivatingBook() {

        String randomName = RandomStringGenerator.getAlphaNumericStringWithSymbols(20);

        UserModel user = new UserModel("khalil@mail.austral.edu.ar", "password", "Khalil", "Stessens", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");
        BookModel book = new BookModel(randomName, 2012, author, publisher);
        bookService.saveBook(book);

        assertThat(bookController.deactivateBook(book.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(!bookService.findBookById(book.getId()).isActive());

    }

    @Test
    public void testBookModificationOk() {
        String randomName1 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);
        String randomName2 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);
        UserModel user = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        TagModel tag1 = tagService.findTagByName("Historia");
        TagModel tag2 = tagService.findTagByName("Fantasia");

        List<TagModel> tagList = new ArrayList<>();
        tagList.add(tag1);
        tagList.add(tag2);

        BookModel book = new BookModel(randomName1, 2010, author, publisher, tagList);

        ResponseEntity<BookModel> response = bookController.createBook(book);

        BookModel saved = response.getBody();
        assert saved != null;
        testTitleModification(book, saved, HttpStatus.OK, randomName2);
        testYearModification(book, saved, HttpStatus.OK, 2007);
        testAuthorModification(book, saved, HttpStatus.OK, authorService.findAuthorByName("Facundo", "Bocalandro"));
        testPublisherModification(book, saved, HttpStatus.OK, publisherService.findPublisherByName("Ediciones 2"));


        List<TagModel> replacementTags = new ArrayList<>();
        replacementTags.add(tag1);
        testTagModification(book, saved, HttpStatus.OK, replacementTags);
    }

    @Test
    public void testBookModificationUnAuthorized() {
        String randomName1 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);
        String randomName2 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);
        UserModel user = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        TagModel tag1 = tagService.findTagByName("Historia");
        TagModel tag2 = tagService.findTagByName("Fantasia");

        List<TagModel> tagList = new ArrayList<>();
        tagList.add(tag1);
        tagList.add(tag2);

        BookModel book = new BookModel(randomName1, 2010, author, publisher, tagList);

        ResponseEntity<BookModel> response = bookController.createBook(book);

        BookModel saved = response.getBody();

        user.setAdmin(false);

        assert saved != null;
        testTitleModification(book, saved, HttpStatus.UNAUTHORIZED, randomName2);
        testYearModification(book, saved, HttpStatus.UNAUTHORIZED, 2007);
        testAuthorModification(book, saved, HttpStatus.UNAUTHORIZED, authorService.findAuthorByName("Facundo", "Bocalandro"));
        testPublisherModification(book, saved, HttpStatus.UNAUTHORIZED, publisherService.findPublisherByName("Ediciones 2"));


        List<TagModel> replacementTags = new ArrayList<>();
        replacementTags.add(tag1);
        testTagModification(book, saved, HttpStatus.UNAUTHORIZED, replacementTags);
    }

    @Test
    public void testBookModificationBadRequest() {
        String randomName1 = RandomStringGenerator.getAlphaNumericStringWithSymbols(30);
        UserModel user = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        TagModel tag1 = tagService.findTagByName("Historia");
        TagModel tag2 = tagService.findTagByName("Fantasia");

        List<TagModel> tagList = new ArrayList<>();
        tagList.add(tag1);
        tagList.add(tag2);

        BookModel book = new BookModel(randomName1, 2010, author, publisher, tagList);

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

    private void testYearModification(BookModel book, BookModel saved, HttpStatus status, int newValue) {
        book.setYear(newValue);

        ResponseEntity<BookModel> responseEntity = bookController.updateBook(saved.getId(), book);
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);
        if (status == HttpStatus.OK) {
            assertThat(Objects.requireNonNull(responseEntity.getBody()).getYear()).isEqualTo(newValue);
        }
    }

    private void testAuthorModification(BookModel book, BookModel saved, HttpStatus status, AuthorModel newAuthor) {
        book.setAuthor(newAuthor);

        ResponseEntity<BookModel> responseEntity = bookController.updateBook(saved.getId(), book);
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);

        if (status == HttpStatus.OK) {
            AuthorModel responseAuthor = Objects.requireNonNull(responseEntity.getBody()).getAuthor();
            assertThat(responseAuthor.getFirstName()).isEqualTo(newAuthor.getFirstName());
            assertThat(responseAuthor.getLastName()).isEqualTo(newAuthor.getLastName());
        }
    }

    private void testPublisherModification(BookModel book, BookModel saved, HttpStatus status, PublisherModel newPublisher) {
        book.setPublisher(newPublisher);

        ResponseEntity<BookModel> responseEntity = bookController.updateBook(saved.getId(), book);
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);

        if (status == HttpStatus.OK) {
            PublisherModel responsePublisher = Objects.requireNonNull(responseEntity.getBody()).getPublisher();
            assertThat(responsePublisher.getName()).isEqualTo(newPublisher.getName());
        }
    }

    private void testTagModification(BookModel book, BookModel saved, HttpStatus status, List<TagModel> newTags) {
        book.setTags(newTags);

        ResponseEntity<BookModel> responseEntity = bookController.updateBook(saved.getId(), book);
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);

        if (status == HttpStatus.OK) {
            List<TagModel> responseTags = Objects.requireNonNull(responseEntity.getBody()).getTags();

            for (TagModel tag : responseTags) {
                boolean isContained = false;
                for (TagModel newTag : newTags) {
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
}
