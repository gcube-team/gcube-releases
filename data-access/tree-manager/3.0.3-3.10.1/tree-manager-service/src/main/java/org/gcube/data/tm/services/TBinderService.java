package org.gcube.data.tm.services;

import static org.gcube.common.core.faults.FaultUtils.*;

import java.util.List;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.faults.GCUBERetryEquivalentFault;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler.Mode;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.tm.Constants;
import org.gcube.data.tm.activationrecord.ActivationRecord;
import org.gcube.data.tm.activationrecord.ActivationRecordBody;
import org.gcube.data.tm.context.ServiceContext;
import org.gcube.data.tm.context.TBinderContext;
import org.gcube.data.tm.publishers.ResilientScheduler;
import org.gcube.data.tm.state.TBinderResource;
import org.gcube.data.tm.stubs.BindParameters;
import org.gcube.data.tm.stubs.InvalidRequestFault;
import org.gcube.data.tm.stubs.SourceBinding;
import org.gcube.data.tm.stubs.SourceBindings;
import org.gcube.data.tm.utils.BindParametersWrapper;
import org.gcube.data.tmf.api.exceptions.InvalidRequestException;

/**
 * The implementation of the T-Binder service.
 * 
 * @author Fabio Simeoni
 *
 */
public class TBinderService {

	private static GCUBELog logger = new GCUBELog(TBinderService.class); 
	
	/**
	 * Returns {@link SourceBindings} from client parameters.
	 * @param parameters the parameters
	 * @return the bindings
	 * @throws InvalidRequestfault if the parameters are malformed
	 * @throws GCUBEFault if the request fails for any other error
	 */
	//public,axis-specific version
	public SourceBindings bind(BindParameters parameters) throws InvalidRequestFault, GCUBEFault {
		return bind(new BindParametersWrapper(parameters));
	}
	
	/**
	 * Returns {@link SourceBindings} from client parameters.
	 * @param parameters the parameters
	 * @return the bindings
	 * @throws InvalidRequestFault if the parameters are malformed
	 * @throws GCUBEFault if the request fails for any other error
	 */
	//internal,future-proof version
	SourceBindings bind(final BindParametersWrapper parameters) throws InvalidRequestFault, GCUBEFault {
		
		try {
		
			if (parameters==null)
				throw new InvalidRequestException("request carries null parameters");
				
			String pluginName = parameters.getPlugin();
			
			TBinderResource binder = TBinderContext.getContext().binder();
			
			if (parameters.isBroadcast() && binder.getPlugin(pluginName).isAnchored())
				throw new InvalidRequestException("cannot broadcast: plugin is anchored, its state cannot be replicated");
		
			//dispatch to factory
			List<SourceBinding> bindings = binder.bind(pluginName, parameters.getPayload());		
			
			//broadcast AR asynchronously and when readers/writers are staged (if there are actually any!)
			if (bindings.size()>0 && parameters.isBroadcast())
				buildAndPublishActivationRecord(binder, parameters);

			return new SourceBindings(bindings.toArray(new SourceBinding[0]));
		}
		catch(InvalidRequestException e) {
			throw newFault(new InvalidRequestFault(),e);
		}
		catch (Exception e) {
			throw newFault(new GCUBERetryEquivalentFault(),e);
	    }
			
	}	
	
	//helper
	private void buildAndPublishActivationRecord(TBinderResource binder,BindParametersWrapper parameters) {
	
		try {
			
			//build AR
			String id = ServiceContext.getContext().getInstance().getID();
			ActivationRecordBody body = new ActivationRecordBody(id,parameters);
			String description = "An activation of the T-Binder Service";
			ActivationRecord record = ActivationRecord.newInstance(description,body);
			
			ResilientScheduler scheduler = new ResilientScheduler(1,Mode.LAZY);
			scheduler.setAttempts(Constants.MAX_ACTIVATIONRECORD_PUBLICATION_ATTEMPTS);
			scheduler.setDelay(10L);
			scheduler.setScopeManager(ServiceContext.getContext());
			scheduler.setSecurityManager(ServiceContext.getContext());
			record.publish(scheduler);
			
			//remember AR
			binder.addActivation(record);
		} 
		catch(Throwable t) {
			logger.error("could not publish activation record "+parameters,t);
		}
	}
	
}
