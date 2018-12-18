package gr.cite.gaap.datatransferobjects;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

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
	private String description;
	private List<String> tags;

	public TsvImportProperties() {
		super();
		logger.trace("Initialized default contructor for TsvImportProperties");
	}

	public String getDescription() {
		return description;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
	public void DecodeToUTF8() throws UnsupportedEncodingException {
		this.setLayerName( URLDecoder.decode( this.getLayerName(), "UTF-8" ) );
		this.setGeocodeSystem( URLDecoder.decode( this.getGeocodeSystem(), "UTF-8" ) );
		this.setFileName( URLDecoder.decode( this.getFileName(), "UTF-8" ) );
		this.setStyle( URLDecoder.decode( this.getStyle(), "UTF-8" ) );
		this.setDescription( URLDecoder.decode( this.getDescription(), "UTF-8" ) );
		this.setTags( this.getTags().stream().map( tag -> {
			try {
				return URLDecoder.decode( tag, "UTF-8" );
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return tag;
		} ).collect(Collectors.toList()));
	}

	@Override
	public String toString() {
		return "TsvImportProperties [layerName=" + layerName + ", geocodeSystem=" + geocodeSystem + ", fileName=" + fileName + ", style=" + style + "]";
	}
}
