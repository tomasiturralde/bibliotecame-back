package bibliotecame.back.Book;

import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.Tag.TagService;
import bibliotecame.back.User.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    private final TagService tagService;

    private final CopyService copyService;

    @Autowired
    public BookController(BookService bookService, TagService tagService, CopyService copyService) {
        this.bookService = bookService;
        this.tagService = tagService;
        this.copyService = copyService;
    }

    @GetMapping("{id}")
    public ResponseEntity<BookModel> getBookModel(@PathVariable Integer id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!bookService.exists(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UserModel user = (UserModel) authentication.getPrincipal();
        BookModel book = this.bookService.findBookById(id);
        if(!book.isActive() && !user.isAdmin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
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

    @PutMapping("/{id}")
    public ResponseEntity<BookModel> updateBook(@PathVariable Integer id, @Valid @RequestBody BookModel book){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = (UserModel) authentication.getPrincipal();

        if(!user.isAdmin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<CopyModel> copies = book.getCopies();

        if(!copies.isEmpty()){
            if(copies.size()>=100) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            for(CopyModel copy : copies){
                if(copyService.exists(copy.getId())) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                copyService.saveCopy(copy);
            }
        }

        return bookService.updateBook(id, book);
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

    @PostMapping("{id}/activate")
    public ResponseEntity<BookModel> activateBook(@PathVariable Integer id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = (UserModel) authentication.getPrincipal();
        if(!user.isAdmin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(!bookService.exists(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BookModel bookModel = bookService.findBookById(id);
        bookModel.setActive(true);
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
