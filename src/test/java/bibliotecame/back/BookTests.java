package bibliotecame.back;

import bibliotecame.back.controllers.BookController;
import bibliotecame.back.models.AuthorModel;
import bibliotecame.back.models.BookModel;
import bibliotecame.back.models.PublisherModel;
import bibliotecame.back.repository.BookRepository;
import bibliotecame.back.services.BookService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookTests {

    @Mock
    public BookController bookController;

    @Mock
    public BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    void setUp(){
        bookService = new BookService(bookRepository);
        bookController = new BookController(bookService);
    }

    @Test
    void testAddBook(){
        AuthorModel author = new AuthorModel("Rocio", "Ferreiro");
        PublisherModel publisher = new PublisherModel("Ediciones");

        bookController.createBook(new BookModel("Las marias", 2010, author, publisher));
    }
}
