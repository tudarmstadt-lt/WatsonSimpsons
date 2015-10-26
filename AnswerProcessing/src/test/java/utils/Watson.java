package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import answerProcessing.EntityCollection;
import answerProcessing.Pipeline;
import answerProcessing.QuizPipeline;
import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Question;
import answerProcessing.types.QuizAnswer;
import jwatson.JWatson;
import jwatson.answer.Answer;
import jwatson.answer.Evidencelist;
import jwatson.answer.WatsonAnswer;
import jwatson.question.WatsonQuestion;

public class Watson {

    private static final int NUM_OF_GENERATED_QUIZANSWERS = 15;

    private static JWatson watson;

    private static String credentialsFile = "src/test/resources/credentials.properties";

    private static String watsonUrl = "";
    private static String username = "";
    private static String password = "";

    private final static int numberOfSelectedAnswersForPipeline = 5;

    private static String remoteNLPUrl = "";

    public static WatsonAnswer retrieveWatsonAnswer(String questionQuery) {
        loadCredentials();
        try {
            watson = new JWatson(username, password, watsonUrl);
        } catch (IOException e) {
            System.err.println("Error during Watson Initialisation"
                    + e.getMessage());
            e.printStackTrace();
            return null;
        }
        WatsonAnswer wAnswer = null;
        try {
            wAnswer = watson.askQuestion(questionQuery);
        } catch (Exception e) {
            System.err
                    .println("Error during Watson Execution" + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return wAnswer;
    }

    public static List<PossibleAnswer> getPossibleAnswers(Question question,
                                                          WatsonAnswer wAnswer, int numOfAnswers) throws IOException {
        // nlp question annotation
        question = Pipeline.annotateQuestion(question, remoteNLPUrl, wAnswer);

        List<PossibleAnswer> possibleAnswers = new ArrayList<PossibleAnswer>();
        for (int i = 0; i < numOfAnswers; i++) {
            if (wAnswer.getAnswerInformation().getAnswers().size() > i
                    && wAnswer.getAnswerInformation().getEvidencelist().size() > i) {
                PossibleAnswer pAnswer = createPossibleAnswer(question, wAnswer
                        .getAnswerInformation().getAnswers().get(i), wAnswer
                        .getAnswerInformation().getEvidencelist().get(i));
                possibleAnswers.add(pAnswer);
            }
        }
        return possibleAnswers;
    }

    public static Question createQuestion(WatsonQuestion watsonQuestion) {
        return new Question(watsonQuestion.getQuestion().getQuestionText());
    }

    public static PossibleAnswer createPossibleAnswer(Question question,
                                                      Answer watsonAnswer, Evidencelist watsonEvidence) {
        PossibleAnswer result = new PossibleAnswer(question,
                watsonEvidence.getText(), watsonAnswer.getConfidence(),
                EntityCollection.retrieveMetadata(watsonEvidence.getTitle()),
                watsonEvidence.getTitle());

        return result;
    }

    public static void main(String[] args) {
        EntityCollection.initEntityInfos();
        String questionQuery =
                // "Who killed Mr. Burns?";
                // "where does does Homer work?";
                // "What Organization did Steve Mobbs found?";
                // "Which animal is Lisa's princess?";
                // "What is the name of Lisa's pony?";
                // "Which season is \"Bart's Girlfriend\"?";
                // "What episode of season 3 is \"Homer Alone\"?";
                // "What is the sixth episode of season 3?";
                // "Where does Alaska Nebraska live?";
                // " How old is Mr Burn's son?";
                // "What percentage of time is Bart a good brother?";
                // "How many children does Marge have?";
                // "Where does Lisa go to school?";
                "What job does Booberella have?";
        // "How many puppies did Santa's Little Helper have?";

        WatsonAnswer wAnswer = retrieveWatsonAnswer(questionQuery);

        Question pQuestion = new Question(questionQuery);
        try {
            List<PossibleAnswer> answers = getPossibleAnswers(pQuestion,
                    wAnswer, numberOfSelectedAnswersForPipeline);
            // System.out.println(answers.get(0).toString());
            // System.out.println(answers.get(0).getQuestion().getQuestionInformation().getqClassList());
            System.out.println(answers.get(0).getQuestion()
                    .getQuestionInformation().getFocusList());
            System.out.println(answers.get(0).getQuestion()
                    .getQuestionInformation().getLatList());
            // System.out.println(answers.get(0).getQuestion().getSynonymList());
            System.out.println(answers.get(0).getQuestion().getQuestionType());

            System.out.println(answers.get(0).getOriginalfile());
            System.out.println("Executing Quiz Pipeline...");
            List<QuizAnswer> allAnswers = QuizPipeline.executePipeline(
                    pQuestion, answers, remoteNLPUrl,
                    NUM_OF_GENERATED_QUIZANSWERS);
            System.out.println("Correct answer " + allAnswers.get(0) + "\n");
            for (QuizAnswer alt : allAnswers.subList(1, allAnswers.size())) {
                System.out.println(alt + ", ");
            }
            System.out.println("");

        } catch (IOException e) {
            System.err.println("Error during Pipeline Execution: "
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getRemoteNLPUrl() {
        if (remoteNLPUrl.isEmpty())
            loadCredentials();

        return remoteNLPUrl;
    }

    private static void loadCredentials() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(credentialsFile);
            prop.load(input);
            password = prop.getProperty("watsonPassword");
            watsonUrl = prop.getProperty("watsonUrl");
            remoteNLPUrl = prop.getProperty("remoteNLPUrl");
            username = prop.getProperty("watsonUsername");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
