package org.gcube.vremanagement.vremodeler.resources.handlers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Ghn;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.RunningInstance;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

public class RunningInstancesHandler implements ResourceHandler<RunningInstance>{

	private static GCUBELog logger = new GCUBELog(RunningInstancesHandler.class);
	
	public static final String tableName="RunningInstance";
	
	private Ghn relatedGhn;
	
	public RunningInstancesHandler(Ghn ghn) {
		 this.relatedGhn=ghn;
	}
	
	public RunningInstancesHandler() {
		 this.relatedGhn=null;
	}
	
	
	public List<RunningInstance> initialize() throws Exception{
		ISClient client= GHNContext.getImplementation(ISClient.class);
		GCUBERIQuery queryRI= client.getQuery(GCUBERIQuery.class);
		queryRI.addGenericCondition("$result/Profile/GHN[string(@UniqueID) eq '"+this.relatedGhn.getId()+"']");
		List<GCUBERunningInstance> riList= client.execute(queryRI, GCUBEScope.getScope(ScopeProvider.instance.get()));
		List<RunningInstance> toReturn = new ArrayList<RunningInstance>();
		for (GCUBERunningInstance gcubeRunningInstance: riList)
			try{
				logger.trace("inserting RI "+gcubeRunningInstance.getServiceClass()+":"+gcubeRunningInstance.getServiceName()+" for ghn "+relatedGhn.getHost() );
				RunningInstance ri= new RunningInstance(gcubeRunningInstance.getID(), gcubeRunningInstance.getServiceClass(), gcubeRunningInstance.getServiceName());
				ri.setGhn(relatedGhn);
				insert(ri);
				toReturn.add(ri);
			}catch(Exception e){logger.error("error inserting values in "+tableName, e);}	
		return toReturn;
	}
	
	
	
	private void insert(RunningInstance ri) throws Exception{
		Dao<RunningInstance, String> runningInstanceDao =
		            DaoManager.createDao(DBInterface.connect(), RunningInstance.class);
		ri.setGhn(this.relatedGhn);
		if (runningInstanceDao.idExists(ri.getId()))
			runningInstanceDao.update(ri);
		else runningInstanceDao.create(ri);
		logger.trace("runningInstance added ");
	}

	public void add(RunningInstance resource) throws Exception {
		this.insert(resource);
		
	}

	public void drop(String riId) throws Exception {
		Dao<RunningInstance, String> runningInstanceDao =
	            DaoManager.createDao(DBInterface.connect(), RunningInstance.class);
		runningInstanceDao.deleteById(riId);
	}
	
}
