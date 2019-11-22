/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util.dataminer;

import java.util.Map;


/**
 * The Class DataMinerOutputData.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 24, 2019
 */
public class DataMinerOutputData {

	private String fileDescription;
	private String mimeType;
	private String publicURL;
	private Map<String, String> dataMinerOutputData;

	/**
	 * Instantiates a new data miner output data.
	 */
	public DataMinerOutputData(){

	}


	/**
	 * Instantiates a new data miner output data.
	 *
	 * @param fileDescription the file description
	 * @param mimeType the mime type
	 * @param publicURL the public url
	 * @param dataMinerOutputData the data miner output data
	 */
	public DataMinerOutputData(
		String fileDescription, String mimeType, String publicURL,
		Map<String, String> dataMinerOutputData) {

		super();
		this.fileDescription = fileDescription;
		this.mimeType = mimeType;
		this.publicURL = publicURL;
		this.dataMinerOutputData = dataMinerOutputData;
	}




	/**
	 * Gets the file description.
	 *
	 * @return the fileDescription
	 */
	public String getFileDescription() {

		return fileDescription;
	}


	/**
	 * Gets the mime type.
	 *
	 * @return the mimeType
	 */
	public String getMimeType() {

		return mimeType;
	}


	/**
	 * Gets the public url.
	 *
	 * @return the publicURL
	 */
	public String getPublicURL() {

		return publicURL;
	}


	/**
	 * Gets the data miner output data.
	 *
	 * @return the dataMinerOutputData
	 */
	public Map<String, String> getDataMinerOutputData() {

		return dataMinerOutputData;
	}


	/**
	 * Sets the file description.
	 *
	 * @param fileDescription the fileDescription to set
	 */
	public void setFileDescription(String fileDescription) {

		this.fileDescription = fileDescription;
	}


	/**
	 * Sets the mime type.
	 *
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {

		this.mimeType = mimeType;
	}


	/**
	 * Sets the public url.
	 *
	 * @param publicURL the publicURL to set
	 */
	public void setPublicURL(String publicURL) {

		this.publicURL = publicURL;
	}


	/**
	 * Sets the data miner output data.
	 *
	 * @param dataMinerOutputData the dataMinerOutputData to set
	 */
	public void setDataMinerOutputData(Map<String, String> dataMinerOutputData) {

		this.dataMinerOutputData = dataMinerOutputData;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("DataMinerOutputData [fileDescription=");
		builder.append(fileDescription);
		builder.append(", mimeType=");
		builder.append(mimeType);
		builder.append(", publicURL=");
		builder.append(publicURL);
		builder.append(", dataMinerOutputData=");
		builder.append(dataMinerOutputData);
		builder.append("]");
		return builder.toString();
	}


}
