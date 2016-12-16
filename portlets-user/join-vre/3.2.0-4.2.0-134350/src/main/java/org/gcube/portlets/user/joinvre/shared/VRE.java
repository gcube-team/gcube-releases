package org.gcube.portlets.user.joinvre.shared;

import java.io.Serializable;

/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@SuppressWarnings("serial")
public class VRE extends ResearchEnvironment implements Serializable, Comparable<VRE> {
	
	protected VreMembershipType membershipType;
	protected long id;


	public VRE() {
		super();
	}
	
	public VRE(long id, String vreName, String description, String imageURL,
			String infraScope, String friendlyURL, UserBelonging userBelonging) {
		super(vreName, description, imageURL, infraScope, friendlyURL, userBelonging);	
		this.membershipType = VreMembershipType.RESTRICTED;
		this.id = id;
	}
	/**
	 * 
	 * @param id
	 * @param vreName
	 * @param description
	 * @param imageURL
	 * @param infraScope
	 * @param friendlyURL
	 * @param userBelonging
	 * @param membershipType
	 */
	public VRE(long id, String vreName, String description, String imageURL,
			String infraScope, String friendlyURL, UserBelonging userBelonging, VreMembershipType membershipType) {
		super(vreName, description, imageURL, infraScope, friendlyURL, userBelonging);	
		this.membershipType = membershipType;
		this.id = id;
	}
	
	
	public VreMembershipType getMembershipType() {
		return membershipType;
	}

	public void setMembershipType(VreMembershipType membershipType) {
		this.membershipType = membershipType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "VRE [membershipType=" + membershipType + ", id=" + id + ", name=" + this.getName() + "]";
	}

	@Override
	public int compareTo(VRE vre) {
		return this.getName().compareTo(vre.getName());
	}
	
}
