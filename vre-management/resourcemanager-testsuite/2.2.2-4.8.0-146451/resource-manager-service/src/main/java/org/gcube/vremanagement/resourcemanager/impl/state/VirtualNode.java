package org.gcube.vremanagement.resourcemanager.impl.state;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGHNQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.common.vremanagement.deployer.stubs.deployer.DeployParameters;
import org.gcube.common.vremanagement.deployer.stubs.deployer.DeployerPortType;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UndeployParameters;
import org.gcube.common.vremanagement.deployer.stubs.deployer.service.DeployerServiceAddressingLocator;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.reporting.DeployerReport;

/**
 * A target node for deployment and undeployment operations
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class VirtualNode {

	/** Object logger */
	protected final GCUBELog logger=new GCUBELog(this);		
	
	/**the callback identifier to send to the Deployer*/
	private String callbackID;
	
	transient private String ghnID = "";
		
	private GCUBEScope scope = null;
		
	private boolean isWorking = false;
	
	private Set<PackageInfo> packagesToAdd = new HashSet<PackageInfo>();

	private Set<PackageInfo> packagesToRemove = new HashSet<PackageInfo>();	
	
	private Set<PackageInfo> packagesToUpgrade = new HashSet<PackageInfo>();	
	
	private long lastActivity;
	
	//here only for backward compatibility (deserialization will fail)
	private String name = "";
		
	//if we do not receive a deployment session from the node after one hour, we assume that the node is not working anymore 
	private static final  long MAX_ACTIVITY = 3600 * 1000; //1h in milliseconds

	private VirtualNode() {}
	
	protected static VirtualNode fromID(String ID, GCUBEScope scope) throws NoGHNFoundException {
		VirtualNode node =  new VirtualNode();
		node.scope = scope;
		node.ghnID = ID;
		node.name = node.detectName();
		return node;
	}
	
	protected static VirtualNode fromName(String name, GCUBEScope scope) throws NoGHNFoundException {
		VirtualNode node =  new VirtualNode();
		node.scope = scope;
		node.name = name;
		node.ghnID = node.detectID();
		return node;
	}
	
	public void setCallbackID(String id) {
		this.callbackID = id;
	}
	
	/**
	 * Detects the name of this node
	 * @return the name as reported in the profile
	 * @throws NoGHNFoundException
	 */
	private String detectName() throws NoGHNFoundException {
		logger.debug("Detecting name for " + this.ghnID);
		try {
			 ISClient client =  GHNContext.getImplementation(ISClient.class);			
			 GCUBEGHNQuery query = client.getQuery(GCUBEGHNQuery.class);
			 query.addAtomicConditions(new AtomicCondition("/ID/text()", this.ghnID));
			 return client.execute(query, this.scope).get(0).getNodeDescription().getName();			
		} catch (Exception e) {
			logger.error("unable to find the target GHN (ghnID=" + this.ghnID + ")", e);
			throw new NoGHNFoundException ("unable to find the target GHN (ghnID=" + this.ghnID + ")");
		}
	}

	/**
	 * Detect the identifier of this node
	 * @return the name as reported in the profile
	 * @throws NoGHNFoundException
	 */
	private String detectID() throws NoGHNFoundException {
		logger.debug("Detecting ID for " + this.name);
		try {
			 ISClient client =  GHNContext.getImplementation(ISClient.class);
			 logger.trace("client class: " + client.getClass().getCanonicalName());
			 GCUBEGHNQuery query = client.getQuery(GCUBEGHNQuery.class);
			 if (query == null)
				 throw new Exception("GCUBEGHNQuery not found");
			 query.addAtomicConditions(new AtomicCondition("/Profile/GHNDescription/Name/text()", this.name));
			 return client.execute(query, this.scope).get(0).getID();			
		} catch (Exception e) {
			logger.error("unable to find the target GHN (name=" + this.name + ")", e);
			throw new NoGHNFoundException ("unable to find the target GHN (name=" + this.name + ")");
		}
	}

	/**
	 * Sets the packages to remove in the next {@link #deploy()} invocation
	 * 
	 * @param packages the list of packagesT to add
	 */
	public synchronized void setPackagesToAdd(Set<PackageInfo> packages) {
		//check if there is a node with all the packagesToAdd already scheduled to be deployed
		//if so, the service will be already activated there, therefore there is nothing to do
		//if (this.hasPackages(packages))
		//	return;		 
		this.packagesToAdd.addAll(packages);		
	}
	
	/**
	 * Gets the packagesToAdd available on this node
	 * 
	 * @return the packagesToAdd
	 */
	protected Set<PackageInfo> getPackages() {
		return this.packagesToAdd;
	}
	
	/**
	 * Checks if a list of packagesToAdd is already on this node
	 * 
	 * @param packagesToAdd the list of packagesToAdd to check
	 * @return true if all the packagesToAdd are already on this node, false otherwise
	 */
	protected boolean hasPackages(Set<PackageInfo> packages) {
		
		if (packages.size() == 0)
			return false;				
		if (this.packagesToAdd.size() == 0)
			return false;		
		input: for (PackageInfo inputPackage : packages) {
			for (PackageInfo nodePackage : this.packagesToAdd) {
				if ( (inputPackage.getServiceClass().compareToIgnoreCase(nodePackage.getServiceClass())==0) &&
						(inputPackage.getServiceName().compareToIgnoreCase(nodePackage.getServiceName())==0) &&
						(inputPackage.getServiceVersion().compareToIgnoreCase(nodePackage.getServiceVersion())==0) &&
						(inputPackage.getName().compareToIgnoreCase(nodePackage.getName())==0) &&
						(inputPackage.getVersion().compareToIgnoreCase(nodePackage.getVersion())==0))
					continue input;
				
			}
			return false; //if we arrive here, no matching package has been found
		}
		return true;
	}
		

	/**
	 * Gets the node ghnID
	 * 
	 * @return the node ghnID
	 */
	public String getID() {	
		if (this.ghnID.equals(""))
			throw new RuntimeException();
		return this.ghnID;
	}
	
	/**
	 * Sends a request to the physical node to deploy the active list of packages to add
	 * 
	 * @throws Exception if the deployment fails
	 */
	public synchronized void deploy() throws Exception {
		if (packagesToAdd.size() == 0)
			return;
		
		if (isWorking && ((System.currentTimeMillis() -this.lastActivity) < MAX_ACTIVITY)) {
			logger.warn("Can't deploy on " + this +", the gHN is already working");
			return;
		}
		EndpointReferenceType callbackEPR;
		if (ServiceContext.getContext().getInstance()!= null) {
			callbackEPR = ServiceContext.getContext().getInstance().getAccessPoint().getEndpoint(ServiceContext.getReportingPTName());
			logger.trace("Setting callback EPR as " + callbackEPR.getAddress().toString());

		} else {
			logger.warn("Can't detect callback EPR");
			callbackEPR = new EndpointReferenceType();
		}
		DeployParameters param = new DeployParameters();
		param.set_package(this.packagesToAdd.toArray(new PackageInfo[0]));
		param.setTargetScope(new String[] {scope.toString()});				
		param.setCallbackID(callbackID); 		
		param.setEndpointReference(callbackEPR);
		EndpointReferenceType nodeEPR = this.loadDeployer();
		logger.trace("Deploying on " + nodeEPR.toString());
		DeployerPortType pt = GCUBERemotePortTypeContext.getProxy(new DeployerServiceAddressingLocator().getDeployerPortTypePort(nodeEPR), 
				scope, ServiceContext.getContext());
		pt.deploy(param);
		this.packagesToAdd.clear();
		isWorking = true;//this will prevent further requests to the ghn until the deployment session is received back
		this.lastActivity = System.currentTimeMillis();
	}
	
	/**
	 * Undeploys the packages from the node
	 * 
	 * @throws Exception if the operation fails
	 */
	public synchronized void undeploy() throws Exception {		
		if (this.packagesToRemove.size() == 0)
			return;
		
		if (isWorking && (this.lastActivity - System.currentTimeMillis() < MAX_ACTIVITY)) {
			logger.warn("Can't undeploy from " + this +", the gHN is already working");
			return;
		}
	
		EndpointReferenceType callbackEPR = ServiceContext.getContext().getInstance().getAccessPoint().getEndpoint(ServiceContext.getReportingPTName());
		UndeployParameters params = new UndeployParameters();
		params.set_package(this.packagesToRemove.toArray(new PackageInfo[0]));
		params.setTargetScope(new String[] {scope.toString()});				
		params.setCallbackID(callbackID); 		
		params.setEndpointReference(callbackEPR);
		EndpointReferenceType nodeEPR = this.loadDeployer();
		logger.trace("Undeploying from " + nodeEPR.toString() + " in scope " + scope);
		DeployerPortType pt = GCUBERemotePortTypeContext.getProxy(new DeployerServiceAddressingLocator().getDeployerPortTypePort(nodeEPR), 
				scope, ServiceContext.getContext());
		pt.undeploy(params);
		this.packagesToRemove.clear();
		isWorking = true;//this will prevent further requests to the ghn until the deployment session is received back
		this.lastActivity = System.currentTimeMillis();
	}	
	
	/**
	 * Upgrades packages on the node
	 * @throws Exception
	 */
	public synchronized void upgrade() throws Exception {
		//TODO: to implement when deployer will support package upgrades
	}

	
	/** NoGHNFoundException exception  */
	public static class NoGHNFoundException  extends Exception {
		private static final long serialVersionUID = 1L;
		public NoGHNFoundException(String message) {super(message);}
	}
	/**
	 * Marks the node as not working node 
	 * (usually called when a closed {@link DeployerReport} is received from the node)
	 */
	public void isNotWorking() {
		this.isWorking = false;
		
	}

	/**
	 * Sets the packages to remove in the next {@link #undeploy()} invocation
	 * @param packages the packages to remove from the node
	 */
	public synchronized void setPackagesToRemove(Set<PackageInfo> packages) {
		this.packagesToRemove.addAll(packages);
	}

	/**
	 * Sets the packages to upgrade in the next {@link #upgrade()} invocation
	 * @param packages the packages to remove from the node
	 */
	public synchronized void setPackagesToUpgrade(Set<PackageInfo> packages) {
		this.packagesToUpgrade.addAll(packages);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VirtualNode [ghnID=" + ghnID +", name="+ name + ", scope="+ scope + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ghnID == null) ? 0 : ghnID.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VirtualNode other = (VirtualNode) obj;
		if (ghnID == null) {
			if (other.ghnID != null)
				return false;
		} else if (!ghnID.equals(other.ghnID))
			return false;
		return true;
	}

	/**
	 * Assigns a working scope to the node
	 * @param scope the scope to set
	 */
	public void setWorkingScope(GCUBEScope scope) {
		this.scope = scope;
	}

	/**
	 * Gets the packages scheduled for the next deployment on this node
	 * @return the set of packages
	 */
	public Set<PackageInfo> getScheduledPackages() {		
		return this.packagesToAdd;
	}

	/**
	 * Gets the current scope used on this node
	 * @return the current scope
	 */
	public GCUBEScope getWorkingScope() {
		return this.scope;
	}
	
	
	/**
	 * Looks for the Deployer's endpoint to contact
	 * @return the endpoint reference of Deployer's portType to contact
	 * @throws Exception if the search fails
	 */
	private EndpointReferenceType loadDeployer() throws Exception {
		this.ghnID = this.detectID(); //in case of node redeployment, we look for the new ID
		logger.trace("ghnId found: "+this.ghnID);
		logger.trace("search RI with: UniqueID");
		ISClient client =  GHNContext.getImplementation(ISClient.class);			
		GCUBERIQuery riquery = client.getQuery(GCUBERIQuery.class);
		 riquery.addAtomicConditions(new AtomicCondition("/Profile/GHN/@UniqueID", this.ghnID), 
					new AtomicCondition("/Profile/ServiceClass", "VREManagement"),
					new AtomicCondition("/Profile/ServiceName", "Deployer"));
		 List<GCUBERunningInstance> results = client.execute(riquery,ServiceContext.getContext().getScope());
		 if (results == null || results.size() ==0) {
			 logger.error("can't find a deployer instance on the target node");
			 throw new Exception("Can't find a deployer instance on the target node");
		 }
		 return results.get(0).getAccessPoint().getEndpoint("gcube/common/vremanagement/Deployer");	
	}

	public String getName() {
		return this.name;
	}
}
