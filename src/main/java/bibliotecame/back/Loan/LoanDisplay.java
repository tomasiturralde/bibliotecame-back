package bibliotecame.back.Loan;

import java.time.LocalDate;

public class LoanDisplay {

    private String bookTitle;
    private String bookAuthor;
    private LocalDate expectedReturnDate;
    private LocalDate returnDate;

    public LoanDisplay(String bookTitle, String bookAuthor, LocalDate expectedReturnDate, LocalDate returnDate) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.expectedReturnDate = expectedReturnDate;
        this.returnDate = returnDate;
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
}
