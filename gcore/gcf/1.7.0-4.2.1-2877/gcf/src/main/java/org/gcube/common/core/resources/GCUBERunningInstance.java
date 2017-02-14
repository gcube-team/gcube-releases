package org.gcube.common.core.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.resources.runninginstance.AccessPoint;
import org.gcube.common.core.resources.runninginstance.DeploymentData;
import org.gcube.common.core.resources.runninginstance.RIEquivalenceFunction;
import org.gcube.common.core.resources.runninginstance.RunningInstanceSecurity;
import org.gcube.common.core.resources.runninginstance.ScopedAccounting;
import org.gcube.common.core.scope.GCUBEScope;


/**
 * Specifies the behaviour common to all gCUBE services.
 * 
 * @author Andrea Manzi, Manuele Simi (CNR)
 *
 */
public abstract class GCUBERunningInstance extends GCUBEResource {

	/**
	 * The type of the resource.
	 */
	public static final String TYPE="RunningInstance";
	

	/** Creates a new instance. */
	public GCUBERunningInstance() {this.type = TYPE;this.logger.setPrefix(TYPE);}
	
	/** The descriptirn of the instance.*/
	private String description;
	
	/** The identifier of the GHN on which the instance is deployed.*/
	private String ghn;
	
	/** The identifier of the instance's service.*/
	private String serviceID;
	
	/** The class of the instance's service. */
	private String clazz;
	
	/**The name of the instance's service.*/
	private String name;
	
	/** Data specific to the instance's service.*/
	private String specificData;
	
	/** The version of the instance's service.*/
	private String version;
	
	/**The security configuration of the instance.*/
	private  List<RunningInstanceSecurity> security = new ArrayList<RunningInstanceSecurity>();
	
	/**
	 * RunningInstance Equivalence functions 
	 */
	private  List<RIEquivalenceFunction> functions = new ArrayList<RIEquivalenceFunction>();
	
	/**
	 * Accounting information 
	 */
	private  HashMap<GCUBEScope,ScopedAccounting> accounting = new HashMap<GCUBEScope,ScopedAccounting>();
	
	private PlatformDescription platform;
	
	/**
	 * Get the ScopedAccoutingMap
	 * 
	 * @return The ScopedAccounting map
	 */
	public HashMap<GCUBEScope, ScopedAccounting> getAccounting() {
		return accounting;
	}

	/**
	 * Set the ScopedAccounting
	 * @param The ScopedAccounting map
	 */
	public void setAccounting(HashMap<GCUBEScope, ScopedAccounting> accounting) {
		this.accounting = accounting;
	}

	/**
	 * 
	 * Running Instance deployment information
	 */
	private DeploymentData deploymentData;
	
	/** The access point of the instance.*/
	private AccessPoint accessPoint;
	
	/**
	 * RI Resource version
	 */
	private static final String VERSION = "1.3.0";
	
