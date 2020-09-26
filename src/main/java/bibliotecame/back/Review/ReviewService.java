package bibliotecame.back.Review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return this.reviewRepository.save(reviewModel);
    }

    public boolean exists(int id){
        return this.reviewRepository.findById(id).isPresent();
    }

}