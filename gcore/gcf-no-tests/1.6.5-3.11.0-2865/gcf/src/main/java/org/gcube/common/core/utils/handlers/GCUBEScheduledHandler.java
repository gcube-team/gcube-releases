package org.gcube.common.core.utils.handlers;

import org.gcube.common.core.utils.handlers.lifetime.Lifetime;
import org.gcube.common.core.utils.handlers.lifetime.State.Done;
import org.gcube.common.core.utils.handlers.lifetime.State.Failed;
import org.gcube.common.core.utils.handlers.lifetime.State.Running;
import org.gcube.common.core.utils.handlers.lifetime.State.Suspended;

/**
 * An extension of {@link GCUBEComplexHandler} which schedules the execution 
 * of a single {@link GCUBEHandler}, the </em>scheduled handler</em>, at regular time intervals.
 * <p>
 * The execution of the scheduled handler occurs in accordance with either one of the two <em>modes</em> 
 * enumerated by {@link Mode}</code>.  In <code>Mode.EAGER</code>, the first execution of the scheduled handler
 * occurs immediately. In <code>Mode.LAZY</code>, it occurs after the first elapse of the time interval.
 * <p>
 * A {@link GCUBEScheduledHandler} may be halted in between iteration of the schedule, either explicitly, by
 * invoking {@link #stop()}, or implicitly, by overriding {@link #repeat(Exception, int)} to implement arbitrary
 * halting conditions. 
 *  
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBEScheduledHandler<HANDLED> extends GCUBEHandler<HANDLED> implements Lifetime<HANDLED> {

		/** Time interval. */
		protected long interval;
		/** Stop flag. */
		protected boolean repeat=true;
		
		/** Possible modes of execution. */
		public static enum Mode{EAGER,LAZY};
		
		/**The last exception raised during the schedule.*/
		protected Exception exception;
		
		/**The number of exceptions raised at any point of the schedule.*/
		protected int exceptionCount;
		
		/** The scheduled handler. */
		private GCUBEHandler<HANDLED> scheduled;
		
		/** The execution mode.*/
		private Mode mode = Mode.EAGER;
		
		/**Creates an instance.*/
		public GCUBEScheduledHandler() {}
		
		/** Creates an instance with a given execution mode, time interval, and scheduled handler. 
		 * 
		 * @param interval the time interval in seconds.
		 * @param mode the execution mode.
		 * @param handler (optional) the scheduled handler.
		 * */
		public GCUBEScheduledHandler(long interval, Mode mode, GCUBEHandler<HANDLED> ...handler) {	
			this.setInterval(interval);
			this.setMode(mode);
			if (handler!=null && handler.length>0) this.setScheduled(handler[0]);
			
		}	
		
		/**Returns the interval of the schedule.
		 * @return the interval.*/
		public long getInterval() {return interval;}

		/**Sets the interval of the schedule.
		 * @param the interval.*/
		public void setInterval(long interval) {this.interval=interval*1000l;}
		
		/**
		 * Returns the scheduled handler.
		 * @return the scheduled handled.
		 */
		public GCUBEHandler<HANDLED> getScheduled() {return this.scheduled;}

		/**
		 * Sets the scheduled handler.
		 * @param scheduled the scheduled handled.
		 */
		public void setScheduled(GCUBEHandler<HANDLED> scheduled) {this.scheduled=scheduled;}
		
		/**
		 * Returns the execution mode of the <code>ScheduledHandler</code>.
		 * @return mode the mode.
		 */
		public Mode getMode() {return mode;}

		/**
		 * Sets the execution mode of the <code>ScheduledHandler</code>. 
		 * @param mode the mode.
		 */
		public void setMode(Mode mode) {this.mode = mode;}
		
		/**{@inheritDoc}*/
		public void run() throws Exception {
			
			//inject managers in scheduled handled.
			if (getSecurityManager()!=null) getScheduled().setSecurityManager(getSecurityManager());
			if (getScopeManager()!=null) {getScheduled().setScopeManager(getScopeManager());}
			
			if(this.mode==Mode.EAGER) {
				//logger.debug(getHandled().getName()+" is eagerly running");
				boolean doNext = this.onIteration(); 
				if (this.exception!=null) throw new Exception(this.getScheduled().getName()+" has failed",this.exception);
				if (!doNext) return;

			}		
			
			setState(Running.INSTANCE);
		
			Thread schedulerThread = new Thread(this.getScheduled().getName()) {			
				public void run() {
					//schedule until it's time to quit or an exception has been raised
					do {
						try {
							GCUBEScheduledHandler.this.setState(Suspended.INSTANCE);
							Thread.sleep(GCUBEScheduledHandler.this.interval);//wait
							//logger.debug(getHandled().getName()+" is waking up");						
						} catch (Exception e) {logger.warn("some unlikely problem",e);}			
					} while (GCUBEScheduledHandler.this.onIteration());
					GCUBEScheduledHandler.this.setState(Done.INSTANCE);
				}
			};
			
			//propagate credentials and scope to new thread.
			if (getSecurityManager()!=null) getSecurityManager().useCredentials(schedulerThread);
			
			schedulerThread.setDaemon(true);
			schedulerThread.start(); //start background updater
		}
		
		/**
		 * Used internally to perform an iteration of the schedule and to indicate whether it should continue.
		 * @return <code>true</code> if the schedule should continue, <code>false</code> otherwise.
		 */
		protected boolean onIteration() {
			
			this.setState(Running.INSTANCE);
			exception = null;
			try {
				if (this.getScheduled()!=null) this.getScheduled().run();
				this.exceptionCount=0; //zero count;
			}
			catch(Exception e) {
				exception=e;
				exceptionCount++;
			}
			
			return (this.repeat && repeat(exception, exceptionCount));
					
		}
		
		/**Stops the scheduling.*/
		public void stop() {this.repeat=false;}
		
		/**
		 * Indicates whether the scheduling should continue.
		 * It is invoked after each iteration of the schedule as a pre-condition to continue it.
		 * By default, it returns <code>true</code> if no exception occurred during the iteration,
		 * and <code>false</code> otherwise. Override to implement
		 * specific conditions.
		 * @param exception any exception which may have occurred during the iteration, 
		 * or <code>null</code> if the iteration completed successfully.
		 * @param exceptionCount the number of exceptions which have occurred so far during the schedule.
		 * @return <code>true</code> if the scheduling should continue, <code>false</code> otherwise.
		 */
		protected boolean repeat(Exception exception, int exceptionCount)  {
			
			//default policiy: continue in the lack of error or else log it
			if (exception!=null) {
				logger.error(this.getScheduled().getName()+" has failed",exception);
				setState(Failed.INSTANCE);
				return false;
			}
			return true;
		}
				
}
