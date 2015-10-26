package imagefinder.types;

import java.util.ArrayList;

public class AllimagesQuery {
	private Querycontinue querycontinue;
	private Query query;

	public static class Querycontinue {
		private Allimages allimages;

		public Allimages getAllimages() {
			return allimages;
		}

		public void setAllimages(Allimages allimages) {
			this.allimages = allimages;
		}
	}

	public static class Allimages {
		private String aicontinue;

		public String getAicontinue() {
			return aicontinue;
		}

		public void setAicontinue(String aicontinue) {
			this.aicontinue = aicontinue;
		}
	}

	public static class Query {
		private ArrayList<Image> allimages;

		public ArrayList<Image> getAllimages() {
			return allimages;
		}

		public void setAllimages(ArrayList<Image> allimages) {
			this.allimages = allimages;
		}
	}

	public Querycontinue getQuerycontinue() {
		return querycontinue;
	}

	public void setQuerycontinue(Querycontinue querycontinue) {
		this.querycontinue = querycontinue;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}
}