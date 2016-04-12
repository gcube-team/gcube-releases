package org.gcube.portlets.user.gcubelogin.client.wizard;

import org.gcube.portlets.user.gcubelogin.client.commons.LoadingPopUp;
import org.gcube.portlets.user.gcubelogin.client.stubs.NewLoginServiceAsync;
import org.gcube.portlets.user.gcubelogin.client.wizard.errors.WizardAccountCreationError;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WizardAdminAccount extends Composite {

	/** 
	 * RFC 2822 compliant 
	 * http://www.regular-expressions.info/email.html 
	 */ 
	private final static String EMAIL_VALIDATION_REGEX = 
			"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"; 

	private static WizardAdminAccountUiBinder uiBinder = GWT
			.create(WizardAdminAccountUiBinder.class);

	interface WizardAdminAccountUiBinder extends
	UiBinder<Widget, WizardAdminAccount> {
	}

	private VerticalPanel mainPanel = new VerticalPanel();
	private NewLoginServiceAsync newLoginSvc = null;
	@UiField TextBox email; 
	@UiField TextBox firstName; 
	@UiField TextBox lastName; 
	@UiField Label errorReport; 
	@UiField PasswordTextBox password1; 
	@UiField PasswordTextBox password2; 
	@UiField Button createButton; 


	public WizardAdminAccount(NewLoginServiceAsync newLoginSvc, VerticalPanel mainPanel) {
		this.newLoginSvc = newLoginSvc;
		this.mainPanel = mainPanel;
		initWidget(uiBinder.createAndBindUi(this));
	}

	
	
	@UiHandler("createButton")
	void onCreateClick(ClickEvent e) {
		errorReport.setVisible(false);
		if (checkFields()) {
			errorReport.addStyleName("ok");
			setUserMessage("Looks like everything was filled in correctly, creating account...");
			showLoading();
			newLoginSvc.createAdministratorAccount(email.getText(), password1.getText(), firstName.getText(), lastName.getText(), new AsyncCallback<Boolean>() {
				
				public void onSuccess(Boolean result) {
					hideLoading();	
					mainPanel.clear();
					if (result)
						mainPanel.add(new WizardResultOK());
					else
						mainPanel.add(new WizardAccountCreationError());
				}
				
				public void onFailure(Throwable caught) {
					hideLoading();	
					mainPanel.clear();
					mainPanel.add(new WizardAccountCreationError());					
				}
			});
		}		
	}
	private boolean checkFields() {
		if (! validateEmail()) {
			setUserMessage("Please, enter a valid email address");
			email.setFocus(true);
			return false;
		}
		if (isTestAccountEmail()) {
			setUserMessage("Please, enter an email address different from the test account one");
			email.setFocus(true);
			return false;
		}
		if (! checkPassword()) {
			return false;
		}
		firstName.setText(firstName.getText().replaceAll(" ", ""));
		if (firstName.getText().isEmpty()) {
			setUserMessage("First name is a mandatory field");
			firstName.setFocus(true);
			return false;
		}
		lastName.setText(lastName.getText().replaceAll(" ", ""));
		if (lastName.getText().isEmpty()) {
			setUserMessage("Last name is a mandatory field");
			lastName.setFocus(true);
			return false;
		}
		return true;
	}
	/**
	 * 
	 * @return true if email is valid against RFC 2822
	 */
	private boolean validateEmail() { 
		String emailaddress = email.getText(); 
		return (emailaddress.matches(EMAIL_VALIDATION_REGEX));
	} 
	/**
	 * 
	 * @return trie if email is valid against RFC 2822
	 */
	private boolean isTestAccountEmail() { 
		String emailaddress = email.getText(); 
		return (emailaddress.compareTo("test@liferay.com") == 0 || emailaddress.compareTo("test@i-maine.eu") == 0);
	} 
	
	/**
	 * 
	 * @return trie if email is valid against RFC 2822
	 */
	private boolean checkPassword() { 
		String pw1 = password1.getText(); 
		String pw2 = password2.getText(); 
		
		if (pw1.isEmpty()) {
			setUserMessage("Password is empty");
			password1.setFocus(true);
			return false;
		}
		if (pw2.isEmpty()) {
			setUserMessage("Please repeat password");
			password2.setFocus(true);
			return false;
		}
		if (!(pw1.compareTo(pw2) == 0)) {
			setUserMessage("Passwords don't match");
			password1.setFocus(true);
			return false;
		}
		if (pw2.length() < 8) {
			setUserMessage("Passwords must be at least 8 chars");
			password1.setFocus(true);
			return false;
		}
		return true;
	} 
	
	private void setUserMessage(String message) {
		errorReport.setVisible(true);
		errorReport.setText(message);
	}
	static void showLoading() {
		LoadingPopUp dlg = LoadingPopUp.get();
		dlg.show();
	}
	static void hideLoading() {
		LoadingPopUp dlg = LoadingPopUp.get();
		dlg.hide();		
	}
}
