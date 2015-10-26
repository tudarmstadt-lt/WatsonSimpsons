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


import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.persistence.Entity;
import javax.persistence.Id;

import backend.QuizBackend;
import backend.types.QuizUser;
import play.Logger;
import play.db.ebean.Model;
import util.AppException;

@Entity
public class User extends Model {
    @Id
    public Integer id;
    public String username;

    public String apiKey;

    public static Finder<Integer, User> find = new Finder<Integer, User>(Integer.class, User.class);

    private User(String username, String apiKey) {
        this.username = username;
        this.apiKey = apiKey;
    }

    public static User create(String username, String apiKey) throws AppException {
        User user = new User(username, apiKey);
        user.save();
        return user;
    }

    public static void createGuestUser() {
        try {
            Config conf = ConfigFactory.load();
            User.create("guest", conf.getString("quizbackend.apiKeyGuest"));
        } catch (AppException e) {
            Logger.error("Can't create guest user");
        }
    }

    /**
     * Checks whether a user exists in the remote database.
     *
     * @param username username to check
     * @return <code>true</code> if the user exists. <code>False</code> otherwise.
     */
    public static boolean userExists(String username, String clearPassword, QuizBackend backend) {
        if (username.equals("guest"))
            return true;
        else {
            QuizUser qUser = backend.loginUser(username, clearPassword, false);
            return qUser != null;
        }
    }

    /**
     * Retrieve a user from an username from the local database.
     *
     * @param username username to search
     * @return a user
     */
    public static User findByUsername(String username) {
        return find.where().eq("username", username).findUnique();
    }

    /**
     * Authenticate a User, from a username and clear password.
     *
     * @param username      username
     * @param clearPassword clear password
     * @return User if authenticated, null otherwise
     * @throws AppException App Exception
     */
    public static User authenticate(String username, String clearPassword, QuizBackend backend) throws AppException {
        User user = find.where().eq("username", username).findUnique();
        if (user != null) { // user found in local  DB
            if (!username.equals("guest")) {
                QuizUser qUser = backend.loginUser(username, clearPassword, false);
                if (qUser != null) {
                    if (user.apiKey.equals(qUser.getApiKey()))
                        return user;
                }
            } else {

                return user;
            }

        } else { // user not found in local DB, might still be in remote DB, update local DB
            QuizUser qUser = backend.loginUser(username, clearPassword, false);
            if (qUser != null)
                return User.create(username, qUser.getApiKey());
        }
        return null;
    }

}
