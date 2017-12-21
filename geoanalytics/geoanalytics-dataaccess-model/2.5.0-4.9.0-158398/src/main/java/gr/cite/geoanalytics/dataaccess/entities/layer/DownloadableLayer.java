package gr.cite.geoanalytics.dataaccess.entities.layer;

public class DownloadableLayer {

	private final Layer layer;
	private final byte[] data;
	private final String contentType;
	private final String filename;

	private DownloadableLayer(Builder builder) {
		this.contentType = builder.contentType;
		this.filename = builder.filename;
		this.layer = builder.layer;
		this.data = builder.data;
	}

	public static class Builder {

		private final Layer layer;
		private byte[] data;
		private String contentType;
		private String filename;

		public Builder(Layer layer) {
			this.layer = layer;
		}

		public Builder data(byte[] data) {
			this.data = data;
			return this;
		}

		public Builder contentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		public Builder filename(String filename) {
			this.filename = filename;
			return this;
		}

		public DownloadableLayer build() {
			return new DownloadableLayer(this);
		}
	}

	public String getContentType() {
		return contentType;
	}

	public String getFilename() {
		return filename;
	}

	public byte[] getData() {
		return data;
	}

	public Layer getLayer() {
		return layer;
	}
}
