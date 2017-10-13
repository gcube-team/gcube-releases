/**
 * 
 */
package org.gcube.vremanagement.executor.pluginmanager;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.accounting.datamodel.UsageRecord.OperationResult;
import org.gcube.accounting.datamodel.usagerecords.JobUsageRecord;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.smartgears.ContextProvider;
import org.gcube.vremanagement.executor.SmartExecutorInitializator;
import org.gcube.vremanagement.executor.exception.AlreadyInFinalStateException;
import org.gcube.vremanagement.executor.exception.InvalidPluginStateEvolutionException;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.plugin.PluginStateNotification;
import org.gcube.vremanagement.executor.plugin.Ref;
import org.gcube.vremanagement.executor.plugin.RunOn;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class RunnablePlugin<T extends Plugin<? extends PluginDeclaration>> implements Runnable {
	
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(RunnablePlugin.class);
	
	protected final T plugin;
	
	protected final Map<String, Object> inputs;
	
	protected final UUID uuid;
	protected final int iterationNumber;
	protected final List<PluginStateNotification> pluginStateNotifications;
	
	protected final String token;
	
	protected PluginStateEvolution actualStateEvolution;
	
	public RunnablePlugin(T plugin, Map<String, Object> inputs, 
			UUID uuid, int iterationNumber, List<PluginStateNotification> pluginStateNotifications, String token){
		this.plugin = plugin;
		this.plugin.setPercentageSetter(new PercentageSetterImpl<T>(this));
		this.inputs = inputs;
		this.uuid = uuid;
		this.iterationNumber = iterationNumber;
		this.pluginStateNotifications = pluginStateNotifications;
		this.token = token;
		try {
			SecurityTokenProvider.instance.set(token);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		try {
			setState(PluginState.CREATED);
		} catch (AlreadyInFinalStateException | InvalidPluginStateEvolutionException e) {
			logger.error(" --- You should not be here. Seem that the {} is suspended before the instance is created. This is really STRANGE.", 
					uuid);
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	public void run(){
		String pluginName = plugin.getPluginDeclaration().getName(); 
		
		logger.info("{} : {} is going to be launched (UUID={}, iterationNumber={}) with the following inputs {}", 
				pluginName, plugin.getPluginDeclaration().getVersion(), 
				uuid, iterationNumber, inputs);
		
		JobUsageRecord jobUsageRecord = new JobUsageRecord();
		
		long startTime = actualStateEvolution.getTimestamp();
		
		try {
			SmartExecutorInitializator.setContext(token);
			
			setState(PluginState.RUNNING);
			
			jobUsageRecord.setJobName(pluginName);
			
			RunOn runOn = ScheduledTask.generateRunOn();
			Ref hnRef = runOn.getHostingNode();
			jobUsageRecord.setHost(hnRef.getAddress());
			
			
			ClientInfo clientInfo = SmartExecutorInitializator.getClientInfo();
			String consumerId = clientInfo.getId();
			jobUsageRecord.setConsumerId(consumerId);
			
			try {
				AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
				String callerQualifier = authorizationEntry.getQualifier();
				jobUsageRecord.setCallerQualifier(callerQualifier);
			}catch (Exception e) {
				jobUsageRecord.setCallerQualifier("UNKNOWN");
			}
			
			jobUsageRecord.setServiceClass(ContextProvider.get().configuration().serviceClass());
			jobUsageRecord.setServiceName(ContextProvider.get().configuration().name());
			
			
			this.plugin.setUUID(uuid);
			this.plugin.setIterationNumber(iterationNumber);
			this.plugin.launch(inputs);
			setState(PluginState.DONE);
		} catch (AlreadyInFinalStateException e1) {
			return;
		} catch(Exception e) {
			logger.trace(String.format("Thread %s failed", this.toString()),e);
			try {
				setState(PluginState.FAILED, e);
			} catch (AlreadyInFinalStateException | InvalidPluginStateEvolutionException e1) {
				return;
			}
			throw new RuntimeException(e);
		}finally {
			AccountingPersistence accountingPersistence = 
					AccountingPersistenceFactory.getPersistence();
			try {
				long endTime = actualStateEvolution.getTimestamp();
				long duration = endTime - startTime;
				jobUsageRecord.setDuration(duration);
				
				PluginState pluginState = actualStateEvolution.getPluginState();
				switch (pluginState) {
					case DONE:
						jobUsageRecord.setOperationResult(OperationResult.SUCCESS);
						break;
	
					default:
						jobUsageRecord.setOperationResult(OperationResult.FAILED);
						break;
				}
				
				accountingPersistence.account(jobUsageRecord);
				
			} catch (InvalidValueException e) {
				logger.error("Unable to account {}", jobUsageRecord, e);
			}

		}
	}
	
	/**
	 * @return the plugin
	 */
	public T getPlugin() {
		return plugin;
	}

	/**
	 * @return the launchInputs
	 */
	public Map<String, Object> getInputs() {
		return inputs;
	}
	
	protected synchronized void setPercentage(Integer percentage) throws AlreadyInFinalStateException, InvalidPluginStateEvolutionException {
		PluginState pluginState = actualStateEvolution.getPluginState();
		if(pluginState != PluginState.RUNNING){
			throw new InvalidPluginStateEvolutionException("Percentage can be set only for runnign plugin");
		}
		setState(pluginState, percentage, null);
	}
	
	public synchronized void setState(PluginState pluginState) throws AlreadyInFinalStateException, InvalidPluginStateEvolutionException {
		Integer percentage = 0;
		if(actualStateEvolution!=null){
			percentage = actualStateEvolution.getPercentage();
		}
		
		if(pluginState==PluginState.DONE){
			percentage = 100;
		}
		setState(pluginState, percentage, null);
	}
	
	public synchronized void setState(PluginState pluginState, Exception e) throws AlreadyInFinalStateException, InvalidPluginStateEvolutionException {
		Integer percentage = 0;
		if(actualStateEvolution!=null){
			percentage = actualStateEvolution.getPercentage();
		}
		
		Exception exception = null;
		if(pluginState == PluginState.FAILED){
			exception = new Exception(e);
		}
		
		setState(pluginState, percentage, exception);
	}
	/**
	 * It is up to the plugin update the State of the Running Plugin using 
	 * this facilities function.
	 * @param pluginState
	 * @throws Exception 
	 */
	protected void setState(PluginState pluginState, Integer percentage, Exception exception) throws AlreadyInFinalStateException, InvalidPluginStateEvolutionException {
		long timestamp =  Calendar.getInstance().getTimeInMillis();
		if(actualStateEvolution!=null && actualStateEvolution.getPluginState().isFinalState()){
			logger.trace("At {} Trying to set {} in {} state, but it was already in the final state {}", timestamp,
					uuid, pluginState.toString(), actualStateEvolution.toString());
			throw new AlreadyInFinalStateException();
		}

		PluginStateEvolution pluginStateEvolution = new PluginStateEvolution(uuid, iterationNumber, timestamp, plugin.getPluginDeclaration(), pluginState, percentage);
		
		for(PluginStateNotification pluginStateNotification : pluginStateNotifications){
			String pluginStateNotificationName = pluginStateNotification.getClass().getSimpleName();
			try {
				logger.debug("Notifing Plugin State Evolution {} to {}.", pluginStateEvolution, pluginStateNotificationName);
				pluginStateNotification.pluginStateEvolution(pluginStateEvolution, exception);
			} catch(Exception e) {
				logger.error("Unable to Notify Plugin State Evolution {} to {}.", 
						pluginStateEvolution, pluginStateNotificationName);
			}
		}
		actualStateEvolution = pluginStateEvolution;
	}
	
	@Override
	public String toString(){
		return String.format("UUID : %s, Iteration : %d,  Plugin : %s", 
				uuid.toString(),  iterationNumber,  
				plugin.getPluginDeclaration().getName());
	}
	
	/**
	 * Stop the Plugin setting state to {@link PluginState#CANCELLED}
	 * @throws Exception
	 */
	public void stop() throws Exception {
		try{
			setState(PluginState.STOPPED);
			plugin.stop();
		}catch(AlreadyInFinalStateException e){}
		
		Thread.currentThread().interrupt();
	}

}
