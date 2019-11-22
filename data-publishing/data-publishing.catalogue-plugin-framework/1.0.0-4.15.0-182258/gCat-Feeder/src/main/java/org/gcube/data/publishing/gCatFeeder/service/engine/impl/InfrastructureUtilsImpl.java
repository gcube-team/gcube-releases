package org.gcube.data.publishing.gCatFeeder.service.engine.impl;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.data.publishing.gCatFeeder.service.engine.Infrastructure;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DatabaseConnectionDescriptor;
import org.gcube.data.publishing.gCatFeeder.utils.CommonUtils;
import org.gcube.data.publishing.gCatFeeder.utils.ContextUtils;
import org.gcube.data.publishing.gCatFeeder.utils.ISUtils;
import org.gcube.data.publishing.gCatFeeder.utils.TokenUtils;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class InfrastructureUtilsImpl implements Infrastructure {

	private static final Logger log= LoggerFactory.getLogger(InfrastructureUtilsImpl.class);
	
	@Override
	public String getCurrentToken() {
		return TokenUtils.getCurrentToken();
	}

	@Override
	public String getCurrentContext() {
		return ContextUtils.getCurrentScope();
	}

	@Override
	public String getClientID(String token){
		try{
			return TokenUtils.getClientId(token);
		}catch(Exception e) {
			throw new RuntimeException("Unable to get client id from "+token);
		}
	}

	@Override
	public void setToken(String token) {
		TokenUtils.setToken(token);
	}

	
	@Override
	public String getCurrentContextName() {
		return ContextUtils.getCurrentScopeName();
	}
	
	
	@Override
	public String decrypt(String toDecrypt) {
		return CommonUtils.decryptString(toDecrypt);
	}
	
	@Override
	public String encrypt(String toEncrypt) {
		return CommonUtils.encryptString(toEncrypt);
	}
	
	@Override
	public DatabaseConnectionDescriptor queryForDatabase(String category, String name) throws InternalError {
		log.debug("Querying for DB {},{} under {}",category,name,getCurrentContext());
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		
		query.addCondition("$resource/Profile/Category/text() eq '"+category+"'");
		
		
		query.addCondition("$resource/Profile/Name/text() eq '"+name+"'");
		
		
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> found=client.submit(query);
		
		if(found.size()==0) throw new InternalError("Unable to find DB "+category+"/"+name+" in "+getCurrentContext());
		if(found.size()>1) log.warn("Multiple DB "+category+"/"+name+" in "+getCurrentContext());
		AccessPoint point= found.get(0).profile().accessPoints().iterator().next();
		
		String url="jdbc:postgresql://"+point.address()+"/"+point.name();
		
		DatabaseConnectionDescriptor toReturn= new DatabaseConnectionDescriptor(point.username(), url, CommonUtils.decryptString(point.password()));
		log.debug("Going to use DB : "+toReturn);
		
		return toReturn;
	}
	
	@Override
	public Map<String, String> getEnvironmentConfigurationParameters() {
		return ISUtils.loadConfiguration();
	}
}
