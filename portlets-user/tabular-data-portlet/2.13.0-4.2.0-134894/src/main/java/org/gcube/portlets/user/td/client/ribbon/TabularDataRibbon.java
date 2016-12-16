/**
 * 
 */
package org.gcube.portlets.user.td.client.ribbon;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;


/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TabularDataRibbon {

	private static final String RIBBON_HEIGHT = "100px";
	
	private TabPanel ribbon;
	
	    
	private FileToolBar fileToolBar;
	private CurationToolBar curationToolBar;
	private ModifyToolBar modifyToolBar;
	private RuleToolBar ruleToolBar;
	private TemplateToolBar templateToolBar;
	private AnalyseToolBar analyseToolBar;

	public TabularDataRibbon(EventBus eventBus) {
		try {
			TabularDataRibbonMessages msgs = GWT.create(TabularDataRibbonMessages.class);
			
			ribbon = new TabPanel();
			ribbon.setId("Ribbon");
			ribbon.setHeight(RIBBON_HEIGHT);
			
			VerticalLayoutData vldata=new VerticalLayoutData(1, -1);
			
			fileToolBar = new FileToolBar(eventBus);
			VerticalLayoutContainer con = new VerticalLayoutContainer();
			con.add(fileToolBar.getToolBar(), vldata);
			ribbon.add(con, msgs.home());
			
			curationToolBar = new CurationToolBar(eventBus);
			con = new VerticalLayoutContainer();
			con.add(curationToolBar.getToolBar(), vldata);
			ribbon.add(con, msgs.curation());

			modifyToolBar = new ModifyToolBar(eventBus);
			con = new VerticalLayoutContainer();
			con.add(modifyToolBar.getToolBar(), vldata);
			ribbon.add(con, msgs.modify());
			
			
			ruleToolBar = new RuleToolBar(eventBus);
			con = new VerticalLayoutContainer();
			con.add(ruleToolBar.getToolBar(), vldata);
			ribbon.add(con, msgs.rule());
			
		
			templateToolBar = new TemplateToolBar(eventBus);
			con = new VerticalLayoutContainer();
			con.add(templateToolBar.getToolBar(), vldata);
			ribbon.add(con, msgs.template());
			
			
			analyseToolBar = new AnalyseToolBar(eventBus);
			con = new VerticalLayoutContainer();
			con.add(analyseToolBar.getToolBar(), vldata);
			ribbon.add(con, msgs.analyse());
			
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
