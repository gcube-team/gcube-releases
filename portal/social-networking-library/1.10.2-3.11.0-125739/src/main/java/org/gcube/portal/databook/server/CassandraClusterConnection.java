package org.gcube.portal.databook.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Cluster;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.ddl.ColumnDefinition;
import com.netflix.astyanax.ddl.ColumnFamilyDefinition;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import com.netflix.astyanax.ddl.SchemaChangeResult;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.astyanax.thrift.ddl.ThriftColumnDefinitionImpl;
import com.netflix.astyanax.thrift.ddl.ThriftColumnFamilyDefinitionImpl;

/**
 * @author Massimiliano Assante ISTI-CNR
 * @author Costantino Perciante ISTI-CNR
 *
 */
public class CassandraClusterConnection {
	/**
	 * logger
	 */
	private static final Logger _log = LoggerFactory.getLogger(CassandraClusterConnection.class);

	/**
	 * keyspace location
	 */
	private static String clusterName;
	private static String host;
	private static String keyspaceName;	

	private Keyspace myKeyspace;
	/**
	 * 
	 * @param dropSchema set true if you want do drop the current and set up new one
	 * @return the connection to cassandra cluster
	 */
	protected CassandraClusterConnection(boolean dropSchema)	{
		if (clusterName == null || host == null || keyspaceName == null) {
			RunningCluster cluster = RunningCluster.getInstance(null);
			clusterName = cluster.getClusterName();
			host = cluster.getHost();
			keyspaceName = cluster.getKeyspaceName();
		}	

		AstyanaxContext<Cluster> clusterContext = new AstyanaxContext.Builder()
		.forCluster(clusterName)
		.withAstyanaxConfiguration(new AstyanaxConfigurationImpl())
		.withConnectionPoolConfiguration(
				new ConnectionPoolConfigurationImpl(
						clusterName).setMaxConnsPerHost(100)
						.setSeeds(host))
						.withConnectionPoolMonitor(
								new CountingConnectionPoolMonitor())
								.buildCluster(ThriftFamilyFactory.getInstance());

		_log.info(keyspaceName + " KeySpace SetUp ...");
		SetUpKeySpaces(clusterContext, dropSchema);
		_log.info("CONNECTED! using KeySpace: " + keyspaceName);
	}
	
	/**
	 * 
	 * @param dropSchema set true if you want do drop the current and set up new one
	 * @return the connection to cassandra cluster
	 */
	protected CassandraClusterConnection(boolean dropSchema, String infrastructureName)	{
		if (clusterName == null || host == null || keyspaceName == null) {
			RunningCluster cluster = RunningCluster.getInstance(infrastructureName);
			clusterName = cluster.getClusterName();
			host = cluster.getHost();
			keyspaceName = cluster.getKeyspaceName();
		}	

		AstyanaxContext<Cluster> clusterContext = new AstyanaxContext.Builder()
		.forCluster(clusterName)
		.withAstyanaxConfiguration(new AstyanaxConfigurationImpl())
		.withConnectionPoolConfiguration(
				new ConnectionPoolConfigurationImpl(
						clusterName).setMaxConnsPerHost(100)
						.setSeeds(host))
						.withConnectionPoolMonitor(
								new CountingConnectionPoolMonitor())
								.buildCluster(ThriftFamilyFactory.getInstance());

		_log.info(keyspaceName + " KeySpace SetUp ...");
		SetUpKeySpaces(clusterContext, dropSchema);
		_log.info("CONNECTED! using KeySpace: " + keyspaceName);
	}

	/**
	 * Get the reference to the current keyspace
	 * @return keyspace reference
	 */
	public  Keyspace getKeyspace() {

		// The Keyspace instance can be shared among different requests
		if(myKeyspace == null){
			synchronized(this){
				if(myKeyspace == null){ // double check lock
					AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
					.forCluster(clusterName)
					.forKeyspace(keyspaceName)
					.withAstyanaxConfiguration(
							new AstyanaxConfigurationImpl() 
							.setDiscoveryType(NodeDiscoveryType.NONE) // use only the host given as seeds (do not discover)
							.setConnectionPoolType(ConnectionPoolType.ROUND_ROBIN) // how handle connections of the the connection pool
							) 
							.withConnectionPoolConfiguration(
									new ConnectionPoolConfigurationImpl("MyConnectionPool")
									.setMaxConnsPerHost(3) // for each seed(host)
									.setSocketTimeout(2000)  //-> default: 11 seconds
									//.setConnectTimeout(1000) -> default: 2 seconds
									.setSeeds(host)
									)
									.withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
									.buildKeyspace(ThriftFamilyFactory.getInstance());


					context.start();
					
					// save keyspace reference
					myKeyspace = context.getEntity();

				}
			}
		}

		return myKeyspace;
	}

