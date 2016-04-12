package org.gcube.common.vremanagement.deployer.impl.state;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.contexts.GHNContext.Status;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEWSFieldsSerializableResource;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report.ReportNotFoundException;
import org.gcube.common.vremanagement.deployer.impl.resources.BasePackage;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;
import org.gcube.common.vremanagement.deployer.impl.resources.Converter;
import org.gcube.common.vremanagement.deployer.impl.resources.KeyData;
import org.gcube.common.vremanagement.deployer.stubs.deployer.DeployedPackage;


/**
 * Deployer stateful resource. 
 * The state is represented by the {@link BaseTypedPackage} serialized instances of the packages deployed on the gHN
 * <ul>
 *  <li> a CountableHashMap<BasePackage.Key, BaseTypedPackage> object
 *  <li> a CountableHashSet<File> object
 *  <li> numOfDeployedPackages RP
 *  <li> DeployedPackage[] RP
 *  <li> LastDeployment ID RP
 * </ul>
 * @author Manuele Simi (ISTI-CNR)
 * @see WSObjectsSerializableResource
 * @see GCUBEWSObjects2FilePersistenceDelegate
 *
 */
public class DeployerResource extends GCUBEWSFieldsSerializableResource {

	private static final long serialVersionUID = 1432537954905035439L;	

	protected static final String NumPackagesRP="NumberOfDeployedPackages";
	
	protected static final String DeployedPackagesRP="DeployedPackages";
	
	protected static final String LastDeploymentRP="LastDeployment";
		
	final static String[] RPNames = {DeployedPackagesRP, LastDeploymentRP};
	
	/** RP <em>DeployedPackages</em>*/
	List<DeployedPackage> packages = new ArrayList<DeployedPackage>();
	
	/** RP <em>NumberOfDeployedPackages</em>*/
	int numOfDeployedPackages = 0;
		
	/** Serialized list of deployed packages */
	private CountableHashMap<KeyData, BaseTypedPackage> packageinfo = new CountableHashMap<KeyData, BaseTypedPackage>();
	
	/** Serialized list of installed files*/
	private CountableHashSet<File> filelist = new CountableHashSet<File>();
	
