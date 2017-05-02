package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;

/**
 * Creation Parameters container class
 * @author Konstantinos Tsakalozos
 */
public class CreationParams {
	private int accessReads = -1;
	private boolean forward = false;
	private boolean dataflow = false;

	/**
	 * Array of property elements
	 */
	public ArrayList<String> properties = new ArrayList<String>();
	private Date expire_date = new Date(0); 
	private PublicKey pKey = null;
	private String all_properties = null;

	/**
	 * Is the RS to be created with control flow?
	 * @return true control flow is enabled in the RS
	 */
	public boolean isDataflow() {
		return dataflow;
	}

	/**
	 * set the data flow property
	 * @param dataflow the data flow property
	 */
	public void setDataflow(boolean dataflow) {
		this.dataflow = dataflow;
	}

	/**
	 * Is the RS to be created with access leasing?
	 * @return the leasing
	 */
	public int getAccessReads() {
		return accessReads;
	}

	/**
	 * Set the access leasing
	 * @param accessReads the access leasing
	 */
	public void setAccessReads(int accessReads) {
		if (accessReads > 0)  
			accessReads++; //Dont count the creator access
		this.accessReads = accessReads;
	}

	/**
	 * Is the RS to be created with forward only property?
	 * @return true if the RS is to be created as forward only
	 */
	public boolean isForward() {
		return forward;
	}

	/**
	 * Set the forward only property
	 * @param forward true if forward only is to be set
	 */
	public void setForward(boolean forward) {
		this.forward = forward;
	}	

	/**
	 * Is the RS to be created with time leasing?
	 * @return the time leasing
	 */
	public Date getExpire_date() {
		return expire_date;
	}

	/**
	 * Set the time leasing for the RS
	 * @param expire_date the time leasing set
	 */
	public void setExpire_date(Date expire_date) {
		this.expire_date = expire_date;
	}

	/**
	 * Is the RS to be created with a private key?
	 * @return the private key
	 */
	public PublicKey getPKey() {
		return pKey;
	}

	/**
	 * Set the Private key of the RS
	 * @param pKey the private key
	 */
	public void setPKey(PublicKey pKey) {
		this.pKey = pKey;
	}

	/**
	 * Get the Element properties of the RS
	 * @return an array list of the properties
	 */
	public ArrayList<String> getProperties() {
		return properties;
	}

	/**
	 * Set the Element properties of the RS
	 * @param  properties an array list of the properties
	 */
	public void setProperties(ArrayList<String> properties) {
		this.properties = properties;
	}

	/**
	 * Get all properties
	 * @return all properties in a string
	 */
	public String getAll_properties() {
		return all_properties;
	}

	/**
	 * Set all properties
	 * @param all_properties the properties
	 */
	public void setAll_properties(String all_properties) {
		this.all_properties = all_properties;
	}
	
}
