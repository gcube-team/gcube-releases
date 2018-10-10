package org.gcube.portlets.user.accountingdashboard.client.gin;


import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.RootPresenter;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
public class CustomRootPresenter extends RootPresenter {
	private static Logger logger = java.util.logging.Logger.getLogger("");
	private static final String SM_DIV = "contentDiv";
	
    public static final class CustomRootView extends RootView {
        @Override
        public void setInSlot(Object slot, IsWidget widget) {
            
            RootPanel root = RootPanel.get(SM_DIV);
			
			if (root != null) {
				logger.log(Level.FINE,"Add Panel in Div " + SM_DIV);
				root.add(widget);
			} else {
				logger.log(Level.FINE,"Add Panel in Root");
				RootPanel.get().add(widget);
			}
            
        }
    }
    
    

    @Inject
    CustomRootPresenter(EventBus eventBus, CustomRootView myRootView) {
        super( eventBus, myRootView );
    }
}