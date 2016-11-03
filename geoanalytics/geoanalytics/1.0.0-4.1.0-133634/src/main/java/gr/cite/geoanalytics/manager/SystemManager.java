package gr.cite.geoanalytics.manager;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.cite.geoanalytics.notifications.event.SystemShutDownEvent;
import gr.cite.geoanalytics.context.ApplicationConfig;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig.SysConfigClass;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.dao.SysConfigDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.SystemGlobalConfig;
import gr.cite.geoanalytics.notifications.NotificationManager;

@Service
public class SystemManager {
	
	private Configuration configuration = null;
	private NotificationManager notificationManager = null;
	private SessionRegistry sessionRegistry = null;
	private PrincipalDao principalDao = null;
	
	private SysConfigDao sysConfigDao;
	
	private Object systemGlobalConfigLock = new Object();
	
	private List<SystemStatusListener> listeners = new ArrayList<SystemStatusListener>();
	
	private SystemGlobalConfig systemGlobalConfig = null;
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}
	
	@Inject
	public void setSysConfigDao(SysConfigDao sysConfigDao) {
		this.sysConfigDao = sysConfigDao;
	}
	
	@Inject
	public void setNotificationManager(NotificationManager notificationManager) {
		this.notificationManager = notificationManager;
	}
	
	@Inject
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	@Inject
	public void setSessionRegistry(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}
	
	private void updateOnlineStatus(boolean value) throws Exception {
		synchronized(systemGlobalConfigLock) {
			if(systemGlobalConfig == null) retrieveSystemGlobalConfig();
			
			systemGlobalConfig.setSystemOnline(value);
			updateSystemGlobalConfig();
		}
	}
	
	public interface SystemStatusListener {
		public void onStatusChange();
	}
	
	private void retrieveSystemGlobalConfig() throws Exception {
		List<SysConfig> configs = sysConfigDao.findByClass(SysConfig.SysConfigClass.GLOBALCONFIG.configClassCode());
		if(configs == null || configs.isEmpty()) throw new Exception("Could not retrieve system global configuration");
		if(configs.size() != 1) throw new Exception("Non-unique system global configuration");
		SysConfig config = configs.get(0);
		
		JAXBContext ctx = JAXBContext.newInstance(SystemGlobalConfig.class);
		Unmarshaller um = ctx.createUnmarshaller();
		systemGlobalConfig = (SystemGlobalConfig)um.unmarshal(new StringReader(config.getConfig()));
	}
	
	private void updateSystemGlobalConfig() throws Exception {
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
	
	@Transactional
	public void bringDownSystem(SystemStatusListener listener) throws Exception {
		synchronized(listeners) {
			listeners.add(listener);
		}
		bringDownSystem();
	}
	
	@Transactional
	public void bringDownSystem() throws Exception {
		ApplicationConfig config = configuration.getApplicationConfig();
		notificationManager.broadcast(new SystemShutDownEvent(
				TimeUnit.MILLISECONDS.convert(config.getServerShutdownDelay(), config.getServerShutdownDelayUnit())));
		config.getServerShutdownDelayUnit().sleep(config.getServerShutdownDelay());
		List<Object> principals = sessionRegistry.getAllPrincipals();
		for(Object p : principals) {
			boolean skip = false;
			for(GrantedAuthority ga : ((org.springframework.security.core.userdetails.User)p).getAuthorities())
			{
				if(ga.getAuthority().equals("ROLE_admin"))
				{
					skip = true;
					break;
				}
			}
			if(skip) continue;
			
			//TODO warning: this replicates the behavior of GeoanalyticsLogoutHandler
			Principal principal = principalDao.findActivePrincipalByName(((org.springframework.security.core.userdetails.User)p).getUsername());
			//notificationManager.unregister(principal.getNotificationId());
			List<SessionInformation> activeSessions = new ArrayList<SessionInformation>();
			for(SessionInformation session : sessionRegistry.getAllSessions(p, false))
		           activeSessions.add(session);
			for(SessionInformation session : activeSessions)
				session.expireNow();
		}
		updateOnlineStatus(false);
		List<SystemStatusListener> remove = new ArrayList<SystemStatusListener>();
		synchronized (listeners)
		{
				
			for(SystemStatusListener l : listeners)
			{
				l.onStatusChange();
				remove.add(l);
			}
			for(SystemStatusListener l : remove)
				listeners.remove(l);
		}
	}

}
