package org.gcube.data.analysis.dataminermanagercl.shared.workspace;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class OutputDataSets implements Serializable {
	private static final long serialVersionUID = -8235652292513149983L;

	private ItemDescription folder;

	public OutputDataSets() {
		super();
	}

	public OutputDataSets(ItemDescription folder) {
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
		return "ComputedData [folder=" + folder + "]";
	}

}
