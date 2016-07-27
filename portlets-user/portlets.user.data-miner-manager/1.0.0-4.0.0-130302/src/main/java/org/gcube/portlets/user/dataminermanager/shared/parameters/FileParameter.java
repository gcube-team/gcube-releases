/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.parameters;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class FileParameter extends Parameter implements IsSerializable {

	private static final long serialVersionUID = -2967577990287112937L;
	private String value;
	private String defaultMimeType;
	private ArrayList<String> supportedMimeTypes;
	/**
	 * 
	 */
	public FileParameter() {
		super();
		this.typology = ParameterTypology.FILE;
	}
	
	/**
	 * 
	 * @param name
	 * @param description
	 * @param fileName
	 * @param mimeType
	 */
	public FileParameter(String name, String description, String defaultMimeType, ArrayList<String> supportedMimeTypes) {
		super(name, ParameterTypology.FILE, description);
		this.defaultMimeType = defaultMimeType;
		this.supportedMimeTypes = supportedMimeTypes;
	}

	public String getDefaultMimeType() {
		return defaultMimeType;
	}

	public void setDefaultMimeType(String defaultMimeType) {
		this.defaultMimeType = defaultMimeType;
	}

	public ArrayList<String> getSupportedMimeTypes() {
		return supportedMimeTypes;
	}

	public void setSupportedMimeTypes(ArrayList<String> supportedMimeTypes) {
		this.supportedMimeTypes = supportedMimeTypes;
	}

	/**
	 * 
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 
	 */
	@Override
	public void setValue(String value) {
		this.value=value;
	}

	@Override
	public String toString() {
		return "FileParameter [value=" + value + ", defaultMimeType="
				+ defaultMimeType + ", supportedMimeTypes="
				+ supportedMimeTypes + "]";
	}

	
	
}
