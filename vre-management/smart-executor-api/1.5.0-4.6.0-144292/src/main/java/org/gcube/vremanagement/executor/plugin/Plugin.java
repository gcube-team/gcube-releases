package org.gcube.vremanagement.executor.plugin;

import java.util.Map;
import java.util.UUID;

/**
 * This interface represent the contract for a plugin runnable by the executor.
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public abstract class Plugin<T extends PluginDeclaration> {
	
	protected UUID uuid;
	protected int iterationNumber;
	
	protected T pluginDeclaration;
	protected PercentageSetter percentageSetter;
	
	public Plugin(T pluginDeclaration){
		this.pluginDeclaration = pluginDeclaration;
		this.percentageSetter = new PercentageSetter() {
			
			@SuppressWarnings("unused")
			private int percentage = 0;
			
			@Override
			public void setPercentageEvolution(Integer integer) {
				this.percentage = integer;
			}
			
		};
	}
	
	/**
	 * @return the pluginDeclaration
	 */
	public T getPluginDeclaration() {
		return pluginDeclaration;
	}
	
	/**
	 * @param percentageSetter the percentageSetter to set
	 */
	public void setPercentageSetter(PercentageSetter percentageSetter) {
		this.percentageSetter = percentageSetter;
	}
	
	protected void setPercentageEvolution(Integer integer){
		//if(this.percentageSetter!=null){
			this.percentageSetter.setPercentageEvolution(integer);
		//}
	}
	
	/**
	 * @return the uuid
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the iterationNumner
	 */
	public int getIterationNumber() {
		return iterationNumber;
	}

	/**
	 * @param iterationNumner the iterationNumner to set
	 */
	public void setIterationNumber(int iterationNumber) {
		this.iterationNumber = iterationNumber;
	}

	/**
	 * Launch the plugin with the provided input.
	 * @param inputs
	 * @throws Exception if the launch fails
	 */
	public abstract void launch(Map<String,Object> inputs) throws Exception;
	
	/**
	 * This function is used to correctly stop the plugin 
	 * @throws Exception if the launch fails
	 */
	protected abstract void onStop() throws Exception;
	
	/**
	 * Invoke onStop() function to allow the plugin to safely stop the execution
	 * @throws Exception
	 */
	public void stop() throws Exception {
		onStop();
	}
	
}
