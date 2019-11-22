package org.gcube.portlets.admin.authportletmanager.client.pagelayout;

import org.gcube.portlets.admin.authportletmanager.client.widget.WindowBox;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog Box for show an error or warning
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public class DialogError extends WindowBox{

	public DialogError() {
		//super(false, true);	
		super(true, false);
	}

	public void showError(String textError){

		/*
		this.setWidth(ConstantsSharing.WIDTH_DIALOG_LOADER + "px");
		this.setHeight(ConstantsSharing.HEIGHT_DIALOG_LOADER + "px");
		this.setPopupPosition(((Window.getClientWidth() - ConstantsSharing.WIDTH_DIALOG_LOADER) / 2),
				((Window.getClientHeight()-ConstantsSharing.HEIGHT_DIALOG_LOADER)/2) );
		this.setGlassEnabled(true);
		this.setAnimationEnabled(true);
		 */
		this.setStyleName("modal_error");
		this.setWidth("800px");
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);
		this.center();

		Icon icon=new Icon();
		icon.setIcon(IconType.EXCLAMATION_SIGN);
		icon.addStyleName("icon_error");
		Label labelError =new Label();
		labelError.setText("Error");
		labelError.setStyleName("label_error");

		Label labelTextError =new Label();
		labelTextError.setText(textError);
		labelTextError.setStyleName("label_text_error");

		Button buttonErrorExit= new Button();
		buttonErrorExit.setType(ButtonType.DANGER);
		buttonErrorExit.setText("Exit");
		buttonErrorExit.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// handle the click event
				hide();
				clear();
			}
		});
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dialogContents.setWidth("100%");
		dialogContents.addStyleName("content_error");
		dialogContents.add(icon);	
		dialogContents.add(labelTextError);
		dialogContents.add(buttonErrorExit);
		this.add(dialogContents);
		this.show();
	}
}
