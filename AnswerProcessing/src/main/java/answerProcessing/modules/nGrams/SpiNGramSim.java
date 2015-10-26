package answerProcessing.modules.nGrams;

import java.util.List;

/**
 * Simple N-Gram Profile Intersection (Frantzeskou et al., 2006), normalized and used as dissimilarity measure
 *
 * G. Frantzeskou, E. Stamatatos, S. Gritzalis, and S. Katsikas, ''Effective Identification of Source Code
 * Authors Using Byte-level Information''. Proc. of the 28th Int. Conf on Software Engineering, 2006
 *
 */
public class SpiNGramSim implements NGramDistEvaluator {

	private static final String DESC= "SPI";
	/**
	 * Count number of n-grams that are in both frequency maps, normalize by size of the first map,
	 * subtract from one to transform into dissimilarity measure
	 */
	@Override
	public double evaluateDistance(NGramFrequencyMap freq1,
			NGramFrequencyMap freq2) {
		double overlap= 0.0;
		List<String> nGrams2= freq2.getNGrams();
		List<String> nGrams1= freq1.getNGrams();
		for(String nGram: nGrams1){
			if(nGrams2.contains(nGram))
				overlap++;
		}
		if(nGrams1.isEmpty())  //no overlap
			return 1.0;
		else{
			double intersection= overlap/(double)nGrams1.size();
			return 1- intersection;
		}
	}

	@Override
	public String getDescription() {
		return DESC;
	}

}
