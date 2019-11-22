/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.shared.performfishservice;

import java.io.Serializable;
import java.util.Map;


/**
 * The Class PerformFishResponse.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 29, 2019
 */
public class PerformFishResponse implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 6064068032560730872L;

	private Map<String, String> mapParameters;
	private String respSessionId;


	/**
	 * Instantiates a new perform fish response.
	 */
	public PerformFishResponse() {
	}

	/**
	 * Instantiates a new perform fish response.
	 *
	 * @param mapParameters the map parameters
	 * @param respSessionId the resp session id
	 */
	public PerformFishResponse(
		Map<String, String> mapParameters, String respSessionId) {

		super();
		this.mapParameters = mapParameters;
		this.respSessionId = respSessionId;
	}


	/**
	 * Gets the map parameters.
	 *
	 * @return the mapParameters
	 */
	public Map<String, String> getMapParameters() {

		return mapParameters;
	}


	/**
	 * Gets the resp session id.
	 *
	 * @return the respSessionId
	 */
	public String getRespSessionId() {

		return respSessionId;
	}


	/**
	 * Sets the map parameters.
	 *
	 * @param mapParameters the mapParameters to set
	 */
	public void setMapParameters(Map<String, String> mapParameters) {

		this.mapParameters = mapParameters;
	}


	/**
	 * Sets the resp session id.
	 *
	 * @param respSessionId the respSessionId to set
	 */
	public void setRespSessionId(String respSessionId) {

		this.respSessionId = respSessionId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("PerformFishResponse [mapParameters=");
		builder.append(mapParameters);
		builder.append(", respSessionId=");
		builder.append(respSessionId);
		builder.append("]");
		return builder.toString();
	}
}
