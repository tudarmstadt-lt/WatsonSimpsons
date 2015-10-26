package answerProcessing.types;

import java.util.List;

public class NeOccurrence {
	private int occurrence;
	private List<String> neTypes;
	private Sentence sentence;

	public NeOccurrence(int occurrence, List<String> neTypes) {
		super();
		this.occurrence = occurrence;
		this.neTypes = neTypes;
	}

	public int getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(int occurrence) {
		this.occurrence = occurrence;
	}

	public List<String> getNeTypes() {
		return neTypes;
	}

	public void setNeTypes(List<String> neTypes) {
		this.neTypes = neTypes;
	}

	public void addOccurrence() {
		this.occurrence++;
	}

	public void addOccurrence(NeOccurrence neOccurrence) {
		this.occurrence += neOccurrence.getOccurrence();
	}

	public void setSentence(Sentence sentence) {
		this.sentence= sentence;
		
	}

	public Sentence getSentence() {
		return sentence;
	}
}
