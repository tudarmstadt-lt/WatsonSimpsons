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

import com.google.inject.Inject;

import jwatson.answer.WatsonAnswer;
import jwatson.question.WatsonQuestion;
import models.Request;
import models.Response;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import static play.data.Form.form;

@Security.Authenticated(ActionAuthenticator.class)
public abstract class AskWatson extends Controller {

    @Inject
    private IWatsonService watsonService;


    public abstract Result getOk(Form<Request> request, User user, Response response);

    public abstract Result getBadRequest(Form<Request> request, User user, Response response);

    public abstract int getNumberOfWatsonDocs(Form<Request> request);

    public abstract int getNumberOfAnswers(Form<Request> request);


    public Result askByForm(boolean isQuizResponse) {
        Form<Request> requestForm = form(Request.class).bindFromRequest();
        return ask(requestForm, isQuizResponse);
    }

    private Result ask(Form<Request> request, boolean isQuizResponse) {
        User user = User.findByUsername(ctx().session().get("username"));

        if (request.hasErrors()) {
            return getBadRequest(request, user, new Response.EmptyResponse());
        }

        WatsonQuestion question = new WatsonQuestion.QuestionBuilder(request.get().question)
                .setNumberOfAnswers(getNumberOfWatsonDocs(request))
                .create();
        WatsonAnswer answer;

        try {
            answer = watsonService.getInstance().askQuestion(question);
        } catch (Exception e) {
            request.reject("Error while asking Watson!");
            return getOk(request, user, new Response.EmptyResponse(question));
        }

        Response response = new Response(answer, question, getNumberOfAnswers(request), isQuizResponse);

        if (hasAnswer(response)) {
            return getOk(request, user, response);
        } else {
            request.reject("No answers found");
            return getOk(request, user, new Response.EmptyResponse(question));
        }
    }

    public boolean hasAnswer(Response response) {
        final String banned = "redp4955"; // IBM document
        response.getFilteredEvidences().removeIf(e -> e.getTitle().startsWith(banned));

        return !response.getFilteredEvidences().isEmpty();
    }
}
