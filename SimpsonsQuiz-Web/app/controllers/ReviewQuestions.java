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

package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.SQuizQuestion;
import models.SQuizQuestionList;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.review_questions;

@Security.Authenticated(ActionAuthenticator.class)
public class ReviewQuestions extends Controller {

    public Result index() {
        User user = User.findByUsername(request().username());
        SQuizQuestionList.loadQuestionsForReview(user.apiKey);
        return ok(review_questions.render(user));
    }

    public Result nextQuestion() {

        SQuizQuestion question = SQuizQuestionList.getNextReviewQuestion(User.findByUsername(request().username()).apiKey);

        if (question == null)
            return noContent();

        // Prepare Answers (Shuffling)
        List<String> quizAnswers = new ArrayList<String>();

        quizAnswers.add(question.correctAnswer);
        quizAnswers.add(question.falseAnswer1);
        quizAnswers.add(question.falseAnswer2);
        quizAnswers.add(question.falseAnswer3);

        Collections.shuffle(quizAnswers);

        // set Question ID
        int questionID = question.questionId;

        // Remember Correct Answer
        int correctAnswerNumber = quizAnswers.indexOf(question.correctAnswer);

        ObjectNode result = play.libs.Json.newObject();
        result.put("question", question.question);
        result.put("answer1", quizAnswers.get(0));
        result.put("answer2", quizAnswers.get(1));
        result.put("answer3", quizAnswers.get(2));
        result.put("answer4", quizAnswers.get(3));
        result.put("correctIndex", correctAnswerNumber + 1);
        result.put("id", questionID);

        return ok(result);

    }

    public Result submit(int id, String check0, String check1, String check2, int difficulty) {
        boolean res = SQuizQuestionList.submitReview(User.findByUsername(request().username()).apiKey, id, check0.equals("true"), check1.equals("true"), check2.equals("true"), difficulty);

        if(res == false)
            return internalServerError("Timeout");

        return nextQuestion();
    }

}
