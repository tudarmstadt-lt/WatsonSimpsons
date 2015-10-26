package answerProcessing.modules;

import static org.junit.Assert.*;

import org.junit.Test;

import answerProcessing.modules.nGrams.NGramFreqCounter;
import answerProcessing.modules.nGrams.NGramFrequencyMap;
import answerProcessing.modules.nGrams.TokenNGramCounter;
import answerProcessing.types.Sentence;
import answerProcessing.types.Text;

public class TokenFreqExtractorTest {

	@Test
	public void testUniGram() {
		String text= "Homer Homer Bart Maggie";
		Sentence sent= TestUtil.setUpSentences(text).get(0);
		NGramFreqCounter extractor= new TokenNGramCounter(1, false);
		
		String[] expectedUnigrams= new String[]{"Homer", "Homer", "Bart", "Maggie"};
		double[] expectedFreqs= new double[]{0.5, 0.5, 0.25, 0.25};
		NGramFrequencyMap freqs= extractor.extractFreqs(sent);
		
		// check number of distinct unigrams
		assertEquals(3, freqs.getNumberOfDistinctNGrams());
		
		// check individual unigrams
		int i= 0;
		for(String expectedUnigram: expectedUnigrams){
			assertTrue(expectedFreqs[i]== freqs.getNGramRelFreq(expectedUnigram));
			i++;
		}
	}

	@Test
	public void testBiGram() {
		NGramFreqCounter extractor= new TokenNGramCounter(2, false);
		String text= "Homer Homer Bart Maggie";
		Sentence sent= TestUtil.setUpSentences(text).get(0);
		String[] expectedBigrams= new String[]{"Homer Homer", "Homer Bart", "Bart Maggie"};
		NGramFrequencyMap freqs= extractor.extractFreqs(sent);
		
		// check number of distinct bigrams
		assertEquals(expectedBigrams.length, freqs.getNumberOfDistinctNGrams());
		
		// check individual bigrams
		for(String expectedBigram: expectedBigrams){
			assertEquals(1, freqs.getNGramAbsFreq(expectedBigram));
		}
	}
	
	@Test
	public void testTextType() {
		Text text= new Text(TestUtil.setUpSentences("Homer ate a doughnut. Life was good."));
		NGramFreqCounter extractor= new TokenNGramCounter(2, false);
		String[] expectedBigrams= new String[]{"Homer ate", "ate a", "a doughnut.", "doughnut. Life", "Life was", "was good."};
		NGramFrequencyMap freqs= extractor.extractFreqs(text);
		
		// check number of distinct bigrams
		assertEquals(expectedBigrams.length, freqs.getNumberOfDistinctNGrams());
		
		// check individual bigrams
		for(String expectedBigram: expectedBigrams){
//			System.out.println(expectedBigram);
			assertEquals(1, freqs.getNGramAbsFreq(expectedBigram));
		}
	}
	
	@Test
	public void testSentenceType() {
		Sentence text= TestUtil.setUpSentences("Homer ate a doughnut.").get(0);
		NGramFreqCounter extractor= new TokenNGramCounter(2, false);
		String[] expectedBigrams= new String[]{"Homer ate", "ate a", "a doughnut."};
		NGramFrequencyMap freqs= extractor.extractFreqs(text);
		
		// check number of distinct bigrams
		assertEquals(expectedBigrams.length, freqs.getNumberOfDistinctNGrams());
		
		// check individual bigrams
		for(String expectedBigram: expectedBigrams){
			assertEquals(1, freqs.getNGramAbsFreq(expectedBigram));
		}
	}
	
}
