/**
 * 
 */
package org.gcube.portlets.user.gisviewerapp.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 21, 2014
 * 
 */
public class BaloonPanel extends PopupPanel{

	private boolean shouldHide = false;
	private Command command;

	/**
	 * 
	 * @param baloonText
	 * @param shouldHide
	 * @param cmd on Click
	 */
	public BaloonPanel(String baloonText, boolean shouldHide, Command cmd) {
		super(shouldHide);
		this.shouldHide = shouldHide;
		this.command = cmd;
		setAutoHideEnabled(shouldHide);
		setStyleName("baloonPanel");
		// some sample widget will be content of the balloon
		HTML text = new HTML(baloonText);
		
		text.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				command.execute();
			}
		});
		
		setWidget(text);
	}

	@Override
	public void show() {
		super.show();
	}
}