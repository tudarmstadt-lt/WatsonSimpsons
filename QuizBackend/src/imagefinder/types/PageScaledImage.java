package imagefinder.types;

import java.util.ArrayList;

public class PageScaledImage extends Page {
	@Override
	public String toString() {
		return "PageScaledImage [images=" + imageinfo + ", imagerepository="
				+ imagerepository + ", getPageid()=" + getPageid()
				+ ", getNs()=" + getNs() + ", getTitle()=" + getTitle() + "]";
	}

	public String getImagerepository() {
		return imagerepository;
	}

	public void setImagerepository(String imagerepository) {
		this.imagerepository = imagerepository;
	}

	private ArrayList<Thumbimage> imageinfo;
	private String imagerepository = "";

	public ArrayList<Thumbimage> getImages() {
		return imageinfo;
	}

	public void setImages(ArrayList<Thumbimage> images) {
		this.imageinfo = images;
	}
}
