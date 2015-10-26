package util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import answerProcessing.EntityCollection;
import answerProcessing.Pipeline;
import answerProcessing.QuizPipeline;
import answerProcessing.types.Image;
import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Question;
import answerProcessing.types.QuizAnswer;
import imagefinder.ImageFinder;
import jwatson.answer.Answer;
import jwatson.answer.Evidencelist;
import jwatson.answer.WatsonAnswer;
import jwatson.question.WatsonQuestion;

public class AnswerProcessing {

    public static List<QuizAnswer> getQuizAnswers(WatsonAnswer answer,
                                                  WatsonQuestion question, int numberOfAnswers) throws IOException {
        return getAnswers(answer, question, numberOfAnswers, true);
    }

    public static List<QuizAnswer> getQaAnswers(WatsonAnswer answer,
                                                WatsonQuestion question, int numberOfAnswers) throws IOException {
        return getAnswers(answer, question, numberOfAnswers, false);
    }

    private static List<QuizAnswer> getAnswers(WatsonAnswer answer,
                                               WatsonQuestion question, int numberOfAnswers, boolean isQuiz)
            throws IOException {

        // init UrlLists
        EntityCollection.initEntityInfos();
        List<QuizAnswer> allAnswers = new ArrayList<QuizAnswer>();
        List<PossibleAnswer> answers = new ArrayList<PossibleAnswer>();

        // Question Annotation
        Question pQuestion = new Question(question.getQuestion()
                .getQuestionText());
        Config conf = ConfigFactory.load();

        // Pipeline Execution
        pQuestion = Pipeline.annotateQuestion(pQuestion,
                conf.getString("remoteNLP.url"), answer);

        for (int i = 0; i < answer.getAnswerInformation().getAnswers().size(); i++) {
            Evidencelist watsonEvidence = answer.getAnswerInformation()
                    .getEvidencelist().get(i);
            Answer watsonAnswer = answer.getAnswerInformation().getAnswers()
                    .get(i);

            PossibleAnswer pAnswer = new PossibleAnswer(
                    pQuestion,
                    watsonEvidence.getText(),
                    watsonAnswer.getConfidence(),
                    EntityCollection.retrieveMetadata(watsonEvidence.getTitle()),
                    watsonEvidence.getTitle());

            answers.add(pAnswer);
        }

        //QuizPipeline Execution
        if (isQuiz)
            allAnswers = QuizPipeline.executePipeline(pQuestion, answers,
                    conf.getString("remoteNLP.url"), numberOfAnswers);
        else
            //without generated (random) answers
            allAnswers = QuizPipeline.executeQAPipeline(pQuestion, answers,
                    conf.getString("remoteNLP.url"));

        //find images for quiz answers
        for (QuizAnswer qAnswer : allAnswers) {
            boolean isEpisodeorSeason = qAnswer.isEpisodeOrSeason();
            imagefinder.types.Image image = ImageFinder.findPicture(
                    qAnswer.getImageName(), isEpisodeorSeason, 220);
            qAnswer.setImage(new Image(image.getName(), image.getWidth(), image
                    .getHeight(), image.getUrl(), image.getDescriptionurl(),
                    image.getThumburl(), image.getTitle()));
        }
        return allAnswers;
    }
}
