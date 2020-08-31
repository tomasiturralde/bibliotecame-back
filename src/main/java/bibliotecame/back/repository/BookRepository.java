package bibliotecame.back.repository;

import bibliotecame.back.models.AuthorModel;
import bibliotecame.back.models.BookModel;
import bibliotecame.back.models.PublisherModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<BookModel, Integer> {

    public Optional<BookModel> findById(int id);

    public Optional<BookModel> findByTitleAndAuthorAndPublisherAndYear(String title, AuthorModel author, PublisherModel publisher, int year);

}
