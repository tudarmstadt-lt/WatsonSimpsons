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

import backend.QuizBackend;
import backend.types.QuizQuestion;
import models.QuestionRequest;
import models.Request;
import models.Response;
import models.User;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import views.html.create_question;

import static play.data.Form.form;

@Security.Authenticated(ActionAuthenticator.class)
public class CreateQuestion extends AskWatson {


    public Result index() {
        return ok(create_question.render(form(Request.class), User.findByUsername(request().username()), new Response.EmptyResponse(), false));
    }

    public Result getOk(Form<Request> request, User user, Response response) {
        return ok(create_question.render(request, user, response, false));
    }

    public Result getBadRequest(Form<Request> request, User user, Response response) {
        return badRequest(create_question.render(request, user, response, false));
    }

    public int getNumberOfWatsonDocs(Form<Request> request) {
        return request.get().numberOfWatsonDocuments;
    }

    public int getNumberOfAnswers(Form<Request> request) {
        return request.get().numberOfAnswers;
    }

    public Result saveQuestion() {
        Form<QuestionRequest> request = form(QuestionRequest.class).bindFromRequest();
        User user = User.findByUsername(ctx().session().get("username"));

        if (request.hasErrors()) {
            return badRequest(create_question.render(form(Request.class), user, new Response.EmptyResponse(), false));
        } else {
            QuizBackend quizBackend = QuizBackendService.getInstance();

            QuizQuestion quizQuestion = new QuizQuestion(request.get().question, request.get().correctAnswer, request.get().falseAnswer1, request.get().falseAnswer2, request.get().falseAnswer3, request.get().category);
            if (quizBackend.addQuestion(user.apiKey, quizQuestion))
                return ok(create_question.render(form(Request.class), user, new Response.EmptyResponse(), true));
            else
                return badRequest(create_question.render(form(Request.class), user, new Response.EmptyResponse(), false));
        }
    }
}
