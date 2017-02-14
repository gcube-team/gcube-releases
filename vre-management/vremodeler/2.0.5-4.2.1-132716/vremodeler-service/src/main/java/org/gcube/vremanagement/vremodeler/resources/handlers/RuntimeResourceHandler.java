package org.gcube.vremanagement.vremodeler.resources.handlers;

import java.util.ArrayList;
import java.util.List;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERuntimeResourceQuery;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.RuntimeResource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

public class RuntimeResourceHandler implements ResourceHandler<RuntimeResource>{

	private static GCUBELog logger= new GCUBELog(RuntimeResourceHandler.class);
	
	@Override
	public List<RuntimeResource> initialize() throws Exception {
		ISClient client= GHNContext.getImplementation(ISClient.class);
		GCUBERuntimeResourceQuery query=client.getQuery(GCUBERuntimeResourceQuery.class);
		List<GCUBERuntimeResource> gcubeRuntimeResourceList= client.execute(query, ServiceContext.getContext().getScope());
		List<RuntimeResource> runtimeResources = new ArrayList<RuntimeResource>();
		for (GCUBERuntimeResource gcubeRuntimeResource:gcubeRuntimeResourceList)
			try{
				RuntimeResource runtimeResource = new RuntimeResource(gcubeRuntimeResource.getID(), gcubeRuntimeResource.getName(), gcubeRuntimeResource.getCategory(), gcubeRuntimeResource.getDescription() );
				insert(runtimeResource);
				runtimeResources.add(runtimeResource);
			}catch(Exception e){logger.error("error inserting runtimeResource", e);}
		return runtimeResources;
	}

	@Override
	public void add(RuntimeResource resource) throws Exception {
		this.insert(resource);
		
	}

	@Override
	public void drop(String resourceId) throws Exception {
		Dao<RuntimeResource, String> runtimeResourceDao =
	            DaoManager.createDao(DBInterface.connect(), RuntimeResource.class);		
		runtimeResourceDao.deleteById(resourceId);
	}
	
	private void insert(RuntimeResource resource) throws Exception {
		Dao<RuntimeResource, String> runtimeResourceDao =
	            DaoManager.createDao(DBInterface.connect(), RuntimeResource.class);
		runtimeResourceDao.createOrUpdate(resource);
				
		logger.trace("inserting runtimeResource with id "+resource.getId());
		
	}
	

}
