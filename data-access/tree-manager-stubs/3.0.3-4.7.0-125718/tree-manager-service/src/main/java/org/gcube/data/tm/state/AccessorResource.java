/**
 * 
 */
package org.gcube.data.tm.state;

import static java.util.concurrent.TimeUnit.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;

import javax.xml.namespace.QName;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.state.GCUBEWSLiteResource;
import org.gcube.common.core.state.GCUBEWSResourcePropertyProxy.ResourcePropertyEvent;
import org.gcube.common.core.state.GCUBEWSResourcePropertySet;
import org.gcube.common.core.state.GCUBEWSResourcePropertySet.RPSetChange;
import org.gcube.common.core.types.DescriptiveProperty;
import org.gcube.data.tm.Constants;
import org.gcube.data.tm.stubs.UpdateNotificationType;
import org.gcube.data.tm.stubs.UpdateNotificationTypeWrapper;
import org.gcube.data.tmf.api.Property;
import org.gcube.data.tmf.api.Source;
import org.gcube.data.tmf.api.SourceConsumer;
import org.gcube.data.tmf.api.SourceEvent;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.impl.ReflectionResourceProperty;
import org.globus.wsrf.impl.SimpleTopic;

/**
 * Base class for stateful resources of T-Reader and T-Writer services.
 * 
 * @author Fabio Simeoni
 *
 */
public abstract class AccessorResource extends GCUBEWSLiteResource<SourceResource> implements SourceConsumer {
		
	//part size for outgoing resultsets 
	protected static final int PART_SIZE = 10;
	
	//default event coalescing delay
	private static final int DEFAULT_CHANGE_COALESCING_DELAY = 60;

	//notification thread
	private Thread notifier;
	
	//RP topic for collective subscription to dynamic collection properties
	private SimpleTopic updateTopic= new SimpleTopic(Constants.UPDATETOPIC_QNAME);
	
	
	/**{@inheritDoc}*/
	@Override protected void initialise(Object... params) throws Exception {
		subscribeForChange();
	} 
	
	/**{@inheritDoc}*/
	@Override
	protected String[] getPropertyNames() {return new String[0];} //playing tricks here: we do things manually instead
	
	
	/**{@inheritDoc}*/
	//uses this instead of usual mechanism to add resource properties from different namespace
	@Override protected void initialiseContainers() throws Exception {
		super.initialiseContainers();
		getResourcePropertySet().add(new ReflectionResourceProperty(Constants.SOURCEID_RP,this));
		getResourcePropertySet().add(new ReflectionResourceProperty(Constants.SOURCENAME_RP,this));
		getResourcePropertySet().add(new ReflectionResourceProperty(Constants.SOURCETYPE_RP,this));
		getResourcePropertySet().add(new ReflectionResourceProperty(Constants.PROPERTY_RP,this));
		getResourcePropertySet().add(new ReflectionResourceProperty(Constants.PLUGIN_RP,this));
		
		ReflectionResourceProperty cardinality = new ReflectionResourceProperty(Constants.CARDINALITY_RP,this);
		
		getResourcePropertySet().add(cardinality);
		ReflectionResourceProperty lastUpdate = new ReflectionResourceProperty(Constants.LAST_UPDATE_RP,this); 
		getResourcePropertySet().add(lastUpdate);
		
		getTopicList().addTopic(updateTopic);
		
	}

	/**
	 * Returns the name of the associated plugin.
	 * @return the name
	 */
	public String getPlugin() throws ResourceException {
		return getLocalResource().getPluginName();
	}
	
	/**
	 * Returns the source identifier.
	 * @return the identifier
	 */
	public String getSourceId() throws ResourceException {
		return getLocalResource().source().id();
	}
	
	/**
	 * Returns the source identifier.
	 * @return the identifier
	 */
	public String getName() throws ResourceException {
		return getLocalResource().source().name();
	}
	
	/**
	 * Returns the cardinality of the source.
	 * @return the cardinality
	 */
	public Long getCardinality() throws ResourceException {
		
		return getLocalResource().source().cardinality();
	}
	
	/**
	 * Returns the time in which the collection was last updated.
	 * @return the time.
	 */
	public Calendar getLastUpdate() throws ResourceException {
		return getLocalResource().source().lastUpdate();
	}
	
	/**
	 * Returns the data type of the resource.
	 * @return the type.
	 */
	public QName[] getType() throws ResourceException {
		List<QName> types = getLocalResource().source().types();
		return types==null?null:types.toArray(new QName[0]);
	}
	
	/**
	 * Returns the descriptive properties of the resource.
	 * @return the properties.
	 */
	public DescriptiveProperty[] getProperty() throws ResourceException {
		List<Property> properties = getLocalResource().source().properties();
		if (properties != null) {
			List<DescriptiveProperty> descriptiveProperties = new ArrayList<DescriptiveProperty>();
			for (Property prop : properties) {
				DescriptiveProperty property = new DescriptiveProperty(
						prop.description(), prop.name(), prop.value());
				descriptiveProperties.add(property);
			}
			return descriptiveProperties.toArray(new DescriptiveProperty[0]);
		}
		else
			return null;
	}
	
	//subscribes for state changes with plugin's collection
	protected  void subscribeForChange()  throws ResourceException {
		Source source = getLocalResource().source();
		logger.trace(this+" is subscribing for changes to source "+source);
		source.notifier().subscribe(this,SourceEvent.CHANGE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void onEvent(SourceEvent ... events) {

		if (notifier!=null && notifier.isAlive())	
			return; //filter out interim calls
		
		notifier = new Thread(getClass().getSimpleName()+"-delayed") {
				public void run() {
					try {
						logger.trace("will notify change in "+DEFAULT_CHANGE_COALESCING_DELAY+" second/s");
						SECONDS.sleep(DEFAULT_CHANGE_COALESCING_DELAY);
						logger.trace("sending change notification ("+getID()+")");
						if (GHNContext.getContext().getMode()==GHNContext.Mode.CONNECTED) {
								UpdateNotificationTypeWrapper message = 
									new UpdateNotificationTypeWrapper(new  UpdateNotificationType(getCardinality(),getSourceId(),getLastUpdate()));
								updateTopic.notify(message);
						}	
						
						//hacks around broken gCF mechanism for change notifications towards publishing 
						GCUBEWSResourcePropertySet set = getResourcePropertySet();
						RPSetChange change = set.new RPSetChange(set.get(Constants.LAST_UPDATE_RP),ResourcePropertyEvent.UPDATED);
						for (Field f : set.getClass().getDeclaredFields()) {
							f.setAccessible(true);
							if (f.get(set) instanceof Observable) {
								Observable.class.cast(f.get(set)).notifyObservers(change);
								break;
							}
						} 
					}
					catch (InterruptedException ex) {
						logger.error("unexpected interruption",ex);//no point resetting
					}
					catch (Exception e) {
						logger.warn(getID()+" could not send change notification",e);
					}
					}};
		
			notifier.start();
		
	};
}
