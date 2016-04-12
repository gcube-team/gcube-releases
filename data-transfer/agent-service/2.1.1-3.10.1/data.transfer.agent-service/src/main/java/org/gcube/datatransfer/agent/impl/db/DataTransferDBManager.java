package org.gcube.datatransfer.agent.impl.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.grs.GRSOutComeWriter;
import org.gcube.datatransfer.agent.impl.jdo.Transfer;
import org.gcube.datatransfer.agent.impl.jdo.TransferObject;
import org.gcube.datatransfer.agent.stubs.datatransferagent.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.grs.FileOutcomeRecord.Outcome;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.datatransfer.common.outcome.TreeTransferOutcome;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class DataTransferDBManager  extends DBManager implements Runnable {

	GCUBELog logger = new GCUBELog(DataTransferDBManager.class);

	/**
	 * Default constructor
	 */
	public DataTransferDBManager() throws Exception{

		dbFileBaseFolder =(String)ServiceContext.getContext().getPersistenceRoot().getAbsolutePath() + File.separator + "DataTransferDB"; 
		dbName = "data_transfer";
		dbFileName =  dbFileBaseFolder +File.separator+ dbName+".db";

		backupFolder = new File ((System.getenv("HOME") + File.separator + "DataTransferDBBackup"));

		Properties prop = new Properties();

		//getting datanucleus conf file from JNDI
		String propFile = GHNContext.getContext().getLocation() + File.separator +
				ServiceContext.getContext().getProperty("configDir", true) + File.separator + 
				ServiceContext.getContext().getDbConfigurationFileName();

		try {
			prop.load(new FileInputStream(new File(propFile)));
		} catch (IOException e) {
			e.printStackTrace();

		}

		persistenceFactory =JDOHelper.getPersistenceManagerFactory(prop);

		Thread t = new Thread(this);
		t.start();

	}

	/**
	 * 
	 * @param Transfer
	 * @param transferObjects
	 * @throws Exception
	 */
	public boolean checkIfTransferExist(String id) throws Exception{
		try{
			PersistenceManager persistenceManager = getPersistenceManager();
			Query query = persistenceManager.newQuery(Transfer.class);
			query.setFilter("id == \""+id+"\"");
			List<Transfer> list = (List<Transfer>)query.execute();
			if(list==null || list.size()==0){
				return false;
			}
			else return true;
		}catch (Exception e)
		{
			e.printStackTrace();	
			throw e;
		}
	}

	public void storeTransfer(Transfer transfer) throws Exception{
		//storing transfer Main object
		PersistenceManager persistenceManager = getPersistenceManager();
		try
		{
			persistenceManager.currentTransaction().begin();
			persistenceManager.makePersistent(transfer);
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
			e.printStackTrace();	
			throw e;
		}
		finally{
			if (persistenceManager.currentTransaction().isActive()) 
				persistenceManager.currentTransaction().rollback();
			persistenceManager.close();
		}

	}
	public void updateTransfer(String id) throws Exception{
		//updating transfer
		PersistenceManager persistenceManager = getPersistenceManager();
		try{
			Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,id);
			t.setStatus(TransferStatus.QUEUED.name());
			t.setSubmitter("N/A");
			t.setTransfersCompleted(0);
			t.setTotalSize(0);
			t.setSizeTransferred(0);
			t.setTotalTransfers(0);

			persistenceManager.makePersistent(t);
			persistenceManager.close();		
		}catch (Exception e){
			e.printStackTrace();	
			throw e;
		}
	}

	public void storeTransferObject (Set<TransferObject> transferObjects) throws Exception{

		PersistenceManager persistenceManager = getPersistenceManager();

		try {

			for (TransferObject obj : transferObjects){
				persistenceManager.currentTransaction().begin();
				persistenceManager.makePersistent(obj);
				persistenceManager.currentTransaction().commit();
			}
		}
		catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if (persistenceManager.currentTransaction().isActive()) 
				persistenceManager.currentTransaction().rollback();
			persistenceManager.close();
		}

	}
	public void updateTransferSubmitterEndpoint(String transferId,String submitterEndpoint) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
		t.setSubmitterEndpoint(submitterEndpoint);
		persistenceManager.makePersistent(t);
		persistenceManager.close();
	}
	public void updateLastNotificationMsgSent(String transferId,boolean value) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
		t.setLastNotificationMsgSent(value);
		persistenceManager.makePersistent(t);
		persistenceManager.close();
	}
	public String getTransferStatus(String transferId) throws Exception {

		PersistenceManager persistenceManager = getPersistenceManager();

		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
		persistenceManager.close();

		return t.getStatus();
	}


	public void updateTransferObjectStatus(String objId,String status) throws Exception{

		PersistenceManager persistenceManager= getPersistenceManager();
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,objId);
		logger.debug("Old status " + t.getStatus());
		t.setStatus(status);
		logger.debug("New Status " + t.getStatus());
		persistenceManager.makePersistent(t);
		persistenceManager.close();
	}
	public void updateReadTreesInTransfer(String transferId,int val) throws Exception{

		PersistenceManager persistenceManager= getPersistenceManager();
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
		t.setTotalReadTrees(val);
		persistenceManager.makePersistent(t);
		persistenceManager.close();
	}
	public void updateWrittenTreesInTransfer(String transferId,int val) throws Exception{

		PersistenceManager persistenceManager= getPersistenceManager();
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
		t.setTotalWrittenTrees(val);
		persistenceManager.makePersistent(t);
		persistenceManager.close();
	}

	public void updateTransferObjectInfo(String objId,long size) throws Exception{

		PersistenceManager persistenceManager= getPersistenceManager();
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,objId);
		//logger.debug("Old byteTransferred" + t.getSizeTransferred());
		t.setSizeTransferred(t.getSizeTransferred()+size);
		//t.setTransfersCompleted(t.getTransfersCompleted()+1);
		//logger.debug("New byteTransferred" + t.getSizeTransferred());
		persistenceManager.makePersistent(t);
		persistenceManager.close();
	}

	public void addTransferObjectCompleted(String objId) throws Exception{

		PersistenceManager persistenceManager= getPersistenceManager();
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,objId);
		logger.debug("Old completed" + t.getTransfersCompleted());
		t.setTransfersCompleted(t.getTransfersCompleted()+1);
		logger.debug("New completed" + t.getTransfersCompleted());
		persistenceManager.makePersistent(t);
		persistenceManager.close();
	}

	public void updateTransferJDO(String transferId, String[] inputURIs, long totalsize) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);

		t.setId(transferId);
		t.setStatus(TransferStatus.STARTED.name());
		t.setSubmitter("N/A");
		t.setTransfersCompleted(0);
		t.setTotalSize(totalsize);
		t.setTransfersCompleted(0);
		t.setTotalTransfers(inputURIs.length);
		persistenceManager.makePersistent(t);
		persistenceManager.close();
	}


	/**
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		do {
			try {
				Thread.sleep(backupIntervalMS);
				this.backup();
			} catch (InterruptedException e) {
				logger.error("Unable to sleep", e);
			} catch (Exception e) {
				logger.error("Unable to backup", e);
			}
		} while (! Thread.interrupted());

	}

	public PersistenceManager getPersistenceManager(){
		return persistenceFactory.getPersistenceManager();
	}

	public String getTransferObjectOutComeAsRS(String transferId) throws Exception {

		GRSOutComeWriter outcomeWriter = null;

		PersistenceManager persistenceManager= getPersistenceManager();
		if(!checkIfTransferExist(transferId)){
			logger.debug("getTransferObjectOutComeAsRS - transfer with id="+transferId+" does not exist");
			return null;			
		}
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
		boolean isItForTrees=false;
		if(t.getSourceID()!=null && t.getDestID()!=null)isItForTrees=true;


		if (!(TransferStatus.valueOf(t.getStatus()).hasCompleted())){
			if(isItForTrees){
				outcomeWriter = new GRSOutComeWriter(1,true);
				outcomeWriter.putField(transferId,"",new Long(0),new Long(0),new Long(0),new Exception("The submitted transfer is not yet completed"));

			}
			else{
				outcomeWriter = new GRSOutComeWriter(1,false);
				outcomeWriter.putField(transferId,"",0,0,"",new Exception("The submitted transfer is not yet completed"));
			}
		}
		else{

			if(isItForTrees){
				outcomeWriter = new GRSOutComeWriter(1,true);
				if (t.getStatus().compareTo(TransferStatus.FAILED.name())==0)
					outcomeWriter.putField(t.getSourceID(),t.getDestID(),t.getTotalReadTrees(),t.getTotalWrittenTrees(),t.getStatus(),new Exception (t.getStatus()));
				else outcomeWriter.putField(t.getSourceID(),t.getDestID(),t.getTotalReadTrees(),t.getTotalWrittenTrees(),t.getStatus());
			}
			else{
				Query query = persistenceManager.newQuery(TransferObject.class);
				query.setFilter("transferID == \""+transferId+"\"");

				List<TransferObject> list = (List<TransferObject>)query.execute();

				if (list == null || list.size()==0) 
					throw new Exception("The Transfer Objects list is empty");

				logger.debug("Getting " + list.size() +" outcomes");
				outcomeWriter = new GRSOutComeWriter(list.size(),false);

				for (TransferObject obj :list){			
					long bytesThatHaveBeenTransferred=0;
					if(obj.getBytesOfObjTransferred()==null && obj.getSize()!=null)bytesThatHaveBeenTransferred=obj.getSize();
					else if(obj.getBytesOfObjTransferred()!=null){
						bytesThatHaveBeenTransferred=obj.getBytesOfObjTransferred();
					}

					long total_size=0;
					if(obj.getSize()!=null)total_size = obj.getSize();

					if (obj.getStatus().compareTo(TransferStatus.FAILED.name())==0)
						outcomeWriter.putField(obj.getSourceURI(),obj.getDestURI(),obj.getTransferTime(),bytesThatHaveBeenTransferred,total_size,new Exception (obj.getOutcome()));
					else outcomeWriter.putField(obj.getSourceURI(),obj.getDestURI(),obj.getTransferTime(),bytesThatHaveBeenTransferred,total_size);
					persistenceManager.deletePersistent(obj);
				}
			}
		}

		persistenceManager.close();
		return outcomeWriter.writer.getLocator().toString();
	}

	public ArrayList<FileTransferOutcome> getTransferObjectOutCome(String transferId) throws Exception {

		ArrayList<FileTransferOutcome> outcomes = new ArrayList<FileTransferOutcome>();
		PersistenceManager persistenceManager= getPersistenceManager();

		if(!checkIfTransferExist(transferId)){
			logger.debug("getTransferObjectOutCome - transfer with id="+transferId+" does not exist");
			return null;			
		}
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);

		if (!(TransferStatus.valueOf(t.getStatus()).hasCompleted())){
			return null;
		}

		Query query = persistenceManager.newQuery(TransferObject.class);
		query.setFilter("transferID == \""+transferId+"\"");

		List<TransferObject> list = (List<TransferObject>)query.execute();

		if (list == null || list.size()==0) 
			throw new Exception("The Transfer Objects list is empty");

		logger.debug("Getting " + list.size() +" outcomes");

		for (TransferObject obj :list){			
			long bytesThatHaveBeenTransferred=0;
			if(obj.getBytesOfObjTransferred()==null && obj.getSize()!=null)bytesThatHaveBeenTransferred=obj.getSize();
			else if(obj.getBytesOfObjTransferred()!=null){
				bytesThatHaveBeenTransferred=obj.getBytesOfObjTransferred();
			}

			String sourceUri= obj.getSourceURI();
			String destUri=obj.getDestURI();
			String outcomeField=Outcome.DONE.name();
			String exception = Outcome.N_A.name();
			long transferTime=0;
			if(obj.getTransferTime()!=null)transferTime = obj.getTransferTime();

			if (obj.getStatus().compareTo(TransferStatus.FAILED.name())==0){
				destUri = Outcome.N_A.name();
				outcomeField=Outcome.ERROR.name();
				exception=obj.getOutcome();
			}
			logger.trace("FileName :" +sourceUri);
			logger.trace("dest :" +destUri);
			logger.trace("Outcome :" +outcomeField);
			logger.trace("Transfer Time: "+transferTime);
			logger.trace("Transferred Bytes: "+bytesThatHaveBeenTransferred);
			logger.trace("Exception: "+exception);			

			FileTransferOutcome outcome = new FileTransferOutcome(sourceUri);
			outcome.setException(exception);
			outcome.setDest(destUri);
			outcome.setTransferTime(transferTime);
			outcome.setTransferredBytes(bytesThatHaveBeenTransferred);

			// need also to include it in the method: getTransferObjectOutComeAsRS
			//because now it's available only when messaging is enabled..
			long total_size=0;
			if(obj.getSize()!=null)total_size = obj.getSize();
			outcome.setTotal_size(total_size);

			outcomes.add(outcome);

			persistenceManager.deletePersistent(obj);
		}
		persistenceManager.close();
		return outcomes;
	}
	public TreeTransferOutcome getTransferTreeOutCome(String transferId) throws Exception {

		TreeTransferOutcome outcome = new TreeTransferOutcome();
		PersistenceManager persistenceManager= getPersistenceManager();
		if(!checkIfTransferExist(transferId)){
			logger.debug("getTransferTreeOutCome - transfer with id="+transferId+" does not exist");
			return null;			
		}		
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);

		if(t==null)return null;
		if (!(TransferStatus.valueOf(t.getStatus()).hasCompleted())){
			return null;
		}
		else{
			outcome.setSourceID(t.getSourceID());
			outcome.setDestID(t.getDestID());
			outcome.setTotalReadTrees(t.getTotalReadTrees());
			outcome.setTotalWrittenTrees(t.getTotalWrittenTrees());			
			if(t.getStatus().compareTo(TransferStatus.FAILED.name())==0){
				outcome.setException(t.getStatus());
			}
			else{
				outcome.setException("N_A");
			}
		}
		return outcome;
	}


	public MonitorTransferReportMessage getTrasferProgress(String transferId) {
		MonitorTransferReportMessage message = new MonitorTransferReportMessage();
		PersistenceManager persistenceManager= getPersistenceManager();
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
		message.setTransferID(transferId);
		message.setTotalBytes(t.getTotalSize());
		message.setTotalTransfers((int)t.getTotaltransfers());
		message.setTransferCompleted((int)t.getTransfersCompleted());
		message.setBytesTransferred(t.getSizeTransferred());
		message.setTransferStatus(t.getStatus());
		persistenceManager.close();
		return message;
	}

	public org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage getTrasferProgressType(String transferId) {
		org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage message = new org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage();
		PersistenceManager persistenceManager= getPersistenceManager();
		Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
		message.setTransferID(transferId);
		message.setTotalBytes(t.getTotalSize());
		message.setTotalTransfers((int)t.getTotaltransfers());
		message.setTransferCompleted((int)t.getTransfersCompleted());
		message.setBytesTransferred(t.getSizeTransferred());
		message.setTransferStatus(t.getStatus());
		persistenceManager.close();
		return message;
	}
} 
