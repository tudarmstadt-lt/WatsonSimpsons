package answerProcessing.types;

public class Token {
	
	/*
	"index": "1",
    "word": "Marge",
    "lemma": "Marge",
    "characterOffsetBegin": "0",
    "characterOffsetEnd": "5",
    "pos": "NNP",
    "ner": "PERSON",
    "speaker": "PER0"
 */
	int index;
	int characterOffsetBegin, characterOffsetEnd;
	String word;
	String lemma;
	String pos;
	String ner;
	String speaker;
	
	public Token() {
		
	}
	
	public Token(int index, String word) {
		this.index = index;
		this.word = word;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getCharacterOffsetBegin() {
		return characterOffsetBegin;
	}
	public void setCharacterOffsetBegin(int characterOffsetBegin) {
		this.characterOffsetBegin = characterOffsetBegin;
	}
	public int getCharacterOffsetEnd() {
		return characterOffsetEnd;
	}
	public void setCharacterOffsetEnd(int characterOffsetEnd) {
		this.characterOffsetEnd = characterOffsetEnd;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getNer() {
		return ner;
	}
	public void setNer(String ner) {
		this.ner = ner;
	}
	public String getSpeaker() {
		return speaker;
	}
	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}
	
	@Override
	public String toString() {
		return "[Token "+index+"] "+word+" ("+lemma+", "+pos+", "+ner+")";
	}
    	
}
