package answerProcessing.modules;

import java.util.ArrayList;
import java.util.List;

import utils.Watson;
import jwatson.answer.Synonymlist;
import jwatson.answer.WatsonAnswer;
import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Question;
import answerProcessing.types.Sentence;
import answerProcessing.types.Text;
import answerProcessing.types.Token;

public class TestUtil {

	/**
	 * Set up list of Sentence of Token from given string,
	 * does not split punctuation like StanfordCoreNLP should
	 */
	//not all parameters set or tested
	public static List<Sentence> setUpSentences(String textStr) {
		List<Sentence> sentences= new ArrayList<Sentence>();
		int tokenBegin= 0;
		int sentBegin= 0;
		int charIndex=0;
		int sentCount= 0;
		int tokenCount;
		int textLen= textStr.length();
		while(charIndex<textLen){
			List<Token> tokens= new ArrayList<Token>();
			tokenCount=0;
			while(charIndex<textLen&& textStr.charAt(charIndex)!= '.'){
				if(textStr.charAt(charIndex)== ' '){
					if(textStr.charAt(charIndex-1)!= '.'){
						addToken(textStr, tokenBegin, charIndex, tokenCount,
								tokens);
						tokenBegin= charIndex+1;
						tokenCount++;
					}else
						tokenBegin++;
				}
				charIndex++;
			}
			//add last token
			int end= Math.min(charIndex+1, textLen);
			addToken(textStr, tokenBegin, end, tokenCount, tokens);
			tokenBegin=charIndex+1;
			//add sentence 
			addSentence(textStr, sentences, sentBegin, sentCount, tokens, end);
			
			sentCount++;
			sentBegin= charIndex+2;
			charIndex++;
		}
		
		return sentences;
	}

	/**
	 * Create new Sentence from given information and text and add it to given list of sentences
	 * @param textStr text that contains this sentence at given indices
	 * @param sentences list of sentences to which this sentence is added
	 * @param sentBegin sentence index
	 * @param sentCount sentence index in over-arching text
	 * @param tokens list of tokens for this sentence
	 * @param sentEnd sentence index
	 */
	protected static void addSentence(String textStr, List<Sentence> sentences,
			int sentBegin, int sentCount, List<Token> tokens, int sentEnd) {
		Sentence sent;
		sent= new Sentence();
		sent.setTokens(tokens);
		sent.setIndex(sentCount);
		String sentenceStr= textStr.substring(sentBegin, sentEnd);
		sent.setRawText(sentenceStr);
		sentences.add(sent);
	}

	/**
	 * Create new Token from given information and text and add it to given list of tokens
	 * @param textStr text which contains the token at indices tokenBegin and tokenEnd
	 * @param tokenBegin token index
	 * @param tokenEnd token index
	 * @param tokenCount token index in over-arching sentence
	 * @param tokens list of tokens this one is added to
	 */
	protected static void addToken(String textStr, int tokenBegin,
			int tokenEnd, int tokenCount, List<Token> tokens) {
		Token token;
		token= new Token();
		token.setCharacterOffsetBegin(tokenBegin);
		token.setCharacterOffsetEnd(tokenEnd);  
		token.setIndex(tokenCount);
		String word= textStr.substring(tokenBegin, tokenEnd);
		token.setWord(word);
		tokens.add(token);
	}
	
	/**
	 * Set up possibleAnswer object with text objects from given question and answer strings
	 */
	public static PossibleAnswer setUpPossibleAnswer(String questionStr,
			String textStr) {
		List<Sentence> sents= TestUtil.setUpSentences(textStr);
		Text text= new Text(sents);
		Text questionText= new Text(TestUtil.setUpSentences(questionStr));
		Question question= new Question(questionStr);
		question.setAnnotatedText(questionText);
		PossibleAnswer possibleAnswer= new PossibleAnswer(question, textStr);
		possibleAnswer.setAnnotatedText(text);
		
		return possibleAnswer;
	}

	/**
	 * Add given NE-types to the tokens in given sentence
	 */
	public static Sentence addNes(Sentence sent, String[] nes) {
		List<Token> tokens= sent.getTokens();
		Token currentToken;
		for(int i=0; i<nes.length; i++){
			currentToken= tokens.get(i);
			currentToken.setNer(nes[i]);
		}
		return sent;
	}
	
	/**
	 * Add given NE-types to the tokens in given sentences
	 */
	public static List<Sentence> addAllTheNes(List<Sentence> sents, String[][] nes) {
		Sentence currentSent;
		for(int i=0; i<sents.size(); i++){
			currentSent= sents.get(i);
			currentSent= addNes(currentSent, nes[i]);
		}
		return sents;
	}
	
	/**
	 * Currently, this sets the following token params: index, word, pos
	 * @param tokenContent list of token contents
	 * @param text raw sentence text
	 */
	public static Sentence createSentenceFromTokens(String[][] tokens, String text){
		Sentence sent= new Sentence();
		List<Token> newTokens= new ArrayList<Token>();
		for(String[] tokenContent: tokens){
			Token token= new Token(Integer.parseInt(tokenContent[0]), tokenContent[1]);
			token.setPos(tokenContent[2]);
			newTokens.add(token);
		}
		sent.setTokens(newTokens);
		sent.setRawText(text);
		return sent;
	}

	/**
	 * get synonymlist via watson
	 */
	public static List<Synonymlist> getSynonymList(String questionQuery) {
		WatsonAnswer wanswer= Watson.retrieveWatsonAnswer(questionQuery);
		return wanswer.getAnswerInformation().getSynonymList();
	}

	/**
	 * Add given lemmata to the given sentence
	 * @return the sentence
	 */
	public static Sentence addLemmata(Sentence sent, String[] lemma) {
		List<Token> tokens= sent.getTokens();
		Token currentToken;
		for(int i=0; i<lemma.length; i++){
			currentToken= tokens.get(i);
			currentToken.setLemma(lemma[i]);
		}
		return sent;
	}
}
