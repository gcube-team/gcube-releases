package org.gcube.datatransfer.agent.impl.context;

import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.resources.GCUBEResource.ResourceTopic;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.events.GCUBEProducer;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.impl.db.DataTransferDBManager;
import org.gcube.datatransfer.agent.impl.event.Events;
import org.gcube.datatransfer.agent.impl.event.TransferRequestSubscription;
import org.gcube.datatransfer.agent.impl.utils.Constants;
import org.gcube.datatransfer.agent.impl.vfs.VFileSystemManager;
import org.gcube.datatransfer.common.messaging.MSGClient;
import org.gcube.datatransfer.common.messaging.MSGClientFactory;

/**
 * 
 * 
 * @author Andrea Manzi (CERN)
 * 
 */
public class ServiceContext extends GCUBEServiceContext {

	public static final String JNDI_NAME = "gcube/datatransfer/agent";
	public static final int FILESXTHREAD = 100;	
	protected static final ServiceContext cache = new ServiceContext();
	private  String dbConfigurationFileName;	
	private  String vfsRoot;	
	private  String awsKeyID;	
	private  String awsKey;	
	private int connectionTimeout;	
	private int transferTimeout;	
	private int retryLimit;
	private VFileSystemManager localFSManager;
	private DataTransferDBManager dbManager = null;
	private  boolean useMessaging= false;
	public static GCUBEProducer<Events.TransferTopics,Object> transferEventproducer = new GCUBEProducer<Events.TransferTopics,Object>();
	
	private MSGClient msgClient = null;
	private int intervalForDBCheckInMS;
	
	private ServiceContext() {}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public String[] getSupportedTransfers() throws Exception{
		String[] transfers = null;

		try {
			transfers = ((String)getProperty("supportedTransfers",true)).split(",");	 
		}catch (RuntimeException e) {
			logger.debug("No Transfers available");
			throw new Exception("No Transfers available");
		}
		return transfers;
	}
	/**
	 * 
	 * @return ServiceContext
	 */
	public static ServiceContext getContext() {
		return cache;
	}

	/**
	 * @return the JNDI name
	 */
	@Override
	public String getJNDIName() {
		return JNDI_NAME;
	}

	@Override 
	protected void onInitialisation() throws Exception {
		//read from jndi
		dbConfigurationFileName = ((String)getProperty("dbConfigurationFile",true));

		setAwsKeyID(((String)getProperty("awsKeyID",false)));

		setAwsKey(((String)getProperty("awsKey",false)));

		setConnectionTimeout(((Integer)getProperty("connectionTimeout",true)).intValue());

		setTransferTimeout(((Integer)getProperty("transferTimeout",true)).intValue());

		setRetryLimit(((Integer)getProperty("retryLimit",true)).intValue());

		setVfsRoot((String)getProperty("vfsRoot",true));

		localFSManager = new VFileSystemManager(vfsRoot);

		this.dbManager = new DataTransferDBManager();

		this.intervalForDBCheckInMS = (((Integer)getProperty("intervalForDBCheckInMS",true)).intValue());

		
		this.setUseMessaging(((Boolean)getProperty(Constants.USEMESSAGING_JNDI_NAME)).booleanValue());
		
		getInstance().subscribeResourceEvents(new AgentRIResourceConsumer(), ResourceTopic.ADDSCOPE);
	
	}

	public String getVfsRoot() {
		return vfsRoot;
	}



	public DataTransferDBManager getDbManager() {
		return dbManager;
	}

	public void setDbManager(DataTransferDBManager dbManager) {
		this.dbManager = dbManager;
	}

	@Override 
	protected void onReady() throws Exception {
		super.onReady();

		logger.trace("creating agent resources in all RI scopes");

		List<PortRange> ports=new ArrayList<PortRange>(); 
		ports.add(new PortRange(4000, 4050));           
		try {
			TCPConnectionManager.Init(
					new TCPConnectionManagerConfig(InetAddress.getLocalHost().getHostName(),
							ports,                               
							true                                   
							));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());      //Register the handler for the gRS2 incoming request
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler()); //Register the handler for the gRS2 store incoming request
	}

	public boolean getUseMessaging() {
		return useMessaging;
	}

	public void setUseMessaging(boolean useMessaging) {
		this.useMessaging = useMessaging;
	}

	public String getDbConfigurationFileName() {
		return dbConfigurationFileName;
	}

	public void setDbConfigurationFileName(String dbConfigurationFileName) {
		this.dbConfigurationFileName = dbConfigurationFileName;
	}

	public VFileSystemManager getLocalFSManager() {
		return localFSManager;
	}


	public String getAwsKeyID() {
		return awsKeyID;
	}

	public void setAwsKeyID(String awsKeyID) {
		this.awsKeyID = awsKeyID;
	}


	public String getAwsKey() {
		return awsKey;
	}

	public void setAwsKey(String awsKey) {
		this.awsKey = awsKey;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}



	public int getTransferTimeout() {
		return transferTimeout;
	}

	public void setTransferTimeout(int transferTimeout) {
		this.transferTimeout = transferTimeout;
	}

	public int getRetryLimit() {
		return retryLimit;
	}

	public void setRetryLimit(int retryLimit) {
		this.retryLimit = retryLimit;
	}

	public void setVfsRoot(String vfsRoot) {
		this.vfsRoot = vfsRoot;
	}

	public MSGClient getMsgClient() {
		return msgClient;
	}

	public void setMsgClient(MSGClient msgClient) {
		this.msgClient = msgClient;
	}

	public int getIntervalForDBCheck() {
		return intervalForDBCheckInMS;
	}

	public void setIntervalForDBCheck(int intervalForDBCheckInMS) {
		this.intervalForDBCheckInMS = intervalForDBCheckInMS;
	}  

}