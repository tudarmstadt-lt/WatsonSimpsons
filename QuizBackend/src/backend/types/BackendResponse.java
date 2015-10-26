package backend.types;

import com.google.gson.Gson;

import java.util.List;

/**
 * Response from Backend
 * for use with Gson
 *
 * @author dath
 */
public class BackendResponse {

    boolean error;
    String message;
    int responseCode;

    QuizQuestion question;
    List<QuizQuestion> questions;
    QuizQuestionReview questionReview;
    int questionsCount;
    boolean questionsFiltered;
    String questionsCategory;
    String questionsDifficulty;
    QuizQuestionUserStatus questionUserStatus;
    QuizUser user;
    QuizReward quizReward;
    int highscoresCount;
    List<QuizUser> highscores;

    public BackendResponse() {
    }

    public BackendResponse(boolean error, String message, int responseCode) {
        this.error = error;
        this.message = message;
        this.responseCode = responseCode;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public QuizQuestion getQuestion() {
        return question;
    }

    public void setQuestion(QuizQuestion question) {
        this.question = question;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestion> questions) {
        this.questions = questions;
    }

    public int getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(int questionsCount) {
        this.questionsCount = questionsCount;
    }

    public boolean isQuestionsFiltered() {
        return questionsFiltered;
    }

    public void setQuestionsFiltered(boolean filtered) {
        this.questionsFiltered = filtered;
    }

    public String getQuestionsDifficulty() {
        return questionsDifficulty;
    }

    public void setQuestionsDifficulty(String questionsDifficulty) {
        this.questionsDifficulty = questionsDifficulty;
    }

    public String getQuestionsCategory() {
        return questionsCategory;
    }

    public void setQuestionsCategory(String questionsCategory) {
        this.questionsCategory = questionsCategory;
    }

    public QuizQuestionReview getQuestionReview() {
        return questionReview;
    }

    public void setQuestionReview(QuizQuestionReview questionReview) {
        this.questionReview = questionReview;
    }

    public QuizReward getQuizReward() {
        return quizReward;
    }

    public void setQuizReward(QuizReward quizReward) {
        this.quizReward = quizReward;
    }

    public QuizQuestionUserStatus getQuestionUserStatus() {
        return questionUserStatus;
    }

    public void setQuestionUserStatus(QuizQuestionUserStatus questionUserStatus) {
        this.questionUserStatus = questionUserStatus;
    }

    public QuizUser getUser() {
        return user;
    }

    public void setUser(QuizUser user) {
        this.user = user;
    }

    public int getHighscoresCount() {
        return highscoresCount;
    }

    public void setHighscoresCount(int highscoresCount) {
        this.highscoresCount = highscoresCount;
    }

    public List<QuizUser> getHighscores() {
        return highscores;
    }

    public void setHighscores(List<QuizUser> highscores) {
        this.highscores = highscores;
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