	/**
	 * Return the class of the instance's service.
	 * @return the class.
	 */
	public String getServiceClass() {return this.clazz;}

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
		logger.warn("Attempt to change class of gCube instance "+this.getID());
		return false;
	}

	/**
	 * Returns the name of the service.
	 * As the name is a one-time constant, the operation returns successfully from the first invocation only. 
	 * @return the name.
	 * @return <code>true</code> if the name was set, <code>false</code> otherwise.
	 */
	public String getServiceName() {return this.name;}

	/**
	 * Sets the name of the service.
	 * As the class is a one-time constant, the operation returns successfully from the first invocation only.
	 * @param name the name.
	 * @return <code>true</code> if the name was set, <code>false</code> otherwise.
	 */
	public synchronized boolean setServiceName(String name) {
		if (this.name==null) {
			this.name=name;
			return true;
		}
		logger.warn("Attempt to change name of gCube instance "+this.getID());
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
	 * Returns the service dependencies.
	 * @return the dependencies.
	 */
	public List<RunningInstanceSecurity> getRunningInstanceSecurity() {
		return this.security;
	}
	

	/**
	 * Sets the deploymentInformation of the runninginstance
	 * @param data deployment data information
	 */
	public void setDeploymentData(DeploymentData data) {
		this.deploymentData =data;
	}

	/**
	 * Returns the deploymentInformation of the runninginstance
	 * @return the  deployment data information
	 */
	public DeploymentData getDeploymentData() {
		return this.deploymentData;
	}
	

	/**
	 * Returns the RunningInstance Equivalence functions 
	 * @return the RunningInstance Equivalence functions
	 */
	public List<RIEquivalenceFunction> getRIEquivalenceFunctions() {
		return this.functions;
	}
	

	/**
	 * Sets Access Point info 
	 * @param accessPoint Access Point info 
	 */
	public void setAccessPoint(AccessPoint accessPoint) {
		this.accessPoint =accessPoint;
	}

	/**
	 * Returns the Access Point info 
	 * @return the  Access Point info 
	 */
	public AccessPoint getAccessPoint() {
		return this.accessPoint;
	}
	
	/**
	 * Sets the specific data of the Running Instance.
	 * @param specificData the datat.
	 */
	public void setSpecificData(String specificData) {
		this.specificData =specificData;
	}
	
	/**
	 * Returns the running Instance specific data
	 * @return the specificData.
	 */
	public String getSpecificData() {
		return this.specificData;
	}
	

	/**
	 * Sets the ghn id.
	 * @param id the ghn id.
	 */
	public void setGHNID(String id) {
		this.ghn =id;
	}

	/**
	 * Returns the ghn id
	 * @return the ghn id.
	 */
	public String getGHNID() {
		return this.ghn;
	}
	
	/**
	 * Sets the serviceID.
	 * @param id the serviceID.
	 */
	public void setServiceID(String id) {
		this.serviceID =id;
	}

	/**
	 * Returns the serviceID
	 * @return the serviceID.
	 */
	public String getServiceID() {
		return this.serviceID;
	}

	public String getInstanceVersion() {
		return this.version;
	}

	/**
	 * Sets the instance version
	 * As the version is a one-time constant, the operation returns successfully from the first invocation only.
	 * @param version the version
	 * @return <code>true</code> if the class was set, <code>false</code> otherwise.
	 */
	public synchronized boolean setInstanceVersion(String version) {
		this.version = version;
		return true;
	}
	
	public String getLastResourceVersion() {return VERSION;}
	
	/**
	 * @param platform the platform to set
	 */
	public void setPlatform(PlatformDescription platform) {
		this.platform = platform;
	}

	/**
	 * @return the platform
	 */
	public PlatformDescription getPlatform() {
		return platform;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (!super.equals(obj)) return false;
		
		final GCUBERunningInstance other = (GCUBERunningInstance) obj;
		
		if (specificData == null) {
			if (other.specificData != null)
				return false;
		} else if (! specificData.equals(other.specificData))
			return false;
		
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (! clazz.equals(other.clazz))
			return false;
		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (! name.equals(other.name))
			return false;
		
		if (security == null) {
			if (other.security != null)
				return false;
		} else if (! security.equals(other.security))
			return false;
		
		if (deploymentData == null) {
			if (other.deploymentData != null)
				return false;
		} else if (! deploymentData.equals(other.deploymentData))
			return false;
		
		if (functions == null) {
			if (other.functions != null)
				return false;
		} else if (! functions.equals(other.functions))
			return false;
		
		if (accessPoint == null) {
			if (other.accessPoint != null)
				return false;
		} else if (! accessPoint.equals(other.accessPoint))
			return false;
		
		if (ghn == null) {
			if (other.ghn != null)
				return false;
		} else if (! ghn.equals(other.ghn))
			return false;
		
		if (serviceID == null) {
			if (other.serviceID != null)
				return false;
		} else if (! serviceID.equals(other.serviceID))
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
		
		if (accounting == null) {
			if (other.accounting != null)
				return false;
		} else if (! accounting.equals(other.accounting))
			return false;
		
		return true;
	}

}
