package bibliotecame.back.Sanction;

import bibliotecame.back.User.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SanctionRepository extends CrudRepository<SanctionModel, Integer> {

    Optional<SanctionModel> findById(int id);

    Optional<SanctionModel> findByUser(UserModel user);
}
