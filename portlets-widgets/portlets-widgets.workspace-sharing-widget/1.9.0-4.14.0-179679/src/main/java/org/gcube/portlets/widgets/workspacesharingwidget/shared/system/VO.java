package org.gcube.portlets.widgets.workspacesharingwidget.shared.system;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 2.0 Jan 10th 2012
 */
@SuppressWarnings("serial")
public class VO extends ResearchEnvironment implements Comparable<VO>, Serializable{
	
	private boolean isRoot;
	
	private ArrayList<VRE> vres = new ArrayList<VRE>();
	
	public VO() {
		super();
	}
	
	public VO(String voName, String description, String imageURL,
			String vomsGroupName, String friendlyURL,
			UserBelonging userBelonging, boolean isRoot, ArrayList<VRE> vres) {
		super(voName, description, imageURL, vomsGroupName, friendlyURL, userBelonging);
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
	
	public void addVRE(VRE toAdd) {
		if (vres == null)
			vres = new ArrayList<VRE>();
		vres.add(toAdd);
	}
	
	public int compareTo(VO voToCompare) {
		return (this.vres.size() >= voToCompare.getVres().size()) ? 1 : -1;
	}
	
}
