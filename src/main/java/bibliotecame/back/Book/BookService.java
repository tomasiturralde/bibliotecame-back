package bibliotecame.back.Book;

import bibliotecame.back.Author.AuthorModel;
import bibliotecame.back.Publisher.PublisherModel;
import bibliotecame.back.Author.AuthorService;
import bibliotecame.back.Publisher.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BookService {

    private final BookRepository bookRepository;

    private final AuthorService authorService;
    private final PublisherService publisherService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService, PublisherService publisherService){
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.publisherService = publisherService;
    }

    public BookModel findBookById(int id){
        return this.bookRepository.findById(id).orElseThrow(() -> new RuntimeException("bibliotecame.back.Book with id: " + id + " not found!"));
    }

    public BookModel saveBook(BookModel bookModel){
        return this.bookRepository.save(bookModel);
    }

    public boolean exists(String title, AuthorModel author, PublisherModel publisher, int year){
        return this.bookRepository.findByTitleAndAuthorAndPublisherAndYear(title, author, publisher, year).isPresent();
    }

    public boolean exists(int id){
        return this.bookRepository.findById(id).isPresent();
    }

    public boolean hasAuthor(BookModel bookModel){
        AuthorModel author = bookModel.getAuthor();
        return authorService.exists(author.getFirstName(), author.getLastName());
    }

    public boolean hasPublisher(BookModel bookModel){
        PublisherModel publisher = bookModel.getPublisher();
        return publisherService.exists(publisher.getName());
    }

    public boolean hasTitle(BookModel bookModel){
        return bookModel.getTitle() != null && !bookModel.getTitle().equals("");
    }

    public boolean validYear(BookModel bookModel){
        return bookModel.getYear() > 800 && bookModel.getYear() <= LocalDate.now().getYear();
    }

    public BookModel findByAttributeCombination(String title, AuthorModel author, PublisherModel publisher, int year){
        return this.bookRepository.findByTitleAndAuthorAndPublisherAndYear(title, author, publisher, year).orElseThrow(() -> new RuntimeException("bibliotecame.back.Book not found."));
    }

    public Iterable<BookModel> findAll(){
        return this.bookRepository.findAll();
    }

    public Iterable<BookModel> findAllActive(){
        return this.bookRepository.findAllByActive(true);
    }

}