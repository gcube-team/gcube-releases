package org.gcube.vremanagement.vremodeler.resources.handlers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.GenericResource;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

public class GenericResourceHandler implements ResourceHandler<GenericResource> {

	//private static GCUBELog logger= new GCUBELog(GenericResourceHandler.class); 
	
	public static GCUBELog logger = new GCUBELog(GenericResourceHandler.class);
	
	public void add(GenericResource resource) throws Exception {
		Dao<GenericResource, String> genericResourceDao =
	            DaoManager.createDao(DBInterface.connect(), GenericResource.class);
		genericResourceDao.createOrUpdate(resource);
	}

	public void drop(String resourceId) throws Exception {
		Dao<GenericResource, String>genericResourceDao =
	            DaoManager.createDao(DBInterface.connect(), GenericResource.class);
		genericResourceDao.deleteById(resourceId);
	}

	public List<GenericResource> initialize() throws Exception {
		ISClient queryClient= GHNContext.getImplementation(ISClient.class);
		GCUBEGenericResourceQuery query= queryClient.getQuery(GCUBEGenericResourceQuery.class);
		List<GCUBEGenericResource> gcubeGenericResourcesList= queryClient.execute(query, GCUBEScope.getScope(ScopeProvider.instance.get()));
		List<GenericResource> genericResources = new ArrayList<GenericResource>();
		for (GCUBEGenericResource gcubeGenericResource:gcubeGenericResourcesList){
			GenericResource genericResource = new GenericResource(gcubeGenericResource.getID(), gcubeGenericResource.getSecondaryType(), gcubeGenericResource.getName(),
					gcubeGenericResource.getDescription(), gcubeGenericResource.getBody());
			logger.trace("add genericResource with secondaryType "+genericResource.getType() );
			this.add(genericResource);
			genericResources.add(genericResource);
		}
		return genericResources;
	}

}
