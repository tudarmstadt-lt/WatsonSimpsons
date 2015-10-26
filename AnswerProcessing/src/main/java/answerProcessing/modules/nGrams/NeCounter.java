package answerProcessing.modules.nGrams;

import java.util.ArrayList;
import java.util.List;
import answerProcessing.types.Sentence;
import answerProcessing.types.Text;
import answerProcessing.types.Token;

/**
 * 
 * Record all named entities and their frequency
 *
 */
public class NeCounter extends NGramFreqCounter {

	private static final String NE= "NE_1";
	private static final String NO_NE= "O";
	
	
	@Override
	public NGramFrequencyMap extractFreqs(Text text) {
		List<Sentence> sents= text.getSentences();
		return extractFreqs(sents);
	}

	@Override
	public NGramFrequencyMap extractFreqs(Sentence sent) {
		List<Sentence> sents= new ArrayList<Sentence>();
		sents.add(sent);
		return extractFreqs(sents);
	}

	protected NGramFrequencyMap extractFreqs(List<Sentence> sents) {
		NGramFrequencyMap freqs= new NGramFrequencyMap();
		for(Sentence sent: sents){
			for(Token token: sent.getTokens()){
				if(!token.getNer().equals(NO_NE)){
					freqs.addNGram(token.getWord());
				}
			}
		}
		return freqs;
	}

	@Override
	public String getDescription() {
		return NE;
	}

}
