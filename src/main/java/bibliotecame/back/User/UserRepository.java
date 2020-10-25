package bibliotecame.back.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserModel, Integer> {

    Optional<UserModel> findById(Integer id);

    Optional<UserModel> findByEmail(String email);

    @Query(value = "select b from UserModel b where" +
            " lower(b.email) like %:search%" +
            " and b.isAdmin = false"+
            " order by b.email")
    List<UserModel> findAllByEmail(@Param("search")String email);

}
