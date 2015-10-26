package backend.types;

/**
 * QuizReward
 *
 * @author dath
 */
public class QuizReward {

    public int review;
    public int creator;

    public QuizReward() {
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }
}