	/**
	 * 
	 * @param cluster
	 * @param dropIfExists
	 * @throws ConnectionException
	 */
	public void SetUpKeySpaces(AstyanaxContext<Cluster> clusterContext, boolean dropSchema) {
		boolean createNew = false;
		clusterContext.start();
		try {
			Cluster cluster = clusterContext.getEntity();
			KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(keyspaceName);

			if (dropSchema && keyspaceDef != null) {
				_log.info("Dropping Keyspace: " + keyspaceName + " ...");
				try {
					OperationResult<SchemaChangeResult> returned = cluster.dropKeyspace(keyspaceName);
					Thread.sleep(2000);
					_log.info("Dropped " + returned.getResult().toString());
				} catch (ConnectionException e) {
					_log.error("Dropping Keyspace operation Failed ... " + keyspaceName + " does NOT exists");
					return;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
				createNew = true;

			}	

			keyspaceDef = cluster.makeKeyspaceDefinition();
			keyspaceDef = cluster.describeKeyspace(keyspaceName);

			if (keyspaceDef == null || keyspaceDef.getName() == null || createNew) {
				_log.info("Keyspace does not exist, triggering schema creation ... ");
				createSchema(cluster);
				_log.info("Cluster " + clusterName + " on " + host + " Initialized OK!");
				_log.info("Using Keyspace " + keyspaceName);
			}
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}


	/*
	 * 
	 ********************** 	CASSANDRA KEYSPACE CREATION	***********************
	 *
	 */
	/**
	 * create the databook schema
	 * @return
	 * @throws ConnectionException 
	 */
	private void createSchema(Cluster cluster) throws ConnectionException {

		Map<String, String> stratOptions = new HashMap<String, String>();
		stratOptions.put("replication_factor", "1");

		KeyspaceDefinition ksDef = cluster.makeKeyspaceDefinition();

		//get static column families with secondary indexes
		/**
		 * define Notifications CF with Type as secondary index
		 */
		ColumnFamilyDefinition cfDefNotifications = getStaticCFDef(DBCassandraAstyanaxImpl.NOTIFICATIONS, "Type");
		/**
		 * define Feeds CF with Privacy as secondary index
		 */
		ColumnFamilyDefinition cfDefFeeds = getStaticCFDef(DBCassandraAstyanaxImpl.FEEDS, "Privacy");
		/**
		 * define Comments CF with FeedId as secondary index
		 */
		ColumnFamilyDefinition cfDefComments = getStaticCFDef(DBCassandraAstyanaxImpl.COMMENTS, "Feedid");
		/**
		 * define Likes CF with FeedId as secondary index
		 */
		ColumnFamilyDefinition cfDefLikes = getStaticCFDef(DBCassandraAstyanaxImpl.LIKES, "Feedid");
		/**
		 * define Invites CF with SenderUserId as secondary index
		 */
		ColumnFamilyDefinition cfDefInvites = getStaticCFDef(DBCassandraAstyanaxImpl.INVITES, "SenderUserId");
		/**
		 * define Attachments CF with FeedId as secondary index
		 */
		ColumnFamilyDefinition cfDefAttachments = getStaticCFDef(DBCassandraAstyanaxImpl.ATTACHMENTS, "Feedid");
		

		//get dynamic column families, act as auxiliary indexes
		ColumnFamilyDefinition cfDefConn = getDynamicCFDef(DBCassandraAstyanaxImpl.CONNECTIONS);
		ColumnFamilyDefinition cfDefPendingConn = getDynamicCFDef(DBCassandraAstyanaxImpl.PENDING_CONNECTIONS_CF_NAME);
		ColumnFamilyDefinition cfDefVRETimeline = getDynamicCFDef(DBCassandraAstyanaxImpl.VRE_TIMELINE_FEEDS);
		ColumnFamilyDefinition cfDefAPPTimeline = getDynamicCFDef(DBCassandraAstyanaxImpl.APP_TIMELINE_FEEDS);
		ColumnFamilyDefinition cfDefUserTimeline = getDynamicCFDef(DBCassandraAstyanaxImpl.USER_TIMELINE_FEEDS);
		ColumnFamilyDefinition cfDefUserLikedFeeds = getDynamicCFDef(DBCassandraAstyanaxImpl.USER_LIKED_FEEDS);
		ColumnFamilyDefinition cfDefUserNotifications = getDynamicCFDef(DBCassandraAstyanaxImpl.USER_NOTIFICATIONS);
		ColumnFamilyDefinition cfDefUserMessagesNotifications = getDynamicCFDef(DBCassandraAstyanaxImpl.USER_MESSAGES_NOTIFICATIONS);
		ColumnFamilyDefinition cfDefUserNotificationsPreferences = getDynamicCFDef(DBCassandraAstyanaxImpl.USER_NOTIFICATIONS_PREFERENCES);
		ColumnFamilyDefinition cfDefHashtagsCounter = getDynamicCFDef(DBCassandraAstyanaxImpl.HASHTAGS_COUNTER);
		ColumnFamilyDefinition cfDefHashtagTimeline = getDynamicCFDef(DBCassandraAstyanaxImpl.HASHTAGGED_FEEDS);


		ksDef.setName(keyspaceName)
		.setStrategyOptions(stratOptions)
		.setStrategyClass("SimpleStrategy")
		.addColumnFamily(cfDefNotifications)
		.addColumnFamily(cfDefFeeds)
		.addColumnFamily(cfDefComments)
		.addColumnFamily(cfDefLikes)
		.addColumnFamily(cfDefInvites)
		.addColumnFamily(cfDefAttachments)
		.addColumnFamily(cfDefConn)
		.addColumnFamily(cfDefPendingConn)
		.addColumnFamily(cfDefVRETimeline)
		.addColumnFamily(cfDefAPPTimeline)
		.addColumnFamily(cfDefUserTimeline)
		.addColumnFamily(cfDefUserNotifications)
		.addColumnFamily(cfDefUserMessagesNotifications)
		.addColumnFamily(cfDefUserNotificationsPreferences)
		.addColumnFamily(cfDefUserLikedFeeds)
		.addColumnFamily(cfDefHashtagsCounter)
		.addColumnFamily(cfDefHashtagTimeline);

		cluster.addKeyspace(ksDef);	
	}

	/**
	 * create a dynamic column family to be added in a keyspace
	 * 
	 * @param cfName the CF name
	 * @return the instance to be added to the keyspace
	 */
	private ColumnFamilyDefinition getDynamicCFDef(String cfName) {
		ColumnFamilyDefinition columnFamilyDefinition = new ThriftColumnFamilyDefinitionImpl();
		columnFamilyDefinition.setName(cfName);			
		columnFamilyDefinition.setKeyValidationClass("UTF8Type");
		columnFamilyDefinition.setComparatorType("UTF8Type");
		return columnFamilyDefinition;
	}

	/**
	 * create a static column family to be added in a keyspace with possibility to add a secondary index for a given column
	 * 
	 * @param cfName the CF name
	 * @param secondaryIndexedField the column name of the column to index
	 * @return the instance to be added to the keyspace
	 */
	private ColumnFamilyDefinition getStaticCFDef(String cfName, String secondaryIndexedField) {
		ColumnFamilyDefinition columnFamilyDefinition = new ThriftColumnFamilyDefinitionImpl();
		columnFamilyDefinition.setName(cfName);

		columnFamilyDefinition.setKeyValidationClass("UTF8Type");
		columnFamilyDefinition.setComparatorType("UTF8Type");


		//Add secondary index for userid
		ColumnDefinition typeCDef = new ThriftColumnDefinitionImpl();
		typeCDef.setName(secondaryIndexedField)
		.setValidationClass("UTF8Type");
		typeCDef.setIndex(secondaryIndexedField+"_"+UUID.randomUUID().toString().substring(0,5), "KEYS");

		columnFamilyDefinition.addColumnDefinition(typeCDef);
		return columnFamilyDefinition;
	}


}
