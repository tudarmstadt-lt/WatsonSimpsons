package answerProcessing.types;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import answerProcessing.EntityCollection;

public class QuizAnswer {

	private String answer;
	private double score;
	private Sentence sentence;
	private int occurrence;
	private List<String> NeTypes = new ArrayList<String>();
	private Image image;

	public QuizAnswer(String answer, Sentence sentence) {
		super();
		//Episode or Season as Question type
		//NEtypes is empty
		this.answer = answer;
		if (sentence != null) {
			this.score = sentence.getScore("default");
			this.sentence = sentence;
			this.occurrence = 1;
		}
	}

	public QuizAnswer(String answer, Double score, NeOccurrence neOccurrence) {
		super();
		//we have concatenate tokens with " "
		//delete white spaces for " '" and " ,"
		this.answer = answer
				//.replace("of ' ", "of '")
				//.replace("and ' ", "and '").replace("& ' ", "& '")
				//.replace("or ' ", "or '").replace(" ' ", "' ")
				//.replace(" , ", ", ").replace(" 's", "'s")
				;
		this.score = score;
		this.sentence = neOccurrence.getSentence();
		this.occurrence = neOccurrence.getOccurrence();
		this.NeTypes = neOccurrence.getNeTypes();
	}
 
	public QuizAnswer(Sentence bestSentence) {
		super();
		//set title as answer
		//no shorter Answer/ NE extraction
		//best answer is sentence
		this.answer = bestSentence.getParentText().getTitle()
				.replaceFirst("\\[\\d+\\]", "").split(" : ")[0].trim();
		this.score = bestSentence.getScore("default");
		this.sentence = bestSentence;
		this.NeTypes.add("UNKNOWN");
		this.occurrence = 1;
	}

	public QuizAnswer(String answer, double score, List<String> NeTypes) {
		// generated (random) answer
		this.answer = answer;
		this.score = score;
		this.NeTypes=NeTypes;
		this.NeTypes.add("GENERATED");
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getSentence() {
		if (sentence != null)
			return sentence.getRawText();
		return "";
	}

	public String getURL() {
		if (sentence != null)
			return sentence.getParentText().getOriginalfile();
		return "";
	}

	public String getTitle() {
		if (sentence != null)
			return sentence.getParentText().getTitle()
					.replaceFirst("\\[\\d+\\]", "").split(" : ")[0].trim();;
		return "";
	}

	@Override
	public String toString() {
		String sentenceScore = "";
		if (sentence != null)
			sentenceScore += ", SetencenceScore= "
					+ sentence.getScore("default");
		return "QuizAnswer [answer=" + answer + ", score=" + score + ","
				+ " occurrence=" + occurrence + sentenceScore + "," + NeTypes
				+ "]";
	}

	public int getOccurrence() {
		return occurrence;
	}

	public Image getImage() throws IOException {
		return this.image;
	}

	public void setImage(Image image) throws IOException {
		this.image = image;
	}

	public void setOccurrence(int occurrence) {
		this.occurrence = occurrence;
	}

	public void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}

	public List<String> getNeTypes() {
		return NeTypes;
	}

	public void setNeTypes(List<String> neTypes) {
		NeTypes = neTypes;
	}

	public double getSentenceScore(String scoreName) {
		if (sentence != null)
			return sentence.getScore(scoreName);
		else
			return 0.0;
	}

	public String getImageName() {
		String answer = getAnswer();
		if (NeTypes.contains("PERSON") || NeTypes.contains("LOCATION")
				|| NeTypes.contains("ORGANIZATION") || NeTypes.contains("MISC")
				|| answer.startsWith("Season "))

		{
			return answer;
		} else {
			if (answer.contains("- Episode ") && answer.contains("Season")) {

				return EntityCollection.getUrls_episodes()
						.get(Integer.parseInt(answer.split("-")[0].trim()) - 1)
						.substring(31);

			} else {
				if (getURL().startsWith("http://simpsons.wikia.com/wiki/")) {
					return getURL().replace("http://simpsons.wikia.com/wiki/",
							"");
				}

				else
					return null;
			}
		}
	}
   
	public boolean isEpisodeOrSeason() {
		return answer.contains("- Episode ") && answer.contains("Season")
				|| NeTypes.isEmpty();
	}
}
