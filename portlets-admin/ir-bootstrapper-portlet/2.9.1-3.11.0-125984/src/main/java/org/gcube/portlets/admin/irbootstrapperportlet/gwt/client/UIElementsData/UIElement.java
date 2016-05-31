/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class UIElement implements IsSerializable {

	/** The unique identifier of this UIElement */
	private String UID;
	
	/** The name of this UI element */
	private String elementName;
	
	/** A description of this UI element */
	private String elementDesc;
	
	/**
	 * Class constructor
	 */
	public UIElement() {
	}
	
	/**
	 * Sets the unique identifier of this UI element
	 * @param UID the UID to set
	 */
	public void setUID(String UID) {
		this.UID = UID;
	}
	
	/**
	 * Returns the unique identifier of this UI element
	 * @return the UID
	 */
	public String getUID() {
		return this.UID;
	}
	
	/**
	 * Sets the name of this element
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.elementName = name;
	}
	
	/**
	 * Returns the name of this element
	 * @return the name of this element
	 */
	public String getName() {
		return this.elementName;
	}
	
	/**
	 * Sets the description of this element
	 * @param desc the description to set
	 */
	public void setDescription(String desc) {
		this.elementDesc = desc;
	}
	
	/**
	 * Returns the description of this element
	 * @return the description of this element
	 */
	public String getDescription() {
		return this.elementDesc;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return this.UID.equals(((UIElement) o).UID);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.UID.hashCode();
	}
}
