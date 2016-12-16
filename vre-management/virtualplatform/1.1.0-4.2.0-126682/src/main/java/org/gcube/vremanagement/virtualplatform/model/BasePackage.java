package org.gcube.vremanagement.virtualplatform.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.gcube.common.core.scope.GCUBEScope;

/**
 * 
 * Abstract model for {@link Package}
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class BasePackage implements Package {


	String name, version, serviceID, serviceName,serviceClass, serviceVersion, profile, targetPath, description;
	File file;
	GCUBEScope scope;
	private Properties properties;
	private File folder;
	private List<String> entryPoints = new ArrayList<String>();
	
	protected BasePackage() {}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setServiceName(String name) {
		this.serviceName = name;
	}

	@Override
	public String getServiceName() {
		return this.serviceName;
	}

	@Override
	public void setServiceClass(String clazz) {
		this.serviceClass = clazz;
	}

	@Override
	public String getServiceClass() {
		return this.serviceClass;
	}

	@Override
	public void setScope(GCUBEScope scope) {
		this.scope = scope;

	}

	@Override
	public GCUBEScope getScope() {
		return this.scope;
	}

	@Override
	public void setFolder(File folder) {
		this.folder = folder;
	}

	@Override
	public File getFolder() {
		return this.folder;
	}

	@Override
	public void setProperties(Properties prop) {
		this.properties = prop;
	}

	@Override
	public Properties getPropeties() {
		return this.properties;
	}

	@Override
	public void setProfile(String profile) {
		this.profile = profile;

	}

	@Override
	public String getProfile() {
		return this.profile;
	}

	/**
	 * @return the targetPath
	 */
	@Override
	public String getTargetPath() {
		return targetPath;
	}

	/**
	 * @param targetPath the targetPath to set
	 */
	@Override
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	/**
	 * @return the file
	 */
	@Override
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	@Override
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the version
	 */
	@Override
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the serviceVersion
	 */
	@Override
	public String getServiceVersion() {
		return serviceVersion;
	}

	/**
	 * @param serviceVersion the serviceVersion to set
	 */
	@Override
	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	@Override
	public void setEntrypoints(List<String> entryPoints) {
		this.entryPoints = entryPoints;
	}
	
	@Override
	public List<String> getEntrypoints() {
		return this.entryPoints;
	}

	@Override
	public void setServiceID(String id) {
		this.serviceID = id;
	}

	@Override
	public String getServiceID() {
		return this.serviceID;
	}
}
