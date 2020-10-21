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

    @Query(value = "select b from SanctionModel b where" +
            " b.endDate >= :today and " +
            " lower(b.user.email) like %:search% order by b.endDate")
    Page<SanctionModel> findAllByUserOrReasonAndActive(Pageable pageable, @Param("search")String title, @Param("today")LocalDate today);
}
