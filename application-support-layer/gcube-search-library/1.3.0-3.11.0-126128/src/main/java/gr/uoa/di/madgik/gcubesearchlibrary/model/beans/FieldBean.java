package gr.uoa.di.madgik.gcubesearchlibrary.model.beans;

import java.io.Serializable;

/**
 * A class that represents a Field object
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class FieldBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7701590363799305056L;

	private String name;

	private String id;
	
	private boolean isPartOfShortResult = false;
	
	/**
	 * Class Constructor
	 * 
	 * @param name Field's name
	 * @param id Field's value or ID
	 */
	public FieldBean(String name, String id, boolean isPartOfShortResult) {
		this.name = name;
		this.id = id;
		this.isPartOfShortResult = isPartOfShortResult;
	}
	
	/**
	 * Field's name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Field's ID
	 * @return 
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * True if it is part of the short result, else False
	 * @return 
	 */
	public boolean isPartOfShortResult() {
		return isPartOfShortResult;
	}
}
