package answerProcessing.modules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jwatson.answer.Synonym;
import jwatson.answer.Synonymlist;
import jwatson.answer.Synset;
import answerProcessing.types.Question;
import answerProcessing.types.Sentence;
import answerProcessing.types.Token;

/**
 * Extract simple number/boolean features from answers (according to some given question information)
 *
 */
public class AnswerFeatureExtraction {
	
	public static double getRelSynonymFreq(Question question, Sentence answer){
		List<Synonymlist> synonymLists= question.getSynonymList();  // contains one synset per chosen word from the question
		double synFreq= 0.0;
		if(!synonymLists.isEmpty()){
			int numOfSyns= synonymLists.size();
			Set<String> matchedWords= new HashSet<String>();
			int numOfMatchedWords= 0;
			//count synonyms from the list that are found in the answer, divide by number of synonyms
			for(Token token: answer.getTokens()){
				for(Synonymlist synlist: synonymLists){
					if(containsSynonym(token, synlist))
						//do not count the same word/lemma twice
						if (matchedWords.add(token.getLemma().toLowerCase()) && matchedWords.add(token.getWord().toLowerCase()))
							numOfMatchedWords++;
				}
			}
			synFreq= (double)numOfMatchedWords/ (double)numOfSyns;
		}
		return synFreq;
	}

	/**
	 * Check if the token's content is found in the given synonyms
	 */
	private static boolean containsSynonym(Token token, Synonymlist synlist) {
		for(Synset synset: synlist.getSynset()){
			for(Synonym synonym: synset.getSynonym()){
				String synWord= synonym.getValue();
				if(token.getWord().equalsIgnoreCase(synWord) || token.getLemma().equalsIgnoreCase(synWord))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if the answer contains named entities of the same type as the question
	 * @return 0 (false) or (1) true
	 */
	public static int containsSpecificNEtypes(Question question, Sentence answer){
		List<String> neTypes = question.getQuestionInformation().getNEtype();
		for(Token token: answer.getTokens()){
			if(neTypes.contains(token.getNer()))
				return 1;
		}
		return 0;
	}
	
	/**
	 * Check if the answer contains synonyms of words from the question
	 * @return 0 (false) or (1) true
	 */
	public static int containsSynonyms(Question question, Sentence answer){
		List<Synonymlist> synonymLists= question.getSynonymList();
		for(Token token: answer.getTokens()){
			for(Synonymlist synlist: synonymLists){
				if (containsSynonym(token, synlist))
					return 1;
			}
		}
		return 0;
	}

}
