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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import answerProcessing.EntityCollection;
import answerProcessing.QuizPipeline;
import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Question;
import answerProcessing.types.QuestionInformation.Questiontype;
import answerProcessing.types.QuizAnswer;
import answerProcessing.utils.RemoteNLP;
import jwatson.answer.WatsonAnswer;

public class EvalQuizQuestions {

    private final static String questionFile = "src/test/resources/questionsForQuizPipeline.tsv";
    private final static String outputFile = "target/evalQuizOutput.tsv";
    // TODO change if other score is used
    private static String scoreType = "charNGramOverlap-charNGramDist-confidence_3:2:5";

    private static String header = "Question\tExpectedQuestiontype\tIdentifiedQuestiontype\tExpectedAnswer\tBestAnswer\tBestScores("
            + scoreType + ")\tAltAnswer1\tAltScores1\n";
    private final static String COL_SEPARATE = "\t";
    private final static int numOfAltAnswers = 15;
    private final static int numOfAltScores = 10;

    private final static int numberOfSelectedAnswersForPipeline = 5;

    private static int correct = 0;
    private static Integer[] altScoreCorrect = new Integer[numOfAltScores];
    private static Integer[] altCorrect = new Integer[numOfAltAnswers];
    private static Integer[][] altCorrect_altScore = new Integer[numOfAltScores][numOfAltAnswers];
    private static int questionCount = 0;

