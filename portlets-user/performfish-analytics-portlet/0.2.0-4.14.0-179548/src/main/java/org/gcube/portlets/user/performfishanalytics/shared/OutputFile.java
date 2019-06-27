/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.shared;

import java.io.Serializable;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 24, 2019
 */
public class OutputFile implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 8312729240987548992L;

	FileContentType dataType;
	String serverLocation;
	String name;

	/**
	 *
	 */
	public OutputFile() {

	}


	/**
	 * @return the dataType
	 */
	public FileContentType getDataType() {

		return dataType;
	}


	/**
	 * @return the serverLocation
	 */
	public String getServerLocation() {

		return serverLocation;
	}


	/**
	 * @return the name
	 */
	public String getName() {

		return name;
	}


	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(FileContentType dataType) {

		this.dataType = dataType;
	}


	/**
	 * @param serverLocation the serverLocation to set
	 */
	public void setServerLocation(String serverLocation) {

		this.serverLocation = serverLocation;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("OutputFile [dataType=");
		builder.append(dataType);
		builder.append(", serverLocation=");
		builder.append(serverLocation);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

}
