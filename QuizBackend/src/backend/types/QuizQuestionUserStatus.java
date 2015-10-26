package backend.types;

import com.google.gson.Gson;

/**
 * QuizQuestionUserStatus
 *
 * @author dath
 */
public class QuizQuestionUserStatus {

    int questionID;
    String status;
    String timestamp;

    public QuizQuestionUserStatus(int questionID, String status) {
        this.questionID = questionID;
        this.status = status;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Status for Question " + questionID + ": "+ status + "(Timestamp: "+timestamp +")";
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
