package imagefinder.types;


	public  class Page {

		@Override
		public String toString() {
			return "Page [pageid=" + pageid + ", ns=" + ns + ", title=" + title
					+  "]";
		}

		private int pageid = 0;
		private int ns = 0;
		private String title = "";


		public int getPageid() {
			return pageid;
		}

		public void setPageid(int pageid) {
			this.pageid = pageid;
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

		
	}

	

