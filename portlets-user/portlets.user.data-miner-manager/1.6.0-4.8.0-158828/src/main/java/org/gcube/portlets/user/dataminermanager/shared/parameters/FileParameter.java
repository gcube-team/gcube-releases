/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.parameters;

import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class FileParameter extends Parameter {

	private static final long serialVersionUID = -2967577990287112937L;
	private String defaultMimeType;
	private ArrayList<String> supportedMimeTypes;
	private boolean netcdf;

	/**
	 * 
	 */
	public FileParameter() {
		super();
		this.typology = ParameterType.FILE;
		netcdf = false;
	}

	/**
	 * 
	 * @param name
	 *            name
	 * @param description
	 *            description
	 * @param defaultMimeType
	 *            default mime type
	 * @param supportedMimeTypes
	 *            supported mime types
	 * @param netcdf
	 *            is netcdf file
	 */
	public FileParameter(String name, String description, String defaultMimeType, ArrayList<String> supportedMimeTypes,
			boolean netcdf) {
		super(name, ParameterType.FILE, description);
		this.defaultMimeType = defaultMimeType;
		this.supportedMimeTypes = supportedMimeTypes;
		this.netcdf = netcdf;
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

	public boolean isNetcdf() {
		return netcdf;
	}

	public void setNetcdf(boolean netcdf) {
		this.netcdf = netcdf;
	}

	@Override
	public String toString() {
		return "FileParameter [defaultMimeType=" + defaultMimeType + ", supportedMimeTypes=" + supportedMimeTypes
				+ ", netcdf=" + netcdf + ", name=" + name + ", description=" + description + ", typology=" + typology
				+ ", value=" + value + "]";
	}

}
