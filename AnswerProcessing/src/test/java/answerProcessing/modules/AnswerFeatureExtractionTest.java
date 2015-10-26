package answerProcessing.modules;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jwatson.answer.Focuslist;
import jwatson.answer.Latlist;
import jwatson.answer.Qclasslist;
import jwatson.answer.Synonymlist;

import org.junit.Test;

import answerProcessing.types.Question;
import answerProcessing.types.Sentence;
import answerProcessing.types.QuestionInformation.Questiontype;

public class AnswerFeatureExtractionTest {

	@Test
	public void testContainsNeTypes() {
		List<Sentence> sents= TestUtil.setUpSentences("Bart is the oldest child of the Simpsons .");
		Sentence sentence= sents.get(0);
		String[] nes= new String[]{"PERSON", "O", "O", "O", "O", "O", "O", "PERSON", "O"};
		sentence= TestUtil.addNes(sentence, nes);
		
		Question question= new Question("Who is the Simpson's first born?");
		question.setQuestionType(new ArrayList<Qclasslist>(), new ArrayList<Latlist>(), new ArrayList<Focuslist>(), sents, question.getRawText());
		question.setQuestionType(Questiontype.person);
		
		int neMatch= AnswerFeatureExtraction.containsSpecificNEtypes(question, sentence);
		assertEquals(1, neMatch);
	}
	
	@Test
	public void testContainsNoNeTypes() {
		List<Sentence> sents= TestUtil.setUpSentences("In New York City , there are a lot of houses .");
		Sentence sentence= sents.get(0);
		String[] nes= new String[]{"O", "LOCATION", "LOCATION", "LOCATION", "O", "O", "O", "O", "O", "O", "O", "O"};
		sentence= TestUtil.addNes(sentence, nes);
		
		Question question= new Question("Who is the Simpson's first born?");
		question.setQuestionType(new ArrayList<Qclasslist>(), new ArrayList<Latlist>(), new ArrayList<Focuslist>(), sents, question.getRawText());
		question.setQuestionType(Questiontype.person);
		
		int noNeMatch= AnswerFeatureExtraction.containsSpecificNEtypes(question, sentence);
		assertEquals(0, noNeMatch);
	}
	
	@Test
	public void testRelSynonym() {
		List<Sentence> sents= TestUtil.setUpSentences("In Washington , there are a lot of locations , are there ?");
		Sentence sentence= sents.get(0);
		String[] lemma= new String[]{"in", "Washington", ",", "there", "be", "a", "lot", "of", "location", ",", "be", "there", "?"};
		sentence= TestUtil.addLemmata(sentence, lemma);
		
		Question question= new Question("Where is the White House located?");
		String questionQuery= question.getRawText();
		question.setQuestionType(new ArrayList<Qclasslist>(), new ArrayList<Latlist>(), new ArrayList<Focuslist>(), sents, questionQuery);
		// contains synonyms "be" for "is" and "location" for "located"
		List<Synonymlist> synonyms= TestUtil.getSynonymList(questionQuery);
		question.setSynonymList(synonyms);
		System.out.println(synonyms);
		
		double synRelMatch= AnswerFeatureExtraction.getRelSynonymFreq(question, sentence);
		assertTrue(2.0/3.0== synRelMatch);
	}
	
	
	@Test
	public void testContainsSynonym() {
		List<Sentence> sents= TestUtil.setUpSentences("In Washington , there are a lot of locations .");
		Sentence sentence= sents.get(0);
		String[] lemma= new String[]{"in", "Washington", ",", "there", "be", "a", "lot", "of", "location", "."};
		sentence= TestUtil.addLemmata(sentence, lemma);
		
		Question question= new Question("Where is the White House located?");
		String questionQuery= question.getRawText();
		question.setQuestionType(new ArrayList<Qclasslist>(), new ArrayList<Latlist>(), new ArrayList<Focuslist>(), sents, questionQuery);
		// contains synonyms "be" for "is" and "location" for "located"
		List<Synonymlist> synonyms= TestUtil.getSynonymList(questionQuery);
		question.setSynonymList(synonyms);
		System.out.println(synonyms);
		
		int synMatch= AnswerFeatureExtraction.containsSynonyms(question, sentence);
		assertEquals(1, synMatch);
		
		double synRelMatch= AnswerFeatureExtraction.getRelSynonymFreq(question, sentence);
		assertTrue(2.0/3.0== synRelMatch);
	}
	
	@Test
	public void testContainsNoSynonym() {
		List<Sentence> sents= TestUtil.setUpSentences("In Washington , there are a lot of locations .");
		Sentence sentence= sents.get(0);
		String[] lemma= new String[]{"in", "Washington", ",", "there", "is", "a", "lot", "of", "locations", "."};
		sentence= TestUtil.addLemmata(sentence, lemma);
		
		Question question= new Question("Where is the White House located?");
		String questionQuery= question.getRawText();
		question.setQuestionType(new ArrayList<Qclasslist>(), new ArrayList<Latlist>(), new ArrayList<Focuslist>(), sents, questionQuery);
		// contains synonyms "be" for "is" and "location" for "located"
		List<Synonymlist> synonyms= TestUtil.getSynonymList(questionQuery);
		question.setSynonymList(synonyms);
		System.out.println(synonyms);
		
		int synMatch= AnswerFeatureExtraction.containsSynonyms(question, sentence);
		assertEquals(0, synMatch);
		
		double synRelMatch= AnswerFeatureExtraction.getRelSynonymFreq(question, sentence);
		assertTrue(0.0== synRelMatch);
	}

}
