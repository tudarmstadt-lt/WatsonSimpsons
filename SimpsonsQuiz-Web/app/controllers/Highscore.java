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

import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.highscore;

@Security.Authenticated(ActionAuthenticator.class)
public class Highscore extends Controller {

    public Result index() {
        User currentUser = User.findByUsername(request().username());
        models.Highscore highscoreModel = new models.Highscore();
        highscoreModel.loadHighscore(currentUser.apiKey);
        int rank = highscoreModel.getUserRank(currentUser.apiKey);
        return ok(highscore.render(currentUser, highscoreModel, rank));
    }


}
