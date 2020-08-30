package bibliotecame.back.Book;

import bibliotecame.back.Author.AuthorModel;
import bibliotecame.back.Publisher.PublisherModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<BookModel, Integer> {

    public Optional<BookModel> findById(int id);

    public Iterable<BookModel> findAllByActive(boolean active);

    public Optional<BookModel> findByTitleAndAuthorAndPublisherAndYear(String title, AuthorModel author, PublisherModel publisher, int year);

}
