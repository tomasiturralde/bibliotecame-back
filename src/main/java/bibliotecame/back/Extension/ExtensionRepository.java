package bibliotecame.back.Extension;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtensionRepository extends CrudRepository<ExtensionModel, Integer> {
}
