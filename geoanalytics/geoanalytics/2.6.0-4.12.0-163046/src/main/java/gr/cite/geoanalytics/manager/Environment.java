package gr.cite.geoanalytics.manager;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.utilities.PresentationConfigXMLHandler;
import gr.cite.geoanalytics.dataaccess.dao.UUIDGenerator;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalData;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDataDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig.SysConfigClass;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.dao.SysConfigDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.SystemGlobalConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.SystemLayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.SystemMappingConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Environment {	
	
	private ConfigurationManager configurationManager;
	private SysConfigDao sysConfigDao;
	private PrincipalDao principalDao;
	private PrincipalDataDao principalDataDao;
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}
	
	@Inject
	public void setSysConfigDao(SysConfigDao sysConfigDao) {
		this.sysConfigDao = sysConfigDao;
	}
	
	@Inject
	public void setPrincipalDataDao(PrincipalDataDao principalDataDao) {
		this.principalDataDao = principalDataDao;
	}
	
	@Inject
	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}
	
	
	/**
	 * TODO: setUp uses direct EntityManager API in order to obtain transaction, 
	 * as Spring context is not yet initialized. Implement app context listener
	 * 
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = true)
	public boolean isSetUp() throws Exception {
		if(principalDao.systemPrincipal() == null) return false;
		
		List<SysConfig> cfg = configurationManager.getSystemConfigsByClass(SysConfig.SysConfigClass.GLOBALCONFIG);
		if(cfg == null || cfg.isEmpty()) return false;
		
		cfg = configurationManager.getSystemConfigsByClass(SysConfig.SysConfigClass.LAYERCONFIG);
		if(cfg == null || cfg.isEmpty()) return false;
		
		cfg = configurationManager.getSystemConfigsByClass(SysConfig.SysConfigClass.PRESENTATION);
		if(cfg == null || cfg.isEmpty()) return false;
		
		cfg = configurationManager.getSystemConfigsByClass(SysConfig.SysConfigClass.ATTRIBUTEMAPPING);
		if(cfg == null || cfg.isEmpty()) return false;
		return true;
	}
	
	private boolean configPresent(SysConfigClass configClass) throws Exception {
			
		List<SysConfig> config = configurationManager.getSystemConfigsByClass(configClass);
		
		if(config == null || config.isEmpty()) return false;
		return true;
	}
	
	private boolean globalConfigPresent() throws Exception {
		return configPresent(SysConfigClass.GLOBALCONFIG);
		//List<SysConfig> global = sysConfigDao.findByClass(SysConfig.SysConfigClass.GLOBALCONFIG.configClassCode());
	}
	
	private boolean layerConfigPresent() throws Exception {
		return configPresent(SysConfigClass.LAYERCONFIG);
	}
	
	private boolean presentationConfigPresent() throws Exception {
		return configPresent(SysConfigClass.PRESENTATION);
	}
	
	private boolean mappingConfigPresent() throws Exception {
		return configPresent(SysConfigClass.ATTRIBUTEMAPPING);
	}
	
	private boolean systemPrincipalPresent() throws Exception {
		if(principalDao.systemPrincipal() != null) return true;
		return false;
	}
	
	@PostConstruct
	@Transactional
	public void setUp() throws Exception {
		try {
			Principal sys = null;
			if(!systemPrincipalPresent()) {
				sys = new Principal();
				PrincipalData sysData = new PrincipalData();
				
				sys.setId(UUIDGenerator.systemUserUUID());
				sysData.setFullName("__System_User__");
				sysData.setInitials("__SU__");
				sysData.setEmail("sys@example.com");
				sys.setCreationDate(Calendar.getInstance().getTime());
				sys.setLastUpdate(Calendar.getInstance().getTime());
				sysData.setExpirationDate(new Date(3000-1900,12,31));
				sys.setName("___System_Usr___");
				sys.setCreator(sys);
				
				principalDataDao.create(sysData);
				sys.setPrincipalData(sysData);
				principalDao.create(sys);
				
				System.out.println("SYSUSR Created");
			}else{
				sys = principalDao.systemPrincipal();
			}
			
			System.out.println(sys.getId().toString());
			
			if(!globalConfigPresent()) {
				SysConfig config = new SysConfig();
				
				SystemGlobalConfig data = new SystemGlobalConfig();
				data.setSystemOnline(true);
				data.setTaxonomyConfig(new ArrayList<TaxonomyConfig>());
				
				JAXBContext ctx = JAXBContext.newInstance(SystemGlobalConfig.class);
				Marshaller m = ctx.createMarshaller();
				StringWriter sw = new StringWriter();
				m.marshal(data, sw);
				System.out.println("Created new global sys config: " + sw.toString());
				
				config.setId(UUIDGenerator.randomUUID());
				config.setConfigClass(SysConfigClass.GLOBALCONFIG);
				config.setConfig(sw.toString());
				config.setCreationDate(Calendar.getInstance().getTime());
				config.setLastUpdate(Calendar.getInstance().getTime());
				config.setCreator(sys);
				
				sysConfigDao.create(config);
			}
			
			if(!layerConfigPresent()) {
				SysConfig config = new SysConfig();
				
				SystemLayerConfig data = new SystemLayerConfig();
				
				JAXBContext ctx = JAXBContext.newInstance(SystemLayerConfig.class);
				Marshaller m = ctx.createMarshaller();
				StringWriter sw = new StringWriter();
				m.marshal(data, sw);
				System.out.println("Created new layer sys config: " + sw.toString());
				
				config.setId(UUIDGenerator.randomUUID());
				config.setConfigClass(SysConfigClass.LAYERCONFIG);
				config.setConfig(sw.toString());
				config.setCreationDate(Calendar.getInstance().getTime());
				config.setLastUpdate(Calendar.getInstance().getTime());
				config.setCreator(sys);
				
				sysConfigDao.create(config);
			}
			
			if(!presentationConfigPresent()) {
				SysConfig config = new SysConfig();
				
				SystemPresentationConfig data = new SystemPresentationConfig();
				data.setupDefaultTheme();
				
				config.setId(UUIDGenerator.randomUUID());
				config.setConfigClass(SysConfigClass.PRESENTATION);
				config.setConfig(PresentationConfigXMLHandler.marshal(data));
				config.setCreationDate(Calendar.getInstance().getTime());
				config.setLastUpdate(Calendar.getInstance().getTime());
				config.setCreator(sys);
				
				sysConfigDao.create(config);
			}
			
			if(!mappingConfigPresent()) {
				SysConfig config = new SysConfig();
				
				SystemMappingConfig data = new SystemMappingConfig();
				
				JAXBContext ctx = JAXBContext.newInstance(SystemMappingConfig.class);
				Marshaller m = ctx.createMarshaller();
				StringWriter sw = new StringWriter();
				m.marshal(data, sw);
				System.out.println("Created new attribute mapping sys config: " + sw.toString());
				
				config.setId(UUIDGenerator.randomUUID());
				config.setConfigClass(SysConfigClass.ATTRIBUTEMAPPING);
				config.setConfig(sw.toString());
				config.setCreationDate(Calendar.getInstance().getTime());
				config.setLastUpdate(Calendar.getInstance().getTime());
				config.setCreator(sys);
				
				sysConfigDao.create(config);
			}

		}catch(Exception e) {
			e.printStackTrace(System.out);
			throw e;
		}
	}
	
	public static void main(String[] args) throws Exception {
		Environment env = new Environment();
		env.setUp();
	}
}
