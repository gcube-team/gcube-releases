/**
 * 
 */
package org.gcube.data.tm.consumers;

import java.io.StringReader;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationEvent;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.tm.Constants;
import org.gcube.data.tm.activationrecord.ActivationRecord;
import org.gcube.data.tm.activationrecord.ActivationRecordBody;
import org.gcube.data.tm.context.ServiceContext;
import org.gcube.data.tm.context.TBinderContext;
import org.gcube.data.tm.state.TBinderResource;

/**
 * A {@link BaseNotificationConsumer} of events that relate to activation records in a given scope.
 *  
 * @author Fabio Simeoni
 *
 */

public class ActivationRecordConsumer extends BaseNotificationConsumer{

	private GCUBELog logger = new GCUBELog(ActivationRecordConsumer.class);
	private GCUBEScope scope;
	
	/**
	 * Creates an instance for a given scope.
	 * @param scope the scope.
	 */
	public ActivationRecordConsumer(GCUBEScope scope) {this.scope=scope;}
	
	/**{@inheritDoc}*/
	public void onNotificationReceived(NotificationEvent event) {
	
		try{
			String id= event.getPayload().getMessage()[0].getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
	        String profile=event.getPayload().getMessage()[0].getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
	        String operation=event.getPayload().getMessage()[0].getChildNodes().item(2).getChildNodes().item(0).getNodeValue();
	        
	        GCUBEGenericResource resource = null;
			try {	 
				 resource =  GHNContext.getImplementation(GCUBEGenericResource.class);
				 resource.load(new StringReader(profile));
			}
			catch(Exception e) {
				logger.warn("could not parse generic resource "+id);
			}
			
			//TODO factor this out in xpath's subscription
			 if (resource.getSecondaryType().equals(Constants.ACTIVATIONRECORD_TYPE) 
					 && operation.equals("create"))
				 onNewActivationRecord(ActivationRecord.newInstance(resource)); //separate this from other checks.
		}
		catch (Throwable e) {
			logger.error("could not process event "+event,e);
		}

	}
	
	/**
	 * Self-stage with the payload of a new activation record  that was not broadcasted by this
	 * running instance. 
	 * @param record the record.
	 */
	public void onNewActivationRecord(ActivationRecord record) {
	
		try {
			
			TBinderResource binder = TBinderContext.getContext().binder(); 
			
			
			//extracts payload
			ServiceContext context =  ServiceContext.getContext();
			ActivationRecordBody body = record.getBody();
			
			
			//should we process this record?
			if (
				//not produced by this instance
				body.getCreatedBy().equals(context.getInstance().getID()) || 
				//or there is not a plugin that can process it 
				binder.getPlugin(body.getParameters().getPlugin())==null ||
				//or it has been already processed
				binder.knowsActivation(record)
				) { 
				 logger.trace("discarding AR "+record.getResource().getID());
				 return;
			 }
			
			 //set scope and self-stages
			 logger.trace("processing AR \n"+record.getResource().getBody()); 
			 context.setScope(scope);
			 context.useServiceCredentials();
			 binder.bind(body.getParameters().getPlugin(),body.getParameters().getPayload());
			 
			 //if successful
			 binder.addActivation(record);	 
				
		}
		catch(Exception e) {
			logger.warn("could not process AR "+record.getResource().getID(),e);
		}
	}
}
