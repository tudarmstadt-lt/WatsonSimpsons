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

package jwatson.answer;


public class Answer {

    /**
     * An integer that uniquely identifies an answer in the context of the question.
     */
    private int id;

    /**
     * A string that contains an answer to the question in the form of text.
     */
    private String text;

    /**
     * (optional)
     * The HTML-formatted version of the answer text that is returned when you set formattedAnswer
     * to true when you submit a question. The formatted answer includes content from the tag of the answer.
     */
    private String formattedText;

    /**
     * A decimal percentage that represents the confidence that Watson has in this answer.
     * Higher values represent higher confidences.
     */
    private float confidence;

    /**
     * ???
     */
    private String pipeline;

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getFormattedText() {
        return formattedText;
    }

    public float getConfidence() {
        return confidence;
    }

    public String getPipeline() {
        return pipeline;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", formattedText='" + formattedText + '\'' +
                ", confidence=" + confidence +
                ", pipeline='" + pipeline + '\'' +
                '}';
    }
}
