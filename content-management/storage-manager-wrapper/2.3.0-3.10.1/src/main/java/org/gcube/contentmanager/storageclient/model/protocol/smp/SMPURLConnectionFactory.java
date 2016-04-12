package org.gcube.contentmanager.storageclient.model.protocol.smp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.contentmanagement.blobstorage.service.operation.GetUrl;
import org.gcube.contentmanager.storageclient.protocol.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class used to determine the right smp url protocol: old format(<2.2.0 version), new format (>= 2.2.0-SNAPSHOT)
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class SMPURLConnectionFactory {

	static Logger logger=LoggerFactory.getLogger(SMPURLConnectionFactory.class);
	
	public static SMPConnection getSmp(URL url) {
		String urlString=url.toString();
		if(isNewSmpFormat(urlString)|| isOldNewType(urlString)){
			logger.info("detected new smp format "+url);
			SMPConnection connection=load(url);
			if (connection!= null)
				return connection;
			return new SMPURLConnectionById(url);
		}else{ 
			logger.info("detected old smp format "+url);
			return new SMPURLConnectionOld(url);
		}
	}
	
	
	/**
	 * patch method for a particular smp uri: smp://data.gcube.org/8TFLhJ991DF1M/Ae8rGamC3CgCjAXlnVGmbP5+HKCzc=
	 * @param urlString
	 * @return
	 */
	private static boolean isOldNewType(String urlString) {
		String [] urlParam=urlString.split(GetUrl.URL_SEPARATOR);
		String infraHost=urlParam[2];
		if(Utils.isScopeProviderMatch(infraHost))
			return true;
		String infra=Utils.getInfraFromResolverHost(infraHost);
		String rootScope="/"+infra;
		ScopeBean scope=new ScopeBean(rootScope);
		if(scope.is(Type.INFRASTRUCTURE)){
			return Utils.validationScope2(rootScope);
		}else{
			return false;
		}
	}

	private static boolean isNewSmpFormat(String urlString) {
		logger.debug("check format: "+urlString);
		String httpString=urlString;
		httpString=httpString.replace("smp://", "http://");
		logger.debug("httpUrl conversion: "+httpString);
		URL httpUrl=null;
		try {
			httpUrl=new URL(httpString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<ServiceEndpoint> services=Utils.queryServiceEndpoint(Utils.URI_RESOLVER_RESOURCE_CATEGORY, Utils.URI_RESOLVER_RESOURCE_NAME);
		String host=null;
		if(services != null && services.size()>0){
			host=Utils.getResolverHost(services.get(0));
		}
		logger.debug("uri-resolver host: "+host+" in scope: "+ScopeProvider.instance.get());
		if((host!=null) && (host.equals(httpUrl.getHost()))){
			return true;
		}else{
			return false;
		}
	}

	
	
	private static SMPConnection load(URL url){
		ServiceLoader<SMPConnection> loader = ServiceLoader.load(SMPConnection.class);
		Iterator<SMPConnection> iterator = loader.iterator();
		List<SMPConnection> impls = new ArrayList<SMPConnection>();
		 while(iterator.hasNext())
			 impls.add(iterator.next());
		 int implementationCounted=impls.size();
//		 System.out.println("size: "+implementationCounted);
		 if(implementationCounted==0){
			 logger.info(" 0 implementation found. Load default implementation of SMPConnection");
			 return null;
		 }else if(implementationCounted>0){
			 SMPConnection connection = impls.get(0);
			 logger.info("1 implementation of TransportManager found. ");
				 connection.init(url);
				 return connection;
		 }else{
			 return null;
		 }
	}

}
