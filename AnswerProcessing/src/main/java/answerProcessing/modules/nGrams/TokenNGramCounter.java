package answerProcessing.modules.nGrams;

import java.util.ArrayList;
import java.util.List;

import answerProcessing.types.Sentence;
import answerProcessing.types.Text;
import answerProcessing.types.Token;

public class TokenNGramCounter extends NGramFreqCounter {
	
	private int numOfTokens;
	private static final String TOKEN= "TOKEN";
	private boolean removeStopWords= true;
	
	// By default removes stopwords
	public TokenNGramCounter(int n){
		numOfTokens= n;
	}
	
	public TokenNGramCounter(int n, boolean stopwords){
		this(n);
		removeStopWords= stopwords;
	}

	/**
	 * Count token n-grams in an annotated text
	 */
	public NGramFrequencyMap extractFreqs(Text annotations) {
		List<Token> tokens= new ArrayList<Token>();
		List<Sentence> filteredSents= annotations.getSentences();
		if(removeStopWords)
			filteredSents= removeStopwords(annotations);
		for(Sentence sent: filteredSents){
			tokens.addAll(sent.getTokens());
		}
		return extractTokenFreqs(tokens);
	}

	/**
	 * Count token n-grams in an annotated sentence
	 */
	public NGramFrequencyMap extractFreqs(Sentence annotations) {
		List<Token> tokens= annotations.getTokens();
		if(removeStopWords){
			Sentence filteredSent= removeStopwords(annotations);
			tokens= filteredSent.getTokens();
		}
		return extractTokenFreqs(tokens);
	}

	protected NGramFrequencyMap extractTokenFreqs(List<Token> tokens) {
		NGramFrequencyMap freqs= new NGramFrequencyMap();
		String nGram= "";
		int lastGramStart= tokens.size()-numOfTokens+ 1;
		
		for(int i= 0; i<lastGramStart; i++){
			nGram= tokens.get(i).getWord()+ " ";
			for(int j=1; j<numOfTokens; j++){
				nGram= nGram+ tokens.get(i+j).getWord()+ " ";
			}
			freqs.addNGram(nGram.trim());
		}
		
		return freqs;
	}

	
	public String getDescription() {
		return TOKEN+"_"+numOfTokens+ "_"+ "StopwordRemoval:"+ removeStopWords;
	}

}
