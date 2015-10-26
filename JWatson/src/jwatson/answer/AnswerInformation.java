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

import java.util.ArrayList;
import java.util.List;


public class AnswerInformation {

    /**
     * An integer that is assigned by the service to
     * identify this question and its answers.
     */
    private String id;

    /**
     * The response status of the request e.g 'Complete',
     * 'Timeout' or 'Failed'.
     */
    private String status;

    /**
     * The category of the question that was submitted with
     * the question. When no category was submitted with the
     * question, an empty category element is returned in the
     * response.
     */
    private String category;

    /**
     * The internal ID that is assigned for the final answer CAS.
     * This element contains the internal CAS ID that is assigned
     * after the question is answered. You can use this ID to identify
     * the question with the internal data structures that Watson uses.
     */
    private String pipelineid;

    /**
     * The container for a list of question classes that are determined
     * by the pipeline for the final answer.
     */
    private List<Qclasslist> qclasslist;

    /**
     * The collection of focus elements that are determined by the pipeline
     * for the final answer.
     */
    private List<Focuslist> focuslist;

    /**
     * The collection of lexical answer types (LATs) that the pipeline determined for
     * the final answer. The WatsonQuestion.lat is submitted in the POST when the question
     * was submitted. The WatsonQuestion.latlist contains the LATs that were determined
     * by the pipeline when it processed the answer.
     */
    private List<Latlist> latlist;

    /**
     * The collection of synonyms for terms in the question.
     */
    private List<Synonymlist> synonymList;


    /**
     * The collection of recoverable errors, if any.
     */
    private List<ErrorNotification> errorNotifications;

    /**
     * The collection of evidence used to support the answer(s).
     */
    private List<Evidencelist> evidencelist;

    /**
     * The collection of answers.
     */
    private List<Answer> answers;

    public AnswerInformation() {
        this.focuslist = new ArrayList<Focuslist>();
        this.latlist = new ArrayList<Latlist>();
        this.evidencelist = new ArrayList<Evidencelist>();
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getCategory() {
        return category;
    }

    public String getPipelineid() {
        return pipelineid;
    }

    public List<Qclasslist> getQclasslist() {
        return qclasslist;
    }

    public List<Focuslist> getFocuslist() {
        return focuslist;
    }

    public List<Latlist> getLatlist() {
        return latlist;
    }

    public List<Synonymlist> getSynonymList() {
        return synonymList;
    }

    public List<ErrorNotification> getErrorNotifications() {
        return errorNotifications;
    }

    public List<Evidencelist> getEvidencelist() {
        return evidencelist;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    @Override
    public String toString() {
        return "WatsonAnswer{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", category='" + category + '\'' +
                ", pipelineid='" + pipelineid + '\'' +
                ", qclasslist=" + qclasslist +
                ", focuslist=" + focuslist +
                ", latlist=" + latlist +
                ", synonymList=" + synonymList +
                ", errorNotifications=" + errorNotifications +
                ", evidencelist=" + evidencelist +
                ", answers=" + answers +
                '}';
    }
}
