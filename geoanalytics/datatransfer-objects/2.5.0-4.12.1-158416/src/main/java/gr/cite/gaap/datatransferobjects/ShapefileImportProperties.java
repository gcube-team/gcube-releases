package gr.cite.gaap.datatransferobjects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonProperty;

import gr.cite.geoanalytics.util.http.CustomException;

public class ShapefileImportProperties {

	private static Logger logger = LoggerFactory.getLogger(TsvImportProperties.class);

	private String newLayerName;
	private String geocodeSystem;
	private String geocodeMapping;
	private String style;
	private String dbfEncoding;
	private String description;
	private List<String> tags;

	@JsonProperty("isTemplate")
	private boolean isTemplate;

	public ShapefileImportProperties() {
		super();
		logger.trace("Initialized default contructor for TsvImportProperties");
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNewLayerName() {
		return newLayerName;
	}

	public void setNewLayerName(String newLayerName) {
		this.newLayerName = newLayerName;
	}

	public String getGeocodeSystem() {
		return geocodeSystem;
	}

	public void setGeocodeSystem(String geocodeSystem) {
		this.geocodeSystem = geocodeSystem;
	}

	public String getGeocodeMapping() {
		return geocodeMapping;
	}

	public void setGeocodeMapping(String geocodeMapping) {
		this.geocodeMapping = geocodeMapping;
	}

	public boolean isTemplate() {
		return isTemplate;
	}

	public void setTemplate(boolean isTemplate) {
		this.isTemplate = isTemplate;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getDbfEncoding() {
		return dbfEncoding;
	}

	public void setDbfEncoding(String dbfEncoding) {
		this.dbfEncoding = dbfEncoding;
	}

	public void validate() throws CustomException {
		try {
			Assert.notNull(newLayerName, "Layer name cannot be empty");
			Assert.hasLength(newLayerName, "Layer name cannot be empty");

			Assert.notNull(style, "Style cannot be empty");
			Assert.hasLength(style, "Style cannot be empty");
			
			Assert.notEmpty(tags, "Tags cannot be empty");

			if (isTemplate) {
				Assert.notNull(geocodeSystem, "Geocode System name cannot be empty");
				Assert.hasLength(geocodeSystem, "Geocode System name cannot be empty");

				Assert.notNull(geocodeMapping, "Geocode Mapping cannot be empty");
				Assert.hasLength(geocodeMapping, "Geocode Mapping cannot be empty");
			}
		} catch (IllegalArgumentException e) {
			throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "ShapefileImportProperties [newLayerName=" + newLayerName + ", geocodeSystem=" + geocodeSystem + ", geocodeMapping=" + geocodeMapping + ", style=" + style
				+ ", isTemplate=" + isTemplate + "]";
	}

}
