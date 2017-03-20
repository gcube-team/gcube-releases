package org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Retrieves the base url of the social-networking service in the scope provided
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class GCoreEndPointReaderSocial {

	private String basePath = null;
	private static final Log logger = LogFactoryUtil.getLog(GCoreEndPointReaderSocial.class);
	private static final String resource = "jersey-servlet";
	private static final String serviceName = "SocialNetworking";
	private static final String serviceClass = "Portal";

	public GCoreEndPointReaderSocial(String context){

		if(context == null || context.isEmpty())
			throw new IllegalArgumentException("A valid context is needed to discover the service");


		String oldContext = ScopeProvider.instance.get();
		ScopeProvider.instance.set(context);

		try{

			SimpleQuery query = queryFor(GCoreEndpoint.class);
			query.addCondition(String.format("$resource/Profile/ServiceClass/text() eq '%s'",serviceClass));
			query.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");
			query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'",serviceName));
			query.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces//Endpoint[@EntryName/string() eq \""+resource+"\"]/text()");
			
			DiscoveryClient<String> client = client();
			List<String> endpoints = client.submit(query);
			if (endpoints == null || endpoints.isEmpty()) throw new Exception("Cannot retrieve the GCoreEndpoint serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+context);


			this.basePath = endpoints.get(0);
			if(basePath==null)
				throw new Exception("Endpoint:"+resource+", is null for serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+context);

			logger.info("found entyname "+basePath+" for ckanResource: "+resource);

		}catch(Exception e){
			logger.error("Unable to retrieve such service endpoint information!", e);
		}finally{
			if(oldContext != null && !oldContext.equals(context))
				ScopeProvider.instance.set(oldContext);
		}
		logger.info("Found base path " + basePath + " for the service");
	}

	/**
	 * Get the base path of the social networking service
	 * @return
	 */
	public String getBasePath() {
		return basePath;
	}
}
