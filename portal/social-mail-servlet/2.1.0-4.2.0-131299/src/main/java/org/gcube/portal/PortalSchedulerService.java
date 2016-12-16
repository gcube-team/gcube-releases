package org.gcube.portal;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.notifications.thread.LikeNotificationsThread;
import org.gcube.portal.socialmail.EmailPopAccount;
import org.gcube.portal.socialmail.PeriodicTask;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.CustomAttributeKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.VirtualHost;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.VirtualHostLocalServiceUtil;


@SuppressWarnings("serial")
public class PortalSchedulerService extends HttpServlet {

	private static final Logger _log = LoggerFactory.getLogger(PortalSchedulerService.class);

	private  static final String POP3_SERVER_NAME = "Pop3MailServer";

	private static DatabookStore store;

	public void init() {
		store = new DBCassandraAstyanaxImpl();		
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Group site;
		EmailPopAccount popAccount = null;
		try {
			site = getSiteFromServletRequest(request);
			popAccount = getPopAccountData(site);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String toReturn = "<DIV>Check Notification Email Starting ... </DIV>";
		Thread likesThread = new Thread(new PeriodicTask(store, popAccount, request));
		likesThread.start();			
		toReturn = "<DIV>Check Notification Email Started ... </DIV>";

		response.setContentType("text/html");		
		response.getWriter().write(toReturn); 
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

	/**
	 * 
	 * @param request
	 * @return the current Group instance based on the request
	 * @throws PortalException
	 * @throws SystemException
	 */
	private Group getSiteFromServletRequest(final HttpServletRequest request) throws PortalException, SystemException {
		String serverName = request.getServerName();
		_log.debug("currentHost is " +  serverName);
		Group site = null;
		List<VirtualHost> vHosts = VirtualHostLocalServiceUtil.getVirtualHosts(0, VirtualHostLocalServiceUtil.getVirtualHostsCount());
		for (VirtualHost virtualHost : vHosts) {
			_log.debug("Found  " +  virtualHost.getHostname());
			if (virtualHost.getHostname().compareTo("localhost") != 0 && 
					virtualHost.getLayoutSetId() != 0 && 
					virtualHost.getHostname().compareTo(serverName) == 0) {
				long layoutSetId = virtualHost.getLayoutSetId();
				site = LayoutSetLocalServiceUtil.getLayoutSet(layoutSetId).getGroup();
				_log.debug("Found match! Your site is " +  site.getName());
				return site;
			}
		}
		return null;
	}

	private EmailPopAccount getPopAccountData(Group site) throws GroupRetrievalFault {

		_log.debug("Found site for vhost, name " +  site.getName() + " reading custom field: " + CustomAttributeKeys.GATEWAY_SITE_NAME);
		String gatewayName = (String) new LiferayGroupManager().readCustomAttr(site.getGroupId(), CustomAttributeKeys.GATEWAY_SITE_NAME.getKeyName());

		//set the scope for the query
		String curScope = 	ScopeProvider.instance.get();
		PortalContext context = PortalContext.getConfiguration();	
		String scope2Set = "/" + context.getInfrastructureName();
		ScopeProvider.instance.set(scope2Set);

		EmailPopAccount toReturn = new EmailPopAccount();
		toReturn.setPortalName(gatewayName);
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq 'Portal'");
		query.addCondition("$resource/Profile/Name/text() eq '" + gatewayName + "'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> list = client.submit(query);
		if (list == null || list.isEmpty()) {
			_log.error("Could not find any Service endpoint registred in the infrastructure for this gateway: " + gatewayName);
			return null;
		}
		else  if (list.size() > 1) {
			_log.warn("Found more than one Service endpoint registred in the infrastructure for this gateway: " + gatewayName);
			return null;
		}
		else {
			for (ServiceEndpoint res : list) {
				org.gcube.common.resources.gcore.utils.Group<AccessPoint> apGroup =  res.profile().accessPoints();
				AccessPoint[] accessPoints = (AccessPoint[]) apGroup.toArray(new AccessPoint[apGroup.size()]);
				for (int i = 0; i < accessPoints.length; i++) {
					if (accessPoints[i].name().compareTo(POP3_SERVER_NAME) == 0) {
						_log.info("Found credentials for " + POP3_SERVER_NAME);
						AccessPoint found = accessPoints[i];
						toReturn.setPop3Server(found.address());
						toReturn.setPop3user(found.username());
						String encrPassword = found.password();						
						try {
							toReturn.setPop3password(StringEncrypter.getEncrypter().decrypt( encrPassword));
						} catch (Exception e) {
							_log.error("Something went wrong while decrypting password for " + POP3_SERVER_NAME);
							e.printStackTrace();
						}
					}
				}
			}
		}
		//set the previous scope
		ScopeProvider.instance.set(curScope);
		return toReturn;
	}


	
}