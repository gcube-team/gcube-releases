package org.gcube.data.access.httpproxy.access.timer;

import java.util.List;
import java.util.TimerTask;

import org.gcube.data.access.httpproxy.access.ISManager;
import org.gcube.data.access.httpproxy.access.URLCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshList extends TimerTask {

	private Logger logger;
	private ISManager isManager;
	private String scopeName;
	
	public RefreshList(String scopeName) {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.isManager = new ISManager();
		this.scopeName = scopeName;
	}
	
	@Override
	public void run() {
		logger.debug("Asking IS for a new List...");
		List<String> domains = isManager.getDomains();
		logger.debug("List obtained");
		this.logger.debug("List "+domains);
		this.logger.debug("Adding new list to the cache for scope "+scopeName);
		URLCache.getInstance().setDomainList(this.scopeName, domains);
		this.logger.debug("Operation completed");
	}

}
