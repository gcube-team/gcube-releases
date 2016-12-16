package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class InterpreterPackageInfo implements Serializable {

	private static final long serialVersionUID = 6638656450670269043L;

	private int id;
	private String name;
	private String version;

	public InterpreterPackageInfo() {
		super();
	}
	
	/**
	 * 
	 * @param id 
	 * @param name Package Name
	 * @param version Package Version
	 */
	public InterpreterPackageInfo(int id, String name, String version) {
		super();
		this.id = id;
		this.name = name;
		this.version = version;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "InterpreterPackageInfo [id=" + id + ", name=" + name
				+ ", version=" + version + "]";
	}

}
