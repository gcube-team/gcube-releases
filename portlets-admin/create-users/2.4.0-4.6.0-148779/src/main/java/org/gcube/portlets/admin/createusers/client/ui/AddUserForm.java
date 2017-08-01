package org.gcube.portlets.admin.createusers.client.ui;

import org.gcube.portlets.admin.createusers.client.CreateUsersPanel;
import org.gcube.portlets.admin.createusers.client.HandleUsersServiceAsync;
import org.gcube.portlets.admin.createusers.client.event.AddUserEvent;
import org.gcube.portlets.admin.createusers.shared.VreUserBean;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Form to add new user to the vre.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AddUserForm extends Composite{

	private static AddUserFormUiBinder uiBinder = GWT
			.create(AddUserFormUiBinder.class);

	/**
	 * Path of the image to be shown during loading
	 */
	public static final String imagePath = GWT.getModuleBaseURL() + "../images/loader.gif";

	interface AddUserFormUiBinder extends UiBinder<Widget, AddUserForm> {
	}

	@UiField
	Form form;

	@UiField
	Button submit;

	@UiField
	CheckBox sendMailCheckbox;

	@UiField
	Image performingRequest;

	@UiField
	TextBox emailTextbox;

	@UiField
	TextBox nameTextbox;

	@UiField
	TextBox surnameTextbox;

	@UiField
	TextBox companyTextbox;

	@UiField
	AlertBlock errorBlock;

	@UiField
	AlertBlock successBlock;

	private static final String NAME_SURNAME_PATTERN = "^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$";

	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static final int HIDE_AFTER_MILLISECONDS = 10000;

	private final HandleUsersServiceAsync registrationService;

	private final HandlerManager eventBus;

	private final CreateUsersPanel parent;

	public AddUserForm(HandleUsersServiceAsync userServices, HandlerManager eventBus, CreateUsersPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.registrationService = userServices;
		this.eventBus = eventBus;
		this.parent = parent;
		
		// set loader url
		performingRequest.setUrl(imagePath);
	}


	@Override
	protected void onAttach() {
		super.onAttach();
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand () {
			public void execute () {
				GWT.log("Setting focus");
				emailTextbox.getElement().focus();
			}
		});
	}


	@UiHandler("submit")
	void onClick(ClickEvent e) {

		// input validation
		String actualName = nameTextbox.getText().trim();
		String actualSurname = surnameTextbox.getText().trim();
		String actualEmail = emailTextbox.getText().trim();
		String actualCompany = companyTextbox.getText().trim();

		if(actualEmail.isEmpty() || !actualEmail.matches(EMAIL_PATTERN)){

			showError("Wrong or empty Email field");
			return;

		}

		if(actualName.isEmpty() || !actualName.matches(NAME_SURNAME_PATTERN)){

			showError("Wrong or empty Name field");
			return;

		}

		if(actualSurname.isEmpty() || !actualSurname.matches(NAME_SURNAME_PATTERN)){

			showError("Wrong or empty Surname field");
			return;

		}

		// check if this user is already present among the people added with this portlet
		boolean presentTable = parent.isUserPresent(actualEmail);

		if(presentTable){

			showAlertBlockThenHide(
					errorBlock,
					"Please note that a user with this email already exists",
					HIDE_AFTER_MILLISECONDS
					);

		}else{
			
			// show loading image
			performingRequest.setVisible(true);
			
			// disable add button
			submit.setEnabled(false);

			// remote service invocation
			registrationService.register(
					actualName, 
					actualSurname, 
					actualCompany, 
					actualEmail, 
					sendMailCheckbox.getValue(), 
					//					maleCheckbox.getValue(), 
					true,
					new AsyncCallback<VreUserBean>() {

						@Override
						public void onFailure(Throwable caught) {

							GWT.log("There was an error while adding the new user.", caught);
							showAlertBlockThenHide(
									errorBlock, 
									"It is not possible to add this user at the moment, retry later.", 
									HIDE_AFTER_MILLISECONDS);
						}

						@Override
						public void onSuccess(VreUserBean result) {

							if(result == null){

								GWT.log("There was an error while adding the new user."
										+ " Are you sure he/she is not already present?");

								showAlertBlockThenHide(
										errorBlock, 
										"It is not possible to add this user at the moment, retry later."
												+ " Also check that he/she was not already registered in the portal.", 
												HIDE_AFTER_MILLISECONDS);

							}else{

								showAlertBlockThenHide(
										successBlock, 
										"User correctly added to this VRE", 
										HIDE_AFTER_MILLISECONDS);

								// append to the list of registered users
								eventBus.fireEvent(new AddUserEvent(result));

							}

						}
					});
		}

		// reset form
		form.reset();
	}

	private void showAlertBlockThenHide(final AlertBlock alert, String msg, int hideAfterMs){
		
		// hide loading image
		performingRequest.setVisible(false);
		
		// enable button again
		submit.setEnabled(true);

		// set text
		alert.setText(msg);
		alert.setVisible(true);

		// hide after a while
		Timer t  = new Timer() {
			@Override
			public void run() {
				alert.setVisible(false);
			}
		};
		t.schedule(hideAfterMs);

	}

	private void showError(String msg) {

		errorBlock.setText(msg);
		errorBlock.setVisible(true);

	}

	private void hideAlertBlocks(){

		errorBlock.setVisible(false);
		successBlock.setVisible(false);
	}

	@UiHandler("emailTextbox")
	void onChangeEmailTextbox(KeyUpEvent event){
		hideAlertBlocks();
	}

	@UiHandler("nameTextbox")
	void onChangeNameTextbox(KeyUpEvent event){
		hideAlertBlocks();
	}

	@UiHandler("surnameTextbox")
	void onChangeSurnameTextbox(KeyUpEvent event){
		hideAlertBlocks();
	}

	@UiHandler("companyTextbox")
	void onChangeCompanyTextbox(KeyUpEvent event){
		hideAlertBlocks();
	}

}
