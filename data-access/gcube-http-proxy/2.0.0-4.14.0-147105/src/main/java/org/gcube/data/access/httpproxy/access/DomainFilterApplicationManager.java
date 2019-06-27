package org.gcube.data.access.httpproxy.access;

import java.util.Timer;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.access.httpproxy.access.timer.RefreshList;
import org.gcube.data.access.httpproxy.utils.Properties;
import org.gcube.data.access.httpproxy.utils.Properties.BooleanPropertyType;
import org.gcube.data.access.httpproxy.utils.Properties.LongPropertyType;
import org.gcube.smartgears.ApplicationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainFilterApplicationManager implements ApplicationManager {

	private Logger logger;
	
	private boolean enabled;
	
	private Timer timer;
	private long timerPeriod;
	
	public DomainFilterApplicationManager() {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.enabled = Properties.getInstance().getProperty(BooleanPropertyType.ENABLED);
		this.timerPeriod = Properties.getInstance().getProperty(LongPropertyType.DEFAULT_PERIOD)*1000;
		this.timer = new Timer();
	}
	
	@Override
	public void onInit() 
	{
	
		if (enabled)
		{
			logger.debug("Starting IS client timer task");
			logger.debug("Timer period "+this.timerPeriod+" ms");
			String scope = ScopeProvider.instance.get();
			logger.debug("For scope "+scope);
			this.timer.schedule(new RefreshList(scope), 0, this.timerPeriod);
			
		}
		else
		{
			logger.debug("Domain filter disabled");
		}
	
	}

	@Override
	public void onShutdown() {
		this.timer.cancel();
		logger.debug("Timer task stopped");
	
	}

	
}
