package org.gcube.portlets.user.tokengenerator.client.ui.subpanels;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.tokengenerator.client.TokenService;
import org.gcube.portlets.user.tokengenerator.client.TokenServiceAsync;
import org.gcube.portlets.user.tokengenerator.client.events.CloseAccordionEvent;
import org.gcube.portlets.user.tokengenerator.client.events.CloseAccordionEventHandler;
import org.gcube.portlets.user.tokengenerator.shared.TokenBean;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Hero;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationTokens extends Composite{

	private static ApplicationTokensUiBinder uiBinder = GWT
			.create(ApplicationTokensUiBinder.class);

	interface ApplicationTokensUiBinder extends
	UiBinder<Widget, ApplicationTokens> {
	}

	/**
	 * Create a remote service proxy to talk to the server-side Token service.
	 */
	private final TokenServiceAsync tokenServices = GWT.create(TokenService.class);
	public static final String REGEX_QUALIFIER = "^[a-zA-Z0-9]+$";
	private static final String ERROR_MESSAGE_APPLICATION_TOKEN_CREATION = "Failed to create the application token you required, sorry";
	private static final String SUCCESS_MESSAGE_APPLICATION_TOKEN  = "Your application token with identifier ";
	private static final String ALREADY_AVAILABLE_TOKEN = "An application token with this label already exists";
	private static final String ERROR_MESSAGE_INVALID_QUALIFIER = "The inserted qualifier is not correct or missing.";

	private ArrayList<TokenBean> applicationTokensList;

	private int maxNumberApplicationTokens = 10; // @TODO

	@UiField 
	Button generateButton;
	@UiField
	ListBox createdApplicationTokens;
	@UiField
	Button createNewApplicationToken;
	@UiField
	ControlGroup newApplicationTokenGroup;
	@UiField
	AlertBlock alertArea;
	@UiField
	TextBox tokenApplicationTextBox;
	@UiField
	ControlGroup applicationTokensGroupPanel;
	@UiField
	Icon loaderIcon;
	@UiField
	VerticalPanel serviceUnavailablePanel;
	@UiField
	TextBox tokenClear;
	@UiField
	PasswordTextBox tokenHide;
	@UiField
	Paragraph tokenDescription;
	@UiField
	Form mainForm;
	@UiField
	Button showToken;
	@UiField
	Hero hero;
	@UiField
	ControlGroup showTokenControlGroup;

	private final HandlerManager eventBus;
	private ApplicationTokens parent;

	public ApplicationTokens(HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
		parent = this;
		generateButton.addStyleName("float-right-button");
		alertArea.addStyleName("alert-area-style");
		hero.addStyleName("hero-style-custom");
		showToken.addStyleName("float-right-button");
		tokenClear.addStyleName("input-text");
		tokenHide.addStyleName("input-text");
		tokenDescription.getElement().getStyle().setTextAlign(TextAlign.JUSTIFY);
		tokenApplicationTextBox.addStyleName("input-text");

		createdApplicationTokens.addItem("Select an identifier");
		createdApplicationTokens.getElement().<SelectElement>cast().getOptions().getItem(0).setDisabled(true);
		bind();
		requireApplicationTokens();
	}


	/**
	 * Bind on events
	 */
	private void bind() {

		eventBus.addHandler(CloseAccordionEvent.TYPE, new CloseAccordionEventHandler() {

			@Override
			public void onCloseAccordion(CloseAccordionEvent event) {

				if(parent.equals(event.getClosedPanel())){
					generateButton.setVisible(false);
					newApplicationTokenGroup.setVisible(false);
					if(tokenClear.isVisible())
						showToken.click();
				}

			}
		});
	}


	private void requireApplicationTokens() {

		tokenClear.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				tokenClear.selectAll();
			}
		});

		tokenClear.setVisible(false);
		tokenClear.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				tokenClear.selectAll();
			}
		});

		tokenServices.getApplicationTokens(new AsyncCallback<List<TokenBean>>() {

			@Override
			public void onSuccess(final List<TokenBean> applicationTokens) {

				GWT.log("Application tokens are : " + applicationTokens);

				if(applicationTokens == null){

					showServiceUnavailableError();

				}else{

					applicationTokensList = new ArrayList<TokenBean>();

					for (TokenBean applicationToken : applicationTokens) {
						createdApplicationTokens.addItem(applicationToken.getQualifier());
						applicationTokensList.add(applicationToken);
					}

					// add handler for listbox change event
					createdApplicationTokens.addChangeHandler(new ChangeHandler() {

						@Override
						public void onChange(ChangeEvent event) {
							String token = findTokenForSelectedLabel();
							tokenHide.setText(token);
							tokenClear.setText(token);

							// set title to textbox
							tokenClear.setTitle("Your application token for identifier " + createdApplicationTokens.getSelectedItemText());
							tokenHide.setTitle("Your application token for identifier " + createdApplicationTokens.getSelectedItemText());
						}
					});

					if(applicationTokens.size() > 0){
						applicationTokensGroupPanel.setVisible(true);
						showTokenControlGroup.setVisible(true);
					}
					else{
						applicationTokensGroupPanel.setVisible(false);
						showTokenControlGroup.setVisible(false);
						showToken.setVisible(false);
					}


					if(maxNumberApplicationTokens > applicationTokens.size())
						createNewApplicationToken.setVisible(true);

					mainForm.setVisible(true);
					loaderIcon.setVisible(false);
				}
			}

			@Override
			public void onFailure(Throwable caught) {

				showServiceUnavailableError();
			}
		});

		showToken.setIcon(IconType.EYE_OPEN);
		showToken.setTitle("Show token");
		showToken.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showTokenHandlerBody(showToken, tokenHide, tokenClear);
			}
		});

	}

	/**
	 * Find the token given the qualifier
	 * @return
	 */
	private String findTokenForSelectedLabel() {

		String selectedItem = createdApplicationTokens.getSelectedItemText();

		for (TokenBean applicationToken : applicationTokensList) 
			if(applicationToken.getQualifier().equals(selectedItem)){
				return  applicationToken.getToken();
			}

		return null;
	}

	/**
	 * Require a new application token
	 * @param applicationIdentifier
	 */
	private void requireApplicationToken(final String applicationIdentifier) {

		// disable generator and hide buttons
		tokenApplicationTextBox.setEnabled(false);
		generateButton.setEnabled(false);

		tokenServices.createApplicationToken(applicationIdentifier, new AsyncCallback<TokenBean>() {

			@Override
			public void onSuccess(TokenBean applicationToken) {

				if(applicationToken != null){

					showInfo(SUCCESS_MESSAGE_APPLICATION_TOKEN + " '" + applicationIdentifier + "' has been correctly created", AlertType.SUCCESS, true);
					tokenApplicationTextBox.setText("");
					showToken.setVisible(true);

					GWT.log("Created token is " + applicationToken);
					applicationTokensList.add(applicationToken);
					createdApplicationTokens.addItem(applicationToken.getQualifier());

					applicationTokensGroupPanel.setVisible(true);
					showTokenControlGroup.setVisible(true);
					createdApplicationTokens.setVisible(true);
					newApplicationTokenGroup.setVisible(false);
					generateButton.setVisible(false);

					// if maximum number is reached, remove the add button
					if(applicationTokensList.size() == maxNumberApplicationTokens){
						createNewApplicationToken.removeFromParent();
						newApplicationTokenGroup.removeFromParent();
						generateButton.removeFromParent();
					}
				}
				else{
					showInfo(ERROR_MESSAGE_APPLICATION_TOKEN_CREATION, AlertType.ERROR, true);
				}

				tokenApplicationTextBox.setEnabled(true);
				generateButton.setEnabled(true);
			}

			@Override
			public void onFailure(Throwable caught) {
				showInfo(ERROR_MESSAGE_APPLICATION_TOKEN_CREATION, AlertType.ERROR, true);
				tokenApplicationTextBox.setEnabled(true);
				generateButton.setEnabled(true);
			}
		});

		showToken.setIcon(IconType.EYE_OPEN);
		showToken.setTitle("Show token");
		showToken.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showTokenHandlerBody(showToken, tokenHide, tokenClear);
			}
		});

	}

	/**
	 * Show errors/info
	 */
	private void showInfo(String message, AlertType type, boolean hideAfterAWhile){

		// add the error message
		alertArea.setText(message);
		alertArea.setType(type);
		alertArea.setVisible(true);
		alertArea.setClose(false);

		if(hideAfterAWhile){
			Timer t = new Timer() {

				@Override
				public void run() {

					alertArea.setVisible(false);

				}
			};
			t.schedule(3000);
		}
	}

	@UiHandler("generateButton")
	public void onGenerateClick(ClickEvent e) {

		if(newApplicationTokenGroup.isVisible()){
			String applicationIdentifier = tokenApplicationTextBox.getText().trim();
			newApplicationTokenGroup.setType(ControlGroupType.NONE);
			if(!applicationIdentifier.isEmpty()){
				if(validateQualifier(applicationIdentifier)){
					// check if it already present among the ones created
					for(TokenBean tokens : applicationTokensList)
						if(tokens.getQualifier().equals(applicationIdentifier)){
							tokenApplicationTextBox.setText("");
							showInfo(ALREADY_AVAILABLE_TOKEN, AlertType.INFO, true);
							return;
						}

					requireApplicationToken(applicationIdentifier);
				}else
					showInfo(ERROR_MESSAGE_INVALID_QUALIFIER, AlertType.ERROR, true);
			}else
				newApplicationTokenGroup.setType(ControlGroupType.ERROR);
		}

	}

	/**
	 * Validate the qualifier the user wants to assign the token
	 * @return
	 */
	private boolean validateQualifier(String qualifier){
		String qualifierToCheck = qualifier.trim();
		if(qualifierToCheck != null && !qualifierToCheck.isEmpty())
			return qualifierToCheck.matches(REGEX_QUALIFIER);
		else
			return false;
	}

	@UiHandler("createNewApplicationToken")
	public void onCreateApplicationToken(ClickEvent e){

		newApplicationTokenGroup.setVisible(true);
		tokenApplicationTextBox.setFocus(true);
		generateButton.setVisible(true);

	}

	/**
	 * Service is not available... show error
	 */
	private void showServiceUnavailableError(){

		loaderIcon.setVisible(false);
		mainForm.setVisible(false);
		serviceUnavailablePanel.setVisible(true);

	}

	/**
	 * Body for the handler that shows the token.
	 * @param showButton
	 * @param pwTextBox
	 * @param tb
	 */
	private void showTokenHandlerBody(final Button showButton, final PasswordTextBox pwTextBox, final TextBox tb){

		if(findTokenForSelectedLabel() != null){
			// change the button and its handler
			showButton.setIcon(IconType.EYE_CLOSE);
			showButton.setTitle("Hide Token");
			showButton.setText("Hide");

			showButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					hideTokenHandlerBody(showButton, tb, pwTextBox);
				}
			});

			// show tb and hide pwTextBox
			tb.setVisible(true);
			tb.setText(findTokenForSelectedLabel());
			pwTextBox.setVisible(false);
		}
	}

	/**
	 * Body for the handler that hides the token.
	 * @param showButton
	 * @param tb
	 * @param pwTextBox
	 */
	private void hideTokenHandlerBody(final Button showButton, final TextBox tb, final PasswordTextBox pwTextBox){

		if(findTokenForSelectedLabel() != null){
			// change the button and its handler
			showButton.setIcon(IconType.EYE_OPEN);
			showButton.setTitle("Show Token");
			showButton.setText("Show");

			showButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					showTokenHandlerBody(showButton, pwTextBox, tb);
				}
			});

			// hide tb and show pwTextBox
			tb.setVisible(false);
			tb.setText("");
			pwTextBox.setVisible(true);
		}
	}

}
