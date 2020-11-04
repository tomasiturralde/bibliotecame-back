package bibliotecame.back.Dashboard;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Loan.LoanModel;
import bibliotecame.back.Loan.LoanService;
import bibliotecame.back.Review.ReviewModel;
import bibliotecame.back.Review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DashboardService {

    private final LoanService loanService;
    private final BookService bookService;

    @Autowired
    public DashboardService(LoanService loanService, BookService bookService) {
        this.loanService = loanService;
        this.bookService = bookService;
    }

    public long getAmountOfBooks(){
        return StreamSupport.stream(bookService.findAll().spliterator(), false).count();
    }

    public int getDelayedLoans(){
        return loanService.getDelayedLoans().size();
    }

    public int getWithdrawalLoans(){
        return loanService.getWithdrawnLoans().size();
    }

    public int getReadyForWithdrawalLoans(){
        return loanService.getReadyForWithdrawal().size();
    }

    public Map<String, Integer> getLoansByMonthOfLastYear() {
        Map<String, Integer> result = new LinkedHashMap<>();
        Month start = LocalDate.now().getMonth().plus(1);
        int year = LocalDate.now().getYear() -1;

        for (int i = 0; i < 12; i++) {
            if(start.plus(i).getValue() == 1) ++year;
            Month actualMonth = start.plus(i);
            int finalYear = year;
            result.put(actualMonth.toString() + " " + year, Math.toIntExact(loanService.findAll().stream().filter(loan ->
                    loan.getReservationDate().getMonth().getValue() == actualMonth.getValue() &&
                            loan.getReservationDate().getYear() == finalYear).count()));
        }
        return result;
    }

    public List<BookModel> get5BestReviewed() {
        return StreamSupport.stream(bookService.findAll().spliterator(), false).sorted(new Comparator<BookModel>() {
            @Override
            public int compare(BookModel book0, BookModel book1) {
                return Double.compare(getAverageScore(book1), getAverageScore(book0));
            }
        }).collect(Collectors.toList()).subList(0, 5);
    }

    public List<BookModel> get5MostLoaned(){
        return StreamSupport.stream(bookService.findAll().spliterator(), false).sorted(new Comparator<BookModel>() {
            @Override
            public int compare(BookModel book0, BookModel book1) {
                return Double.compare(getAmountOfLoans(book1), getAmountOfLoans(book0));
            }
        }).collect(Collectors.toList()).subList(0, 5);
    }

    public double getAverageScore(BookModel book){
        int total = book.getReviews().stream().mapToInt(ReviewModel::getValue).sum();
        if(book.getReviews().size() != 0) return total / book.getReviews().size();
        else return 0;
    }

    public int getAmountOfLoans(BookModel book){
        return book.getCopies().stream().mapToInt(this::getAmountOfLoansByCopy).sum();
    }

    public int getAmountOfLoansByCopy(CopyModel copy){
        List<LoanModel> loans = new ArrayList<>();

        for(LoanModel loan : loanService.findAll()){
            if(loan.getCopy().getId() == copy.getId()) loans.add(loan);
        }
        return loans.size();
    }
}
