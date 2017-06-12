package org.gcube.vremanagement.vremodeler.resources.handlers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Collection;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

public class CollectionHandler implements ResourceHandler<Collection> {

	private static GCUBELog logger = new GCUBELog(CollectionHandler.class);
	
		
	public void add(Collection resource) throws Exception {
		this.insert(resource);
		
	}

	public void drop(String collectionId) throws Exception {
		Dao<Collection, String> collectionDao =
	            DaoManager.createDao(DBInterface.connect(), Collection.class);
		collectionDao.deleteById(collectionId);
	}

	public List<Collection> initialize() throws Exception {
		ISClient client= GHNContext.getImplementation(ISClient.class);
		//TODO: change when the new CM will return to GCUBECollection
		GCUBEGenericResourceQuery query=client.getQuery(GCUBEGenericResourceQuery.class);
		query.addAtomicConditions(new AtomicCondition("/Profile/Body/CollectionInfo/user","true"), new AtomicCondition("/Profile/SecondaryType", "GCUBECollection"));
		List<GCUBEGenericResource> gcubeCollectionList= client.execute(query, GCUBEScope.getScope(ScopeProvider.instance.get()));
		List<Collection> collections = new ArrayList<Collection>();
		for (GCUBEGenericResource gcubeCollection:gcubeCollectionList)
			try{
				Collection collection = new Collection(gcubeCollection.getID(),gcubeCollection.getName() , 
						gcubeCollection.getDescription()==null? "not provided" : gcubeCollection.getDescription());
				insert(collection);
				collections.add(collection);
			}catch(Exception e){logger.error("error inserting collections", e);}
		return collections;
	}
	
	
	private void insert(Collection collection) throws Exception {
		Dao<Collection, String> collectionDao =
	            DaoManager.createDao(DBInterface.connect(), Collection.class);
		collectionDao.createOrUpdate(collection);
				
		logger.trace("inserting collection with id "+collection.getId());
		
	}

}
