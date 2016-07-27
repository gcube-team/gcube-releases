package org.gcube.data.spd.manager;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.stubs.PluginMap;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.PropertySupport;
import org.gcube.data.spd.model.util.Capabilities;

import com.thoughtworks.xstream.XStream;


public class Manager extends GCUBEWSResource{

	/** RP names. */
	private static String[] RPNames = { Constants.PLUGIN_DESCRIPTION_RPNAME };
	
		
	/** {@inheritDoc} */
	public String[] getPropertyNames() {
		return RPNames;
	}

	

	private static Logger logger = LoggerFactory.getLogger(Manager.class);
	
	@Override
	protected void initialise(Object... args) throws Exception {
		logger.info("creating manager resource");
	}
	
		

	@Override
	public void publish(GCUBEScope... scopes) throws ResourceException {
		String previousScope =ScopeProvider.instance.get(); 
		for (GCUBEScope scope: scopes){
			ScopeProvider.instance.set(scope.toString());
			createPluginsDescription();
		}
		ScopeProvider.instance.set(previousScope);
		super.publish(scopes);
	}

	@Override
	public void unpublish(GCUBEScope... scopes) throws ResourceException {
		for (GCUBEScope scope: scopes){
			ScopeProvider.instance.set(scope.toString());
			removePluginsDescription();
			PluginManager.removeInstance();
		}
		super.unpublish(scopes);
	}
			
	public void removePluginsDescription() {
		ResourceProperty typeRP = this.getResourcePropertySet().get(
				Constants.PLUGIN_DESCRIPTION_RPNAME);
		if (typeRP.size()==0) return;
		PluginMap pm =null;
		for (int i=0; i<typeRP.size(); i++){
			pm=(PluginMap)typeRP.get(i);
			if (pm.getKey().equals(ScopeProvider.instance.get()))
				break;			
		}
		if (pm!=null)
			typeRP.remove(pm);
	}

	public void createPluginsDescription() {
		ResourceProperty typeRP = this.getResourcePropertySet().get(
				Constants.PLUGIN_DESCRIPTION_RPNAME);
		if (typeRP.size()==0)
			typeRP.clear();
		logger.debug("the retrieved plugins for scope "+ScopeProvider.instance.get()+" are "+PluginManager.get().plugins().size());
						
		Set<PluginDescription> descriptions = new HashSet<PluginDescription>();
		
		for (AbstractPlugin plugin : PluginManager.get().plugins().values())
			descriptions.add(getPluginDescription(plugin));
				
		if (descriptions.size()>0){
			String streamedDescriptions = new XStream().toXML(descriptions);
			typeRP.add(new PluginMap(ScopeProvider.instance.get(),streamedDescriptions));
		}
		
	}
	
	
	public void loadPluginDescription(AbstractPlugin plugin){
		ResourceProperty typeRP = this.getResourcePropertySet().get(
				Constants.PLUGIN_DESCRIPTION_RPNAME);
		if (typeRP.size()==0) return;
		PluginMap pm =null;
		for (int i=0; i<typeRP.size(); i++){
			pm=(PluginMap)typeRP.get(i);
			if (pm.getKey().equals(ScopeProvider.instance.get()))
				break;			
		}
		PluginDescription descr = getPluginDescription(plugin);
		if (pm!=null){
			@SuppressWarnings("unchecked")
			Set<PluginDescription> descriptions = (Set<PluginDescription>) new XStream().fromXML(new StringReader(pm.getValue()));
			if (descriptions.contains(plugin.getRepositoryName())){
				logger.trace("updating description for plugin "+plugin.getRepositoryName()+" in scope "+ScopeProvider.instance.get());
				descriptions.remove(descr);
			}
			descriptions.add(descr);
			String streamedDescriptions = new XStream().toXML(descriptions);
			pm.setValue(streamedDescriptions);
		}else {
			Set<PluginDescription> descriptions = new HashSet<PluginDescription>();
			descriptions.add(descr);
			String streamedDescriptions = new XStream().toXML(descriptions);
			typeRP.add(new PluginMap(ScopeProvider.instance.get(),streamedDescriptions));
		}
		
	}
	
	protected static PluginDescription getPluginDescription(AbstractPlugin plugin){
		PluginDescription description = new PluginDescription(plugin.getRepositoryName(), plugin.getDescription(), plugin.getRepositoryInfo());
		description.setRemote(plugin.isRemote());
		
		Map<Capabilities, List<Conditions>> capabilityMap = new HashMap<Capabilities, List<Conditions>>();
		
		
		for (Capabilities capability : plugin.getSupportedCapabilities()){
			if (capability.isPropertySupport())
				try{
					Set<Conditions> props = ((PropertySupport) plugin.getClass().getDeclaredMethod(capability.getMethod()).invoke(plugin)).getSupportedProperties();		
					capabilityMap.put(capability, new ArrayList<Conditions>(props));
				}catch (Exception e) {
					logger.warn("cannot retreive properties for capability "+capability,e);
				}
			else{
				List<Conditions> emptyConditions = Collections.emptyList();
				capabilityMap.put(capability, emptyConditions);
			}
		}
		description.setSupportedCapabilities(capabilityMap);
		return description;
	}
	
}
