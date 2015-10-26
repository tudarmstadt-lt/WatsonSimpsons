package answerProcessing.types;

public class EntityMention {
	
	String id, value, type;
	int spanStart, spanEnd;
	
	public EntityMention(String id, String value, String type, int spanStart,
			int spanEnd) {
		super();
		this.id = id;
		this.value = value;
		this.type = type;
		this.spanStart = spanStart;
		this.spanEnd = spanEnd;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getSpanStart() {
		return spanStart;
	}
	public void setSpanStart(int spanStart) {
		this.spanStart = spanStart;
	}
	public int getSpanEnd() {
		return spanEnd;
	}
	public void setSpanEnd(int spanEnd) {
		this.spanEnd = spanEnd;
	}
	
	@Override
	public String toString() {
		return "EntityMention [id="+id+", type="+type+", start="+spanStart+", end="+spanEnd+", value=\""+value+"\"]";
	}
}
