package org.gcube.portlets.widgets.dataminermanagerwidget.shared.workspace;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class Computations implements Serializable {

	private static final long serialVersionUID = 7375248981531583668L;
	private ItemDescription folder;

	public Computations() {
		super();
	}

	public Computations(ItemDescription folder) {
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
		return "Computations [folder=" + folder + "]";
	}

}
