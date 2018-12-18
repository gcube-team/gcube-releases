package org.gcube.portlets.user.reportgenerator.client.dialog;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.user.reportgenerator.client.uibinder.ShowSaving;

public class WaitingOperationDialog extends GCubeDialog {

	public WaitingOperationDialog() {
		super(true, false);
		setText("Please wait ...");
		setWidth("200px");
		setWidget(new ShowSaving());
	}
}
