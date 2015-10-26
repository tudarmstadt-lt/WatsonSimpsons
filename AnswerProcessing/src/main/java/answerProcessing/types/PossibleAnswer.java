package answerProcessing.types;

public class PossibleAnswer {

	Text annotatedText;
	String rawText;
	String title;
	Question question;
	float confidence;
	private String originalfile;
	
	
	public PossibleAnswer(Question question, String rawText) {
		super();
		this.question = question;
		this.rawText = rawText;
		this.annotatedText = null;
	}

	public PossibleAnswer(Question question, String rawText, float confidence, String originalfile, String title) {
		super();
		this.question = question;
		this.rawText = rawText;
		this.confidence = confidence;
		this.originalfile = originalfile;
		this.annotatedText = null;
		this.title = title;
		
	}

	public void setConfidence(float confidence){this.confidence=confidence; }

	public float getConfidence(){ return this.confidence; }
	
	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public Text getAnnotatedText() {
		return annotatedText;
	}

	public void setAnnotatedText(Text annotatedText) {
		this.annotatedText = annotatedText;
	}

	public String getRawText() {
		return rawText;
	}

	public void setRawText(String rawText) {
		this.rawText = rawText;
	}
	
	@Override
	public String toString() {
		return getRawText();
	}

	public String getOriginalfile() {
		return originalfile;
	}

	public String setOriginalfile(String originalfile) {
		return this.originalfile = originalfile;
		
	}

	public String getTitle() {
		return title;
	}
	
	
	
}
