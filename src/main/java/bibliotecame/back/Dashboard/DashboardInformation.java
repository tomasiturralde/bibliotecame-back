package bibliotecame.back.Dashboard;

import org.springframework.data.util.Pair;

import java.util.List;

public class DashboardInformation {

    private int delayedLoans;
    private int withdrawnLoans;
    private int readyForWithdrawalLoans;
    private List<Pair<String, Integer>> loansByMonth;
    private long amountOfBooks;
    private int amountOfStudents;
    private List<BookDashboardDisplay> bestReviewed;
    private List<BookDashboardDisplay> mostLoaned;

    public DashboardInformation() {
    }

    public int getDelayedLoans() {
        return delayedLoans;
    }

    public void setDelayedLoans(int delayedLoans) {
        this.delayedLoans = delayedLoans;
    }

    public int getWithdrawnLoans() {
        return withdrawnLoans;
    }

    public void setWithdrawnLoans(int withdrawnLoans) {
        this.withdrawnLoans = withdrawnLoans;
    }

    public int getReadyForWithdrawalLoans() {
        return readyForWithdrawalLoans;
    }

    public void setReadyForWithdrawalLoans(int readyForWithdrawalLoans) {
        this.readyForWithdrawalLoans = readyForWithdrawalLoans;
    }

    public List<Pair<String, Integer>> getLoansByMonth() {
        return loansByMonth;
    }

    public void setLoansByMonth(List<Pair<String, Integer>> loansByMonth) {
        this.loansByMonth = loansByMonth;
    }

    public long getAmountOfBooks() {
        return amountOfBooks;
    }

    public void setAmountOfBooks(long amountOfBooks) {
        this.amountOfBooks = amountOfBooks;
    }

    public int getAmountOfStudents() {
        return amountOfStudents;
    }

    public void setAmountOfStudents(int amountOfStudents) {
        this.amountOfStudents = amountOfStudents;
    }

    public List<BookDashboardDisplay> getBestReviewed() {
        return bestReviewed;
    }

    public void setBestReviewed(List<BookDashboardDisplay> bestReviewed) {
        this.bestReviewed = bestReviewed;
    }

    public List<BookDashboardDisplay> getMostLoaned() {
        return mostLoaned;
    }

    public void setMostLoaned(List<BookDashboardDisplay> mostLoaned) {
        this.mostLoaned = mostLoaned;
    }
}
