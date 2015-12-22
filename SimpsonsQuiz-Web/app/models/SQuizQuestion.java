package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import backend.types.QuizQuestion;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Created by Admin on 13.10.2015.
 */
@Entity
public class SQuizQuestion extends Model {

    @Id
    public String id;

    @Constraints.Required
    public String correctAnswer;
    @Constraints.Required
    public String falseAnswer1;
    @Constraints.Required
    public String falseAnswer2;
    @Constraints.Required
    public String falseAnswer3;
    @Constraints.Required
    public int questionId;
    @Constraints.Required
    public String question;

    public SQuizQuestion(QuizQuestion qq, String listId) {
        correctAnswer = qq.getCorrectAnswer();
        falseAnswer1 = qq.getFalseAnswer1();
        falseAnswer2 = qq.getFalseAnswer2();
        falseAnswer3 = qq.getFalseAnswer3();
        questionId = qq.getId();
        question = qq.getQuestion();
        id= listId+"_"+ questionId;
    }

    public static SQuizQuestion createFromQuizQuestion(QuizQuestion question, String listId) {
        SQuizQuestion newSQQ = new SQuizQuestion(question, listId);
        return newSQQ;
    }
}
