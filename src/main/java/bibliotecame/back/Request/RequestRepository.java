package bibliotecame.back.Request;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestRepository extends CrudRepository<RequestModel, Integer> {

    Optional<RequestModel> findById (int id);
}
