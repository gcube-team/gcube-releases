package org.acme;

import java.util.Map;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
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
	
	
	public String getScopeFromToken(){
		String token = SecurityTokenProvider.instance.get();
		AuthorizationEntry authorizationEntry;
		try {
			authorizationEntry = Constants.authorizationService().get(token);
			String clientID = authorizationEntry.getClientInfo().getId();
			logger.debug("Current Client ID is {}", clientID);
		} catch (Exception e) {
			String scope = ScopeProvider.instance.get();
			logger.debug("Current Context (using ScopeProvider) is {}", scope);
			return scope;
		}
		
		String currentContext = authorizationEntry.getContext();
		logger.debug("Current Context (using Authorization) is {}", currentContext);
		return currentContext;
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
		
		getScopeFromToken();
		
		this.setPercentageEvolution(20);
		logger.debug("{} - Inputs : {}", this.getClass().getSimpleName(), inputs);
		
		this.setPercentageEvolution(30);
		long sleepTime = (long) inputs.get(SLEEP_TIME);
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
