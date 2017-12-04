package org.acme;

import java.util.Map;

import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.InvalidInputsException;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class HelloWorldPlugin extends Plugin<HelloWorldPluginDeclaration> {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(HelloWorldPlugin.class);
	
	public static final String SLEEP_TIME = "sleepTime";
	
	public HelloWorldPlugin(HelloWorldPluginDeclaration pluginDeclaration) {
		super(pluginDeclaration);
		logger.debug("contructor");
	}
	
	/**{@inheritDoc}*/
	@Override
	public void launch(Map<String, Object> inputs) throws Exception {
		logger.debug("My UUID is {}", uuid.toString());
		logger.debug("My iteration number is {}", iterationNumber);
		
		logger.debug("Launching HelloWorld");
		if(inputs == null){
			throw new InputsNullException();
		}else if(inputs.isEmpty() || !inputs.containsKey(SLEEP_TIME)){
			logger.debug("HelloWorld inputs {} are not valid", inputs);
			throw new InvalidInputsException();
		}
		
		this.setPercentageEvolution(20);
		logger.debug("{} - Inputs : {}", this.getClass().getSimpleName(), inputs);
		
		this.setPercentageEvolution(30);
		
		Long sleepTime;
		Object sleepTimeObject = inputs.get(SLEEP_TIME);
		if(sleepTimeObject instanceof Integer){
			sleepTime = new Long((Integer) sleepTimeObject);
		}else if(sleepTimeObject instanceof Long){
			sleepTime = (Long) sleepTimeObject;
		} else {
			sleepTime = new Long(sleepTimeObject.toString());
		}
		
		Thread.sleep(sleepTime);
		
		this.setPercentageEvolution(90);
		logger.debug("HelloWorld finished");
	}

	/**{@inheritDoc}*/
	@Override
	protected void onStop() throws Exception {
		logger.debug("onStop()");
		Thread.currentThread().interrupt();
	}

}
