package org.gcube.common.core.monitoring;

import java.util.HashMap;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler.Mode;

/**
 * Abstract GCUBETestProbe
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public abstract class GCUBETestProbe  extends GCUBEHandler<GCUBETestProbe> implements GCUBEProbe{


	private String Description;
	protected Scheduler scheduler = null;
	protected  HashMap<GCUBEScope,GCUBEMessage> map = null;
	
	/**
	 * Default constructor
	 */
	public GCUBETestProbe() {
		scheduler = new Scheduler(GHNContext.DEFAULT_TEST_INTERVAL, Mode.EAGER,this);
		map = new HashMap<GCUBEScope,GCUBEMessage>();
	}

	/**
	 * Get the Probe description
	 * @return the  probe description
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * Set the probe description
	 * @param description
	 */
	public void setDescription(String description) {
		Description = description;
	}

	
	public long getInterval() {
		return scheduler.getInterval();
	}

	public void setInterval(long interval) {
		scheduler.setInterval(interval);
	}

	/**
	 * Execute the probe as a  {@link org.gcube.common.core.utils.handlers.GCUBEScheduledHandler}
	 * @throws Exception
	 */
	public void execute() throws Exception {
			scheduler.run();
		 }

	private class Scheduler extends GCUBEScheduledHandler<GCUBETestProbe> {
		
		
		public long getInterval(){
			return this.interval;
		}
		
		public void setInterval(long interval){
			this.interval = interval*1000;
		}
		public Scheduler(long interval, Mode mode,GCUBETestProbe probe) {
			super(interval, mode, probe);
		}
		
	}
	
}
