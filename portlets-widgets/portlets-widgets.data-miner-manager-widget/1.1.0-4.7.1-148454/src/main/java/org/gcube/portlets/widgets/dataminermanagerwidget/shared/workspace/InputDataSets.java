package org.gcube.portlets.widgets.dataminermanagerwidget.shared.workspace;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InputDataSets implements Serializable {
	private static final long serialVersionUID = -4408116083736005844L;

	private ItemDescription folder;

	public InputDataSets() {
		super();
	}

	public InputDataSets(ItemDescription folder) {
		super();
		this.folder = folder;
	}

	public ItemDescription getFolder() {
		return folder;
	}

	public void setFolder(ItemDescription folder) {
		this.folder = folder;
	}

	@Override
	public String toString() {
		return "ImportedData [folder=" + folder + "]";
	}

}
