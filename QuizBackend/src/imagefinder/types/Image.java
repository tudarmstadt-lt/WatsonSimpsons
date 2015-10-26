package imagefinder.types;
public class Image {
	String name="";
	int	size=0;
	int	width=0;
	int	height=0;
	String url="";
	String descriptionurl="";
	int ns=0;
	String title="";
	private String thumburl;
	
	public Image(String name, int size, int width, int height, String url,
			String descriptionurl,String thumburl, int ns, String title) {
		super();
		this.name = name;
		this.size = size;
		this.width = width;
		this.height = height;
		this.url = url;
		this.descriptionurl = descriptionurl;
		this.thumburl = thumburl;
		this.ns = ns;
		this.title = title;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
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
	public int getNs() {
		return ns;
	}
	public void setNs(int ns) {
		this.ns = ns;
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
	
	@Override
	public String toString() {
		return "Image [name=" + name + ", size=" + size + ", width=" + width
				+ ", height=" + height + ", url=" + url + ", descriptionurl="
				+ descriptionurl + ", ns=" + ns + ", title=" + title + "]";
	}
}
