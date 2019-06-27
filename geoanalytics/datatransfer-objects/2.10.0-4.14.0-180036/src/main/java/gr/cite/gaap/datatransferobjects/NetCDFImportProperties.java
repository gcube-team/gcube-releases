package gr.cite.gaap.datatransferobjects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import gr.cite.geoanalytics.util.http.CustomException;

public class NetCDFImportProperties {
    private static Logger logger = LoggerFactory.getLogger(NetCDFImportProperties.class);

    private String layerName;
    private String style;
    private String description;
    private List<String> tags;
    private boolean seed;


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

    public NetCDFImportProperties() {
        super();
        logger.trace("Initialized default contructor for NetCDFImportProperties");
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

    public boolean isSeed() {
        return seed;
    }

    public void setSeed(boolean seed) {
        this.seed = seed;
    }

    public void DecodeToUTF8() throws UnsupportedEncodingException {
        this.setLayerName( URLDecoder.decode( this.getLayerName(), "UTF-8" ) );
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
        return "NetCDFImportProperties [layerName=" + layerName + ", style=" + style + "]";
    }

}
