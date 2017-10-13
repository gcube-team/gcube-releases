package org.gcube.vremanagement.vremodeler.resources.handlers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEServiceQuery;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.resources.service.MainPackage;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

public class ServiceHandler implements ResourceHandler<Service>{

	private static GCUBELog logger= new GCUBELog(ServiceHandler.class);
	
	@Override
	public List<Service> initialize() throws Exception {
		ISClient client= GHNContext.getImplementation(ISClient.class);
		GCUBEServiceQuery query=client.getQuery(GCUBEServiceQuery.class);
		List<GCUBEService> gcubeServiceList= client.execute(query, GCUBEScope.getScope(ScopeProvider.instance.get()));
		List<Service> services = new ArrayList<Service>();
		for (GCUBEService gcubeService:gcubeServiceList)
			try{
				String packageName =null;
				String packageVersion = null;
				for (Package packageSW : gcubeService.getPackages()){
					if (packageSW instanceof MainPackage){
						packageName = packageSW.getName();
						packageVersion = packageSW.getVersion();
						break;
					}else if (packageName==null || packageVersion==null){
						packageName = packageSW.getName();
						packageVersion = packageSW.getVersion(); 
					}
				}
				Service service = new Service(gcubeService.getID(), gcubeService.getServiceClass(), gcubeService.getServiceName(), gcubeService.getVersion(), packageName, packageVersion );
				insert(service);
				services.add(service);
				logger.trace("added service "+service.getServiceClass()+" "+service.getServiceName()+" "+packageName+" "+packageVersion);
			}catch(Exception e){logger.error("error inserting service", e);}
		return services;
	}

	@Override
	public void add(Service resource) throws Exception {
		this.insert(resource);
		
	}

	@Override
	public void drop(String resourceId) throws Exception {
		Dao<Service, String> serviceDao =
	            DaoManager.createDao(DBInterface.connect(), Service.class);		
		serviceDao.deleteById(resourceId);
	}
	
	private void insert(Service resource) throws Exception {
		Dao<Service, String> serviceDao =
	            DaoManager.createDao(DBInterface.connect(), Service.class);
		serviceDao.createOrUpdate(resource);
				
		logger.trace("inserting service with name "+resource.getServiceName()+" class "+resource.getServiceClass()+" version "+resource.getVersion());
		
	}

}
