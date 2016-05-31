package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ProjectInfo implements Serializable {

	private static final long serialVersionUID = 7304965177776383842L;
	private String algorithmName;
	private String algorithmDescription;
	private ArrayList<RequestedVRE> listRequestedVRE;

	public ProjectInfo() {
		super();
	}

	public ProjectInfo(String algorithmName, String algorithmDescription,
			ArrayList<RequestedVRE> listRequestedVRE) {
		super();
		this.algorithmName = algorithmName;
		this.algorithmDescription = algorithmDescription;
		this.listRequestedVRE = listRequestedVRE;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public String getAlgorithmNameToUpper() {
		return algorithmName.toUpperCase();
	}

	public String getAlgorithmNameToClassName() {
		return algorithmName.replaceAll("_", "");
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public String getAlgorithmDescription() {
		return algorithmDescription;
	}

	public void setAlgorithmDescription(String algorithmDescription) {
		this.algorithmDescription = algorithmDescription;
	}

	public ArrayList<RequestedVRE> getListRequestedVRE() {
		return listRequestedVRE;
	}

	public void setListRequestedVRE(ArrayList<RequestedVRE> listRequestedVRE) {
		this.listRequestedVRE = listRequestedVRE;
	}

	@Override
	public String toString() {
		return "ProjectInfo [algorithmName=" + algorithmName
				+ ", algorithmDescription=" + algorithmDescription
				+ ", listRequestedVRE=" + listRequestedVRE + "]";
	}

	

}
