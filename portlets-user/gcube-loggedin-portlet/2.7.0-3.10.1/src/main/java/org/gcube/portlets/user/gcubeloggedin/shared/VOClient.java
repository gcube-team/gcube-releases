package org.gcube.portlets.user.gcubeloggedin.shared;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author massi
 *
 */
@SuppressWarnings("serial")
public class VOClient extends VObject implements Comparable<VOClient>, Serializable {
	
	private boolean isRoot;
	private List<VREClient> vres;
	
	public VOClient() {	}

	public VOClient(
			String name, 
			String groupName,
			String description,
			String imageURL,
			String friendlyURL,
			UserBelongingClient userBelonging) {
		super(name, groupName, description, imageURL, friendlyURL, userBelonging, true, true);
		// TODO Auto-generated constructor stub
	}

	public VOClient(
			String name,
			String groupName,
			String description,
			String imageURL,
			String friendlyURL,
			UserBelongingClient userBelonging,
			boolean isRoot, List<VREClient> vres) {
		super(name, groupName, description, imageURL, friendlyURL, userBelonging, true, true);
		this.isRoot = isRoot;
		this.vres = vres;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public List<VREClient> getVres() {
		return vres;
	}

	public void setVres(List<VREClient> vres) {
		this.vres = vres;
	}

	public int compareTo(VOClient voToCompare) {		
		return (this.vres.size() >= voToCompare.getVres().size()) ? 1 : -1;
	}
}