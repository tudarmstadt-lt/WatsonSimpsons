package answerProcessing.modules;

import static org.junit.Assert.*;

import org.junit.Test;

import answerProcessing.modules.nGrams.NGramFreqCounter;
import answerProcessing.modules.nGrams.TokenNGramCounter;
import answerProcessing.types.Sentence;

public class NGramFreqCounterTest {

	@Test
	public void testOneSentenceRemoveStopwords() {
		String text= "He is the most misunderstood of the family (After Maggie, who currently can only speak 2 words).";
		String[][] tokens= {{"0", "He", "PRP"}, {"1", "is", "VBZ"}, {"2", "the", "DT"}, {"3", "most", "RBS"}, {"4", "misunderstood", "VBN"}, {"5", "of", "IN"}, {"6", "the", "DT"},
				{"7", "family", "NN"}, {"8", "(", "-LRB-"}, {"9", "After", "IN"}, {"10", "Maggie", "NNP"}, {"11", ",", ","}, {"12", "who", "WP"}, {"13", "currently", "RB"}, 
				{"14", "can", "MD"}, {"15", "only", "RB"}, {"16", "speak", "VB"}, {"16", "2", "CD"}, {"17", "words", "NNS"}, {"18", ")", "-RRB-"}, {"19", ".", "."}};
		String expectedConcat= "is most misunderstood family Maggie who currently can only speak 2 words";
		
		Sentence sent= TestUtil.createSentenceFromTokens(tokens, text);
		NGramFreqCounter counter= new TokenNGramCounter(2);
		Sentence filteredSent= counter.removeStopwords(sent);
		
		assertEquals(text, filteredSent.getRawText());
//		System.out.println(filteredSent.getConcatTokens());
		assertEquals(expectedConcat, filteredSent.getConcatTokens());
		
	}

}
