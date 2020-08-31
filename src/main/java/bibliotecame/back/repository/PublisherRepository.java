package bibliotecame.back.repository;

import bibliotecame.back.models.PublisherModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublisherRepository extends CrudRepository<PublisherModel, Integer> {

    Optional<PublisherModel> findById(int id);

    Optional<PublisherModel> findByName(String name);
}
