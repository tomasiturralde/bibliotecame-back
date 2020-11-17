package bibliotecame.back.Copy;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CopyRepository extends CrudRepository<CopyModel, Integer> {

    Optional<CopyModel> findById(String id);
}
