package bibliotecame.back.Request;

import java.time.LocalDate;

public class RequestDisplay {
    private LocalDate date;
    private String userEmail;
    private String title;
    private RequestStatus status;
    private String author;
    private int id;

    public RequestDisplay(RequestModel requestModel, boolean withUserEmail) {
        this.date = requestModel.getDate();
        this.userEmail = withUserEmail ? requestModel.getUser().getEmail() : null;
        this.title = requestModel.getTitle();
        this.status = requestModel.getStatus();
        this.author = requestModel.getAuthor();
        this.id = requestModel.getId();
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

    public int getId() {
        return id;
    }
}
