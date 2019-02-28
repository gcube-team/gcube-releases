package gr.cite.gaap.datatransferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.geoanalytics.util.http.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

public class GeojsonImportProperties {

    private static Logger logger = LoggerFactory.getLogger(TsvImportProperties.class);

    private String newLayerName;
    private String geocodeSystem;
    private String geocodeMapping;
    private String style;
    private String dbfEncoding;
    private boolean dbfUserInput;
    private String description;
    private List<String> tags;

    @JsonProperty("isTemplate")
    private boolean isTemplate;

    public GeojsonImportProperties() {
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

    public boolean isDbfUserInput() {
        return dbfUserInput;
    }

    public void setDbfUserInput(boolean dbfUserInput) {
        this.dbfUserInput = dbfUserInput;
    }

    public void validate() throws CustomException {
        try {
            Assert.notNull(newLayerName, "Layer name cannot be empty");
            Assert.hasLength(newLayerName, "Layer name cannot be empty");

            Assert.notNull(style, "Style cannot be empty");
            Assert.hasLength(style, "Style cannot be empty");

            Assert.notEmpty(tags, "Tags cannot be empty");
            Assert.notNull(dbfEncoding, "Encoding cannot be empty");

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

    public void DecodeToUTF8() throws UnsupportedEncodingException {
        this.setNewLayerName( URLDecoder.decode( this.getNewLayerName(), "UTF-8" ) );
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
        return "GeojsonImportProperties [newLayerName=" + newLayerName + ", geocodeSystem=" + geocodeSystem + ", geocodeMapping=" + geocodeMapping + ", style=" + style
                + ", isTemplate=" + isTemplate + "]";
    }


}
