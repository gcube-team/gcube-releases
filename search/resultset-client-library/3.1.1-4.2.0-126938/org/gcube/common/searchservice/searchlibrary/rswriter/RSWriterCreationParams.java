package org.gcube.common.searchservice.searchlibrary.rswriter;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;

import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;

/**
 * Creation Parameters container class
 * @author Konstantinos Tsakalozos
 *
 */
public class RSWriterCreationParams {
	private int accessReads = -10;
	private boolean forward = false;
	private boolean dataflow = false;
	
	/**
	 * Array of property elements
	 */
	public ArrayList<PropertyElementBase> properties = new ArrayList<PropertyElementBase>();
	private Date expire_date = new Date(0); 
	private PublicKey pubKey = null;
	private PrivateKey privKey = null;
	private int partSize = 102400;
	private int recsPerPart = 20;

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
	public PrivateKey getPrivKey() {
		return privKey;
	}

	
	/**
	 * Set the rivate key of the RS
	 * @param privKey the private key
	 */
	public void setPrivKey(PrivateKey privKey) {
		this.privKey = privKey;
	}

	/**
	 * Get the Element properties of the RS
	 * @return an array list of the properties
	 */
	public ArrayList<PropertyElementBase> getProperties() {
		return properties;
	}

	
	/**
	 * Set the Element properties of the RS
	 * @param  properties an array list of the properties
	 */
	public void setProperties(ArrayList<PropertyElementBase> properties) {
		this.properties = properties;
	}

	/**
	 * Get the public key of the RS
	 * @return the public key
	 */
	public PublicKey getPubKey() {
		return pubKey;
	}

	/**
	 * Set the public key of the RS
	 * @param pubKey the public key
	 */
	public void setPubKey(PublicKey pubKey) {
		this.pubKey = pubKey;
	}

	/**
	 * Get the part size of the RS 
	 * @return the part size
	 */
	public int getPartSize() {
		return partSize;
	}

	/**
	 * Set the part size 
	 * @param partSize the part size
	 */
	public void setPartSize(int partSize) {
		this.partSize = partSize;
	}

	/**
	 * Get the records per part
	 * @return the records per part
	 */
	public int getRecsPerPart() {
		return recsPerPart;
	}

	/**
	 * Set the records per part
	 * @param recsPerPart the records per part
	 */
	public void setRecsPerPart(int recsPerPart) {
		this.recsPerPart = recsPerPart;
	}
	
}
