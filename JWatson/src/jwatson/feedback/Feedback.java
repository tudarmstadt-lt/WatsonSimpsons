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

package jwatson.feedback;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jwatson.answer.Answer;
import jwatson.question.QuestionInformation;

public class Feedback {
    /**
     * The question ID, obtained from the QA API result.
     */
    private String questionId;

    /**
     * The question text obtained from the QA API result.
     */
    private String questionText;

    /**
     * The answer ID to provide feedback on. The Answer ID is obtained from the QA API result.
     */
    private String answerId;

    /**
     * The answer text to provide feedback on. The answer text is obtained from the QA API result.
     */
    private String answerText;

    /**
     * (optional)
     * The name of the user submitting the feedback.
     */
    private String userName;

    /**
     * (optional)
     * The Watson mode used to obtain the QA API result.
     */
    private WatsonMode mode;

    /**
     * (optional)
     * The confidence of the answer obtained from the QA API result.
     */
    private String confidence;

    /**
     * (optional)
     * If the answer was shown to the user.
     */
    private boolean shown;

    /**
     * (optional)
     * If the evidence was viewed by the user.
     */
    private boolean evidenceViewed;


    /**
     * (optional)
     * Representation of the feedback.
     */
    private FeedbackRating feedback;


    /**
     * (optional)
     * User comment.
     */
    private String comment;


    private Feedback(FeedbackBuilder builder) {
        this.questionId = builder.questionId;
        this.questionText = builder.questionText;
        this.answerId = builder.answerId;
        this.answerText = builder.answerText;
        this.feedback = builder.feedback;
        this.confidence = builder.confidence;
        this.comment = builder.comment;
    }

    public JsonObject toJson() {
        Gson gson = new Gson();
        JsonElement data = new JsonParser().parse(gson.toJson(this));
        return data.getAsJsonObject();
    }

    public static class FeedbackBuilder {
        private final String questionId;
        private final String questionText;
        private final String answerId;
        private final String answerText;

        private FeedbackRating feedback;
        private String confidence;
        private String comment;

        public FeedbackBuilder(String id, QuestionInformation question, Answer answer) {
            this(id, question.getQuestionText(), answer.getId(), answer.getText());
        }

        private FeedbackBuilder(String questionId, String questionText, int answerId, String answerText) {
            this.questionId = questionId;
            this.questionText = questionText;
            //weird api answer id is an integer and feedback call wants it as string
            this.answerId = Integer.toString(answerId);
            this.answerText = answerText;
        }

        public FeedbackBuilder addRating(FeedbackRating rating) {
            this.feedback = rating;
            return this;
        }

        public FeedbackBuilder addComment(String comment) {
            this.comment = comment;
            return this;
        }

        public FeedbackBuilder withConfidence(int confidence) {
            this.confidence = Integer.toString(confidence);
            return this;
        }

        public Feedback create() {
            return new Feedback(this);
        }
    }
}
