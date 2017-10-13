package gr.cite.gaap.servicelayer;

import gr.cite.gaap.datatransferobjects.LayerStyleMessenger;
import gr.cite.gaap.utilities.PresentationConfigXMLHandler;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.GeocodeSystemDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig.SysConfigClass;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.dao.SysConfigDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.SystemGlobalConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.SystemLayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.SystemMappingConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.GeoStyle;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.LayerStyle;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.TermStyle;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.Theme;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationManager {
	
	public interface SystemStatusListener {
		public void onStatusChange();
	}
		
	private SystemLayerConfig systemLayerConfig = null;
	private SystemMappingConfig systemMappingConfig = null;
	private SystemPresentationConfig systemPresentationConfig = null;
	private SystemGlobalConfig systemGlobalConfig = null;
	private Object systemGlobalConfigLock = new Object();
	private Object systemLayerConfigLock = new Object();
	private Object systemMappingConfigLock = new Object();
	private Object systemPresentationConfigLock = new Object();
	
	private GeocodeSystemDao geocodeSystemDao;
	private SysConfigDao sysConfigDao;		
		
	@Inject
	public void setGeocodeSystemDao(GeocodeSystemDao geocodeSystemDao) {
		this.geocodeSystemDao = geocodeSystemDao;
	}
	
	@Inject
	public void setSysConfigDao(SysConfigDao sysConfigDao) {
		this.sysConfigDao = sysConfigDao;
	}
	
	@Transactional(readOnly = true)
	public boolean isSystemOnline() throws Exception
	{
		synchronized(systemGlobalConfigLock)
		{
			if(systemGlobalConfig == null) retrieveSystemGlobalConfig();
			
			return systemGlobalConfig.isSystemOnline();
		}
	}
	
	private void updateOnlineStatus(boolean value) throws Exception
	{
		synchronized(systemGlobalConfigLock)
		{
			if(systemGlobalConfig == null) retrieveSystemGlobalConfig();
			
			systemGlobalConfig.setSystemOnline(value);
			updateSystemGlobalConfig();
		}
	}
	
	
	@Transactional
	public void bringUpSystem() throws Exception
	{
		updateOnlineStatus(true);
	}
	
	public List<String> listTaxonomyConfigTypes() throws Exception
	{
		Set<String> cachedTypes = SystemGlobalConfig.getCachedTaxonomyConfigTypes();
		if(cachedTypes != null)
			return new ArrayList<String>(cachedTypes);
		
		synchronized(systemGlobalConfigLock)
		{
			if(systemGlobalConfig == null) retrieveSystemGlobalConfig();
			return new ArrayList<String>(systemGlobalConfig.getTaxonomyConfigTypes());
		}
	}
	
	@Transactional
	public void setTaxonomyConfig(List<TaxonomyConfig> taxonomyConfigs) throws Exception
	{
		Map<String, GeocodeSystem> taxons = new HashMap<String, GeocodeSystem>();
		for(TaxonomyConfig taxonomyConfig : taxonomyConfigs)
		{
			List<GeocodeSystem> t = geocodeSystemDao.findByName(taxonomyConfig.getId());
			if(t == null || t.size() == 0) throw new Exception("Taxonomy " + taxonomyConfig.getId() + " not found");
			if(t.size() != 1) throw new Exception("Non-unique taxonomy " + taxonomyConfig.getId());
			taxons.put(taxonomyConfig.getId(), t.get(0));
		}
		
		synchronized(systemGlobalConfigLock)
		{
			if(systemGlobalConfig == null) retrieveSystemGlobalConfig();
		
			List<TaxonomyConfig> toDelete = new ArrayList<TaxonomyConfig>();
			for(TaxonomyConfig taxonomyConfig : taxonomyConfigs)
			{
				if(taxonomyConfig.getId() == null || taxonomyConfig.getId().isEmpty() || taxonomyConfig.getId().equalsIgnoreCase("none"))
				{
					toDelete.add(taxonomyConfig);
					continue;
				}
				
				GeocodeSystem t = taxons.get(taxonomyConfig.getId());
				taxonomyConfig.setId(t.getId().toString());
				systemGlobalConfig.setTaxonomyConfig(taxonomyConfig);
			}
			for(TaxonomyConfig taxonomyConfig : toDelete)
				systemGlobalConfig.removeTaxonomyConfig(taxonomyConfig);
			
			updateSystemGlobalConfig();
		}
	}
	
	@Transactional
	public void setTaxonomyConfig(TaxonomyConfig taxonomyConfig) throws Exception
	{
		List<GeocodeSystem> t = geocodeSystemDao.findByName(taxonomyConfig.getId());
		if(t == null) throw new Exception("Taxonomy " + taxonomyConfig.getId() + " not found");
		if(t.size() != 1) throw new Exception("Non-unique taxonomy " + taxonomyConfig.getId());
		
		synchronized(systemGlobalConfigLock)
		{
			if(systemGlobalConfig == null) retrieveSystemGlobalConfig();
			
			taxonomyConfig.setId(t.get(0).getId().toString());
			systemGlobalConfig.setTaxonomyConfig(taxonomyConfig);
			
			updateSystemGlobalConfig();
		}
		
	}
	
	@Transactional
	public void addTaxonomyConfigType(String taxonomyConfigType) throws Exception
	{
		if(SystemGlobalConfig.getCachedTaxonomyConfigTypes() != null && SystemGlobalConfig.getCachedTaxonomyConfigTypes().contains(taxonomyConfigType))
			return;
		
		synchronized(systemGlobalConfigLock)
		{
			if(systemGlobalConfig == null) retrieveSystemGlobalConfig();
			
			if(!systemGlobalConfig.getTaxonomyConfigTypes().contains(taxonomyConfigType))
			{
				systemGlobalConfig.addTaxonomyConfigType(taxonomyConfigType);
				updateSystemGlobalConfig();
			}
		}
		
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyConfig> retrieveTaxonomyConfig(boolean translateId) throws Exception
	{
		List<TaxonomyConfig> res = new ArrayList<TaxonomyConfig>();
		List<TaxonomyConfig> cfg  = null;
		synchronized(systemGlobalConfigLock)
		{
			if(systemGlobalConfig == null) retrieveSystemGlobalConfig();
		
			cfg =  systemGlobalConfig.getTaxonomyConfig();
		}
		
//		for(TaxonomyConfig c : cfg)
//		{
//			TaxonomyConfig tcfg = new TaxonomyConfig();
//			tcfg.setId(translateId ? geocodeSystemDao.read(UUID.fromString(c.getId())).getName() : c.getId());
//			tcfg.setType(c.getType());
//			res.add(tcfg);
//		}
		
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyConfig> retrieveTaxonomyConfig(String taxonomyConfigType, boolean translateId) throws Exception
	{
		List<TaxonomyConfig> cfg  = null;
		synchronized(systemGlobalConfigLock)
		{
			if(systemGlobalConfig == null) retrieveSystemGlobalConfig();
		
			cfg =  systemGlobalConfig.getTaxonomyConfig();
		}
		
		List<TaxonomyConfig> taxonomyConfigs = new ArrayList<TaxonomyConfig>();
		for(TaxonomyConfig c : cfg)
		{
			if(c.getType().equals(taxonomyConfigType))
			{
				TaxonomyConfig tcfg = new TaxonomyConfig();
				tcfg.setId(translateId ? geocodeSystemDao.read(UUID.fromString(c.getId())).getName() : c.getId());
				tcfg.setType(c.getType());
				taxonomyConfigs.add(tcfg);
			}
		}
		if (!taxonomyConfigs.isEmpty()){
			return taxonomyConfigs;
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyConfig> retrieveTaxonomyConfig(TaxonomyConfig.Type taxonomyConfigType, boolean translateId) throws Exception
	{
		return retrieveTaxonomyConfig(taxonomyConfigType.toString(), translateId);
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyConfig> retrieveTaxonomyConfig(String taxonomyConfigType) throws Exception
	{
		return retrieveTaxonomyConfig(taxonomyConfigType, false);
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyConfig> retrieveTaxonomyConfig(TaxonomyConfig.Type type) throws Exception
	{
		return retrieveTaxonomyConfig(type.toString());
	}
	
	private List<GeocodeSystem> filterTaxonomyByInclusionType(List<GeocodeSystem> ts, Principal principal, DescendantInclusionType inclusionType)
	{
		if(inclusionType == DescendantInclusionType.ALL)
			return ts;
		
		List<GeocodeSystem> res = new ArrayList<GeocodeSystem>();
		for(GeocodeSystem t : ts)
		{
			switch(inclusionType)
			{
			case ALL:
				res.add(t);
				break;
			case EXCLUDE_USER_TAXONOMIES:
				if(!t.getIsUserTaxonomy())
					res.add(t);
				break;
			case INCLUDE_TAXONOMIES_OF_USER:
				if(!t.getIsUserTaxonomy() || (principal.getId().equals(t.getCreator().getId())))
					res.add(t);
				break;
			case INCLUDE_TAXONOMIES_OF_CUSTOMER:
				if(!t.getIsUserTaxonomy() || (principal.getId().equals(t.getCreator().getId()) || 
						(principal.getTenant() != null && t.getCreator().getTenant() != null && principal.getTenant().getId() == t.getCreator().getTenant().getId())))
					res.add(t);
				break;
			}
		}
		return res;
	}
	
	//TODO part of this logic should perhaps be migrated to TaxonomyManager
	private List<GeocodeSystem> doRetrieveTaxonomiesByClass(TaxonomyConfig.Type taxonomyConfigClassType, boolean loadDetails) throws Exception
	{
		List<String> baseTaxonomyIds = null;
		synchronized(systemGlobalConfigLock)
		{
			if(systemGlobalConfig == null) retrieveSystemGlobalConfig();
		
			baseTaxonomyIds = systemGlobalConfig.getByTaxonomyTag(taxonomyConfigClassType.toString());
		}
		
		if(baseTaxonomyIds == null || baseTaxonomyIds.isEmpty()) return new ArrayList<GeocodeSystem>();
		List<GeocodeSystem> baseTaxonomies = new ArrayList<GeocodeSystem>();
		for(String bti : baseTaxonomyIds)
			baseTaxonomies.add(geocodeSystemDao.read(UUID.fromString(bti)));
		
		List<GeocodeSystem> instances = new ArrayList<GeocodeSystem>();
		for(GeocodeSystem t : baseTaxonomies){
//			instances.addAll(taxonomyDao.getInstances(t));
			instances.addAll(geocodeSystemDao.getInstancesByID(t.getId()));
		}
		if(loadDetails)
		{
			for(GeocodeSystem instance : instances)
			{
				instance.getCreator().getTenant();
				instance.getExtraData();
				instance.getTaxonomyClass();
			}
		}
		return instances;
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyConfig> retrieveTaxonomyConfigByClass(TaxonomyConfig.Type taxonomyConfigClassType) throws Exception
	{
		return retrieveTaxonomyConfigByClass(taxonomyConfigClassType, false);
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyConfig> retrieveTaxonomyConfigByClass(TaxonomyConfig.Type taxonomyConfigClassType, boolean translateId) throws Exception
	{
		List<TaxonomyConfig> res = new ArrayList<TaxonomyConfig>();
		List<GeocodeSystem> instances = doRetrieveTaxonomiesByClass(taxonomyConfigClassType, false);
		
		//non-atomic because of doRetrieveTaxonomiesByClass, but suits the purpose
		synchronized(systemGlobalConfig)
		{
			for(GeocodeSystem i : instances)
			{
				TaxonomyConfig tcfg = systemGlobalConfig.getByTaxonomyId(i.getId().toString());
				
				if(tcfg != null)
				{
					if(translateId)
						tcfg.setId(geocodeSystemDao.read(UUID.fromString(tcfg.getId())).getName());
					res.add(tcfg);
				}
			}
		}
		
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<GeocodeSystem> retrieveTaxonomiesByClass(TaxonomyConfig.Type taxonomyConfigClassType, Principal principal, DescendantInclusionType inclusionType, boolean loadDetails) throws Exception {
		List<GeocodeSystem> instances = doRetrieveTaxonomiesByClass(taxonomyConfigClassType, loadDetails);
		return filterTaxonomyByInclusionType(instances, principal, inclusionType);
	}
	
	@Transactional(readOnly = true)
	public List<GeocodeSystem> retrieveTaxonomiesByClass(TaxonomyConfig.Type taxonomyConfigClassType, Principal principal, DescendantInclusionType inclusionType) throws Exception {
		return retrieveTaxonomiesByClass(taxonomyConfigClassType, principal, inclusionType, false);
	}
	
	@Transactional(readOnly = true)
	public List<GeocodeSystem> retrieveTaxonomiesByClass(TaxonomyConfig.Type taxonomyConfigClassType, boolean loadDetails) throws Exception
	{
		return retrieveTaxonomiesByClass(taxonomyConfigClassType, null, DescendantInclusionType.ALL, loadDetails);
	}
	
	@Transactional(readOnly = true)
	public List<GeocodeSystem> retrieveTaxonomiesByClass(TaxonomyConfig.Type taxonomyConfigClassType) throws Exception
	{
		return retrieveTaxonomiesByClass(taxonomyConfigClassType, null, DescendantInclusionType.ALL, false);
	}
	
	@Transactional(readOnly = true)
	public TaxonomyConfig retrieveTaxonomyConfigById(String id, boolean translateId) throws Exception
	{
		TaxonomyConfig tcfg = null;
		synchronized(systemGlobalConfig)
		{
			if(systemGlobalConfig == null) retrieveSystemGlobalConfig();
		
			tcfg = systemGlobalConfig.getByTaxonomyId(id);
		}
		if(tcfg == null) return null;
		if(translateId)
			tcfg.setId(geocodeSystemDao.read(UUID.fromString(tcfg.getId())).getName());
		return tcfg;
	}
	
	@Transactional(readOnly = true)
	public TaxonomyConfig retrieveTaxonomyConfigById(String id) throws Exception
	{
		return retrieveTaxonomyConfigById(id, false);
	}
	
	private void retrieveSystemGlobalConfig() throws Exception
	{
		List<SysConfig> configs = sysConfigDao.findByClass(SysConfig.SysConfigClass.GLOBALCONFIG.configClassCode());
		if(configs == null || configs.isEmpty()) throw new Exception("Could not retrieve system global configuration");
		if(configs.size() != 1) throw new Exception("Non-unique system global configuration");
		SysConfig config = configs.get(0);
		
		JAXBContext ctx = JAXBContext.newInstance(SystemGlobalConfig.class);
		Unmarshaller um = ctx.createUnmarshaller();
		systemGlobalConfig = (SystemGlobalConfig)um.unmarshal(new StringReader(config.getConfig()));
	}
	
	private void retrieveSystemLayerConfig() throws Exception
	{
		List<SysConfig> cfgs = sysConfigDao.findByClass(SysConfigClass.LAYERCONFIG.configClassCode());
		if(cfgs == null || cfgs.isEmpty()) throw new Exception("Could not retrieve system layer config");
		if(cfgs.size() != 1) throw new Exception("Non-unique system layer config");
		SysConfig cfg = cfgs.get(0);
		
		JAXBContext ctx = JAXBContext.newInstance(SystemLayerConfig.class);
		Unmarshaller um = ctx.createUnmarshaller();
		systemLayerConfig = (SystemLayerConfig)um.unmarshal(new StringReader(cfg.getConfig()));
	}
	
	private void retrieveSystemAttributeMappingConfig() throws Exception
	{
		List<SysConfig> cfgs = sysConfigDao.findByClass(SysConfigClass.ATTRIBUTEMAPPING.configClassCode());
		if(cfgs == null || cfgs.isEmpty()) throw new Exception("Could not retrieve system mapping config");
		if(cfgs.size() != 1) throw new Exception("Non-unique system mapping config");
		SysConfig cfg = cfgs.get(0);
		
		JAXBContext ctx = JAXBContext.newInstance(SystemMappingConfig.class);
		Unmarshaller um = ctx.createUnmarshaller();
		systemMappingConfig = (SystemMappingConfig)um.unmarshal(new StringReader(cfg.getConfig()));
	}
	
	private void retrieveSystemPresentationConfig() throws Exception
	{
		List<SysConfig> cfgs = sysConfigDao.findByClass(SysConfigClass.PRESENTATION.configClassCode());
		if(cfgs == null || cfgs.isEmpty()) throw new Exception("Could not retrieve system mapping config");
		if(cfgs.size() != 1) throw new Exception("Non-unique system mapping config");
		SysConfig cfg = cfgs.get(0);
		
		systemPresentationConfig = PresentationConfigXMLHandler.unmarshal(cfg.getConfig());
	}
	
	private void updateSystemGlobalConfig() throws Exception
	{
		List<SysConfig> cfgs = sysConfigDao.findByClass(SysConfigClass.GLOBALCONFIG.configClassCode());
		if(cfgs == null || cfgs.isEmpty()) throw new Exception("Could not retrieve system global config");
		if(cfgs.size() != 1) throw new Exception("Non-unique system global config");
		
		SysConfig cfg = cfgs.get(0);
		
		JAXBContext ctx = JAXBContext.newInstance(SystemGlobalConfig.class);
		Marshaller m = ctx.createMarshaller();
		StringWriter sw = new StringWriter();
		m.marshal(systemGlobalConfig, sw);
		
		cfg.setConfig(sw.toString());
		sysConfigDao.update(cfg);
	}
	
	private void updateSystemLayerConfig() throws Exception
	{
		List<SysConfig> cfgs = sysConfigDao.findByClass(SysConfigClass.LAYERCONFIG.configClassCode());
		if(cfgs == null || cfgs.isEmpty()) throw new Exception("Could not retrieve system layer config");
		if(cfgs.size() != 1) throw new Exception("Non-unique system layer config");
		
		SysConfig cfg = cfgs.get(0);
		
		JAXBContext ctx = JAXBContext.newInstance(SystemLayerConfig.class);
		Marshaller m = ctx.createMarshaller();
		StringWriter sw = new StringWriter();
		m.marshal(systemLayerConfig, sw);
		
		cfg.setConfig(sw.toString());
		sysConfigDao.update(cfg);
	}
	
	private void updateSystemMappingConfig() throws Exception
	{
		List<SysConfig> cfgs = sysConfigDao.findByClass(SysConfigClass.ATTRIBUTEMAPPING.configClassCode());
		if(cfgs == null || cfgs.isEmpty()) throw new Exception("Could not retrieve system mapping config");
		if(cfgs.size() != 1) throw new Exception("Non-unique system mapping config");
		
		SysConfig cfg = cfgs.get(0);
		
		JAXBContext ctx = JAXBContext.newInstance(SystemMappingConfig.class);
		Marshaller m = ctx.createMarshaller();
		StringWriter sw = new StringWriter();
		m.marshal(systemMappingConfig, sw);
		
		cfg.setConfig(sw.toString());
		sysConfigDao.update(cfg);
	}
	
	private void updateSystemPresentationConfig() throws Exception
	{
		List<SysConfig> cfgs = sysConfigDao.findByClass(SysConfigClass.PRESENTATION.configClassCode());
		if(cfgs == null || cfgs.isEmpty()) throw new Exception("Could not retrieve system mapping config");
		if(cfgs.size() != 1) throw new Exception("Non-unique system mapping config");
		
		SysConfig cfg = cfgs.get(0);
		
		String pc = PresentationConfigXMLHandler.marshal(systemPresentationConfig);
		cfg.setConfig(pc);
		sysConfigDao.update(cfg);
	}
	
	@Transactional(readOnly=true)
	public List<SysConfig> getSystemConfigsByClass(SysConfigClass sysConfigClass) {
		return sysConfigDao.findByClass(sysConfigClass.configClassCode());
	}
	
	@Transactional(readOnly=true)
	public List<LayerConfig> getLayerConfig() throws Exception
	{
		List<LayerConfig> res = new ArrayList<>();
		synchronized(systemLayerConfigLock)
		{
			if(systemLayerConfig == null) retrieveSystemLayerConfig();
			
			for(LayerConfig lc : systemLayerConfig.getLayerConfigs())
				res.add(new LayerConfig(lc));
		}
		return res;
	}
	
//	public String getGeoserverUrl() throws Exception {
//		return configuration.getGeoServerBridgeConfig().getGeoServerBridgeUrl();
//	}
//	
//	public String getLayerWorkspace() throws Exception {
//		return configuration.getGeoServerBridgeConfig().getGeoServerBridgeWorkspace();
//	}
//	
//	public String getLayerDatastore() throws Exception {
//		return configuration.getGeoServerBridgeConfig().getDataStoreConfig().getDataStoreName();
//	}
	
	@Transactional(readOnly=true)
	public LayerConfig getLayerConfig(UUID layerID) throws Exception
	{
		LayerConfig lcfg = null;
		synchronized(systemLayerConfigLock)
		{
			if(systemLayerConfig == null) retrieveSystemLayerConfig();
			lcfg = systemLayerConfig.getLayerConfig(layerID.toString());
			if(lcfg != null) lcfg = new LayerConfig(lcfg);
		}
		return lcfg;
	}	
	
	private void updateLayerConfig(LayerConfig lcfg, boolean create) throws Exception
	{
		if(systemLayerConfig == null) retrieveSystemLayerConfig();
		
		if(!create && systemLayerConfig.getLayerConfig(lcfg.getLayerId()) == null) 
			throw new Exception("Layer configuration for term " + lcfg.getLayerId() + " does not exist");
		else if(create && systemLayerConfig.getLayerConfig(lcfg.getLayerId()) != null) 
			throw new Exception("Layer configuration for term " + lcfg.getLayerId() + " already exists");
		
		systemLayerConfig.setLayerConfig(lcfg);
		updateSystemLayerConfig();
	}
	
	@Transactional
	public void updateLayerConfig(LayerConfig lcfg) throws Exception
	{
		synchronized(systemLayerConfigLock)
		{
			updateLayerConfig(lcfg, false);
		}
	}
	
	@Transactional
	public void addLayerConfig(LayerConfig lcfg) throws Exception
	{
		synchronized(systemLayerConfigLock)
		{
			updateLayerConfig(lcfg, true);
		}
	}
	
	@Transactional
	public void removeLayerConfig(UUID layerID) throws Exception
	{
		LayerConfig lcfg = null;
		synchronized(systemLayerConfigLock)
		{
			if(systemLayerConfig == null) retrieveSystemLayerConfig();
			
			lcfg = systemLayerConfig.getLayerConfig(layerID.toString());
			if(lcfg == null) throw new Exception("No layer configuration found for layerID " + layerID);
			
			systemLayerConfig.removeLayerConfig(layerID.toString());
			updateSystemLayerConfig();
		}
	}
	
	@Transactional(readOnly=true)
	public List<AttributeMappingConfig> getMappingConfig() throws Exception
	{
		List<AttributeMappingConfig> res = new ArrayList<AttributeMappingConfig>();
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			
			for(AttributeMappingConfig mc : systemMappingConfig.getMappingConfigs())
				res.add(new AttributeMappingConfig(mc));
		}
		return res;
	}
	
	@Transactional(readOnly=true)
	public List<AttributeMappingConfig> getMappingConfigs(String attributeName) throws Exception
	{
		List<AttributeMappingConfig> res = null;
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			List<AttributeMappingConfig> mcfg = systemMappingConfig.getMappingConfig(attributeName);
			if(mcfg != null)
			{
				res = new ArrayList<AttributeMappingConfig>();
				for(AttributeMappingConfig m : mcfg)
					res.add(new AttributeMappingConfig(m));
			}
		}
		return res;
	}
	
	@Transactional(readOnly=true)
	public List<AttributeMappingConfig> getMappingConfigsForLayer(String attributeName, String layerTermId) throws Exception
	{
		List<AttributeMappingConfig> res = null;
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			List<AttributeMappingConfig> mcfg = systemMappingConfig.getMappingConfigForLayer(attributeName, layerTermId);
			if(mcfg != null)
			{
				res = new ArrayList<AttributeMappingConfig>();
				for(AttributeMappingConfig m : mcfg)
					res.add(new AttributeMappingConfig(m));
			}
		}
		return res;
	}
	
	@Transactional(readOnly=true)
	public List<AttributeMappingConfig> getMappingConfigsForLayer(String layerTermId) throws Exception
	{
		List<AttributeMappingConfig> res = null;
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			List<AttributeMappingConfig> mcfg = systemMappingConfig.getMappingConfigForLayer(layerTermId);
			if(mcfg != null)
			{
				res = new ArrayList<AttributeMappingConfig>();
				for(AttributeMappingConfig m : mcfg)
					res.add(new AttributeMappingConfig(m));
			}
		}
		return res;
	}
	
	@Transactional(readOnly=true)
	public List<AttributeMappingConfig> getAttributeMappings(String attributeName, String attributeValue) throws Exception
	{
		List<AttributeMappingConfig> res = null;
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			List<AttributeMappingConfig> mcfg = systemMappingConfig.getMappingConfig(attributeName, attributeValue);
			if(mcfg != null)
			{
				res = new ArrayList<AttributeMappingConfig>();
				for(AttributeMappingConfig m : mcfg)
					res.add(new AttributeMappingConfig(m));
			}
		}
		return res;
	}
	
	@Transactional(readOnly=true)
	public AttributeMappingConfig getAttributeMappingForLayer(String attributeName, String attributeValue, String layerTermId) throws Exception
	{
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			AttributeMappingConfig mcfg = systemMappingConfig.getMappingConfigForLayer(attributeName, attributeValue, layerTermId);
			if(mcfg != null)
				return mcfg;
		}
		return null;
	}
	
	@Transactional(readOnly=true)
	public List<AttributeMappingConfig> getAttributeMappingsForTermId(String termId) throws Exception
	{
		List<AttributeMappingConfig> res = null;
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			List<AttributeMappingConfig> mcfg = systemMappingConfig.getMappingConfigForId(termId);
			if(mcfg != null)
			{
				res = new ArrayList<AttributeMappingConfig>();
				for(AttributeMappingConfig m : mcfg)
					res.add(new AttributeMappingConfig(m));
			}
		}
		return res;
	}
	
	public static class AttributeLayerIdPair {
		public Attribute attr = null;
		public String layerId = null;
		
		public AttributeLayerIdPair() { }
		
		public AttributeLayerIdPair(Attribute attr, String layerId) {
			this.attr = attr;
			this.layerId = layerId;
		}
	}
	
	public AttributeLayerIdPair findAttributeByTermId(String termId) throws Exception {
		List<AttributeMappingConfig> mcfgs = getAttributeMappingsForTermId(termId);
		String layerId = null;
		Attribute attr = null;
		for(AttributeMappingConfig mcfg : mcfgs)
		{
			if(mcfg.getAttributeValue() == null)
			{
				if(mcfg.isPresentable() == false)
					throw new Exception("Not a presentable attribute");
				layerId = mcfg.getLayerTermId();
				attr = new Attribute(mcfg.getAttributeName(), mcfg.getAttributeType(), mcfg.getTermId(), null);
				break;
			}
		}
		return new AttributeLayerIdPair(attr, layerId);
	}
	
	@Transactional(readOnly=true)
	public AttributeMappingConfig getMappingConfigsForIdAndLayer(String termId, String layerTermId) throws Exception
	{
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			AttributeMappingConfig mcfg = systemMappingConfig.getMappingConfigForIdAndLayer(termId, layerTermId);
			if(mcfg != null) return new AttributeMappingConfig(mcfg);
		}
		return null;
	}
	
	private void updateMappingConfig(AttributeMappingConfig mcfg, boolean create, boolean strict) throws Exception
	{
		if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
		
		if(!create && strict && systemMappingConfig.getMappingConfig(mcfg.getAttributeName()) == null) 
			throw new Exception("Attribute mapping configuration for term " + mcfg.getAttributeName() + 
					(mcfg.getAttributeValue() != null ? mcfg.getAttributeValue() : "") + " does not exist");
		else if(create && systemMappingConfig.getMappingConfig(mcfg.getAttributeName()) != null) 
			throw new Exception("Attribute mapping configuration for term " + mcfg.getAttributeName() +
					(mcfg.getAttributeValue() != null ? mcfg.getAttributeValue() : "") + " already exists");
		
		systemMappingConfig.setMappingConfig(mcfg);
		updateSystemMappingConfig();
	}
	
	@Transactional
	public void updateMappingConfig(AttributeMappingConfig mcfg) throws Exception
	{
		synchronized(systemMappingConfigLock)
		{
			updateMappingConfig(mcfg, false, false);
		}
	}
	
	@Transactional
	public void removeMappingConfigs(String attributeName) throws Exception
	{
		List<AttributeMappingConfig> mcfg = null;
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			
			mcfg = systemMappingConfig.getMappingConfig(attributeName);
			if(mcfg == null) throw new Exception("No mapping configuration found for attribute " + attributeName);
			
			systemMappingConfig.removeMappingConfig(attributeName);
			updateSystemMappingConfig();
		}
	}
	
	@Transactional
	public void removeMappingConfigs(String attributeName, String attributeValue) throws Exception
	{
		List<AttributeMappingConfig> mcfg = null;
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			
			mcfg = systemMappingConfig.getMappingConfig(attributeName, attributeValue);
			if(mcfg == null) throw new Exception("No mapping configuration found for attribute " + attributeName + " and value " + attributeValue);
			
			systemMappingConfig.removeMappingConfig(attributeName, attributeValue);
			updateSystemMappingConfig();
		}
	}
	
	@Transactional
	public void removeMappingConfigForLayer(String attributeName, String attributeValue, String layerTermId) throws Exception
	{
		AttributeMappingConfig mcfg = null;
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			
			mcfg = systemMappingConfig.getMappingConfigForLayer(attributeName, attributeValue, layerTermId);
			if(mcfg == null) throw new Exception("No mapping configuration found for layer " + layerTermId + " and attribute " + 
													attributeName + " and value " + attributeValue);
			
			systemMappingConfig.removeMappingConfigForLayer(attributeName, attributeValue, layerTermId);
			updateSystemMappingConfig();
		}
	}
	
	@Transactional
	public void removeMappingConfigForLayer(String attributeName, String layerTermId) throws Exception
	{
		List<AttributeMappingConfig> mcfg = null;
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			
			mcfg = systemMappingConfig.getMappingConfigForLayer(attributeName, layerTermId);
			if(mcfg == null) throw new Exception("No mapping configuration found for layer " + layerTermId + " and attribute " + attributeName);
			
			systemMappingConfig.removeMappingConfigForLayer(attributeName, layerTermId);
			updateSystemMappingConfig();
		}
	}
	
	@Transactional
	public void removeMappingConfigForLayer(String layerTermId) throws Exception
	{
		List<AttributeMappingConfig> mcfg = null;
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			
			mcfg = systemMappingConfig.getMappingConfigForLayer(layerTermId);
			if(mcfg == null) throw new Exception("No mapping configuration found for layer " + layerTermId);
			
			systemMappingConfig.removeMappingConfigForLayer(layerTermId);
			updateSystemMappingConfig();
		}
	}
	
	@Transactional
	public void addMappingConfig(AttributeMappingConfig mcfg) throws Exception
	{
		synchronized(systemMappingConfigLock)
		{
			if(systemMappingConfig == null) retrieveSystemAttributeMappingConfig();
			
			updateMappingConfig(mcfg, true, true);
		}
	}
	
	@Transactional(readOnly=true)
	public Map<String, String> getLayerStyles() throws Exception
	{
		Map<String, String> styles = new HashMap<String, String>();
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			List<LayerStyle> sts =systemPresentationConfig.getLayerStyles();
			for(LayerStyle st : sts)
				styles.put(st.getName(), st.getStyle());
		}
		return styles;
	}
	
	@Transactional(readOnly=true)
	public SystemPresentationConfig getSystemPresentationConfig() throws Exception {
		synchronized(systemPresentationConfigLock) {
			
			if(systemPresentationConfig == null)
				retrieveSystemPresentationConfig();
			
			return systemPresentationConfig;
		}
	}
	
	@Transactional(readOnly=true)
	public String getLayerStyle(String name) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			LayerStyle s = systemPresentationConfig.getLayerStyle(name);
			if(s == null) return null;
			return s.getStyle();
		}
	}
	
	@Transactional(readOnly=true)
	public List<String> listLayerStyles() throws Exception
	{
		List<String> names = new ArrayList<String>();
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			for(LayerStyle s : systemPresentationConfig.getLayerStyles())
				names.add(s.getName());
		}
		return names;
	}
	
	@Transactional
	public void addLayerStyle(String name, String style) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			LayerStyle ls = new LayerStyle();
			ls.setName(name);
			ls.setStyle(style);
			systemPresentationConfig.addLayerStyle(ls);
			updateSystemPresentationConfig();
		}
	}
	
	@Transactional
	public void updateLayerStyle(String name, String style) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			LayerStyle ls = new LayerStyle();
			ls.setName(name);
			ls.setStyle(style);
			systemPresentationConfig.updateLayerStyle(ls);
			updateSystemPresentationConfig();
		}
	}
	
	@Transactional
	public void removeLayerStyles(List<String> names) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			for(String name : names)
			{	
				if(!name.equals(SystemPresentationConfig.DEFAULT_STYLE))
					systemPresentationConfig.removeLayerStyle(name);
			}
			updateSystemPresentationConfig();
		}
	}
	
	@Transactional(readOnly=true)
	public List<String> listThemes() throws Exception
	{
		List<String> names = new ArrayList<String>();
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			for(Theme t : systemPresentationConfig.getThemes())
				names.add(t.getTitle());
		}
		return names;
	}
	
	@Transactional
	public void addTheme(Theme theme, String template) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			theme.setGeoStyle(null); //prevent addition of preconfigured themes; styles are associated with terms only
										//via the addTermStyle method
			systemPresentationConfig.addTheme(theme);
			if(template != null)
			{
				Theme t = systemPresentationConfig.getTheme(template);
				if(t == null) throw new Exception("Could not find template theme: " + template);
				GeoStyle gs = new GeoStyle();
				List<TermStyle> tss = new ArrayList<TermStyle>();
				for(TermStyle tts : t.getGeoStyle().getTermStyles())
				{
					TermStyle ts = new TermStyle();
					ts.setId(tts.getId());
					ts.setStyle(tts.getStyle());
					tss.add(ts);
				}
				gs.setTermStyles(tss);
				theme.setGeoStyle(gs);
			}
			updateSystemPresentationConfig();
		}
	}
	
	@Transactional
	public void addTheme(Theme theme) throws Exception
	{
		addTheme(theme, null);
	}
	
	@Transactional
	public void removeThemes(List<String> names) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			for(String name : names)
			{
				if(!name.equals(SystemPresentationConfig.DEFAULT_THEME))
					systemPresentationConfig.removeTheme(name);
			}
			updateSystemPresentationConfig();
		}
	}
	
	@Transactional(readOnly = true)
	public String getDefaultTermStyle(String termId) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			return systemPresentationConfig.getTermStyle(termId);
		}
	}
	
	@Transactional(readOnly = true)
	public String getTermStyle(String themeName, String termId) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			return systemPresentationConfig.getTermStyle(themeName, termId);
		}
	}
	
	@Transactional(readOnly = true)
	public List<LayerStyleMessenger> getLayersReferencingStyle(String style) throws Exception
	{
		List<LayerStyleMessenger> lsms = new ArrayList<LayerStyleMessenger>();
		
		synchronized(systemPresentationConfigLock)
		{
			if(systemLayerConfig == null) retrieveSystemPresentationConfig();
			for(LayerConfig cfg : getLayerConfig())
			{
				for(Theme theme : systemPresentationConfig.getThemes())
				{
					if(theme.getTitle().equals(SystemPresentationConfig.DEFAULT_THEME)) continue;
					String ts = systemPresentationConfig.getTermStyle(theme.getTitle(), cfg.getLayerId());
					if(ts != null && ts.equals(style))
					{
						LayerStyleMessenger lsm = new LayerStyleMessenger();
						lsm.setLayerName(cfg.getName());
						lsm.setMinScale(cfg.getMinScale());
						lsm.setMaxScale(cfg.getMaxScale());
						lsm.setTermId(cfg.getLayerId());
						lsm.setTheme(theme.getTitle());
						lsms.add(lsm);
						break;
					}
				}
			}
		}
		return lsms;
	}
	
	@Transactional(readOnly = true)
	public List<LayerStyleMessenger> getLayersReferencingDefaultStyle(String style) throws Exception
	{
		List<LayerStyleMessenger> lsms = new ArrayList<LayerStyleMessenger>();
		
		synchronized(systemPresentationConfigLock)
		{
			if(systemLayerConfig == null) retrieveSystemPresentationConfig();
			for(LayerConfig cfg : getLayerConfig())
			{
				String ts = systemPresentationConfig.getTermStyle(cfg.getLayerId());
				if(ts != null && ts.equals(style))
				{
					LayerStyleMessenger lsm = new LayerStyleMessenger();
					lsm.setLayerName(cfg.getName());
					lsm.setMinScale(cfg.getMinScale());
					lsm.setMaxScale(cfg.getMaxScale());
					lsm.setTermId(cfg.getLayerId());
					lsm.setTheme(SystemPresentationConfig.DEFAULT_THEME);
					lsms.add(lsm);
				}
			}
		}
		return lsms;
	}
	
	@Transactional
	public void addDefaultTermStyle(String termId, String styleRef) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			systemPresentationConfig.assignTermStyle(termId, styleRef);
			updateSystemPresentationConfig();
		}
	}
	
	@Transactional
	public void addTermStyle(String themeName, String termId, String styleRef) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			systemPresentationConfig.assignTermStyle(themeName, termId, styleRef);
			updateSystemPresentationConfig();
		}
	}
	
	@Transactional 
	public void removeDefaultTermStyle(String termId) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			systemPresentationConfig.removeTermStyle(termId);
			updateSystemPresentationConfig();
		}
	}
	
	@Transactional 
	public void removeTermStyle(String themeName, String termId) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			systemPresentationConfig.removeTermStyle(themeName, termId);
			updateSystemPresentationConfig();
		}
	}
	
	@Transactional 
	public void removeTermStyles(String termId) throws Exception
	{
		synchronized(systemPresentationConfigLock)
		{
			if(systemPresentationConfig == null) retrieveSystemPresentationConfig();
			
			systemPresentationConfig.removeTermStyles(termId);
			updateSystemPresentationConfig();
		}
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void updateTermStyle(String theme, String termId, String style) throws Exception
	{
		if(theme == null || theme.equals(SystemPresentationConfig.DEFAULT_THEME))
			addDefaultTermStyle(termId, style);
		else
			addTermStyle(theme, termId, style);
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void removeLayerConfig(Layer layer) throws Exception{
		try {
			this.removeLayerConfig(layer.getId());
			this.removeMappingConfigForLayer(layer.getId().toString());
			this.removeTermStyles(layer.getId().toString());
		} catch (Exception e) {
			throw new Exception("Error while removing configuration of layer [ " + layer.getId() + " ]", e);
		}	
	}
}
