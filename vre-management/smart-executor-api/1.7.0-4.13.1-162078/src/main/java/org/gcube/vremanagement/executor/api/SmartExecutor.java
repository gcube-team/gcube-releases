package org.gcube.vremanagement.executor.api;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.exception.ExecutorException;
import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.LaunchException;
import org.gcube.vremanagement.executor.exception.PluginInstanceNotFoundException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;

/**
 * Service Endpoint Interface
 * @author Luca Frosini (ISTI - CNR)
 */
@WebService(serviceName = SmartExecutor.WEB_SERVICE_SERVICE_NAME, targetNamespace=SmartExecutor.TARGET_NAMESPACE)
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface SmartExecutor {
	
	public static final String TARGET_NAMESPACE = "http://gcube-system.org/";
	public static final String WEB_SERVICE_SERVICE_NAME = 
			"gcube/vremanagement/smart-executor";
	
	/**
	 * Launch the plugin identified by the name provided as parameters
	 * with the provided inputs. Inputs cannot be null. If you need to launch 
	 * an execution with no inputs provide an empty Map.
	 * This method return as soon as the plugin has been launched.
	 * The execution is made in a separated Thread. 
	 * @param launchParameter which contains the name of the plugin to launch 
	 * and the input to be provided to plugin to run and the scheduling 
	 * strategy.
	 * @return the UUID execution identifier as String which identify the 
	 * execution of the plugin.
	 * @throws InputsNullException if {@link LaunchParameter} contains null 
	 * inputs.
	 * @throws PluginNotFoundException if {@link LaunchParameter} contains a 
	 * name of a plugin which is not available on classpath
	 * @throws LaunchException if an error occurs trying to instantiate and/or
	 * launch the plugin execution
	 * @throws ExecutorException if any other undefined error occur
	 */
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public String launch(LaunchParameter launchParameter) throws 
		InputsNullException, PluginNotFoundException, 
		LaunchException, ExecutorException;
	
	/**
	 * The method use the provided UUID execution identifier as String to 
	 * stop the last running execution of a Task (if any).
	 * If the Task is a scheduled Task the current execution is stopped but the
	 * next one will occur. To remove the 
	 * @param executionIdentifier UUID as String which identify the execution
	 * @return return true if the current execution has been correctly 
	 * stopped. False otherwise.
	 * @throws Exception if there is no Task identified by the 
	 * provided UUID execution identifier as String
	 */
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public boolean stop(String executionIdentifier) throws ExecutorException;
	
	/**
	 * The method use the provided UUID execution identifier as String to 
	 * stop the last running execution of a Task (if any) and release the
	 * the scheduling if the Task is a Scheduled Task.
	 * If the identified Task is not a Scheduled Task, the only effect is 
	 * stopping the current execution. In other word has the same side effect 
	 * of invoking {@link SmartExecutor#stop(String)} method.
	 * @param executionIdentifier UUID as String which identify the execution
	 * @param globally a boolean which when true indicate if releasing the 
	 * Scheduled Task globally, so that no other SmartExecutor instance will 
	 * take in charge the scheduling. When false this invocation has the same
	 * side effect of invoking {@link SmartExecutor#unSchedule(String, boolean)}.
	 * @return return true if the current execution has been correctly 
	 * stopped and the Task was unscheduled. False otherwise.
	 * @throws Exception if there is no Task identified by the 
	 * provided UUID execution identifier as String
	 */
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public boolean unSchedule(String executionIdentifier, boolean globally) 
			throws ExecutorException;
	
	/**
	 * Deprecated Use {@link SmartExecutor#getStateEvolution(String)} instead
	 * The method use the provided UUID as String to retrieve the status of the 
	 * associated execution 
	 * @param executionIdentifier UUID as String which identify the execution
	 * @return {@link PluginState} which contains the state of the execution
	 * @throws Exception if there is no execution identified by the provided 
	 * UUID execution identifier as String
	 */
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	@Deprecated
	public PluginState getState(String executionIdentifier) 
			throws PluginInstanceNotFoundException, ExecutorException;
	
	/**
	 * The method use the provided UUID as String to retrieve the status of the 
	 * associated execution 
	 * @param executionIdentifier UUID as String which identify the execution
	 * @return {@link PluginState} which contains the state of the execution
	 * @throws Exception if there is no execution identified by the provided 
	 * UUID execution identifier as String
	 */
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public PluginStateEvolution getStateEvolution(String executionIdentifier) 
			throws PluginInstanceNotFoundException, ExecutorException;
	
	/**
	 * Deprecated Use {@link SmartExecutor#getIterationStateEvolution(String, int)} instead
	 * The method use the provided UUID as String and the iteration number
	 * to retrieve the status of the associated execution 
	 * @param executionIdentifier UUID as String which identify the execution
	 * @param iterationNumber iteration number
	 * @return {@link PluginState} which contains the state of the execution
	 * @throws Exception if there is no execution identified by the provided 
	 * UUID execution identifier as String
	 */
	@Deprecated
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public PluginState getIterationState(
			String executionIdentifier, int iterationNumber) 
			throws PluginInstanceNotFoundException, ExecutorException;
	
	
	/**
	 * The method use the provided UUID as String and the iteration number
	 * to retrieve the status of the associated execution 
	 * @param executionIdentifier UUID as String which identify the execution
	 * @param iterationNumber iteration number
	 * @return {@link PluginStateEvolution} which contains the state of the execution
	 * @throws Exception if there is no execution identified by the provided 
	 * UUID execution identifier as String
	 */
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public PluginStateEvolution getIterationStateEvolution(
			String executionIdentifier, int iterationNumber) 
			throws PluginInstanceNotFoundException, ExecutorException;
}
