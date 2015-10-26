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
 *         - Uli Faher
 */

package models;

import java.util.ArrayList;
import java.util.List;

import play.i18n.Messages;
import util.ValidateUtil;

public class Request {

    public String question;

    public int numberOfAnswers;

    public int numberOfWatsonDocuments;

    public Request() {
    }

    public Request(String question, int numberOfAnswers) {
        this.question = question;
        this.numberOfAnswers = numberOfAnswers;
        this.numberOfWatsonDocuments = numberOfAnswers;
    }

    public static List<String> numberOfWatsonDocuments() {
        List<String> choices = new ArrayList();
        for (int i = 5; i >= 1; i--) {
            choices.add("" + i);
        }
        return choices;
    }

    public static List<String> numbersOfAnswers() {
        List<String> choices = new ArrayList();
        for (int i = 15; i >= 5; i--) {
            choices.add("" + i);
        }
        return choices;
    }

    public String validate() {
        if (ValidateUtil.isBlank(question)) {
            return Messages.get("error.noquestion");
        }

        return null;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(int num) {
        this.numberOfAnswers = num;
    }

    public int getNumberOfWatsonDocuments() {
        return numberOfWatsonDocuments;
    }

    public void setNumberOfWatsonDocuments(int num) {
        this.numberOfWatsonDocuments = num;
    }
}
