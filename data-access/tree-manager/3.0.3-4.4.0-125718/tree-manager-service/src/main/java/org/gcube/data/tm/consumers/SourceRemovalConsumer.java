/**
 * 
 */
package org.gcube.data.tm.consumers;

import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationEvent;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.tm.context.TBinderContext;
import org.gcube.data.tm.state.TBinderResource;

/**
 * A {@link BaseNotificationConsumer} of events that relate to removal of collections in a given scope.
 *  
 * @author Fabio Simeoni
 *
 */

public class SourceRemovalConsumer extends BaseNotificationConsumer{

	private GCUBELog logger = new GCUBELog(SourceRemovalConsumer.class);
	private GCUBEScope scope;
	
	/**
	 * Creates an instance for a given scope.
	 * @param scope the scope.
	 */
	public SourceRemovalConsumer(GCUBEScope scope) {this.scope=scope;}
	
	/**{@inheritDoc}*/
	public void onNotificationReceived(NotificationEvent event) {
	
		try{
			
			String id= event.getPayload().getMessage()[0].getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
			logger.info("processing profile removal notification for collection "+id+". Cancelling all managers in "+scope);
			TBinderResource factory = TBinderContext.getContext().binder();
			factory.deleteAccessors(id, scope);
			
		}
		catch (Throwable e) {
			logger.error("could not process collection removal event "+event,e);
		}

	}
}
