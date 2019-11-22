/**
 * 
 */
package org.gcube.portlets.user.tdw.server.util;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.tdw.server.datasource.DataSource;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceException;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceFactory;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceFactoryRegistry;
import org.gcube.portlets.user.tdw.server.session.TDSession;
import org.gcube.portlets.user.tdw.server.session.TDSessionList;
import org.gcube.portlets.user.tdw.shared.Constants;
import org.gcube.portlets.user.tdw.shared.model.TableId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SessionUtil {
	

	private static final Logger log = LoggerFactory.getLogger(SessionUtil.class);
	
	public static final String TD_SESSIONS_ATTRIBUTE_NAME = "TDW.SESSIONS";
	
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
	
	public static DataSource getDataSource(HttpSession httpSession, int tdSessionId)
	{
		ASLSession session = getSession(httpSession);
		TDSession tdSession = getSession(session, tdSessionId);
		return tdSession.getDataSource();
	}
	
	public static void setDataSource(HttpSession httpSession, int tdSessionId, DataSource datasource)
	{
		ASLSession session = getSession(httpSession);
		TDSession tdSession = getSession(session, tdSessionId);
		tdSession.setDataSource(datasource);
		log.trace("datasource "+datasource+" set in session "+tdSessionId);
	}
	
	protected static TDSession getSession(ASLSession session, int tdSessionId)
	{
		//workaround to TDSession object loaded from different class loader
		Object tsSession = session.getAttribute(TD_SESSIONS_ATTRIBUTE_NAME);
		TDSessionList sessions = (tsSession instanceof TDSessionList)?((TDSessionList) tsSession):null;
		if (sessions == null) {
			sessions = new TDSessionList();
			session.setAttribute(TD_SESSIONS_ATTRIBUTE_NAME, sessions);
		}
		if (sessions.get(tdSessionId)==null) {
			sessions.set(tdSessionId, new TDSession(tdSessionId));
			log.trace("created new sessions "+tdSessionId);
		}
		return sessions.get(tdSessionId);
	}
	
	public static DataSource openDataSource(HttpSession httpSession, TableId tableId) throws DataSourceException
	{
		DataSourceFactoryRegistry dataSourceFactoryRegistry = DataSourceFactoryRegistry.getInstance();
		DataSourceFactory factory = dataSourceFactoryRegistry.get(tableId.getDataSourceFactoryId());
		if (factory==null) throw new DataSourceException("DataSourceFactory with id "+tableId.getDataSourceFactoryId()+" don't exists");
		ASLSession session = getSession(httpSession);
		return factory.openDataSource(session, tableId);
	}
	
	public static void closeDataSource(HttpSession httpSession, int tdSessionId) throws DataSourceException
	{
		DataSource currentDataSource = getDataSource(httpSession, tdSessionId);
		if (currentDataSource != null) {
			DataSourceFactoryRegistry dataSourceFactoryRegistry = DataSourceFactoryRegistry.getInstance();
			DataSourceFactory factory = dataSourceFactoryRegistry.get(currentDataSource.getDataSourceFactoryId());
			ASLSession session = getSession(httpSession);
			factory.closeDataSource(session, currentDataSource);
		}
	}

}
