package org.gcube.common.core.resources;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.resources.service.Configuration;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.resources.service.ServiceDependency;
import org.gcube.common.core.resources.service.Version;
import org.gcube.common.core.scope.GCUBEScope;


/**
 * Specifies the behaviour common to all gCUBE services.
 * 
 * @author Andrea Manzi (ISTI-CNR), Manuele Simi (ISTI-CNR), Fabio Simeoni (University of Strathclyde), Luca Frosini (ISTI-CNR)
 *
 */
public abstract class GCUBEService extends GCUBEResource {
	
	/**
	 * The type of the resource.
	 */
	public static final String TYPE="Service";
	
	
	/** 
	 * The service description.
	 */
	private String description;
	
	/**
	 * The service class.
	 */
	private String clazz;
	
	/**
	 * The service name.
	 */
	private String name;
	
	/**
	 * The service version.
	 */
	private String version;
	
	/**
	 * The configuration of the service.
	 */
	protected Configuration configuration;

	/**
	 * VRE Dependencies 
	 */
	protected List<ServiceDependency> dependencies = new ArrayList<ServiceDependency>();

	/**
	 * Service packages.
	 */
	private List<Package> packages = new ArrayList<Package>();
	
	/**
	 * Service-specific data.
	 */
	protected String specificData;
		
	/**
	 * Service Resource version
	 */
	private static final String VERSION = "1.3.0";
	
	/**
	 * 
	 */
	public GCUBEService() {this.type = TYPE;this.logger.setPrefix(TYPE);}
	
	/**
	 * {@inheritDoc}
	 */
	public synchronized boolean inScope(GCUBEScope ... scopes) {
		if (scopes==null || scopes.length==0) throw new IllegalArgumentException();
		outer: for (GCUBEScope scope : scopes) {
			for (GCUBEScope resScope : this.getScopes().values())
				if (scope.isEnclosedIn(resScope)) continue outer;//resource is in this scope, move to test next scope
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the class of the service.
	 * @return the class.
	 */
	public String getServiceClass() {
		return this.clazz;
	}

	/**
	 * Sets the class of the service.
	 * As the class is a one-time constant, the operation returns successfully from the first invocation only.
	 * @param clazz the class.
	 * @return <code>true</code> if the class was set, <code>false</code> otherwise.
	 */	
	public synchronized boolean setServiceClass(String clazz) {
		if (this.clazz==null) {
			this.clazz=clazz;
			return true;
		}
		logger.warn("Attempt to change class of gCUbe service "+this.getID()+" from "+ this.clazz+" to "+clazz);
		return false;
	}

	/**
	 * Returns the name of the service.
	 * As the name is a one-time constant, the operation returns successfully from the first invocation only. 
	 * @return the name.
	 * @return <code>true</code> if the name was set, <code>false</code> otherwise.
	 */
	public String getServiceName() {
		return this.name;
	}

	/**
	 * Sets the name of the service.
	 * As the class is a one-time constant, the operation returns successfully from the first invocation only.
	 * @param name the name.
	 * @return <code>true</code> if the identifier was set, <code>false</code> otherwise.
	 */
	public synchronized boolean setServiceName(String name) {
		if (this.name==null) {
			this.name=name;
			return true;
		}
		logger.warn("Attempt to change name of gCUbe service "+this.getID()+" from "+ this.name+" to "+name);
		return false;
	}

	/**
	 * Sets the description of the service.
	 * @param description the description.
	 */
	public void setDescription(String description) {
		this.description =description;
	}

	/**
	 * Returns the description of the service
	 * @return the description.
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Returns the service configuration.
	 * @return the configuration.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Sets the service configuration.
	 * @param configuration the configuration.
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Return service-specific data.
	 * @return the data.
	 */
	public String getSpecificData() {
		return specificData;
	}

	/**
	 * Sets service-specific data.
	 * @param specificData the data.
	 */
	public void setSpecificData(String specificData) {
		this.specificData = specificData;
	}

	/**
	 * Returns the list of service packages.
	 * @return the packages.
	 */
	public List<Package> getPackages() {
		return this.packages;
	}


	/**
	 * Returns the service dependencies.
	 * @return the dependencies.
	 */
	public List<ServiceDependency> getDependencies() {
		return this.dependencies;
	}

	/**
	 * Returns the service version
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	
	
	/**
	 * Sets the service version
	 * As the version is a one-time constant, the operation returns successfully from the first invocation only.
	 * @param version the version
	 * @return <code>true</code> if the class was set, <code>false</code> otherwise.
	 */
	public synchronized boolean setVersion(String version) {			
		if (version!=null) {			
			try {
				this.version = /*Version.completeVersion(version)*/ version;
				return true;
			} catch (Exception e) {
				logger.warn("Accepted version are in the form of \\d{1,2}+.\\d{1,2}+.\\d{1,2}");
				return false;
			}
		}else{
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Attempt to change version of gCube service ").append(this.getID()).append(" from ").append(this.version).append(" to ").append(version);
			logger.warn(stringBuilder.toString());
			throw new IllegalArgumentException(stringBuilder.toString());
		}
	}
	
	public String getLastResourceVersion() {return VERSION;}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (!super.equals(obj)) return false;
		
		final GCUBEService other = (GCUBEService) obj;
		
		if (specificData == null) {
			if (other.specificData != null)
				return false;
		} else if (! specificData.equals(other.specificData))
			return false;
		
		if (clazz == null) {
			if (other.clazz  != null)
				return false;
		} else if (! clazz .equals(other.clazz ))
			return false;
		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (! name.equals(other.name))
			return false;
		
		if (configuration == null) {
			if (other.configuration != null)
				return false;
		} else if (! configuration.equals(other.configuration))
			return false;
		
		if (dependencies == null) {
			if (other.dependencies != null)
				return false;
		} else if (! dependencies.equals(other.dependencies))
			return false;
		
		if (packages == null) {
			if (other.packages != null)
				return false;
		} else if (! packages.equals(other.packages))
			return false;
		
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (! version.equals(other.version))
			return false;
		
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (! description.equals(other.description))
			return false;
		
		
		return true;
	}
	
}

