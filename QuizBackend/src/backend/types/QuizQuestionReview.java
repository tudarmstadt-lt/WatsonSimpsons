package backend.types;

import com.google.gson.Gson;

/**
 * QuizQuestionRating
 *
 * @author dath
 */
public class QuizQuestionReview {
    int questionID;
    int userID;
    int answerCorrect;
    int answerDistractors;
    int questionFormulation;
    int questionDifficulty;
    int num;
    float avgAnswerCorrect;
    float avgAnswerDistractors;
    float avgQuestionFormulation;
    float avgQuestionDifficulty;
    String timestamp;

    public QuizQuestionReview(int questionID, int answerCorrect, int answerDistractors, int questionFormulation, int questionDifficulty) {
        this.questionID = questionID;
        this.answerCorrect = answerCorrect;
        this.answerDistractors = answerDistractors;
        this.questionFormulation = questionFormulation;
        this.questionDifficulty = questionDifficulty;
    }

    public QuizQuestionReview(int questionID, int num, float avgAnswerCorrect, float avgAnswerDistractors, float avgQuestionFormulation, float avgQuestionDifficulty) {
        this.questionID = questionID;
        this.num = num;
        this.avgAnswerCorrect = avgAnswerCorrect;
        this.avgAnswerDistractors = avgAnswerDistractors;
        this.avgQuestionFormulation = avgQuestionFormulation;
        this.avgQuestionDifficulty = avgQuestionDifficulty;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getAnswerCorrect() {
        return answerCorrect;
    }

    public void setAnswerCorrect(int answerCorrect) {
        this.answerCorrect = answerCorrect;
    }

    public int getAnswerDistractors() {
        return answerDistractors;
    }

    public void setAnswerDistractors(int answerDistractors) {
        this.answerDistractors = answerDistractors;
    }

    public int getQuestionFormulation() {
        return questionFormulation;
    }

    public void setQuestionFormulation(int questionFormulation) {
        this.questionFormulation = questionFormulation;
    }

    public float getQuestionDifficulty() {
        return questionDifficulty;
    }

    public void setQuestionDifficulty(int questionDifficulty) {
        this.questionDifficulty = questionDifficulty;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public float getAvgAnswerCorrect() {
        return avgAnswerCorrect;
    }

    public void setAvgAnswerCorrect(float avgAnswerCorrect) {
        this.avgAnswerCorrect = avgAnswerCorrect;
    }

    public float getAvgAnswerDistractors() {
        return avgAnswerDistractors;
    }

    public void setAvgAnswerDistractors(float avgAnswerDistractors) {
        this.avgAnswerDistractors = avgAnswerDistractors;
    }

    public float getAvgQuestionFormulation() {
        return avgQuestionFormulation;
    }

    public void setAvgQuestionFormulation(float avgQuestionFormulation) {
        this.avgQuestionFormulation = avgQuestionFormulation;
    }

    public float getAvgQuestionDifficulty() {
        return avgQuestionDifficulty;
    }

    public void setAvgQuestionDifficulty(float avgQuestionDifficulty) {
        this.avgQuestionDifficulty = avgQuestionDifficulty;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        if(avgQuestionDifficulty > 0.0) {
            return "Avg-Review for Question " + questionID + ": " +
                    "numOfReviews = " + num + ", " +
                    "answerCorrect = " + avgAnswerCorrect + ", " +
                    "answerDistractors =" + avgAnswerDistractors + ", " +
                    "questionFormulation =" + avgQuestionFormulation + ", " +
                    "difficulty = " + avgQuestionDifficulty;
        }

        return "Review for Question " + questionID + ": " +
                (answerCorrect == 1 ? "correct" : "incorrect") + ", " +
                (answerDistractors == 1 ? "good distractors" : "bad distractors") + ", " +
                (questionFormulation == 1? "good formulation" : "bad formulation") + ", " +
                "difficulty = " + questionDifficulty;
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
