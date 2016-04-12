package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InformationWidget extends DialogBox implements ClickHandler{

	private VerticalPanel dialogBoxContents;
	private SimplePanel holder;
	private Button closeBtn = new Button("OK");
	private HTML message;


	public InformationWidget(String caption, String msg) {

		this.addStyleName("mydialogBox");
		dialogBoxContents = new VerticalPanel();
		message = new HTML(msg, true);
		message.addStyleName("dialogBox-message");
		this.setText(caption);

		holder = new SimplePanel();
		holder.add(closeBtn);
		holder.setStyleName("dialogBox-footer");
		dialogBoxContents.setSpacing(6);
		dialogBoxContents.add(message);
		dialogBoxContents.add(holder);
		this.clear();
		this.setWidget(dialogBoxContents);

		closeBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				InformationWidget.this.hide();
			}
		});

	}


	public void onClick(ClickEvent event) {
		this.hide();

	}

}
