package answerProcessing.modules;

import static org.junit.Assert.*;

import org.junit.Test;

import answerProcessing.modules.answerGen.NumberConverter;

public class NumberConverterTest {

	
	@Test
	public void testSmallNumbers() {
		String[] numStr= new String[] {"One Dozen and Two", "zero", "five", "fourteen", "one dozen"};
		Integer[] expectedNum= new Integer[] {14, 0, 5, 14, 12};
		NumberConverter converter= new NumberConverter();
		
		Integer generatedNum;
		for(int i=0; i<numStr.length; i++){
			generatedNum= converter.convertWordToNumber(numStr[i]);
			assertEquals(expectedNum[i], generatedNum);
		}	
	}
	
	@Test
	public void testBigNumbers() {
		String[] numStr= new String[] {"thousand", "twenty four thousand three hundred one", "one hundred twenty four", "five hundred", "four thousand twenty", "sixty two"};
		Integer[] expectedNum= new Integer[] {1000, 24301, 124, 500, 4020, 62};
		NumberConverter converter= new NumberConverter();
		
		Integer generatedNum;
		for(int i=0; i<numStr.length; i++){
			generatedNum= converter.convertWordToNumber(numStr[i]);
			assertEquals(expectedNum[i], generatedNum);
		}
	}
	
	@Test
	public void testNegative() {
		String[] numStr= new String[] {"minus one", "- seven", "negative fourteen thousand"};
		Integer[] expectedNum= new Integer[] {-1, -7, -14000};
		NumberConverter converter= new NumberConverter();
		
		Integer generatedNum;
		for(int i=0; i<numStr.length; i++){
			generatedNum= converter.convertWordToNumber(numStr[i]);
			assertEquals(expectedNum[i], generatedNum);
		}
	}
	
	@Test
	public void testFaultyStrings() {
		String[] numStr= new String[] {"62-years", "onethousand", "athousand"};
		NumberConverter converter= new NumberConverter();
		Integer generatedNum;
		for(String faulty: numStr){
			generatedNum= converter.convertWordToNumber(faulty);
			assertEquals(null, generatedNum);	
		}
	}

}
