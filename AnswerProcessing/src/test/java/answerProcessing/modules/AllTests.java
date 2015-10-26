package answerProcessing.modules;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AnswerExtractionTest.class, CharFreqExtractorTest.class,
		FreqMapTest.class, NGramDifferenceTest.class,
		TokenFreqExtractorTest.class, NumberConverterTest.class,
		UrlOrginalFileTest.class, QuestionTypeTest.class,
		AnswerFeatureExtractionTest.class, NEExtractionTest.class,
		AnswerExtractionTest.class, FilterNeTest.class, NGramFreqCounterTest.class})
public class AllTests {

}
