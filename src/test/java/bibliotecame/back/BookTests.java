package bibliotecame.back;

import bibliotecame.back.controllers.BookController;
import bibliotecame.back.models.AuthorModel;
import bibliotecame.back.models.BookModel;
import bibliotecame.back.models.PublisherModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookTests {

    @Autowired
    public BookController bookController;

    @Test
    void testAddBook(){
        AuthorModel author = new AuthorModel("Rocio", "Ferreiro");
        PublisherModel publisher = new PublisherModel("Ediciones");

        bookController.createBook(new BookModel("Las marias", 2010, author, publisher));
    }
}
