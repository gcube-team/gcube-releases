package org.gcube.data.publishing.gCatFeeder.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.core.Application;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;
import org.gcube.data.publishing.gCatFeeder.service.engine.CatalogueControllersManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.CollectorsManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.ConnectionManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.ExecutionManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.FeederEngine;
import org.gcube.data.publishing.gCatFeeder.service.engine.Infrastructure;
import org.gcube.data.publishing.gCatFeeder.service.engine.LocalConfiguration;
import org.gcube.data.publishing.gCatFeeder.service.engine.PersistenceManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.Storage;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.CatalogueControllersManagerImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.CollectorsManagerImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.ExecutionManagerImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.FeederEngineImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.LiveEnvironmentConfiguration;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.LocalConfigurationImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.ConnectionManagerImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.PersistenceManagerImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.StorageImpl;
import org.gcube.data.publishing.gCatFeeder.service.mockups.InfrastructureMockup;
import org.gcube.data.publishing.gCatFeeder.service.mockups.StorageMockup;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.BeforeClass;

public class BaseTest extends JerseyTest{


	private static String testContext=null; 

	@BeforeClass
	public static void checkEnvironment() {	
		testContext=System.getProperty("testContext");		
		System.out.println("TEST CONTEXT = "+testContext);
	}


	@Before
	public void init() throws IOException, SQLException{
		setTestInfrastructure();
	}


	@Override
	protected Application configure() {		
		if(testContext!=null) {
			System.out.println("TEST INFRASTRUCTURE IS "+testContext);
			AbstractBinder binder = new AbstractBinder() {
				@Override
				protected void configure() {                    
					bind(FeederEngineImpl.class).to(FeederEngine.class);
		              bind(CatalogueControllersManagerImpl.class).to(CatalogueControllersManager.class).in(Singleton.class);
		              bind(CollectorsManagerImpl.class).to(CollectorsManager.class).in(Singleton.class);
		              bind(ExecutionManagerImpl.class).to(ExecutionManager.class).in(Singleton.class);
		              bind(StorageImpl.class).to(Storage.class);
		              bind(PersistenceManagerImpl.class).to(PersistenceManager.class);
		              bind(ConnectionManagerImpl.class).to(ConnectionManager.class).in(Singleton.class);
		              bind(LocalConfigurationImpl.class).to(LocalConfiguration.class).in(Singleton.class);
		              bind(LiveEnvironmentConfiguration.class).to(EnvironmentConfiguration.class);
		              //Mockup
		              bind(InfrastructureMockup.class).to(Infrastructure.class);
				}
			};

			return new GCatFeeder(binder);		
		}else {
			System.out.println("NO TEST INFRASTRUCTURE AVAILABLE");
			AbstractBinder binder = new AbstractBinder() {
				@Override
				protected void configure() {                    
					bind(FeederEngineImpl.class).to(FeederEngine.class);
					bind(CatalogueControllersManagerImpl.class).to(CatalogueControllersManager.class).in(Singleton.class);
					bind(CollectorsManagerImpl.class).to(CollectorsManager.class).in(Singleton.class);
					bind(ExecutionManagerImpl.class).to(ExecutionManager.class).in(Singleton.class);
					bind(PersistenceManagerImpl.class).to(PersistenceManager.class);
		              bind(ConnectionManagerImpl.class).to(ConnectionManager.class).in(Singleton.class);
		              bind(LocalConfigurationImpl.class).to(LocalConfiguration.class).in(Singleton.class);
		              bind(LiveEnvironmentConfiguration.class).to(EnvironmentConfiguration.class);
		              // Mockups 
		              bind(InfrastructureMockup.class).to(Infrastructure.class);
		              bind(StorageMockup.class).to(Storage.class);
				}
			};

			return new GCatFeeder(binder);		
		}
	}


	public static void setTestInfrastructure() {
		if(isTestInfrastructureEnabled()) {
			Properties props=new Properties();
			try{
				props.load(BaseTest.class.getResourceAsStream("/tokens.properties"));
			}catch(IOException e) {throw new RuntimeException(e);}
			if(!props.containsKey(testContext)) throw new RuntimeException("No token found for scope : "+testContext);
			SecurityTokenProvider.instance.set(props.getProperty(testContext));
			ScopeProvider.instance.set(testContext);
		}
	}
	
	public static boolean isTestInfrastructureEnabled() {
		return testContext!=null;
	}
}
