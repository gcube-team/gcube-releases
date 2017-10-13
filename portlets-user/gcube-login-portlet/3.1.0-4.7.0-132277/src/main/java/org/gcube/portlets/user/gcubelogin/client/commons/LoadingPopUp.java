package org.gcube.portlets.user.gcubelogin.client.commons;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;

public class LoadingPopUp extends DialogBox {
	
	private static LoadingPopUp singleton = null;
	private boolean hidden = true;
	private String loading_image = "";
	
	public static LoadingPopUp get()
	{
		return singleton;
	}
	
	public LoadingPopUp(boolean autoHide, boolean modal, String loading_image) {
		super(autoHide, modal);
		this.loading_image = loading_image;
		HTML msg = new HTML(setToDisplay(), true);
		DockPanel dock = new DockPanel();
		dock.setSpacing(0);
		dock.add(msg, DockPanel.NORTH);
		dock.setPixelSize(msg.getOffsetWidth(), msg.getOffsetHeight());
		setWidget(dock);
		if (singleton == null) singleton = this;
	}
	
	protected String setToDisplay() {
		return 
		"<center><table border='0'>"+
		"<tr>"+
		"<td>"+
		"<img src='" + this.loading_image + "'>"+
		"</td></tr>"+
		"</table></center>" ;
	}
	public void hide() {
		super.hide();
		this.hidden = true;
	}
	public void show() {
		super.show();
		this.hidden = false;
	}
	public boolean isHidden() {
		return this.hidden;
	}
}

