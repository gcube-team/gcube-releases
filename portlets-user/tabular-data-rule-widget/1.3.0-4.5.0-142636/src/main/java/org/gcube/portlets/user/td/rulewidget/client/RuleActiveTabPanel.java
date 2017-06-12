package org.gcube.portlets.user.td.rulewidget.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class RuleActiveTabPanel extends TabPanel {
	private EventBus eventBus;
	private RuleActiveOnColumnPanel ruleActiveOnColumnPanel;
	private TRId trId;
	private RuleActiveOnTablePanel ruleActiveOnTablePanel;
	private RuleActiveMessages msgs;

	public RuleActiveTabPanel(TRId trId, EventBus eventBus) {
		super();
		Log.debug("Create RuleActiveTabPanel");
		initMessages();
		this.trId=trId;
		this.eventBus = eventBus;
		setBodyBorder(false);
		setBorders(false);
		setAnimScroll(true);
		setTabScroll(true);
		setCloseContextMenu(false);
		addTabs();
		startTabs();
	}

	protected void initMessages(){
		msgs = GWT.create(RuleActiveMessages.class);
	}
	
	public void startTabs() {
		Log.debug("Start RuleActiveTabPanel Tabs");		
		setActiveWidget(getWidget(0));

	}
	
	public void addTabs(){
		TabItemConfig ruleOnColumnItemConf = new TabItemConfig(
				msgs.ruleOnColumnItemHead(), false);

		ruleActiveOnColumnPanel = new RuleActiveOnColumnPanel(trId,eventBus);
		add(ruleActiveOnColumnPanel, ruleOnColumnItemConf);
		
		TabItemConfig ruleOnTableItemConf = new TabItemConfig(
				msgs.ruleOnTableItemHead(), false);

		ruleActiveOnTablePanel = new RuleActiveOnTablePanel(trId,eventBus);
		add(ruleActiveOnTablePanel, ruleOnTableItemConf);

	}
	
}
