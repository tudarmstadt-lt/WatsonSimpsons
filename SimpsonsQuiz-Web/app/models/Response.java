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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import answerProcessing.types.QuizAnswer;
import jwatson.answer.Evidencelist;
import jwatson.answer.WatsonAnswer;
import jwatson.question.WatsonQuestion;
import play.Logger;
import util.AnswerProcessing;

public class Response {

    private final static double confidenceThreshold = 10E-4;

    protected Optional<WatsonAnswer> answer;
    protected Optional<WatsonQuestion> question;
    private List<QuizAnswer> quizAnswers = null;
    private int numberOfAnswers = 0;
    private boolean isQuizResponse;

    public Response() {
        // Default
    }

    public Response(WatsonAnswer answer, WatsonQuestion question, int numberOfAnswers, boolean isQuiz) {
        this.answer = Optional.of(answer);
        this.question = Optional.of(question);
        this.numberOfAnswers = numberOfAnswers;
        this.isQuizResponse = isQuiz;
    }

    public List<Evidencelist> getFilteredEvidences() {
        int numberOfAnswers = question.get().getQuestion().getNumberOfAnswers();

        return answer.get().getAnswerInformation().getEvidencelist().stream()
                .filter(e -> e.getValue() >= confidenceThreshold)
                .limit(numberOfAnswers).collect(Collectors.toList());
    }

    public List<QuizAnswer> getQuizAnswers() {
        if (quizAnswers != null) return quizAnswers;
        try {
            quizAnswers = isQuizResponse ? AnswerProcessing.getQuizAnswers(answer.get(), question.get(), numberOfAnswers) :
                    AnswerProcessing.getQaAnswers(answer.get(), question.get(), numberOfAnswers);
            return quizAnswers;
        } catch (IOException e) {
            Logger.info("Response IO Exception");
            return new ArrayList<>();
        }
    }

    public boolean isEmpty() {
        return !answer.isPresent();
    }

    public static class EmptyResponse extends Response {

        public EmptyResponse(WatsonQuestion question) {
            this.answer = Optional.empty();
            this.question = Optional.of(question);
        }

        public EmptyResponse() {
            this.answer = Optional.empty();
            this.question = Optional.empty();
        }

        @Override
        public List<Evidencelist> getFilteredEvidences() {
            return new ArrayList<>();
        }

        @Override
        public List<QuizAnswer> getQuizAnswers() {
            return new ArrayList<>();
        }

    }
}