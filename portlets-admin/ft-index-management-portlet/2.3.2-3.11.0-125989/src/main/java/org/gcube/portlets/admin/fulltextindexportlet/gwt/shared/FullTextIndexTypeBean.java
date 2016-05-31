/**
 * 
 */
package org.gcube.portlets.admin.fulltextindexportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A class used to transport index type information between the client and the
 * service
 * 
 * @author Spyros Boutsis, NKUA
 */
public class FullTextIndexTypeBean implements IsSerializable, Comparable<FullTextIndexTypeBean>{

	/** The indexTypeID */
	private String indexTypeID;
	
	/** Empty constructor */
	public FullTextIndexTypeBean() { }
	
	/**
	 * Returns the index type ID
	 * @return the index type ID
	 */
	public String getIndexTypeID() {
		return indexTypeID;
	}
	
	/**
	 * Sets the index type ID
	 * @param indexTypeID the index type ID
	 */
	public void setIndexTypeID(String indexTypeID) {
		this.indexTypeID = indexTypeID;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(FullTextIndexTypeBean arg0) {
		return this.indexTypeID.compareTo(arg0.getIndexTypeID());
	}

}
