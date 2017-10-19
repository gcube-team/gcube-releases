package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SpacesData implements Serializable {

	private static final long serialVersionUID = 719740085818609829L;
	private String space;

	public SpacesData() {
		super();
	}

	public SpacesData(String space) {
		super();
		this.space = space;
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public String getLabel() {
		return space;
	}

	public void setLabel(String space) {
		this.space = space;
	}

	@Override
	public String toString() {
		return "SpacesData [space=" + space + "]";
	}

}
