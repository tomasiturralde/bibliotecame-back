package bibliotecame.back.Book;

import bibliotecame.back.Tag.TagModel;

import java.util.ArrayList;
import java.util.List;


public class BookSearchForm {

    String title;
    String publisher;
    String author;
    List<TagModel> tags;
    String year;

    public BookSearchForm(){
        title="";
        publisher="";
        author="";
        tags= new ArrayList<TagModel>(1);
        year="";
    }

    public BookSearchForm(String title, String publisher, String author, List<TagModel> tags, String year) {
        this.title = title;
        this.publisher = publisher;
        this.author = author;
        this.tags = tags;
        this.year = year;
    }

    public void lowerCase(){
        title=title.toLowerCase();
        publisher=publisher.toLowerCase();
        author=author.toLowerCase();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TagModel> getTags() {
        return tags;
    }

    public void setTags(List<TagModel> tags) {
        this.tags = tags;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