    public static void main(String[] args) {
        EntityCollection.initEntityInfos();
        for (int i = 0; i < altCorrect.length; i++) {
            altCorrect[i] = 0;
        }
        for (int i = 0; i < altScoreCorrect.length; i++) {
            altScoreCorrect[i] = 0;
            for (int j = 0; j < altCorrect.length; j++) {
                altCorrect_altScore[i][j] = 0;
            }
        }
        List<Integer> lst;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            RemoteNLP.setUrlNlpServer(Watson.getRemoteNLPUrl());
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(questionFile)), "UTF8"));
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(new File(outputFile)), "UTF-8"));

            writer.write(header);

            String line, evalOut;
            while ((line = reader.readLine()) != null) {
                evalOut = evalQuestion(line);
                writer.write(evalOut);


            }
            // Console output
            lst = new ArrayList<Integer>(Arrays.asList(altCorrect));
            for (int k = 0; k < 5; k++) {
                lst = new ArrayList<Integer>(Arrays.asList(altCorrect_altScore[k]));


                System.out.println("Correct " + altScoreCorrect[k] + " of "
                        + questionCount + ", number of correct alternatives"
                        + lst);
                int countCorrects = altScoreCorrect[k];
                int i = 0;
                for (Integer alternative : lst) {
                    countCorrects += alternative;
                    i++;
                    System.out.println("Correct with " + i + " alternative : "
                            + (double) countCorrects / (double) questionCount);
                }
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

    private static String evalQuestion(String line) {

        String evalOut = "";
        String[] questionAnswer = line.split(COL_SEPARATE);
        String question = questionAnswer[0];
        String expectedAnswer = questionAnswer[1];
        String expectedQuestiontype = questionAnswer[2];

        try {
            WatsonAnswer wAnswer = Watson.retrieveWatsonAnswer(question);
            Question pQuestion = new Question(question);
            List<PossibleAnswer> answers = new ArrayList<PossibleAnswer>();
            List<QuizAnswer> allAnswers = new ArrayList<QuizAnswer>();
            QuizAnswer firstAnswer = new QuizAnswer("D'Oh", 0.0, new ArrayList<String>());
            Questiontype qType = Questiontype.unknown;
            List<QuizAnswer> altAnswers = new ArrayList<QuizAnswer>();
            altAnswers.add(firstAnswer);
            if (wAnswer != null) {
                answers = Watson.getPossibleAnswers(pQuestion, wAnswer,
                        numberOfSelectedAnswersForPipeline);
                allAnswers = QuizPipeline.executePipeline(pQuestion, answers,
                        Watson.getRemoteNLPUrl(), numOfAltAnswers);
                qType = pQuestion.getQuestionType();
            }


            StringBuilder sb = new StringBuilder();
            sb.append(question);
            sb.append(COL_SEPARATE);
            sb.append(expectedQuestiontype);
            sb.append(COL_SEPARATE);

            sb.append(qType);
            sb.append(COL_SEPARATE);
            sb.append(expectedAnswer);
            sb.append(COL_SEPARATE);
            for (int k = 0; k < 5; k++) {
                if (k == 0) allAnswers = sortByValue(allAnswers, 0.06, "default");
                if (k == 1) allAnswers = sortByValue(allAnswers, 0.08, "default");
                if (k == 2) allAnswers = sortByValue(allAnswers, 0.1, "default");
                if (k == 3) allAnswers = sortByValue(allAnswers, 0.12, "default");
                if (k == 4) allAnswers = sortByValue(allAnswers, 0.14, "default");


                altAnswers = allAnswers.subList(1, allAnswers.size());
                firstAnswer = allAnswers.get(0);
                boolean found = false;
                if (isCorrect(firstAnswer, expectedAnswer)) {
                    if (k == 0) correct++;
                    altScoreCorrect[k]++;
                    found = true;
                }
                String answerText = firstAnswer.getAnswer();
                if (firstAnswer.getNeTypes().contains("UNKNOWN"))
                    answerText = firstAnswer.getSentence();
                sb.append(answerText);
                sb.append(COL_SEPARATE);
                sb.append(firstAnswer.getScore());

                sb.append(COL_SEPARATE);
                Iterator<QuizAnswer> iterator = altAnswers.iterator();
                iterator.next();
                int numOfAnswers = 1;
                int i = 0;
                while (numOfAnswers <= numOfAltAnswers && iterator.hasNext()) {

                    QuizAnswer answer = (QuizAnswer) iterator.next();
                    if (isCorrect(answer, expectedAnswer))
                        if (!found) {
                            if (k == 0) altCorrect[i]++;
                            altCorrect_altScore[k][i]++;
                            found = true;
                        }
                    answerText = answer.getAnswer();
                    if (firstAnswer.getNeTypes().contains("UNKNOWN"))
                        answerText = answer.getSentence();
                    sb.append(answerText);
                    sb.append(COL_SEPARATE);
                    sb.append(answer.getScore());
                    sb.append(COL_SEPARATE);
                    numOfAnswers++;
                    i++;
                }
                evalOut = sb.toString().trim();
                evalOut = evalOut + "\n";
                if (k == 0) questionCount++;
            }
        } catch (Exception e) {
            System.err.println("Error during Pipeline Execution: "
                    + e.getMessage());
            e.printStackTrace();
        }
        return evalOut;
    }

    private static boolean isCorrect(QuizAnswer qAnswer, String expectedAnswer) {

        if (qAnswer.getNeTypes().contains("GENERATED"))
            return false;
        String answer;
        if (qAnswer.getNeTypes().contains("UNKNOWN"))
            answer = qAnswer.getSentence();
        else
            answer = qAnswer.getAnswer();
        return answer.toLowerCase().contains(expectedAnswer.toLowerCase());
    }

    public static List<QuizAnswer> sortByValue(
            List<QuizAnswer> allAnswers, double occurencesWeight, String scoreName) {

        Map<String, Double> scoredNes = new HashMap<String, Double>();
        Map<String, QuizAnswer> neOccurrences = new HashMap<>();
        Set<Integer> occurrences = new HashSet<>();
        for (QuizAnswer quizAnswer : allAnswers) {
            neOccurrences.put(quizAnswer.getAnswer() + quizAnswer.getScore(),
                    quizAnswer);
            occurrences.add(quizAnswer.getOccurrence());
        }
        // computes score for each NE in answer list
        // QuizAnswer.score = 15% relative neOccurrence + 85% score of
        // bestSentence
        final int max = (occurrences.isEmpty()) ? 1 : Collections.max(occurrences);
        for (Entry<String, QuizAnswer> nes : neOccurrences.entrySet()) {
            double oc = (double) nes.getValue().getOccurrence();
            double sc = nes.getValue().getSentenceScore(scoreName);
            double score = occurencesWeight * (oc / max) + (1 - occurencesWeight) * sc;
            scoredNes.put(nes.getKey(), score);
        }
        List<Entry<String, Double>> sortedEntries = new LinkedList<Entry<String, Double>>(
                scoredNes.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Entry<String, Double> o1,
                                       Entry<String, Double> o2) {
                        return Double.compare(o1.getValue(), o2.getValue());
                        // Double.compare(oc2 * Math.pow(sc2, 8),
                        // oc1 * Math.pow(sc1, 8));
                    }
                });
        List<QuizAnswer> lst = new ArrayList<QuizAnswer>();
        for (Entry<String, Double> nes : sortedEntries) {
            QuizAnswer qa =
                    neOccurrences.get(nes.getKey());
            qa.setScore(nes.getValue());

            lst.add(0, qa);
        }
        return lst;
    }
}
