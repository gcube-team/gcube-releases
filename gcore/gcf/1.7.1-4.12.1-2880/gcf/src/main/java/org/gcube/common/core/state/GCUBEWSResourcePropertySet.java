package org.gcube.common.core.state;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.xml.namespace.QName;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager.IllegalScopeException;
import static org.gcube.common.core.state.GCUBEWSResourcePropertyProxy.ResourcePropertyEvent;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.impl.ReflectionResourceProperty;
import org.globus.wsrf.impl.SimpleResourceProperty;
import org.globus.wsrf.impl.SimpleResourcePropertyMetaData;
import org.globus.wsrf.impl.SimpleResourcePropertySet;

/**
 * Set of Resource Properties for {@link GCUBEWSResource GCUBEWSResources}.
 * 
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (CNR-ISTI)
 * 
 */
public class GCUBEWSResourcePropertySet extends SimpleResourcePropertySet {	

	/** Namespace of the gCube Provider */
     public static final String PROVIDER_NS = "http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider";

    /** RP: The identifier of the service which manages the WS-Resource. */ 
    private ResourceProperty serviceID;
    /** The name of the service ID RP. */
    public static final String RP_SID_NAME = "ServiceID";

    /** RP: The gCube name of the service which manages the WS-Resource. */
    private ResourceProperty serviceName;
    /** The name of the service name RP. */
    public static final String RP_SNAME_NAME = "ServiceName";

    /** RP: The gCube class of the service which manages the WS-Resource. */
    private ResourceProperty serviceClass;
    /** The name of the service class RP. */
    public static final String RP_SCLASS_NAME = "ServiceClass";

    /** RP: The identifier of the RI of the service which manages the WS-Resource. */
    private ResourceProperty RI;
    /** The name of the RI ID RP. */
    public static final String RP_RIID_NAME = "RI";

    /** RP: The identifier of the GHN which hosts the WS-Resource. */
    private ResourceProperty GHN;
    /** The name of the GHN ID RP. */
    public static final String RP_GID_NAME = "GHN";

    /** RP: The scopes in which the WS-Resource exists. */
    private ResourceProperty scope;
    /** The name of the DLs RP. */
    public static final String RP_SCOPES_NAME = "Scope";

    /**RP: Lease termination time. Regulates scheduled destruction of WS-Resources.*/
    private ResourceProperty terminationTime;

    /** RP: current time. Regulates scheduled destruction of WS-Resources. */
    private ResourceProperty currentTime;

    /** The port-type context of the WS-Resource. */
    protected volatile GCUBEStatefulPortTypeContext context;
    
    private Observable producer = new RPSetChangeProducer(); 
    
    /** Class logger. */
    protected GCUBELog logger = new GCUBELog(GCUBEWSResourcePropertySet.class);

    /**
     * Creates an instance for a given resource.
     * @param resource the resource.
     * @throws Exception  if the set could not be initialised.
     */
    public GCUBEWSResourcePropertySet(GCUBEWSResource resource) throws Exception {
    	super(new QName(resource.getPorttypeContext().getNamespace(),resource.getPorttypeContext().getRPDName()));
    	this.context = resource.getPorttypeContext();
    	logger.setContext(this.context.getServiceContext());

		// define default RPs
		this.serviceID = new SimpleResourceProperty(new QName(PROVIDER_NS, RP_SID_NAME));
		this.serviceName = new SimpleResourceProperty(new QName(PROVIDER_NS,RP_SNAME_NAME));
		this.serviceClass = new SimpleResourceProperty(new QName(PROVIDER_NS,RP_SCLASS_NAME));
		this.RI = new SimpleResourceProperty(new QName(PROVIDER_NS, RP_RIID_NAME));
		this.GHN = new SimpleResourceProperty(new QName(PROVIDER_NS, RP_GID_NAME));
		this.scope = new SimpleResourceProperty(new QName(PROVIDER_NS, RP_SCOPES_NAME));
		// current time
		this.currentTime = new ReflectionResourceProperty(
			SimpleResourcePropertyMetaData.CURRENT_TIME, this);
		// By default, lifetime is infinite but, if configured, will be
		// overridden before initialisation is over.
		this.terminationTime = new SimpleResourceProperty(
			SimpleResourcePropertyMetaData.TERMINATION_TIME);
	
		// load properties into set (calling the super class, no need for notifications here)
		super.add(this.RI);
		super.add(this.serviceID);
		super.add(this.serviceName);
		super.add(this.GHN);
		super.add(this.serviceClass);
		super.add(this.scope);
		super.add(this.currentTime);
		super.add(this.terminationTime);

		// initialise GCUBE properties
		this.setGHN(GHNContext.getContext().getGHNID());
		this.setServiceID(context.getServiceContext().getID());
		this.setServiceName(context.getServiceContext().getName());
		this.setServiceClass(context.getServiceContext().getServiceClass());
		this.setRI(context.getServiceContext().getInstance().getID());
		this.setTerminationTime(Calendar.getInstance());
    }
   
