package bibliotecame.back.Book;

import bibliotecame.back.Author.AuthorModel;
import bibliotecame.back.Publisher.PublisherModel;
import bibliotecame.back.Tag.TagModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class BookModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column
    private String title;

    @Column
    private int year;

    @ManyToOne
    private AuthorModel author;

    @ManyToOne
    private PublisherModel publisher;

    @ManyToMany
    private List<TagModel> tags;

    @Column
    private boolean active;

    public BookModel() {
        this.active= true;
    }

    public BookModel(String title, int year, AuthorModel author, PublisherModel publisher) {
        this.title = title;
        this.year = year;
        this.author = author;
        this.publisher = publisher;
        tags = new ArrayList<>();
        this.active= true;
    }

    public BookModel(String title, int year, AuthorModel author, PublisherModel publisher, List<TagModel> tags) {
        this.title = title;
        this.year = year;
        this.author = author;
        this.publisher = publisher;
        this.tags = tags;
        this.active= true;
    }

    public int getId() {
        return id;
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

    public AuthorModel getAuthor() {
        return author;
    }

    public void setAuthor(AuthorModel author) {
        this.author = author;
    }

    public PublisherModel getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherModel publisher) {
        this.publisher = publisher;
    }

    public List<TagModel> getTags() {
        return tags;
    }

    public void setTags(List<TagModel> tags) {
        this.tags = tags;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
