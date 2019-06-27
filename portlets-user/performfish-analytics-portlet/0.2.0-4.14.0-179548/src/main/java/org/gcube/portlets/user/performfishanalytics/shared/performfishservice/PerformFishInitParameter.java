/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.shared.performfishservice;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 22, 2019
 */
public class PerformFishInitParameter implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -1197595803048727741L;


	private Map<String, String> parameters = new HashMap<String, String>();

	/**
	 *
	 */
	public PerformFishInitParameter() {
	}


	public void addParameter(String key, String value){
		if(parameters==null)
			parameters = new HashMap<String, String>();
		parameters.put(key, value);
	}

	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {

		return parameters;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("PerformFishInitParameter [parameters=");
		builder.append(parameters);
		builder.append("]");
		return builder.toString();
	}
}
