package org.gcube.common.core.utils.handlers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.core.utils.handlers.lifetime.Lifetime;
import org.gcube.common.core.utils.handlers.lifetime.State.Done;
import org.gcube.common.core.utils.handlers.lifetime.State.Failed;
import org.gcube.common.core.utils.handlers.lifetime.State.Running;



/**
 * An extension of {@link GCUBEComplexHandler} which parallelises the execution of
 * the component handlers. A {@link GCUBEParallelHandler} propagates 
 * its state to all the component handlers (non-destructively). 
 * Failures are handled in accordance with either one of the two <em>modes</em> 
 * enumerated by {@link Mode}.  
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 * @param <HANDLED> the type of the handled object of the component handlers.
 */
public class GCUBEParallelHandler<HANDLED> extends GCUBEComplexHandler<HANDLED> implements Lifetime<HANDLED> {	

	/**
	 * Enumeration of the two failure handling modes of a {@link GCUBEParallelHandler}. 
	 * In {@link Mode#STRICT} the handler fails as soon one of its component handlers does.
	 * {@link GCUBEParallelHandler#undo()} is then invoked on all component handlers
	 * when all of them have terminated. In {@link Mode#LAX}, the handler logs the
	 * failures but executes until all component handlers have completed theirs (successfully or not).
	 *
	 */
	public enum Mode{STRICT,LAX}; 
	
	/** Failure mode for the execution of the handler. */
	protected GCUBEParallelHandler.Mode mode; 

	/** Per-component handler threads which have failed during the execution of the handler. */
	protected List<SlaveThread> failedSlaves = new ArrayList<SlaveThread>();
	
	/** Per-component handler threads spawned by the handler. */
	protected List<SlaveThread> runningSlaves = new ArrayList<SlaveThread>();
	
	/** Component handlers which have failed during the execution of the handler. */
	protected Map<GCUBEIHandler<? extends HANDLED>,Exception> failedHandlers = new HashMap<GCUBEIHandler<? extends HANDLED>,Exception>();
	
	
	/**
	 * Creates an instance with a list of component handlers.
	 * @param components the component handlers.
	 */
	public GCUBEParallelHandler(GCUBEIHandler<? extends HANDLED> ... components) {super(components);}

	/**
	 * Creates an handler with a given failure handling mode.
	 * @param mode the failure handling mode.
	 */
	public GCUBEParallelHandler(GCUBEParallelHandler.Mode mode) {this.mode=mode;}
	
	
	/**
	 * Returns the failure handling mode of the handler.
	 * @return the mode.
	 */
	public Mode getMode() {return mode;}

	/**
	 * Sets the failure handling mode of the handler. 
	 * @param mode the mode.
	 */
	public void setMode(Mode mode) {this.mode = mode;}
	
	/**
	 * Returns the component handlers which have failed during execution of the handler, along with the
	 * corresponding failures.
	 * @return a list of (handler,exception) pairs.
	 */
	public Map<GCUBEIHandler<? extends HANDLED>, Exception> getFailedHandlers() {return failedHandlers;}
	
	/**{@inheritDoc}*/
	synchronized public void run() throws Exception {

		setState(Running.INSTANCE);
		synchronized(this) {//gets exclusive lock on own monitor
			
			//spawn slave threads
			for (GCUBEIHandler<? extends HANDLED> component : this.getHandlers()) {// for each component handler
				SlaveThread slave = new SlaveThread(component);
				if (getSecurityManager()!=null) {
					component.setSecurityManager(getSecurityManager());
					getSecurityManager().useCredentials(slave);//sets credentials for spawned thread
				}
				slave.start();
				runningSlaves.add(slave);
			}

			
			while (runningSlaves.size()>0) {//repeat until all slaves are done
				
				this.wait();
				//logger.debug(getName()+":running...");
				
				for (SlaveThread error : failedSlaves) {//process latest outcomes
					
					if (mode==Mode.LAX) {//log warning and continue
						logger.warn(error.handler.getName()+" has failed",error.exception);
					}
					else {//log error, start sweeper, and exit
						logger.error(error.handler.getName()+" has failed",error.exception);
						new SweeperThread().start();
						setState(Failed.INSTANCE);
						throw new Exception(error.handler.getName()+" has failed",error.exception);
					}
				}
				failedSlaves.clear();//avoid processing outcomes again
			}
			
			setState(Done.INSTANCE);
		}

	}
	

	/**
	 * An extension of {@link Thread} which executes a component handler and records its potential errors.
	 * 
	 * @author Fabio Simeoni (University of Strathclyde)
	 *
	 */
	protected class SlaveThread extends Thread {
		
		/** The component handler. */
		GCUBEIHandler<? extends HANDLED> handler;
		
		/** The exception. */
		Exception exception;
		
		/**
		 * Creates a thread for executing a component handler.
		 * @param handler the component handler.
		 */
		SlaveThread(GCUBEIHandler<? extends HANDLED> handler) {this.handler = handler;}
		
		/**{@inheritDoc}*/
		public void run() {

			try {handler.run();}
			catch(Exception e) {exception=e;} //records exception

			synchronized(GCUBEParallelHandler.this) {
				if (exception!=null) {
					//logger.debug(getName()+":notifying of error..");
					failedSlaves.add(this);//got to do this when holding lock
					failedHandlers.put(this.handler,this.exception);
				}
				runningSlaves.remove(this);
				GCUBEParallelHandler.this.notify();

			}	
			
		}
		
	}

	/**
	 * An extension of {@link Thread} which waits for all component handlers to terminate 
	 * before invoking {@link GCUBEParallelHandler#undo()}.
	 * 
	 * @author Fabio Simeoni (University of Strathclyde)
	 *
	 */
	protected class SweeperThread extends Thread {
		
		/**{@inheritDoc}*/
		public void run() {
			
			synchronized (GCUBEParallelHandler.this) {
				
				while (runningSlaves.size()>0) {
					//logger.debug(getName()+":sweeper running...");
					try {GCUBEParallelHandler.this.wait();}
					catch (InterruptedException e) {return;} //not going to happen
				}
			}	

			//propagates handler's failure to component handlers
			GCUBEParallelHandler.this.undo();
				
		}
		
		
	}

	
}