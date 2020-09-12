package bibliotecame.back.Book;

import bibliotecame.back.Copy.CopyModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<BookModel, Integer> {

    Optional<BookModel> findById(int id);

    Iterable<BookModel> findAllByActive(boolean active);

    Optional<BookModel> findByTitleAndAuthorAndPublisherAndYear(String title, String author, String publisher, int year);

}
