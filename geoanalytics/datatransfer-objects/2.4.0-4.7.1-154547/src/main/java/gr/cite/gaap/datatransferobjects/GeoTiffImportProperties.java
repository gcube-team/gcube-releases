package gr.cite.gaap.datatransferobjects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import gr.cite.geoanalytics.util.http.CustomException;

public class GeoTiffImportProperties {

	private static Logger logger = LoggerFactory.getLogger(GeoTiffImportProperties.class);

	private String layerName;
	private String style;
	private String description;
	private List<String> tags;

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

	public GeoTiffImportProperties() {
		super();
		logger.trace("Initialized default contructor for GeoTiffImportProperties");
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void validate() throws CustomException {
		try {
			Assert.isTrue(layerName != null && !layerName.isEmpty(), "Layer Name cannot be empty");
			Assert.isTrue(style != null && !style.isEmpty(), "Style cannot be empty");
			Assert.notEmpty(tags, "Tags cannot be empty");
		} catch (IllegalArgumentException e) {
			throw new CustomException(BAD_REQUEST, e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "GeoTiffImportProperties [layerName=" + layerName + ", style=" + style + "]";
	}
}
