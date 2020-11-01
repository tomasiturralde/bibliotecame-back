package bibliotecame.back.Sanction;

import bibliotecame.back.User.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SanctionRepository extends PagingAndSortingRepository<SanctionModel, Integer> {

    Optional<SanctionModel> findById(int id);

    Optional<SanctionModel> findByUser(UserModel user);

    @Query(value = "select s from SanctionModel s where" +
            " s.endDate >= :today order by s.endDate")
    Page<SanctionModel> findAllActive(Pageable pageable, @Param("today") LocalDate today);

    Iterable<SanctionModel> findAll();
}
