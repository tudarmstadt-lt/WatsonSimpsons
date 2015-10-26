package answerProcessing.modules;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import answerProcessing.EntityCollection;
import answerProcessing.QuizPipeline;
import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Question;
import answerProcessing.types.QuestionInformation.Questiontype;
import answerProcessing.types.QuizAnswer;
import jwatson.answer.WatsonAnswer;
import utils.Watson;

import static org.junit.Assert.assertEquals;

public class QuestionTypeTest {
	Question q;
	List<PossibleAnswer> answers;
	private final static int numberOfSelectedAnswersForPipeline = 20;

	private void askWatson(String questionQuery) throws IOException {
		EntityCollection.initEntityInfos();
		WatsonAnswer wAnswer = Watson.retrieveWatsonAnswer(questionQuery);
		q = new Question(questionQuery);
		answers = Watson.getPossibleAnswers(q, wAnswer,
				numberOfSelectedAnswersForPipeline);
		List<QuizAnswer> allAnswers = QuizPipeline.executePipeline(q, answers,
				Watson.getRemoteNLPUrl(), 30);
		System.out.println(q.getQuestionInformation().getqClassList());
		System.out.println(q.getQuestionInformation().getFocusList());
		System.out.println(q.getQuestionInformation().getLatList());
		System.out.println(q.getQuestionType());
		System.out.println(q.getQuestionInformation().getNEtype());

		for (QuizAnswer quizAnswer : allAnswers) {
			
			System.out.println(quizAnswer.getSentenceScore("default"));
			System.out.println(quizAnswer.getTitle());
			System.out.print(quizAnswer.getOccurrence()+"x");
			System.out.println(quizAnswer.getAnswer()+":");
			System.out.print(quizAnswer.getSentence());
		}
	}

	@Test
	public void personTest() throws IOException {
		askWatson("Who is Mr. Burns son?");
		assertEquals(Questiontype.malePerson, q.getQuestionType());
		askWatson("Who owned a dog named Chewbacca?");
		assertEquals(Questiontype.person, q.getQuestionType());
		askWatson("To whom does Richie Sakai dedicate his karaoke song?");
		assertEquals(Questiontype.person, q.getQuestionType());
		askWatson("How does Homer call himself on his webpage in The Computer Wore Menace Shoes ?");
		assertEquals(Questiontype.person, q.getQuestionType());
	}

	@Test
	public void unknownTest() throws IOException {
		askWatson("What job does Richie Sakai have?");
		assertEquals(Questiontype.unknown, q.getQuestionType());
	}

	@Test
	public void locationTest1() throws IOException {
		askWatson("What is Ned Flanders address?");
		assertEquals(Questiontype.location, q.getQuestionType());
	}

	@Test
	public void locationTest2() throws IOException {
		askWatson("What did a hurricane destroy in Springfield?");
		assertEquals(Questiontype.location_organization, q.getQuestionType());
		askWatson("Where does Mr. Burns live?");
		assertEquals(Questiontype.location_organization, q.getQuestionType());
		askWatson("Where does Ned Flanders live?");
		assertEquals(Questiontype.location_organization, q.getQuestionType());
	}

	@Test
	public void durationTest1() throws IOException {
		askWatson("How old is Lisa Simpson?");
		assertEquals(Questiontype.duration, q.getQuestionType());
	}

	// @Test
	public void durationTest2() throws IOException {
		askWatson("What is Homer Simpson's age?");
		assertEquals(Questiontype.duration, q.getQuestionType());
	}

	@Test
	public void dateTest1() throws IOException {
		askWatson("When is Lisa's birthday?");
		assertEquals(Questiontype.date, q.getQuestionType());
	}

	@Test
	public void dateTest2() throws IOException {
		askWatson("When was the first Simpsons Episode aired");
		assertEquals(Questiontype.date, q.getQuestionType());
	}

	@Test
	public void femalePersonTest1() throws IOException {
		askWatson("Who is Bart's first girlfriend?");
		assertEquals(Questiontype.femalePerson, q.getQuestionType());
	}

	@Test
	public void femalePersonTest2() throws IOException {

		askWatson("What is the name of Mr. Burns' mother");
		assertEquals(Questiontype.femalePerson, q.getQuestionType());
	}

	@Test
	public void malePersonTest1() throws IOException {
		askWatson("Who is Lisa's first boyfriend?");
		assertEquals(Questiontype.malePerson, q.getQuestionType());
	}

	@Test
	public void malePersonTest2() throws IOException {
		askWatson("What is the name of Mr. Burns son");
		assertEquals(Questiontype.malePerson, q.getQuestionType());
	}

	@Test
	public void personTest1() throws IOException {
		askWatson("Who is Lisa's teacher?");
		assertEquals(Questiontype.person, q.getQuestionType());
	}

	@Test
	public void personTest2() throws IOException {
		askWatson("What is Bart's graffiti sprayer alias?");
		assertEquals(Questiontype.person, q.getQuestionType());
	}

