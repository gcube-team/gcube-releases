package org.gcube.informationsystem.registry.impl.state;


import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.informationsystem.registry.stubs.RegistryProperty;
import org.globus.wsrf.ResourceException;


/**
 * 
 * The <em>RegistryFactoryResource</em> has the role to maintain WS-Topics used to notify changes each time
 * a GCUBEResource profile is created/removed/updated. A single instance of this class is created within 
 * each instance of the IS-Registry service.
 * 
 * @author Manuele Simi (CNR)
 *
 */
public class RegistryFactoryResource extends GCUBEWSResource {

	protected static final String ExternalRunningInstanceRP="ExternalRunningInstance";
	
	protected static final String ServiceRP="Service";
	
	protected static final String CollectionRP="Collection";
	
	protected static final String RuntimeResourceRP="RuntimeResource";
		
	protected static final String GHNRP="GHN";
	
	protected static final String gLiteSERP="gLiteSE";
	
	protected static final String gLiteCERP="gLiteCE";
	
	protected static final String gLiteSiteRP="gLiteSite";
	
	protected static final String gLiteServiceRP="gLiteService";
	
	protected static final String VRERP="VRE";
	
	protected static final String GenericResourceRP="GenericResource";
	
	protected static final String MetadataCollectionRP="MetadataCollection";
	
	protected static final String RunningInstanceRP="RunningInstance";
	

	protected static String[] RPNames = {
		RunningInstanceRP,
		ExternalRunningInstanceRP,
		ServiceRP,
		CollectionRP,
		RuntimeResourceRP,
		GHNRP,
		gLiteSERP,
		gLiteCERP,
		gLiteSiteRP,
		gLiteServiceRP,
		VRERP,
		MetadataCollectionRP,
		GenericResourceRP
		
	};
	
	@Override
	protected String[] getTopicNames() {		
		return RPNames;
	}
	
	/**
	 * 
	 *   Initializes Resource Properties. 
	 *   @throws  Exception Exception
	 */
	protected void initialise(Object... o) throws ResourceException {
		logger.debug("Initialising the RegistryFactoryresource...");
		/* Initialize the RP's */
		this.initialiseRPs();
		logger.debug("RegistryFactoryResource RPs initialised");
			
	}
	
	
	
	/** 
	 * Default Setter for the RP runningInstanceRP
	 * 
	 * @param property The RI property
	 * 
	 */ 
	public  void setRunningInstance (RegistryProperty property) {
		this.getResourcePropertySet().get(RunningInstanceRP).clear();
		this.getResourcePropertySet().get(RunningInstanceRP).add(property);
		//this.getPersistenceDelegate().store(this);
		
	}
	
	/** 
	 * Default getter for the RP runningInstanceRP
	 * 
	 * @return  The RegistryProperty
	 */ 
	public  RegistryProperty getRunningInstance () {
		return (RegistryProperty) this.getResourcePropertySet().get(RunningInstanceRP).get(0);
		
	}
	
	
	/** 
	 * Default Setter for the RP ExternalRunningInstanceRP
	 * 
	 * @param   property The RegistryProperty
	 * 
	 */ 
	public  void setExternalRunningInstance (RegistryProperty property) {
		this.getResourcePropertySet().get(ExternalRunningInstanceRP).clear();
		this.getResourcePropertySet().get(ExternalRunningInstanceRP).add(property);
		//this.getPersistenceDelegate().store(this);
		
	}
	
	/** 
	 * Default getter for the RP ExternalRunningInstanceRP
	 * 
	 * @return   The RegistryProperty
	 * 
	 */ 
	public  RegistryProperty getExternalRunningInstance () {
		return (RegistryProperty) this.getResourcePropertySet().get(ExternalRunningInstanceRP).get(0);
		
	}
	
	
	/** 
	 * Default Setter for the RP Service
	 * 
	 * @param   property The RegistryProperty

	 * 
	 */ 
	public  void setService (RegistryProperty property) {
		this.getResourcePropertySet().get(ServiceRP).clear();
		this.getResourcePropertySet().get(ServiceRP).add(property);
		//this.getPersistenceDelegate().store(this);
		
	}
	
