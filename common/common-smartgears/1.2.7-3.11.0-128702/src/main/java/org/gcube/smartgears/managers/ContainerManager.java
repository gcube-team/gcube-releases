package org.gcube.smartgears.managers;

import static org.gcube.smartgears.lifecycle.container.ContainerState.active;
import static org.gcube.smartgears.lifecycle.container.ContainerState.down;
import static org.gcube.smartgears.lifecycle.container.ContainerState.failed;
import static org.gcube.smartgears.lifecycle.container.ContainerState.partActive;
import static org.gcube.smartgears.lifecycle.container.ContainerState.stopped;
import static org.gcube.smartgears.provider.ProviderFactory.provider;

import java.util.*;

import org.gcube.common.events.*;
import org.gcube.common.events.Observes.Kind;
import org.gcube.smartgears.configuration.container.*;
import org.gcube.smartgears.context.application.*;
import org.gcube.smartgears.context.container.*;
import org.gcube.smartgears.handlers.container.*;
import org.gcube.smartgears.lifecycle.application.*;
import org.slf4j.*;

/**
 * Coordinates management of the container as a gCube resource.
 * 
 * @author Fabio Simeoni
 * 
 */
public class ContainerManager {

	private static Logger log = LoggerFactory.getLogger(ContainerManager.class);
	
	public static ContainerManager instance = new ContainerManager();
	
	private ContainerContext context;
	
	private ContainerPipeline pipeline;
	
	private ContainerManager() {}
	
	/**
	 * Starts container management.
	 */
	public ContainerContext start(ContainerContext context) {
		
		this.context = context;
		
		try {
			
			// TODO Ask if is not enough that is already done in 
			// Bootstrap.initialiseContainer() function;
			context.configuration().validate();
		
			ContainerHandlers handlers = provider().containerHandlers();
			
			log.trace("managing container lifecycle with {}", handlers.get());
			
			startHandlers(handlers.get());
			
			context.lifecycle().moveTo(active);
			
			return context;
		}
		catch(RuntimeException e) {
			
			log.error("cannot manage container (see cause)",e);
			
			if (context!=null)
				context.lifecycle().moveTo(failed);
			
			throw e;
		}
		
	}
	
	public void manage(ApplicationContext app) {
		
		app.events().subscribe(this); 
		
	}
	
	@Observes(value={ApplicationLifecycle.failure,ApplicationLifecycle.stop},kind=Kind.critical)
	void monitorApplication(ApplicationLifecycle lifecycle) {
		
		context.lifecycle().tryMoveTo(partActive);
	}
	
	/**
	 * Stops container management on remote request.
	 * 
	 */
	public void stop() {

		stop(false);
	}

	
	/**
	 * Stops container management on remote request or container shutdown.
	 * 
	 */
	public void stop(boolean shutdown) {

		//two cases: stop-on-shutdown and stop-on-request, some listeners will be selective about this,
		
		//shutdown is triggered by probe app, which is notified among other apps
		//if other app have been already notified, the container may already be part-active.
		//apps still to notify will listen only on stop, hence won't react to this but will go down when their turn arrives.
		
		if (context == null)
			return;

		log.info("stopping container management");

		try {
			
			context.lifecycle().tryMoveTo(shutdown?down:stopped);
		
			stopHandlers();
			
			//no further reactions
			log.info("stopping container events");
			context.events().stop();
			
		} 
		catch (RuntimeException e) {

			log.warn("cannot stop container management (see cause)", e);
		}

	}
	
	//helpers
	
	private void startHandlers(List<ContainerHandler> handlers) {

		try {

			pipeline = new ContainerPipeline(handlers);
			
			pipeline.forward(new ContainerLifecycleEvent.Start(context));

		} catch (RuntimeException e) {
			
			context.lifecycle().tryMoveTo(failed);
			throw e;
		}
	}
	
	
	private void stopHandlers() {

		if (pipeline == null)
			return;

		// copy pipeline, flip it, and
		ContainerPipeline returnPipeline = pipeline.reverse();

		// start lifetime pipeline in inverse order with stop event
		returnPipeline.forward(new ContainerLifecycleEvent.Stop(context));

	}
	
	
	

}
