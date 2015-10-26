package answerProcessing.types;
public class Image {
	String name="";
	int	width=0;
	int	height=0;
	String url="";
	String descriptionurl="";
	String title="";
	private String thumburl;
	
	public Image(String name, int width, int height, String url,
			String descriptionurl,String thumburl, String title) {
		super();
		this.name = name;
		this.width = width;
		this.height = height;
		this.url = url;
		this.descriptionurl = descriptionurl;
		this.thumburl = thumburl;
		this.title = title;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescriptionurl() {
		return descriptionurl;
	}
	public void setDescriptionurl(String descriptionurl) {
		this.descriptionurl = descriptionurl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getThumburl() {
		return thumburl;
	}
	public void setThumburl(String thumburl) {
		this.thumburl = thumburl;
	}
	
	public int getComputedWidth(int maxHeight, int maxWidth) {
		double result = (double) (maxHeight * width) / (double) height;
		if (result > maxWidth)
			result = maxWidth;
		return (int) result;

	}

	public int getComputedHeight(int maxHeight, int maxWidth) {
		double result = (double) (maxHeight * width) / (double) height;
		if (result > maxWidth)
			return (int) ((double) (maxWidth * height) / (double) width);
		else
			return maxHeight;

	}
	@Override
	public String toString() {
		return "Image [name=" + name + ", width=" + width
				+ ", height=" + height + ", url=" + url + ", descriptionurl="
				+ descriptionurl + ", title=" + title + "]";
	}
}
