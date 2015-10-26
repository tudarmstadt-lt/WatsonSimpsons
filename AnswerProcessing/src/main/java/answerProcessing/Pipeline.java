package answerProcessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jwatson.answer.WatsonAnswer;
import answerProcessing.modules.AnswerFeatureExtraction;
import answerProcessing.modules.nGrams.CharNGramCounter;
import answerProcessing.modules.nGrams.NGramDifference;
import answerProcessing.modules.nGrams.NGramDistEvaluator;
import answerProcessing.modules.nGrams.NGramSentenceAnswerExtractor;
import answerProcessing.modules.nGrams.SpiNGramSim;
import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Question;
import answerProcessing.types.ScoringComparator;
import answerProcessing.types.Sentence;
import answerProcessing.utils.RemoteNLP;

public class Pipeline {
	
    /**
     * Executes the Pipeline and returns a resulting list with the best answers.
     *
     * @param q               
     * the question to ask
     * @param possibleAnswers 
     * the possible Answers to select from
     * @param serverUrl       
     * the URL of the Remote-NLP-Server
     * @return a list with the best answers
     * @throws IOException if connection to Remote-NLP-Server fails
     */
    public static List<Sentence> executePipeline(Question q,
                                                 List<PossibleAnswer> possibleAnswers, String serverUrl) throws IOException {
    	//Counters
    	CharNGramCounter charBigrams = new CharNGramCounter(2);
        CharNGramCounter charTrigrams = new CharNGramCounter(3);
        //Differences
        NGramDistEvaluator differences = new NGramDifference();
        NGramDistEvaluator spiDist= new SpiNGramSim();
        //Annotators
        NGramSentenceAnswerExtractor differenceAnnotator = new NGramSentenceAnswerExtractor(
                charBigrams, differences);
        NGramSentenceAnswerExtractor overlapAnnotator = new NGramSentenceAnswerExtractor(
                charTrigrams, spiDist);
        //Descriptors
        String charTriGramOverlapType = overlapAnnotator.extractDescription(charTrigrams);
        String charGramType = differenceAnnotator.extractDescription(charBigrams);
        
        ArrayList<Sentence> bestAnswers = new ArrayList<Sentence>();
        for (PossibleAnswer answer : possibleAnswers) {
            // nlp annotation
            answer.setAnnotatedText(RemoteNLP.annotate(answer.getRawText()));
            // n-gram sentence annotation
            differenceAnnotator.annotate(answer);
            overlapAnnotator.annotate(answer);
            //metadata
            answer.getAnnotatedText().setOriginalfile(answer.getOriginalfile());
            answer.getAnnotatedText().setTitle(answer.getTitle());
            for (Sentence s : answer.getAnnotatedText().getSentences()) {
            	int neTypeScore= AnswerFeatureExtraction.containsSpecificNEtypes(q, s);
                double score =0.3 * (1 - s.getNgramDistance(charTriGramOverlapType))+ 0.2 * (1 - s.getNgramDistance(charGramType)) + 0.1* neTypeScore+ 0.4 * answer.getConfidence();
       
                // add different scores
                s.addScore("default", score);
                bestAnswers.add(s);

            }
        }

        // Sort answers
        Collections.sort(bestAnswers, new ScoringComparator("default"));

        return bestAnswers;
    }

    /**
     * Executes the Pipeline and returns a list with the best answers.
     *
     * @param q               
     * the question to ask
     * @param possibleAnswers 
     * the possible Anwers to select from
     * @param serverUrl       
     * the URL of the Remote-NLP-Server
     * @return a list with the best answers
     * @throws IOException if connection to Remote-NLP-Server fails
     */
    public static List<Sentence> getBestAnswerList(Question q,List<PossibleAnswer> possibleAnswers, String serverUrl, int numOfAnswers) throws IOException {
        List<Sentence> resultList = executePipeline(q, possibleAnswers, serverUrl);
        if (numOfAnswers < 0)
            return resultList;

        return resultList.subList(0, (resultList.size() < numOfAnswers ? resultList.size() : numOfAnswers));
    }

    /**
     * Executes the Pipeline and returns only the first best answers.
     *
     * @param q               
     * the question to ask
     * @param possibleAnswers 
     * the possible Anwers to select from
     * @param serverUrl       
     * the URL of the Remote-NLP-Server
     * @return the first best answers
     * @throws IOException if connection to Remote-NLP-Server fails
     */
    public static Sentence getBestAnswer(Question q,
                                         List<PossibleAnswer> possibleAnswers, String serverUrl) throws IOException {
        List<Sentence> bestAnswers = executePipeline(q, possibleAnswers, serverUrl);
        return bestAnswers.get(0);
    }
 
	

	/**
	 * NLP Annotation for Question
     * computes question type 
     * sets the SynonmyLists
	 * @param question 		
	 * the question to ask
	 * @param remoteNLPUrl	
	 * the URL of the Remote-NLP-Server
	 * @param wAnswer		
	 * answer list of Watson
	 * @return the annotated Question
	 * @throws IOException if connection to Remote-NLP-Server fails
	 */
	public static Question annotateQuestion( Question question, String remoteNLPUrl, WatsonAnswer wAnswer) throws IOException{
		 RemoteNLP.setUrlNlpServer(remoteNLPUrl);
				question.setAnnotatedText(RemoteNLP.annotate(question.getRawText()));
			question.setQuestionType(
					wAnswer.getAnswerInformation().getQclasslist(),
					wAnswer.getAnswerInformation().getLatlist(),
					wAnswer.getAnswerInformation().getFocuslist(),
				    question.getAnnotatedText().getSentences(),
				    question.getRawText());
			question.setSynonymList(wAnswer.getAnswerInformation().getSynonymList());
	return question;
	}
	
}
