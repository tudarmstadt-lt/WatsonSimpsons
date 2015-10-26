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


import backend.QuizBackend;
import controllers.QuizBackendService;
import play.data.validation.Constraints;
import util.ValidateUtil;

public class Register {

    @Constraints.Required
    public String username;

    @Constraints.Required
    public String inputPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getInputPassword() {
        return inputPassword;
    }

    public void setInputPassword(String pwd) {
        this.inputPassword = pwd;
    }

    /**
     * Validate the authentication. Called by the framework.
     * See: https://www.playframework.com/documentation/2.0/JavaForms
     *
     * @return null if validation ok, string with details otherwise
     */
    @SuppressWarnings("unused")
    public String validate() {

        if (ValidateUtil.isBlank(username)) {
            return "Username is required";
        }

        if (ValidateUtil.isBlank(inputPassword)) {
            return "Password is required";
        }

        QuizBackend quizBackend = QuizBackendService.getInstance();

        if (User.userExists(username, inputPassword, quizBackend)) {
            return "User already exists";
        }

        return null;
    }
}
