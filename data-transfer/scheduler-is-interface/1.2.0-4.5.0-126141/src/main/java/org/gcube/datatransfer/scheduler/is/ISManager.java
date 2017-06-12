package org.gcube.datatransfer.scheduler.is;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.Query;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBERuntimeResourceQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.AgentStatistics;
import org.gcube.datatransfer.scheduler.db.model.DataSource;
import org.gcube.datatransfer.scheduler.db.model.DataStorage;



public class ISManager  {
	/** The UUIDGen */
	private static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();
	public String type;
	public DataTransferDBManager dbManager;
	public String scope;
	GCUBELog logger = new GCUBELog(ISManager.class);

	public ISManager(String type, DataTransferDBManager dbManager, String scope) {
		this.dbManager=dbManager;		
		this.scope = scope;
		this.type=type;
	}


	/*
	 * updateObjsInDB
	 * input: Nothing
	 * return: Nothing
	 * used by CheckIS Thread 
	 */
	public void updateObjsInDB(){

		List<String> resultsFromIS;
		int newContentInDB=0;
		int oldContentInDB=0;
		String typeOfISManager = "";
		//1.
		//retrieving the objects (either Agents or Sources or Storages) 
		//from the IS and we check if there's something new
		resultsFromIS = this.getObjsFromIS();
		if(resultsFromIS==null)return;
		String delimiter="--";

		for(String tmpResult : resultsFromIS){	
			String[] tmpSplitResult;
			tmpSplitResult=tmpResult.split(delimiter);
			String resultIdOfIS = tmpSplitResult[0];

			if(this.checkIfObjExistsInDB_ById(resultIdOfIS)!=null){ //this one already exists in DB but is its status up?.. 
 				String idOfObjInDB = this.checkIfObjExistsInDB_ById(resultIdOfIS);
				//even if it exists, we update the info of obj again .. 
 				updateSpecificObjInDB(tmpSplitResult, idOfObjInDB);
			}
			else{
				//in other case we store it in DB because it seems to be a new one
				
				String idOfObjInDB = this.setObjToDB(tmpSplitResult);
				if(idOfObjInDB==null){
					logger.debug("ISManager - updateObjsInDB - Error in storing the new obj in DB");
				}
				else if(idOfObjInDB.compareTo("lessParameters")==0){
					//nothing ... it was not stored because it seems there is 
					//a new object that has different structure..
				}
				else{
					newContentInDB++;
					if(this.type.compareTo("Agent")==0){ 
						//if it's an agent we check also if there is an agentStatistics table 
						Extent<?> resultExtentSt = null;
						boolean exists=false;
						resultExtentSt = this.dbManager.getPersistenceManager().getExtent(AgentStatistics.class, true);
						Iterator<?> iterst = resultExtentSt.iterator();
						while (iterst.hasNext()){
							AgentStatistics obj=(AgentStatistics) iterst.next();
							if(obj.getAgentIdOfIS().compareTo(resultIdOfIS)==0){exists=true;break;}
						}
						if(!exists){ // if there is no statistics table for agent we create it
							AgentStatistics agentStatistics = new AgentStatistics();
							agentStatistics.setAgentIdOfIS(resultIdOfIS);
							try {// *** store the AgentStatistics in DB ***
								this.dbManager.storeAgentStatistics(agentStatistics);
							} catch (Exception e) {
								logger.error("ISManager.setObjToDB - Exception in storing the AgentStatistics");
								e.printStackTrace();								
							}
						}
					}
				}
			}				
		}

		//2.
		//retrieving all the nodes (either agents-sources or storages) from the DB and we check if someone does not exist anymore		
		try{
			Extent<?> resultExtent = null;
			if(this.type.compareTo("Agent")==0){
				resultExtent = this.dbManager.getPersistenceManager().getExtent(Agent.class, true);
			}
			else if(this.type.compareTo("DataSource")==0){
				resultExtent = this.dbManager.getPersistenceManager().getExtent(DataSource.class, true);
			}
			else if(this.type.compareTo("DataStorage")==0){
				resultExtent = this.dbManager.getPersistenceManager().getExtent(DataStorage.class, true);
			}
			else logger.debug("ISManager - updateObjsInDB - Error - there is no class with this name");

			Iterator<?> iter = resultExtent.iterator();
			String idOfObjInDB = null;
			String idOfObjOfIS = null;

			while (iter.hasNext()){
				Object obj=iter.next();

				if(this.type.compareTo("Agent")==0){
					idOfObjInDB = ((Agent)obj).getAgentId();
					idOfObjOfIS = ((Agent)obj).getAgentIdOfIS();
				}
				else if(this.type.compareTo("DataSource")==0){
					idOfObjInDB = ((DataSource)obj).getDataSourceId();
					idOfObjOfIS = ((DataSource)obj).getDataSourceIdOfIS();
				}
				else if(this.type.compareTo("DataStorage")==0){
					idOfObjInDB = ((DataStorage)obj).getDataStorageId();
					idOfObjOfIS = ((DataStorage)obj).getDataStorageIdOfIS();
				}
				else logger.debug("ISManager - updateObjsInDB - Error - there is no class with this name");

				if(idOfObjInDB.endsWith("datastorageSM")){ //continue because this has to do with the storage manager
					continue;
				}
				
				if(this.checkIfObjExistsInIS_ById(idOfObjOfIS)){
					//exists in IS so we proceed ..
					continue;
				}
				else{
					//exists in DB but it does not exist anymore in IS so we delete it ..
					Long number=(long) 0;
					try{
						if(this.type.compareTo("Agent")==0){
							// Deleting agent  
							Query q = this.dbManager.getPersistenceManager().newQuery(Agent.class);
							q.setFilter("agentId == \""+idOfObjInDB+"\"");
							number= (Long) q.deletePersistentAll();
						}
						else if(this.type.compareTo("DataSource")==0){
							// Deleting DataSource  
							Query q = this.dbManager.getPersistenceManager().newQuery(DataSource.class);
							q.setFilter("dataSourceId == \""+idOfObjInDB+"\"");					
							number= (Long) q.deletePersistentAll();
						}
						else if(this.type.compareTo("DataStorage")==0){
							// Deleting DataSource  
							Query q = this.dbManager.getPersistenceManager().newQuery(DataStorage.class);
							q.setFilter("dataStorageId == \""+idOfObjInDB+"\"");	
							number= (Long) q.deletePersistentAll();
						}
					}catch (Exception e){
						logger.error("ISManager - updateObjsInDB - Error in deleting the existed obj in DB - idOfObjInDB="+idOfObjInDB); 
						e.printStackTrace();
						return ;
					}
					
					oldContentInDB++;
				}		
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
		if(this.type.compareTo("DataSource")==0)typeOfISManager=new String("for DataSources");
		else if(this.type.compareTo("Agent")==0)typeOfISManager=new String("for Agents");
		else if(this.type.compareTo("DataStorage")==0)typeOfISManager=new String("for DataStorages");
		
		logger.debug("ISManager.updateObjsInDB ("+typeOfISManager+"):\nnew UP-Nodes For Storing in DB="+newContentInDB+" - Nodes that do not exist anymore in IS="+oldContentInDB);
	}	


	/*
	 * getObjsFromIS
	 * input: Nothing
	 * return: List<String> of Objects (either Agents or Sources or Storages) 
	 * for storing them in the DB
	 */
	public List<String> getObjsFromIS(){

		List<String> agentsFromIS=new ArrayList<String>();
		List<String> dataSourcesFromIS=new ArrayList<String>();
		List<String> dataStoragesFromIS=new ArrayList<String>();

		if (this.type.compareTo("Agent")==0){
			try{
				ISClient client = GHNContext.getImplementation(ISClient.class);
				GCUBERIQuery RIquery = client.getQuery(GCUBERIQuery.class);
				RIquery.addAtomicConditions(new AtomicCondition("//ServiceName","agent-service"));
				//logger.debug("ISManager.getObjsFromIS - Printing all the received agents from IS");
				for (GCUBERunningInstance instance : client.execute(RIquery,GCUBEScope.getScope(this.scope))){
					String tmpAgent=new String();
					tmpAgent=instance.getID()+
							"--"+instance.getServiceName()+
							"--"+instance.getAccessPoint().getEndpoint("gcube/datatransfer/agent/DataTransferAgent").getAddress().getHost()+
							"--"+instance.getAccessPoint().getEndpoint("gcube/datatransfer/agent/DataTransferAgent").getAddress().getPort()+
							"--" ;

					//logger.debug("      tmpAgent="+tmpAgent);
					agentsFromIS.add(tmpAgent);
				}
			}catch(RuntimeException e){
				logger.error("ISManager.getObjsFromIS(agents) - RuntimeException....");
				e.printStackTrace();
			}catch(Exception e){
				logger.error("ISManager.getObjsFromIS(agents) - exception....");
				e.printStackTrace();
			}
			return agentsFromIS;
		}
		else if(this.type.compareTo("DataSource")==0){
			try{
				ISClient client = GHNContext.getImplementation(ISClient.class);
				GCUBERuntimeResourceQuery RRquery = client.getQuery(GCUBERuntimeResourceQuery.class);
				RRquery.addAtomicConditions(new AtomicCondition("//Category","DataSource"));
				for (GCUBERuntimeResource resource : client.execute(RRquery,GCUBEScope.getScope(scope))){
					String tmpDataSource="";
					tmpDataSource=tmpDataSource.concat(resource.getID()+"--" +
							resource.getName()+"--" +
							resource.getDescription()+"--" +
							resource.getAccessPoints().get(0).getEndpoint()+"--" +
							resource.getAccessPoints().get(0).getUsername()+"--" +
							resource.getAccessPoints().get(0).getPassword()+"--" +
							resource.getAccessPoints().get(0).getProperty("folder")+"--" );

					//tmpDataSource structure: 
					// resultIdOfIS--name--description--endpoint--username--password--folder
					dataSourcesFromIS.add(tmpDataSource);					
				}
			}catch(RuntimeException e){
				logger.error("ISManager.getObjsFromIS(datasources) - RuntimeException....");
				e.printStackTrace();

			}catch(Exception e){
				logger.error("ISManager.getObjsFromIS(datasources) - exception....");
				e.printStackTrace();
			}
			return dataSourcesFromIS;
		}
		else if(this.type.compareTo("DataStorage")==0){
			try{
				ISClient client = GHNContext.getImplementation(ISClient.class);
				GCUBERuntimeResourceQuery RRquery = client.getQuery(GCUBERuntimeResourceQuery.class);
				RRquery.addAtomicConditions(new AtomicCondition("//Category","DataStorage"));
				for (GCUBERuntimeResource resource : client.execute(RRquery,GCUBEScope.getScope(scope))){
					String tmpDataStorage="";
					tmpDataStorage=tmpDataStorage.concat(resource.getID()+"--" +
							resource.getName()+"--" +
							resource.getDescription()+"--" +
							resource.getAccessPoints().get(0).getEndpoint()+"--" +
							resource.getAccessPoints().get(0).getUsername()+"--" +
							resource.getAccessPoints().get(0).getPassword()+"--" +
							//resource.getAccessPoints().get(0).getProperty("folder")+
							"--" );

					//tmpDataSource structure: 
					// resultIdOfIS--name--description--endpoint--username--password--
					dataStoragesFromIS.add(tmpDataStorage);					
				}
			}catch(RuntimeException e){
				logger.error("ISManager.getObjsFromIS(datasources) - RuntimeException....");
				e.printStackTrace();

			}catch(Exception e){
				logger.error("ISManager.getObjsFromIS(datasources) - exception....");
				e.printStackTrace();
			}
			return dataStoragesFromIS;
		}

		return null;
	}

	/*
	 * checkIfObjExistsInIS_ById
	 * input: String with the objId of IS
	 * return: boolean - if exists in IS returns true, in other case false
	 */
	public boolean checkIfObjExistsInIS_ById(String id){

		if (this.type.compareTo("Agent")==0){
			try{
				ISClient client = GHNContext.getImplementation(ISClient.class);
				GCUBERIQuery RIquery = client.getQuery(GCUBERIQuery.class);
				RIquery.addAtomicConditions(new AtomicCondition("//ServiceName","agent-service"));
				for (GCUBERunningInstance instance : client.execute(RIquery,GCUBEScope.getScope(this.scope))){
					String instanceId=instance.getID();
					if(instanceId.compareTo(id)==0){
						return true;
					}
				}
			}catch(Exception e){
				logger.error("ISManager.checkIfObjExistsInIS_ById - exception");
				e.printStackTrace();
			}
			return false;
		}	
		else if(this.type.compareTo("DataSource")==0){
			try{
				ISClient client = GHNContext.getImplementation(ISClient.class);
				GCUBERuntimeResourceQuery RRquery = client.getQuery(GCUBERuntimeResourceQuery.class);
				RRquery.addAtomicConditions(new AtomicCondition("//Category","DataSource"));
				for (GCUBERuntimeResource resource : client.execute(RRquery,GCUBEScope.getScope(scope))){
					String resourceId=resource.getID();
					if(resourceId.compareTo(id)==0){
						return true;
					}
				}
			}catch(Exception e){
				logger.error("ISManager.checkIfObjExistsInIS_ById - exception");
				e.printStackTrace();
			}
			return false;
		}
		else if(this.type.compareTo("DataStorage")==0){
			try{
				ISClient client = GHNContext.getImplementation(ISClient.class);
				GCUBERuntimeResourceQuery RRquery = client.getQuery(GCUBERuntimeResourceQuery.class);
				RRquery.addAtomicConditions(new AtomicCondition("//Category","DataStorage"));
				for (GCUBERuntimeResource resource : client.execute(RRquery,GCUBEScope.getScope(scope))){
					String resourceId=resource.getID();
					if(resourceId.compareTo(id)==0){
						return true;
					}
				}
			}catch(Exception e){
				logger.error("ISManager.checkIfObjExistsInIS_ById - exception");
				e.printStackTrace();
			}
			return false;		}
		return false;
	}

	/*
	 * checkIfObjExistsInDB_ById
	 * input: String with the objId of IS
	 * return: String - if exists in DB returns the objId of DB, in other case null
	 */
	public String checkIfObjExistsInDB_ById(String id){	

		if (this.type.compareTo("Agent")==0){
			try{
				Extent<?> agentExtent = this.dbManager.getPersistenceManager().getExtent(Agent.class, true);
				Iterator<?> iter = agentExtent.iterator();

				while (iter.hasNext()){
					Object obj=iter.next();
					String tmpId = ((Agent)obj).getAgentIdOfIS();
					if(tmpId==null)continue;
					if(tmpId.compareTo(id)==0){
						return ((Agent)obj).getAgentId();
					}
				}
			}catch(Exception e){
				logger.error("ISManager.checkIfObjExistsInDB_ById(for agents) - exception");
				e.printStackTrace();
			}		
			return null;
		}	
		else if(this.type.compareTo("DataSource")==0){
			try{
				Extent<?> sourceExtent = this.dbManager.getPersistenceManager().getExtent(DataSource.class, true);
				Iterator<?> iter = sourceExtent.iterator();

				while (iter.hasNext()){
					Object obj=iter.next();
					String tmpId = ((DataSource)obj).getDataSourceIdOfIS();
					if(tmpId==null)continue;
					if(tmpId.compareTo(id)==0){
						return ((DataSource)obj).getDataSourceId();
					}
				}
			}catch(Exception e){
				logger.error("ISManager.checkIfObjExistsInDB_ById(for datasources) - exception");
				e.printStackTrace();
			}		
			return null;
		}
		else if(this.type.compareTo("DataStorage")==0){
			try{
				Extent<?> sourceExtent = this.dbManager.getPersistenceManager().getExtent(DataStorage.class, true);
				Iterator<?> iter = sourceExtent.iterator();

				while (iter.hasNext()){
					Object obj=iter.next();
					String tmpId = ((DataStorage)obj).getDataStorageIdOfIS();
					if(tmpId==null)continue;
					if(tmpId.compareTo(id)==0){
						return ((DataStorage)obj).getDataStorageId();
					}
				}
			}catch(Exception e){
				logger.error("ISManager.checkIfObjExistsInDB_ById(for datastorages) - exception");
				e.printStackTrace();
			}		
			return null;
		}
		return null;
	}

	/*
	 * checkIfObjExistsInDB_ByHostname
	 * input: String with the host name
	 * return: String - if exists in DB returns the id of DB, in other case null
	 */
	public String checkIfObjExistsInDB_ByHostname(String hostname){	

		if (this.type.compareTo("Agent")==0){
			try{
				Extent<?> agentExtent = this.dbManager.getPersistenceManager().getExtent(Agent.class, true);
				Iterator<?> iter = agentExtent.iterator();

				while (iter.hasNext()){
					Object obj=iter.next();
					String host = ((Agent)obj).getHost();
					if(host.compareTo(hostname)==0){
						return ((Agent)obj).getAgentId();
					}
				}
			}catch(Exception e){
				logger.error("ISManager.checkIfObjExistsInDB_ByHostname(for agents) - exception");
				e.printStackTrace();
			}		
			return null;
		}	
		else if(this.type.compareTo("DataSource")==0){
			try{
				Extent<?> sourceExtent = this.dbManager.getPersistenceManager().getExtent(DataSource.class, true);
				Iterator<?> iter = sourceExtent.iterator();

				while (iter.hasNext()){
					Object obj=iter.next();
					String host = ((DataSource)obj).getEndpoint();
					if(host.compareTo(hostname)==0){
						return ((DataSource)obj).getDataSourceId();
					}
				}
			}catch(Exception e){
				logger.error("ISManager.checkIfObjExistsInDB_ByHostname(for datasources) - exception");
				e.printStackTrace();
			}		
			return null;
		}
		else if(this.type.compareTo("DataStorage")==0){
			return null;
		}
		return null;
	}
	
	public void updateSpecificObjInDB(String[] input, String id){
		if (this.type.compareTo("Agent")==0){
			//the input for the agent contains this info : 
			//resultIdOfIS--service--host--port
			if(input.length<4)logger.debug("ISManager.updateSpecificObjInDB - Error - the given string for Agent has less than 4 parameters");
			String agentIdOfIs=input[0];
			String host=input[2];
			int port=Integer.valueOf(input[3]);
			try {// *** updating the specific Agent in DB ***
				this.dbManager.updateEverythingInAgent(id,agentIdOfIs,host,port);
			} catch (Exception e) {
				logger.error("ISManager.updateSpecificObjInDB - Exception in updating the specific Agent with id="+id);
				e.printStackTrace();
				return;
			}
		}
		else if (this.type.compareTo("DataSource")==0){
			///the input for the data source contains this info : 
			// resultIdOfIS--name--description--endpoint--username--password--folder
			if(input.length<7)logger.debug("ISManager.updateSpecificObjInDB - Error - the given string for DataSource has less than 6 parameters");
			String dataSourceIdOfIS=input[0];
			String dataSourceName = input[1];
			String description = input[2];
			String endpoint = input[3];
			String username = input[4];
			String pass= input[5];
			String folder = input[6];
			
			try {// *** update the specific Datasource in DB ***
				this.dbManager.updateEverythingInSource(id, dataSourceIdOfIS, dataSourceName,description,endpoint,username,pass,folder);
			} catch (Exception e) {
				logger.error("ISManager.updateSpecificObjInDB - Exception in updating the specific DataSource with id="+id);
				e.printStackTrace();
				return ;
			}
		}
		else if (this.type.compareTo("DataStorage")==0){
			//update ... 
			///the input for the data storage contains this info : 
			// resultIdOfIS--name--description--endpoint--username--password--
			if(input.length<6)logger.debug("ISManager.updateSpecificObjInDB - Error - the given string for DataStorage has less than 6 parameters");
			String dataStorageIdOfIS=input[0];
			String dataStorageName = input[1];
			String description = input[2];
			String endpoint = input[3];
			String username = input[4];
			String pass= input[5];
			//String folder = input[6];
			
			try {// *** update the specific Datastorage in DB ***
				this.dbManager.updateEverythingInStorage(id, dataStorageIdOfIS, dataStorageName,description,endpoint,username,pass);
			} catch (Exception e) {
				logger.error("ISManager.updateSpecificObjInDB - Exception in updating the specific DataStorage with id="+id);
				e.printStackTrace();
				return ;
			}
		}
		return;
	}
	/*
	 * setObjToDB
	 * input: String with the information being related to the specific obj
	 * input: In case of updating there is also the id input which is the same id of obj that it had before been deleted
	 * return: String with the objId of DB, in other case null
	 */
	public String setObjToDB(String[] input){
		String id = uuidgen.nextUUID();		
		
		if (this.type.compareTo("Agent")==0){
			//the input for the agent contains this info : 
			//resultIdOfIS--service--host--port
			if(input.length<4){
				//logger.debug("ISManager.setObjToDB - Error - the given string for Agent has less than 4 parameters");
				return "lessParameters";
			}

			Agent agentDB=new Agent();
			agentDB.setAgentId(id);
			agentDB.setAgentIdOfIS(input[0]);
			agentDB.setHost(input[2]);
			agentDB.setPort(Integer.valueOf(input[3]));
			try {// *** store the Agent in DB ***
				this.dbManager.storeAgent(agentDB);
			} catch (Exception e) {
				logger.error("ISManager.setObjToDB - Exception in storing the Agent");
				e.printStackTrace();
				return null;
			}
			return id;
		}
		else if (this.type.compareTo("DataSource")==0){
			///the input for the data source contains this info : 
			// resultIdOfIS--name--description--endpoint--username--password--folder
			if(input.length<7){
				//logger.debug("ISManager.setObjToDB - Error - the given string for DataSource has less than 6 parameters");
				return "lessParameters";
			}

			DataSource dataSource =new DataSource();
			dataSource.setDataSourceId(id);
			dataSource.setDataSourceIdOfIS(input[0]);
			dataSource.setDataSourceName(input[1]);
			dataSource.setDescription(input[2]);
			dataSource.setEndpoint(input[3]);
			dataSource.setUsername(input[4]);
			dataSource.setPass(input[5]);
			dataSource.setFolder(input[6]);
			
			try {// *** store the Agent in DB ***
				this.dbManager.storeSource(dataSource);
			} catch (Exception e) {
				logger.error("ISManager.setObjToDB - Exception in storing the DataSource");
				e.printStackTrace();
				return null;
			}
			return id;
		}
		else if (this.type.compareTo("DataStorage")==0){
			DataStorage storageDB = new DataStorage();
			///the input for the data source contains this info : 
			// resultIdOfIS--name--description--endpoint--username--password--
			if(input.length<6){
				//logger.debug("ISManager.setObjToDB - Error - the given string for DataSource has less than 6 parameters");
				return "lessParameters";
			}

			storageDB.setType("RemoteNode");
			storageDB.setDataStorageId(id);
			storageDB.setDataStorageIdOfIS(input[0]);
			storageDB.setDataStorageName(input[1]);
			storageDB.setDescription(input[2]);
			storageDB.setEndpoint(input[3]);
			storageDB.setUsername(input[4]);
			storageDB.setPass(input[5]);
						
			try {// *** store the storage in DB ***
				this.dbManager.storeStorage(storageDB);
			} catch (Exception e) {
				logger.error("ISManager.setObjToDB - Exception in storing the DataStorage");
				e.printStackTrace();
				return null;
			}
			return id;
		}
		return null;		

	}


	/*
	 * checkIfObjExistsInIS_ByHostname
	 * input: String with the host of the Object (either Agent or Source or Storage)
	 * return: String - if exists in IS returns the objId of IS, in other case null
	 */
	public String checkIfObjExistsInIS_ByHostname(String host){

		if (this.type.compareTo("Agent")==0){
			//logger.debug("ISManager.checkIfObjExistsInIS_ByHostname - Its an agent");

			try{
				ISClient client = GHNContext.getImplementation(ISClient.class);
				GCUBERIQuery RIquery = client.getQuery(GCUBERIQuery.class);
				RIquery.addAtomicConditions(new AtomicCondition("//ServiceName","agent-service"));
				for (GCUBERunningInstance instance : client.execute(RIquery,GCUBEScope.getScope(this.scope))){
					//logger.debug("ISManager.checkIfObjExistsInIS_ByHostname - host="+instance.getAccessPoint().getEndpoint("gcube/datatransfer/agent/DataTransferAgent").getAddress().getHost()+" - diko mas host="+host);
					if(instance.getAccessPoint().getEndpoint("gcube/datatransfer/agent/DataTransferAgent").getAddress().getHost().compareTo(host)==0){
						return instance.getID();
					}
				}
			}catch(Exception e){
				logger.error("ISManager.checkIfObjExistsInIS_ByHostname - exception");
				e.printStackTrace();
			}
			return null;	
		}
		else if (this.type.compareTo("DataSource")==0){
			DataSource sourceDB = new DataSource();
			return null;
		}
		else if (this.type.compareTo("DataStorage")==0){
			DataStorage storageDB = new DataStorage();
			return null;
		}
		return null;
	}


}