	/** list of objects to serialize */
	private  List<Serializable> objsToSerialize = new ArrayList<Serializable>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void initialise(Object... arg0) throws Exception {	
		objsToSerialize.add(packageinfo);
		objsToSerialize.add(filelist);
		//this.setNumberOfDeployedPackages(numOfDeployedPackages);
		this.setDeployedPackages(new DeployedPackage[0]);
		this.printState();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String[] getPropertyNames() {		
		return RPNames;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	 public synchronized List<? extends Serializable> getFieldsToSerialize() {
		return this.objsToSerialize;
	}
	
	/**
	 * @{inerithDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	 public synchronized void setFieldsToSerialize(List<? extends Serializable> objs) {
		this.objsToSerialize = (ArrayList<Serializable>) objs;		
		this.packageinfo = (CountableHashMap<KeyData, BaseTypedPackage>) this.objsToSerialize.get(0);
		this.objsToSerialize.set(0, this.packageinfo);
		this.filelist = (CountableHashSet<File>) this.objsToSerialize.get(1);		
		this.objsToSerialize.set(1, this.filelist);
	}
	
	/** 
	 * Sets for the RP <em>LastDeploymentRP</em>

	 * @param lastDeployment the callback ID related to the last deployment operation invoked
	 * 	
	 */
	 public synchronized void setLastDeployment (String lastDeployment) {
		this.getResourcePropertySet().get(LastDeploymentRP).clear();
		this.getResourcePropertySet().get(LastDeploymentRP).add(lastDeployment);
	}
	
	 public synchronized  void clearLastDeployment () {
		this.getResourcePropertySet().get(LastDeploymentRP).clear();
	}
	
	/** 
	 * Gets the RP <em>LastDeploymentRP</em>
	 * 
	 * @return  the callback ID related to the last deployment operation invoked
	 */ 
	 public synchronized String getLastDeployment () {
		if ((this.getResourcePropertySet().get(LastDeploymentRP) != null) && (this.getResourcePropertySet().get(LastDeploymentRP).size() > 0))
			return (String) this.getResourcePropertySet().get(LastDeploymentRP).get(0);
		else return "";
	}
	

	/** 
	 * Gets for the RP <em>DeployedPackages</em>
	 * 
	 * @return  the packages deployed on the node
	*/
	@SuppressWarnings("unchecked")
	 public synchronized DeployedPackage[] getDeployedPackages () {
		if ((this.getResourcePropertySet().get(DeployedPackagesRP) != null) && (this.getResourcePropertySet().get(DeployedPackagesRP).size() > 0)) {
			this.packages = (List<DeployedPackage>) this.getResourcePropertySet().get(DeployedPackagesRP).get(0);
			return packages.toArray(new DeployedPackage[this.packages.size()]);		
		} else return new DeployedPackage[0];
	}	
	
	/**
	 * Sets for the RP <em>DeployedPackages</em>
	 * 
	 * @param packages the packages deployed on the node
	 */
	 public synchronized void setDeployedPackages (DeployedPackage[] packages) {
		this.packages = new ArrayList<DeployedPackage>(packages.length);
		for (int i=0; i<packages.length; i++) this.packages.add(packages[i]);		
		this.getResourcePropertySet().get(DeployedPackagesRP).clear();
		this.getResourcePropertySet().get(DeployedPackagesRP).add(this.packages);
		//this.store();
	}

	/**
	 * Adds a new package to the resource state
	 * 
	 * @param pack the package to add
	 */
	 public synchronized void addPackage(BaseTypedPackage pack) {
		//add the package to the RPs		
		this.packages.add(Converter.toDeployablePackage(pack));
		//... and to the state
		this.packageinfo.put(pack.getKey(), pack);		
		this.filelist.addAll(pack.getPackageFileList());
		this.store();
		this.printState();
		//add the package to the GHN profile too
		if (this.packageinfo.getCounter(pack.getKey()) == 1) {
			GHNContext.getContext().getGHN().getDeployedPackages().add(Converter.toGHNPackage(pack));
			GHNContext.getContext().setStatus(Status.UPDATED);
		}
	}

	/**
	 * Gets a previously deployed package from the service's state
	 * @param key the key of the package to look for
	 * @return the package
	 * @throws NoSuchPackageException if the package is not in the service's state
	 */
	 public synchronized BaseTypedPackage getPackage(KeyData key) throws NoSuchPackageException {
		if (this.packageinfo.containsKey(key))
			return this.packageinfo.get(key);
		else {
			logger.warn("Package key " + key.toString()+ " not found" );
			throw new NoSuchPackageException(key.getPackageName());
		}
		
	}
	
	/**
	 * Checks if a package is already deployed here
	 * @param pack the package to check
	 */
	 public synchronized boolean isDeployed(BaseTypedPackage pack) {
		if ( (this.packageinfo.containsKey(pack.getKey())) && (this.packageinfo.getCounter(pack.getKey()) > 0))
			return true;
		return false;
	}


	/**
	 * Removes a package from the resource state
	 * 
	 * @param pack the package to remove
	 */
	 public synchronized void removePackage(BaseTypedPackage pack) throws NoSuchPackageException {
		this.printState();
		this.packageinfo.remove(pack.getKey());
		this.filelist.removeAll(pack.getPackageFileList());
		//remove the package from the state
		if (this.packageinfo.getCounter(pack.getKey()) == 0) {			 
			this.packages.remove(Converter.toDeployablePackage(pack));			
			this.store();		
			this.printState();
			//remove the package to the GHN profile too		
			org.gcube.common.core.resources.GCUBEHostingNode.Package newPack = Converter.toGHNPackage(pack);
			org.gcube.common.core.resources.GCUBEHostingNode.Package packToRemove = null;
			for (org.gcube.common.core.resources.GCUBEHostingNode.Package toCheck :  GHNContext.getContext().getGHN().getDeployedPackages()) {
				if ( (toCheck.getPackageName().equals(newPack.getPackageName())) &&					
					 (toCheck.getPackageVersion().equals(newPack.getPackageVersion())) &&
					 (toCheck.getServiceName().equals(newPack.getServiceName())) &&
					 (toCheck.getServiceClass().equals(newPack.getServiceClass())) &&
					 (toCheck.getServiceVersion().equals(newPack.getServiceVersion()))
						) {
					packToRemove = toCheck;
					break;
				}
			}			
			if (packToRemove != null ) {
				GHNContext.getContext().getGHN().getDeployedPackages().remove(packToRemove);
				GHNContext.getContext().setStatus(Status.UPDATED);
			} 
		}
	}

	private void printState() {
		logger.trace("Installed files: \n" + this.filelist);
		logger.trace("Deployed packages: \n" + this.packageinfo);
	}
	
	/** 
	 * Gets the list of packages to be updated with new Scopes
	 * @return the list of packages to update
	 */
	public Set<? extends BasePackage> getPackagesToUpdate() {
		Set<BasePackage> packages = new HashSet<BasePackage>();
		for (BasePackage pack : this.packageinfo.values()) {
			if (pack.getScopesToAdd().size() > 0)
				packages.add(pack);
		}
		return packages;
	}

	/**
	 * Gets the last deployment report
	 * @return the last deployment report
	 * @throws ReportNotFoundException if the report is not valid or does not exist
	 */
	public Report getLastReport() throws ReportNotFoundException {
		try {
			if ((this.getLastDeployment() != null) && (this.getLastDeployment().compareTo("")!=0)) {
				logger.trace("Last deployment report is " + this.getLastDeployment());
				return Report.load(this.getLastDeployment());
			}
		} catch (ReportNotFoundException e) {
			logger.debug("No deployment report available since the latest shut down");
		} catch (Exception e) {
			logger.error("Unable to read the last deployment report", e);
		}
		throw new ReportNotFoundException();
	}
	/**
	 * Decides whether or not a package can be undeployed
	 * @param base the package to check
	 * @param scopes the undeployment scope(s)
	 * @return true if the package can be undeployed, false otherwise
	 */
	
	public boolean isUndeployable(BaseTypedPackage base, Set<GCUBEScope> scopes) {
		if (scopes.size() == 0)
			return true; //by default, no scope means removing the package from all the scopes
		
		if (scopes.containsAll(base.getScopes()))
			return true; // the package is in the same scopes of the undeployment request
		
		/**for (File packageFile : base.getPackageFileList()) {
			if (this.filelist.getCounter(packageFile) >1) {
				return false;
			}
		}	*/	
		 		
		return false;
	}
	
	
	/** No package on the gHN has been found */
	public static class NoSuchPackageException extends Exception {	
		/**
		 * @param packageName the package name
		 */
		public NoSuchPackageException(String packageName) {
			super("no dynamic package " + packageName + " found in the gHN state (it might be statically deployed)");
		}

	private static final long serialVersionUID = 3102419117086905179L;	}
	
}
