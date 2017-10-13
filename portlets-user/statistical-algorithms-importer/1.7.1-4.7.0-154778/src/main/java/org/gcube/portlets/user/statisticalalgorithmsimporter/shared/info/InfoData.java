package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.info;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InterpreterPackageInfo;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InfoData implements Serializable {

	private static final long serialVersionUID = 6804461443058040026L;
	private String username;
	private String fullname;
	private String email;
	private String language;
	private String algorithmName;
	private String className;
	private String algorithmDescription;
	private String algorithmCategory;
	private String interpreterVersion;
	private ArrayList<InterpreterPackageInfo> packagesInfo;

	public InfoData() {
	}

	public InfoData(String username, String fullname, String email, String language, String algorithmName,
			String className, String algorithmDescription, String algorithmCategory, String interpreterVersion,
			ArrayList<InterpreterPackageInfo> packagesInfo) {
		super();
		this.username = username;
		this.fullname = fullname;
		this.email = email;
		this.language = language;
		this.algorithmName = algorithmName;
		this.className = className;
		this.algorithmDescription = algorithmDescription;
		this.algorithmCategory = algorithmCategory;
		this.interpreterVersion = interpreterVersion;
		this.packagesInfo = packagesInfo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getAlgorithmDescription() {
		return algorithmDescription;
	}

	public void setAlgorithmDescription(String algorithmDescription) {
		this.algorithmDescription = algorithmDescription;
	}

	public String getAlgorithmCategory() {
		return algorithmCategory;
	}

	public void setAlgorithmCategory(String algorithmCategory) {
		this.algorithmCategory = algorithmCategory;
	}

	public String getInterpreterVersion() {
		return interpreterVersion;
	}

	public void setInterpreterVersion(String interpreterVersion) {
		this.interpreterVersion = interpreterVersion;
	}

	public ArrayList<InterpreterPackageInfo> getPackagesInfo() {
		return packagesInfo;
	}

	public void setPackagesInfo(ArrayList<InterpreterPackageInfo> packagesInfo) {
		this.packagesInfo = packagesInfo;
	}

	
	@Override
	public String toString() {
		return "InfoData [username=" + username + ", fullname=" + fullname + ", email=" + email + ", language="
				+ language + ", algorithmName=" + algorithmName + ", className=" + className + ", algorithmDescription="
				+ algorithmDescription + ", algorithmCategory=" + algorithmCategory + ", interpreterVersion="
				+ interpreterVersion + ", packagesInfo=" + packagesInfo + "]";
	}

}