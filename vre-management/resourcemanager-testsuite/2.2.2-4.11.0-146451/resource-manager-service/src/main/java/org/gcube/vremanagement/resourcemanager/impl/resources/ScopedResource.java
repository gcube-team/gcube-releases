package org.gcube.vremanagement.resourcemanager.impl.resources;


import static org.gcube.common.vremanagement.whnmanager.client.plugins.AbstractPlugin.whnmanager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGHNQuery;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.vremanagement.whnmanager.client.proxies.WHNManagerProxy;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.operators.Operator.ACTION;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * An abstract model for a scoped {@link GCUBEResource}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class ScopedResource  {			

	/** Object getLogger() */
	@XStreamOmitField
	protected GCUBELog logger;
	
	/** the resource identifier*/	
	protected String id;
	
	/** the resource type */
	protected String type;
	
	/** where the resource is hosted on, it makes sense for RI, GHN*/
	protected String hostedOn = "";
	
	/** result of the last operation performed on the resource */
	protected boolean success;
	
	/** the error message, if any, from the last operation */	
	protected String errorMessage = "";
	
	protected String scope;
	
	/** the last action performed on the resource*/
	protected ACTION action;		
	
	/** Last modification time stamp*/
	protected Date lastModificationTime;
	
	/** Last modification time stamp*/
	protected Date jointTime;
	
	/** the current status of the resource */
	protected STATUS status;
	
	/** resource's legal statuses */
	public static enum STATUS {
		CREATED() {public List<STATUS> previous() {return Collections.emptyList();}},
		ADDREQUESTED() {public List<STATUS> previous() {return Collections.unmodifiableList(Arrays.asList(CREATED));}}, 
		ADDED() {public List<STATUS> previous() {return Collections.unmodifiableList(Arrays.asList(ADDREQUESTED));}}, 
		PUBLISHED() {public List<STATUS> previous() {return Collections.unmodifiableList(Arrays.asList(ADDED));}},
		REMOVEREQUESTED() {public List<STATUS> previous() {return Collections.unmodifiableList(Arrays.asList(ADDED, PUBLISHED));}},
		REMOVED() {public List<STATUS> previous() {return Collections.unmodifiableList(Arrays.asList(REMOVEREQUESTED));}}, 
		UNPUBLISHED () {public List<STATUS> previous() {return Collections.unmodifiableList(Arrays.asList(REMOVED));}},
		LOST () {public List<STATUS> previous() {return Collections.unmodifiableList(Arrays.asList(REMOVED,REMOVEREQUESTED,ADDED,PUBLISHED,CREATED,UNPUBLISHED));}};
		
		abstract public List<STATUS> previous();
	}

	public ScopedResource(String id, String type, GCUBEScope scope) {
		this.id = id;
		this.type = type;
		this.scope = scope.toString();
		this.status = STATUS.CREATED;
	}
	
	/**
	 * Custom 
	 * 
	 * @throws ResourceNotFound if it is impossible to locate the resource
	 */
	protected abstract void find() throws Exception;;
	
	/**
	 * Looks for the resource in the infrastructure
	 * 
	 * @throws ResourceNotFound if it is impossible to locate the resource
	 */
	public synchronized void findResource() throws ResourceNotFound {
		this.setErrorMessage(""); //empty any previous message
		int max_attempts = this.getMaxFindAttempts(); //try to find the resource 5 times
		int i = 0;
		while (true) {
			try {							
				this.find();
				this.success = true;
				break;
			} catch (Exception e) {
				logger.warn("Can't find the resource "+ this.getId() + " on the IS in scope: "+scope);
				if (i++ <= max_attempts) {
					logger.warn("try again in 5 secs");
					try {Thread.sleep(5000);} catch (InterruptedException e1) {}					
					continue;
				}
				else {
					this.noHopeForMe("Can't find resource "+ this.getId() + " on the IS", new ResourceNotFound(e));
					break;
				}
			}
		}
	}
	
	public synchronized void doAction(ACTION action) throws ResourceNotFound, Exception {
		this.action = action;	
		switch (action) {
			case ADD: this.addToScope(); this.setJointTime(Calendar.getInstance().getTime()); this.success= true; break;
			case REMOVE: this.removeFromScope(); this.success= true; break;
			default: break;
		}
		this.setChanged();
	}
	
	protected boolean checkGhnType( String id, GCUBEScope scope) throws Exception{
		logger.trace("checkGhnType method: check if the ghn is managed by WhnManager ghnId: "+id+ " on scope: "+scope.toString());
		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBEGHNQuery query=null;
		try {
			query = client.getQuery(GCUBEGHNQuery.class);
			query.addAtomicConditions(new AtomicCondition("/ID/text()", id));
// condition for discover if is managed by WHNMAnager
			query.addGenericCondition("$result/Profile/GHNDescription/RunTimeEnv//Variable[Key/string() eq 'SmartGears']");
		} catch (Exception e) {					
			throw new Exception("checkGhnType method: unable to query of the target GHN (ID=" + id + ")", e);			
		}
		List<GCUBEHostingNode> hostingNodes=null;
		try {
			logger.debug("checkGhnType method: execute query");
			hostingNodes =  client.execute(query, scope);
		} catch (Exception e) {
			logger.debug(" WHNManager query failed. Query exception: "+e.getMessage());
		}
		if(hostingNodes.isEmpty()){
			logger.debug("GHNProfile is not managed by WHNManager");
			return false;		
		}
		String nodename = hostingNodes.get(0).getNodeDescription().getName();
		this.hostedOn = nodename;
		logger.info("The GHN hosted on:"+nodename+" is managed by WhnManager. Next step: connect to WHNManager");
		return true;
	}
	
	protected WHNManagerProxy loadWHNManager(String nodename) throws MalformedURLException {
		String scopeString=ServiceContext.getContext().getScope().getEnclosingScope().toString();
		logger.debug("contacting the WHNManager on"+nodename+"  with scope "+scopeString+" for adding/remove the scope: "+this.scope+" from/to the resource with id: "+this.id);
		ScopeProvider.instance.set(scopeString);
		WHNManagerProxy proxy = whnmanager().at(new URL("http://"+ nodename +"/whn-manager/gcube/vremanagement/ws/whnmanager")).build();
		return proxy;
	}

	
	/**
	 * Adds the resource to the scope
	 * @throws Exception if the operation fails
	 * @throws ResourceNotFound if the resource does not exist in the infrastructure
	 */
	protected abstract void addToScope() throws ResourceNotFound, Exception;
	
	
	/**
	 * Removes the resource from the scope
	 * @throws Exception if the operation fails
	 * @throws ResourceNotFound if the resource does not exist in the infrastructure
	 */
	protected abstract void removeFromScope() throws ResourceNotFound, Exception;
		
	
	/**
	 * @return the resource ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the resource type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the scope
	 */
	public final GCUBEScope getScope() {
		return GCUBEScope.getScope(this.scope);
	}

	public final void setScope(GCUBEScope scope) {
		this.scope = scope.toString();
	}
	/**
	 * @return the action
	 */
	public ACTION getAction() {
		return action;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.success = false;
		this.errorMessage = errorMessage;		
	}

	/**
	 * Updates the time the resource joined the scope 
	 * @param time the new joint time
	 */
	public void setJointTime(Date time) {
		this.jointTime = time;
		
	}
	
	private void setChanged() {
		this.setLastModificationTime(Calendar.getInstance().getTime());
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @return the node on which the Scope is hosted
	 */
	public String getHostedOn() {
		return hostedOn;
	}

	public void setHostedON(String hostedOn) {
		this.hostedOn = hostedOn;
	}

	public Date getJointTime() {
		if (jointTime == null)
			jointTime = Calendar.getInstance().getTime();	
		return jointTime;
	}
	
	public Date getLastModificationTime() {
		if (lastModificationTime == null)
			this.setChanged();
		return lastModificationTime;
	}

	private void setLastModificationTime(Date lastModificationTime) {
		this.lastModificationTime = lastModificationTime;
	}

	/**
	 * @return the status
	 */
	public STATUS getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public synchronized void setStatus(STATUS status) {
		getLogger().trace(this.toString()+  ": status set to " + status);
		this.status = status;
	}
	
	/**
	 * Number of times the resource is searched in the IS before to declare it lost
	 * @return
	 */
	protected int getMaxFindAttempts(){
		return 5;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScopedResource other = (ScopedResource) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
	/**
	 * Gives up the operation on the resource
	 * @param message the error message to return
	 * @param e the exception that generates the hopeless
	 * @throws E the source exception
	 */
	protected <E extends Exception> void noHopeForMe(String message, E e) throws E {
		getLogger().error(this.toString() +": Unable to manage the resource " + message ,e);
		this.setStatus(STATUS.LOST);
		this.success = false;
		this.setErrorMessage(message);		
		throw e;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return new StringBuilder().append("Resource [id=")
				.append(id)
				.append(", type=")
				.append(type)
				.append(", timestamp=")
				.append(lastModificationTime)
				.append(", scope=")
				.append(scope)
				.append(", status=")
				.append(status)
				.append(", hostedOn=")
				.append(hostedOn)
				.append("]")
				.toString();
	}
   
	
	protected GCUBELog getLogger() {
		if (this.logger == null)
			logger=new GCUBELog(this);
		return logger;
	}
	
	/** Unable to find the resource in the infrastructure*/
	public class ResourceNotFound extends Exception {
		public ResourceNotFound(String message) {super(message);}
	
		public ResourceNotFound(Exception e) {
			super(e);
		}

		private static final long serialVersionUID = -6111206113583291172L;
	}

}
