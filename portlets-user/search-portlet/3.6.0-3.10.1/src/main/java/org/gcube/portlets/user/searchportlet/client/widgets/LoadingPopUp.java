package org.gcube.portlets.user.searchportlet.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * This class extends the dialog box of GWT and shows the loading image.
 * This pop-up is displayed when the search button are clicked
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class LoadingPopUp extends DialogBox implements ClickHandler {
	public LoadingPopUp(boolean autoHide) {
	
		super(autoHide);
		Image loadingIcon = new Image(GWT.getModuleBaseURL() + "../images/loading.gif");
		
		DockPanel dock = new DockPanel();
		dock.setSpacing(0);
		dock.add(loadingIcon, DockPanel.NORTH);
		dock.setPixelSize(loadingIcon.getOffsetWidth(), loadingIcon.getOffsetHeight());
		setWidget(dock);
	}

	public void onClick(ClickEvent event) {
		hide();
	}
}
