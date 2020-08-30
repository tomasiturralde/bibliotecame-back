package bibliotecame.back.Tag;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<TagModel,Integer> {

    Optional<TagModel> findById(int id);

    Optional<TagModel> findByName(String name);
}