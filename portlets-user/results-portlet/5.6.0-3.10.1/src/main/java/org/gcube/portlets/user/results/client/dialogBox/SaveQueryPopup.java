package org.gcube.portlets.user.results.client.dialogBox;

import org.gcube.portlets.user.results.client.constants.CommonConstants;
import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.user.results.client.util.QuerySearchType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SaveQueryPopup extends DialogBox {
	
		private TextArea templNameTextBox = new TextArea();
		private Button saveButton = new Button("Save");
		

		public SaveQueryPopup(final Controller control, boolean autoHide, String suggestedName, final String descrption, final QuerySearchType qType) {
			
			super(autoHide);
		
			// Create a panel to hold all of the form widgets.
			VerticalPanel panel = new VerticalPanel();
			Label theLabel = null;
			
			this.setText("Save Query");
			theLabel = new Label("provide a query name");
			
			panel.add(theLabel);
			panel.setSpacing(4);
			
			templNameTextBox.setSize("280", "60");
			templNameTextBox.setText(suggestedName);

			panel.add(templNameTextBox);

			HorizontalPanel buttonsPanel = new HorizontalPanel();
			HorizontalPanel buttonsContainer = new HorizontalPanel();
			buttonsPanel.setWidth("100%");
			buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			buttonsContainer.setSpacing(8);
			buttonsPanel.add(buttonsContainer);
			buttonsContainer.add(new Button("Cancel", new ClickHandler() {
				public void onClick(ClickEvent arg0) {
					hide();
				}}));

			buttonsContainer.add(saveButton);
			panel.add(buttonsPanel);
			panel.setPixelSize(220, 120);
			setWidget(panel);
			
			saveButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent arg0) {
					//checking user input
					String inputUser = templNameTextBox.getText();
					if (inputUser.compareTo(templNameTextBox.getText().replaceAll(CommonConstants.ACCEPTED_CHARS_REG_EX, "")) != 0) {
						Window.alert("Template name contains illegal characters detected, System will remove them");
						templNameTextBox.setText(templNameTextBox.getText().replaceAll(CommonConstants.ACCEPTED_CHARS_REG_EX, ""));
					}
					else if (inputUser.compareTo("") == 0) {
						Window.alert("Template Name cannot be empty");
					}
					else {
						control.addQueryToBasket(templNameTextBox.getText(), descrption, qType);
						hide();
					}
					
				}
			});
		}
//		/*
//		 * selectAll method works only when the widget is attacched to the DOM,
//		 * indeed I need to use this timer 
//		 */ 
//		Timer t = new Timer() {
//					@Override
//			public void run() {
//				templNameTextBox.selectAll();
//			}
//		};
//		
//		public void setFocus() {
//			templNameTextBox.setFocus(true);
//			t.schedule(300);
//		}
	} //end  class

