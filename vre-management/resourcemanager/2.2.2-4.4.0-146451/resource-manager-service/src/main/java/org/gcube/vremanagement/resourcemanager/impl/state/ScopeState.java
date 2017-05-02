package org.gcube.vremanagement.resourcemanager.impl.state;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Observable;
import java.util.Set;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.ServiceNotFoundException;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource.STATUS;
import org.gcube.vremanagement.resourcemanager.impl.resources.software.GCUBEPackage;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode.NoGHNFoundException;
import org.gcube.vremanagement.resourcemanager.impl.state.observers.Serializer;

/**
 * 
 * A list of scoped resources
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public final class ScopeState extends Observable { 	
	
	protected GCUBELog logger = new GCUBELog(this);
			
	/** last operation performed on the list*/	
	protected OPERATION lastOperationPerformed; 
	
	public enum OPERATION {CREATED, LOADED, TOBEMANAGED, PUBLISHED, SERIALIZED, EXECUTED};
	
	private RawScopeState rawState;

	private Session report;
	
	private boolean isDisposed = false;
	
	//list of nodes associated to this Scope
	private VirtualNodeList nodes;
	
	protected ScopeState() {}
	
	protected void initialize(GCUBEScope scope, String name, boolean securityEnabled, String ... description) {
	// serialized on fs	
		rawState = new RawScopeState();
		rawState.initialize(scope);
		this.rawState.data.put("NAME", name);
		this.lastOperationPerformed = OPERATION.CREATED;
		if (!this.rawState.data.containsKey("STARTTIME"))
			this.rawState.data.put("STARTTIME", Calendar.getInstance().getTime());
		this.rawState.data.put("ENDTIME", null);
		if ((! this.rawState.data.containsKey("DESCRIPTION")) && (description.length > 0))		
			this.rawState.data.put("DESCRIPTION", description[0]);
		this.rawState.data.put("SECURITYENABLED",securityEnabled);
		this.nodes = new VirtualNodeList(scope);	
		this.nodes.loadFromState(this.rawState);
	}
	
	/**
	 * Sets the scope manager identity
	 * @param manager the manager
	 */
	public synchronized void setManager(String manager) {
		this.rawState.data.put("MANAGER", manager);
		this.notifyObservers();
	}
	
	/**
	 * Sets the scope designer identity
	 * @param manager the manager
	 */
	public synchronized void setDesigner(String designer) {
		this.rawState.data.put("DESIGNER", designer);
		this.notifyObservers();
	}
	
	/**
	 * Changes the scope description
	 * 
	 * @param description the description
	 */
	public synchronized void changeDescription (String description) {
		this.rawState.data.put("DESCRIPTION", description);
		this.notifyObservers();
	}
	
	/**
	 * Adds a new resources to the list
	 * @param newresources the resources to add
	 */
	public synchronized void addResources(Set<ScopedResource> newresources) {
		for (ScopedResource resource : newresources) {
			logger.trace("Adding resource "  + resource.getId() + " to scope state " + this.getScope().toString());
			resource.setStatus(STATUS.ADDREQUESTED);
			if (rawState.resources.primaryKeySet().contains(resource.getId()))
				rawState.resources.removeValuesByPrimaryKey((resource.getId()));
			rawState.resources.put(resource.getId(), resource.getType(), resource);
		}
		this.setLastOperationPerformed(OPERATION.TOBEMANAGED);
		this.notifyObservers();
	}
	
	/**
	 * Gets resources of the given type
	 * 
	 * @param type the type to filter 
	 * @return the collection of resources
	 */
	public synchronized Set<ScopedResource> getResourcesByType(String type) {
		return rawState.resources.getValuesBySecondaryKey(type);		
	}
	
	/**
	 * Gets the resource with the given id
	 * 
	 * @param id the resource identified
	 * @return the resource
	 */
	public synchronized ScopedResource getResource(String id) {
		return (ScopedResource) rawState.resources.getValuesByPrimaryKey(id).iterator().next();		
	}
	
	public boolean containsResource(String id) {
		return rawState.resources.primaryKeySet().contains(id);
	}
	
	/**
	 * Removes all the resource of the given type
	 * 
	 * @param type the type of resources to remove
	 */
	public synchronized void removeAllResourcesByType(String type) {
		for (ScopedResource resource : rawState.resources.getValuesBySecondaryKey(type)) {
			resource.setStatus(STATUS.REMOVEREQUESTED);
			rawState.resources.put(resource.getId(), resource.getType(), resource);//TODO: is needed?
		}
		this.setLastOperationPerformed(OPERATION.TOBEMANAGED);
		this.notifyObservers();
		rawState.resources.removeValuesBySecondaryKey(type);		
	}
	
	/**
	 * Removes the resources from the scope
	 * 
	 * @param oldresources the resources to remove
	 * @throws NoGHNFoundException 
	 * 
	 */
	public synchronized void removeResources(Set<ScopedResource> oldresources) throws NoGHNFoundException {
		logger.trace("ScopeState: removeResource method: resources must be in the state in order to be removed ");
		//resources must be in the state in order to be removed :-)
		//this is to prevent service's unconscious cleanup..
		for (ScopedResource resource : oldresources) {
			logger.debug("set REMOVEREQUESTED status to "+resource.getId()+ " type of resource: "+resource.getType());
			resource.setStatus(STATUS.REMOVEREQUESTED);
/*FORCE RUNNING INSTANCE RESOURCE SETTING: delete the old, put the new*/
			if(rawState.resources.getValuesByPrimaryKey(resource.getId()) != null){
				logger.debug("the resource is already present on rawState with the same id . Deleting it...");
				Set<ScopedResource> resourceset=rawState.resources.getValuesByPrimaryKey(resource.getId());
				for(ScopedResource r : resourceset){
					rawState.resources.removeValue(r);
					logger.debug("r "+r+" deleted ");
				}
				logger.debug("put new value "+resource.getId()+" "+resource.getType().trim());
			}
/*END FORCE*/			
			rawState.resources.put(resource.getId(), resource.getType().trim(), resource);
			
			if (resource.getType().compareToIgnoreCase(ScopedDeployedSoftware.TYPE) == 0) {
				//retrieve the node from wich the software must be undeployed
				VirtualNode node = nodes.getNode(((ScopedDeployedSoftware) resource).getTargetNodeName());
				((ScopedDeployedSoftware) resource).scheduleUndeploy(node);
				((ScopedDeployedSoftware) resource).setCallbackID(this.getLastReport().getId());
			}
		}
		this.setLastOperationPerformed(OPERATION.TOBEMANAGED);
		logger.debug("ScopeState notify observers ");
		this.notifyObservers(); //let the obs do their work before to remove the resources
		logger.debug("ScopeState removing the resources...");
		for (ScopedResource resource : oldresources) {
			logger.debug("Removing resource " + resource+ " from the scope state"); 
			if (resource.getStatus() == STATUS.UNPUBLISHED) {
				rawState.resources.removeValuesByPrimaryKey(resource.getId());
				logger.debug("...removed");
			} else {
				logger.warn("Resource " + resource + " is still PUBLISHED in the scope state, can't be removed. The resource status is: "+resource.getStatus());
			}
		}
		
		this.notifyObservers(); //notify about the physical removal(s)
	}
	
	/**
	 * Removes the resource from the state, no matter about its actual status
	 * @param resources
	 */
	public synchronized void forceResourceRemoval(Set<ScopedResource> resources) {		
		for (ScopedResource resource : resources) {
			logger.debug("Removing resource " + resources+ " from the scope state");
			rawState.resources.removeValuesByPrimaryKey(resource.getId());		
		}
			
	}
	
	/**
	 * Empty the list of resources
	 */
	protected synchronized void removeAllResources() {
		for (ScopedResource resource : rawState.resources.values())
			resource.setStatus(STATUS.REMOVEREQUESTED);
		this.setLastOperationPerformed(OPERATION.TOBEMANAGED);
		this.notifyObservers();
		rawState.resources.clean();
	}

	/**
	 * Gets the resource's scope
	 * 
	 * @return the scope
	 */
	public GCUBEScope getScope() {
		return rawState.getScope();
	}
	
	public synchronized void notifyObservers(Object whatschanged) {
	    // Otherwise it won't propagate changes
	    setChanged();
	    super.notifyObservers(whatschanged);
	 }

	public synchronized void notifyObservers() {
	    // Otherwise it won't propagate changes
	    setChanged();
	    super.notifyObservers();
	 }
	
	/**
	 * Gets all the {@link ScopedResource}s
	 * 
	 * @return all the {@link ScopedResource}s
	 */
	public synchronized Collection<ScopedResource> getAllResources() {
		return Collections.unmodifiableCollection(rawState.resources.values());
	}

	/**
	 * Gets the scope manager
	 * @return the scope manger
	 */
	public String getManager() {		
		return (String) this.rawState.data.get("MANAGER");
	}

	/**
	 * Gets the scope designer
	 * @return the scope designer
	 */
	public String getDesigner() {		
		return (String) this.rawState.data.get("DESIGNER");
	}

	public String getDescription() {		
		return (String) this.rawState.data.get("DESCRIPTION");
	}

	public String getName() {		
		
		return (String) this.rawState.data.get("NAME");
	}

	
	public Date getEndTime() {		
		return (Date) this.rawState.data.get("ENDTIME");
	}

	public Date getStartTime() {		
		return (Date) this.rawState.data.get("STARTTIME");
	}

	public boolean isSecurityEnabled() {		
		return (Boolean) this.rawState.data.get("SECURITYENABLED");
	}

	public void setEndTime(Date endTime) {
		this.rawState.data.put("ENDTIME", endTime);
		this.notifyObservers();
	}

	public void setStartTime(Date startTime) {
		logger.trace("setStartTime: Start time " + ProfileDate.toXMLDateAndTime(startTime));
		this.rawState.data.put("STARTTIME", startTime);
		this.notifyObservers();
	}

	public void setName(String name) {	
		this.rawState.data.put("NAME", name);
		this.notifyObservers();
	}

	/**
	 * @return the the last operation performed on the list
	 */
	public OPERATION getLastOperationPerformed() {
		return lastOperationPerformed;
	}

	/**
	 * @param operation the last operation performed on the list
	 */
	public synchronized void setLastOperationPerformed(OPERATION operation) {
		this.lastOperationPerformed = operation;
	}	
	
	
	/**
	 * Gets the {@link RawScopeState}
	 * @return the raw state
	 */
	public RawScopeState getRawScopeState() {
		return rawState;
	}

	/**
	 * Sets the new {@link RawScopeState} 
	 * it usually invoked at deserialization time, see {@link Serializer#load(ScopeState, GCUBEScope)}
	 * @param state
	 */
	public void setRawScopeState(RawScopeState state) {
		this.rawState = state;
		this.nodes = new VirtualNodeList(state.getScope());	
		this.nodes.loadFromState(this.rawState);
		this.lastOperationPerformed = OPERATION.CREATED;
		
	}

	public void setSecurity(boolean securityEnabled) {
		this.rawState.data.put("SECURITYENABLED",securityEnabled);
		
	}

	/**
	 * Gets the last active {@link Session}
	 * @return the session
	 */
	public Session getLastReport() {	
		return this.report;
	}
	
	/**
	 * Sets the last active {@link Session}
	 * @param session the session
	 */
	public void setLastSession(Session report) {
		this.report  = report;
		
	}

	/**
	 * @return the isDisposed
	 */
	public boolean isDisposed() {
		return isDisposed;
	}

	/**
	 * @param isDisposed the isDisposed to set
	 */
	public void markAsDisposed() {
		this.isDisposed = true;
		this.notifyObservers();
	}

	/**
	 * Gets the node by its name
	 * @param name the name
	 * @return the node
	 * @throws NoGHNFoundException if the node does not exist
	 */
	public VirtualNode getNode(String name) throws NoGHNFoundException {
		return nodes.getNode(name);
	}
	
	/**
	 * Gets the node by its identifier
	 * @param id the identifier
	 * @return the node
	 * @throws NoGHNFoundException if the node does not exist
	 */
	public VirtualNode getNodeById(String id) throws NoGHNFoundException {
		return nodes.getNodeById(id);
	}

	/**
	 * Gets the deployed service of the given service, if any
	 * @param scopeState the state where to search the deployed service
	 * @param sourceService the service
	 * @return the deployed service
	 */

	public ScopedDeployedSoftware getRelatedDeployedSoftware(GCUBEPackage sourceService) throws ServiceNotFoundException {
		for (ScopedResource resource : this.getResourcesByType(ScopedDeployedSoftware.TYPE)) {
			if (resource == null) continue;
			ScopedDeployedSoftware service = (ScopedDeployedSoftware)resource;
			if (service.getSourcePackage().equals(sourceService)) {
				logger.debug("Source service found" );
				return service;
			}
		}
		logger.error("Unable to find a source service ");
		throw new ServiceNotFoundException();
	}
}
