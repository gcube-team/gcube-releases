/**
 * 
 */
package org.gcube.portlets.widgets.inviteswidget.client.ui;

import org.gcube.portal.databook.shared.InviteOperationResult;
import org.gcube.portlets.widgets.inviteswidget.client.InviteService;
import org.gcube.portlets.widgets.inviteswidget.client.InviteServiceAsync;
import org.gcube.portlets.widgets.inviteswidget.client.validation.FormView;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpBlock;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDriver;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class FormViewImpl extends Composite implements FormView, Editor<FormView.UserDetails> {

	/**
	 * Create a remote service proxy to talk to the server-side service
	 */
	private final InviteServiceAsync service = GWT.create(InviteService.class);

	interface Binder extends UiBinder<Widget, FormViewImpl> {}
	private static Binder uiBinder = GWT.create(Binder.class);

	interface Driver extends SimpleBeanEditorDriver<FormView.UserDetails, FormViewImpl> { }	
	private Driver driver = GWT.create(Driver.class);

	private final Delegate delegate;

	@UiField TextBox email;
	@UiField TextBox name;

	@UiField Button openModal;
	@UiField Button cancelInvite;
	@UiField Button sendInvite;
	@UiField Modal modalWindow;
	@UiField HelpBlock nameErrors;
	@UiField HelpBlock feedback;

	@UiField
	ControlGroup nameGroup;
	@UiField
	ControlGroup feedbackGroup;

	public FormViewImpl(Delegate delegate) {
		super();
		this.delegate = delegate;
		initWidget(uiBinder.createAndBindUi(this));
		driver.initialize(this);
		driver.edit(new UserDetails());

		email.addKeyDownHandler(new KeyDownHandler() {			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
					openModal.click();				
			}
		});

		name.addKeyDownHandler(new KeyDownHandler() {			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
					sendInvite.click();				
			}
		});

		sendInvite.setEnabled(true);
	}

	@UiHandler("openModal")
	void onUserClick(ClickEvent event) {
		if (!delegate.onSendClick()) {
			service.accountExistInVRE(email.getText(), new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Ops, There were problems communicating with the server, please retry later or report this issue to http://support.d4science.org");				
				}

				@Override
				public void onSuccess(Boolean userAccountExists) {
					if (userAccountExists) {
						Window.alert("A user with the same email already exists in this VRE, the invite cannot be sent.");
					} else {
						sendInvite.setEnabled(true);
						feedback.setText("This gateway will send the invitation email to: "+email.getText() + " and put you in cc. It will never email your contacts without your consent.");
						name.setText("");
						modalWindow.show();
						Timer t = new Timer() {				
							@Override
							public void run() {
								name.setFocus(true);					
							}
						};
						t.schedule(1000);	
					}
				}
			});

		}
	}

	@UiHandler("sendInvite")
	void onSendInviteClick(ClickEvent event) {
		if (delegate.onEmailSendInviteClick())  {
			nameGroup.setType(ControlGroupType.ERROR);
			nameErrors.setText("Name must not be null");
		} else {
			nameGroup.setType(ControlGroupType.NONE);
			nameErrors.setText("");
			service.sendInvite(name.getText(), "", email.getText(), new AsyncCallback<InviteOperationResult>() {

				@Override
				public void onSuccess(InviteOperationResult result) {
					switch (result) {
					case SUCCESS:
						feedback.setText("Thanks for inviting, we sent the email correctly.");	
						feedbackGroup.setType(ControlGroupType.SUCCESS);
						break;
					case ALREADY_INVITED:
						feedback.setText("Thanks for inviting, however the user was already invited.");	
						feedbackGroup.setType(ControlGroupType.WARNING);
						break;
					case FAILED:
						feedback.setText("Sorry, an error occurred in the server and we could not send the invite, please try again later.");	
						feedbackGroup.setType(ControlGroupType.ERROR);
						break;
					}					
					sendInvite.setEnabled(false);
					cancelInvite.setText("Close");
					email.setText("");
				}

				@Override
				public void onFailure(Throwable caught) {
					feedback.setText("Sorry, an error occurred and we could not send the invite, please try again later.");	
					feedbackGroup.setType(ControlGroupType.ERROR);
					sendInvite.setEnabled(false);
					email.setText("");
				}
			});
		}
	}

	@UiHandler("cancelInvite")
	public void onCancelClick(ClickEvent e) {
		modalWindow.hide();
		email.setText("");
	}

	@Override
	public EditorDriver<FormView.UserDetails> getEditorDriver() {
		return driver;
	}
}

