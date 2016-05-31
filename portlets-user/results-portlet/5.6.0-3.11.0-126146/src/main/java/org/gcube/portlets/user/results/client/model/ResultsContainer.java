package org.gcube.portlets.user.results.client.model;

import java.util.HashMap;
import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <code> ResultsContainer </code> class contains the information neede to retrieve search results
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version January 2009 (0.1) 
 */

public class ResultsContainer implements IsSerializable {
	
	private ResultType type;
	/**
	 * The actual results from search
	 */
	private Vector<ResultObj> resultRecords;

	/**
	 * optional parameters to pass when returning the records
	 * in general
	 * 
	 * 	 options[0] contains the starting point
	 * 			[1] contains true if back button has to be enabled
	 *			[2] contains true if next button has to be enabled
	 *			[3] contains the number of results per page
	 */
	private String[] optionalParams;

	/**
	 * contains the content of the propeties file as key value for the existing portlets deployed into a VRE
	 */
	private HashMap<String, String> activelinksIntoVRE;

	/**
	 * Default Constructor
	 *
	 */
	public ResultsContainer() {	}

	/**
	 * 
	 * @param resultRecords The actual results from search as ResultObj class
	 * @param optionalParams array for optional parameters to pass when returning the records
	 * @param type type can be results, no results, error.
	 * @param activelinksIntoVRE contains the links for redirecting to annotation, ontology, contentviever if any
	 */
	public ResultsContainer(Vector<ResultObj> resultRecords, String[] optionalParams, ResultType type, HashMap<String, String> activelinksIntoVRE) {
		this.resultRecords = resultRecords;
		this.optionalParams = optionalParams;
		this.type = type;
		this.activelinksIntoVRE = activelinksIntoVRE;
	}

	public String[] getOptionalParams() {
		return optionalParams;
	}

	public void setOptionalParamsVector(String[] optionalParams) {
		this.optionalParams = optionalParams;
	}

	public Vector<ResultObj> getResultRecords() {
		return resultRecords;
	}

	public void setResultRecords(Vector<ResultObj> resultRecords) {
		this.resultRecords = resultRecords;
	}

	public ResultType getType() {
		return type;
	}

	public void setType(ResultType type) {
		this.type = type;
	}	
	public HashMap<String, String>  getActivelinksIntoVRE() {
		return activelinksIntoVRE;
	}

	public void setActivelinksIntoVRE(HashMap<String, String>  activelinksIntoVRE) {
		this.activelinksIntoVRE = activelinksIntoVRE;
	}
}
