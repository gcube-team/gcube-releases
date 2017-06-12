package org.gcube.portlets.user.newsfeed.client.ui;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SharePostDialog extends Composite {

	private static SharePostDialogUiBinder uiBinder = GWT
			.create(SharePostDialogUiBinder.class);

	interface SharePostDialogUiBinder extends UiBinder<Widget, SharePostDialog> {
	}

	public SharePostDialog(TweetTemplate toShare) {
		initWidget(uiBinder.createAndBindUi(this));
		
		input.addItem("devVRE", "devVRE");
		input.addItem("devVRE2", "devVRE");
		input.addItem("devVRE3", "devVRE");
		
	
	}

	@UiField Button sharePostButton;	
	@UiField Button cancel;
	@UiField Modal modalWindow;
	@UiField ListBox input;
	
	@UiHandler("sharePostButton")
	void onClick(ClickEvent e) {
		Window.alert("Hello!");
	}
	
	@UiHandler("cancel")
	void onCancelClick(ClickEvent e) {
		modalWindow.hide();
	}
	
	public void openModal() {
		GWT.log("OpenModal");
		modalWindow.show();
	}

}
