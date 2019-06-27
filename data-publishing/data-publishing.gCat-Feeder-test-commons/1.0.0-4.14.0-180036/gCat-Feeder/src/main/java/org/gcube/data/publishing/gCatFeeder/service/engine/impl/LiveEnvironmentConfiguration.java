package org.gcube.data.publishing.gCatFeeder.service.engine.impl;

import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;
import org.gcube.data.publishing.gCatFeeder.service.engine.Infrastructure;

public class LiveEnvironmentConfiguration implements EnvironmentConfiguration {

	@Inject
	private Infrastructure infra; 
	
	
	@Override
	public Map<String, String> getCurrentConfiguration() {
		return infra.getEnvironmentConfigurationParameters();
	}

}
