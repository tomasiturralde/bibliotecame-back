package bibliotecame.back.repository;

import bibliotecame.back.models.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserModel, Integer> {

    Optional<UserModel> findById(Integer id);

}
