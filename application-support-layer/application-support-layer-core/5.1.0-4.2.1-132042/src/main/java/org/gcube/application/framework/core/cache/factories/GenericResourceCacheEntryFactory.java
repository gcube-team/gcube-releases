
package org.gcube.application.framework.core.cache.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gcube.application.framework.core.genericresources.model.ISGenericResource;
import org.gcube.application.framework.core.util.CacheEntryConstants;
import org.gcube.application.framework.core.util.QueryString;
import org.gcube.application.framework.core.util.SessionConstants;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.gcube.common.core.contexts.GHNContext;
//import org.gcube.common.core.informationsystem.client.AtomicCondition;
//import org.gcube.common.core.informationsystem.client.ISClient;
//import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
//import org.gcube.common.core.resources.GCUBEGenericResource;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.scope.GCUBEScope.Type;

import static org.gcube.resources.discovery.icclient.ICFactory.*;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

/**
 * @author Valia Tsagkalidou
 *
 */
public class GenericResourceCacheEntryFactory implements CacheEntryFactory {

//	static ISClient client = null;
	
	static DiscoveryClient<GenericResource> client = null;
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(GenericResourceCacheEntryFactory.class);
	
	/**
	 * Constructor to initialize the ISClient
	 */
	public GenericResourceCacheEntryFactory() {
		super();
		if(client == null)
		{
			try {
//				client = GHNContext.getImplementation(ISClient.class);
				client = clientFor(GenericResource.class);
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}

	/**
	 * @param key a QueryString representing pairs of keys and values: needed keys are "vre" and one of "id" or "name"
	 * @return  a list containing the Generic Resources that correspond to the query
	 */
	public List<ISGenericResource> createEntry(Object key) throws Exception {
		QueryString querySt = (QueryString) key;
		logger.info("query: " + querySt.toString());
		String scope = ScopeProvider.instance.get();
		SimpleQuery query = queryFor(GenericResource.class);
		
		logger.info("In generic resources cache entry factory");

		if(querySt.containsKey(CacheEntryConstants.id))//Retrieving generic resources based on their ID
			query.addCondition("$resource/ID eq '"+querySt.get(CacheEntryConstants.id)+"'");
		else if(querySt.containsKey(CacheEntryConstants.name)) //Retrieving generic resources based on their name
			query.addCondition("$resource/Profile/Name eq '"+querySt.get(CacheEntryConstants.name)+"'");
		else if(querySt.containsKey(CacheEntryConstants.vreResource)){		
			String secondaryType = new String();
			ScopeBean sb = new ScopeBean(ScopeProvider.instance.get());
			if(sb.is(ScopeBean.Type.VRE))
				secondaryType = ScopeBean.Type.VRE.name();
			else if (sb.is(ScopeBean.Type.VO))
				secondaryType = ScopeBean.Type.VO.name();
			else
				secondaryType = ScopeBean.Type.INFRASTRUCTURE.name();
			query.addCondition("$resource/Profile/SecondaryType eq '"+secondaryType+"'")
				 .addCondition("$resource/Scopes eq '"+scope+"'");

		}
		
		
		
		try{
			List<GenericResource> results = client.submit(query);
			if (querySt.containsKey(CacheEntryConstants.name) && querySt.get(CacheEntryConstants.name).equals(SessionConstants.ScenarioSchemaInfo)) {
				logger.info("The number of generic Resources for ScenarioSchemaInfo returned is: " + results.size());
				List<GenericResource> newResult = new ArrayList<GenericResource>();
				for (GenericResource resource : results) {
					ScopeGroup<String> scopes = resource.scopes();
					logger.info("Number of scopes for ScenarioSchemaInfo: " + scopes.size());
					if(scopes.contains(scope))
						newResult.add(resource);
				}
			
				if (newResult.size() > 1) {
					GenericResource voResource = null;
					for (GenericResource resource : newResult) {
						if(resource.scopes().size()==1){
							voResource = resource;
							break;
						}
					}
					newResult.clear();
					newResult.add(voResource);
				}
				logger.info("Number of genericResources for ScenarioSchemaInfo left after the pruning" + newResult.size());
				results = newResult;
			}
			logger.debug("size of results: " + results.size());
			
//			logger.debug("Printing filtered results contents: ");
//			for(GenericResource genRes : results){
//				logger.debug("genRes.id():"+genRes.id());
//				logger.debug("genRes.profile().body():"+genRes.profile().body());
//				for(String s : genRes.scopes())
//					logger.debug("scope:"+s);
//				logger.debug("genRes.profile().bodyAsString():"+genRes.profile().bodyAsString());
//				logger.debug("genRes.profile().description():"+genRes.profile().description());
//				logger.debug("genRes.profile().name():"+genRes.profile().name());
//				logger.debug("genRes.profile().type():"+genRes.profile().type());
//			}
			
			List<ISGenericResource> res = new ArrayList<ISGenericResource>();
			for(GenericResource resource : results)
			{
				ISGenericResource genResource = new ISGenericResource(resource.id(), resource.profile().name(),resource.profile().description(),resource.profile().bodyAsString(),resource.profile().type());
				res.add(genResource);
			}
			
//			logger.debug("Printing res contents: ");
//			for(ISGenericResource gr : res){
//				logger.debug("gr.getBody():"+gr.getBody());
//				logger.debug("gr.getDescription():"+gr.getDescription());
//				logger.debug("gr.getId():"+gr.getId());
//				logger.debug("gr.getName():"+gr.getName());
//				logger.debug("gr.getSecondaryType():"+gr.getSecondaryType());
//			}
			
			
			return res;
		}catch (Exception e) {
			logger.error("",e);
			return null;
		}

	}

}
