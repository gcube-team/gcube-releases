package org.acme;

import java.util.Map;

import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.InvalidInputsException;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
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
		logger.debug("Launching HelloWorld");
		if(inputs == null){
			throw new InputsNullException();
		}else if(inputs.isEmpty() || !inputs.containsKey(SLEEP_TIME)){
			logger.debug("HelloWorld inputs {} are not valid", inputs);
			throw new InvalidInputsException();
		}
		logger.debug("{} - Inputs : {}", this.getClass().getSimpleName(), inputs);
		
		long sleepTime = (long) inputs.get(SLEEP_TIME);
		Thread.sleep(sleepTime);
		logger.debug("HelloWorld finished");
	}

	/**{@inheritDoc}*/
	@Override
	protected void onStop() throws Exception {
		logger.debug("onStop()");
		Thread.currentThread().interrupt();
	}

}
