package answerProcessing.modules.answerGen;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberConverter {
	
	private Map<String, Integer> adderNums;
	private Map<String, Integer> multiplierNums;

	public NumberConverter(){
		initNumberMaps();
	}
	
	private void initNumberMaps() {
		String[] adders= new String[] {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty"};
		String[] moreAdders= new String[]{"thirty", "fourty", "fifty", "sixty", "seventy", "eighty", "ninety"};
		String[] multipliers= new String[] {"hundred", "thousand", "million", "dozen"};
		Integer[] moreAddersVal= new Integer[]{30, 40, 50, 60 ,70, 80 ,90};
		Integer[] multipliersVal= new Integer[] {100, 1000, 1000000, 12};
		
		adderNums= new HashMap<String, Integer>();
		multiplierNums= new HashMap<String, Integer>();
		for(int i=0; i<adders.length; i++){
			adderNums.put(adders[i], Integer.valueOf(i));
		}
		for(int j=0; j<moreAdders.length; j++){
			adderNums.put(moreAdders[j], moreAddersVal[j]);
		}
		for(int k=0; k<multipliers.length; k++){
			multiplierNums.put(multipliers[k], multipliersVal[k]);
		}
	}
	
	/**
	 * Convert text representation of a number up to a billion into integer representation
	 * (e.g. fourty two to 42).
	 * Considers faulty strings only partially.
	 * @param numberStr written number
	 */
	public Integer convertWordToNumber(String numberStr) {
		if(numberStr== null)
			return null;
		String numStr= numberStr.toLowerCase();
		if(isNumeric(numStr))
			return Integer.parseInt(numStr);
		else{
			int sign= 1;
			Pattern minusRegex= Pattern.compile("^((minus\\s)|(-\\s)|(negative\\s)).*");
			Matcher matcher= minusRegex.matcher(numStr);
			if(matcher.matches()){
				numStr= numStr.replaceAll(matcher.group(1), "");
				sign= -1;
			}
			String[] numberParts= numStr.split("(\\sand\\s)|(\\s)|-");
			int numberLen= numberParts.length;
			Integer number= 0;
			Integer subTotal= 0;
			String numberPart;
			for(int i=0; i<numberLen; i++){
				numberPart= numberParts[i];
				if(adderNums.containsKey(numberPart)){
					subTotal= subTotal+ adderNums.get(numberPart);
				}else 
					if(multiplierNums.containsKey(numberPart)){
						subTotal= (subTotal==0)? multiplierNums.get(numberPart): subTotal* multiplierNums.get(numberPart);
						number= number+ subTotal;
						subTotal= 0;
					}else
						return null;
			}
			number= number+ subTotal;
			number= number* sign;
			return number;					
		}
	}
	
	public boolean isNumeric(String numStr) {
		try {
			Integer.parseInt(numStr);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
