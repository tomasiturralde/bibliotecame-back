package bibliotecame.back.Request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestRepository extends PagingAndSortingRepository<RequestModel, Integer> {

    Optional<RequestModel> findById (int id);

    Page<RequestModel> findAll(Pageable pageable);

}