	/** 
	 * Default getter for the RP Service
	 * 
	 * @return   The RegistryProperty
	 * 
	 */ 
	public  RegistryProperty getService () {
		return (RegistryProperty) this.getResourcePropertySet().get(ServiceRP).get(0);
		
	}
	
	
	/** 
	 * Default Setter for the RP Collection
	 * 
	 * @param   property The RegistryProperty
	 * 
	 */ 
	public  void setCollection (RegistryProperty property) {
		this.getResourcePropertySet().get(CollectionRP).clear();
		this.getResourcePropertySet().get(CollectionRP).add(property);		
	}
	
	/** 
	 * Default getter for the RP Collection
	 * @return   The RegistryProperty
	 * 
	 */ 
	public  RegistryProperty getCollection () {
		return (RegistryProperty) this.getResourcePropertySet().get(CollectionRP).get(0);
		
	}
	
	
	/** 
	 * Default Setter for the RP RuntimeResource
	 * 
	 * @param  property The RegistryProperty
	 * 
	 */ 
	public  void setRuntimeResource (RegistryProperty property) {
		this.getResourcePropertySet().get(RuntimeResourceRP).clear();
		this.getResourcePropertySet().get(RuntimeResourceRP).add(property);		
	}
	
	/** 
	 * Default getter for the RP RuntimeResource
	 * 
	 * @return   The RegistryProperty
	 * 
	 */ 
	public  RegistryProperty getRuntimeResource () {
		return (RegistryProperty) this.getResourcePropertySet().get(RuntimeResourceRP).get(0);
		
	}
	
	
	/** 
	 * Default Setter for the RP ghn
	 * 
	 * @param property  The RegistryProperty
	 * 
	 */ 
	public  void setGHN (RegistryProperty property) {
		this.getResourcePropertySet().get(GHNRP).clear();
		this.getResourcePropertySet().get(GHNRP).add(property);
		//this.getPersistenceDelegate().store(this);
		
	}
	
	/** 
	 * Default getter for the RP ghn
	 * 
	 * @return   The RegistryProperty
	 */ 
	public  RegistryProperty getGHN () {
		return (RegistryProperty) this.getResourcePropertySet().get(GHNRP).get(0);
		
	}
	
	/** 
	 * Default Setter for the RP gLiteSE
	 * 
	 * @param property The RegistryProperty
	 * 
	 */ 
	public  void setGLiteSE (RegistryProperty property) {
		this.getResourcePropertySet().get(gLiteSERP).clear();
		this.getResourcePropertySet().get(gLiteSERP).add(property);
	}
	
	/** 
	 * Default getter for the RP gLiteSE
	 * 
	 * @return   The RegistryProperty
	 * 
	 */ 
	public  RegistryProperty getGLiteSE () {
		return (RegistryProperty) this.getResourcePropertySet().get(gLiteSERP).get(0);
		
	}
	
	/** 
	 * Default Setter for the RP gLiteCE
	 * 
	 * @param  property The RegistryProperty
	 * 
	 */ 
	public  void setGLiteCE (RegistryProperty property) {
		this.getResourcePropertySet().get(gLiteCERP).clear();
		this.getResourcePropertySet().get(gLiteCERP).add(property);		
	}
	
	/** 
	 * Default getter for the RP gLiteCE
	 * @return   The RegistryProperty
	 * 
	 */ 
	public  RegistryProperty getGLiteCE () {
		return (RegistryProperty) this.getResourcePropertySet().get(gLiteCERP).get(0);
		
	}
	
	
	
	/** 
	 * Default Setter for the RP gLiteSite
	 * 
	 * @param  property The RegistryProperty
	 * 
	 */ 
	public  void setGLiteSite (RegistryProperty property) {
		this.getResourcePropertySet().get(gLiteSiteRP).clear();
		this.getResourcePropertySet().get(gLiteSiteRP).add(property);		
	}
	
	/** 
	 * Default getter for the RP gLiteSite
	 * 
	 * @return   The RegistryProperty
	 * 
	 */ 
	public  RegistryProperty getGLiteSite () {
		return (RegistryProperty) this.getResourcePropertySet().get(gLiteSiteRP).get(0);
		
	}
	
	/** 
	 * Default Setter for the RP gLiteSite
	 * 
	 * @param property The RegistryProperty
	 * 
	 */ 
	public  void setGLiteService (RegistryProperty property) {
		this.getResourcePropertySet().get(gLiteServiceRP).clear();
		this.getResourcePropertySet().get(gLiteServiceRP).add(property);		
	}
	
