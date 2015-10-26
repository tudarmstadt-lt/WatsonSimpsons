package answerProcessing.modules.nGrams;



public class NGramDifference implements NGramDistEvaluator {
	
	private static final String DESC= "SUMMED_DIFF";

	/**
	 * Evaluate distance of two collections of n-gram frequencies as their summed difference
	 * of n-grams contained in the first collection
	 */
	public double evaluateDistance(NGramFrequencyMap freq1,
			NGramFrequencyMap freq2) {
		double summedDiff= 0;
		double diff, relFreq1, relFreq2;
				
		for(String nGram: freq1.getNGrams()){
			relFreq1= freq1.getNGramRelFreq(nGram);
			relFreq2= freq2.getNGramRelFreq(nGram);
			// absolute value of difference of relative frequencies
			diff= Math.abs(relFreq1- relFreq2);
			summedDiff= summedDiff+ diff;
		}
		return summedDiff;
	}

	@Override
	public String getDescription() {
		return DESC;
	}

}
