package bibliotecame.back.Book;

import bibliotecame.back.Tag.TagService;
import bibliotecame.back.User.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    private final TagService tagService;


    @Autowired
    public BookController(BookService bookService, TagService tagService){
        this.bookService = bookService;
        this.tagService = tagService;
    }

    @GetMapping("{id}")
    public ResponseEntity<BookModel> getBookModel(@PathVariable Integer id){
        return new ResponseEntity<>(this.bookService.findBookById(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<BookModel> createBook(@Valid @RequestBody BookModel bookModel){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = (UserModel) authentication.getPrincipal();

        if(!user.isAdmin()){
            return new ResponseEntity<>(bookModel, HttpStatus.UNAUTHORIZED);
        }
        if(!bookService.hasTitle(bookModel) || !bookService.hasAuthor(bookModel) || !bookService.validYear(bookModel) || !bookService.hasPublisher(bookModel)){
            return new ResponseEntity<>(bookModel, HttpStatus.BAD_REQUEST);
        }
        if(bookService.exists(bookModel.getTitle(), bookModel.getAuthor(), bookModel.getPublisher(), bookModel.getYear())){
            return new ResponseEntity<>(bookModel, HttpStatus.NOT_ACCEPTABLE);
        }

        if(!bookModel.getTags().isEmpty() && !tagService.validate(bookModel.getTags())) return new ResponseEntity<>(bookModel, HttpStatus.BAD_REQUEST);

        return ResponseEntity.ok(this.bookService.saveBook(bookModel));
    }


    @PostMapping("{id}/deactivate")
    public ResponseEntity<BookModel> deactivateBook(@PathVariable Integer id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = (UserModel) authentication.getPrincipal();
        if(!user.isAdmin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(!bookService.exists(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BookModel bookModel = bookService.findBookById(id);
        bookModel.setActive(false);
        return ResponseEntity.ok(this.bookService.saveBook(bookModel));
    }

    @GetMapping()
    public ResponseEntity<Iterable<BookModel>> getBookModel(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = (UserModel) authentication.getPrincipal();
        if(!user.isAdmin()){
            return ResponseEntity.ok(this.bookService.findAllActive());
        }
        return ResponseEntity.ok(this.bookService.findAll());
    }

}
