/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public abstract class ExtendedTimer {

	protected boolean canceled = false;
	
	public void cancel()
	{
		Log.trace("ExtendedTimer CANCEL");
		canceled = true;
	}
	
	public abstract void run();
	
	public void scheduleRepeating(int delayMs)
	{
		canceled = false;
		Log.trace("ExtendedTimer scheduleRepeating delayMs: "+delayMs);
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				Log.trace("ExtendedTimer RUNNING");
				run();
				Log.trace("ExtendedTimer RUN COMPLETE returning "+(!canceled));
				return !canceled;
			}
		}, delayMs);
	}

}
