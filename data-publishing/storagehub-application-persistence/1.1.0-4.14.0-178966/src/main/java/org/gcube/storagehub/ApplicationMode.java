package org.gcube.storagehub;

import javax.ws.rs.InternalServerErrorException;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ApplicationMode {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationMode.class);
	
	private final String applicationToken;
	private final String originalToken;
	
	public ApplicationMode(String applicationToken) {
		this.applicationToken = applicationToken;
		String currentToken = SecurityTokenProvider.instance.get();
		if(applicationToken.compareTo(currentToken)!=0) {
			this.originalToken = currentToken;
		}else {
			logger.warn("You are already in application Mode. Operation on this instance will not have any effect.");
			this.originalToken = null;
		}
	}
	
	public synchronized void start() {
		if(originalToken!=null) {
			try {
				ContextUtility.setContext(applicationToken);
			}catch (Exception e) {
				throw new InternalServerErrorException(e);
			}
		}else {
			logger.warn("You are already in application Mode. start() does not provide any effect.");
		}
	}
	
	public synchronized  void end() {
		if(originalToken!=null) {
			try {
				ContextUtility.setContext(originalToken);
			}catch (Exception e) {
				throw new InternalServerErrorException(e);
			}
		}else {
			logger.warn("You are already in application Mode. end() does not provide any effect.");
		}
	}
	
}
