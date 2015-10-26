package answerProcessing.modules.nGrams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import answerProcessing.types.NeOccurrence;

public class NGramFrequencyMap {

	private Map<String, Integer> nGramFreqs= new HashMap<String, Integer>();
	
	
	/**
	 * Initialize or add to n-gram count of the given n-gram
	 * @param nGram the given n-gram as a string separated by simple spaces
	 */
	public void addNGram(String nGram){
		Integer freq= 1;
		String nGramLowerCase= nGram.toLowerCase();
		if(nGramFreqs.containsKey(nGramLowerCase)){
			freq= nGramFreqs.get(nGramLowerCase);
			freq++;
		}
		nGramFreqs.put(nGramLowerCase, freq);
	}
	
	/**
	 * Get absolute count of the given ngram
	 * @param nGram the given ngram
	 * @return absolute count or 0
	 */
	public int getNGramAbsFreq(String nGram){
		String nGramLowerCase= nGram.toLowerCase();
		if(nGramFreqs.containsKey(nGramLowerCase))
			return nGramFreqs.get(nGramLowerCase).intValue();
		else
			return 0;
	}
	
	/**
	 * Get relative count of the given ngram
	 * @param nGram the given ngram
	 * @return 0 or absolute ngram count divided by sum of all counts
	 */
	public double getNGramRelFreq(String nGram){
		String nGramLowerCase= nGram.toLowerCase();
		if(nGramFreqs.containsKey(nGramLowerCase)){
			double nGramFreq= (double) nGramFreqs.get(nGramLowerCase);
			double sum= 0;
			for (Integer freq: nGramFreqs.values()){
				sum= sum+ freq;
			}
			return nGramFreq/sum;
		}
		else
			return 0;
	}
	
	public int getNumberOfDistinctNGrams(){
		return nGramFreqs.size();
	}
	
	/**
	 * Get all distinct n-grams from this map.
	 * @return distinct nGrams
	 */
	public List<String> getNGrams(){
		return new ArrayList<String>(nGramFreqs.keySet());
	}

	/**
	 * Add given count to given n-gram's count
	 */
	public void addToNGram(String nGram, NeOccurrence neOccurrence) {
		String nGramLowerCase= nGram.toLowerCase();
		Integer freq= neOccurrence.getOccurrence();
		if(nGramFreqs.containsKey(nGramLowerCase)){
			freq= nGramFreqs.get(nGramLowerCase);
			freq+= neOccurrence.getOccurrence();
		}
		nGramFreqs.put(nGramLowerCase, freq);
		
	}
}
