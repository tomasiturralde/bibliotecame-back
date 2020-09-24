package bibliotecame.back.Loan;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanRepository extends CrudRepository<LoanModel, Integer> {

    Optional<LoanModel> findById (int id);
}
