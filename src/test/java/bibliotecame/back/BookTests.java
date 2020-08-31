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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

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
    void setUp(){
        authorService = new AuthorService(authorRepository);
        publisherService = new PublisherService(publisherRepository);
        tagService = new TagService(tagRepository);
        bookService = new BookService(bookRepository, authorService, publisherService);
        bookController = new BookController(bookService, tagService);

        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        AuthorModel author = authorService.saveAuthor(new AuthorModel("Rocio", "Ferreiro"));
        PublisherModel publisher = publisherService.savePublisher(new PublisherModel("Ediciones"));

        bookService.saveBook(new BookModel("papap", 2000, author, publisher));

        tagService.saveTag(new TagModel("Historia"));
        tagService.saveTag(new TagModel("Fantasia"));

    }

    @Test
    void testAddBook(){

        UserModel user = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        TagModel tag1 =  tagService.findTagByName("Historia");
        TagModel tag2 =  tagService.findTagByName("Fantasia");

        List<TagModel> tagList = new ArrayList<TagModel>();
        tagList.add(tag1);
        tagList.add(tag2);

        assertThat(bookController.createBook(new BookModel("Las marias", 2010, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(bookController.createBook(new BookModel("hola", 2010, author, publisher, tagList)).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        assertThat(bookService.exists("Las marias", author, publisher, 2010)).isTrue();
        assertThat(bookController.getBookModel(bookService.findByAttributeCombination("Las marias", author, publisher, 2010).getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        assertThat(bookService.exists("hola", author, publisher, 2010)).isTrue();
        assertThat(bookController.getBookModel(bookService.findByAttributeCombination("hola", author, publisher, 2010).getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    @Test
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
    void testBadRequest(){
        UserModel user = new UserModel("rocio@mail.austral.edu.ar", "password", "Rocio", "Ferreiro", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        List<TagModel> tagList = new ArrayList<TagModel>();
        tagList.add(new TagModel("hola"));

        assertThat(bookController.createBook(new BookModel("", 2012, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", 700, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", 2022, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", 2012, new AuthorModel(), publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", 2012, author, new PublisherModel("asd"))).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(bookController.createBook(new BookModel("Asd", 2012, author, publisher, tagList)).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);

    }

    @Test
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
        UserModel user = new UserModel("khalil@mail.austral.edu.ar", "password", "Khalil", "Stessens", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");

        TagModel tag1 =  tagService.findTagByName("Historia");
        TagModel tag2 =  tagService.findTagByName("Fantasia");

        List<TagModel> tagList = new ArrayList<TagModel>();
        tagList.add(tag1);
        tagList.add(tag2);

        assertThat(bookController.createBook(new BookModel("Las marias", 2010, author, publisher)).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(bookController.createBook(new BookModel("hola", 2010, author, publisher, tagList)).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        BookModel lasMarias = bookService.findByAttributeCombination("Las marias",author,publisher,2010);

        List<BookModel> result = new ArrayList<>();
        bookService.findAll().iterator().forEachRemaining(result::add);
        assertThat(result.contains(lasMarias));

        lasMarias.setActive(false);
        bookService.saveBook(lasMarias);

        result= new ArrayList<>();
        bookService.findAllActive().iterator().forEachRemaining(result::add);
        assertThat(!result.contains(lasMarias));

    }

    @Test
    void testUnauthorizedForDeactivate(){

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
    void testDeactivatingNonexistentBookReturnsBadRequest(){

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
    void testDeactivatingBook(){

        UserModel user = new UserModel("khalil@mail.austral.edu.ar", "password", "Khalil", "Stessens", "12341234");
        user.setAdmin(true);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuthorModel author = authorService.findAuthorByName("Rocio", "Ferreiro");
        PublisherModel publisher = publisherService.findPublisherByName("Ediciones");
        BookModel book = new BookModel("Las calles", 2012, author, publisher);
        bookService.saveBook(book);

        assertThat(bookController.deactivateBook(book.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(!bookService.findBookById(book.getId()).isActive());

    }
}
