package org.gcube.portlets.admin.accountingmanager.shared.tabs;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class EnableTabs implements Serializable {

	private static final long serialVersionUID = 6831995927636728501L;

	private ArrayList<EnableTab> tabs;

	public EnableTabs() {
		super();
	}

	public EnableTabs(ArrayList<EnableTab> tabs) {
		super();
		this.tabs = tabs;
	}

	public ArrayList<EnableTab> getTabs() {
		return tabs;
	}

	public void setTabs(ArrayList<EnableTab> tabs) {
		this.tabs = tabs;
	}

	@Override
	public String toString() {
		return "EnableTabs [tabs=" + tabs + "]";
	}

}