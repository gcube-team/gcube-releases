package org.gcube.soa3.connector.impl;

import it.eng.rdlab.soa3.connector.beans.UserBean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.HostingNode.Profile;
import org.gcube.common.resources.gcore.HostingNode.Profile.NodeDescription;
import org.gcube.common.resources.gcore.HostingNode.Profile.Site;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.gcube.soa3.connector.Authenticate;

import com.sun.jersey.core.util.Base64;

/**
 * 
 * Authentication service client for federated users
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class ServiceDNAuthentication  implements Authenticate 
{
	private final String 	XPATH_STRING = "$resource/Profile/GHNDescription/SecurityData/CredentialsDistinguishedName/text()",
							RESULT_TYPE = "$resource/Profile",
							
							GHN_PREFIX = "GHN";
							
	private Log 			logger;
	
	private String scope = null;
	
	public  ServiceDNAuthentication (String scope)
	{
		this.logger = LogFactory.getLog(this.getClass());
		this.scope = scope;
	}


	@Override
	public void setSoa3Endpoint(String soa3Endpoint) 
	{
	}

	@Override
	public UserBean authenticate(String parameter, String organization) 
	{
		this.logger.debug("Using information system client");
		this.logger.debug("For scope "+scope);
		ScopeProvider.instance.set(scope);
		//ScopeHelper.setContext((RenderRequest)request
		String decodedDN = Base64.base64Decode(parameter);
		SimpleQuery query = ICFactory.queryFor(HostingNode.class);
		StringBuilder xpathStringBuilder = new StringBuilder (XPATH_STRING);
		xpathStringBuilder.append(" eq ").append("\"").append(decodedDN).append("\"");
		logger.debug("Query string "+xpathStringBuilder);
		query.addCondition(xpathStringBuilder.toString()).setResult(RESULT_TYPE);
		logger.debug("Loading discovery client..."); 
		DiscoveryClient<Profile> client = ICFactory.clientFor(Profile.class);
		UserBean responseBean = null;
		
		try
		{
			List<Profile> profiles = client.submit(query);
			logger.debug("Query performed");
			
			if (profiles != null && profiles.size()>0)
			{
				if (profiles.size()>1) logger.warn("Found "+profiles.size()+" entries: only the first one will be used");
				
				Profile profile = profiles.get(0);
				
				logger.debug("Received profile ");
				logger.debug(profile);
				responseBean = new UserBean();
				NodeDescription description = profile.description();
				logger.debug("Description: "+description);
				List<String> roles = new ArrayList<String>();
				
				if (description != null)
				{
					String name = description.name();
					logger.debug("Name "+name);
					responseBean.setUserName(name);
					roles.add(GHN_PREFIX+"."+profile.description().name());
				}

				Site site = profile.site();
				logger.debug("Site: "+site);
				
				if (site != null)
				{
					String country = site.country();
					String domain = site.domain();
					String location = site.location();
					logger.debug("Country "+country);
					logger.debug("Domain "+domain);
					logger.debug("Location "+location);
					roles.add(GHN_PREFIX+"."+profile.site().country());
					roles.add(GHN_PREFIX+"."+profile.site().domain());
					roles.add(GHN_PREFIX+"."+profile.site().location());
				}

				roles.add(decodedDN);
				logger.debug("Profile attributes "+roles);
				responseBean.setRoles(roles);

			}
			else
			{
				logger.debug("Node not found");
			}


		} catch (RuntimeException e)
		{
			logger.error("Exception in sending IS query", e);
		}
		
		
		return responseBean;
	}
	


}
