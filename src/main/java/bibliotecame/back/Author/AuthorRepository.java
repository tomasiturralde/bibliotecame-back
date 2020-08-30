package bibliotecame.back.Author;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<AuthorModel, Integer> {

    Optional<AuthorModel> findById(int id);

    Optional<AuthorModel> findByFirstNameAndLastName(String firstName, String lastName);
}
