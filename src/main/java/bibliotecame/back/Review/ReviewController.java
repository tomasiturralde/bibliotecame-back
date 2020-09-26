package bibliotecame.back.Review;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Loan.LoanModel;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final BookService bookService;
    private final ReviewService reviewService;
    private final UserService userService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService, BookService bookService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.bookService = bookService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewModel> getReviewModel(@PathVariable Integer id){
        //Este método será ampliado y modificado por quien corresponda cuando se haga la US "Visualizar reseña"
        return new ResponseEntity<>(this.reviewService.findReviewById(id), HttpStatus.OK);
    }

    @PostMapping("/create/{bookId}")
    public ResponseEntity<ReviewModel> createReview(@Valid @RequestBody ReviewModel reviewModel, @PathVariable Integer bookId){
        if(!bookService.exists(bookId)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(checkAdmin()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if(reviewModel.getValue()<0 || reviewModel.getValue()>5) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(reviewModel.getUserModel().getId()!=getLogged().getId()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if(!userPreviouslyBookedThisOne(bookId)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        reviewService.saveReview(reviewModel);
        BookModel bookToUpdate = bookService.findBookById(bookId);
        bookToUpdate.getReviews().add(reviewModel);
        bookService.saveBook(bookToUpdate);

        return new ResponseEntity<>(reviewModel,HttpStatus.OK);
    }

    private boolean userPreviouslyBookedThisOne(Integer bookId){
        List<LoanModel> loans = getLogged().getLoans();
        List<CopyModel> copies = bookService.findBookById(bookId).getCopies();
        List<String> copiesIds = new ArrayList<>();
        for (int i = 0; i < copies.size() ; i++) {
            copiesIds.add(copies.get(i).getId());
        }
        for (int i = 0; i < loans.size() ; i++) {
            if(copiesIds.contains(loans.get(i).getCopy().getId())) return true;
        }
        return false;
    }

    private UserModel getLogged(){
        return userService.findLogged();
    }

    private boolean checkAdmin(){
        return userService.findLogged().isAdmin();
    }

}
