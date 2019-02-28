/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class AvailableCharsetList implements Serializable {
	
	private static final long serialVersionUID = -1635081144303522534L;
	
	protected ArrayList<String> charsetList;
	protected String defaultCharset;
	
	protected AvailableCharsetList()
	{}
	
	/**
	 * @param charsetList
	 * @param defaultCharset
	 */
	public AvailableCharsetList(ArrayList<String> charsetList, String defaultCharset) {
		this.charsetList = charsetList;
		this.defaultCharset = defaultCharset;
	}
	
	/**
	 * @return the charsetList
	 */
	public ArrayList<String> getCharsetList() {
		return charsetList;
	}
	
	/**
	 * @return the defaultCharset
	 */
	public String getDefaultCharset() {
		return defaultCharset;
	}
}
