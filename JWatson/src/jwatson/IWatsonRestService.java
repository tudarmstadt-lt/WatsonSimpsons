/*
 *  Copyright 2015 Technische UniversitÃ¤t Darmstadt
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

package jwatson;


import java.io.IOException;

import jwatson.answer.WatsonAnswer;
import jwatson.feedback.Feedback;
import jwatson.question.WatsonQuestion;

public interface IWatsonRestService {
    WatsonAnswer askQuestion(WatsonQuestion question) throws IOException;
    WatsonAnswer askQuestion(String questionText) throws IOException;
    boolean sendFeedback(Feedback feedback);
    boolean ping();
}
