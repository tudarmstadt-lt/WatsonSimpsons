package answerProcessing.modules.answerGen;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class DateGenerator {
	NumberConverter nc = new NumberConverter();
	DateFormatSymbols dfs = new DateFormatSymbols(Locale.US);
	String[] dates = new String[] { "Spring", "Eastern", "Christmas",
			"Winter", "Summer", "Autumn", "Fall", "Thanksgiving", "New Year","Silvester","Christmas Eve" };

	/**
	 * generates a random Date-String using a DateType given the best quiz answer 
	 * @param correctDate
	 * best quiz answer
	 * @return a random date
	 */
	public String generateDate(String correctDate) {
		int value = 1977;
		String type = getDateType(correctDate);
		switch (type) {
		case "DATE":
			return randomDOB();
		case "YEAR":
			value = nc.convertWordToNumber(correctDate);
			return randomYear(value);
		case "WEEKDAY":
			int rand = random(0, 9);
			if (rand > 7)
				generateDate(1977);
			else
				return randomWeekday();
		case "MONTH":
			 rand = random(0, 10);
				if (rand > 8)
					return generateDate(1977);
				else randomMonth();
		case "SEASON": {
		    rand = random(0, 10);
			if (rand > 8)
				return generateDate(1977);
			else
				return dates[random(0, 8)];
		}
		}
		return generateDate(1977);
	}
/**
 * DateType given the best quiz answer
 * @param correctDate
 * best quiz answer
 * @return DateType
 */
	private String getDateType(String correctDate) {
		String type = "UNKNOWN";
		if (nc.isNumeric(correctDate)) {
			return type = "YEAR";
		}
		if (correctDate== null) {
			return type;
		}
		for (int i = 0; i < 10; i++)
			if (correctDate.contains("" + i))
				return type = "DATE";
		for (int i = 0; i < 7; i++)
			if (dfs.getWeekdays()[i].contains(correctDate))
				return type = "WEEKDAY";
		for (int i = 0; i < 12; i++)
			if (dfs.getMonths()[i].contains(correctDate))
				return type = "MONTH";
		for (int i = 0; i < 9; i++) {
			if (dates[i].contains(correctDate))
				return type = "SEASON";
		}
		return type;
	}
	/**
	 * generates random date without using a date type
	 * @param year
	 * @return a random date
	 */
	public String generateDate(int year) {
		int rand = random(0, 4);
		switch (rand) {
		case 0:
			return randomDOB();
		case 1:
			return randomYear(year);
		case 2:
			return randomWeekday();
		case 3:
			return randomMonth();
		case 4:
			return dates[random(0, 8)];
		}
		return null;
	}

	private String randomMonth() {
		int month = random(0, 11);
		String[] months = dfs.getMonths();
		return months[month];
	}

	private String randomWeekday() {
		int day = random(0, 6);
		String[] days = dfs.getWeekdays();
		return days[day];
	}

	private String randomDOB() {

		int year = random(1900, 2015);
		int month = random(0, 11);
		int day = 0; 

		switch (month) {
		case 1:
			if (isLeapYear(year)) {
				day = random(1, 29);
			} else {
				day = random(1, 28);
			}
			break;

		case 0:
		case 2:
		case 4:
		case 6:
		case 7:
		case 9:
		case 11:
			day = random(1, 31);
			break;

		default:
			day = random(1, 30);
			break;
		}

		String[] months = dfs.getMonths();

		return months[month] + " " + getDayOfMonthSuffix(day);
	}

	public String randomYear(int value) {
		int delta = 2015 - value;
		int year = random(value - delta / 2 -10, value + delta / 2 +10);
		return "" + year;
	}

	public int random(int lowerBound, int upperBound) {
		return (lowerBound + (int) Math.round(Math.random()
				* (upperBound - lowerBound)));
	}

	private boolean isLeapYear(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		int noOfDays = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);

		if (noOfDays > 365) {
			return true;
		}

		return false;

	}

	private String getDayOfMonthSuffix(final int n) {
		if (n >= 11 && n <= 13) {
			return n + "th";
		}
		switch (n % 10) {
		case 1:
			return n + "st";
		case 2:
			return n + "nd";
		case 3:
			return n + "rd";
		default:
			return n + "th";
		}
	}
}
