package gr.uoa.di.madgik.gcubesearchlibrary.model.beans;

import java.io.Serializable;

/**
 * A class that represents a Collection
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class CollectionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5765990563229207603L;

	private String name;

	private String id;
	
	/**
	 * Class Constructor
	 * 
	 * @param name The collection's name
	 * @param id The collection's ID
	 */
	public CollectionBean(String name, String id) {
		this.name = name;
		this.id = id;
	}
	
	/**
	 * 
	 * @return The collection's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return The collection's ID
	 */
	public String getId() {
		return id;
	}
}
