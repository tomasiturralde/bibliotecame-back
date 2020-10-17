package bibliotecame.back.Loan;

import java.time.LocalDate;

@SuppressWarnings("unused")
public class LoanDisplay {

    private int id;
    private String bookTitle;
    private String bookAuthor;
    private LocalDate expectedReturnDate;
    private LocalDate returnDate;
    private LoanStatus loanStatus;
    private String userEmail;
    private Integer reviewId;
    private Integer bookId;

    public LoanDisplay(int id, String bookTitle, String bookAuthor, LocalDate expectedReturnDate, LocalDate returnDate, Integer reviewId, Integer bookId) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.expectedReturnDate = expectedReturnDate;
        this.returnDate = returnDate;
        loanStatus = null;
        userEmail = null;
        this.reviewId = reviewId;
        this.bookId = bookId;
    }

    public LoanDisplay(int id, String bookTitle, String bookAuthor, LocalDate expectedReturnDate, LocalDate returnDate, LoanStatus loanStatus) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.expectedReturnDate = expectedReturnDate;
        this.returnDate = returnDate;
        this.loanStatus = loanStatus;
        userEmail = null;
        reviewId = null;
        bookId = null;
    }

    public LoanDisplay(int id, String bookTitle, String bookAuthor, LocalDate expectedReturnDate, LocalDate returnDate, String userEmail) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.expectedReturnDate = expectedReturnDate;
        this.returnDate = returnDate;
        this.userEmail = userEmail;
        loanStatus = null;
        reviewId = null;
        bookId = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LoanStatus getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(LoanStatus loanStatus) {
        this.loanStatus = loanStatus;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public Integer getBookId() {
        return bookId;
    }
}
