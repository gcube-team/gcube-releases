package org.gcube.portal.ldapexport;

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
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


@SuppressWarnings("serial")
public class LDAPExportService extends HttpServlet {

	private static final Log _log = LogFactoryUtil.getLog(LDAPExportService.class);

	private  static final String LDAP_SERVER_NAME = "LDAPServer";
	private  static final String LDAP_SERVER_FILTER_NAME = "filter";
	private  static final String LDAP_SERVER_PRINCPAL_NAME = "ldapPrincipal";

	private static final int LDAP_MINUTES_DELAY = 10;
	
	private String portalName; 
	private String ldapUrl;
	private String filter; 
	private String principal;
	private String ldapPassword;
	
	public void init() {
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
					if (accessPoints[i].name().compareTo(LDAP_SERVER_NAME) == 0) {
						_log.info("Found credentials for " + LDAP_SERVER_NAME);
						AccessPoint found = accessPoints[i];
						ldapUrl = found.address();
						String encrPassword = found.password();						
						try {
							ldapPassword = StringEncrypter.getEncrypter().decrypt( encrPassword);
						} catch (Exception e) {
							_log.error("Something went wrong while decrypting password for " + LDAP_SERVER_NAME);
							e.printStackTrace();
						}
						Group<Property> propGroup =  found.properties();
						Property[] props = (Property[]) propGroup.toArray(new Property[propGroup.size()]);
						for (int j = 0; j < props.length; j++) {
							if (props[j].name().compareTo(LDAP_SERVER_FILTER_NAME) == 0) {
								_log.info("\tFound properties of " + LDAP_SERVER_FILTER_NAME);
								String encrValue = props[j].value();			
								System.out.println("Filter encrypted = " + encrValue);
								try {
									filter = StringEncrypter.getEncrypter().decrypt(encrValue);
								} catch (Exception e) {
									_log.error("Something went wrong while decrypting value for " + LDAP_SERVER_FILTER_NAME);
									e.printStackTrace();
								}
							}
							else if (props[j].name().compareTo(LDAP_SERVER_PRINCPAL_NAME) == 0) {
								_log.info("\tFound properties of " + LDAP_SERVER_PRINCPAL_NAME);
								String encrValue = props[j].value();						
								try {
									principal = StringEncrypter.getEncrypter().decrypt(encrValue);
								} catch (Exception e) {
									_log.error("Something went wrong while decrypting value for " + LDAP_SERVER_PRINCPAL_NAME);
									e.printStackTrace();
								}
							}
						}
						
					}
				}
				
			}
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ScheduledExecutorService ldapScheduler = Executors.newScheduledThreadPool(1);
		ldapScheduler.scheduleAtFixedRate(new LDAPSync(ldapUrl, filter, principal, ldapPassword), 0, LDAP_MINUTES_DELAY, TimeUnit.MINUTES);
		
		String toReturn =  "<DIV>LDAPSync SCRIPT Started ... </DIV>";
	
		response.setContentType("text/html");		
		response.getWriter().write(toReturn); 
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
}