package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class Spaces implements Serializable {

	private static final long serialVersionUID = -1704075020370137961L;
	private ArrayList<String> spacesList;

	public Spaces() {
		super();
	}

	public Spaces(ArrayList<String> spacesList) {
		super();
		this.spacesList = spacesList;
	}

	public ArrayList<String> getSpacesList() {
		return spacesList;
	}

	public void setSpacesList(ArrayList<String> spacesList) {
		this.spacesList = spacesList;
	}

	@Override
	public String toString() {
		return "Spaces [spacesList=" + spacesList + "]";
	}

}
