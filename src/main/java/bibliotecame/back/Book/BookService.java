package bibliotecame.back.Book;

import bibliotecame.back.Copy.CopyModel;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;


    @Autowired
    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    public BookModel findBookById(int id){
        return this.bookRepository.findById(id).orElseThrow(() -> new RuntimeException("bibliotecame.back.Book with id: " + id + " not found!"));
    }

    public BookModel saveBook(BookModel bookModel){
        return this.bookRepository.save(bookModel);
    }

    public boolean exists(String title, String author, String publisher, int year){
        return this.bookRepository.findByTitleAndAuthorAndPublisherAndYear(title, author, publisher, year).isPresent();
    }

    public boolean exists(int id){
        return this.bookRepository.findById(id).isPresent();
    }

    public boolean hasAuthor(BookModel bookModel){
        return bookModel.getAuthor() != null && !bookModel.getAuthor().equals("");
    }

    public boolean hasPublisher(BookModel bookModel){
        return bookModel.getPublisher() != null && !bookModel.getPublisher().equals("");
    }

    public boolean hasTitle(BookModel bookModel){
        return bookModel.getTitle() != null && !bookModel.getTitle().equals("");
    }

    public boolean validYear(BookModel bookModel){
        return bookModel.getYear() > 800 && bookModel.getYear() <= LocalDate.now().getYear();
    }

    public BookModel findByAttributeCombination(String title, String author, String publisher, int year){
        return this.bookRepository.findByTitleAndAuthorAndPublisherAndYear(title, author, publisher, year).orElseThrow(() -> new RuntimeException("bibliotecame.back.Book not found."));
    }

    public Iterable<BookModel> findAll(){
        return this.bookRepository.findAll();
    }

    public Iterable<BookModel> findAllActive(){
        return this.bookRepository.findAllByActive(true);
    }

    public ResponseEntity<BookModel> updateBook(Integer id, BookModel book) {
        BookModel bookToUpdate;
        try {
            bookToUpdate = this.bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
        } catch (NotFoundException e) {
            return new ResponseEntity<>(book, HttpStatus.NOT_FOUND);
        }

        //check validity
        if (!validBook(book)){
            return new ResponseEntity<>(book, HttpStatus.BAD_REQUEST);
        }

        //update fields
        bookToUpdate.setTitle(book.getTitle());
        bookToUpdate.setAuthor(book.getAuthor());
        bookToUpdate.setPublisher(book.getPublisher());
        bookToUpdate.setTags(book.getTags());
        bookToUpdate.setYear(book.getYear());

        //save book and return
        BookModel updated = this.bookRepository.save(bookToUpdate);
        return ResponseEntity.ok(updated);
    }

    boolean validBook(BookModel book) {
        String updatedTitle = book.getTitle();
        int updatedYear = book.getYear();

        //check validity
        return updatedTitle != null && hasAuthor(book) && hasAuthor(book) && updatedYear >= 800 && updatedYear <= Calendar.getInstance().get(Calendar.YEAR);
    }

    public void addCopies(BookModel book, List<CopyModel> copies){
        List<CopyModel> actualCopies = book.getCopies();
        actualCopies.addAll(copies);
        book.setCopies(actualCopies);
    }

    public boolean containsCopy(BookModel bookModel, CopyModel copyModel){
        for(CopyModel copy : bookModel.getCopies()){
            if(copy.getId().equals(copyModel.getId())) return true;
        }
        return false;
    }

}