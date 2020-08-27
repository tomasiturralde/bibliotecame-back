package bibliotecame.back.services;

import bibliotecame.back.models.AuthorModel;
import bibliotecame.back.models.BookModel;
import bibliotecame.back.models.PublisherModel;
import bibliotecame.back.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    public BookModel findBookById(int id){
        return this.bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book with id: " + id + " not found!"));
    }

    public BookModel saveBook(BookModel bookModel){
        return this.bookRepository.save(bookModel);
    }

    public boolean exists(String title, AuthorModel author, PublisherModel publisher, int year){
        return this.bookRepository.findByTitleAndAuthorAndPublisherAndYear(title, author, publisher, year).isPresent();
    }

    public boolean hasAuthor(BookModel bookModel){
        return bookModel.getAuthor() != null;
    }

    public boolean hasPublisher(BookModel bookModel){
        return bookModel.getPublisher() != null;
    }

    public boolean hasTitle(BookModel bookModel){
        return bookModel.getTitle() != null && bookModel.getTitle() != "";
    }

    public boolean validYear(BookModel bookModel){
        return bookModel.getYear() > 800 && bookModel.getYear() <= LocalDate.now().getYear();
    }

}