    /**
     * Returns a resource property from its local name.
     * @param name the name.
     * @return the property.
     */
    public ResourceProperty get(String name) {return this.get(new QName(context.getNamespace(),name));}
    
    
    /**
     * Returns a system resource property from its local name.
     * @param name the name.
     * @return the property.
     */
    public ResourceProperty getSystemRP(String name) {return this.get(new QName(PROVIDER_NS, name));}

    /**
     * Returns the current time.
     * @return the time.
     */
    public Calendar getCurrentTime() {return Calendar.getInstance();}

    /**
     * Returns the termination time of the WS-Resource.
     * @return the termination time.
     */
    public synchronized Calendar getTerminationTime() {return (Calendar) this.terminationTime.get(0);}

    /**
     * Sets the termination time of WS-Resource.
     * @param calendar the time.
     */
    public synchronized void setTerminationTime(Calendar calendar) {
		this.terminationTime.clear();
		this.terminationTime.add(calendar);
    }

    /**
     * Returns the identifier of the Running Instance of the WS-Resource.
     * @return the identifier.
     */
    public synchronized String getRI() {return (String) this.RI.get(0);}

    /**
     * Set the identifier of the Running Instance of the WS-Resource.
     * @param id the identifier
     */
    public synchronized void setRI(String id) {
		this.RI.clear();
		this.RI.add(id);
    }

    /**
     * Returns the identifier of the service of the WS-Resource.
     * @return the identifier
     */
    public synchronized String getServiceID() {return (String) this.serviceID.get(0);}

    /**
     * Set the identifier of the service of the WS-Resource.
     * @param id the identifier.
     */
    public synchronized void setServiceID(String id) {
		this.serviceID.clear();
		this.serviceID.add(id);
    }

    /**
     * Returns the identifier of the GHN of the WS-Resource.
     * @return the identifier.
     */
    public synchronized String getGHN() {return (String) this.GHN.get(0);}

    /**
     * Sets the identifier of the GHN of the WS-Resource.
     * @param id the identifier.
     */
    public synchronized void setGHN(String id) {
		this.GHN.clear();
		this.GHN.add(id);
    }

    /**
     * Sets the name of the service of the WS-Resource.
     * @param name the name.
     */
    public synchronized void setServiceName(String name) {
		this.serviceName.clear();
		this.serviceName.add(name);
    }

    /**
     * Returns the name of the service of the WS-Resource.
     * @return the name.
     */
    public synchronized String getServiceName() {return (String) this.serviceName.get(0);}

    /**
     * Sets the class of the service of the WS-Resource.
     * @param serviceclass the class.
     */
    public synchronized void setServiceClass(String serviceclass) {
		this.serviceClass.clear();
		this.serviceClass.add(serviceclass);
    }

    /**
     * Returns the class of the service of the WS-Resource.
     * @return the class.
     */
    public synchronized String getServiceClass() {return (String) this.serviceClass.get(0);}

    /**
     * Sets the scopes of the WS-Resource.
     * @param scopes the scopes.
     */
    public synchronized void setScope(List<String> scopes) {
    	if (scopes==null) return;
		this.scope.clear(); 
		for (String scope : scopes) if (scope!=null) this.scope.add(scope);
    }

    /**
     * Returns the scopes of the WS-Resource.
     * @return the scopes.
     */
    public synchronized List<String> getScope() {
    	List<String> scopes = new ArrayList<String>();
    	Iterator<?> i = this.scope.iterator();
    	while (i.hasNext()) scopes.add((String) i.next());
    	return scopes;
    }

