/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.client.beans;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * The Class NcWmsLayerMetadata.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 18, 2015
 */
public class NcWmsLayerMetadata implements IsSerializable {
	
	//GET PARAMETERS
	public static final String SUPPORTEDSTYLES = "supportedStyles";
	public static final String PALETTES = "palettes";
	public static final String DEFAULTPALETTE = "defaultPalette";
	private String defaultPalette;
	private List<String> supportedStyles;
	private List<String> palettes;
	private int responseCode;
	
	/**
	 * Instantiates a new nc wms layer metadata.
	 */
	public NcWmsLayerMetadata() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Instantiates a new nc wms layer metadata.
	 *
	 * @param responseCode the response code
	 * @param defaultPalette the default palette
	 * @param supportedStyles the supported styles
	 * @param palettes the palettes
	 */
	public NcWmsLayerMetadata(int responseCode, String defaultPalette, List<String> supportedStyles, List<String> palettes) {
		this.responseCode = responseCode;
		this.defaultPalette = defaultPalette;
		this.supportedStyles = supportedStyles;
		this.palettes = palettes;
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
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
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
		builder.append("]");
		return builder.toString();
	}

}
