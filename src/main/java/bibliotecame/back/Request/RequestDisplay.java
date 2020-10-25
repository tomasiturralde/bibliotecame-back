package bibliotecame.back.Request;

import java.time.LocalDate;

public class RequestDisplay {
    private LocalDate date;
    private String userEmail;
    private String title;
    private RequestStatus status;
    private String author;

    public RequestDisplay(RequestModel requestModel) {
        this.date = requestModel.getDate();
        this.userEmail = requestModel.getUser().getEmail();
        this.title = requestModel.getTitle();
        this.status = requestModel.getStatus();
        this.author = requestModel.getAuthor();
    }

    public RequestDisplay() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
