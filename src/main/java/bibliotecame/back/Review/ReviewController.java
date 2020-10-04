package bibliotecame.back.Review;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Loan.LoanModel;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import javassist.NotFoundException;
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
        ReviewModel review;

        try {
            review = this.reviewService.findReviewById(id);
        } catch (NotFoundException e) {
            return unexistingReviewError();
        }

        if (!(review.getUserModel().getId() == getLogged().getId())){
            return unauthorizedActionError();
        }

        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @PostMapping("/create/{bookId}")
    public ResponseEntity createReview(@Valid @RequestBody ReviewModel reviewModel, @PathVariable Integer bookId){
        if(!bookService.exists(bookId)) return unexistingBookError();

        if(checkAdmin()) return unauthorizedActionError();

        if(reviewModel.getValue()<0 || reviewModel.getValue()>5) return new ResponseEntity<>("¡El valor de la reseña debe estar entre 0 y 5!",HttpStatus.BAD_REQUEST);

        reviewModel.setUserModel(getLogged());

        if(userPreviouslyReviewedThisOne(bookId)) return new ResponseEntity<>("¡Usted ya escribió una reseña para este libro, modifiquela en lugar de crear una nueva!",HttpStatus.TOO_MANY_REQUESTS);

        if(!userPreviouslyBookedThisOne(bookId)) return new ResponseEntity<>("¡Usted no puede escribir una reseña de un libro que no haya retirado previamente!",HttpStatus.BAD_REQUEST);

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
        return getLogged().isAdmin();
    }

    private ResponseEntity unauthorizedActionError(){
        return new ResponseEntity<>("¡No estás autorizado a realizar esta acción!",HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity unexistingBookError(){
        return new ResponseEntity<>("¡El libro solicitado no existe!",HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity unexistingReviewError(){
        return new ResponseEntity<>("¡La reseña solicitada no existe!",HttpStatus.BAD_REQUEST);
    }
}
