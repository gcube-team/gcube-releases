package org.gcube.portlets.user.gisviewer.server.baselayer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.gisviewer.server.exception.PropertyFileNotFoundException;


/**
 * The Class BaseLayerPropertyReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 28, 2015
 */
public class BaseLayerPropertyReader {

	protected static final String BASE_LAYER_FILE = "baselayer.properties";
	protected static final String BASELAYER_WMS_SERVER = "BASELAYER_WMS_SERVER";
	protected static final String BASELAYER_LAYERS = "BASELAYER_LAYERS";
	protected static final String BASELAYER_TITLE = "BASELAYER_TITLE";

	private String layerName;
	private String layerTitle;
	private String layerWmsUrl;

	public static Logger logger = Logger
			.getLogger(BaseLayerPropertyReader.class);

	/**
	 * Instantiates a new base layer property reader.
	 *
	 * @throws PropertyFileNotFoundException the property file not found exception
	 */
	public BaseLayerPropertyReader() throws PropertyFileNotFoundException {

		Properties prop = new Properties();

		try {

			InputStream in = (InputStream) BaseLayerPropertyReader.class.getResourceAsStream(BASE_LAYER_FILE);
			
			// load a properties file
			prop.load(in);
			
			this.layerWmsUrl = prop.getProperty(BASELAYER_WMS_SERVER);
			this.layerTitle = prop.getProperty(BASELAYER_TITLE);
			this.layerName = prop.getProperty(BASELAYER_LAYERS);

		} catch (IOException e) {
			logger.error("An error occurred on read property file " + e, e);
			throw new PropertyFileNotFoundException(
					"An error occurred on read property file " + e);
		}
	}

	/**
	 * Gets the base layer file.
	 *
	 * @return the base layer file
	 */
	public static String getBaseLayerFile() {
		return BASE_LAYER_FILE;
	}

	/**
	 * Gets the baselayer wms server.
	 *
	 * @return the baselayer wms server
	 */
	public static String getBaselayerWmsServer() {
		return BASELAYER_WMS_SERVER;
	}

	/**
	 * Gets the baselayer layers.
	 *
	 * @return the baselayer layers
	 */
	public static String getBaselayerLayers() {
		return BASELAYER_LAYERS;
	}

	/**
	 * Gets the baselayer title.
	 *
	 * @return the baselayer title
	 */
	public static String getBaselayerTitle() {
		return BASELAYER_TITLE;
	}

	/**
	 * Gets the layer name.
	 *
	 * @return the layer name
	 */
	public String getLayerName() {
		return layerName;
	}

	/**
	 * Gets the layer title.
	 *
	 * @return the layer title
	 */
	public String getLayerTitle() {
		return layerTitle;
	}

	/**
	 * Gets the layer wms url.
	 *
	 * @return the layer wms url
	 */
	public String getLayerWmsUrl() {
		return layerWmsUrl;
	}

	/**
	 * Sets the layer name.
	 *
	 * @param layerName the new layer name
	 */
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	/**
	 * Sets the layer title.
	 *
	 * @param layerTitle the new layer title
	 */
	public void setLayerTitle(String layerTitle) {
		this.layerTitle = layerTitle;
	}

	/**
	 * Sets the layer wms url.
	 *
	 * @param layerWmsUrl the new layer wms url
	 */
	public void setLayerWmsUrl(String layerWmsUrl) {
		this.layerWmsUrl = layerWmsUrl;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BaseLayerPropertyReader [layerName=");
		builder.append(layerName);
		builder.append(", layerTitle=");
		builder.append(layerTitle);
		builder.append(", layerWmsUrl=");
		builder.append(layerWmsUrl);
		builder.append("]");
		return builder.toString();
	}

	/*
	public static void main(String[] args) {
		BaseLayerPropertyReader gr;
		try {
			gr = new BaseLayerPropertyReader();
			System.out.println(gr);
		} catch (PropertyFileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}
