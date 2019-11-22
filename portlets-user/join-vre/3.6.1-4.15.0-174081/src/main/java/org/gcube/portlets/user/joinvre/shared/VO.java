package org.gcube.portlets.user.joinvre.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
@SuppressWarnings("serial")
public class VO extends ResearchEnvironment implements Comparable<VO>, Serializable{
	
	private boolean isRoot;
	
	private ArrayList<VRE> vres = new ArrayList<VRE>();
	/**
	 * 
	 */
	public VO() {
		super();
	}
	/**
	 * 
	 * @param voName .
	 * @param description .
	 * @param imageURL .
	 * @param vomsGroupName .
	 * @param friendlyURL .
	 * @param userBelonging .
	 * @param isRoot .
	 * @param vres .
	 */
	public VO(String voName, String description, String imageURL,
			String infraScope, String friendlyURL,
			UserBelonging userBelonging, boolean isRoot, ArrayList<VRE> vres) {
		super(voName, description, imageURL, infraScope, friendlyURL, userBelonging);
		this.isRoot = isRoot;
		this.vres = vres;
	}
	
	public boolean isRoot() {
		return isRoot;
	}
	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	public ArrayList<VRE> getVres() {
		return vres;
	}
	public void setVres(ArrayList<VRE> vres) {
		this.vres = vres;
	}
	/**
	 * 
	 * @param toAdd
	 */
	public void addVRE(VRE toAdd) {
		if (vres == null)
			vres = new ArrayList<VRE>();
		vres.add(toAdd);
	}
	/**
	 * compare the number of vres
	 */
	public int compareTo(VO voToCompare) {
		return (this.vres.size() >= voToCompare.getVres().size()) ? 1 : -1;
	}
	
}
