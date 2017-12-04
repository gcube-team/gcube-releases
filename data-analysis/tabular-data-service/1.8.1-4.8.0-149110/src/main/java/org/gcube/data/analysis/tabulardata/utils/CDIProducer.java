package org.gcube.data.analysis.tabulardata.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.gcube.common.database.DatabaseEndpointIdentifier;
import org.gcube.common.database.DatabaseProvider;
import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.gcube.common.database.endpoint.DatabaseProperty;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class CDIProducer {

	private static ExecutorService executorService;

	@Produces
	public ApplicationContext getApplicationContext(){
		return ContextProvider.get();
	}


	private static Factories factoryMap;

	@Produces @TabularDataMetadata
	public EntityManagerFactory createEntityManagerFactory(@Named("Metadata-Admin") DatabaseEndpointIdentifier metaDBEndpointId,
			DatabaseProvider dbProvider) {
		DatabaseEndpoint dbDescriptor = dbProvider.get(metaDBEndpointId);

		// Retrieve driver from properties
		String driver = null;
		String eclipseLinkTargetDb = null;
		String ddlGenerationStrategy = null;
		for (DatabaseProperty p : dbDescriptor.getProperties()) {
			if (p.getKey().equals("driver"))
				driver = p.getValue();
			if (p.getKey().equals("eclipselink.target-database"))
				eclipseLinkTargetDb = p.getValue();
			if (p.getKey().equals("eclipselink.ddl-generation"))
				ddlGenerationStrategy = p.getValue();
		}
		if (driver == null)
			throw new RuntimeException("Unable to find the right driver for the connection to the DB: "
					+ dbDescriptor);

		Map<String, String> properties = Maps.newHashMap();
		properties.put("javax.persistence.jdbc.user", dbDescriptor.getCredentials().getUsername());
		properties.put("javax.persistence.jdbc.password", dbDescriptor.getCredentials().getPassword());
		properties.put("javax.persistence.jdbc.driver", driver);
		properties.put("javax.persistence.jdbc.url", dbDescriptor.getConnectionString());
		properties.put("eclipselink.target-database", eclipseLinkTargetDb);

		if (ddlGenerationStrategy != null)
			properties.put("eclipselink.ddl-generation", ddlGenerationStrategy);
		else
			properties.put("eclipselink.ddl-generation", "create-tables");
		// properties.put("eclipselink.ddl-generation","create-or-extend-tables");
		// properties.put("eclipselink.ddl-generation","drop-and-create-tables");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("tabulardata", properties);
		return emf;

	}

	protected void disposeEntityManagerFactory(@Disposes @TabularDataMetadata EntityManagerFactory entityManagerFactory){
		if(entityManagerFactory.isOpen())
			entityManagerFactory.close();
		
	}


	@Produces
	public Logger getLogger(InjectionPoint injectionPoint){
		return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getClass());
	}	


	@Produces
	public ExecutorService getExecutor(){
		if (executorService==null)
			executorService = Executors.newCachedThreadPool(new ThreadFactory() {
								
				@Override
				public Thread newThread(Runnable r) {
					return new Thread(new ThreadGroup(UUID.randomUUID().toString()), r);
				}
			});
		return executorService;
	}

	protected void shutdownExecutor(@Disposes ExecutorService executorService){
		if (!executorService.isShutdown())
			executorService.shutdown();
	}

	@Produces
	public  Factories workerFactoryPerId(Instance<WorkerFactory<?>> factories){
		if (factoryMap ==null){
			HashMap<OperationId, WorkerFactory<?>> tempFactoryMap = Maps.newHashMap();
			Iterator<WorkerFactory<?>> factoriesIT =factories.iterator();
			while( factoriesIT.hasNext()){
				WorkerFactory<?> factory = factoriesIT.next();
				tempFactoryMap.put(factory.getOperationDescriptor().getOperationId(), factory);
			}
			factoryMap = new Factories(tempFactoryMap);
		}
		return factoryMap;
	}

	public static Factories getFactoryMap(){
		if (factoryMap==null) throw new RuntimeException("factory map not yet initialized");
		return factoryMap;
	}

}
