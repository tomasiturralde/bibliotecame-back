package bibliotecame.back.Dashboard;

public class BookDashboardDisplay {

    private String title;
    private String author;
    private double avgScore;
    private int amountOfLoans;

    public BookDashboardDisplay() {
    }

    public BookDashboardDisplay(String title, String author, double avgScore, int amountOfLoans) {
        this.title = title;
        this.author = author;
        this.avgScore = avgScore;
        this.amountOfLoans = amountOfLoans;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public double getAvgScore() {
        return avgScore;
    }

    public int getAmountOfLoans() {
        return amountOfLoans;
    }
}
