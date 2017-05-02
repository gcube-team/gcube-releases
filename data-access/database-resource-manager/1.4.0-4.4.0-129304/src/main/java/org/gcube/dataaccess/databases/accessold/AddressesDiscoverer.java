//package org.gcube.dataaccess.databases.accessold;
//
//import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
//import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.gcube.common.resources.gcore.ServiceEndpoint;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.dataaccess.databases.resources.DBResource;
//import org.gcube.resources.discovery.client.api.DiscoveryClient;
//import org.gcube.resources.discovery.client.queries.impl.XQuery;
//
///**
// * Class that, allowing to set a scope and to submit a query to the IS, recover
// * a list of url for each DBResource object
// */
//
//public class AddressesDiscoverer {
//
//	/** Method to set the scope */
//	public void setScope(String scope) {
//
//		ScopeProvider.instance.set(scope);
//
//	}
//
//	/** Method to recover the url's list */
//	public List<String> retrieveAddress(String Category) {
//
//		List<String> addresses = new ArrayList<String>();
//
//		XQuery query = queryFor(ServiceEndpoint.class);
//		query.addCondition("$resource/Profile/Category/text() eq '" + Category
//				+ "'");
//
//		DiscoveryClient<DBResource> submitop = clientFor(DBResource.class);
//		List<DBResource> access = submitop.submit(query);
//
//		// System.out.println("size resource:  "+access.size());
//
//		int APsize = 0;
//		String address = "";
//
//		for (int i = 0; i < access.size(); i++) {
//
//			APsize = access.get(i).getAccessPoints().size();
//
//			for (int j = 0; j < APsize; j++) {
//
//				address = access.get(i).getAccessPoints().get(j).address();
//				addresses.add(address);
//
//			}
//
//		}
//
//		return addresses;
//
//	}
//
//}
