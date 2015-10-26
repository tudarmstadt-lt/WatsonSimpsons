package answerProcessing.modules;

import static org.junit.Assert.*;

import org.junit.Test;

import answerProcessing.modules.nGrams.NGramFrequencyMap;

public class FreqMapTest {

	@Test
	public void testAddNGram() {
		String[] ngrams= new String[]{"H o", "m e", "e r", "m e", "e r"};
		int[] expectedCounts= new int[]{1, 2, 2, 2, 2};
		NGramFrequencyMap freqs= addSomeGrams(ngrams);		
		
		// check amount of distinct ngrams
		assertEquals(3, freqs.getNumberOfDistinctNGrams());
		
		// check individual counts
		String currentNgram;
		for(int i= 0; i<ngrams.length; i++){
			currentNgram= ngrams[i];
			assertEquals(expectedCounts[i], freqs.getNGramAbsFreq(currentNgram));
		}
	}
	
	@Test
	public void testRelCounts() {
		String[] ngrams= new String[]{"H o", "m e", "e r", "m e", "e r", "B a", "a r", "r t"};
		double[] expectedCounts= new double[]{0.125, 0.25, 0.25, 0.25, 0.25, 0.125, 0.125, 0.125};
		NGramFrequencyMap freqs= addSomeGrams(ngrams);		
		
		// check counts
		String currentNgram;
		for(int i= 0; i<ngrams.length; i++){
			currentNgram= ngrams[i];
			assertTrue(expectedCounts[i]== freqs.getNGramRelFreq(currentNgram));
		}
	}
	
	private NGramFrequencyMap addSomeGrams(String[] ngrams){
		NGramFrequencyMap freqs= new NGramFrequencyMap();
		
		for(String ngram: ngrams){
			freqs.addNGram(ngram);
		}
		
		return freqs;		
	}

}
