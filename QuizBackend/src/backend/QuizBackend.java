package backend;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import backend.types.BackendResponse;
import backend.types.ParameterPair;
import backend.types.QuizQuestion;
import backend.types.QuizQuestionReview;
import backend.types.QuizUser;

/**
 * QuizBackend
 * - Save, retrieve and delete quiz questions
 * - Save, retrieve and delete question user reviews
 * - Register and login users to web backend
 * - Read, increment and reset user score
 *
 * @author dath
 */
public class QuizBackend {

    public static String backendUrl;
    public static String appKey = "";
    public static String apiKey = "";

    @SuppressWarnings("static-access")
    public QuizBackend(String backendUrl, String appKey) {
        this.backendUrl = backendUrl;
        this.appKey = appKey;
    }

    @SuppressWarnings("static-access")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }


    /**
     * Retrieves all quiz questions from backend database
     *
     * @param apiKey apiKey of the user for which the request should be send
     * @return list of QuizQuestion objects, empty list if no questions found
     */
    public List<QuizQuestion> getQuestions(String apiKey) {
        BackendResponse response = BackendCommunicator.sendGetRequest(backendUrl.concat("questions"), appKey, apiKey);

        if (!response.isError())
            return response.getQuestions();
        else
            return new ArrayList<QuizQuestion>();
    }

    /**
     * Retrieves all quiz questions from backend database
     *
     * @return list of QuizQuestion objects, empty list if no questions found
     */
    public List<QuizQuestion> getQuestions() {
        return getQuestions(apiKey);
    }

    /**
     * Retrieves all quiz questions for given category unseen by current user (neither answered nor created/modified)
     *
     * @param apiKey           apiKey of the user for which the request should be send
     * @param filterUnapproved set to true, if only approved questions should be retrieved
     * @param difficulty       set the average difficulty level to filter questions (easy, medium, hard)
     * @param count            number of questions to load, if count==-1, all questions are returned
     * @param shuffle          true if returned list should be shuffled (random order)
     * @return list of QuizQuestion objects, empty list if no matching questions found
     */
    public List<QuizQuestion> getQuestions(String apiKey, boolean filterUnapproved, String difficulty, int count, boolean shuffle) {

        List<ParameterPair> params = new ArrayList<ParameterPair>();
        if (filterUnapproved)
            params.add(new ParameterPair("filter_unapproved", String.valueOf(filterUnapproved)));
        if (difficulty != null)
            params.add(new ParameterPair("difficulty", difficulty));

        BackendResponse response = BackendCommunicator.sendGetRequest(backendUrl.concat("questions"), appKey, apiKey, params);

        if (response.isError() || count == 0) {
            return new ArrayList<QuizQuestion>();
        }

        List<QuizQuestion> result = response.getQuestions();

        if (shuffle) {
            Collections.shuffle(result);
        }

        if (count == -1) {
            return result;
        }

        if (count > 0) {

            if (result.size() > count) {
                return result.subList(0, count);
            } else {
                return result;
            }
        }

        return new ArrayList<QuizQuestion>();

    }

    /**
     * Retrieves all quiz questions for given category unseen by current user (neither answered nor created/modified)
     *
     * @param filterUnapproved set to true, if only approved questions should be retrieved
     * @param difficulty       set the average difficulty level to filter questions (easy, medium, hard)
     * @param count            number of questions to load, if count==-1, all questions are returned
     * @param shuffle          true if returned list should be shuffled (random order)
     * @return list of QuizQuestion objects, empty list if no matching questions found
     */
    public List<QuizQuestion> getQuestions(boolean filterUnapproved, String difficulty, int count, boolean shuffle) {
        return getQuestions(apiKey, filterUnapproved, difficulty, count, shuffle);
    }

    /**
     * Retrieves all approved quiz questions
     *
     * @param apiKey apiKey of the user for which the request should be send
     * @return list of QuizQuestion objects, empty list if no matching questions found
     */
    public List<QuizQuestion> getApprovedQuestions(String apiKey) {
        return getQuestions(apiKey, true, null, -1, false);
    }

    /**
     * Retrieves all approved quiz questions
     *
     * @return list of QuizQuestion objects, empty list if no matching questions found
     */
    public List<QuizQuestion> getApprovedQuestions() {
        return getQuestions(true, null, -1, false);
    }

    /**
     * Retrieves all quiz questions unseen by current user (neither answered nor created/modified)
     *
     * @param apiKey           apiKey of the user for which the request should be send
     * @param category         category to filter user questions, set to null or empty for all categories
     * @param filterUnapproved set to true, if only approved questions should be retrieved
     * @return list of QuizQuestion objects, empty list if no matching questions found
     */
    public List<QuizQuestion> getQuestionsForUser(String apiKey, String category, boolean filterUnapproved) {

        List<ParameterPair> params = new ArrayList<ParameterPair>();
        if (category != null && !category.isEmpty())
            params.add(new ParameterPair("category", category));
        if (filterUnapproved)
            params.add(new ParameterPair("filter_unapproved", String.valueOf(filterUnapproved)));

        BackendResponse response = BackendCommunicator.sendGetRequest(backendUrl.concat("questions"), appKey, apiKey, params);

        if (!response.isError())
            return response.getQuestions();
        else
            return new ArrayList<QuizQuestion>();
    }

    /**
     * Retrieves all quiz questions unseen by current user (neither answered nor created/modified)
     *
     * @param category         category to filter user questions, set to null or empty for all categories
     * @param filterUnapproved set to true, if only approved questions should be retrieved
     * @return list of QuizQuestion objects, empty list if no matching questions found
     */
    public List<QuizQuestion> getQuestionsForUser(String category, boolean filterUnapproved) {
        return getQuestionsForUser(apiKey, category, filterUnapproved);
    }

    /**
     * Retrieves all quiz questions for given category unseen by current user (neither answered nor created/modified)
     *
     * @param apiKey   apiKey of the user for which the request should be send
     * @param category category to filter user questions, set to null or empty for all categories
     * @return list of QuizQuestion objects, empty list if no matching questions found
     */
    public List<QuizQuestion> getQuestionsForUser(String apiKey, String category) {
        return getQuestionsForUser(apiKey, category, false);
    }

    /**
     * Retrieves all quiz questions for given category unseen by current user (neither answered nor created/modified)
     *
     * @param category category to filter user questions, set to null or empty for all categories
     * @return list of QuizQuestion objects, empty list if no matching questions found
     */
    public List<QuizQuestion> getQuestionsForUser(String category) {
        return getQuestionsForUser(category, false);
    }

    /**
     * Retrieves all approved quiz questions unseen by current user (neither answered nor created/modified)
     *
     * @param apiKey apiKey of the user for which the request should be send
     * @return list of QuizQuestion objects, empty list if no matching questions found
     */
    public List<QuizQuestion> getApprovedQuestionsForUser(String apiKey) {
        return getQuestionsForUser(apiKey, null, true);
    }

    /**
     * Retrieves all approved quiz questions unseen by current user (neither answered nor created/modified)
     *
     * @return list of QuizQuestion objects, empty list if no matching questions found
     */
    public List<QuizQuestion> getApprovedQuestionsForUser() {
        return getQuestionsForUser(null, true);
    }

    /**
     * Retrieves all approved quiz questions for given category unseen by current user (neither answered nor created/modified)
     *
     * @param apiKey   apiKey of the user for which the request should be send
     * @param category category to filter user questions, set to null or empty for all categories
     * @return list of QuizQuestion objects, empty list if no matching questions found
     */
    public List<QuizQuestion> getApprovedQuestionsForUserAndCategory(String apiKey, String category) {
        return getQuestionsForUser(apiKey, category, true);
    }

    /**
     * Retrieves all approved quiz questions for given category unseen by current user (neither answered nor created/modified)
     *
     * @param category category to filter user questions, set to null or empty for all categories
     * @return list of QuizQuestion objects, empty list if no matching questions found
     */
    public List<QuizQuestion> getApprovedQuestionsForUserAndCategory(String category) {
        return getQuestionsForUser(category, true);
    }

    /**
     * Retrieves 9 questions of ascending difficulty for a quiz game
     *
     * @param apiKey apiKey of the user for which the request should be sent
     * @return list of QuizQuestions
     */
    public List<QuizQuestion> getQuestionsForGame(String apiKey) {
        List<QuizQuestion> questions = new ArrayList<QuizQuestion>();
        String[] difficulties = {"easy", "medium", "hard"};

        for (String difficulty : difficulties) {
            List<QuizQuestion> tempQuestionList = getQuestions(apiKey, true, difficulty, 3, true);
            if (tempQuestionList != null && tempQuestionList.size() > 0)
                questions.addAll(tempQuestionList);
        }

        if (questions.size() < 9) {
            System.err.println("Load additionals...");
            List<QuizQuestion> tempQuestionList = getApprovedQuestions(apiKey);
            Collections.shuffle(tempQuestionList);
            if (tempQuestionList != null && tempQuestionList.size() > 9 - questions.size()) {
                for(QuizQuestion q : tempQuestionList) {
                    if(!questions.contains(q)) {
                        questions.add(q);
                        if(questions.size() == 9)
                            break;
                    }
                }
                //questions.addAll(tempQuestionList.subList(0, 9 - questions.size()));
            } else {
                questions.addAll(tempQuestionList);
            }

        }

        return questions;
    }

    /**
     * Retrieves 9 questions of ascending difficulty for a quiz game
     *
     * @return list of QuizQuestions
     */
    public List<QuizQuestion> getQuestionsForGame() {
        return getQuestionsForGame(apiKey);
    }

    /**
     * Retrieves a question form backend database
     *
     * @param apiKey     apiKey of the user for which the request should be send
     * @param questionID id of the question
     * @return the requesting QuizQuestion object if existent, otherwise null
     */
    public QuizQuestion getQuestion(String apiKey, int questionID) {

        BackendResponse response = BackendCommunicator.sendGetRequest(backendUrl.concat("questions/").concat(Integer.toString(questionID)), appKey, apiKey);

        if (!response.isError())
            return response.getQuestion();
        else
            return null;
    }

    /**
     * Retrieves a question form backend database
     *
     * @param questionID id of the question
     * @return the requesting QuizQuestion object if existent, otherwise null
     */
    public QuizQuestion getQuestion(int questionID) {
        return getQuestion(apiKey, questionID);
    }

    /**
     * Adds a new question to backend database
     *
     * @param apiKey       apiKey of the user for which the request should be send
     * @param quizQuestion QuizQuestion object to be added
     * @return true if adding was successful
     */
    public boolean addQuestion(String apiKey, QuizQuestion quizQuestion) {
        List<ParameterPair> params = new ArrayList<ParameterPair>();
        params.add(new ParameterPair("question", quizQuestion.getQuestion()));
        params.add(new ParameterPair("correctAnswer", quizQuestion.getCorrectAnswer()));
        params.add(new ParameterPair("falseAnswer1", quizQuestion.getFalseAnswer1()));
        params.add(new ParameterPair("falseAnswer2", quizQuestion.getFalseAnswer2()));
        params.add(new ParameterPair("falseAnswer3", quizQuestion.getFalseAnswer3()));
        params.add(new ParameterPair("category", quizQuestion.getCategory()));

        BackendResponse response = BackendCommunicator.sendPostRequest(backendUrl.concat("questions"), appKey, apiKey, params);

        return !response.isError();
    }

    /**
     * Adds a new question to backend database
     *
     * @param quizQuestion QuizQuestion object to be added
     * @return true if adding was successful
     */
    public boolean addQuestion(QuizQuestion quizQuestion) {
        return addQuestion(apiKey, quizQuestion);
    }

    /**
     * Deletes a question from backend database
     * Note: Current user needs to have role admin!
     *
     * @param apiKey     apiKey of the user for which the request should be send
     * @param questionID id of the question
     * @return true if deletion was successful
     */
    public boolean deleteQuestion(String apiKey, int questionID) {
        BackendResponse response = BackendCommunicator.sendDeleteRequest(backendUrl.concat("questions/").concat(Integer.toString(questionID)), appKey, apiKey);

        return !response.isError();
    }

    /**
     * Deletes a question from backend database
     * Note: Current user needs to have role admin!
     *
     * @param questionID id of the question
     * @return true if deletion was successful
     */
    public boolean deleteQuestion(int questionID) {
        return deleteQuestion(apiKey, questionID);
    }


    /**
     * Updates/Edits a question
     * Note: Current user needs to have role editor or admin!
     *
     * @param apiKey       apiKey of the user for which the request should be send
     * @param questionID   id of the question
     * @param quizQuestion changed QuizQuestion object
     * @return true if update was successful
     */
    public boolean updateQuestion(String apiKey, int questionID, QuizQuestion quizQuestion) {
        List<ParameterPair> params = new ArrayList<ParameterPair>();
        params.add(new ParameterPair("question", quizQuestion.getQuestion()));
        params.add(new ParameterPair("correctAnswer", quizQuestion.getCorrectAnswer()));
        params.add(new ParameterPair("falseAnswer1", quizQuestion.getFalseAnswer1()));
        params.add(new ParameterPair("falseAnswer2", quizQuestion.getFalseAnswer2()));
        params.add(new ParameterPair("falseAnswer3", quizQuestion.getFalseAnswer3()));
        params.add(new ParameterPair("category", quizQuestion.getCategory()));

        BackendResponse response = BackendCommunicator.sendPostRequest(backendUrl.concat("questions/edit/").concat(Integer.toString(questionID)), appKey, apiKey, params);

        return !response.isError();
    }

    /**
     * Updates/Edits a question
     * Note: Current user needs to have role editor or admin!
     *
     * @param questionID   id of the question
     * @param quizQuestion changed QuizQuestion object
     * @return true if update was successful
     */
    public boolean updateQuestion(int questionID, QuizQuestion quizQuestion) {
        return updateQuestion(apiKey, questionID, quizQuestion);
    }

    /**
     * Approves a question (set status to 1)
     * Note: Current user needs to have role approver or admin
     *
     * @param apiKey     apiKey of the user for which the request should be send
     * @param questionID id of the question
     * @return true if approving was successful
     */
    public boolean approveQuestion(String apiKey, int questionID) {
        List<ParameterPair> params = new ArrayList<ParameterPair>();
        params.add(new ParameterPair("status", "1"));

        BackendResponse response = BackendCommunicator.sendPostRequest(backendUrl.concat("questions/approve/").concat(Integer.toString(questionID)), appKey, apiKey, params);

        return !response.isError();
    }

    /**
     * Approves a question (set status to 1)
     * Note: Current user needs to have role approver or admin
     *
     * @param questionID id of the question
     * @return true if approving was successful
     */
    public boolean approveQuestion(int questionID) {
        return approveQuestion(apiKey, questionID);
    }


    /**
     * Retrieves all quiz questions not reviewed by current user (least reviewe
     *
     * @param apiKey apiKey of the user for which the request should be send
     * @return list of QuizQuestion objects, empty list if no unreviewed quest
     */
    public List<QuizQuestion> getQuestionsForReview(String apiKey) {

        BackendResponse response = BackendCommunicator.sendGetRequest(backendUrl.concat("questions/review"), appKey, apiKey);

        if (!response.isError())
            return response.getQuestions();
        else
            return new ArrayList<QuizQuestion>();
    }

    /**
     * Retrieves all quiz questions not reviewed by current user (least reviewe
     *
     * @return list of QuizQuestion objects, empty list if no unreviewed quest
     */
    public List<QuizQuestion> getQuestionsForReview() {
        return getQuestionsForReview(apiKey);
    }

    /**
     * Sets the user review for a question
     *
     * @param apiKey              apiKey of the user for which the request should be send
     * @param questionID          id of the question
     * @param answerCorrect       true if green marked answer is the correct answer
     * @param answerDistractors   true if the distractors were wisely chosen
     * @param questionFormulation true if the question was well-formulated
     * @param questionDifficulty  difficulty value in range 1 to 9 (from very easy to very hard)
     * @return true if review was successfully saved
     */
    public boolean setQuestionReview(String apiKey, int questionID, boolean answerCorrect, boolean answerDistractors, boolean questionFormulation, int questionDifficulty) {
        List<ParameterPair> params = new ArrayList<ParameterPair>();
        params.add(new ParameterPair("answerCorrect", (answerCorrect ? Integer.toString(1) : Integer.toString(0))));
        params.add(new ParameterPair("answerDistractors", (answerDistractors ? Integer.toString(1) : Integer.toString(0))));
        params.add(new ParameterPair("questionFormulation", (questionFormulation ? Integer.toString(1) : Integer.toString(0))));
        params.add(new ParameterPair("questionDifficulty", Integer.toString(questionDifficulty)));

        BackendResponse response = BackendCommunicator.sendPostRequest(backendUrl.concat("questions/review/").concat(Integer.toString(questionID)), appKey, apiKey, params);

        return !response.isError();
    }

    /**
     * Sets the user review for a question
     *
     * @param questionID          id of the question
     * @param answerCorrect       true if green marked answer is the correct answer
     * @param answerDistractors   true if the distractors were wisely chosen
     * @param questionFormulation true if the question was well-formulated
     * @param questionDifficulty  difficulty value in range 1 to 9 (from very easy to very hard)
     * @return true if review was successfully saved
     */
    public boolean setQuestionReview(int questionID, boolean answerCorrect, boolean answerDistractors, boolean questionFormulation, int questionDifficulty) {
        return setQuestionReview(apiKey, questionID, answerCorrect, answerDistractors, questionFormulation, questionDifficulty);
    }

    /**
     * Retrieves the user review for a question
     *
     * @param apiKey     apiKey of the user for which the request should be send
     * @param questionID id of the question
     * @return question review
     */
    public QuizQuestionReview getQuestionReview(String apiKey, int questionID) {
        BackendResponse response = BackendCommunicator.sendGetRequest(backendUrl.concat("questions/review/").concat(Integer.toString(questionID)), appKey, apiKey);

        if (!response.isError())
            return response.getQuestionReview();
        else
            return null;

    }

    /**
     * Retrieves the user review for a question
     *
     * @param questionID id of the question
     * @return question review
     */
    public QuizQuestionReview getQuestionReview(int questionID) {
        return getQuestionReview(apiKey, questionID);

    }

    /**
     * Retrieves the average review for a question
     *
     * @param apiKey     apiKey of the user for which the request should be send
     * @param questionID id of the question
     * @return average review for the question, null if no ratings found
     */
    public QuizQuestionReview getQuestionAverageReview(String apiKey, int questionID) {
        BackendResponse response = BackendCommunicator.sendGetRequest(backendUrl.concat("questions/avg-review/").concat(Integer.toString(questionID)), appKey, apiKey);

        if (!response.isError())
            return response.getQuestionReview();
        else
            return null;
    }

    /**
     * Retrieves the average review for a question
     *
     * @param questionID id of the question
     * @return average review for the question, null if no ratings found
     */
    public QuizQuestionReview getQuestionAverageReview(int questionID) {
        return getQuestionAverageReview(apiKey, questionID);
    }

    /**
     * Deletes the user review for a question
     *
     * @param apiKey     apiKey of the user for which the request should be send
     * @param questionID id of the question
     * @return true if deletion of user review was successful
     */
    public boolean deleteQuestionReview(String apiKey, int questionID) {
        BackendResponse response = BackendCommunicator.sendDeleteRequest(backendUrl.concat("questions/review/").concat(Integer.toString(questionID)), appKey, apiKey);

        return !response.isError();

    }

    /**
     * Deletes the user review for a question
     *
     * @param questionID id of the question
     * @return true if deletion of user review was successful
     */
    public boolean deleteQuestionReview(int questionID) {
        return deleteQuestionReview(apiKey, questionID);

    }

    /**
     * Retrieves the status of the current user for a question
     *
     * @param apiKey     apiKey of the user for which the request should be send
     * @param questionID id of the question
     * @return status if existent, otherwise undefined
     */
    public String getUserQuestionStatus(String apiKey, int questionID) {
        BackendResponse response = BackendCommunicator.sendGetRequest(backendUrl.concat("questions/user-status/").concat(Integer.toString(questionID)), appKey, apiKey);

        if (!response.isError())
            return response.getQuestionUserStatus().getStatus();
        else
            return "undefined";

    }

    /**
     * Retrieves the status of the current user for a question
     *
     * @param questionID id of the question
     * @return status if existent, otherwise undefined
     */
    public String getUserQuestionStatus(int questionID) {
        return getUserQuestionStatus(apiKey, questionID);
    }

    /**
     * Register a user at backend for later communication
     *
     * @param username username for the new user
     * @param password password for the new user
     * @return true if registration was successful
     */
    public boolean registerUser(String username, String password) {
        List<ParameterPair> params = new ArrayList<ParameterPair>();
        params.add(new ParameterPair("username", username));
        params.add(new ParameterPair("password", password));

        BackendResponse response = BackendCommunicator.sendPostRequest(backendUrl.concat("register"), appKey, "", params);

        return !response.isError();

    }

    /**
     * Try to login a user with given credentials.
     * Need to set ApiKey of user to backend after login.
     *
     * @param username  username of the user used by registration
     * @param password  password of the user used by registration
     * @param setApiKey true, if apiKey should set automatically, otherwise it must be set manually before following calls
     * @return the user if login was successful, otherwise null
     */
    public QuizUser loginUser(String username, String password, boolean setApiKey) {
        List<ParameterPair> params = new ArrayList<ParameterPair>();
        params.add(new ParameterPair("username", username));
        params.add(new ParameterPair("password", password));

        BackendResponse response = BackendCommunicator.sendPostRequest(backendUrl.concat("login"), appKey, "", params);

        if (response != null && !response.isError()) {
            QuizUser user = response.getUser();
            if (setApiKey && user != null && !user.getApiKey().isEmpty()) {
                this.setApiKey(user.getApiKey());
                System.out.println("Api-Key was set for user " + username);
            }

            return user;

        } else {
            return null;
        }
    }

    /**
     * Updates user data/credentials at backend
     *
     * @param apiKey   apiKey of the user for which the request should be send
     * @param username username for the current user
     * @param password password for the current user
     * @return true if update was successful
     */
    public boolean updateUser(String apiKey, String username, String password) {
        List<ParameterPair> params = new ArrayList<ParameterPair>();
        params.add(new ParameterPair("username", username));
        params.add(new ParameterPair("password", password));

        BackendResponse response = BackendCommunicator.sendPostRequest(backendUrl.concat("user"), appKey, apiKey, params);

        return !response.isError();
    }

    /**
     * Updates user data/credentials at backend
     *
     * @param username username for the current user
     * @param password password for the current user
     * @return true if update was successful
     */
    public boolean updateUser(String username, String password) {
        return updateUser(apiKey, username, password);
    }

    /**
     * Deletes the current user from backend
     * Don't forget to logout the user in the application, since current apiKey will be invalid afterwards!
     *
     * @param apiKey apiKey of the user for which the request should be send
     * @return true if deletion of current user was successful
     */
    public boolean deleteUser(String apiKey) {
        BackendResponse response = BackendCommunicator.sendDeleteRequest(backendUrl.concat("user"), appKey, apiKey);

        return !response.isError();
    }

    /**
     * Deletes the current user from backend
     * Don't forget to logout the user in the application, since current apiKey will be invalid afterwards!
     *
     * @return true if deletion of current user was successful
     */
    public boolean deleteUser() {
        return deleteUser(apiKey);
    }

    /**
     * Retrieves the user with given user id
     * Note: Current user needs to have role admin!
     *
     * @param apiKey apiKey of the user for which the request should be send
     * @return QuizUser with given id, null if request failed (user not existent or current user not admin)
     */
    public QuizUser getUser(String apiKey, int userID) {
        BackendResponse response = BackendCommunicator.sendGetRequest(backendUrl.concat("user/").concat(Integer.toString(userID)), appKey, apiKey);

        if (response != null && !response.isError() && response.getUser() != null) {
            return response.getUser();
        }
        return null;
    }

    /**
     * Retrieves the user with given user id
     * Note: Current user needs to have role admin!
     *
     * @return QuizUser with given id, null if request failed (user not existent or current user not admin)
     */
    public QuizUser getUser(int userID) {
        return getUser(apiKey, userID);
    }

    /**
     * Retrieves the score for current user
     *
     * @param apiKey apiKey of the user for which the request should be send
     * @return score of current user, -1 if request failed
     */
    public int getUserScore(String apiKey) {
        BackendResponse response = BackendCommunicator.sendGetRequest(backendUrl.concat("user/score"), appKey, apiKey);

        if (response != null && !response.isError() && response.getUser() != null) {
            return response.getUser().getScore();
        }
        return -1;
    }

    /**
     * Retrieves the score for current user
     *
     * @return score of current user, -1 if request failed
     */
    public int getUserScore() {
        return getUserScore(apiKey);
    }

    /**
     * Increments the score of the current user by given value
     *
     * @param apiKey apiKey of the user for which the request should be send
     * @param points value of increment
     * @return true if incrementation was successful
     */
    public boolean incrementUserScore(String apiKey, int points) {
        List<ParameterPair> params = new ArrayList<ParameterPair>();
        params.add(new ParameterPair("points", Integer.toString(points)));

        BackendResponse response = BackendCommunicator.sendPostRequest(backendUrl.concat("user/score"), appKey, apiKey, params);

        return !response.isError();
    }

    /**
     * Increments the score of the current user by given value
     *
     * @param points value of increment
     * @return true if incrementation was successful
     */
    public boolean incrementUserScore(int points) {
        return incrementUserScore(apiKey, points);
    }

    /**
     * Resets the score of the current user to zero
     *
     * @param apiKey apiKey of the user for which the request should be send
     * @return true if reset was successful
     */
    public boolean resetUserScore(String apiKey) {
        BackendResponse response = BackendCommunicator.sendDeleteRequest(backendUrl.concat("user/score"), appKey, apiKey);

        return !response.isError();
    }

    /**
     * Resets the score of the current user to zero
     *
     * @return true if reset was successful
     */
    public boolean resetUserScore() {
        return resetUserScore(apiKey);
    }

    /**
     * Retrieves the highscores list with users and their scores (decrementing score order)
     *
     * @param apiKey apiKey of the user for which the request should be sent
     * @return list of QuizUsers
     */
    public List<QuizUser> getUserHighscores(String apiKey) {
        BackendResponse response = BackendCommunicator.sendGetRequest(backendUrl.concat("highscores"), appKey, apiKey);

        if (response != null && !response.isError() && response.getHighscores() != null) {
            return response.getHighscores();
        }

        return new ArrayList<>();
    }

    /**
     * Retrieves the highscores list with users and their scores (decrementing score order)
     *
     * @return list of QuizUsers
     */
    public List<QuizUser> getUserHighscores() {
        return getUserHighscores(apiKey);
    }

    /**
     * Retrieves the score of the current user and its rank in the highscore
     *
     * @param apiKey apiKey of the user for which the request should be send
     * @return {QuizUser, rank}
     */
    public Map.Entry<QuizUser, Integer> getUserRanking(String apiKey) {
        QuizUser user = new QuizUser();
        int rank = -1;

        BackendResponse response = BackendCommunicator.sendGetRequest(backendUrl.concat("user/score"), appKey, apiKey);

        if (response != null && !response.isError() && response.getUser() != null) {
            user = response.getUser();
            response = BackendCommunicator.sendGetRequest(backendUrl.concat("highscores"), appKey, apiKey);

            if (response != null && !response.isError() && response.getHighscores() != null) {
                rank = response.getHighscores().indexOf(user) + 1;
            }
        }

        return new AbstractMap.SimpleEntry<QuizUser, Integer>(user, rank);
    }

    /**
     * Retrieves the score of the current user and its rank in the highscore
     *
     * @return {QuizUser, rank}
     */
    public Map.Entry<QuizUser, Integer> getUserRanking() {
        return getUserRanking(apiKey);
    }

}
