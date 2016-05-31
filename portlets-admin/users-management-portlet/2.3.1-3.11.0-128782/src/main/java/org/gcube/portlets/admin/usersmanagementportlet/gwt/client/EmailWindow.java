package org.gcube.portlets.admin.usersmanagementportlet.gwt.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.HtmlEditor;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.AnchorLayoutData;

public class EmailWindow extends Window {
	
	private FormPanel formPanel = new FormPanel(Position.LEFT);
	private TextField mailSubject = new TextField("Subject", "msubject");
	private TextField mailTo = new TextField("Recipients", "mto");
	private HtmlEditor mailBody = new HtmlEditor("Message", "mbody");
	
	public EmailWindow(final ArrayList<String> emails) {
		
		this.setSize(805, 430);
		
		formPanel.setPaddings(10);
		formPanel.setSize(800, 400);
		formPanel.setTitle("Email Notification");
		
		mailTo.setGrow(true);
		mailTo.setDisabled(true);
		String mailToValue = "";
		for (String e: emails)
			mailToValue = mailToValue + e + "; ";
		mailToValue = mailToValue.substring(0, mailToValue.length()-2);
		mailTo.setValue(mailToValue);
		
		mailSubject.setGrow(true);
		mailSubject.setGrowMax(650);
		mailSubject.setGrowMin(250);
		formPanel.add(mailTo);
		formPanel.add(mailSubject);
		mailBody.setPixelSize(550, 200);
		mailBody.setValue("Email's message body");
		mailBody.focus(false, 1000);
		formPanel.add(mailBody, new AnchorLayoutData("98%"));
		
		final Button clearBtn= new Button("Clear", new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				mailSubject.setValue("");
				mailBody.setValue("");
			}
		});

		final Button sendBtn= new Button("Send", new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				MessageBox.confirm("Confirm Changes", "Are you sure you want to send this email?", new MessageBox.ConfirmCallback() {  
					public void execute(String btnID) {
						if(btnID.equalsIgnoreCase("yes")) {

							AsyncCallback<Void> authorizeUsersCallback = new AsyncCallback<Void>() {

								public void onFailure(Throwable caught) {
									MessageBox.alert("Failed to send the email. Please try again");
									

								}

								public void onSuccess(Void result) {
									MessageBox.alert("The email has been sent to all registered users");

								}

							};UsersManagement.userService.sendEmail(emails, mailSubject.getText(), mailBody.getRawValue(), authorizeUsersCallback);
						}
						EmailWindow.this.hide();
						EmailWindow.this.close();
						
					}});
			}
		});
		
		formPanel.addButton(sendBtn); 
		formPanel.addButton(clearBtn);
		
		this.add(formPanel);
		this.show();
		this.center();

	}
	

	

}
