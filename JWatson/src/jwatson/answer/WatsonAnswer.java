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


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jwatson.util.CustomEvidenceDeserializer;

import java.util.Collections;

/**
 * An answer received from Watson.
 *
 * @author Uli Fahrer
 */
public class WatsonAnswer {

    private AnswerInformation question;

    private WatsonAnswer() {
        question = new AnswerInformation();
    }

    /**
     * Creates a Watson Answer from the given response.
     *
     * @param content
     */
    public static WatsonAnswer createAnswerFromContent(String content) {

        if(content == null) {
            return new WatsonAnswer();
        }

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Evidencelist.class, new CustomEvidenceDeserializer())
                .create();

        WatsonAnswer answer = gson.fromJson(content, WatsonAnswer.class);
        answer.question.getEvidencelist().removeAll(Collections.singleton(null));

        return answer;
    }

    public AnswerInformation getAnswerInformation() {
        return question;
    }

    @Override
    public String toString() {
        return "WatsonAnswer{" +
                "question=" + question +
                '}';
    }
}
