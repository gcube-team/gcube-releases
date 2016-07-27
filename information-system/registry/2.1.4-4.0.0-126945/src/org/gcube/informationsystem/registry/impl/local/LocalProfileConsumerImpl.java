package org.gcube.informationsystem.registry.impl.local;

import java.io.StringWriter;

import org.gcube.common.core.informationsystem.publisher.ISLocalPublisher.LocalProfileConsumer;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext;
import org.gcube.informationsystem.registry.impl.porttypes.ResourceRegistration;
import org.gcube.informationsystem.registry.stubs.resourceregistration.CreateMessage;
import org.gcube.informationsystem.registry.stubs.resourceregistration.RemoveMessage;
import org.gcube.informationsystem.registry.stubs.resourceregistration.UpdateMessage;

public class LocalProfileConsumerImpl extends LocalProfileConsumer {
	
	private final GCUBELog logger = new GCUBELog(LocalProfileConsumerImpl.class);
	/* (non-Javadoc)
     * @see org.gcube.common.core.informationsystem.publisher.ISLocalPublisher.LocalProfileConsumer#onProfileRegistered(org.gcube.common.core.resources.GCUBEResource)
     */
    @Override
    protected void onProfileRegistered(final GCUBEResource resource, final GCUBEScope scope) {
    	LocalProfileConsumerImpl.this.logger.debug("onProfileRegistered event received in scope " + scope );
        new Thread() {
			@Override
			public void run() {
				 ServiceContext.getContext().waitUntilReady();
		         ResourceRegistration factory= new ResourceRegistration();
		            try {
		            	ServiceContext.getContext().setScope(scope);
		            	CreateMessage crm = new CreateMessage();
		                StringWriter writer = new StringWriter();
		                resource.store(writer);
		                crm.setProfile(writer.toString());
		                crm.setType(resource.getType());
		                //crm.setScopes(new String[] {scope.toString()});
		                LocalProfileConsumerImpl.this.logger.debug("Creating resource ");
		                factory.create(crm);				               
		            } catch (Exception e) {
		            	LocalProfileConsumerImpl.this.logger.error("cannot handle the create resource event"+e);
		            }
			}
        	
        }.start();
       
               
    }

    /* (non-Javadoc)
     * @see org.gcube.common.core.informationsystem.publisher.ISLocalPublisher.LocalProfileConsumer#onProfileRemoved(java.lang.String, java.lang.String)
     */
    @Override
    protected void onProfileRemoved(final String resourceID, final String type, final GCUBEScope scope) {
    	logger.debug("onProfileRemoved event received" );
    	new Thread() {
			@Override
			public void run() {
				ServiceContext.getContext().waitUntilReady();
	        	ServiceContext.getContext().setScope(scope);
	        	ResourceRegistration factory= new ResourceRegistration();
	        	RemoveMessage rrm= new RemoveMessage();
	        	rrm.setType(type);
	        	rrm.setUniqueID(resourceID);			   
	        	try {
					factory.remove(rrm);
				} catch (Exception e) {
					logger.error("cannot handle the remove resource event"+e);
					
				}
			}
    	}.start();
    		                               
    }

    /* (non-Javadoc)
     * @see org.gcube.common.core.informationsystem.publisher.ISLocalPublisher.LocalProfileConsumer#onProfileUpdated(org.gcube.common.core.resources.GCUBEResource)
     */
    @Override
    protected void onProfileUpdated(final GCUBEResource resource, final GCUBEScope scope) {
        logger.debug("onProfileUpdated event received" );
        new Thread() {
			@Override
			public void run() {
				ServiceContext.getContext().waitUntilReady();
	            ServiceContext.getContext().setScope(scope);
	            ResourceRegistration factory= new ResourceRegistration();
	            try {
	            	UpdateMessage urm= new UpdateMessage();
	            	StringWriter writer = new StringWriter();
	                resource.store(writer);
	                urm.setXmlProfile(writer.toString());
	                urm.setType(resource.getType());
	                urm.setUniqueID(resource.getID());
	               factory.update(urm);
	            } catch (Exception e) {
	            	logger.error("cannot handle the update resource event"+e);
	            }
			}
        }.start();
    }
    
 

}
