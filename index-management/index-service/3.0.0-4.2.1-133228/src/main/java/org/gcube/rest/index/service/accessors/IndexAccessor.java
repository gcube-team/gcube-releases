package org.gcube.rest.index.service.accessors;


import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.Serializable;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.gcube.elasticsearch.FullTextNode;
import org.gcube.rest.index.common.Constants;
import org.gcube.rest.index.common.discover.IndexDiscoverer;
import org.gcube.rest.index.service.jobs.IndexReplicationUpdater;
import org.gcube.rest.index.service.resources.ServiceProperties;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IndexAccessor implements Serializable, ServletContextListener {
	
	private static final long serialVersionUID = 1L;
	
	private static FullTextNode ftn;
	
	private static final Logger logger = LoggerFactory.getLogger(IndexAccessor.class);
	
	private Scheduler scheduler;
	private static final int UPDATE_REPLICATION_INTERVAL = 60; //seconds
	
	@Override
	public void contextDestroyed(ServletContextEvent context) {
		ftn.stopAndCloseNode();
	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
		ServiceProperties indexProps = new ServiceProperties();
		try {
			logger.info("Will initiate an index node by the following properties read from local setup (" + Constants.PROPERTIES_FILE + ")");
			logger.info("fragm_cnt           : " + indexProps.getMaxFragmentCnt());
			logger.info("fragm_size          : " + indexProps.getMaxFragmentSize());
//			logger.info("noReplicas          : " + indexProps.getNoReplicas());
			logger.info("noShards            : " + indexProps.getNoShards());
			logger.info("esPort              : " + indexProps.getElasticSearchPort());
			logger.info("dataDirectory       : " + indexProps.getDataDir());
			logger.info("maxResults          : " + indexProps.getMaxResults());
			logger.info("hostname            : " + indexProps.getHostname());
			logger.info("Setting index scope to " + indexProps.getScope());
			logger.info("Initializing fulltextnode...");
			
			//this.ftn = new FullTextNode(hostname, dataDir, transformedClusterName, "main-index", noReplicas, noShards, scope, fragm_cnt, fragm_size, useRRAdaptor);
			FullTextNode.Builder builder = new FullTextNode.Builder()
				.scope(indexProps.getScope())
				.hostname(indexProps.getHostname())
				.dataDir(indexProps.getDataDir())
				.clusterName("gCubeIndex")
//				.noOfReplicas(indexProps.getNoReplicas())
				.noOfShards(indexProps.getNoShards())
				.maxFragmentCnt(indexProps.getMaxFragmentCnt())
				.maxResults(indexProps.getMaxResults())
				.maxFragmentSize(indexProps.getMaxFragmentSize());
			
			ftn = builder.build();
			
			Set<String> indexNodes = new IndexDiscoverer().discoverFulltextIndexNodes(indexProps.getScope());
			
			logger.info("Initializing Index Node...");
			ftn.createOrJoinCluster(indexNodes);
			logger.info("Index Node initalized!");
			
			
			try{
				scheduler = StdSchedulerFactory.getDefaultScheduler();
			}
			catch(SchedulerException e){/*do nothing*/}
			
			if(scheduler != null){
				logger.debug("Initializing smart replication factor updater!");
				JobDetail job = newJob(IndexReplicationUpdater.class).build();
				Trigger trigger = newTrigger()
					.withSchedule(simpleSchedule().withIntervalInSeconds(UPDATE_REPLICATION_INTERVAL).repeatForever())
					.build();
				scheduler.getContext().put("ftn", ftn);
				scheduler.scheduleJob(job, trigger);
				scheduler.start();
			}
			else
				logger.error("Could not initate smart replication factor updater! However, do not panic...");
			
//			logger.info("Recreating meta index from data to resolve inconsistencies");
//			ftn.recreateMetaIndex();
//			ftn.refreshIndexTypesOfIndex();
			
		} catch (Exception e) {
			logger.error("Error while initializing the index client", e);
		}
	}

	
	public static FullTextNode getFullTextNode()
	{
		return ftn;
	}
	
	public static String getScope()
	{
		return ftn.getScope();
	}
	
	public static String getClusterName()
	{
		return ftn.getClusterName();
	}
	
	
	


	
	
}