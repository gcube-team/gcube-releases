package org.gcube.data.transfer.service;

import java.util.Set;

import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.ResourceModel;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

@Provider
public class ProviderLoggingListener implements ApplicationEventListener {

    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
            case INITIALIZATION_FINISHED: {
            	System.out.println("*************** LISTING PROVIDERS ******************");
            	
                for(Class c: event.getProviders()){
                	System.out.println(c.getCanonicalName()+" \t "+ c);
                }
                
                System.out.println("*************** RESOURCE CONFIG ******************");
                System.out.println(event.getResourceConfig());
                System.out.println("*************** RESOURCE MODEL ******************");
                System.out.println(event.getResourceModel());
                break;
            }
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null;
    }
}
