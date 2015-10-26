/*
 *  Copyright 2015 Technische Universität Darmstadt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Contributors:
 *      - Uli Fahrer
 *
 */

package jwatson.question;

import com.google.gson.Gson;

public class WatsonQuestion {

    private QuestionInformation question;

    private WatsonQuestion(QuestionBuilder builder) {
        this.question = new QuestionInformation(builder.questionText,
                                                builder.items,
                                                builder.category,
                                                builder.context,
                                                builder.formattedAnswer);
    }

    public QuestionInformation getQuestion() {
        return question;
    }

    public String toJsonString() {
        return new Gson().toJson(this).toString();
    }

    @Override
    public String toString() {
        return "WatsonQuestion{" +
                "question=" + question +
                '}';
    }

    public static class QuestionBuilder {
        private final String questionText;
        private int items;
        private boolean formattedAnswer;
        private String category;
        private String context;

        public QuestionBuilder(String questionText) {
            this.questionText = questionText;
            this.items = 5;
        }

        public QuestionBuilder setNumberOfAnswers(int num) {
            this.items = num;
            return this;
        }

        public QuestionBuilder formatAnswer() {
            this.formattedAnswer = true;
            return this;
        }

        public QuestionBuilder setCategory(String category) {
            this.category = category;
            return this;
        }

        public QuestionBuilder setContext(String context) {
            this.context = context;
            return this;
        }

        public WatsonQuestion create() {
            return new WatsonQuestion(this);
        }
    }
}
