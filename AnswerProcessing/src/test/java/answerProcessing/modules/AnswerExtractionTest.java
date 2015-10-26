package answerProcessing.modules;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import answerProcessing.modules.nGrams.CharNGramCounter;
import answerProcessing.modules.nGrams.NGramDifference;
import answerProcessing.modules.nGrams.NGramDistEvaluator;
import answerProcessing.modules.nGrams.NGramSentenceAnswerExtractor;
import answerProcessing.modules.nGrams.TokenNGramCounter;
import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Sentence;

public class AnswerExtractionTest {

	private CharNGramCounter charBigrams= new CharNGramCounter(2);
	private TokenNGramCounter tokenUnigrams= new TokenNGramCounter(1, false);
	private NGramDistEvaluator differences= new NGramDifference();
	
	@Test
	public void testCharBigram() {
		String questionStr= "Who was a better prankster than Bart is?";
		String textStr= "Groundskeeper Willie tells Bart about Andy Hamilton, a former student who was an even better prankster than Bart is."+ 
					 " Bart tracks down Andy, now 19 and still pulling pranks, and the two become quick friends.";
		Double[] expectedScores= new Double[]{0.4479375696767003, 0.6363636363636364};
		
		PossibleAnswer possibleAnswer= TestUtil.setUpPossibleAnswer(questionStr, textStr);
		NGramSentenceAnswerExtractor answering= new NGramSentenceAnswerExtractor(charBigrams, differences);
		
		//annotate with n-gram-scores
		answering.annotate(possibleAnswer);
		
		//check scores
		List<Sentence> sentences= possibleAnswer.getAnnotatedText().getSentences();
		checkScores(expectedScores, sentences, "CHAR_2_StopwordRemoval:false_SUMMED_DIFF");
	}
	
	@Test
	public void testTokenUnigram() {
		String questionStr= "Who was a better prankster than Bart is?";
		String textStr= "Groundskeeper Willie tells Bart about Andy Hamilton, a former student who was an even better prankster than Bart is."+ 
					 " Bart tracks down Andy, now 19 and still pulling pranks, and the two become quick friends.";
		Double[] expectedScores= new Double[]{0.5789473684210527, 0.9375};
		
		PossibleAnswer possibleAnswer= TestUtil.setUpPossibleAnswer(questionStr, textStr);
		NGramSentenceAnswerExtractor answering= new NGramSentenceAnswerExtractor(tokenUnigrams, differences);
		
		//annotate with n-gram-scores
		answering.annotate(possibleAnswer);
		
		//check scores
		List<Sentence> sentences= possibleAnswer.getAnnotatedText().getSentences();
		checkScores(expectedScores, sentences, "TOKEN_1_StopwordRemoval:false_SUMMED_DIFF");
	}
	
	@Test
	public void testTokenUnigramAndCharBigram() {
		String questionStr= "Who was a better prankster than Bart is?";
		String textStr= "Groundskeeper Willie tells Bart about Andy Hamilton, a former student who was an even better prankster than Bart is."+ 
					 " Bart tracks down Andy, now 19 and still pulling pranks, and the two become quick friends.";
		Double[] expectedUniScores= new Double[]{0.5789473684210527, 0.9375};
		Double[] expectedBiScores= new Double[]{0.4479375696767003, 0.6363636363636364};
		
		PossibleAnswer possibleAnswer= TestUtil.setUpPossibleAnswer(questionStr, textStr);
		NGramSentenceAnswerExtractor answering= new NGramSentenceAnswerExtractor(tokenUnigrams, differences);
		answering.addFreqCounter(charBigrams);
		
		//annotate with n-gram-scores
		answering.annotate(possibleAnswer);
		
		//check scores
		List<Sentence> sentences= possibleAnswer.getAnnotatedText().getSentences();
		checkScores(expectedUniScores, sentences, "TOKEN_1_StopwordRemoval:false_SUMMED_DIFF");
		checkScores(expectedBiScores, sentences, "CHAR_2_StopwordRemoval:false_SUMMED_DIFF");
	}

	@Test
	public void testMoreCharBigram() {
		String questionStr= "Who challenged Marge to a MMA match?";
		String textStr= "Marge and her friends are out doing \"crazy bowling\" and wonder why there are no men in the alley."+
					 " Marge discovers that all the men, are spending their time viewing the Mixed Martial Arts Ultimate Punch, Kick and Choke Championship."+
				     " When Marge catches Bart engaged in ultimate fighting in the school yard, she convinces her friends to form a protest group."+
					 " The women picket the ultimate fighting stadium, but this fails to attract attention."+
				     " Marge enters the arena to denounce ultimate fighting, and the promoter, Chett Englebrick, offers Marge a compromise."+
					 " He challenges Marge to a match, and if she wins he will shut down ultimate fighting in Springfield."; 
					 
		Double[] expectedScores= new Double[]{0.7193452380952381, 0.7073593073593075, 0.7135831381733021, 0.8767641996557659, 0.724223602484472, 0.6612244897959183};
		
		PossibleAnswer possibleAnswer= TestUtil.setUpPossibleAnswer(questionStr, textStr);
		assertTrue(textStr.equals(possibleAnswer.getRawText()));
		assertTrue(possibleAnswer.getRawText().equals(possibleAnswer.getAnnotatedText().toString()));
		assertTrue(textStr.equals(possibleAnswer.getAnnotatedText().toString()));
		NGramSentenceAnswerExtractor answering= new NGramSentenceAnswerExtractor(charBigrams, differences);
		
		//annotate with n-gram-scores
		answering.annotate(possibleAnswer);
		
		//check scores
		List<Sentence> sentences= possibleAnswer.getAnnotatedText().getSentences();
		checkScores(expectedScores, sentences, "CHAR_2_StopwordRemoval:false_SUMMED_DIFF");
	}

	protected void checkScores(Double[] scores, List<Sentence> sentences, String nGramType) {
		int numOfScores= scores.length;
		assertEquals(numOfScores, sentences.size());
		
		for(int i= 0; i<numOfScores; i++){
			System.out.println(nGramType+ " "+ sentences.get(i).getNgramDistance(nGramType)+ ": "+ sentences.get(i));
			assertEquals(scores[i], sentences.get(i).getNgramDistance(nGramType), 0.000001);
		}
	}

}
