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

public class QuestionRequest {

    public String question;
    public String correctAnswer;
    public String falseAnswer1;
    public String falseAnswer2;
    public String falseAnswer3;
    // Category can be set per Question if wanted, currently not used
    public String category = "WEB";

    public QuestionRequest() {
    }

    public QuestionRequest(String question, String correctAnswer, String falseAnswer1, String falseAnswer2, String falseAnswer3, String category) {
        this.correctAnswer = correctAnswer;
        this.question = question;
        this.falseAnswer1 = falseAnswer1;
        this.falseAnswer2 = falseAnswer2;
        this.falseAnswer3 = falseAnswer3;
        this.category = category;
    }

    public QuestionRequest(String question, String correctAnswer, String falseAnswer1, String falseAnswer2, String falseAnswer3) {
        this.correctAnswer = correctAnswer;
        this.question = question;
        this.falseAnswer1 = falseAnswer1;
        this.falseAnswer2 = falseAnswer2;
        this.falseAnswer3 = falseAnswer3;
    }

    public static List<String> categories() {
        List<String> choices = new ArrayList();

        choices.add("Bart");
        choices.add("Lisa");
        choices.add("Homer");
        choices.add("Maggie");
        choices.add("Marge");
        choices.add("Springfield");
        choices.add("MISC");

        return choices;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getFalseAnswer1() {
        return falseAnswer1;
    }

    public void setFalseAnswer1(String falseAnswer1) {
        this.falseAnswer1 = falseAnswer1;
    }

    public String getFalseAnswer2() {
        return falseAnswer1;
    }

    public void setFalseAnswer2(String falseAnswer2) {
        this.falseAnswer2 = falseAnswer2;
    }

    public String getFalseAnswer3() {
        return falseAnswer3;
    }

    public void setFalseAnswer3(String falseAnswer3) {
        this.falseAnswer3 = falseAnswer3;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String validate() {
        if (ValidateUtil.isBlank(question)) {
            return Messages.get("error.noquestion");
        }
        if (ValidateUtil.isBlank(correctAnswer)) {
            return Messages.get("error.noquestion");
        }
        if (ValidateUtil.isBlank(falseAnswer1) || ValidateUtil.isBlank(falseAnswer2) || ValidateUtil.isBlank(falseAnswer3)) {
            return Messages.get("error.noquestion");
        }

        return null;
    }


}
