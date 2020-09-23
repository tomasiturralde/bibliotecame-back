package bibliotecame.back.Book;

import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.Tag.TagService;
import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

//import bibliotecame.back.User.UserModel;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    private final TagService tagService;

    private final CopyService copyService;

    private final UserService userService;

    @Autowired
    public BookController(BookService bookService, TagService tagService, CopyService copyService, UserService userService) {
        this.bookService = bookService;
        this.tagService = tagService;
        this.copyService = copyService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookModel> getBookModel(@PathVariable Integer id){
        if(!bookService.exists(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BookModel book = this.bookService.findBookById(id);
        if(!book.isActive() && !checkAdmin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(this.bookService.findBookById(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<BookModel> createBook(@Valid @RequestBody BookModel bookModel){
        if(!checkAdmin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return checkAndCreateBook(bookModel);
    }

    public ResponseEntity<BookModel> checkAndCreateBook(BookModel bookModel){
        if(!bookService.hasTitle(bookModel) || !bookService.hasAuthor(bookModel) || !bookService.validYear(bookModel) || !bookService.hasPublisher(bookModel)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(bookService.exists(bookModel.getTitle(), bookModel.getAuthor(), bookModel.getPublisher(), bookModel.getYear())){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        if(bookModel.getTags()!= null || !bookModel.getTags().isEmpty()){
            bookModel.setTags(tagService.validate(bookModel.getTags()));
        }

        return ResponseEntity.ok(this.bookService.saveBook(bookModel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookModel> updateBook(@PathVariable Integer id, @Valid @RequestBody BookModel book) {
        if (!checkAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return checkAndUpdateBook(id, book);
    }

    public ResponseEntity<BookModel> checkAndUpdateBook(Integer id, BookModel book){
        List<CopyModel> copies = book.getCopies();
        List<CopyModel> savedCopies = new ArrayList<>();

        if(copies!=null && !copies.isEmpty()){
            if(copies.size()>=100) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            for(CopyModel copy : copies){
                if(copyService.exists(copy.getId())){
                    if ((copy.getBooked() && !copy.getActive())) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    savedCopies.add(copyService.saveCopy(copy));
                }
                else {
                    savedCopies.add(copyService.saveCopy(new CopyModel(copy.getId())));
                }
            }
        }

        book.setCopies(savedCopies);

        return bookService.updateBook(id, book);
    }


    @PostMapping("/{id}/deactivate")
    public ResponseEntity<BookModel> deactivateBook(@PathVariable Integer id) {
        if (!checkAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!bookService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BookModel bookModel = bookService.findBookById(id);
        bookModel.setActive(false);
        return ResponseEntity.ok(this.bookService.saveBook(bookModel));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<BookModel> activateBook(@PathVariable Integer id){
        if(!checkAdmin()){
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
        if(!checkAdmin()){
            return ResponseEntity.ok(this.bookService.findAllActive());
        }
        return ResponseEntity.ok(this.bookService.findAll());
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Page<BookModel>> getAllByTitleOrAuthorOrPublisherOrTag(
            @Valid @RequestParam(value = "page") int page,
            @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @Valid @RequestParam(value = "search") String search
    ) {
        if (size == 0) size = 10;
        Page<BookModel> bookPage;
        if(checkAdmin()) {
            bookPage = bookService.findAllByTitleOrAuthorOrPublisherOrTags(page, size, search);
        } else bookPage = bookService.findAllByTitleOrAuthorOrPublisherOrTagsAndActive(page,size,search);
        return ResponseEntity.ok(bookPage);
    }

    private boolean checkAdmin(){
        return userService.findLogged().isAdmin();
    }

}
