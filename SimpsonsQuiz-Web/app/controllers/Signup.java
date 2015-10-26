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
import backend.types.QuizUser;
import models.Login;
import models.Register;
import models.User;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import util.AppException;
import views.html.index;
import static play.data.Form.form;

public class Signup extends Controller {

    /**
     * Save the new user.
     *
     * @return Successful page or created form if bad
     */
    public Result save() {
        Form<Register> registerForm = form(models.Register.class).bindFromRequest();
        Form<Login> loginForm = form(Login.class);


        if (registerForm.hasErrors()) {
            return badRequest(index.render(loginForm, registerForm));
        }

        Register register = registerForm.get();

        try {
            User user = registerUser(register);
            
            if(user == null){
                registerForm.reject(Messages.get("qa.noConnection"));
                return badRequest(index.render(loginForm, registerForm));
            }
            else{
                ctx().session().put("username", user.username);
                return Application.GO_HOME;
            }
                

        } catch (Exception e) {
            Logger.error("Signup.save error", e);
            flash("error", Messages.get("error.technical"));
        }
        return badRequest(index.render(loginForm, registerForm));
    }

    /**
     * Save user in local as well as remote database,
     * store apikey for remote one
     *
     * @throws AppException
     */
    private User registerUser(Register register) throws AppException {
        String password = register.inputPassword;
        String username = register.username;
        QuizBackend quizBackend = QuizBackendService.getInstance();
        User user = null;
        if (quizBackend.registerUser(username, password)) {
            QuizUser qUser = quizBackend.loginUser(username, password, true);
            user = User.create(username, qUser.getApiKey());
        }
        return user;
    }
}
