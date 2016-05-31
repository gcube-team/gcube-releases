package org.gcube.portal;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.socialmail.PeriodicTask;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("serial")
public class PortalSchedulerService extends HttpServlet {

	private static final Logger _log = LoggerFactory.getLogger(PortalSchedulerService.class);

	private  static final String POP3_SERVER_NAME = "Pop3MailServer";
	
	private static final int POP3_MINUTES_DELAY = 1;

	
	private static DatabookStore store;
	
	private String portalName; 
	private String pop3Server; 
	private String pop3user;
	private String pop3password;
	
	
	public void init() {
		store = new DBCassandraAstyanaxImpl();		
		portalName = PortalContext.getPortalInstanceName();
		
		PortalContext context = PortalContext.getConfiguration();	
		String scope = "/" + context.getInfrastructureName();
		ScopeProvider.instance.set(scope);
	
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq 'Portal'");
		query.addCondition("$resource/Profile/Name/text() eq '" + portalName + "'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> list = client.submit(query);
		if (list == null || list.isEmpty()) {
			_log.error("Could not find any Service endpoint registred in the infrastructure for this portal: " + portalName);
		}
		else  if (list.size() > 1) {
			_log.warn("Found more than one Service endpoint registred in the infrastructure for this portal: " + portalName);
		}
		else {
			for (ServiceEndpoint res : list) {
				Group<AccessPoint> apGroup =  res.profile().accessPoints();
				AccessPoint[] accessPoints = (AccessPoint[]) apGroup.toArray(new AccessPoint[apGroup.size()]);
				for (int i = 0; i < accessPoints.length; i++) {
					if (accessPoints[i].name().compareTo(POP3_SERVER_NAME) == 0) {
						_log.info("Found credentials for " + POP3_SERVER_NAME);
						AccessPoint found = accessPoints[i];
						pop3Server = found.address();
						pop3user = found.username();
						String encrPassword = found.password();						
						try {
							pop3password = StringEncrypter.getEncrypter().decrypt( encrPassword);
						} catch (Exception e) {
							_log.error("Something went wrong while decrypting password for " + POP3_SERVER_NAME);
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		ScheduledExecutorService pop3Scheduler = Executors.newScheduledThreadPool(1);
		pop3Scheduler.scheduleAtFixedRate(new PeriodicTask(store, portalName, pop3Server, pop3user, pop3password), 0, POP3_MINUTES_DELAY, TimeUnit.MINUTES);
		
		String toReturn = "<DIV>Check Notification Email Started ... </DIV>";
		
		response.setContentType("text/html");		
		response.getWriter().write(toReturn); 
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
}