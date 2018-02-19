package org.gcube.common.core.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.contexts.GCUBEServiceContext.RILifetimeEvent;
import org.gcube.common.core.contexts.GHNContext.Mode;
import org.gcube.common.core.contexts.service.Consumer;
import org.gcube.common.core.faults.GCUBERetryEquivalentException;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.core.utils.proxies.AccessControlProxyContext.Restricted;

/**
 * Partial implementation of remote persistence managers for Running Instances of stateful gCube services.
 * 
 * @author Fabio Simeoni (University of Strathclyde) *
 */
public abstract class GCUBERIPersistenceManager {
	
	/** Instance Logger */
	protected GCUBELog logger = new GCUBELog(this);
	

	/** The context of the service. */
	protected GCUBEServiceContext ctxt;
	/** The configuration profile of the manager. */
	protected GCUBERIPersistenceManagerProfile profile;
	/** List of excludes directives. */
	protected List<Pattern> excludes = new ArrayList<Pattern>();
	
	/** Flags a state change. */
	private boolean commit=false;
	
	/** 
	 * Creates a new instance for a given service and from a given configuration profile.
	 * @param ctxt the context of the service.
	 * @param profile the configuration profile.
	 */
	public GCUBERIPersistenceManager(GCUBEServiceContext ctxt, GCUBERIPersistenceManagerProfile profile){
		this.ctxt=ctxt;
		this.profile=profile;
		if (profile.getExcludes()!=null) this.exclude(profile.getExcludes().split(","));
		this.logger.setContext(ctxt);
	}
	
	/**Sets the instance logger.
	 * @param logger the logger.*/
	public void setLogger(GCUBELog logger) {this.logger=logger;}
	
	/**Sets the commit flag.
	 * @param value the flag value.*/
	public synchronized void setCommit(boolean value) {this.commit=value;}
	
	/**Returns the commit flag value.
	 * @return the value.*/
	protected synchronized boolean getCommit() {return this.commit;}
		
	/**
	 * Commits the state of the running instance.
	 * @throws Exception if the state could not be committed.
	 */
	protected synchronized void commit() throws Exception {
		if (this.getCommit())	{
			logger.trace("committing RI state");
			this.setCommit(false);//storing might take time
			if (GHNContext.getContext().getMode()==Mode.CONNECTED) this.commitState();
		}
	}
	
	/**
	 * Invoked by {@link #commit()} to commit the state of the running instance.
	 * @throws Exception if the state could not be committed.
	 */
	protected abstract void commitState() throws Exception;
	
	/**
	 * Recovers the state of the running instance.
	 * @throws Exception if the state could not be recovered.
	 */
	@Restricted public synchronized void recover() throws Exception {

		logger.info("recovering RI state");
		
		if (GHNContext.getContext().getMode()==Mode.CONNECTED) {
			try {this.recoverState();}
			catch(StateNotFoundException e){logger.warn("remote state could not be retrieved");} 
			catch(Exception ignore) {logger.info("no remote state was found");}
		}
		
		
		if (profile.getMonitoringInterval()>0) //start monitoring, if required
			new GCUBEScheduledHandler<Object>(profile.getMonitoringInterval(),
								      GCUBEScheduledHandler.Mode.LAZY,
									  new GCUBEHandler<Object>() {
											public String getName(){return ctxt.getName()+"RIPersistenceMonitor";}
											public void run() throws Exception{commit();}}) {
				@Override protected boolean repeat(Exception exception, int exceptionCount) {
					if (exception!=null) 
						GCUBERIPersistenceManager.this.logger.warn("could not commit RI state (failures "+exceptionCount+")",exception);
					return true;
				}
				
			}.run();
		
		try {
			logger.info("subscribing for changes to the state of the RI");
			ctxt.subscribeLifetTime(new StateChangeConsumer(), GCUBEServiceContext.RILifetimeTopic.STATECHANGE);}
		catch(Exception e) {
			logger.error("could not subscribe for RI state changes",e);
			throw e;
		}
	}
	
	/**
	 * Invoked by {@link #recover()} to recover the state of the running instance.
	 * @throws Exception if the state could not be recovered.
	 */
	protected abstract void recoverState() throws StateNotFoundException, Exception;
	
	/**
	 * Add one or more regular expressions as exclude directives.
	 * @param excludes the regular expressions.
	 */
	public void exclude(String ...excludes) {
		for (String ex : excludes) this.excludes.add(Pattern.compile(ex));
	}
	/**
	 * Consumer of state change events.
	 * @author Fabio Simeoni (University of Strathclyde)
	 **/
	protected class StateChangeConsumer extends Consumer {
		@Override protected void onRIStateChange(RILifetimeEvent event) {setCommit(true);}
	}
	
	/**
	 * Signals the lack of remote state.
	 * @author Fabio Simeoni (University of Strathclyde)
	 **/
	public class StateNotFoundException extends GCUBERetryEquivalentException{
		private static final long serialVersionUID = 1L;
		@Override public String getMessage() {return "state not found";}
	}
}
