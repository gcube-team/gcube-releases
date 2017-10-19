package org.gcube.portlets.user.td.gwtservice.shared.share;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ShareTabResource implements Serializable {

	private static final long serialVersionUID = -4803711252511329006L;
	
	private TabResource tabResource;
	private ArrayList<Contacts> contacts;

	public ShareTabResource() {

	}

	public ShareTabResource(TabResource tabResource, ArrayList<Contacts> contacts) {
		this.tabResource = tabResource;
		this.contacts = contacts;

	}

	public TabResource getTabResource() {
		return tabResource;
	}

	public void setTabResource(TabResource tabResource) {
		this.tabResource = tabResource;
	}

	public ArrayList<Contacts> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<Contacts> contacts) {
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		return "ShareInfo [tabResource=" + tabResource + ", contacts="
				+ contacts + "]";
	}

	
	

}
