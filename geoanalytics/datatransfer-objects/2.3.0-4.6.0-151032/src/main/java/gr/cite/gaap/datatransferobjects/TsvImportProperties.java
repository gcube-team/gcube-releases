package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import gr.cite.geoanalytics.util.http.CustomException;

public class TsvImportProperties {

	private static Logger logger = LoggerFactory.getLogger(TsvImportProperties.class);

	private String layerName;
	private String geocodeSystem;
	private String fileName;
	private String style;

	public TsvImportProperties() {
		super();
		logger.trace("Initialized default contructor for TsvImportProperties");
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public String getGeocodeSystem() {
		return geocodeSystem;
	}

	public void setGeocodeSystem(String geocodeSystem) {
		this.geocodeSystem = geocodeSystem;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void validate() throws CustomException {
		try {
			Assert.notNull(layerName, "Layer name cannot be empty");
			Assert.notNull(geocodeSystem, "Geocode System cannot be empty");
			Assert.notNull(style, "Style cannot be empty");

			Assert.hasLength(layerName, "Layer name cannot be empty");
			Assert.hasLength(geocodeSystem, "Geocode System cannot be empty");
			Assert.hasLength(style, "Style cannot be empty");
		} catch (IllegalArgumentException e) {
			throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	
	@Override
	public String toString() {
		return "TsvImportProperties [layerName=" + layerName + ", geocodeSystem=" + geocodeSystem + ", fileName="
				+ fileName + ", style=" + style + "]";
	}
}
