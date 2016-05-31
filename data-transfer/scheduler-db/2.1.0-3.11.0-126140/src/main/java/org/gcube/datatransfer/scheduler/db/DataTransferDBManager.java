package org.gcube.datatransfer.scheduler.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.scheduler.db.model.*;



public class DataTransferDBManager  extends DBManager implements Runnable {

	GCUBELog logger = new GCUBELog(DataTransferDBManager.class);
	private static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

	/*
	 * DataTransferDBManager (general constructor)
	 */
	public DataTransferDBManager(String dbConfigurationFileName, String persistenceRootPath, String propertyConfig) {

		dbFileBaseFolder =persistenceRootPath + File.separator + "DataTransferDB"; 
		dbName = "data_transfer";
		dbFileName =  dbFileBaseFolder +File.separator+ dbName+".db";

		backupFolder = new File ((System.getenv("HOME") + File.separator + "DataTransferDBBackup"));

		prop = new Properties();

		//getting datanucleus config file from JNDI
		String propFile = GHNContext.getContext().getLocation() + File.separator +
				propertyConfig + File.separator + dbConfigurationFileName;

		//Thread.currentThread().getContextClassLoader().getResource(propFile);

		try {
			prop.load(new FileInputStream(new File(propFile)));
		} catch (IOException e) {
			e.printStackTrace();

		}
		logger.debug("DataTransferDBManager (General constructor) has been created : " +"\n"+
				"properties File="+propFile);

		persistenceFactory =JDOHelper.getPersistenceManagerFactory(prop);

		//setting backupIntervalMS
		int backupIntervalInHours=12;
		String backupInterv=this.getScheduledBackupInHours();
		try{
			backupIntervalInHours=Integer.valueOf(backupInterv);
		}catch(Exception e){
			e.printStackTrace();
			backupIntervalInHours=12;
		}
		backupIntervalMS = 3600 * 1000 * backupIntervalInHours;


		Thread t = new Thread(this);
		t.start();
	}


