package org.gcube.vremanagement.vremodeler.resources.handlers;


import java.io.StringReader;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.FunctionalityPersisted;
import org.gcube.vremanagement.vremodeler.resources.kxml.KGCUBEGenericFunctionalityResource;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;

public class FunctionalityHandler implements ResourceHandler<FunctionalityPersisted>{
		
	private static GCUBELog logger= new GCUBELog(FunctionalityHandler.class);
	
		
	private String functionalityResourceId;
	
	public void add(FunctionalityPersisted resource)
			throws Exception {
		this.insert(resource);
	}

	public void drop(String resourceId)
			throws Exception {}

	public List<FunctionalityPersisted> initialize() throws Exception {
		
		KGCUBEGenericFunctionalityResource resource= new KGCUBEGenericFunctionalityResource();
		try{
			ISClient queryClient= GHNContext.getImplementation(ISClient.class);
			GCUBEGenericResourceQuery query= queryClient.getQuery(GCUBEGenericResourceQuery.class);
			query.addAtomicConditions(new AtomicCondition("/Profile/Name","FuctionalitiesResource"), new AtomicCondition("/Profile/SecondaryType","VREModelerResource"));
			GCUBEGenericQuery genericQuery= queryClient.getQuery(GCUBEGenericQuery.class);
			genericQuery.setExpression(query.getExpression());
			resource.load(new StringReader(queryClient.execute(genericQuery, ServiceContext.getContext().getScope()).get(0).toString()));
			this.functionalityResourceId= resource.getID();
			logger.debug("the functionality resource ID is "+this.functionalityResourceId);
			for (FunctionalityPersisted functionality: resource.fromResourceToPersistedList())
				insert(functionality);
			
			logger.debug("functionalities initialized");
		}catch(Exception e ){logger.error("Functionality resource not found",e); logger.warn("the service will be initialized without functionalities"); }
		
		return null;
	}
	
	private void insert( FunctionalityPersisted resource) throws Exception{
		Dao<FunctionalityPersisted, Integer> functionalityDao =
	            DaoManager.createDao(DBInterface.connect(), FunctionalityPersisted.class);
		functionalityDao.createOrUpdate(resource);
	}

	public void clearTable() throws Exception{
		TableUtils.clearTable(DBInterface.connect(), FunctionalityPersisted.class);
	}

	public String getFunctionalityResourceId() {
		return functionalityResourceId;
	}

}
