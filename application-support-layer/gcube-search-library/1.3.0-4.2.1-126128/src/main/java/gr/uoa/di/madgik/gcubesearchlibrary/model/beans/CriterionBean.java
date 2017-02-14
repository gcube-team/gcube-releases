package gr.uoa.di.madgik.gcubesearchlibrary.model.beans;

import java.io.Serializable;

/**
 * A class that represents a Criterion for the advanced search
 * A criterion consists of the field ID and the term to search
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class CriterionBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9179269547143098056L;

	private String id = null;
	
	private String value = null;
	
	/**
	 * Class constructor
	 * 
	 * @param id The ID of the criterion
	 * @param value The value of the criterion
	 */
	public CriterionBean(String id, String value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * Criterion ID
	 * 
	 * @return The ID of the criterion
	 */
	public String getId() {
		return id;
	}

	/**
	 * Criterion value
	 * 
	 * @return The value of the criterion
	 */
	public String getValue() {
		return value;
	}
	
	

}
