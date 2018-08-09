/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.client.ribbon;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;


/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class StatAlgoImporterRibbon {

	private static final String RIBBON_HEIGHT = "70px";
	
	private TabPanel ribbon;
	
	    
	private HomeToolBar homeToolBar;

	public StatAlgoImporterRibbon(EventBus eventBus) {
		try {
			StatAlgoImporterRibbonMessages msgs = GWT.create(StatAlgoImporterRibbonMessages.class);
			
			ribbon = new TabPanel();
			ribbon.setId("Ribbon");
			ribbon.setHeight(RIBBON_HEIGHT);
			
			VerticalLayoutData vldata=new VerticalLayoutData(1, -1);
			
			homeToolBar = new HomeToolBar(eventBus);
			VerticalLayoutContainer con = new VerticalLayoutContainer();
			con.add(homeToolBar.getToolBar(), vldata);
			ribbon.add(con, msgs.home());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Ribbon error:" + e.getLocalizedMessage());
		}

	}

	/**
	 * @return the container
	 */
	public TabPanel getContainer() {
		return ribbon;
	}

}
