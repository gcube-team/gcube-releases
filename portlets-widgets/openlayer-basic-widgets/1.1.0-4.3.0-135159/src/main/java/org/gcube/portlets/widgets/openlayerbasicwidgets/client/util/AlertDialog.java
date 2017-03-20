package org.gcube.portlets.widgets.openlayerbasicwidgets.client.util;

import com.google.gwt.core.client.Callback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AlertDialog extends DialogBox implements ClickHandler {
	private Callback<Void, Void> callback;
	protected HTML msg;
	protected double msgMinWidth=200;
	protected double msgMinHeight=30;
	
	
	public AlertDialog(String title, String text, int zIndex) {
		init(title, text, zIndex);
	}
	
	public AlertDialog(String title, String text, int zIndex, Callback<Void, Void> callback) {
		init(title, text, zIndex);
	}
	
	private void init(String title, String text, int zIndex) {
		setText(title);
		setModal(true);
		setGlassEnabled(true);
		
		Button closeButton = new Button("Close", this);
		msg = new HTML(text, true);
		msg.getElement().getStyle().setProperty("minWidth", msgMinWidth, Unit.PX);
		msg.getElement().getStyle().setProperty("minHeight",msgMinHeight, Unit.PX);
		
		DockPanel dock = new DockPanel();
		dock.setSpacing(4);
		
		
		dock.add(closeButton, DockPanel.SOUTH);
		dock.add(msg, DockPanel.CENTER);

		dock.setCellHorizontalAlignment(closeButton, DockPanel.ALIGN_CENTER);
		dock.setWidth("100%");
		setWidget(dock);
		
		if(zIndex>0){
			getGlassElement().getStyle().setZIndex(zIndex+4);
			getElement().getStyle().setZIndex(zIndex+5);
		}
		center();
	}

	@Override
	public void onClick(ClickEvent event) {
		if(callback!=null){
			callback.onSuccess(null);
		}
		hide();
		

	}
}