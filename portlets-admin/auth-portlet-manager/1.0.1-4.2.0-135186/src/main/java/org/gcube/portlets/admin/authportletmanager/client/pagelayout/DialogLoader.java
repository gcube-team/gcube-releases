package org.gcube.portlets.admin.authportletmanager.client.pagelayout;

import org.gcube.portlets.admin.authportletmanager.shared.ConstantsSharing;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ProgressBar;
import com.github.gwtbootstrap.client.ui.base.ProgressBarBase.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * Dialog Box for show a loader status
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public class DialogLoader extends DialogBox{
	private ProgressBar progressLoader =new ProgressBar();


	public DialogLoader() {
		super(false, true);	
	}

	public void startLoader(){

		this.setWidth(ConstantsSharing.WIDTH_DIALOG_LOADER + "px");
		this.setHeight(ConstantsSharing.HEIGHT_DIALOG_LOADER + "px");
		this.setPopupPosition(((Window.getClientWidth() - ConstantsSharing.WIDTH_DIALOG_LOADER) / 2),
				((Window.getClientHeight()-ConstantsSharing.HEIGHT_DIALOG_LOADER)/2) );

		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);
		this.setAnimationEnabled(true);
		this.setStyleName("modal_loader");

		Label labelLoader =new Label();
		labelLoader.setText("Loading...");
		labelLoader.setStyleName("label_loader");


		progressLoader.setActive(true);
		progressLoader.setPercent(100);
		progressLoader.setType(Style.ANIMATED);

		VerticalPanel dialogContents = new VerticalPanel();

		dialogContents.setWidth("100%");
		dialogContents.addStyleName("content_loader");
		dialogContents.add(labelLoader);
		dialogContents.add(progressLoader);	

		this.add(dialogContents);
		this.show();

	}
	public void stopLoader(){
		this.hide();
		this.clear();
	}

}
