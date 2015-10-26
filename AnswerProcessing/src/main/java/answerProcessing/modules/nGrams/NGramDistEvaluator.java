package answerProcessing.modules.nGrams;

public interface NGramDistEvaluator {

	/**
	 * Compare two collections of n-gram frequencies
	 * @param freq1 map of frequencies the others are compared to
	 * @param freq2 the other frequencies
	 * @return distance
	 */
	public double evaluateDistance(NGramFrequencyMap freq1, NGramFrequencyMap freq2);

	/**
	 * Get description for type of distance measure
	 * @return string description
	 */
	public String getDescription();
}
