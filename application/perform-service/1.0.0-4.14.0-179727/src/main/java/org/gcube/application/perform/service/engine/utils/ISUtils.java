package org.gcube.application.perform.service.engine.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.application.perform.service.engine.model.DatabaseConnectionDescriptor;
import org.gcube.application.perform.service.engine.model.ISQueryDescriptor;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISUtils {

	private static final Logger log= LoggerFactory.getLogger(ISUtils.class);
	
	private static String fixedToken=null;
	
	
	public static DatabaseConnectionDescriptor queryForDatabase(ISQueryDescriptor desc) throws InternalException {
		
		if(fixedToken!=null) {
			SecurityTokenProvider.instance.set(fixedToken);
		}
		
		
		
		log.debug("Querying for Service Endpoints {} ",desc,ScopeUtils.getCurrentScope());
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		if(desc.getCategory()!=null)
		query.addCondition("$resource/Profile/Category/text() eq '"+desc.getCategory()+"'");
		
		if(desc.getPlatformName()!=null)
		query.addCondition("$resource/Profile/Platform/Name/text() eq '"+desc.getPlatformName()+"'");
		
		if(desc.getResourceName()!=null)
		query.addCondition("$resource/Profile/Name/text() eq '"+desc.getResourceName()+"'");
		
		
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> found=client.submit(query);
		
		if(found.size()==0) throw new InternalException("Unable to find Service Endpoint "+desc+" in "+ScopeUtils.getCurrentScope());
		if(found.size()>1) log.warn("Multiple Endpoints "+desc+" found in "+ScopeUtils.getCurrentScope());
		AccessPoint point= found.get(0).profile().accessPoints().iterator().next();
		
		String url="jdbc:postgresql://"+point.address()+"/"+point.name();
		
		DatabaseConnectionDescriptor toReturn= new DatabaseConnectionDescriptor(point.username(), url, CommonUtils.decryptString(point.password()));
		log.debug("Going to use DB : "+toReturn);
		
		return toReturn;
	}
	
	public static final void setFixedToken(String toSet) {
		log.warn("SETTING FIXED TOKEN. THIS SHOULD HAPPEN ONLY IN DEBUG MODE.");
		fixedToken=toSet;
	}
}
