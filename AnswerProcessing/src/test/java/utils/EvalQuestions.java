package utils;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import answerProcessing.EntityCollection;
import answerProcessing.Pipeline;
import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Question;
import answerProcessing.types.ScoringComparator;
import answerProcessing.types.Sentence;
import answerProcessing.utils.RemoteNLP;
import jwatson.answer.WatsonAnswer;

/**
 * Compare generated sentence answers to gold standard from file, print dump to output file and evaluation metrics to console.
 * Needs filepaths, nlp-server url and access to test.java.utils.watson with Watson Private Instance credentials.
 */
public class EvalQuestions {

    private final static String questionFile = "src/test/resources/questions.tsv";
    private final static String outputFile = "target/evalOutput.tsv";
    //Change if other scores are added
    private static String[] scoreTypes = new String[]{"default"};

    private static String header = "Question\tExpectedAnswer\tBestAnswer\tScoreType\tScore\tAltAnswer1\tAltScore1\n";
    ;
    private final static String COL_SEPARATE = "\t";
    private final static int numOfAltAnswers = 5;

    private final static int numberOfSelectedAnswersForPipeline = 3;

    private static int[] correct = new int[scoreTypes.length];
    private static int questionCount = 0;
    private static int[][] altCorrect = new int[numOfAltAnswers][scoreTypes.length];


    public static void main(String[] args) {
        EntityCollection.initEntityInfos();

        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            RemoteNLP.setUrlNlpServer(Watson.getRemoteNLPUrl());
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(
                    new File(questionFile)), "UTF8"));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFile)), "UTF-8"));

            writer.write(header);

            String line, evalOut;
            while ((line = reader.readLine()) != null) {
                evalOut = evalQuestion(line);
                writer.write(evalOut);
            }

            for (int i = 0; i < scoreTypes.length; i++) {
                System.out.println(scoreTypes[i] + ": Correct/All : " + correct[i] + "/" + questionCount + "= " + String.format("%.2f", (double) correct[i] / (double) questionCount));
                evalAndPrintAlternatives(i);
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(writer);
        }
    }

    /**
     * Calculate accuracy of answers when including alternative answers
     */
    private static void evalAndPrintAlternatives(int scoreTypeIndex) {
        int countCorrects = correct[scoreTypeIndex];
        for (int i = 0; i < numOfAltAnswers; i++) {
            System.out.println(scoreTypes[scoreTypeIndex] + ": Correct with " + i + " alternatives : " + String.format("%.2f", (double) countCorrects / (double) questionCount));
            countCorrects += altCorrect[i][scoreTypeIndex];
        }
        System.out.println(scoreTypes[scoreTypeIndex] + ": Correct with " + numOfAltAnswers + " alternatives : " + String.format("%.2f", (double) countCorrects / (double) questionCount));

    }

    /**
     * Evaluate one question-answer pair for all score types
     *
     * @param line question answer pair from gold standard file
     * @return evaluation line
     */
    private static String evalQuestion(String line) {
        String[] questionAnswer = line.split(COL_SEPARATE);
        String question = questionAnswer[0];
        String expectedAnswer = questionAnswer[1];
        String evalOut = "";

        try {
            WatsonAnswer wAnswer = Watson.retrieveWatsonAnswer(question);
            Question pQuestion = new Question(question);
            List<PossibleAnswer> answers = Watson.getPossibleAnswers(pQuestion, wAnswer, numberOfSelectedAnswersForPipeline);
            List<Sentence> bestAnswers = Pipeline.getBestAnswerList(pQuestion, answers, Watson.getRemoteNLPUrl(), -1);

            for (int j = 0; j < scoreTypes.length; j++) {
                Collections.sort(bestAnswers, new ScoringComparator(scoreTypes[j]));
                evalOut = evalOut + "\n" + evalOneScoreType(expectedAnswer, question, bestAnswers, j);
            }
            questionCount++;
        } catch (Exception e) {
            System.err.println("[EvalQuestions] Error during Watson or Pipeline Execution: " + e.getMessage());
            e.printStackTrace();
        }

        evalOut = evalOut + "\n";
        return evalOut;
    }

    private static String evalOneScoreType(String expectedAnswer, String question,
                                           List<Sentence> bestAnswers, int scoreIndex) {
        StringBuilder sb = new StringBuilder();
        sb.append(question);
        sb.append(COL_SEPARATE);
        sb.append(expectedAnswer);
        sb.append(COL_SEPARATE);
        Sentence firstAnswer = bestAnswers.get(0);
        boolean found = false;
        if (firstAnswer.toString().toLowerCase().contains(expectedAnswer.toLowerCase())) {
            correct[scoreIndex]++;
            found = true;
        }
        sb.append(firstAnswer);
        sb.append(COL_SEPARATE);

        String scoreType = scoreTypes[scoreIndex];
        sb.append(scoreType);
        sb.append(COL_SEPARATE);
        sb.append(firstAnswer.getScore(scoreType));
        sb.append(COL_SEPARATE);
        Iterator<Sentence> iterator = bestAnswers.iterator();
        iterator.next();
        int numOfAnswers = 1;
        int i = 0;
        while (numOfAnswers <= numOfAltAnswers && iterator.hasNext()) {
            Sentence sentence = (Sentence) iterator.next();
            sb.append(sentence);
            sb.append(COL_SEPARATE);
            sb.append(sentence.getScore(scoreType));
            sb.append(COL_SEPARATE);
            // check alternative answers for correct one if necessary
            if (sentence.toString().toLowerCase().contains(expectedAnswer.toLowerCase()))
                if (!found) {
                    altCorrect[i][scoreIndex]++;
                    found = true;
                }
            i++;
            numOfAnswers++;
        }

        return sb.toString().trim();
    }

}
