package answerProcessing.modules;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import answerProcessing.EntityCollection;
import answerProcessing.QuizPipeline;
import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Question;
import jwatson.answer.WatsonAnswer;
import utils.Watson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UrlOrginalFileTest {

	Question q;
	List<PossibleAnswer> answers;
	private final static int numberOfSelectedAnswersForPipeline = 15;

	@BeforeClass
	public static void setUp() throws Exception {
		EntityCollection.initEntityInfos();
	}

	private void askWatson(String questionQuery) throws IOException {
		WatsonAnswer wAnswer = Watson.retrieveWatsonAnswer(questionQuery);
		q = new Question(questionQuery);
		answers = Watson.getPossibleAnswers(q, wAnswer,
				numberOfSelectedAnswersForPipeline);
		QuizPipeline .executePipeline(q, answers, Watson.getRemoteNLPUrl(), 20);

	}

	@Test
	public void test() throws IOException {
		askWatson("Who is peeping Mom?");
		assertEquals("http://simpsons.wikia.com/wiki/Peeping_Mom",answers.get(0).getOriginalfile());
		assertEquals("http://simpsons.wikia.com/wiki/Peeping_Mom",answers.get(1).getOriginalfile());
		assertEquals("http://simpsons.wikia.com/wiki/Peeping_Mom",answers.get(2).getOriginalfile());
		assertEquals("http://simpsons.wikia.com/wiki/Dolph_Starbeam",answers.get(3).getOriginalfile());
		assertEquals("http://simpsons.wikia.com/wiki/Maggie_Simpson",answers.get(4).getOriginalfile());
	

		for (int i = 0; i < 588; i++) {

			assertTrue(EntityCollection.getUrls_episodes().get(i).startsWith(
					"http://simpsons.wikia.com/wiki/"));
		}

		for (int key : EntityCollection.getUrls().keySet()) {
			assertTrue(EntityCollection.getUrls().get(key).startsWith(
					"http://simpsons.wikia.com/wiki/"));

		}
		askWatson("Who is Bart Simpson?");
		assertEquals("http://simpsons.wikia.com/wiki/Bart_Simpson",answers.get(0).getOriginalfile());
		askWatson("Who is Charles Montgomery Burns?");
		assertEquals("http://simpsons.wikia.com/wiki/Charles_Montgomery_Burns",answers.get(0).getOriginalfile());
		
	}
}