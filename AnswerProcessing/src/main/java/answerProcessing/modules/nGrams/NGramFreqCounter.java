package answerProcessing.modules.nGrams;

import java.util.ArrayList;
import java.util.List;

import answerProcessing.types.Sentence;
import answerProcessing.types.Text;
import answerProcessing.types.Token;


public abstract class NGramFreqCounter {

	/**
	 * Extract n-gram frequencies from the given annotated text
	 * @param annotations the annotated text
	 * @return n-gram frequency map of n-grams from the given text
	 */
	public abstract NGramFrequencyMap extractFreqs(Text annotations);
	
	/**
	 * Extract n-gram frequencies from the given annotated sentence
	 * @param annotations the annotated sentence
	 * @return n-gram frequency map of n-grams from the given text
	 */
	public abstract NGramFrequencyMap extractFreqs(Sentence annotations);
	
	
	/**
	 * Get description for type of gram and its number n
	 * @return string description
	 */
	public abstract String getDescription();
	
	/**
	 * Remove stopwords from the sentences in the given text
	 * @param text the text
	 * @return list of stopword free sentences
	 */
	public List<Sentence> removeStopwords(Text text){
		List<Sentence> filteredSentences= new ArrayList<Sentence>();
		
		for(Sentence sent: text.getSentences()){
			filteredSentences.add(removeStopwords(sent));
		}
		
		return filteredSentences;
	}

	/**
	 * Remove stopwords from the given sentence
	 * @param sent the sentence
	 * @return stopword free sentence
	 */
	public Sentence removeStopwords(Sentence sent) {
		Sentence filteredSentence = shallowCopySentence(sent);
		List<Token> filteredTokens= new ArrayList<Token>();
		String contentPattern="(VB).?|(MD)|(RB).?|(NN).*|(JJ).?|(CD)|(SYM)|(FW)|(WRB)|(WP)";  //verbs, modal verbs, adverbs, nouns, adjectives, numbers, symbols, foreign word, who, how etc.
		for(Token origToken: sent.getTokens()){
			if(origToken.getPos().matches(contentPattern))
				filteredTokens.add(origToken);
		}
		filteredSentence.setTokens(filteredTokens);
		return filteredSentence;
	}

	private Sentence shallowCopySentence(Sentence sent) {
		Sentence filteredSentence= new Sentence();
		filteredSentence.setRawText(sent.getRawText());
		filteredSentence.setIndex(sent.getIndex());
		filteredSentence.setBasicDependencies(sent.getBasicDependencies());
		filteredSentence.setCollapsedCcprocessedDependencies(sent.getCollapsedCcprocessedDependencies());
		filteredSentence.setCollapsedDependencies(sent.getCollapsedDependencies());
		filteredSentence.setMachineReading(sent.getMachineReading());
		filteredSentence.setParentText(sent.getParentText());
		filteredSentence.setParse(sent.getParse());
		return filteredSentence;
	}
}
