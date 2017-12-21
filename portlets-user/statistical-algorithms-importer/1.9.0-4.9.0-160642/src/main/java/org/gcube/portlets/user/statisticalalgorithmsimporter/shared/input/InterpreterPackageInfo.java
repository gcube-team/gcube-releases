package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InterpreterPackageInfo implements Serializable {

	private static final long serialVersionUID = 6638656450670269043L;

	private int id;
	private String name;
	private String version;
	private String details;

	public InterpreterPackageInfo() {
		super();
	}

	/**
	 * 
	 * @param id
	 *            id
	 * @param name
	 *            name
	 * @param version
	 *            version
	 * @param details
	 *            details
	 */
	public InterpreterPackageInfo(int id, String name, String version, String details) {
		super();
		this.id = id;
		this.name = name;
		this.version = version;
		this.details = details;
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

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "InterpreterPackageInfo [id=" + id + ", name=" + name + ", version=" + version + ", details=" + details
				+ "]";
	}

}
