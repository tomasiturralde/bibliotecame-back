package bibliotecame.back.Book;

import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Tag.TagModel;
import bibliotecame.back.Tag.TagService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    private final TagService tagService;


    @Autowired
    public BookService(BookRepository bookRepository, TagService tagService){
        this.bookRepository = bookRepository;
        this.tagService = tagService;
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
        bookToUpdate.setTags(tagService.validate(book.getTags()));
        bookToUpdate.setYear(book.getYear());
        bookToUpdate.setCopies(book.getCopies());

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

    public boolean containsCopy(BookModel bookModel, CopyModel copyModel){
        for(CopyModel copy : bookModel.getCopies()){
            if(copy.getId().equals(copyModel.getId())) return true;
        }
        return false;
    }

    public void addTags(BookModel book, List<TagModel> tags) {
        List<TagModel> actualTags = findBookById(book.getId()).getTags() ;
        actualTags.addAll(tags);
        book.setTags(actualTags);
    }

    public List<CopyModel> getAvailableCopies(BookModel book){
        List<CopyModel> available = new ArrayList<>();
        for(CopyModel copy : book.getCopies()){
            if(!copy.getBooked() && copy.getActive()){
                available.add(copy);
            }
        }
        return available;
    }
}