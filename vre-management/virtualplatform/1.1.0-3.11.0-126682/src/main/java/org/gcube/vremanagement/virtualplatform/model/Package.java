package org.gcube.vremanagement.virtualplatform.model;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.gcube.common.core.scope.GCUBEScope;

/**
 * A package accepted by an implementation of the {@link TargetPlatform}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public interface Package {
	
	public void setName(String name);
	
	public String getName();
	
	public void setVersion(String version);
	
	public String getVersion();
	
	public void setServiceName(String name);
	
	public String getServiceName();

	public void setServiceClass(String clazz);
	
	public String getServiceClass();
	
	public void setServiceVersion(String version);
	
	public String getServiceVersion();
	
	public void setServiceID(String id);
	
	public String getServiceID();
	
	public void setScope(GCUBEScope scope);
	
	public GCUBEScope getScope();
	
	public void setFolder(File folder);
	
	public File getFolder();
	
	public void setProperties(Properties prop);
	
	public Properties getPropeties();
		
	public void setProfile(String profile);
	
	public String getProfile();
	
	public File getFile();
	
	public void setFile(File file);
	
	public void setTargetPath(String targetPath);
	
	/**
	 * 
	 * @return the context path of this application
	 */
	public String getTargetPath();
	
	public String getDescription();
	
	void setDescription(String description);

	public void setTargetPlatform(String name);
	
	public String getTargetPlatform();
	
	public void setTargetPlatformVersion(int version);
	
	public int getTargetPlatformVersion();
	
	public void setTargetPlatformMinorVersion(int version);
	
	public int getTargetPlatformMinorVersion();

	public void setEntrypoints(List<String> entrypoints);
	
	public List<String> getEntrypoints();
	
}
