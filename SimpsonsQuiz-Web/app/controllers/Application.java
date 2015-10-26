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

import models.Login;
import models.Register;
import models.User;
import play.Logger;
import play.Routes;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.about;
import views.html.home;
import views.html.index;

import static play.data.Form.form;

public class Application extends Controller {

    public static Result GO_INDEX = redirect(
            controllers.routes.Application.index()
    );

    public static Result GO_HOME = redirect(
            controllers.routes.Application.home()
    );

    public Result index() {

        String username = ctx().session().get("username");

        if (username != null) {
            User user = User.findByUsername(username);
            if (user != null) {
                return GO_HOME;
            } else {
                Logger.debug("Clearing invalid session credentials");
                session().clear();
            }
        }

        return ok(index.render(form(Login.class), form(Register.class)));
    }

    public Result home() {

        String username = ctx().session().get("username");

        if (username != null) {
            User user = User.findByUsername(username);
            if (user != null) {
                return ok(home.render(user));
            } else {
                return GO_INDEX;
            }
        }

        return GO_INDEX;
    }

    public Result about() {

        String username = ctx().session().get("username");

        if (username != null) {
            User user = User.findByUsername(username);
            if (user != null) {
                return ok(about.render(user));
            } else {
                return GO_INDEX;
            }
        }

        return GO_INDEX;


    }

    /**
     * Handle login form submission.
     *
     * @return Dashboard if auth OK or login form if auth KO
     */
    public Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        Form<Register> registerForm = form(Register.class);

        if (loginForm.hasErrors()) {
            return badRequest(index.render(loginForm, registerForm));
        } else {
            Logger.debug(loginForm.get().toString());
            session("username", loginForm.get().username);
            return GO_HOME;
        }
    }

    public Result loginAsGuest() {
        Logger.info("loginAsGuest");
        User user = User.findByUsername("guest");
        if (user == null) {
            User.createGuestUser();
        }
        session("username", "guest");
        return GO_HOME;
    }

    /**
     * Logout and clean the session.
     *
     * @return Index page
     */
    public Result logout() {
        session().clear();
        flash("success", Messages.get("youve.been.logged.out"));
        return GO_INDEX;
    }


    /**
     * JavaScript-Routes
     *
     * @return
     */
    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        routes.javascript.Quiz.nextQuestion(),
                        routes.javascript.ReviewQuestions.submit(),
                        routes.javascript.ReviewQuestions.nextQuestion()
                )
        );
    }
}
