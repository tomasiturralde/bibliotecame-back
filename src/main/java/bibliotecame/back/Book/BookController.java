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
    public ResponseEntity getBookModel(@PathVariable Integer id){
        if(!bookService.exists(id)){
            return unexistingBookError();
        }
        BookModel book = this.bookService.findBookById(id);
        if(!book.isActive() && !checkAdmin()){
            return unauthorizedActionError();
        }
        return new ResponseEntity<>(this.bookService.findBookById(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity createBook(@Valid @RequestBody BookModel bookModel){
        if(!checkAdmin()){
            return unauthorizedActionError();
        }
        return checkAndCreateBook(bookModel);
    }

    public ResponseEntity checkAndCreateBook(BookModel bookModel){
        if(!bookService.hasTitle(bookModel) || !bookService.hasAuthor(bookModel) || !bookService.validYear(bookModel) || !bookService.hasPublisher(bookModel)){
            return new ResponseEntity<>("¡El libro recibido no es valido, verifique los campos!",HttpStatus.BAD_REQUEST);
        }
        if(bookService.exists(bookModel.getTitle(), bookModel.getAuthor(), bookModel.getPublisher(), bookModel.getYear())){
            return new ResponseEntity<>("¡El libro ya existe!",HttpStatus.NOT_ACCEPTABLE);
        }

        if(bookModel.getTags()!= null || !bookModel.getTags().isEmpty()){
            bookModel.setTags(tagService.validate(bookModel.getTags()));
        }

        return ResponseEntity.ok(this.bookService.saveBook(bookModel));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateBook(@PathVariable Integer id, @Valid @RequestBody BookModel book) {
        if (!checkAdmin()) {
            return unauthorizedActionError();
        }

        return checkAndUpdateBook(id, book);
    }

    public ResponseEntity checkAndUpdateBook(Integer id, BookModel book){
        List<CopyModel> copies = book.getCopies();
        List<CopyModel> savedCopies = new ArrayList<>();

        if(copies!=null && !copies.isEmpty()){
            if(copies.size()>=100) return new ResponseEntity<>("¡El libro ya tiene demasiados ejemplares!",HttpStatus.BAD_REQUEST);

            for(CopyModel copy : copies){
                if(copyService.exists(copy.getId())){
                    if ((copy.getBooked() && !copy.getActive())) return new ResponseEntity<>("¡No puedes desactivar un ejemplar reservado!",HttpStatus.BAD_REQUEST);
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
    public ResponseEntity deactivateBook(@PathVariable Integer id) {
        if (!checkAdmin()) {
            return unauthorizedActionError();
        }
        if (!bookService.exists(id)) {
            return unexistingBookError();
        }
        BookModel bookModel = bookService.findBookById(id);
        bookModel.setActive(false);
        return ResponseEntity.ok(this.bookService.saveBook(bookModel));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity activateBook(@PathVariable Integer id){
        if(!checkAdmin()){
            return unauthorizedActionError();
        }
        if(!bookService.exists(id)){
            return unexistingBookError();
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
        search = search.toLowerCase();
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


    private ResponseEntity unauthorizedActionError(){
        return new ResponseEntity<>("¡No estás autorizado a realizar esta acción!",HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity unexistingBookError(){
        return new ResponseEntity<>("¡El libro solicitado no existe!",HttpStatus.BAD_REQUEST);
    }

}
