package bibliotecame.back.Review;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.ErrorMessage;
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
    public ResponseEntity<ReviewModel> getReviewModel(@PathVariable Integer id) {
        ReviewModel review;

        try {
            review = this.reviewService.findReviewById(id);
        } catch (NotFoundException e) {
            return unexistingReviewError();
        }

        if (!(review.getUserModel().getId() == getLogged().getId())) {
            return unauthorizedActionError();
        }

        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @PostMapping("/create/{bookId}")
    public ResponseEntity createReview(@Valid @RequestBody ReviewModel reviewModel, @PathVariable Integer bookId) {
        if (!bookService.exists(bookId)) return unexistingBookError();

        if (checkAdmin()) return unauthorizedActionError();

        return createReviewKnowingUser(reviewModel, bookId, getLogged());
    }

    public ResponseEntity createReviewKnowingUser(ReviewModel reviewModel, Integer bookId, UserModel userModel) {
        if (reviewModel.getValue() < 1 || reviewModel.getValue() > 5) return illegalValueError();

        reviewModel.setUserModel(userModel);

        if (userPreviouslyReviewedThisOne(bookId, userModel))
            return new ResponseEntity<>(new ErrorMessage("¡Usted ya escribió una reseña para este libro, modifiquela en lugar de crear una nueva!"), HttpStatus.TOO_MANY_REQUESTS);

        if (!userPreviouslyBookedThisOne(bookId, userModel))
            return new ResponseEntity<>(new ErrorMessage("¡Usted no puede escribir una reseña de un libro que no haya retirado y devuelto previamente!"), HttpStatus.BAD_REQUEST);

        reviewService.saveReview(reviewModel);
        BookModel bookToUpdate = bookService.findBookById(bookId);
        bookToUpdate.getReviews().add(reviewModel);
        bookService.saveBook(bookToUpdate);

        return new ResponseEntity<>(reviewModel, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateReview(@PathVariable Integer id, @Valid @RequestBody ReviewModel reviewModel) {
        if (checkAdmin()) {
            return unauthorizedActionError();
        }

        try {
            if (reviewService.findReviewById(id).getUserModel().getId() != userService.findLogged().getId()) {
                return new ResponseEntity(new ErrorMessage("¡No puedes modificar una reseña escrita por otro alumno!"), HttpStatus.UNAUTHORIZED);
            }
        } catch (NotFoundException e) {
            return unexistingReviewError();
        }

        if (reviewModel.getId() != id)
            return new ResponseEntity(new ErrorMessage("¡No puedes modificar una reseña a través de otra!"), HttpStatus.BAD_REQUEST);

        if (reviewModel.getValue() < 1 || reviewModel.getValue() > 5) return illegalValueError();

        reviewModel.setUserModel(userService.findLogged());

        return new ResponseEntity(reviewService.saveReview(reviewModel), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteReview(@PathVariable Integer id) {

        try {
            reviewService.findReviewById(id);
        } catch (NotFoundException e) {
            return unexistingReviewError();
        }

        if (getLogged().isAdmin())
            return new ResponseEntity(new ErrorMessage("Solo un usuario alumno puede eliminar la reseña"), HttpStatus.UNAUTHORIZED);

        try {
            if (!checkReviewIsFromUser(id, getLogged().getId()))
                return new ResponseEntity(new ErrorMessage("Solo el alumno que realizó esta reseña puede eliminarla"), HttpStatus.UNAUTHORIZED);
        } catch (NotFoundException e) {
            return unexistingReviewError();
        }

        BookModel book = null;
        for (BookModel bookModel : bookService.findAll()) {
            if (bookModel.getReviews().stream().anyMatch(reviewModel -> reviewModel.getId() == id)) book = bookModel;
        }

        if (book == null) return unexistingBookError();

        List<ReviewModel> reviews = book.getReviews().stream().filter(reviewModel -> reviewModel.getId() != id).collect(Collectors.toList());

        book.setReviews(reviews);
        bookService.saveBook(book);
        return new ResponseEntity(reviewService.deleteReview(id), HttpStatus.OK);
    }

    private boolean userPreviouslyBookedThisOne(Integer bookId, UserModel user) {
        List<LoanModel> loans = user.getLoans().stream().filter(loanModel -> loanModel.getReturnDate() != null).collect(Collectors.toList());
        List<String> copiesIds = bookService.findBookById(bookId).getCopies().stream().map(CopyModel::getId).collect(Collectors.toList());
        return loans.stream().map(loanModel -> copiesIds.contains(loanModel.getCopy().getId())).reduce(false, ((aBoolean, aBoolean2) -> aBoolean || aBoolean2));
    }

    private boolean userPreviouslyReviewedThisOne(Integer bookId, UserModel user) {
        List<Integer> reviewsIds = reviewService.findAllByUserModel(user).stream().map(ReviewModel::getId).collect(Collectors.toList());
        List<ReviewModel> bookReviews = bookService.findBookById(bookId).getReviews();
        return bookReviews.stream().map(reviewModel -> reviewsIds.contains(reviewModel.getId())).reduce(false, ((aBoolean, aBoolean2) -> aBoolean || aBoolean2));
    }

    private boolean checkReviewIsFromUser(Integer reviewId, Integer userId) throws NotFoundException {
        return reviewService.findReviewById(reviewId).getUserModel().getId() == userId;
    }

    private UserModel getLogged() {
        return userService.findLogged();
    }

    private boolean checkAdmin() {
        return getLogged().isAdmin();
    }

    private ResponseEntity unauthorizedActionError() {
        return new ResponseEntity<>(new ErrorMessage("¡Usted no está autorizado a realizar esta acción!"), HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity unexistingBookError() {
        return new ResponseEntity<>(new ErrorMessage("¡El libro solicitado no existe!"), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity unexistingReviewError() {
        return new ResponseEntity<>(new ErrorMessage("¡La reseña solicitada no existe!"), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity illegalValueError() {
        return new ResponseEntity<>(new ErrorMessage("¡El valor de la reseña debe estar entre 1 y 5!"), HttpStatus.BAD_REQUEST);
    }
}
