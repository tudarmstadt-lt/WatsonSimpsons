package answerProcessing.modules.nGrams;

import java.util.List;

import answerProcessing.types.Sentence;
import answerProcessing.types.Text;




public class CharNGramCounter extends NGramFreqCounter {
	
	private int numOfChars;
	
	private static final String CHAR= "CHAR";
	private boolean removeStopWords= false;
	
	// by default stopwords are not removed
	public CharNGramCounter(int n){
		numOfChars= n;
	}
	
	public CharNGramCounter(int n, boolean stopwords){
		this(n);
		removeStopWords= stopwords;
	}

	public NGramFrequencyMap extractFreqs(Text annotations) {
		String text;
		
		if(removeStopWords){
			List<Sentence> noStopwordsText= removeStopwords(annotations);
			text= "";
			for(Sentence sent: noStopwordsText){
				text= text+ " "+ sent.getConcatTokens();
			}
			text= text.trim();
		}else
			text= annotations.toString();
		
		return extractFreqs(text);
	}

	private NGramFrequencyMap extractFreqs(String text) {
		NGramFrequencyMap freqs= new NGramFrequencyMap();
		String nGram= "";
		int lastGramStart= text.length()-numOfChars+ 1;
		
		for(int i= 0; i<lastGramStart; i++){
			nGram= text.charAt(i)+ " ";
			for(int j=1; j<numOfChars; j++){
				nGram= nGram+ text.charAt(i+j)+ " ";
			}
			freqs.addNGram(nGram.trim());
		}
		
		return freqs;
	}

	public NGramFrequencyMap extractFreqs(Sentence annotations) {
		String text;
		
		if(removeStopWords){
			Sentence noStopwordsText= removeStopwords(annotations);
			text= noStopwordsText.getConcatTokens();
		}else
			text= annotations.toString();
		
		return extractFreqs(text);
	}

	public String getDescription() {
		return CHAR+ "_"+ numOfChars+ "_"+ "StopwordRemoval:"+ removeStopWords;
	}

}
