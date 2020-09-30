package bibliotecame.back.User;

import bibliotecame.back.Loan.LoanModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserModel, Integer> {

    Optional<UserModel> findById(Integer id);

    Optional<UserModel> findByEmail(String email);
}
