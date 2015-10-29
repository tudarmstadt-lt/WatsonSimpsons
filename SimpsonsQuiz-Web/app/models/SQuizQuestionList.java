/*
 * Copyright 2015 Technische Universitaet Darmstadt
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *         - Uli Fahrer
 */

package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import backend.QuizBackend;
import backend.types.QuizQuestion;
import controllers.QuizBackendService;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class SQuizQuestionList extends Model {
    @Id
    public String id;

    @Constraints.Required
    @OneToMany(cascade = CascadeType.ALL)
    public List<SQuizQuestion> quizQuestionList;

    @Constraints.Required
    public int questionIterator;

    public static Finder<String, SQuizQuestionList> find = new Finder<String, SQuizQuestionList>(String.class, SQuizQuestionList.class);


    public SQuizQuestionList(String id) {
        this.id = id;
        questionIterator = 0;
        quizQuestionList = new ArrayList<>();
    }

    public static SQuizQuestionList loadQuizQuestions(String apiKey) {
        QuizBackend quizBackend = QuizBackendService.getInstance();
        return loadQuestions(apiKey, "_quiz", quizBackend.getQuestionsForGame(apiKey));
    }

    public static SQuizQuestionList loadQuestionsForReview(String apiKey) {
        QuizBackend quizBackend = QuizBackendService.getInstance();
        return loadQuestions(apiKey, "_review", quizBackend.getQuestionsForReview(apiKey));
    }

    public static SQuizQuestionList loadQuestions(String apiKey, String tag, List<QuizQuestion> quizQuestions) {
        SQuizQuestionList sQQL = find.where().eq("id", apiKey + tag).findUnique();
        if (sQQL != null) sQQL.delete();
        sQQL = new SQuizQuestionList(apiKey + tag);

        if (quizQuestions != null) {
            for (QuizQuestion question : quizQuestions) {
                SQuizQuestion newSQQ = SQuizQuestion.createFromQuizQuestion(question);
                sQQL.quizQuestionList.add(newSQQ);
            }

            sQQL.save();

            return sQQL;
        }
        return null;
    }

    public static SQuizQuestion getNextQuizQuestion(String apiKey) {
        return getNextQuestion(apiKey, "_quiz");
    }

    public static SQuizQuestion getNextReviewQuestion(String apiKey) {
        return getNextQuestion(apiKey, "_review");
    }

    public static SQuizQuestion getNextQuestion(String apiKey, String tag) {
        SQuizQuestionList sQQ = find.where().eq("id", apiKey + tag).findUnique();
        if (sQQ.quizQuestionList.size() > sQQ.questionIterator) {
            SQuizQuestion nextQuestion = sQQ.quizQuestionList.get(sQQ.questionIterator);
            sQQ.questionIterator++;
            sQQ.save();
            return nextQuestion;
        } else {
            sQQ.delete();
            return null;
        }
    }

    public static QuizQuestion findById(int id) {

        QuizBackend quizBackend = QuizBackendService.getInstance();
        QuizQuestion q = quizBackend.getQuestion(id);

        if (q == null)
            return new QuizQuestion();

        return q;
    }

    public static boolean submitReview(String apiKey, int questionID, boolean check0, boolean check1, boolean check2, int difficulty) {
        QuizBackend quizBackend = QuizBackendService.getInstance();
        return quizBackend.setQuestionReview(apiKey, questionID, check0, check1, check2, difficulty);
    }

}
