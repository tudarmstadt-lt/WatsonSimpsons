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


import java.util.List;

/**
 * A question to ask Watson.
 * See "IBM Watson Developer Cloud" for more details
 * IBM Watson Developer Cloud: http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/apis/#!/Question_Answer/question
 *
 * @author Uli Fahrer
 */
public class QuestionInformation {

    /**
     * The text of the question to be answered.
     */
    private String questionText;

    /**
     * An integer in the range 1 – 10 that represents the number of possible answers to be returned.
     * If you do not specify the number of items, the request assumes five answers.
     */
    private int items;

    /**
     * Specifies that you want Watson to return supporting evidence
     * for each answer in the answers/answer/evidence section of the answer response.
     */
    private EvidenceRequest evidenceRequest;

    /**
     * (optional)
     * Specify an answer to receive the supporting evidence passages for that answer. Without this element,
     * Watson searches for answers from the questionText. When you assert an answer, Watson uses that answer
     * instead to search for supporting evidence passages.
     * 
     * If you configured your pipeline to support a ranked list of evidence, the supporting evidence appears 
     * in the question/evidencelist section. If you include the evidenceRequest element with the question, the
     * supporting evidence appears in the answers/answer/evidence section. If no supporting passages are returned 
     * for the asserted answer, the API returns a message that no answers were found.
     *
     * Restriction: Your processing pipeline must be configured to support AnswerAssertionPrimarySearch. 
     * Otherwise, the element is ignored.
     */
    private String answerAssertion;

    /**
     * The category of the question in terms of a constraint on the possible answers.
     */
    private String category;

    /**
     * (optional)
     * A natural language string that is composed of words that provide extra information for Watson to
     * consider when it determines answers. The maximum length of this element is 1024 characters.
     * 
     * The context element can have one of the following values:
     * <ul>
     *     <li>Plain text: 
     *      The context is provided as text in the context element: <context>Fauna</context>
     *     </li>
     *     <li>Url:
     *      A valid URL that points to the context string. The service accepts text and HTML in the
     *      response. 
     *      
     *          <context>http://www.example.com/fauna.txt</context>
     *
     *      The first 1024 characters of the contents in the file that is identified by the URL are used as
     *      the context value. If the file is not found (HTTP 404), the service returns an error and the
     *      question is not submitted to Watson.
     *     </li>
     * </ul>
     */
    private String context;

    /**
     * Requests Watson to return the formatted answer.
     */
    private boolean formattedAnswer;

    /**
     * Specifies a string that you include with the question. The passthru data is not submitted with the
     * pipeline but does pass through to the answer.
     */
    //Todo: Add to answer
    private String passthru;

    /**
     * The lexical answer type (LAT) of the question. The LAT is a word or noun phrase that appears in the
     * question, or is implied by it. LATs are terms in the question that indicate what type of entity is 
     * being asked for. The headword of the focus is generally a LAT, but questions often contain additional
     * LATs. LATs are used by Watson’s type coercion components to determine whether a candidate answer
     * is an instance of the answer types.
     */
    private String lat;

    /**
     * Supports the use of metadata to restrict answers to specific documents.
     */
    //Todo: Filter add own builder?
    private List<Filter> filters;


    public QuestionInformation(String questionText, int items, String category, String context, boolean formattedAnswer) {
        this.questionText = questionText;
        this.items = items;
        this.category = category;
        this.context = context;
        this.formattedAnswer = formattedAnswer;
        //TODO: Init rest
    }

    public String getQuestionText() {
        return questionText;
    }
    
    public int getNumberOfAnswers() {
        return items;
    }

    public boolean isFormattedAnswer() {
        return formattedAnswer;
    }

    @Override
    public String toString() {
        return "QuestionInformation{" +
                "questionText='" + questionText + '\'' +
                ", items=" + items +
                ", evidenceRequest=" + evidenceRequest +
                ", answerAssertion='" + answerAssertion + '\'' +
                ", category='" + category + '\'' +
                ", context='" + context + '\'' +
                ", formattedAnswer=" + formattedAnswer +
                ", passthru='" + passthru + '\'' +
                ", lat='" + lat + '\'' +
                ", filters=" + filters +
                '}';
    }
}
