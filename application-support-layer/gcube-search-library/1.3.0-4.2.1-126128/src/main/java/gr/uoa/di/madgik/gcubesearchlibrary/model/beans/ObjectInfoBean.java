package gr.uoa.di.madgik.gcubesearchlibrary.model.beans;

import java.io.Serializable;

/**
 * A class that contains a gCube Object's info
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class ObjectInfoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8780643522444898436L;

	private String name;
	
	private String objectID;
	
	private String mimeType;
	
	private String objectLength;
	
	/**
	 * Class constructor
	 * 
	 * @param name Object's name
	 * @param objectID Object's ID
	 * @param mimeType Object's mime type
	 * @param objectLength Object's size
	 */
	public ObjectInfoBean(String name, String objectID, String mimeType, String objectLength) {
		this.name = name;
		this.objectID = objectID;
		this.mimeType = mimeType;
		this.objectLength = objectLength;
	}

	/**
	 * Object's name
	 * @return 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Object's ID
	 * @return
	 */
	public String getObjectID() {
		return objectID;
	}

	/**
	 * Object's mime type
	 * @return 
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Object's payload length
	 * @return 
	 */
	public String getObjectLength() {
		return objectLength;
	}

	
}
