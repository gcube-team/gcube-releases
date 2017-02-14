/**
 * 
 */
package org.gcube.portlets.user.tdwx.server.util;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceX;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXException;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXFactory;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXFactoryRegistry;
import org.gcube.portlets.user.tdwx.server.session.TDSession;
import org.gcube.portlets.user.tdwx.server.session.TDSessionList;
import org.gcube.portlets.user.tdwx.shared.Constants;
import org.gcube.portlets.user.tdwx.shared.model.TableId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SessionUtil {
	

	private static final Logger log = LoggerFactory.getLogger(SessionUtil.class);
	
	public static final String TDWX_SESSIONS_ATTRIBUTE_NAME = "TDWX.SESSIONS";
	
	protected static ASLSession getSession(HttpSession httpSession)
	{
		String username = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession session;
		if (username == null) {
			log.warn("no user found in session, using test one");
			username = Constants.DEFAULT_USER;
			String scope = Constants.DEFAULT_SCOPE;

			httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, username);
			session = SessionManager.getInstance().getASLSession(
					httpSession.getId(), username);
			session.setScope(scope);

		} else {
			session= SessionManager.getInstance().getASLSession(
					httpSession.getId(), username);
			
		}
		
		return session;
	}
	
	public static DataSourceX getDataSource(HttpSession httpSession, int tdSessionId)
	{
		ASLSession session = getSession(httpSession);
		TDSession tdSession = getSession(session, tdSessionId);
		return tdSession.getDataSource();
	}
	
	public static void setDataSource(HttpSession httpSession, int tdSessionId, DataSourceX datasource)
	{
		ASLSession session = getSession(httpSession);
		TDSession tdSession = getSession(session, tdSessionId);
		tdSession.setDataSource(datasource);
		log.trace("datasource "+datasource+" set in session "+tdSessionId);
	}
	
	protected static TDSession getSession(ASLSession session, int tdSessionId)
	{
		//workaround to TDSession object loaded from different class loader
		Object tsSession = session.getAttribute(TDWX_SESSIONS_ATTRIBUTE_NAME);
		TDSessionList sessions = (tsSession instanceof TDSessionList)?((TDSessionList) tsSession):null;
		if (sessions == null) {
			sessions = new TDSessionList();
			session.setAttribute(TDWX_SESSIONS_ATTRIBUTE_NAME, sessions);
		}
		if (sessions.get(tdSessionId)==null) {
			sessions.set(tdSessionId, new TDSession(tdSessionId));
			log.trace("created new sessions "+tdSessionId);
		}
		return sessions.get(tdSessionId);
	}
	
	public static DataSourceX openDataSource(HttpSession httpSession, TableId tableId) throws DataSourceXException
	{
		DataSourceXFactoryRegistry dataSourceFactoryRegistry = DataSourceXFactoryRegistry.getInstance();
		DataSourceXFactory factory = dataSourceFactoryRegistry.get(tableId.getDataSourceFactoryId());
		if (factory==null) throw new DataSourceXException("DataSourceFactory with id "+tableId.getDataSourceFactoryId()+" don't exists");
		ASLSession session = getSession(httpSession);
		return factory.openDataSource(session, tableId);
	}
	
	public static void closeDataSource(HttpSession httpSession, int tdSessionId) throws DataSourceXException
	{
		DataSourceX currentDataSource = getDataSource(httpSession, tdSessionId);
		if (currentDataSource != null) {
			DataSourceXFactoryRegistry dataSourceFactoryRegistry = DataSourceXFactoryRegistry.getInstance();
			DataSourceXFactory factory = dataSourceFactoryRegistry.get(currentDataSource.getDataSourceFactoryId());
			ASLSession session = getSession(httpSession);
			factory.closeDataSource(session, currentDataSource);
		}
	}

}