	/*
	 * DataTransferDBManager (temporary (only for testing inside the eclipse) - giving manually the config file)
	 */
	public DataTransferDBManager() {

		dbFileBaseFolder =(String)"/home/nick/DataNucleus/transfer-scheduler-db" + File.separator + "DataTransferDB"; 
		//dbFileBaseFolder =(String)"/home/nick/DataNucleus/transfer-scheduler-db" + File.separator + "DataTransferDB"; 

		dbName = "data_transfer";
		dbFileName =  dbFileBaseFolder +File.separator+ dbName+".db";

		backupFolder = new File ((System.getenv("HOME") + File.separator + "DataTransferDBBackup"));

		prop = new Properties();
		URL properties = null;
		properties = Thread.currentThread().getContextClassLoader().getResource("db.properties");

		try {
			prop.load(properties.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.debug("DataTransferDBManager (For testing inside the eclipse) has been created : " +"\n"+
				"properties File="+"db.properties");

		persistenceFactory =JDOHelper.getPersistenceManagerFactory(prop);

		Thread t = new Thread(this);
		t.start();
	}


	/* --------------------- */
	/* Methods for Storing  */
	/* --------------------*/

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
		finally
		{
			if (persistenceManager.currentTransaction().isActive()) 
				persistenceManager.currentTransaction().rollback();
			persistenceManager.close();
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

	public void storeTypeOfSchedule(TypeOfSchedule typeOfSchedule) throws Exception{

		PersistenceManager persistenceManager = getPersistenceManager();
		try
		{
			persistenceManager.currentTransaction().begin();
			persistenceManager.makePersistent(typeOfSchedule);
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
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
	public void storePeriodicallyScheduled(PeriodicallyScheduled periodicallyScheduled) throws Exception{

		PersistenceManager persistenceManager = getPersistenceManager();
		try
		{
			persistenceManager.currentTransaction().begin();
			persistenceManager.makePersistent(periodicallyScheduled);
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
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

	public void storeManuallyScheduled(ManuallyScheduled manuallyScheduled) throws Exception{

		PersistenceManager persistenceManager = getPersistenceManager();
		try
		{
			persistenceManager.currentTransaction().begin();
			persistenceManager.makePersistent(manuallyScheduled);
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
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


	public void storeAgent(Agent agent) throws Exception{

		PersistenceManager persistenceManager = getPersistenceManager();
		try
		{
			persistenceManager.currentTransaction().begin();
			persistenceManager.makePersistent(agent);
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
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

	public void storeSource(DataSource source) throws Exception{

		PersistenceManager persistenceManager = getPersistenceManager();
		try
		{
			persistenceManager.currentTransaction().begin();
			persistenceManager.makePersistent(source);
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
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
	public void storeStorage(DataStorage storage) throws Exception{

		PersistenceManager persistenceManager = getPersistenceManager();
		try
		{
			persistenceManager.currentTransaction().begin();
			persistenceManager.makePersistent(storage);
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
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

	public void storeAgentStatistics(AgentStatistics agentStatistics) throws Exception{

		PersistenceManager persistenceManager = getPersistenceManager();
		try
		{
			persistenceManager.currentTransaction().begin();
			persistenceManager.makePersistent(agentStatistics);
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
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
	public void storeTransferOutcomes(TransferOutcome transferOutcome) throws Exception{

		//storing transfer Main object
		PersistenceManager persistenceManager = getPersistenceManager();
		try
		{
			persistenceManager.currentTransaction().begin();
			persistenceManager.makePersistent(transferOutcome);
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
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
	/* ---------------------- */
	/* Methods for Deleting  */
	/* ---------------------*/

	public void deleteTransfer(Transfer transfer) throws Exception{

		PersistenceManager persistenceManager = getPersistenceManager();

		try
		{
			persistenceManager.currentTransaction().begin();
			//persistenceManager.deletePersistent(transfer.getStorage());
			//persistenceManager.deletePersistent(transfer.getSource());
			persistenceManager.deletePersistent(transfer);		
			persistenceManager.currentTransaction().commit();

		}catch (Exception e)
		{
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

	public void deleteTransferObject (Set<TransferObject> transferObjects) throws Exception{
		PersistenceManager persistenceManager = getPersistenceManager();
		try {
			for (TransferObject obj : transferObjects){
				persistenceManager.currentTransaction().begin();
				persistenceManager.deletePersistent(obj);
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

	public void deleteAgent (Agent agent) throws Exception{
		PersistenceManager persistenceManager = getPersistenceManager();
		try {
			persistenceManager.currentTransaction().begin();
			persistenceManager.deletePersistent(agent);
			persistenceManager.currentTransaction().commit();
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

	/* ------------------------ */
	/* Methods for updating    */
	/* ---------------------- */
	public void updateTransferStatus(String transferId,String status) throws Exception{

		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
			t.setStatus(status);
			persistenceManager.makePersistent(t);
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

	public void updateTransferBytes(String transferId,long total_size,long transferredBytes) throws Exception{

		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
			t.setTotal_size(total_size);
			t.setBytes_have_been_transferred(transferredBytes);
			persistenceManager.makePersistent(t);
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
	public void updateTreeOutcomeInTransfer(String transferId,String exception, int totalReadTrees, int totalWrittenTrees) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();

		try{			
			//CREATE TRANSFER TREE OUTCOMES
			persistenceManager.currentTransaction().begin();
			Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
			
			TransferTreeOutcome treeOutcome = new TransferTreeOutcome();
			String outcomeId = uuidgen.nextUUID();
			treeOutcome.setTransferTreeOutcomesId(outcomeId);
			treeOutcome.setSourceId(t.getSourceId());
			treeOutcome.setStorageId(t.getStorageId());
			treeOutcome.setSubmittedDateOfTransfer(t.getSubmittedDate());
			treeOutcome.setTotalReadTrees(totalReadTrees);
			treeOutcome.setTotalWrittenTrees(totalWrittenTrees);
			treeOutcome.setTreeException(exception);
			if(exception==null || exception.compareTo("N_A")==0){
				treeOutcome.setFailure(false);
				treeOutcome.setSuccess(true);
			}
			else{
				treeOutcome.setFailure(true);
				treeOutcome.setSuccess(false);
			}
			treeOutcome.setTransferId(transferId);			
			persistenceManager.makePersistent(treeOutcome);
			
			persistenceManager.currentTransaction().commit();
			//-----------------
			
			//UPDATE TRANSFER TREE OUTCOMES
			persistenceManager.currentTransaction().begin();
			t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
			
			List<String> list=new ArrayList<String>();
			String[] arrayOutcomes=t.getTreeOutcomes();
			if(arrayOutcomes!=null){
				for(String tmp:arrayOutcomes)list.add(tmp);
			}
			
			list.add(outcomeId);
			String[] arrayToBeStored=list.toArray(new String[list.size()]);
			t.setTreeOutcomes(arrayToBeStored);
			persistenceManager.makePersistent(t);			
			
			persistenceManager.currentTransaction().commit();
			//-----------------

		}catch (Exception e)
		{
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

	public void updateTransferError(String transferId,List<String> errors) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();
		try{
			Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
			String[] arrayToBeStored=errors.toArray(new String[errors.size()]);
			t.setTransferError(arrayToBeStored);		
			persistenceManager.makePersistent(t);
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
	public void updateObjectTrasferredIDs(String transferId,String[] objectTrasferredIDs) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();
		try{
			Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
			t.setObjectTrasferredIDs(objectTrasferredIDs);
			persistenceManager.makePersistent(t);
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
	public void updateObjectFailedIDs(String transferId,String[] objectFailedIDs) throws Exception{

		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
			t.setObjectFailedIDs(objectFailedIDs);
			persistenceManager.makePersistent(t);
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


	public void updateAgentInTransfer(String transferId,String agentid) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer transfer = (Transfer) persistenceManager.getObjectById(Transfer.class, transferId);
			transfer.setAgentId(agentid);
			persistenceManager.makePersistent(transfer);
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
	public void updateStartTimeInTransfer(String transferId,long startTime) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer transfer = (Transfer) persistenceManager.getObjectById(Transfer.class, transferId);
			transfer.setStartTime(startTime);
			persistenceManager.makePersistent(transfer);
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
	public void updateTotalTimeInTransfer(String transferId,long totalTime) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer transfer = (Transfer) persistenceManager.getObjectById(Transfer.class, transferId);
			transfer.setStartTime(-1); 
			transfer.setTotalTime(totalTime);
			persistenceManager.makePersistent(transfer);
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
	public void updateStorageInTransfer(String transferId,String storageid) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer transfer = (Transfer) persistenceManager.getObjectById(Transfer.class, transferId);
			transfer.setStorageId(storageid);
			persistenceManager.makePersistent(transfer);
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
	public void updateSourceInTransfer(String transferId,String sourceid) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer transfer = (Transfer) persistenceManager.getObjectById(Transfer.class, transferId);
			transfer.setSourceId(sourceid);
			persistenceManager.makePersistent(transfer);
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

	public void updateTransferIdOfAgentInTransfer(String transferId,String transferIdOfAgent) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer transfer = (Transfer) persistenceManager.getObjectById(Transfer.class, transferId);
			transfer.setTransferIdOfAgent(transferIdOfAgent);
			persistenceManager.makePersistent(transfer);
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

	public void updateTransferStartInstanceString(String transferId,String startInstanceString) throws Exception{

		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);
			String idTypeOfSchedule = t.getTypeOfScheduleId();		

			TypeOfSchedule typeOfSchedule = persistenceManager.getObjectById(TypeOfSchedule.class, idTypeOfSchedule);
			String idperiodicallyScheduled = typeOfSchedule.getPeriodicallyScheduledId();

			PeriodicallyScheduled periodicallyScheduled = persistenceManager.getObjectById(PeriodicallyScheduled.class, idperiodicallyScheduled);

			periodicallyScheduled.setStartInstanceString(startInstanceString);

			persistenceManager.makePersistent(periodicallyScheduled);
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
	public void updateTransferReadyObjects(String transferId,boolean flag) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer transfer = (Transfer) persistenceManager.getObjectById(Transfer.class, transferId);
			transfer.setReadyObjects(flag);
			persistenceManager.makePersistent(transfer);
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
	public void updateOutcomesInTransfer(String transferId,List<String> outcomes) throws Exception{

		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);		
			String[] arrayToBeStored=outcomes.toArray(new String[outcomes.size()]);
			t.setOutcomes(arrayToBeStored);			
			persistenceManager.makePersistent(t);
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
	public void updateEverythingInAgent(String agentId, String agentIdOfIs, String host, int port) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();
		try{
			Agent agent = (Agent) persistenceManager.getObjectById(Agent.class,agentId);
			agent.setAgentIdOfIS(agentIdOfIs);
			agent.setHost(host);
			agent.setPort(port);
			persistenceManager.makePersistent(agent);
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

	public void updateEverythingInSource(String sourceId, String dataSourceIdOfIS, String dataSourceName, String description, String endpoint, String username,String pass,String folder) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();
		try{
			DataSource source = (DataSource) persistenceManager.getObjectById(DataSource.class,sourceId);
			source.setDataSourceIdOfIS(dataSourceIdOfIS);
			source.setDataSourceName(dataSourceName);
			source.setDescription(description);
			source.setEndpoint(endpoint);
			source.setFolder(folder);
			source.setUsername(username);
			source.setPass(pass);
			persistenceManager.makePersistent(source);
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

	public void updateEverythingInStorage(String id, String idOfIS, String dataStorageName, String description, String endpoint, String username,String pass) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();
		try{
			DataStorage storage = (DataStorage) persistenceManager.getObjectById(DataStorage.class,id);
			storage.setDataStorageIdOfIS(idOfIS);
			storage.setDataStorageName(dataStorageName);
			storage.setDescription(description);
			storage.setEndpoint(endpoint);
			storage.setUsername(username);
			storage.setPass(pass);
			persistenceManager.makePersistent(storage);
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

	public void updateAgentStatistics(String id,int ongoing,int failed, int succeeded, int canceled, int total) throws Exception{
		PersistenceManager persistenceManager= getPersistenceManager();
		try{
			AgentStatistics stats = (AgentStatistics) persistenceManager.getObjectById(AgentStatistics.class, id);
			stats.setOngoingTransfers(ongoing);
			stats.setFailedTransfers(failed);
			stats.setSucceededTransfers(succeeded);
			stats.setCanceledTransfers(canceled);
			stats.setTotalFinishedTransfers(total);
			persistenceManager.makePersistent(stats);
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
	public void resetProgressInTransfer(String transferId) throws Exception{

		PersistenceManager persistenceManager= getPersistenceManager();

		try{
			Transfer t = (Transfer) persistenceManager.getObjectById(Transfer.class,transferId);		
			t.resetProgress();
			persistenceManager.makePersistent(t);
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
	/**
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		do {
			try {				
				Thread.sleep(backupIntervalMS);
				this.startBackUp();
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

} 
