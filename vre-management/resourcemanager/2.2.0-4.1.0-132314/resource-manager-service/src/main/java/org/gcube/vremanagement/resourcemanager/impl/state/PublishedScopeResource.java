package org.gcube.vremanagement.resourcemanager.impl.state;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResourceFactory;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource.STATUS;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.Type;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * The scope resource published in the IS
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class PublishedScopeResource  {

	protected GCUBELog logger = new GCUBELog(this);
	
	private GCUBEGenericResource resource;
	
	private PublishedResourceList publishedResourceList;

	private GCUBEScope scope;
	
	private static final String NS ="";

	private String manager= "", designer = "", service = "";

	private Date startTime = null, endTime = null;

	private boolean securityEnabled = false;

	private boolean loaded = false;

	private static Map<GCUBEScope,PublishedScopeResource> cache = new HashMap<GCUBEScope, PublishedScopeResource>();;
	
	public void reload() throws Exception {
		try {
			publishedResourceList = new PublishedResourceList();
			this.load();
		} catch (Exception e) {
			logger.error("Failed to reload the Scope Resource");
			throw e;
		}
	}
	
	// make it private to avoid explicit creation of VREscope objects
	private PublishedScopeResource(GCUBEScope scope) throws Exception{
		this.scope = scope;
		publishedResourceList = new PublishedResourceList();
		//get the service EPR from the RI profile
		this.service = ServiceContext.getContext().getInstance().getAccessPoint().getRunningInstanceInterfaces().getEndpoint().get(0).getValue();
		// get the resource implementation
		try {
			this.resource = GHNContext.getImplementation(GCUBEGenericResource.class);
		} catch (Exception e) {			
			throw new Exception("Unable to create resource to publish for " + scope.toString());
		}
	}

	/**
	 * Gets the current Scope Resource
	 * @param scope the scope of the resource to load
	 * @return the PublishedScopeResource
	 */
	public static PublishedScopeResource getResource(GCUBEScope scope) throws Exception {		
		if (! cache.containsKey(scope))
			cache.put(scope,  new PublishedScopeResource(scope));
		return cache.get(scope);				
	}

	/**
	 * Adds a resource to PublishedScopeResource
	 * 
	 * @param resource
	 * @throws Exception
	 */
	public void addResource(ScopedResource resource) throws Exception {		
		this.publishedResourceList.add(resource.getId(), resource.getType(),resource.getJointTime(), resource.getHostedOn());
		resource.setStatus(STATUS.PUBLISHED);
	}

	/**
	 * Removes a resource from the PublishedScopeResource
	 * 
	 * @param resource
	 * @throws Exception
	 */
	public void removeResource(ScopedResource resource) throws Exception {
		this.publishedResourceList.remove(resource.getId(), resource.getType());
		resource.setStatus(STATUS.UNPUBLISHED);
	}

	/**
	 * Returns a string representation of the PublishedScopeResource
	 * @throws IOException 
	 */
	public String toString() {
		try {
			this.resource.setBody(this.prepareBody());
			StringWriter ret = new StringWriter();
			this.resource.store(ret);		
			return ret.toString();		
		} catch (Exception e) { logger.error("Invalid Scope Resource serialization",e); return "";}		
		
	}

	/**
	 * @return the scope
	 */
	public GCUBEScope getScope() {
		return scope;
	}

	/**
	 * Returns the type of the scope.
	 * 
	 * @return the type.
	 */
	public Type getType() {
		return scope.getType();
	}

	/**
	 * Sets a scope option
	 * 
	 * @param name
	 *            the option name
	 * @param value
	 *            the option value
	 * @throws Exception 
	 * @throws ParseException
	 */
	public void setOption(String name, String value)
			throws Exception {
		if (value == null)
			return;
		logger.debug("setting option " + name);
		try {
			if (name.compareToIgnoreCase("DESIGNER") == 0) {
				// the manager DN
				this.designer = value;
			} else if ((name.compareToIgnoreCase("MANAGER") == 0) 
					|| ((name.compareToIgnoreCase("CREATOR") == 0))){
				// the creator's DN
				this.manager = value;
			} else if (name.compareToIgnoreCase("ENDTIME") == 0) {
				// the time at which the scope will be dismissed
				this.endTime = ProfileDate.fromXMLDateAndTime(value);
			} else if (name.compareToIgnoreCase("STARTTIME") == 0) {
				// the time this scope was created
				this.startTime = ProfileDate.fromXMLDateAndTime(value);
			} else if (name.compareToIgnoreCase("DESCRIPTION") == 0) {
				// the scope description
				this.resource.setDescription(value);
			} else if (name.compareToIgnoreCase("DISPLAYNAME") == 0) {
				// the name to display (the unique name is the qualified scope)
				this.resource.setName(value);
			} else if (name.compareToIgnoreCase("SECURITYENABLED") == 0) {
				// the name to display (the unique name is the qualified scope)
				this.securityEnabled = Boolean.valueOf(value);
			} /*else
				throw new UnknownScopeOptionException();*/
		} catch (ParseException e) {
			throw new Exception ("Unable to parse option " + name);
		}

	}

	/**
	 * Publishes the {@link PublishedScopeResource} into the IS
	 */
	public synchronized void publish() throws Exception {		
		logger.trace("publish method invokation ");
		this.resource.setBody(this.prepareBody());			
		logger.trace("Publishing Scope Resource: \n" + this.toString());
		
		//this.store();
		ISPublisher publisher = GHNContext.getImplementation(ISPublisher.class);
		if (this.loaded) {		
			logger.trace("update resource by ISPublisher: "+this.resource+" scope "+this.scope+" context  "+ServiceContext.getContext());
			publisher.updateGCUBEResource(this.resource, this.scope, ServiceContext.getContext());		
		}
		else {
			logger.trace("register resource by ISPublisher: res "+this.resource+" scope "+this.scope+" context  "+ServiceContext.getContext());
			publisher.registerGCUBEResource(this.resource, this.scope, ServiceContext.getContext());
			this.loaded = true;
		}
	}

	/**
	 * Dismisses the {@link PublishedScopeResource} from the IS and the scope
	 */
	public synchronized void dismiss() throws Exception {		
		this.resource.setBody(this.prepareBody());			
		//logger.trace("Unpublishing Scope Resource: \n" + this.toString());
		//this.store();
		ISPublisher publisher = GHNContext.getImplementation(ISPublisher.class);
		publisher.removeGCUBEResource(this.resource.getID(), this.resource.getType(), this.scope, ServiceContext.getContext());		
		cache.remove(this.getScope());
	}

	private String prepareBody() throws IOException {
		StringWriter body = new StringWriter(); // serialises to a temporary writer first		
		KXmlSerializer serializer = new KXmlSerializer();
		serializer.setOutput(body);
		try {
			serializer.startDocument("UTF-8", true);
			serializer.startTag(NS,"Scope").text(scope.toString()).endTag(NS,"Scope");
			serializer.startTag(NS,"Service").text(this.service).endTag(NS,"Service");
			serializer.startTag(NS,"Manager").text(this.getManager()).endTag(NS,"Manager");
			serializer.startTag(NS,"Designer").text(this.getDesigner()).endTag(NS,"Designer");
			if (this.startTime == null)
				this.startTime = Calendar.getInstance().getTime();
			serializer.startTag(NS,"StartTime").text(ProfileDate.toXMLDateAndTime(this.startTime)).endTag(NS,"StartTime");
			if (this.endTime != null)
				serializer.startTag(NS,"EndTime").text(ProfileDate.toXMLDateAndTime(this.endTime)).endTag(NS,"EndTime");
			serializer.startTag(NS,"SecurityEnabled").text(String.valueOf(this.securityEnabled)).endTag(NS,"SecurityEnabled");			
			publishedResourceList.store(serializer);
		} catch (Exception e) {
			logger.error("The Scope Resource does not have a valid serialisation", e);
			throw new IOException("The Scope Resource does not have a valid serialisation");
		}
		finally {
			body.close();
		}		
		return body.toString();
		
	}

	/**
	 * @return the creator
	 */
	public String getManager() {
		return (manager != null) ? manager : "";
	}

	/**
	 * @return the designer
	 */
	public String getDesigner() {
		return (designer != null)?  designer : "";
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return (this.resource.getDescription() != null)? this.resource.getDescription() :  "";
	}

	/**
	 * @return the display name
	 */
	public String getDisplayName() {
		return (this.resource.getName() != null) ? this.resource.getName() : "";
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @return the securityEnabled
	 */
	public boolean isSecurityEnabled() {
		return securityEnabled;
	}


	/**
	 * Loads from the <em>Body</em> element the resource information
	 * @param body the <em>Body</em> of the generic resource
	 * @throws Exception if the element is not valid or well formed
	 */
	private void parseBody(String body) throws Exception {
		KXmlParser parser = new KXmlParser();
		parser.setInput(new BufferedReader(new StringReader(body)));		
		loop: while (true) {
			try {
				switch (parser.next()) {
				case KXmlParser.START_TAG:
					if (parser.getName().equals("Creator")) this.manager = parser.nextText();
					else if (parser.getName().equals("Designer")) this.designer = parser.nextText();
					else if (parser.getName().equals("EndTime")) this.endTime = ProfileDate.fromXMLDateAndTime(parser.nextText());
					else if (parser.getName().equals("StartTime")) this.startTime = ProfileDate.fromXMLDateAndTime(parser.nextText());
					//else if (parser.getName().equals("DisplayName")) this.display = parser.nextText();
					else if (parser.getName().equals("SecurityEnabled")) this.securityEnabled = Boolean.valueOf(parser.nextText());
					else if (parser.getName().equals(PublishedResourceList.RESOURCES_ELEMENT)) publishedResourceList.load(parser);
					else parser.nextText();//just skip the text
					break;
				case KXmlParser.END_DOCUMENT: break loop;
				}				
			} catch (Exception e) {
				throw new Exception ("Unable to parse the PublishedScopeResource body");
			}
		}
	}

	/**
	 * Loads the scope resource from the IS
	 * @param resource the resource to load
	 * 
	 * @return true if the resource is successfully loaded, false otherwise
	 */
	protected boolean load() {
		
		try {
			ISClient client = GHNContext.getImplementation(ISClient.class);
			GCUBEGenericResourceQuery query = client.getQuery(GCUBEGenericResourceQuery.class);
			query.addAtomicConditions(new AtomicCondition("/Profile/SecondaryType", determineSecondaryType()),
					new AtomicCondition("/Profile/Body/Scope", scope.toString()));
			List<GCUBEGenericResource> results = client.execute(query, scope);
			if ((results != null) && (results.size() > 0)) {
				this.resource = results.get(0);				
				this.parseBody(this.resource.getBody());								
				this.loaded = true;
				logger.trace("Resource loaded from the IS: \n" + this.toString());
				return true;
			} else 
				logger.warn("Unable to load the resource for "+ this.scope.toString() + " from the IS");
		} catch (Exception e) {logger.warn("Published resource for "+ this.scope.toString()+ " does not exist on the IS", e);}
		return false;
	}
	

	
	/**
	 * Determines the secondary type of the scope resource
	 * 
	 * @return the secondary type
	 */
	private String determineSecondaryType() {	
		if (this.scope.getType() == Type.VRE) 
			return GCUBEGenericResource.SECONDARYTYPE_VRE;							
		else if (this.scope.getType() == Type.VO)
			return GCUBEGenericResource.SECONDARYTYPE_VO;											
		else 
			return GCUBEGenericResource.SECONDARYTYPE_INFRASTRUCTURE;
	}


	public boolean loaded() {		
		return this.loaded;
	}
	
	/**
	 * Fills the input {@link ScopeState} with the actual content of the {@link PublishedResourceList}
	 * 
	 * @param scopeState the list to fill
	 */
	public void to(ScopeState scopeState) {
		logger.debug("To: Filling the local scope state with the published state");
		scopeState.addResources(publishedResourceList.asScopedResources(scopeState));	
		scopeState.setDesigner(this.getDesigner());
		scopeState.setManager(this.getManager());
		scopeState.setSecurity(this.isSecurityEnabled());
		scopeState.changeDescription(this.getDescription());
		scopeState.setEndTime(this.endTime);
		logger.trace("Setting the scope state to " + ProfileDate.toXMLDateAndTime(this.startTime));
		scopeState.setStartTime(this.startTime);
		logger.trace("Scope state set to:" + ProfileDate.toXMLDateAndTime(scopeState.getStartTime()));
		scopeState.setName(this.getDisplayName());
		
	}
	
	/**
	 * Fills this {@link PublishedResourceList} with the content of the input {@link ScopeState}
	 * 
	 * @param scopeState the list to load
	 * @throws Exception if the load fails
	 */
	public synchronized void loadFromLocalState(ScopeState scopeState) throws Exception {
		logger.debug("LoadFromLocalState: Loading the published state for " + this.scope.getName() + " from the local file system");
		//reloading is needed in order to reuse the resource ID and 		
		//to avoid to cancel the old Generic Resource from the IS (plus reusing any other information do not overridden)
		this.reload();
		publishedResourceList = new PublishedResourceList();//empty the scopeState
		this.synchBasicInfo(scopeState);
		for(ScopedResource resource : scopeState.getAllResources()) 
			this.addResource(resource);			
		
		//initialise some resource's fields
		if (this.scope.getType() == Type.VRE)
			this.resource.setSecondaryType(GCUBEGenericResource.SECONDARYTYPE_VRE);					
		else if (this.scope.getType() == Type.VO) 
			this.resource.setSecondaryType("VO");									
		else 
			this.resource.setSecondaryType("INFRASTRUCTURE");			
					
		this.resource.addScope(this.scope);
		
		 // force the first serialization of Body
		this.toString();
		
	}
	/**
	 * Synchronizes this {@link PublishedResourceList} with the content of the input {@link ScopeState}
	 * 
	 * @param scopeState the list to synchronize with
	 * @throws Exception if the synchronization fails
	 */
	public synchronized void synchWithLocalState(ScopeState scopeState) throws Exception {
		logger.debug("SynchWithLocalState: Synch the published state from the scope state");
		for(ScopedResource resource : scopeState.getAllResources()) {
			logger.debug("resource: "+resource.getId()+ " with status "+resource.getStatus());
			switch (resource.getStatus()) { 
				case ADDED: this.addResource(resource); break;			
				case REMOVED: this.removeResource(resource); break;
				case LOST: this.removeResource(resource); break;
				//in the other statuses, the resource is just ignored
			}			
		}
		
		this.synchBasicInfo(scopeState);
	}
	
	private void synchBasicInfo(ScopeState scopeState) throws Exception {
		logger.debug("Synchronizing basic info to publish");
		this.setOption("CREATOR",scopeState.getManager());
		this.setOption("DESIGNER",scopeState.getDesigner());
		this.setOption("DESCRIPTION",scopeState.getDescription());
		this.setOption("DISPLAYNAME",scopeState.getName());
		//here we directly assign the values, don't want to format/unformat them via setOption()
		this.endTime = scopeState.getEndTime();
		this.startTime = scopeState.getStartTime();		
		this.securityEnabled = scopeState.isSecurityEnabled();
						
	}
	
	/** InvalidVREOption exception */
	public static class UnknownScopeOptionException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * List of resources forming the {@link PublishedScopeResource}
	 *  
	 * @author Manuele Simi (ISTI-CNR)
	 *
	 */
	class PublishedResourceList {		
		
		private Set<Item> resources = Collections.synchronizedSet(new HashSet<Item>());
		
		private static final String RESOURCES_ELEMENT = "ScopedRescources";
		
		private static final String RESOURCE_ELEMENT = "ScopedRescource";
		
		class Item {
			protected String id;
			protected String type;
			protected String hostedOn;
			protected Date timestamp;
			
			protected Item(String id, String value, Date timestamp, String ... hostedOn) {
				this.id = id; 
				this.type = value; 
				this.timestamp = timestamp;
				this.hostedOn = (hostedOn!=null && hostedOn.length>0) ? hostedOn[0] : null;
			}
			
			protected Item(String id, String value, String ... hostedOn) {
				this(id, value, null, hostedOn);				
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((id == null) ? 0 : id.hashCode());
				result = prime * result
						+ ((type == null) ? 0 : type.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				final Item other = (Item) obj;
				if (id == null) {
					if (other.id != null)
						return false;
				} else if (!id.equals(other.id))
					return false;
				if (type == null) {
					if (other.type != null)
						return false;
				} else if (!type.equals(other.type))
					return false;
				return true;
			}
			
			
		}
		
		void add(String id, String type, Date time, String ... hostedOn) {resources.add(new Item(id, type, time, hostedOn));}
		
		void remove(String id, String type) {resources.remove(new Item(id, type)); }	
		
		Set<ScopedResource> asScopedResources(ScopeState state) {
			Set<ScopedResource> temp = new HashSet<ScopedResource>();
			for (Item item : resources) {
				ScopedResource resource;
				try {
					resource = ScopedResourceFactory.newResource(state.getScope(), item.id, item.type);
					resource.setJointTime(item.timestamp);
					if ((item.hostedOn != null) && (item.hostedOn.compareTo("") != 0))
						resource.setHostedON(item.hostedOn);							
					temp.add(resource);
				} catch (Exception e) {
					logger.error("Can't build the resource",e);
				}
				
			}
			return temp;
			
		}
		
		int getSize() {return resources.size();}
		/**
		 * Stores the resources in the given serializer
		 * @param serializer the serializer
		 * @throws IOException if the storage fails
		 */
		void store(KXmlSerializer serializer) throws IOException {
			serializer.startTag(PublishedScopeResource.NS, RESOURCES_ELEMENT);
			for (Item item : resources) {
				serializer.startTag(PublishedScopeResource.NS, RESOURCE_ELEMENT);
				serializer.startTag(PublishedScopeResource.NS, "ResourceID").text(item.id).endTag(PublishedScopeResource.NS, "ResourceID");
				serializer.startTag(PublishedScopeResource.NS, "ResourceType").text(item.type).endTag(PublishedScopeResource.NS, "ResourceType");
				if ((item.hostedOn != null) && (item.hostedOn.compareTo("") != 0))
					serializer.startTag(PublishedScopeResource.NS, "HostedOn").text(item.hostedOn).endTag(PublishedScopeResource.NS, "HostedOn");
				if (item.timestamp != null)
					serializer.startTag(PublishedScopeResource.NS, "JointTime").text(ProfileDate.toXMLDateAndTime(item.timestamp)).endTag(PublishedScopeResource.NS, "JointTime");				
				serializer.endTag(PublishedScopeResource.NS, RESOURCE_ELEMENT);		
			}
			serializer.endTag(PublishedScopeResource.NS, RESOURCES_ELEMENT);			
		}
		
		/**
		 * Parses the <em>Resources</em> element 
		 * @param parser the parser
		 * @throws Exception if the element is not valid or well formed
		 */
		void load(KXmlParser parser) throws Exception {
			loop: while (true) {
				try {
					switch (parser.next()) {
						case KXmlParser.START_TAG:
							if (parser.getName().equals(RESOURCE_ELEMENT)) {
								String id=null, type=null, hostedOn=null;
								Date time = null;
								innerloop: while (true) {
									switch (parser.next()) {
										case KXmlParser.START_TAG:
											if (parser.getName().equals("ResourceID")) id=parser.nextText();
											else if (parser.getName().equals("ResourceType")) type=parser.nextText();
											else if (parser.getName().equals("HostedOn")) hostedOn=parser.nextText();
											else if (parser.getName().equals("JointTime")) time=ProfileDate.fromXMLDateAndTime(parser.nextText());
											else parser.nextText();
											break;
										case KXmlParser.END_TAG: if (parser.getName().equals(RESOURCE_ELEMENT)){this.add(id, type, time, hostedOn);break innerloop;} 
											break;
										case KXmlParser.END_DOCUMENT: throw new Exception ("Parsing failed at " + RESOURCE_ELEMENT);	
									}														
								}
							}
							break;
						case KXmlParser.END_TAG: if (parser.getName().equals(RESOURCES_ELEMENT)) break loop;
							break;
						case KXmlParser.END_DOCUMENT: throw new Exception ("Failed to parse at " + RESOURCES_ELEMENT);
					}
				} catch (Exception e) {
					throw new Exception ("Failed to parse at Resources");
				}
			}
		}

		
	}
	

}
