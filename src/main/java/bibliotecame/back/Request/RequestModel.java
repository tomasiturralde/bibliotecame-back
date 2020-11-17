package bibliotecame.back.Request;

import bibliotecame.back.User.UserModel;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
public class RequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column
    private String title;

    @Column
    private int year;

    @Column
    private String author;

    @Column
    private String publisher;

    @Column
    private String reason;

    @Column
    private LocalDate date;

    @ManyToOne
    private UserModel user;

    @Column
    private RequestStatus status;

    public RequestModel() {
    }

    public RequestModel(String title, int year, String author, String publisher, String reason) {
        this.title = title;
        this.year = year;
        this.author = author;
        this.publisher = publisher;
        this.reason = reason;
    }

    public RequestModel(RequestForm form){
        this.title = form.getTitle();
        this.year = form.getYear();
        this.author = form.getAuthor();
        this.publisher = (form.getPublisher()==null? "" : form.getPublisher());
        this.reason = form.getReason();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
