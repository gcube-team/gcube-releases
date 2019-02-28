package org.gcube.portlets.admin.accountingmanager.server.is;

import org.gcube.portlets.admin.accountingmanager.server.util.ThreadPoolInfo;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class BuildThreadPoolInfo {

	private static Logger logger = LoggerFactory
			.getLogger(BuildThreadPoolInfo.class);

	public static ThreadPoolInfo build(String scope) throws ServiceException {
		ThreadPoolInfo threadPoolInfo=null;
		
		if (Constants.DEBUG_MODE) {
			logger.debug("AccountingManager use default configuration for threadpool [scope="+scope+"]");
			threadPoolInfo=new ThreadPoolInfo(Constants.SERVICE_CLIENT_TIMEOUT_DEFAULT_MILLIS);
		} else {
			ThreadPoolJAXB threadPoolJAXB=null;
			try {
				threadPoolJAXB= InformationSystemUtils
					.retrieveThreadPoolTimeout(scope);
			} catch(ServiceException e){
				logger.debug(e.getLocalizedMessage());
			}
		
			logger.debug("ThreadPool: " + threadPoolJAXB);
			if (threadPoolJAXB != null && threadPoolJAXB.getTimeout() != null
					&& !threadPoolJAXB.getTimeout().isEmpty()) {
				long timeout=0;
				try {
					timeout=Long.parseLong(threadPoolJAXB.getTimeout());
				}catch(NumberFormatException e){
					logger.error("AccountingManager invalid timeout set in threadpool resource",e);
				}
				
				if(timeout<=0){
					logger.info("AccountingManager use default configuration for threadpool [scope="+scope+"]");
					threadPoolInfo=new ThreadPoolInfo(Constants.SERVICE_CLIENT_TIMEOUT_DEFAULT_MILLIS);
				} else {
					logger.info("AccountingManager use timeout="+timeout+" for threadpool [scope="+scope+"]");
					threadPoolInfo=new ThreadPoolInfo(timeout);
				}
				
			} else {
				logger.info("AccountingManager use default configuration for threadpool [scope="+scope+"]");
				threadPoolInfo=new ThreadPoolInfo(Constants.SERVICE_CLIENT_TIMEOUT_DEFAULT_MILLIS);
				
			}
		}
		
		logger.debug("ThreadPoolInfo: " + threadPoolInfo);
		return threadPoolInfo;
	}

}
