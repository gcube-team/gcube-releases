package org.gcube.portlets.admin.dataminermanagerdeployer.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
public interface AppResources extends ClientBundle {
    interface Normalize extends CssResource {
    }

    interface Style extends CssResource {
    	String sideNavPBg();
    }
    
    interface PageTable extends CssResource {
    	
    }
    
   
    
    @Source("css/normalize.gss") 
    Normalize normalize();

    @Source("css/style.gss")
    Style style();
    
    @Source("css/pageTable.gss")
    PageTable pageTable();
        
}
