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

public class QualifiedTokens extends Composite{

	private static QualifiedTokensUiBinder uiBinder = GWT
			.create(QualifiedTokensUiBinder.class);

	interface QualifiedTokensUiBinder extends UiBinder<Widget, QualifiedTokens> {
	}

	/**
	 * Create a remote service proxy to talk to the server-side Token service.
	 */
	private final TokenServiceAsync tokenServices = GWT.create(TokenService.class);

	@UiField 
	Button generateButton;
	@UiField
	ListBox createdQualifiedTokens;
	@UiField
	Button createNewQualifiedToken;
	@UiField
	ControlGroup newQualifiedTokenGroup;
	@UiField
	AlertBlock alertArea;
	@UiField
	TextBox tokenQualifierTextBox;
	@UiField
	ControlGroup qualifiedTokensGroupPanel;
	@UiField
	Icon loaderIcon;
	@UiField
	VerticalPanel serviceUnavailablePanel;
	@UiField
	TextBox tokenClear;
	@UiField
	PasswordTextBox tokenHide;
	@UiField
	ControlGroup showTokenControlGroup;
	@UiField
	Hero hero;
	@UiField
	Form mainForm;
	@UiField
	Button showToken;
	@UiField
	Paragraph tokenDescription;

	// qualified tokens list for this scope
	private List<TokenBean> qualifiedTokensList;

	public static final String REGEX_QUALIFIER = "^[a-zA-Z]+$";

	private static final String ERROR_MESSAGE_INVALID_QUALIFIER = "The inserted qualifier is not correct or missing";
	private static final String ERROR_MESSAGE_FAILED_CREATION = "Failed to create a qualified token having qualifier ";
	private static final String SUCCESS_MESSAGE_QUALIFIED_TOKEN  = "Qualified token created correctly";
	private static final String ALREADY_AVAILABLE_TOKEN = "A qualified token with this label already exists";

	// max number of qualified tokens that can be created
	private long maxNumberQualifiedTokens = 10; // TODO

	private final HandlerManager eventBus;
	private QualifiedTokens parent;

