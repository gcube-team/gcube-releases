package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class InterpreterInfo implements Serializable {

	private static final long serialVersionUID = -7259162372427984451L;

	private String version;
	private ArrayList<InterpreterPackageInfo> interpreterPackagesInfo;

	public InterpreterInfo() {
		super();
	}

	public InterpreterInfo(String version,
			ArrayList<InterpreterPackageInfo> interpreterPackagesInfo) {
		super();
		this.version = version;
		this.interpreterPackagesInfo = interpreterPackagesInfo;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ArrayList<InterpreterPackageInfo> getInterpreterPackagesInfo() {
		return interpreterPackagesInfo;
	}

	public void setInterpreterPackagesInfo(
			ArrayList<InterpreterPackageInfo> interpreterPackagesInfo) {
		this.interpreterPackagesInfo = interpreterPackagesInfo;
	}

	@Override
	public String toString() {
		return "InterpreterInfo [version=" + version
				+ ", interpreterPackagesInfo=" + interpreterPackagesInfo + "]";
	}

}
