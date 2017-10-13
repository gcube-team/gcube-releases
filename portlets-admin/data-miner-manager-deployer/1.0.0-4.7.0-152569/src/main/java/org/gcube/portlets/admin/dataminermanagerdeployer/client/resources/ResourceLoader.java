package org.gcube.portlets.admin.dataminermanagerdeployer.client.resources;


import javax.inject.Inject;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
public class ResourceLoader {
    @Inject
    ResourceLoader(AppResources appResources) {
        appResources.normalize().ensureInjected();
        appResources.style().ensureInjected();
        appResources.pageTable().ensureInjected();
        
    }
}
