package org.gcube.spatial.data.geonetwork.configuration;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.utils.RuntimeParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class DefaultConfiguration implements  Configuration {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConfiguration.class);
	
	private String url;
	private Map<LoginLevel, String> users=new HashMap<LoginLevel, String>();
	private Map<LoginLevel, String> pwds=new HashMap<LoginLevel, String>();
	
	private int group;
	
	public DefaultConfiguration() throws Exception{
		Properties props=new RuntimeParameters().getProps();
		
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		 
		query.addCondition("$resource/Profile/Category/text() eq '"+props.getProperty("geonetwork.category")+"'")
				.addCondition("$resource/Profile/Platform/Name/text() eq '"+props.getProperty("geonetwork.platform.name")+"'")				
		         .setResult("$resource/Profile/AccessPoint");
		 
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		 
		List<AccessPoint> accesspoints = client.submit(query);

		String endpointName=props.getProperty("geonetwork.endpoint.name");
		String masterProperty=props.getProperty("geonetwork.master");
		String scopeName=ScopeProvider.instance.get().substring(ScopeProvider.instance.get().lastIndexOf('/')+1);
		String scopeUser=scopeName+props.getProperty("geonetwork.scopeuser.password.suffix");
		String privateUser=scopeName+props.getProperty("geonetwork.privateuser.password.suffix");
		String scopeGroup=scopeName+props.getProperty("geonetwork.scopegroup.id");
		
		
		boolean found=false;
		
		for (AccessPoint point : accesspoints) {
			if(point.name().equals(endpointName)){
				Map<String, Property> properties=point.propertyMap();
				if(properties.containsKey(masterProperty)&&Boolean.parseBoolean(properties.get(masterProperty).value())){
					url=point.address();
					users.put(LoginLevel.DEFAULT, point.username());
					pwds.put(LoginLevel.DEFAULT,decrypt(point.password()));
					if(properties.containsKey(privateUser)){
						users.put(LoginLevel.PRIVATE, privateUser);
						pwds.put(LoginLevel.PRIVATE, decrypt(properties.get(privateUser).value()));
					}
					if(properties.containsKey(scopeUser)){
						users.put(LoginLevel.SCOPE, scopeUser);
						pwds.put(LoginLevel.SCOPE, decrypt(properties.get(scopeUser).value()));
					}
					if(properties.containsKey(scopeGroup))
						group=Integer.parseInt(properties.get(scopeGroup).value());
					found=true;
					break;
//					users=point.username();
//					pwds=StringEncrypter.getEncrypter().decrypt(point.password());
				}
			}
		}
		if(!found) throw new Exception("No Resource found under current scope "+ScopeProvider.instance.get());
	}
	
	@Override
	public String getGeoNetworkEndpoint() {
		return url;
	}

	@Override
	public Map<LoginLevel, String> getGeoNetworkPasswords() {
		return pwds;
	}
	@Override
	public Map<LoginLevel, String> getGeoNetworkUsers() {
		return users;
	}

	@Override
	public int getScopeGroup() {
		return group;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DefaultConfiguration [url=");
		builder.append(url);
		builder.append(", users=");
		builder.append(users);
		builder.append(", pwds=");
		builder.append(pwds);
		builder.append("]");
		return builder.toString();
	}

	private static final String decrypt(String toDecrypt) throws Exception{
		return StringEncrypter.getEncrypter().decrypt(toDecrypt);
	}
	
	
}
