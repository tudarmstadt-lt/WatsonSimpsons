package answerProcessing.types;

import java.util.List;

import answerProcessing.types.QuestionInformation.Questiontype;
import jwatson.answer.Focuslist;
import jwatson.answer.Latlist;
import jwatson.answer.Qclasslist;
import jwatson.answer.Synonymlist;

public class Question {

	Text annotatedText;
	String rawText;
	List<Synonymlist> synonymList;
	// determines question type e.g. PERSON
	QuestionInformation questionInformation;

	
	public Question(String rawText) {
		super();
		this.rawText = rawText;
		this.annotatedText = null;
	}

	public QuestionInformation getQuestionInformation() {
		return questionInformation;
	}

	public void setQuestionType(List<Qclasslist> qClassList, List<Latlist> latList,
			List<Focuslist> focusList, List<Sentence> sentences, String questionText) {
		this.questionInformation = new QuestionInformation(qClassList, latList,  focusList, sentences, questionText);
	}
	
	public Text getAnnotatedText() {
		return annotatedText;
	}

	public void setAnnotatedText(Text annotatedText) {
		this.annotatedText = annotatedText;
	}

	public String getRawText() {
		return rawText;
	}

	public void setRawText(String rawText) {
		this.rawText = rawText;
	}


	public List<Synonymlist> getSynonymList() {
		return synonymList;
	}

	public void setSynonymList(List<Synonymlist> synonymList) {
		this.synonymList = synonymList;
	}

	@Override
	public String toString() {
		return getRawText();
	}

	public Questiontype getQuestionType() {		
		return questionInformation.computeQuestionType();
	}

	public void setQuestionType(Questiontype type) {
		questionInformation.setQuestionType(type);
	}


}
