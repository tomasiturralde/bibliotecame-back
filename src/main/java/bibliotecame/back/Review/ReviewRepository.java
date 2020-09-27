package bibliotecame.back.Review;

import bibliotecame.back.User.UserModel;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends PagingAndSortingRepository<ReviewModel, Integer> {

    Optional<ReviewModel> findById(int id);

    Iterable<ReviewModel> findAllByUserModel(UserModel userModel);

}
