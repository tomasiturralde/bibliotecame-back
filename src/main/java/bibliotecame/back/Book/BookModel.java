package bibliotecame.back.Book;

import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Tag.TagModel;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@SuppressWarnings("unused")
public class BookModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column
    private String title;

    @Column
    private int year;

    private String author;

    private String publisher;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TagModel> tags;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<CopyModel> copies;

    @Column
    private boolean active;

    public BookModel() {
        this.active= true;
    }

    public BookModel(String title, int year, String author, String publisher) {
        this.title = title;
        this.year = year;
        this.author = author;
        this.publisher = publisher;
        tags = new ArrayList<>();
        this.active= true;
        copies = new ArrayList<>();
    }

    public BookModel(String title, int year, String author, String publisher, List<TagModel> tags) {
        this.title = title;
        this.year = year;
        this.author = author;
        this.publisher = publisher;
        this.tags = tags;
        this.active= true;
        copies = new ArrayList<>();
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

    public List<CopyModel> getCopies() {
        return copies;
    }

    public void setCopies(List<CopyModel> copies) {
        this.copies = copies;
    }
}
