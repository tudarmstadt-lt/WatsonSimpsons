package backend.types;


import com.google.gson.Gson;

/**
 * QuizQuestion
 *
 * @author dath
 */
public class QuizQuestion {

    int id;
    String question;
    String correctAnswer;
    String falseAnswer1;
    String falseAnswer2;
    String falseAnswer3;
    String category;
    int status;
    String createdAt;
    String modifiedAt;
    int reviewNum;

    public QuizQuestion() {
        this.question = "";
        this.correctAnswer = "";
        this.falseAnswer1 = "";
        this.falseAnswer2 = "";
        this.falseAnswer3 = "";
        this.category = "";
        this.reviewNum = 0;
        this.status = 0;
    }

    public QuizQuestion(String question, String correctAnswer, String falseAnswer1, String falseAnswer2, String falseAnswer3, String category) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.falseAnswer1 = falseAnswer1;
        this.falseAnswer2 = falseAnswer2;
        this.falseAnswer3 = falseAnswer3;
        this.category = category;
        this.reviewNum = 0;
        this.status = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getFalseAnswer1() {
        return falseAnswer1;
    }

    public void setFalseAnswer1(String falseAnswer1) {
        this.falseAnswer1 = falseAnswer1;
    }

    public String getFalseAnswer2() {
        return falseAnswer2;
    }

    public void setFalseAnswer2(String falseAnswer2) {
        this.falseAnswer2 = falseAnswer2;
    }

    public String getFalseAnswer3() {
        return falseAnswer3;
    }

    public void setFalseAnswer3(String falseAnswer3) {
        this.falseAnswer3 = falseAnswer3;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public String toString() {
        return getQuestion() + "\n" + "[" + getCorrectAnswer() +", " + getFalseAnswer1() +", " + getFalseAnswer2() +", " + getFalseAnswer3() + "] id="+getId();
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof QuizQuestion) {
            if(this.id == ((QuizQuestion) o).getId() && this.correctAnswer.equals(((QuizQuestion) o).getCorrectAnswer()))
                return true;
            else
                return false;
        }
        return super.equals(o);
    }
}
