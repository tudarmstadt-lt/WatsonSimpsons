package answerProcessing.modules;

import static org.junit.Assert.*;

import org.junit.Test;

import answerProcessing.modules.nGrams.CharNGramCounter;
import answerProcessing.modules.nGrams.NGramDifference;
import answerProcessing.modules.nGrams.NGramDistEvaluator;
import answerProcessing.modules.nGrams.NGramFreqCounter;
import answerProcessing.modules.nGrams.NGramFrequencyMap;
import answerProcessing.types.Sentence;


public class NGramDifferenceTest {

	@Test
	public void testReallyDifferent() {
		Sentence sent1= TestUtil.setUpSentences("Homer").get(0);
		NGramFreqCounter extractor= new CharNGramCounter(2);
		NGramFrequencyMap freq1= extractor.extractFreqs(sent1);
		Sentence sent2= TestUtil.setUpSentences("Bart").get(0);
		NGramFrequencyMap freq2= extractor.extractFreqs(sent2);
		NGramDistEvaluator difference= new NGramDifference();
		
		/*
		 * Homer: (H,o), (o,m), (m,e), (e,r)
		 * 		  0.25   0.25    0.25  0.25
		 * Bart: (B,a), (a,r), (r,t)
		 * 		  0.33   0.33   0.33
		 * dist= 4* 0.25
		 */
		assertTrue(1== difference.evaluateDistance(freq1, freq2));
	}
	
	@Test
	public void testSimilar() {
		Sentence sent1= TestUtil.setUpSentences("Homer").get(0);
		NGramFreqCounter extractor= new CharNGramCounter(2);
		NGramFrequencyMap freq1= extractor.extractFreqs(sent1);
		Sentence sent2= TestUtil.setUpSentences("Homes").get(0);
		NGramFrequencyMap freq2= extractor.extractFreqs(sent2);
		NGramDistEvaluator difference= new NGramDifference();
		
		/*
		 * Homer: (H,o), (o,m), (m,e), (e,r)
		 * 		  0.25   0.25    0.25  0.25
		 * Homes: (H,o), (o,m), (m,e), (e,s)
		 * 		  0.25   0.25    0.25  0.25
		 * dist= 0+ 0+ 0+ 0.25
		 */
		assertTrue(0.25== difference.evaluateDistance(freq1, freq2));
	}

	@Test
	public void testSame() {
		Sentence sent= TestUtil.setUpSentences("Homer").get(0);
		NGramFreqCounter extractor= new CharNGramCounter(2);
		NGramFrequencyMap freqs= extractor.extractFreqs(sent);
		NGramDistEvaluator difference= new NGramDifference();
		
		assertTrue(0== difference.evaluateDistance(freqs, freqs));
	}
	
}
