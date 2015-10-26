package answerProcessing.types;

import com.google.gson.Gson;

import java.util.List;

public class Text {

	List<Sentence> sentences;
	private String originalfile;
	private String title;
	
	public Text(List<Sentence> sentences) {
		super();
		for(Sentence sent: sentences){
			sent.setParentText(this);
		}
		this.sentences = sentences;
	}

	public List<Sentence> getSentences() {
		for(Sentence sent: sentences){
			sent.setParentText(this);
		}
		return sentences;
	}

	public void setSentences(List<Sentence> sentences) {
		for(Sentence sent: sentences){
			sent.setParentText(this);
		}
		this.sentences = sentences;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Sentence s : sentences) {
			sb.append(s+" ");
		}
		return sb.toString().trim();
	}
	
	public String toJsonString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public void setOriginalfile(String originalfile) {
		this.originalfile = originalfile;
		
	}
	public String getOriginalfile() {
		return originalfile;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