	@Test
	public void animalNETest1() throws IOException {
		askWatson("Which racehorse was rescued by Bart Simpson?");
		assertEquals(Questiontype.animalNE, q.getQuestionType());
	}

	@Test
	public void animalNETest2() throws IOException {
		askWatson("Whose dog is very fast?");
		assertEquals(Questiontype.person, q.getQuestionType());
	}

	// @Test
	// public void animalNETest3() throws IOException {
	// askWatson("Which animal is Itchy?");
	// assertEquals(Questiontype.animal,q.getQuestionType());
	// }
	@Test
	public void animalNETest4() throws IOException {
		askWatson("Who has an elephant as a pet?");
		assertEquals(Questiontype.person, q.getQuestionType());
	}

	@Test
	public void numberTest1() throws IOException {
		askWatson("How many puppies did Santa's Little Helper's mate give birth to?");
		assertEquals(Questiontype.number, q.getQuestionType());
	}

	@Test
	public void numberTest2() throws IOException {
		askWatson("What is Professor Frink's IQ?");
		assertEquals(Questiontype.number, q.getQuestionType());
	}

	@Test
	public void season() throws IOException {
		askWatson("Which season started in September 10 2006 with The Mook, the Chef, the Wife and Her Homer?");
		assertEquals(Questiontype.season, q.getQuestionType());
	}

	@Test
	public void familyTest() throws IOException {
		askWatson("Which family live in Evergreen Terrace 734?");
		assertEquals(Questiontype.family, q.getQuestionType());
	}

	@Test
	public void numberTest() throws IOException {
		askWatson("What year did Jebediah Springfield found Springfield?");
		assertEquals(Questiontype.date, q.getQuestionType());
	}

	@Test
	public void dateTest() throws IOException {
		askWatson("When did Homer bring Santa's Little Helper home?");
		assertEquals(Questiontype.date, q.getQuestionType());
	}

	@Test
	public void episodeTest() throws IOException {
		askWatson("In which episode did Maude die?");
		assertEquals(Questiontype.episode, q.getQuestionType());
		askWatson("In which season did Maude die?");
		assertEquals(Questiontype.season, q.getQuestionType());
	}

	@Test
	public void REGEXNERTest() throws IOException {
		askWatson("How is the three-eyed fish called?");
		assertEquals(Questiontype.animalNE, q.getQuestionType());
		askWatson("How is Homer's wife called?");
		assertEquals(Questiontype.femalePerson, q.getQuestionType());
		askWatson("Who is Homer's Vegas wife?");
		assertEquals(Questiontype.femalePerson, q.getQuestionType());
	}

	@Test
	public void episodeTest2() throws IOException {
		askWatson("In which episode did Krusty fake his death and assume a new identity?");
		assertEquals(Questiontype.episode, q.getQuestionType());
	}

	@Test
	public void catladyTest() throws IOException {
		askWatson("What is the name of the cat lady?");
		assertEquals(Questiontype.femalePerson, q.getQuestionType());
	}

	@Test
	public void elHomoTest() throws IOException {
		askWatson("What is Homer's graphiti sprayer alias?");
		assertEquals(Questiontype.person, q.getQuestionType());
	}

	@Test
	public void countryTest() throws IOException {
		askWatson("From which country does Apu come?");
		assertEquals(Questiontype.location, q.getQuestionType());
	}

	@Test
	public void shopTest() throws IOException {
		askWatson("What is the name of Ned Flanders shop?");
		assertEquals(Questiontype.organization, q.getQuestionType());
	}

	@Test
	public void schoolTest() throws IOException {
		askWatson("In which school works Willie?");
		assertEquals(Questiontype.organization, q.getQuestionType());
	}

	@Test
	public void dateTest3() throws IOException {
		askWatson("On which date does the Simpsons get their dog?");
		assertEquals(Questiontype.date, q.getQuestionType());
		askWatson("What is the name of Simpson's dog?");
		assertEquals(Questiontype.animalNE, q.getQuestionType());
	}

	@Test
	public void mineyTest() throws IOException {
		askWatson("How much does a MyPhone cost?");
		assertEquals(Questiontype.money, q.getQuestionType());
		askWatson("What does Lisa's MyBill cost?");
		assertEquals(Questiontype.money, q.getQuestionType());
	}

	@Test
	public void colorTest() throws IOException {
		askWatson("What is the real color of Marge's hair?");
		assertEquals(Questiontype.color, q.getQuestionType());
		askWatson("Which hair color has Krusty the clown?");
		assertEquals(Questiontype.color, q.getQuestionType());

	}

	@Test
	public void heightTest() throws IOException {
		askWatson("How tall is Lard Lad Donuts mascot?");
		assertEquals(Questiontype.distance, q.getQuestionType());
	}

	@Test
	public void mrXTest() throws IOException {
		askWatson("How does Homer call himself on his webpage in The Computer Wore Menace Shoes ?");
		assertEquals(Questiontype.person, q.getQuestionType());
	}

}
