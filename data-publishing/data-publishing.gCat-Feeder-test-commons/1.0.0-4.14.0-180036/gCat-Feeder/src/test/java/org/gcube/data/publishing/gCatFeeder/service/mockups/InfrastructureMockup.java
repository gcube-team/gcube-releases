package org.gcube.data.publishing.gCatFeeder.service.mockups;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import org.gcube.data.publishing.gCatFeeder.service.BaseTest;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.InfrastructureUtilsImpl;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DatabaseConnectionDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DatabaseConnectionDescriptor.Flavor;

public class InfrastructureMockup extends InfrastructureUtilsImpl {

	
	static {
		try {
			org.h2.tools.Server.createTcpServer().start();
		} catch (SQLException e) {
			throw new RuntimeException("Unable to init in memory DB.",e);
		}
	}
	
	@Override
	public String getCurrentToken() {
		if(BaseTest.isTestInfrastructureEnabled()) {
			BaseTest.setTestInfrastructure();
			return super.getCurrentToken();
		}
		return "FAKE_TOKEN";
	}

	@Override
	public String getCurrentContext() {
		if(BaseTest.isTestInfrastructureEnabled()) {
			BaseTest.setTestInfrastructure();
			return super.getCurrentContext();
		}
		return "FAKE_CONTEXT";
	}

	@Override
	public String getCurrentContextName() {
		if(BaseTest.isTestInfrastructureEnabled()) {
			BaseTest.setTestInfrastructure();
			return super.getCurrentContextName();
		}
		return "FAKE";
	}

	@Override
	public String getClientID(String token){
		if(BaseTest.isTestInfrastructureEnabled()) {
			BaseTest.setTestInfrastructure();
			return super.getClientID(token);
		}
		return "FAKE_ID";
	}

	@Override
	public void setToken(String token) {
		if(BaseTest.isTestInfrastructureEnabled()) {
			BaseTest.setTestInfrastructure();
			super.setToken(token);
		}
	}

	
	@Override
	public String decrypt(String toDecrypt) {
		if(BaseTest.isTestInfrastructureEnabled()) {
			BaseTest.setTestInfrastructure();
			return super.decrypt(toDecrypt);
		}
		else return toDecrypt;
	}
	
	@Override
	public String encrypt(String toEncrypt) {
		if(BaseTest.isTestInfrastructureEnabled()) {
			BaseTest.setTestInfrastructure();
			return super.encrypt(toEncrypt);
		}
		return toEncrypt;
	}
	
	@Override
	public Map<String, String> getEnvironmentConfigurationParameters() {
		if(BaseTest.isTestInfrastructureEnabled())
			return super.getEnvironmentConfigurationParameters();
		else return Collections.emptyMap();
	}
	
	
	@Override
	public DatabaseConnectionDescriptor queryForDatabase(String category, String name) throws InternalError {
		if(BaseTest.isTestInfrastructureEnabled()) {
			BaseTest.setTestInfrastructure();
			return super.queryForDatabase(category, name);
		}else {
			String url="jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
//			String url="jdbc:h2:tcp://localhost:9092//data;DB_CLOSE_DELAY=-1";
			return new DatabaseConnectionDescriptor(null, url, null,Flavor.MYSQL);
			
		}
	}
}
