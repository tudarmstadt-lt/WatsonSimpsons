package answerProcessing.modules;

import static org.junit.Assert.*;

import org.junit.Test;

import answerProcessing.modules.nGrams.CharNGramCounter;
import answerProcessing.modules.nGrams.NGramFreqCounter;
import answerProcessing.modules.nGrams.NGramFrequencyMap;
import answerProcessing.types.Sentence;
import answerProcessing.types.Text;

public class CharFreqExtractorTest {

	@Test
	public void testUniGram() {
		Sentence sent= TestUtil.setUpSentences("Homer").get(0);
		NGramFreqCounter extractor= new CharNGramCounter(1);
		String[] expectedUnigrams= new String[]{"H", "o", "m", "e", "r"};
		NGramFrequencyMap freqs= extractor.extractFreqs(sent);
		
		// check number of distinct unigrams
		assertEquals(expectedUnigrams.length, freqs.getNumberOfDistinctNGrams());
		
		// check individual unigrams
		for(String expectedUnigram: expectedUnigrams){
			assertEquals(1, freqs.getNGramAbsFreq(expectedUnigram));
		}
	}
		
	@Test
	public void testTextType() {
		Text text= new Text(TestUtil.setUpSentences("Homer"));
		NGramFreqCounter extractor= new CharNGramCounter(2);
		String[] expectedBigrams= new String[]{"H o", "o m", "m e", "e r"};
		NGramFrequencyMap freqs= extractor.extractFreqs(text);
		
		// check number of distinct bigrams
		assertEquals(expectedBigrams.length, freqs.getNumberOfDistinctNGrams());
		
		// check individual bigrams
		for(String expectedBigram: expectedBigrams){
			assertEquals(1, freqs.getNGramAbsFreq(expectedBigram));
		}
	}
	
	@Test
	public void testSentenceType() {
		Sentence sent= TestUtil.setUpSentences("Homer").get(0);
		NGramFreqCounter extractor= new CharNGramCounter(2);
		String[] expectedBigrams= new String[]{"H o", "o m", "m e", "e r"};
		NGramFrequencyMap freqs= extractor.extractFreqs(sent);
		
		// check number of distinct bigrams
		assertEquals(expectedBigrams.length, freqs.getNumberOfDistinctNGrams());
		
		// check individual bigrams
		for(String expectedBigram: expectedBigrams){
			assertEquals(1, freqs.getNGramAbsFreq(expectedBigram));
		}
	}

	@Test
	public void testTriGram() {
		Sentence sent= TestUtil.setUpSentences("Homer").get(0);
		NGramFreqCounter extractor= new CharNGramCounter(3);
		String[] expectedTrigrams= new String[]{"H o m", "o m e", "m e r"};
		NGramFrequencyMap freqs= extractor.extractFreqs(sent);
		
		// check number of distinct trigrams
		assertEquals(expectedTrigrams.length, freqs.getNumberOfDistinctNGrams());
		
		// check individual trigrams
		for(String expectedTrigram: expectedTrigrams){
			assertEquals(1, freqs.getNGramAbsFreq(expectedTrigram));
		}
	}

}
