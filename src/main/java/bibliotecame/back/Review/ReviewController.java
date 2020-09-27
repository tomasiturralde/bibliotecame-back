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
import java.util.List;
import java.util.stream.Collectors;

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

        reviewModel.setUserModel(getLogged());

        if(userPreviouslyReviewedThisOne(bookId)) return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);

        if(!userPreviouslyBookedThisOne(bookId)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        reviewService.saveReview(reviewModel);
        BookModel bookToUpdate = bookService.findBookById(bookId);
        bookToUpdate.getReviews().add(reviewModel);
        bookService.saveBook(bookToUpdate);

        return new ResponseEntity<>(reviewModel,HttpStatus.OK);
    }

    private boolean userPreviouslyBookedThisOne(Integer bookId){
        List<LoanModel> loans = getLogged().getLoans();
        List<String> copiesIds = bookService.findBookById(bookId).getCopies().stream().map(CopyModel::getId).collect(Collectors.toList());
        return loans.stream().map(loanModel -> copiesIds.contains(loanModel.getCopy().getId())).reduce(false, ((aBoolean, aBoolean2) -> aBoolean || aBoolean2));
    }

    private boolean userPreviouslyReviewedThisOne(Integer bookId){
        List<Integer> reviewsIds = reviewService.findAllByUserModel(getLogged()).stream().map(ReviewModel::getId).collect(Collectors.toList());
        List<ReviewModel> bookReviews = bookService.findBookById(bookId).getReviews();
        return bookReviews.stream().map(reviewModel -> reviewsIds.contains(reviewModel.getId())).reduce(false, ((aBoolean, aBoolean2) -> aBoolean || aBoolean2));
    }

    private UserModel getLogged(){
        return userService.findLogged();
    }

    private boolean checkAdmin(){
        return userService.findLogged().isAdmin();
    }

}
