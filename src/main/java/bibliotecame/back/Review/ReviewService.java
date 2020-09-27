package bibliotecame.back.Review;

import bibliotecame.back.User.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    public ReviewModel findReviewById(int id){
        return this.reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review with id: " + id + " not found!"));
    }

    public ReviewModel saveReview(ReviewModel reviewModel){
        if(reviewModel.getDescription()==null) reviewModel.setDescription("");
        return this.reviewRepository.save(reviewModel);
    }

    public List<ReviewModel> findAllByUserModel(UserModel userModel){
        Iterable<ReviewModel> reviews = this.reviewRepository.findAllByUserModel(userModel);
        List<ReviewModel> actualList = new ArrayList<>();
        reviews.iterator().forEachRemaining(actualList::add);
        return actualList;
    }

    public boolean exists(int id){
        return this.reviewRepository.findById(id).isPresent();
    }

}