package org.gcube.portlets.user.messages.client.view.message;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;


/**
 * The Class GxtMessagesPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 6, 2015
 */
public class GxtMessagesPanel extends ContentPanel{

	private GxtToolBarMessage toolBarMessage;
	
	/**
	 * Instantiates a new gxt messages panel.
	 *
	 * @param messagesPanelContainer the messages panel container
	 * @param toolBarMessage the tool bar message
	 */
	public GxtMessagesPanel(GxtGridMessagesFilterPanel messagesPanelContainer, GxtToolBarMessage toolBarMessage) {
		this.setLayout(new FitLayout());
		setBorders(false);
		setBodyBorder(false);
		setHeaderVisible(false);
		this.toolBarMessage = toolBarMessage;
		setTopComponent(this.toolBarMessage.getToolBar());
		add(messagesPanelContainer);
	}

}
