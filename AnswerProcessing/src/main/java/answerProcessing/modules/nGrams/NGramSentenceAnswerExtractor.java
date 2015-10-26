package answerProcessing.modules.nGrams;


import java.util.ArrayList;
import java.util.List;

import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Question;
import answerProcessing.types.Sentence;
import answerProcessing.types.Text;


public class NGramSentenceAnswerExtractor {

	protected List<NGramFreqCounter> freqCounters= new ArrayList<NGramFreqCounter>();
	protected NGramDistEvaluator distEval;
	
	
	
	public NGramSentenceAnswerExtractor(List<NGramFreqCounter> counters, NGramDistEvaluator eval){
		freqCounters= counters;
		distEval= eval;
	}
	
	public NGramSentenceAnswerExtractor(NGramFreqCounter counter, NGramDistEvaluator eval){
		freqCounters.add(counter);
		distEval= eval;
	}
	
	public void addFreqCounter(NGramFreqCounter counter){
		freqCounters.add(counter);
	}
	
	/**
	 * Assign scores for similarity in n-gram frequency to the given question to every sentence in the possible answer.
	 */
	public void annotate(PossibleAnswer possibleAnswer) {
		// count n-gram frequencies of question text
		Question question= possibleAnswer.getQuestion();
		Text text= possibleAnswer.getAnnotatedText();
		
		NGramFrequencyMap questionFreq;
		for(NGramFreqCounter freqCounter: freqCounters){
			questionFreq= freqCounter.extractFreqs(question.getAnnotatedText());
			
			NGramFrequencyMap answerFreq;
			Double distance;
			for(Sentence sentence: text.getSentences()) {
				// count n-gram frequencies for every sentence in the answer-text
				answerFreq= freqCounter.extractFreqs(sentence);
				// compare to question frequencies
				distance= distEval.evaluateDistance(questionFreq, answerFreq);
				// store distance in sentence
				sentence.addNgramDistance(extractDescription(freqCounter), distance);
			}
		}
	}

	public String extractDescription(NGramFreqCounter freqCounter) {
		return freqCounter.getDescription()+"_"+ distEval.getDescription();
	}
	
	/**
	 * Gets descriptions of used methods (ngram types and distance measures)
	 * @return list of descriptions
	 */
	public List<String> getMethodDescriptions(){
		List<String> methods= new ArrayList<String>();
		for(NGramFreqCounter counter: freqCounters){
			methods.add(extractDescription(counter));
		}
		return methods;
	}

}