	public QualifiedTokens(HandlerManager eventBus) {
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
		tokenQualifierTextBox.addStyleName("input-text");

		createdQualifiedTokens.addItem("Select a Qualifier");
		createdQualifiedTokens.getElement().<SelectElement>cast().getOptions().getItem(0).setDisabled(true);
		bind();
		requireQualifiedTokens();
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
					newQualifiedTokenGroup.setVisible(false);					
					if(tokenClear.isVisible())
						showToken.click();
				}
			}
		});
	}

	/**
	 * Get the qualified tokens
	 */
	private void requireQualifiedTokens(){

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

		tokenServices.getQualifiedTokens(new AsyncCallback<List<TokenBean>>() {

			@Override
			public void onSuccess(final List<TokenBean> qualifiedTokens) {

				GWT.log("Qualified tokens are : " + qualifiedTokens);

				if(qualifiedTokens == null){

					showServiceUnavailableError();

				}else{

					qualifiedTokensList = new ArrayList<TokenBean>();

					for (TokenBean qualifiedToken : qualifiedTokens) {
						createdQualifiedTokens.addItem(qualifiedToken.getQualifier());
						qualifiedTokensList.add(qualifiedToken);
					}

					// add handler for listbox change event
					createdQualifiedTokens.addChangeHandler(new ChangeHandler() {

						@Override
						public void onChange(ChangeEvent event) {
							String token = findTokenForSelectedLabel();
							tokenHide.setText(token);
							tokenClear.setText(token);

							// set title to textbox
							tokenClear.setTitle("Your qualified token with label " + createdQualifiedTokens.getSelectedItemText());
							tokenHide.setTitle("Your qualified token with label " + createdQualifiedTokens.getSelectedItemText());
						}
					});

					if(qualifiedTokens.size() > 0){
						qualifiedTokensGroupPanel.setVisible(true);
						showTokenControlGroup.setVisible(true);
					}
					else{
						qualifiedTokensGroupPanel.setVisible(false);
						showTokenControlGroup.setVisible(false);	
						showToken.setVisible(false);
					}


					if(maxNumberQualifiedTokens > qualifiedTokens.size())
						createNewQualifiedToken.setVisible(true);

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
	private String findTokenForSelectedLabel(){

		String selectedItem = createdQualifiedTokens.getSelectedItemText();

		for (TokenBean qualifiedToken : qualifiedTokensList) {
			if(qualifiedToken.getQualifier().equals(selectedItem)){
				return  qualifiedToken.getToken();
			}
		}

		return null;
	}

	/**
	 * Service is not available... show error
	 */
	private void showServiceUnavailableError(){

		loaderIcon.setVisible(false);
		mainForm.setVisible(false);
		serviceUnavailablePanel.setVisible(true);

	}

	@UiHandler("generateButton")
	public void onGenerateClick(ClickEvent e) {

		newQualifiedTokenGroup.setType(ControlGroupType.NONE);
		String insertedQualifier = tokenQualifierTextBox.getText().trim();
		if(!insertedQualifier.isEmpty()){
			if(validateQualifier(insertedQualifier)){

				// check if it already present among the ones created
				for(TokenBean qualifiedToken : qualifiedTokensList)
					if(qualifiedToken.getQualifier().equals(insertedQualifier)){
						tokenQualifierTextBox.setText("");
						showInfo(ALREADY_AVAILABLE_TOKEN, AlertType.INFO, true);
						return;
					}

				requireQualifiedToken(insertedQualifier);
			}else
				showInfo(ERROR_MESSAGE_INVALID_QUALIFIER, AlertType.ERROR, true);
		}
		else
			newQualifiedTokenGroup.setType(ControlGroupType.ERROR);
	}

	@UiHandler("createNewQualifiedToken")
	void onAddQualifiedTokenClick(ClickEvent ce){

		// show qualifier textbox/ generate button
		newQualifiedTokenGroup.setVisible(true);
		tokenQualifierTextBox.setFocus(true);
		generateButton.setVisible(true);

	}

	/**
	 * Require a new qualified token
	 * @param qualifier
	 */
	private void requireQualifiedToken(final String qualifier){

		// disable generator and hide buttons
		tokenQualifierTextBox.setEnabled(false);
		generateButton.setEnabled(false);

		// remote call
		tokenServices.createQualifiedToken(qualifier, new AsyncCallback<TokenBean>() {

			@Override
			public void onSuccess(TokenBean createdToken) {

				if(createdToken != null){

					GWT.log("Created token is " + createdToken);
					qualifiedTokensList.add(createdToken);
					createdQualifiedTokens.addItem(createdToken.getQualifier());
					tokenQualifierTextBox.setText("");

					newQualifiedTokenGroup.setVisible(false);
					generateButton.setVisible(false);
					showToken.setVisible(true);
					
					qualifiedTokensGroupPanel.setVisible(true);
					showTokenControlGroup.setVisible(true);
					
					// if maximum number is reached, remove the add button
					if(qualifiedTokensList.size() == maxNumberQualifiedTokens){
						createNewQualifiedToken.removeFromParent();
						newQualifiedTokenGroup.removeFromParent();
						generateButton.removeFromParent();
					}

					showInfo(SUCCESS_MESSAGE_QUALIFIED_TOKEN + " '" + qualifier + "'", AlertType.SUCCESS, true);

				}else
					showInfo(ERROR_MESSAGE_FAILED_CREATION + " '" + qualifier + "'", AlertType.ERROR, true);

				tokenQualifierTextBox.setEnabled(true);
				generateButton.setEnabled(true);

			}

			@Override
			public void onFailure(Throwable caught) {
				showInfo(ERROR_MESSAGE_FAILED_CREATION + " '" + qualifier + "'", AlertType.ERROR, true);
				tokenQualifierTextBox.setEnabled(true);
				generateButton.setEnabled(true);
			}
		});
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
