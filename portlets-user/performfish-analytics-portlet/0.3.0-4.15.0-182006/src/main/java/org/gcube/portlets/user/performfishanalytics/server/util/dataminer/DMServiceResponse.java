/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util.dataminer;

import java.util.List;


/**
 * The Class DMResponse.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 24, 2019
 */
public class DMServiceResponse {

	private boolean withError;
	private String httpRequestURL;
	private String bodyResponse;
	private List<DataMinerOutputData> listDMOutputData;


	/**
	 * Instantiates a new DM service response.
	 *
	 * @param withError the with error
	 * @param httpRequestURL the http request url
	 * @param bodyResponse the body response
	 * @param listDMOutputData the list dm output data
	 */
	public DMServiceResponse(boolean withError, String httpRequestURL, String bodyResponse, List<DataMinerOutputData> listDMOutputData) {
		this.httpRequestURL = httpRequestURL;
		this.withError = withError;
		this.bodyResponse = bodyResponse;
		this.listDMOutputData = listDMOutputData;
	}



	/**
	 * @return the withError
	 */
	public boolean isWithError() {

		return withError;
	}



	/**
	 * @return the httpRequestURL
	 */
	public String getHttpRequestURL() {

		return httpRequestURL;
	}



	/**
	 * @return the bodyResponse
	 */
	public String getBodyResponse() {

		return bodyResponse;
	}



	/**
	 * @return the listDMOutputData
	 */
	public List<DataMinerOutputData> getListDMOutputData() {

		return listDMOutputData;
	}



	/**
	 * @param withError the withError to set
	 */
	public void setWithError(boolean withError) {

		this.withError = withError;
	}



	/**
	 * @param httpRequestURL the httpRequestURL to set
	 */
	public void setHttpRequestURL(String httpRequestURL) {

		this.httpRequestURL = httpRequestURL;
	}



	/**
	 * @param bodyResponse the bodyResponse to set
	 */
	public void setBodyResponse(String bodyResponse) {

		this.bodyResponse = bodyResponse;
	}



	/**
	 * @param listDMOutputData the listDMOutputData to set
	 */
	public void setListDMOutputData(List<DataMinerOutputData> listDMOutputData) {

		this.listDMOutputData = listDMOutputData;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("DMServiceResponse [withError=");
		builder.append(withError);
		builder.append(", httpRequestURL=");
		builder.append(httpRequestURL);
		builder.append(", bodyResponse=");
		builder.append(bodyResponse);
		builder.append(", listDMOutputData=");
		builder.append(listDMOutputData);
		builder.append("]");
		return builder.toString();
	}





}
