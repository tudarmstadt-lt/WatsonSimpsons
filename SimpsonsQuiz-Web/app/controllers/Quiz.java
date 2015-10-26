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
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.quiz;

@Security.Authenticated(ActionAuthenticator.class)
public class Quiz extends Controller {

    public Result index() {
        User user = User.findByUsername(request().username());
        SQuizQuestionList.loadQuizQuestions(user.apiKey);
        return ok(quiz.render(user));
    }

    public Result nextQuestion() {

        SQuizQuestion nextQuestion = SQuizQuestionList.getNextQuizQuestion(User.findByUsername(request().username()).apiKey);

        if (nextQuestion == null)
            return noContent();

        // Prepare Answers (Shuffling)
        List<String> quizAnswers = new ArrayList<String>();

        quizAnswers.add(nextQuestion.correctAnswer);
        quizAnswers.add(nextQuestion.falseAnswer1);
        quizAnswers.add(nextQuestion.falseAnswer2);
        quizAnswers.add(nextQuestion.falseAnswer3);

        Collections.shuffle(quizAnswers);

        // set Question ID
        int questionID = nextQuestion.questionId;

        // Remember Correct Answer
        int correctAnswerNumber = quizAnswers.indexOf(nextQuestion.correctAnswer);

        ObjectNode result = Json.newObject();
        result.put("question", nextQuestion.question);
        result.put("answer1", quizAnswers.get(0));
        result.put("answer2", quizAnswers.get(1));
        result.put("answer3", quizAnswers.get(2));
        result.put("answer4", quizAnswers.get(3));
        result.put("correctIndex", correctAnswerNumber + 1);

        return ok(result);

    }


}
