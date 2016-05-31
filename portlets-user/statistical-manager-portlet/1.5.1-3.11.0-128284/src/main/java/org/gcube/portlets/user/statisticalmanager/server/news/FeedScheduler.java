/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.server.news;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * 
 */
public class FeedScheduler {

	protected Timer scheduler;
	protected Map<String, OperatorsNewsProducer> scheduledScopes;
	protected Logger logger = Logger.getLogger(FeedScheduler.class);

	public FeedScheduler(long refreshTime) {
		scheduledScopes = new HashMap<String, OperatorsNewsProducer>();
		scheduler = new Timer(true);
		scheduler.schedule(new TimerTask() {
			@Override
			public void run() {
				checkScopes();
			}
		}, 0, refreshTime);
	}

	public void schedule() throws Exception {
		List<String> scopes = getAvailableScopes();
		for (String scope : scopes) {
			logger.trace("checking scope: " + scope);
			if (isServicePresentInScope(scope)) {
				logger.trace("service present");
				schedule(scope);
			} else
				logger.trace("service not present");
		}

		checkScopes();
	}

	protected void schedule(String scope) {
		if (!scheduledScopes.containsKey(scope)) {
			System.out.println("inside create new operator newProduce");

			OperatorsNewsProducer feeder = new OperatorsNewsProducer(scope);
			scheduledScopes.put(scope, feeder);
		}
		else
		{
			System.out.println("non created operator");

		}
	}

	protected void checkScopes() {
		System.out.println("inside checkScope");
		for (OperatorsNewsProducer feeder : scheduledScopes.values()) {
			try {
				feeder.checkOperatorsForFeed();
			} catch (Exception e) {
			}
		}
	}

	protected boolean isServicePresentInScope(String scope) throws Exception {

		SimpleQuery query = queryFor(GCoreEndpoint.class);

		query.addCondition("$resource/Profile/ServiceName/text() eq 'statistical-manager-gcubews'");
		query.addCondition("$resource/Profile/ServiceClass/text() eq 'DataAnalysis'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		//
		System.out.println("scope"+ScopeProvider.instance.get());
		List<ServiceEndpoint> resources = client.submit(query);

		System.out.println("number of find service :"+resources.size());

		return resources.size()> 0;
	}

	protected static List<String> getAvailableScopes() throws Exception {
		
		List<String> scopes = new ArrayList<String>();
		String []scopeNames= ScopeProvider.instance.get().split("/");
		int i=0;
		String toltaPathScope="";
		while(i<scopeNames.length || i <2)
		{
			if(i==0)
			toltaPathScope=toltaPathScope+scopeNames[i];
			else
				toltaPathScope=toltaPathScope+"/"+scopeNames[i];

			System.out.println("scope add:"+toltaPathScope);
			scopes.add(toltaPathScope);
			i++;
			
		}
		
		return scopes;
	}

//	protected static List<String> findAvailableScopes(String infrastructure)
//			throws Exception {
//
//		List<String> scopes = new ArrayList<String>();
//
//		// ************* PORTAL MODE, Checking organizations
//		scopes.add(infrastructure);
//
//		// /************* GET ROOT ORGANIZATION
//		List<Organization> organizations = OrganizationLocalServiceUtil
//				.getOrganizations(0,
//						OrganizationLocalServiceUtil.getOrganizationsCount());
//		Organization rootOrganization = null;
//		for (Organization organization : organizations) {
//			if (organization.getName().equals(infrastructure.getName())) {
//				rootOrganization = organization;
//				break;
//			}
//		}
//		if (rootOrganization == null)
//			throw new Exception("Unable to find infrastructure scope "
//					+ infrastructure.getName() + " among organizations");
//		// ************** GET VO
//		for (Organization vOrg : rootOrganization.getSuborganizations()) {
//			String VOScopeString = "/" + vOrg.getParentOrganization().getName()
//					+ "/" + vOrg.getName();
//			try {
//				scopes.add(GCUBEScope.getScope(VOScopeString));
//				for (Organization vre : vOrg.getSuborganizations()) {
//					String VREScopeString = VOScopeString + "/" + vre.getName();
//					try {
//						scopes.add(GCUBEScope.getScope(VREScopeString));
//					} catch (Exception e) {
//					}
//				}
//			} catch (Exception e) {
//			}
//			// ************* GET VRE
//		}
//
//		return scopes;
//	}

	public static void main(String[] args) throws Exception {
		ScopeProvider.instance.set("/gcube/devNext");
		FeedScheduler scheduler = new FeedScheduler(1000);
		scheduler.schedule();
	}

}
