package org.gcube.datatransfer.scheduler.impl.context;

import static org.gcube.datatransfer.scheduler.impl.constants.Constants.*;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.resources.GCUBEResource.ResourceTopic;
import org.gcube.datatransfer.common.messaging.MSGClient;
import org.gcube.datatransfer.common.messaging.MSGClientFactory;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.DataSource;
import org.gcube.datatransfer.scheduler.db.model.DataStorage;
import org.gcube.datatransfer.scheduler.impl.state.SchedulerRIResourceConsumer;
import org.gcube.datatransfer.scheduler.is.ISManager;
//import org.gcube.datatransfer.scheduler.impl.db.DataTransferDBManager;
//import org.gcube.datatransfer.scheduler.impl.utils.Constants;



public class ServiceContext extends GCUBEServiceContext {

	/** Single context instance, created eagerly */
	private static ServiceContext cache = new ServiceContext();
	
	private DataTransferDBManager dbManager = null;
	private String dbConfigurationFileName = null;
	
	private ISManager isManagerForAgents = null;
	private ISManager isManagerForSources = null;
	private ISManager isManagerForStorages = null;
	private boolean messagingEnabled=false;
	private MSGClient msgClient = null;
	private long maxTimeToSetInactiveAnOngoingTransferInMS=0;
	
	@Override 
	protected void onInitialisation() throws Exception {
		//read from jndi
		try{
			this.maxTimeToSetInactiveAnOngoingTransferInMS = Long.valueOf(((String)getProperty("maxTimeToSetInactiveAnOngoingTransferInMS",true)));
		}
		catch(Exception e){
			e.printStackTrace();
			this.maxTimeToSetInactiveAnOngoingTransferInMS=1800000;
		}
		this.dbConfigurationFileName = ((String)getProperty("dbConfigurationFile",true));
		this.messagingEnabled = ((Boolean)getProperty("messaging",true));
		
		this.dbManager = new DataTransferDBManager(this.dbConfigurationFileName, (String)getContext().getPersistenceRoot().getAbsolutePath(), (String)getContext().getProperty("configDir", true));
		getInstance().subscribeResourceEvents(new SchedulerRIResourceConsumer(), ResourceTopic.ADDSCOPE);
	}
	
	
	/** Returns cached instance */
	public static ServiceContext getContext() {return cache;}
	
	/** Prevents accidental creation of more instances */
	private ServiceContext(){};
		
	/** {@inheritDoc} */
	protected String getJNDIName() {return NAME;}
	
	public DataTransferDBManager getDbManager() {
		return dbManager;
	}

	public void setDbManager(DataTransferDBManager dbManager) {
		this.dbManager = dbManager;
	}

	

	public ISManager getIsManagerForAgents() {
		return isManagerForAgents;
	}


	public void setIsManagerForAgents(ISManager isManagerForAgents) {
		this.isManagerForAgents = isManagerForAgents;
	}


	public ISManager getIsManagerForSources() {
		return isManagerForSources;
	}


	public void setIsManagerForSources(ISManager isManagerForSources) {
		this.isManagerForSources = isManagerForSources;
	}


	public ISManager getIsManagerForStorages() {
		return isManagerForStorages;
	}


	public void setIsManagerForStorages(ISManager isManagerForStorages) {
		this.isManagerForStorages = isManagerForStorages;
	}


	public String getDbConfigurationFileName() {
		return dbConfigurationFileName;
	}
	public void setDbConfigurationFileName(String dbConfigurationFileName) {
		this.dbConfigurationFileName = dbConfigurationFileName;
	}


	public boolean isMessagingEnabled() {
		return messagingEnabled;
	}


	public MSGClient getMsgClient() {
		return msgClient;
	}


	public void setMessagingEnabled(boolean messagingEnabled) {
		this.messagingEnabled = messagingEnabled;
	}


	public void setMsgClient(MSGClient msgClient) {
		this.msgClient = msgClient;
	}


	public long getMaxTimeToSetInactiveAnOngoingTransferInMS() {
		return maxTimeToSetInactiveAnOngoingTransferInMS;
	}


	public void setMaxTimeToSetInactiveAnOngoingTransferInMS(
			long maxTimeToSetInactiveAnOngoingTransferInMS) {
		this.maxTimeToSetInactiveAnOngoingTransferInMS = maxTimeToSetInactiveAnOngoingTransferInMS;
	}

	
	
}