	/** 
	 * Default getter for the RP gLiteSite
	 * @return   The RegistryProperty
	 * 
	 */ 
	public  RegistryProperty getGLiteService () {
		return (RegistryProperty) this.getResourcePropertySet().get(gLiteServiceRP).get(0);
		
	}
	
	/** 
	 * Default Setter for the RP VRE
	 * 
	 * @param property The RegistryProperty
	 * 
	 */ 
	public  void setVRE (RegistryProperty property) {
		this.getResourcePropertySet().get(VRERP).clear();
		this.getResourcePropertySet().get(VRERP).add(property);		
	}
	
	/** 
	 * Default getter for the RP VRE
	 * @return   The RegistryProperty
	 * 
	 */ 
	public  RegistryProperty getVRE () {
		return (RegistryProperty) this.getResourcePropertySet().get(gLiteServiceRP).get(0);
	}
	
	
	/** 
	 * Default Setter for the RP MetadataColletion
	 * 
	 * @param property The RegistryProperty
	 * 
	 */ 
	public  void setMetadataCollection (RegistryProperty property) {
		this.getResourcePropertySet().get(MetadataCollectionRP).clear();
		this.getResourcePropertySet().get(MetadataCollectionRP).add(property);		
	}
	
	/** 
	 * Default getter for the RP MetadataColletion
	 * 
	 * @return   The RegistryProperty
	 * 
	 */ 
	public  RegistryProperty getMetadataCollection () {
		return (RegistryProperty) this.getResourcePropertySet().get(MetadataCollectionRP).get(0);
		
	}
	
		
	/** 
	 * Default Setter for the RP Generic
	 * 
	 * @param property The RegistryProperty
	 * 
	 */ 
	public  void setGenericResource (RegistryProperty property) {
		this.getResourcePropertySet().get(GenericResourceRP).clear();
		this.getResourcePropertySet().get(GenericResourceRP).add(property);
		//this.getPersistenceDelegate().store(this);
	}
	
	/** 
	 * Default getter for the RP Generic
	 * 
	 * @return   The RegistryProperty
	 * 
	 */ 
	public  RegistryProperty getGenericResource () {
		return (RegistryProperty) this.getResourcePropertySet().get(GenericResourceRP).get(0);
		
	}

	/**
	 * Sets an empty resource for each RP for notification purposes
	 */
	private void initialiseRPs() {
		
		RegistryProperty property = new RegistryProperty();
						
		this.getResourcePropertySet().get(RunningInstanceRP).clear();
		this.getResourcePropertySet().get(RunningInstanceRP).add(property);
		
		this.getResourcePropertySet().get(ExternalRunningInstanceRP).clear();
		this.getResourcePropertySet().get(ExternalRunningInstanceRP).add(property);
		
		this.getResourcePropertySet().get(ServiceRP).clear();
		this.getResourcePropertySet().get(ServiceRP).add(property);
		
		this.getResourcePropertySet().get(CollectionRP).clear();
		this.getResourcePropertySet().get(CollectionRP).add(property);
		
		this.getResourcePropertySet().get(RuntimeResourceRP).clear();
		this.getResourcePropertySet().get(RuntimeResourceRP).add(property);
				
		this.getResourcePropertySet().get(GHNRP).clear();
		this.getResourcePropertySet().get(GHNRP).add(property);
		
		this.getResourcePropertySet().get(gLiteCERP).clear();
		this.getResourcePropertySet().get(gLiteCERP).add(property);
				
		this.getResourcePropertySet().get(gLiteSERP).clear();
		this.getResourcePropertySet().get(gLiteSERP).add(property);
		
		this.getResourcePropertySet().get(gLiteServiceRP).clear();
		this.getResourcePropertySet().get(gLiteServiceRP).add(property);
		
		this.getResourcePropertySet().get(gLiteSiteRP).clear();
		this.getResourcePropertySet().get(gLiteSiteRP).add(property);
		
		this.getResourcePropertySet().get(VRERP).clear();
		this.getResourcePropertySet().get(VRERP).add(property);
		
		this.getResourcePropertySet().get(MetadataCollectionRP).clear();
		this.getResourcePropertySet().get(MetadataCollectionRP).add(property);
		
		this.getResourcePropertySet().get(GenericResourceRP).clear();
		this.getResourcePropertySet().get(GenericResourceRP).add(property);
				
	}

}
