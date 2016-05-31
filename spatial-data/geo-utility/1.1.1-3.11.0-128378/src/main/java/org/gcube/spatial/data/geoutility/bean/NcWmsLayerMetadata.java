/**
 *
 */
package org.gcube.spatial.data.geoutility.bean;

import java.io.Serializable;
import java.util.List;


/**
 * The Class NcWmsLayerMetadata.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 18, 2015
 */
public class NcWmsLayerMetadata implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5111586382138532571L;

	public static enum METADATA {
		SUPPORTEDSTYLES("supportedStyles"),
		PALETTES("palettes"),
		DEFAULTPALETTE("defaultPalette"),
		Z_AXIS("zaxis");

		private String key;

		METADATA(String key){
			this.key = key;
		}

		public String getKey() {

			return key;
		}

	};

	private String defaultPalette;
	private List<String> supportedStyles;
	private List<String> palettes;
	private int responseCode;
	private LayerZAxis zAxis;
	private String rawJson;

	/**
	 * Instantiates a new nc wms layer metadata.
	 */
	public NcWmsLayerMetadata() {
	}


	/**
	 * Instantiates a new nc wms layer metadata.
	 *
	 * @param responseCode the response code
	 * @param defaultPalette the default palette
	 * @param supportedStyles the supported styles
	 * @param palettes the palettes
	 * @param zAxis the z axis
	 * @param json the json
	 */
	public NcWmsLayerMetadata(int responseCode, String defaultPalette, List<String> supportedStyles, List<String> palettes, LayerZAxis zAxis, String json) {
		this.responseCode = responseCode;
		this.defaultPalette = defaultPalette;
		this.supportedStyles = supportedStyles;
		this.palettes = palettes;
		this.zAxis = zAxis;
		this.rawJson = json;
	}

	/**
	 * Sets the raw json.
	 *
	 * @param jsonTxt the new raw json
	 */
	public void setRawJson(String jsonTxt) {
		this.rawJson =jsonTxt;
	}

	/**
	 * Gets the raw json.
	 *
	 * @return the rawJson
	 */
	public String getRawJson() {
		return rawJson;
	}


	/**
	 * Gets the default palette.
	 *
	 * @return the defaultPalette
	 */
	public String getDefaultPalette() {
		return defaultPalette;
	}

	/**
	 * Gets the supported styles.
	 *
	 * @return the supportedStyles
	 */
	public List<String> getSupportedStyles() {
		return supportedStyles;
	}


	/**
	 * Gets the palettes.
	 *
	 * @return the palettes
	 */
	public List<String> getPalettes() {
		return palettes;
	}


	/**
	 * Gets the z axis.
	 *
	 * @return the zAxis
	 */
	public LayerZAxis getZAxis() {

		return zAxis;
	}

	/**
	 * Sets the z axis.
	 *
	 * @param zAxis the zAxis to set
	 */
	public void setZAxis(LayerZAxis zAxis) {

		this.zAxis = zAxis;
	}


	/**
	 * Sets the default palette.
	 *
	 * @param defaultPalette the defaultPalette to set
	 */
	public void setDefaultPalette(String defaultPalette) {
		this.defaultPalette = defaultPalette;
	}


	/**
	 * Sets the supported styles.
	 *
	 * @param supportedStyles the supportedStyles to set
	 */
	public void setSupportedStyles(List<String> supportedStyles) {
		this.supportedStyles = supportedStyles;
	}

	/**
	 * Gets the response code.
	 *
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * Sets the response code.
	 *
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}


	/**
	 * Sets the palettes.
	 *
	 * @param palettes the palettes to set
	 */
	public void setPalettes(List<String> palettes) {
		this.palettes = palettes;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("NcWmsLayerMetadata [defaultPalette=");
		builder.append(defaultPalette);
		builder.append(", supportedStyles=");
		builder.append(supportedStyles);
		builder.append(", palettes=");
		builder.append(palettes);
		builder.append(", responseCode=");
		builder.append(responseCode);
		builder.append(", zAxis=");
		builder.append(zAxis);
		builder.append("]");
		return builder.toString();
	}

}
