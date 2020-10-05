package bibliotecame.back.Loan;

import java.time.LocalDate;

@SuppressWarnings("unused")
public class LoanDisplay {

    private String bookTitle;
    private String bookAuthor;
    private LocalDate expectedReturnDate;
    private LocalDate returnDate;
    private LoanStatus loanStatus;
    private String userEmail;

    public LoanDisplay(String bookTitle, String bookAuthor, LocalDate expectedReturnDate, LocalDate returnDate) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.expectedReturnDate = expectedReturnDate;
        this.returnDate = returnDate;
        loanStatus = null;
        userEmail = null;
    }

    public LoanDisplay(String bookTitle, String bookAuthor, LocalDate expectedReturnDate, LocalDate returnDate, LoanStatus loanStatus) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.expectedReturnDate = expectedReturnDate;
        this.returnDate = returnDate;
        this.loanStatus = loanStatus;
        userEmail = null;
    }

    public LoanDisplay(String bookTitle, String bookAuthor, LocalDate expectedReturnDate, LocalDate returnDate, String userEmail) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.expectedReturnDate = expectedReturnDate;
        this.returnDate = returnDate;
        this.userEmail = userEmail;
        loanStatus = null;
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
}
