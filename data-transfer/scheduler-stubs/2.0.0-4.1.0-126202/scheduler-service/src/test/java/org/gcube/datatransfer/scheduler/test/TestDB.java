package org.gcube.datatransfer.scheduler.test;

import java.util.Iterator;

import javax.jdo.Extent;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.Transfer;
import org.gcube.datatransfer.scheduler.impl.state.SchedulerResource;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestDB {
	private static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

	private static DataTransferDBManager dbManager;

	public static void main (String [] args){
		
		String transferId=uuidgen.nextUUID();
		dbManager=new DataTransferDBManager();
		SchedulerResource res = new SchedulerResource();
		res.setNumOfActiveTransfers("567");
		
		//create transfer1
		Transfer transfer = new Transfer();
		String agentid1 = null;
		try{
			Agent agent=new Agent();
			agent.setAgentId(transferId.concat("-agent"));

			System.out.println("TestDB - Setting Transfer Info1");
			transfer.setTransferId(transferId);
			//transfer.setAgent(agent);
			transfer.setSubmitter("test_submitter");
			transfer.setAgentId(agent.getAgentId());
			System.out.println("TestDB - Storing Transfer Info1"); 
			agentid1=agent.getAgentId();
			dbManager.storeTransfer(transfer);
		}catch (Exception e){
			e.printStackTrace();
		}

		//create transfer 2
		Transfer transfer2 = new Transfer();
		try{		
			System.out.println("TestDB - Setting Transfer Info2");
			transfer2.setTransferId(transferId.concat("-2"));
			//transfer2.setAgent(agent);
			transfer2.setSubmitter("test_submitter2");

			System.out.println("TestDB - Storing Transfer Info2"); 
			dbManager.storeTransfer(transfer2);
		}catch (Exception e){
			System.out.println("TestDB - Exception in storing Transfer Info2"); 
			e.printStackTrace();
		}

		//retrieving all the transfers
		try{
			Extent<?> transferExtent = dbManager.getPersistenceManager().getExtent(Transfer.class, true);
			Iterator<?> iter = transferExtent.iterator();
			System.out.println("\nTESTIS - - Retrieve all the Transfers by method:\n");

			while (iter.hasNext()){
			Object obj=iter.next();
			System.out.println("Transferid="+((Transfer)obj).getTransferId()+
					" - Submitter="+((Transfer)obj).getSubmitter()+" - agentid="+((Transfer)obj).getAgentId());
			}
		}catch(Exception e){
			System.out.println("TestDB - Exception in retrieving all the Transfers"); 
			e.printStackTrace();
		}
	}

}
