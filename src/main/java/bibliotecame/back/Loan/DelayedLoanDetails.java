package bibliotecame.back.Loan;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.User.UserModel;

import java.time.LocalDate;

public class DelayedLoanDetails {
    private int id;
    private String bookTitle;
    private LocalDate withdrawDate;
    private LocalDate returnDate;
    private String userEmail;
    private String userName;

    public DelayedLoanDetails(LoanModel loanModel, UserModel userModel, BookModel bookModel){
        this.id= loanModel.getId();
        this.bookTitle= bookModel.getTitle();
        this.withdrawDate= loanModel.getWithdrawalDate();
        this.returnDate= loanModel.getExpirationDate();
        this.userEmail= userModel.getEmail();
        this.userName= userModel.getFirstName();
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

    public LocalDate getWithdrawDate() {
        return withdrawDate;
    }

    public void setWithdrawDate(LocalDate withdrawDate) {
        this.withdrawDate = withdrawDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
