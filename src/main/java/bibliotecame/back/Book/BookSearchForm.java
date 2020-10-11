package bibliotecame.back.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class BookSearchForm {

    String title;
    String publisher;
    String author;
    List<String> tags;
    String year;

    public BookSearchForm(){
        title="";
        publisher="";
        author="";
        tags= new ArrayList<String>(1);
        year="";
    }

    public BookSearchForm(String title, String publisher, String author, List<String> tags, String year) {
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
        tags = tags.stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public boolean isEmpty(){
        return ((title+publisher+author+year).length()==0 && tags.isEmpty());
    }

    public boolean matches(BookModel bookModel){
        if(this.isEmpty()) return true;
        if(!title.equals("") && !bookModel.getTitle().toLowerCase().contains(title)) return false;
        if(!publisher.equals("") && !bookModel.getPublisher().toLowerCase().contains(publisher)) return false;
        if(!author.equals("") && !bookModel.getAuthor().toLowerCase().contains(author)) return false;
        if(!year.equals("") && !Integer.toString(bookModel.getYear()).toLowerCase().contains(year)) return false;
        List<String> bookTags = bookModel.getTags().stream().map(tagModel -> tagModel.getName().toLowerCase()).collect(Collectors.toList());
        List<String> checkTags = new ArrayList<>(getTags());
        if(!checkTags.isEmpty()){
            if(bookTags.isEmpty()) return false;
            for (int i = 0; i <bookTags.size() ; i++) {
                for (int j = 0; j <checkTags.size() ; j++) {
                    if(bookTags.get(i).contains(checkTags.get(j))) {
                        checkTags.remove(j);
                        break;
                    }
                }
            }
            if(!checkTags.isEmpty()) return false;
        }
        return true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
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
