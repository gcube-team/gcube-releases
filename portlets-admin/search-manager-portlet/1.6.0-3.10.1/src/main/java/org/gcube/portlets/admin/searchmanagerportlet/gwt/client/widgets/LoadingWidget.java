package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * This class extends the dialog box of GWT and shows the loading image.
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class LoadingWidget extends DialogBox implements ClickHandler {
	
	public  LoadingWidget(boolean autoHide) {
		super(autoHide);
		Image loadingIcon = new Image(GWT.getModuleBaseURL() + "../images/loading.gif");
		HTML loadingMsg = new HTML("<div style=\"vertical-align: middle; display: inline\">Loading, please wait...</div>");
		DockPanel dock = new DockPanel();
		dock.add(loadingMsg, DockPanel.WEST);
		dock.add(loadingIcon, DockPanel.EAST);
		//dock.setPixelSize(loadingIcon.getOffsetWidth(), loadingIcon.getOffsetHeight());
		setWidget(dock);
	}

	public void onClick(ClickEvent event) {
		hide();
	}
}
