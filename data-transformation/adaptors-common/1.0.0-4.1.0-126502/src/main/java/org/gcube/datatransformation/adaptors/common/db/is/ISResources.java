package org.gcube.datatransformation.adaptors.common.db.is;


import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.application.framework.core.genericresources.model.ISGenericResource;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.core.util.GenericResource;
import org.gcube.application.framework.core.util.RuntimeResource;
import org.gcube.common.database.DatabaseProvider;
import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.gcube.common.database.engine.DatabaseInstance;
import org.gcube.common.database.is.ISDatabaseProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransformation.adaptors.common.constants.ConstantNames;
import org.gcube.datatransformation.adaptors.common.db.tools.DBConstants;
import org.gcube.datatransformation.adaptors.common.db.tools.SourcePropsTools;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBProps;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBSource;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISResources {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(ISResources.class);
	
//	protected static ScopedPublisher scopedPublisher = null;
//	protected static DiscoveryClient<ServiceEndpoint> client = null;
	private static DatabaseProvider isDBProvider = new ISDatabaseProvider();
	

	public static DBSource getDBSourceInfo(String dbName, String scope) throws MalformedURLException{
		ScopeProvider.instance.set(scope);
		logger.debug("Searching for dbName: "+dbName+ " on scope: "+scope);
		DatabaseInstance db = isDBProvider.get(dbName);
		DatabaseEndpoint [] endpoints = db.getEndpoints().values().toArray(new DatabaseEndpoint[0]);
		logger.debug("Endpoints.length: "+endpoints.length);
		DBSource output = new DBSource();
		for(DatabaseEndpoint ep : endpoints){
			if(ep.getId().contains("jdbc")){
				output.setSourceName(dbName);
				output.setDBType(DBConstants.getFixedFullNameOf(db.getPlatform().getName()));
				output.setConnectionString(ep.getConnectionString());
				output.setHostName(db.getHostingURL());
				output.setUserName(ep.getCredentials().getUsername());
				output.setPassword(ep.getCredentials().getPassword());
				output.setVersionMajor(db.getPlatform().getVersion());
				output.setVersionMinor(db.getMinorVersion());
			}
		}
		return output;
	}

	
	public static String getDBPropsByName(String scope, String dbName, String propsName) throws Exception{
		List<ISGenericResource> allRes;
		try {
			allRes = GenericResource.getGenericResourcesByType(scope, ConstantNames.RESOURCE_CLASS);
		} catch (RemoteException e) {
			return null;
		}
		String result = new String();
		for(ISGenericResource res : allRes){
			//skip the generic resources of ConstantNames.RESOURCE_CLASS which are not for DB
			if(!res.getName().contains(ConstantNames.RESOURCE_NAME_PREF_DB))
				continue;
			DBProps props = SourcePropsTools.parseSourceProps(res.getBody());
			if(res.getName().equals(propsName) && props.getSourceName().equals(dbName)){
				logger.debug("propsName: "+res.getName()+"\tdbName: "+props.getSourceName());
				result = res.getBody();
			}
		}
		return result;
	}

	

}


