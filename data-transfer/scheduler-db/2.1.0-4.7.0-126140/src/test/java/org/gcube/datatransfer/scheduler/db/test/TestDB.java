package org.gcube.datatransfer.scheduler.db.test;



import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.Query;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.DataStorage;
import org.gcube.datatransfer.scheduler.db.model.Transfer;
import org.gcube.datatransfer.scheduler.db.model.TransferObject;



public class TestDB {

	/** The UUIDGen */
	private static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();
	static GCUBELog logger = new GCUBELog(TestDB.class);

	private static DataTransferDBManager dbManager;

	public static void main (String [] args){
		
		String transferId=uuidgen.nextUUID();
		dbManager=new DataTransferDBManager();

	/*	//create transfer object1
		Set<TransferObject> transferObjects=new HashSet<TransferObject>();
		TransferObject transferObj = new TransferObject();
		try{		
			logger.debug("TestDB - Setting Transfer Info2");
			transferObj.setObjectId(transferId.concat("-obj"));
			//transfer2.setAgent(agent);
			transferObj.setSrcURI("file:/kjfakljfa");
			transferObjects.add(transferObj);
			logger.debug("TestDB - Storing Transfer Info2"); 
			dbManager.storeTransferObject(transferObjects);
		}catch (Exception e){
			logger.error("TestDB - Exception in storing TransferObj Info2"); 
			e.printStackTrace();
		}
		
		transferObjects=new HashSet<TransferObject>();
		TransferObject obj = new TransferObject();
		TransferObject obj2 = new TransferObject();

		try {
			obj.setSrcURI("http://upload.wikimedia.org/wikipedia/commons/6/6e/Wikipedia_logo_silver.png");
			obj.setObjectId("1-8989");
			obj.setTransferid("test");
			obj2.setObjectId("2-8989");

			obj2.setSrcURI("http://upload.wikimedia.org/wikipedia/commons/c/c4/Kalamata%2C_Peloponnese%2C_Greece.jpg");
			obj2.setTransferid("test");
		} catch (Exception e) {
			e.printStackTrace();
		}
		transferObjects.add(obj);
		transferObjects.add(obj2);

		try	{
			dbManager.storeTransferObject(transferObjects);
		}
		catch(Exception e){
			System.out.println("Exception in storing the Set of Transfer Objects:\n");
			e.printStackTrace();
			return;
		}
		
		*/
		/*
		//retrieving again the transfers
		try{
			Extent<?> transferExtent = dbManager.getPersistenceManager().getExtent(TransferObject.class, true);
			Iterator<?> iter = transferExtent.iterator();
			logger.debug("\nTESTIS - - Retrieve all the Transfers Obj by method\n");

			while (iter.hasNext()){
				Object obj=iter.next();
				logger.debug("Objid="+((TransferObject)obj).getObjectId()+
						" - Uri="+((TransferObject)obj).getURI().toString());		
			}
		}catch(Exception e){
			logger.error("TestDB - Exception in retrieving all the TransfersObjs"); 
			e.printStackTrace();
		}
*/
		
		
		//create transfer1
		Transfer transfer = new Transfer();
		String agentid1 = null;
		try{
			Agent agent=new Agent();
			agent.setAgentId(transferId.concat("-agent"));

			logger.debug("TestDB - Setting Transfer Info1");
			transfer.setTransferId(transferId);
			//transfer.setAgent(agent);
			transfer.setSubmitter("test_submitter");
			transfer.setAgentId(agent.getAgentId());
			logger.debug("TestDB - Storing Transfer Info1"); 
			agentid1=agent.getAgentId();
			dbManager.storeTransfer(transfer);
		}catch (Exception e){
			e.printStackTrace();
		}

		//create transfer 2
		Transfer transfer2 = new Transfer();
		try{		
			logger.debug("TestDB - Setting Transfer Info2");
			transfer2.setTransferId(transferId.concat("-2"));
			//transfer2.setAgent(agent);
			transfer2.setSubmitter("test_submitter2");

			logger.debug("TestDB - Storing Transfer Info2"); 
			dbManager.storeTransfer(transfer2);
		}catch (Exception e){
			logger.error("TestDB - Exception in storing Transfer Info2"); 
			e.printStackTrace();
		}

		//retrieving all the transfers
		try{
			Extent<?> transferExtent = dbManager.getPersistenceManager().getExtent(Transfer.class, true);
			Iterator<?> iter = transferExtent.iterator();
			logger.debug("\nTESTIS - - Retrieve all the Transfers by method:\n");

			while (iter.hasNext()){
			Object obj=iter.next();
			logger.debug("Transferid="+((Transfer)obj).getTransferId()+
					" - Submitter="+((Transfer)obj).getSubmitter()+" - agentid="+((Transfer)obj).getAgentId());
			}
		}catch(Exception e){
			logger.error("TestDB - Exception in retrieving all the Transfers"); 
			e.printStackTrace();
		}
/*

		//deleting Agent
		Long number=(long) 0;
		try{
			logger.debug("TestDB - Deleting Agent Info1"); 
			Query q = dbManager.getPersistenceManager().newQuery(Agent.class);
			q.setFilter("agentId == \""+agentid1+"\"");
			number= (Long) q.deletePersistentAll();
			logger.debug("TestDB - long number for deleting agent="+number); 
		}catch (Exception e){
			logger.error("TestDB - Exception in Deleting agent Info1"); 
			e.printStackTrace();
		}

		try {
			dbManager.updateAgentInTransfer(transferId, null);
		} catch (Exception e1) {
			logger.error("TestDB - Exception in updating agent Info in transfer"); 

			e1.printStackTrace();
		}


		//retrieving again the transfers
		try{
			Extent<?> transferExtent = dbManager.getPersistenceManager().getExtent(Transfer.class, true);
			Iterator<?> iter = transferExtent.iterator();
			logger.debug("\nTESTIS - - Retrieve all the Transfers by method after deleting the first:\n");

			while (iter.hasNext()){
			Object obj=iter.next();
			logger.debug("Transferid="+((Transfer)obj).getTransferId()+
					" - Submitter="+((Transfer)obj).getSubmitter()+" - agentid="+((Transfer)obj).getAgentId());		
			}
		}catch(Exception e){
			logger.error("TestDB - Exception in retrieving all the Transfers"); 
			e.printStackTrace();
		}
		 */

	}
	
	
}


