package org.gcube.vremanagement.executor.api.rest;

import java.util.UUID;

import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.exception.ExecutorException;
import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.LaunchException;
import org.gcube.vremanagement.executor.exception.PluginInstanceNotFoundException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;

public interface SmartExecutor {
	
	public String launch(String launchParameterString) throws ExecutorException;
	
	public UUID launch(LaunchParameter launchParameter)
			throws InputsNullException, PluginNotFoundException, LaunchException, ExecutorException;
	
	public String getPluginStateEvolution(String executionIdentifier, Integer iterationNumber) throws PluginInstanceNotFoundException, ExecutorException;
	
	public PluginStateEvolution getPluginStateEvolution(UUID executionIdentifier, Integer iterationNumber)
			throws PluginInstanceNotFoundException, ExecutorException;
	
	public boolean delete(String executionIdentifier, boolean globally)
			throws ExecutorException;
	
	public boolean delete(UUID executionIdentifier, boolean globally)
			throws ExecutorException;

}
