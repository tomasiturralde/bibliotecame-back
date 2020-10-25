package bibliotecame.back.Request;

public class RequestForm {

    private String title;
    private int year;
    private String author;
    private String publisher;
    private String reason;

    public RequestForm() {
    }

    public RequestForm(String title, int year, String author, String publisher, String reason) {
        this.title = title;
        this.year = year;
        this.author = author;
        this.publisher = publisher;
        this.reason = reason;
    }

    public RequestForm(String title, String author, String reason) {
        this.title = title;
        this.author = author;
        this.reason = reason;
        this.year = 0;
        this.publisher = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
