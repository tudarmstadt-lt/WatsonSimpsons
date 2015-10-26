package de.tudarmstadt.lt.simpsonsQuiz;

import android.os.Parcel;
import android.os.Parcelable;

import backend.types.QuizQuestion;


public class AndroidQuizQuestion implements Parcelable {

    private QuizQuestion quizQuestion;

    public AndroidQuizQuestion(QuizQuestion question) {
        quizQuestion = question;
    }

    public int getId() {
        return quizQuestion.getId();
    }

    public void setId(int id) {
        quizQuestion.setId(id);
    }

    public String getQuestion() {
        return quizQuestion.getQuestion();
    }

    public void setQuestion(String question) {
        quizQuestion.setQuestion(question);
    }

    public String getCorrectAnswer() {
        return quizQuestion.getCorrectAnswer();
    }

    public void setCorrectAnswer(String correct) {
        quizQuestion.setCorrectAnswer(correct);
    }

    public String getFalseAnswer1() {
        return quizQuestion.getFalseAnswer1();
    }

    public void setFalseAnswer1(String falseAnswer) {
        quizQuestion.setFalseAnswer1(falseAnswer);
    }

    public String getFalseAnswer2() {
        return quizQuestion.getFalseAnswer2();
    }

    public void setFalseAnswer2(String falseAnswer) {
        quizQuestion.setFalseAnswer2(falseAnswer);
    }

    public String getFalseAnswer3() {
        return quizQuestion.getFalseAnswer3();
    }

    public void setFalseAnswer3(String falseAnswer) {
        quizQuestion.setFalseAnswer3(falseAnswer);
    }

    public String getCategory() {
        return quizQuestion.getCategory();
    }

    public void setCategory(String cat) {
        quizQuestion.setCategory(cat);
    }

    public int getStatus() {
        return quizQuestion.getStatus();
    }

    public void setStatus(int status) {
        quizQuestion.setStatus(status);
    }

    public String getCreatedAt() {
        return quizQuestion.getCreatedAt();
    }

    public void setCreatedAt(String created) {
        quizQuestion.setCreatedAt(created);
    }

    public String getModifiedAt() {
        return quizQuestion.getModifiedAt();
    }

    public void setModifiedAt(String modified) {
        quizQuestion.setModifiedAt(modified);
    }


    @Override
    public String toString() {
        return getQuestion() + "\n" + "[" + getCorrectAnswer() + ", " + getFalseAnswer1() + ", " + getFalseAnswer2() + ", " + getFalseAnswer3() + "] id=" + getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getQuestion());
        dest.writeString(getCorrectAnswer());
        dest.writeString(getFalseAnswer1());
        dest.writeString(getFalseAnswer2());
        dest.writeString(getFalseAnswer3());
        dest.writeString(getCategory());
        dest.writeInt(getStatus());
    }

    private AndroidQuizQuestion(Parcel in) {
        quizQuestion.setId(in.readInt());
        quizQuestion.setQuestion(in.readString());
        quizQuestion.setCorrectAnswer(in.readString());
        quizQuestion.setFalseAnswer1(in.readString());
        quizQuestion.setFalseAnswer2(in.readString());
        quizQuestion.setFalseAnswer3(in.readString());
        quizQuestion.setCategory(in.readString());
        quizQuestion.setStatus(in.readInt());
    }

    public static final Parcelable.Creator<AndroidQuizQuestion> CREATOR = new Parcelable.Creator<AndroidQuizQuestion>() {
        public AndroidQuizQuestion createFromParcel(Parcel in) {
            return new AndroidQuizQuestion(in);
        }

        public AndroidQuizQuestion[] newArray(int size) {
            return new AndroidQuizQuestion[size];
        }
    };


}
