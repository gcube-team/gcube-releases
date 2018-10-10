package org.gcube.portlets.user.gcubewidgets.client.popup;

import com.google.gwt.user.client.ui.DialogBox;

/**
 * A form of popup that has a caption area at the top and can be dragged by the
 * user. Unlike a PopupPanel, calls to {@link #setWidth(String)} and
 * {@link #setHeight(String)} will set the width and height of the dialog box
 * itself, even if a widget has not been added as yet.
 * 
 * <code> GCubeDialog </code> is the Dialog Box for gCube Portlet, so far it simply extends the GWT one by assigning it a default Style
 * 
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version December 2009 (0.1a) 
 */
public class GCubeDialog extends DialogBox {

	public GCubeDialog() {
		super();
		setStyleName("gcube_DialogBox");
	}

	public GCubeDialog(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		setStyleName("gcube_DialogBox");
	}

	public GCubeDialog(boolean autoHide) {
		super(autoHide);
		setStyleName("gcube_DialogBox");
	}
	
}
