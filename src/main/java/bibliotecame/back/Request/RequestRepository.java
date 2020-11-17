package bibliotecame.back.Request;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestRepository extends PagingAndSortingRepository<RequestModel, Integer> {

    Optional<RequestModel> findById (int id);

    @Query(value="select r from RequestModel r order by r.status,r.date desc ")
    Iterable<RequestModel> findAllOrdered();
}
