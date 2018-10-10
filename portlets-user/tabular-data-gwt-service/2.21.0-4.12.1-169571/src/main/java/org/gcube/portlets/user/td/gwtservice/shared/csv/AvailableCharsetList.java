/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.csv;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class AvailableCharsetList implements Serializable {

	private static final long serialVersionUID = -1635081144303522534L;

	private ArrayList<String> charsetList;
	private String defaultCharset;

	public AvailableCharsetList() {
		super();
	}

	public AvailableCharsetList(ArrayList<String> charsetList, String defaultCharset) {
		super();
		this.charsetList = charsetList;
		this.defaultCharset = defaultCharset;
	}

	public ArrayList<String> getCharsetList() {
		return charsetList;
	}

	public String getDefaultCharset() {
		return defaultCharset;
	}
}