    /**
     * Adds a given scope to the WS-Resource.
     * @param scope the scope.
     * @return <code>true</code> if the scope did not exist and was successfully added, <code>false</code> otherwise.
     */
    public synchronized boolean addScope(GCUBEScope scope) {
    	if (scope==null) throw new IllegalScopeException();
    	List<String> scopes = this.getScope();
    	if (scopes.contains(scope.toString())) return false;
    	this.scope.add(scope.toString());
    	return true;
    }

    /**
     * Removes the WS-Resource from a given scope.
     * @param scope the scope.
     * @return <code>true</code> if the scope existed and the binding removed, <code>false</code> otherwise.
     */
    public synchronized boolean removeScope(GCUBEScope scope) {
    	if (scope==null) throw new IllegalScopeException();
    	boolean removed=false;
		Iterator<?> i = this.scope.iterator();
    	while (i.hasNext()) if (((String) i.next()).equals(scope.toString())) {i.remove();removed=true;break;}
		return removed;
    }

    /**
     * Returns the names of the system properties common to all WS-Resources.
     * @return the system property names;
     */
    public static String[] getSystemRPNames() {
    	return new String[]{
    			SimpleResourcePropertyMetaData.TERMINATION_TIME.getName().getLocalPart(),
    			SimpleResourcePropertyMetaData.CURRENT_TIME.getName().getLocalPart(),
    			RP_GID_NAME,RP_RIID_NAME,RP_SCLASS_NAME,RP_SCOPES_NAME,RP_SID_NAME,RP_SNAME_NAME};
    }

	/* (non-Javadoc)
	 * @see org.globus.wsrf.impl.SimpleResourcePropertySet#add(org.globus.wsrf.ResourceProperty)
	 */
	@Override
	public boolean add(ResourceProperty prop) {
		boolean ret = super.add(prop);
		this.producer.notifyObservers(new RPSetChange(prop,ResourcePropertyEvent.CREATED));
		return ret;
	}	
	
	/** Deletes an observer from the set of observers of this object. 
     * Passing <CODE>null</CODE> to this method will have no effect.
     * @param   o   the observer to be deleted.
     */
    public void deleteObserver(Observer o) {
        this.producer.deleteObserver(o);
    }

    /**
     * Adds an observer to the set of observers for this RPSet
     *
     * @param   o   an observer to be added.
     * @throws NullPointerException   if the parameter o is null.
     */
    public synchronized void addObserver(Observer o) {    
    	this.producer.addObserver(o);
	}
    
    /**
     * Notifies the registered observer about change on the RP
     * @param change
     */
    protected void notifyObservers(final RPSetChange change) {
    	new Thread("RPSetNotifier") {
			@Override
			public void run() {	
				GCUBEWSResourcePropertySet.this.producer.notifyObservers(change);
			}
		}.start();		
    }
	
	/* (non-Javadoc)
	 * @see org.globus.wsrf.impl.SimpleResourcePropertySet#clear()
	 */
	@Override
	public void clear() {
		super.clear();
		//all the RPs are cleared 
		this.notifyObservers(new RPSetChange(null, ResourcePropertyEvent.DELETED));
	}
	
    
	/** RPSet change event. */
	public class RPSetChange {
		private ResourceProperty resourceProperty;
		private ResourcePropertyEvent event;
		
		/**
		 * Creates a new change
		 * @param rp the modified resourceProperty
		 * @param topic the event occurred on the resourceProperty
		 */
		public RPSetChange(ResourceProperty rp, ResourcePropertyEvent event) {
			this.resourceProperty = rp; this.event = event;
		}
		/**
		 * @return the resourceProperty
		 */
		public ResourceProperty getResourceProperty() {
			return resourceProperty;
		}
		/**
		 * @return the event occurred on the resourceProperty
		 */
		public ResourcePropertyEvent getEvent() {
			return event;
		}
		
	}	
	
	/**
	 * Observable for {@link RPSetChange} notifications
	 * @author manuele
	 *
	 */
	class RPSetChangeProducer extends Observable {

		/* (non-Javadoc)
		 * @see java.util.Observable#notifyObservers()
		 */
		@Override
		public void notifyObservers() {
			this.setChanged();
			super.notifyObservers();
		}

		/* (non-Javadoc)
		 * @see java.util.Observable#notifyObservers(java.lang.Object)
		 */
		@Override
		public void notifyObservers(Object arg) {
			this.setChanged();
			super.notifyObservers(arg);
		}				
	}

}
