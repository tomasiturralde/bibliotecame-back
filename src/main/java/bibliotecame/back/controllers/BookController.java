package bibliotecame.back.controllers;

import bibliotecame.back.models.BookModel;
import bibliotecame.back.models.UserModel;
import bibliotecame.back.services.BookService;
import bibliotecame.back.services.UserService;
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

    private final UserService userService;

    @Autowired
    public BookController(BookService bookService, UserService userService){
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity<BookModel> getBookModel(@PathVariable Integer id){
        return new ResponseEntity(this.bookService.findBookById(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity createBook(@Valid @RequestBody BookModel bookModel){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = (UserModel) authentication.getPrincipal();

        if(!user.isAdmin()){
            return new ResponseEntity(bookModel, HttpStatus.UNAUTHORIZED);
        }
        if(!bookService.hasTitle(bookModel) || !bookService.hasAuthor(bookModel) || !bookService.validYear(bookModel) || !bookService.hasPublisher(bookModel)){
            return new ResponseEntity(bookModel, HttpStatus.BAD_REQUEST);
        }
        if(bookService.exists(bookModel.getTitle(), bookModel.getAuthor(), bookModel.getPublisher(), bookModel.getYear())){
            return new ResponseEntity(bookModel, HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(this.bookService.saveBook(bookModel));
    }

}
