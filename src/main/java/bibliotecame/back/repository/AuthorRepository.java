package bibliotecame.back.repository;

import bibliotecame.back.models.AuthorModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<AuthorModel, Integer> {

    Optional<AuthorModel> findById(int id);
}
