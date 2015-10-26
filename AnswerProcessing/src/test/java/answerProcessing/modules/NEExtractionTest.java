package answerProcessing.modules;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import answerProcessing.types.Sentence;
import answerProcessing.types.Text;
import answerProcessing.utils.RemoteNLP;
import utils.Watson;

import static org.junit.Assert.assertEquals;

public class NEExtractionTest {
	/**
	 * extract NEs from a text, which match a question type
	 * @param sentence
	 * @param neTypes
	 * @return extracted NEs
	 * @throws IOException
	 */
	private List<String> ner(String sentence, ArrayList<String> neTypes)
			throws IOException {
		RemoteNLP.setUrlNlpServer(Watson.getRemoteNLPUrl());
		// annotateText
		Text annotatedText = RemoteNLP.annotate(sentence);
		Sentence annotatedSentence = annotatedText.getSentences().get(0);
		return NamedEntityProcessing.getNamedEntities(annotatedSentence,
				neTypes, true);
	}

	@Test
	public void neExtractionTest() throws IOException {
		// neTypes
		
		//questionType Location or Organization
		ArrayList<String> neTypesForLocations = new ArrayList<>();
		neTypesForLocations.add("LOCATION");
		neTypesForLocations.add("PERSON");
		neTypesForLocations.add("MISC");
		neTypesForLocations.add("ORGANIZATION");
		neTypesForLocations.add("NUMBER");
		neTypesForLocations.add("ORDINAL");
		neTypesForLocations.add("DATE");
		neTypesForLocations.add("PARTOFPERSON");
		neTypesForLocations.add("PARTOFLOCATION");
		neTypesForLocations.add("BINDING");
		neTypesForLocations.add("APOSTROPHE");
		neTypesForLocations.add("AND");
		neTypesForLocations.add("MONEY");

		//questionType Person
		ArrayList<String> neTypesForPersons = new ArrayList<>();
		neTypesForPersons.add("PERSON");
		neTypesForPersons.add("PARTOFPERSON");
		neTypesForPersons.add("MISC");
		neTypesForPersons.add("APOSTROPHE");

		//questionType Number
		ArrayList<String> neTypesForNumbers = new ArrayList<>();
		neTypesForNumbers.add("NUMBER");

		//questionType color
		ArrayList<String> neTypesForColor = new ArrayList<>();
		neTypesForColor.add("COLOR");

		//questionType weight
		ArrayList<String> neTypesForWeight = new ArrayList<>();
		neTypesForWeight.add("NUMBER");
		neTypesForWeight.add("WEIGHT");

		//questionType distance
		ArrayList<String> neTypesForDistance = new ArrayList<>();
		neTypesForDistance.add("NUMBER");
		neTypesForDistance.add("DISTANCE");

		assertEquals("Santa's Little Helper",
				ner("Santa's Little Helper.", neTypesForPersons).get(0));
		
		assertEquals("Ned Flanders' home",
				ner("Ned Flanders' home.", neTypesForLocations).get(0));
		assertEquals("Ned Flanders'",
				ner("Ned Flanders' home.", neTypesForPersons).get(0));

		assertEquals(
				"The Android's Dungeon & Baseball Card Shop",
				ner("The Android's Dungeon & Baseball Card Shop.",
						neTypesForLocations).get(0));

		String sentence = "Nedward Flanders, Jr. lives at 740 Evergreen Terrace.";
		assertEquals("740 Evergreen Terrace",
				ner(sentence, neTypesForLocations).get(0));
		assertEquals("740", ner(sentence, neTypesForNumbers).get(0));
		assertEquals("Nedward Flanders , Jr.", ner(sentence, neTypesForPersons)
				.get(0));

		sentence = "Marge has blue hair";
		assertEquals("Marge", ner(sentence, neTypesForPersons).get(0));
		assertEquals("blue", ner(sentence, neTypesForColor).get(0));

		sentence = "Any employee that weighs 300 pounds  or more qualifies as disabled";
		assertEquals("300", ner(sentence, neTypesForNumbers).get(0));
		assertEquals("300 pounds", ner(sentence, neTypesForWeight).get(0));

		sentence = "It's generally an estimated 3 meter tall donut store with an estimated 8 meter tall mascot of a young boy proudly holding a donut.";
		assertEquals("3 meter", ner(sentence, neTypesForDistance).get(0));
		assertEquals("8 meter", ner(sentence, neTypesForDistance).get(1));
	}

}
