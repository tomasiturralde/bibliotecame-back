package bibliotecame.back.Tag;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<TagModel,Integer> {

    Optional<TagModel> findById(int id);

    Optional<TagModel> findByName(String name);

    @Query(value = "select t from TagModel t where t.name like %:name%")
    Iterable<TagModel> findByNameWildcard(@Param("name")String name);
}