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

import models.Request;
import models.Response;
import models.User;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import views.html.qa;

import static play.data.Form.form;

@Security.Authenticated(ActionAuthenticator.class)
public class QA extends AskWatson {

    public Result index() {
        return ok(qa.render(form(Request.class), User.findByUsername(request().username()), new Response.EmptyResponse(), true));
    }

    public Result getOk(Form<Request> request, User user, Response response) {
        if (response.isEmpty())
            return ok(qa.render(request, user, response, false));
        return ok(qa.render(request, user, response, true));
    }

    public Result getBadRequest(Form<Request> request, User user, Response response) {
        return badRequest(qa.render(request, user, response, false));
    }

    public int getNumberOfWatsonDocs(Form<Request> request) {
        return 5;
    }

    public int getNumberOfAnswers(Form<Request> request) {
        return 10;
    }
}
