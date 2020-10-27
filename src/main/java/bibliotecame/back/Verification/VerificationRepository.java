package bibliotecame.back.Verification;

import bibliotecame.back.User.UserModel;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface VerificationRepository  extends PagingAndSortingRepository<VerificationModel, Integer> {

    Optional<VerificationModel> findByToken(String token);

    Optional<VerificationModel> findByUserModel(UserModel userModel);
}
