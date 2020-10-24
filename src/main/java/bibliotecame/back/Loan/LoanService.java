package bibliotecame.back.Loan;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.Review.ReviewModel;
import bibliotecame.back.Review.ReviewService;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.toIntExact;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final CopyService copyService;

    @Autowired
    public LoanService(LoanRepository loanRepository, BookService bookService, UserService userService, ReviewService reviewService, CopyService copyService) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.copyService = copyService;
    }

    public LoanModel saveLoan(LoanModel loanModel){
        return this.loanRepository.save(loanModel);
    }

    public LoanModel getLoanById(int loanId) throws NotFoundException {
        return this.loanRepository.findById(loanId).orElseThrow(() -> new NotFoundException("Loan not found"));
    }

    public Page<LoanDisplay> getLoansPage(int page, int size, String search){
        Pageable pageable = PageRequest.of(page, size);
        List<LoanModel> list = findAll();
        list.sort((l1, l2) -> {
            if(l1.getReturnDate() == null && l2.getReturnDate() != null) return -1;
            if(l1.getReturnDate() != null && l2.getReturnDate() == null) return 1;
            return l1.getExpirationDate().compareTo(l2.getExpirationDate());
        });
        List<LoanDisplay> result = new ArrayList<>();
        List<LoanDisplay> finalResult = result;
        list.forEach(loanModel -> finalResult.add(turnLoanModalToDisplay(loanModel, Optional.of(userService.getUserFromLoan(loanModel)),true)));
        result = finalResult.stream().filter(loanDisplay -> loanDisplayMatches(loanDisplay,search)).collect(Collectors.toList());
        int total = result.size();
        int start = toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), total);
        List<LoanDisplay> output = new ArrayList<>();
        if (start <= end) {
            output = result.subList(start, end);
        }
        return new PageImpl<>(output, pageable, result.size());
    }

    private boolean loanDisplayMatches(LoanDisplay display, String lookFor){
        if (display.getBookAuthor().toLowerCase().contains(lookFor) ||
                display.getBookTitle().toLowerCase().contains(lookFor) ||
                display.getUserEmail().toLowerCase().contains(lookFor) ||
                display.getLoanStatus().getLabel().toLowerCase().contains(lookFor)) return true;
        return false;
    }

    public LoanDisplay turnLoanModalToDisplay(LoanModel modal, Optional<UserModel> user, boolean withStatus){
        BookModel book = bookService.findBookByCopy(modal.getCopy());
        LoanDisplay display = user.map(userModel -> new LoanDisplay(modal.getId(),book.getTitle(), book.getAuthor(), modal.getExpirationDate(), modal.getReturnDate(), userModel.getEmail()))
                .orElseGet(() -> new LoanDisplay(modal.getId(),book.getTitle(), book.getAuthor(), modal.getExpirationDate(), modal.getReturnDate(), getReviewByBook(book.getId()), book.getId()));
        return withStatus? setLoanDisplayStatus(modal, display) : display;
    }

    public DelayedLoanDetails turnLoanModalToDelayedDetails(LoanModel modal){
        BookModel book = bookService.findBookByCopy(modal.getCopy());
        return new DelayedLoanDetails(modal,userService.getUserFromLoan(modal),book);
    }

    private Integer getReviewByBook(Integer bookId){
        List<ReviewModel> userReviews = reviewService.findAllByUserModel(userService.findLogged());
        List<ReviewModel> bookReviews = bookService.findBookById(bookId).getReviews();
        List<ReviewModel> intersection = userReviews.stream().distinct().filter(bookReviews::contains).collect(Collectors.toList());
        return intersection.size() > 0 ? intersection.get(0).getId() : null;
    }

    public List<LoanModel> findAll(){
        return (List<LoanModel>) loanRepository.findAll();
    }

    public LoanDisplay setLoanDisplayStatus(LoanModel model, LoanDisplay display){
        if(model.getReturnDate() != null) display.setLoanStatus(LoanStatus.RETURNED);
        else if(model.getExpirationDate().isBefore(LocalDate.now())) display.setLoanStatus(LoanStatus.DELAYED);
        else if(model.getExtension() != null) display.setLoanStatus(LoanStatus.getFromInt(model.getExtension().getStatus().ordinal()));
        else if(model.getWithdrawalDate() != null) display.setLoanStatus(LoanStatus.WITHDRAWN);
        else display.setLoanStatus(LoanStatus.READY_FOR_WITHDRAWAL);
        return display;
    }

    public void deleteEveryExpiredLoan(){
        for(UserModel user : userService.getUsersWithLoans()){
            deleteExpirationLoansOfUsers(user);
        }
    }

    public void deleteExpirationLoansOfUsers(UserModel user){
        List<LoanModel> remainingLoans = new ArrayList<>();
        List<LoanModel> deletingLoans = new ArrayList<>();
        for(LoanModel loan: user.getLoans()){
            if(loan.getExpirationDate().isBefore(LocalDate.now()) && loan.getWithdrawalDate() == null){
                deletingLoans.add(loan);
            } else {
                remainingLoans.add(loan);
            }
        }
        user.setLoans(remainingLoans);
        userService.saveWithoutEncryption(user);
        for(LoanModel loan: deletingLoans){
            loan.getCopy().setBooked(false);
            copyService.saveCopy(loan.getCopy());
            loanRepository.delete(loan);
        }
    }

    public List<LoanModel> getDelayedLoans(){
        return findAll().stream().filter(loan -> loan.getWithdrawalDate()!=null && loan.getReturnDate()==null && loan.getExpirationDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public Page<LoanDisplay> getReturnedLoansPage(int page, int size, UserModel user, String search){
        Pageable pageable = PageRequest.of(page, size);
        List<LoanModel> returned = new ArrayList<>();
        for(LoanModel loan : user.getLoans()){
            if(loan.getReturnDate() != null){
                returned.add(loan);
            }
        }
        returned.sort((l0, l1) -> l1.getReturnDate().compareTo(l0.getReturnDate()));

        Stream<LoanDisplay> result = returned.stream().map(l -> turnLoanModalToDisplay(l, Optional.empty(), false));

        List<LoanDisplay> list;
        if(!search.equals("")){
            list = result.filter(l -> l.getBookTitle().toLowerCase().contains(search) ||
                                 l.getExpectedReturnDate().toString().contains(search) ||
                                 l.getReturnDate().toString().contains(search)).collect(Collectors.toList());
        } else {
            list = result.collect(Collectors.toList());
        }


        int start = page*size;
        int end = Math.min((start + size), list.size());
        List<LoanDisplay> output = new ArrayList<>();
        if (start <= end) {
            output = list.subList(start, end);
        }
        return new PageImpl<>(output, pageable, list.size());
    }
}
