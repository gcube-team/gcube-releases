package org.gcube.vremanagement.vremodeler.resources.handlers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGHNQuery;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Ghn;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

public class GHNHandler implements ResourceHandler<Ghn> {

	private static GCUBELog logger = new GCUBELog(GHNHandler.class);
	
	public static final String tableName="GHN";
		
	public List<Ghn> initialize() throws Exception{
		
		ISClient client= GHNContext.getImplementation(ISClient.class);
		GCUBEGHNQuery query=client.getQuery(GCUBEGHNQuery.class);
		//query.addAtomicConditions(new AtomicCondition("/Profile/GHNDescription/Type","Dynamic"));
		query.addAtomicConditions(new AtomicCondition("/Profile/GHNDescription/Status","certified"));
		List<GCUBEHostingNode> gcubeHostingNodeList= client.execute(query, GCUBEScope.getScope(ScopeProvider.instance.get()));
		List<Ghn> ghns = new ArrayList<Ghn>();
		for (GCUBEHostingNode gcubeHostingNode:gcubeHostingNodeList)
			try{
				long availableMemory = gcubeHostingNode.getNodeDescription().getMemory()==null?0:gcubeHostingNode.getNodeDescription().getMemory().getAvailable();
				long localAvailableSpace = gcubeHostingNode.getNodeDescription().getLocalAvailableSpace()==null?0l:gcubeHostingNode.getNodeDescription().getLocalAvailableSpace();
				Ghn ghn = new Ghn(gcubeHostingNode.getID(), 
						gcubeHostingNode.getNodeDescription().getName(),
						gcubeHostingNode.getSite().getLocation(), 
						gcubeHostingNode.getSite().getCountry(), 
						gcubeHostingNode.getSite().getDomain(),
						availableMemory, 
						localAvailableSpace, false);
				ghn.setSecurityEnabled(gcubeHostingNode.getNodeDescription().isSecurityEnabled());
				insert(ghn);
				ghns.add(ghn);
			}catch(Exception e){
				logger.error("error inserting values in "+tableName, e);
			}
		return ghns;
	}
	
	private void insert(Ghn ghn) throws Exception {
		RunningInstancesHandler riHandler= new RunningInstancesHandler(ghn);
		riHandler.initialize();
		Dao<Ghn, String> ghnDao =
	            DaoManager.createDao(DBInterface.connect(), Ghn.class);
		if (ghnDao.idExists(ghn.getId()))
			ghnDao.update(ghn);
		else ghnDao.create(ghn);
	}

	public void add(Ghn resource) throws Exception {
		this.insert(resource);
		
	}

	public void drop(String ghnId) throws Exception {
		Dao<Ghn, String> ghnDao =
	            DaoManager.createDao(DBInterface.connect(), Ghn.class);
		ghnDao.deleteById(ghnId);
	}
	
}
