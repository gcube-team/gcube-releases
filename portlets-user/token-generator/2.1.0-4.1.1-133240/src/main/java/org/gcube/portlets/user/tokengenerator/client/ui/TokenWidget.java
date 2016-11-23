package org.gcube.portlets.user.tokengenerator.client.ui;


import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.tokengenerator.client.TokenService;
import org.gcube.portlets.user.tokengenerator.client.TokenServiceAsync;
import org.gcube.portlets.user.tokengenerator.shared.QualifiedToken;
import org.gcube.portlets.user.tokengenerator.shared.TokenBean;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Token generator widget
 * @author Massimiliano Assante, ISTI-CNR
 * @author Costantino Perciante, ISTI-CNR
 */
public class TokenWidget extends Composite {

	private static final String ERROR_MESSAGE_INVALID_QUALIFIER = "The inserted qualifier is not correct or missing";
	private static final String ERROR_MESSAGE_FAILED_CREATION = "Failed to create a qualified Token having qualifier ";
	private static final String SUCCESS_MESSAGE_QUALIFIED_TOKEN  = "Qualified Token created correctly";
	private static final String MESSAGE_TOKEN_FOR_CONTEXT = "Your Token for ";

	// The token for this context (not qualified one)
	private TokenBean notQualifiedTokenBean;

	// max number of qualified tokens that can be created
	private long maxNumberQualifiedTokens = 10; // TODO

	// qualified tokens list for this scope
	private List<QualifiedToken> qualifiedTokensList = new ArrayList<QualifiedToken>();

	// info to show for popover
	private final static HTML aboutTokenGeneratorHeader = new HTML("<span style=\"font-size:14px; font-weight:bold\">"+"About this Token"+"</span>");
	private final static HTML aboutTokenGeneratorBody = new HTML("<p>"+"This Token is needed for external VRE service calls."+"</p>");
	public static final String REGEX_QUALIFIER = "^[a-zA-Z]+$";

	private static InviteWidgetUiBinder uiBinder = GWT.create(InviteWidgetUiBinder.class);
	interface InviteWidgetUiBinder extends UiBinder<Widget, TokenWidget> {}

	/**
	 * Create a remote service proxy to talk to the server-side Token service.
	 */
	private final TokenServiceAsync tokenServices = GWT.create(TokenService.class);

	@UiField 
	LoadingText loader;
	@UiField
	VerticalPanel serviceUnavailablePanel;
	@UiField 
	HTMLPanel tokenPanel;
	@UiField 
	Button generateButton;
	@UiField 
	VerticalPanel placeHolder;
	@UiField 
	Button infoButton;
	@UiField
	Button showToken;
	@UiField
	Popover popover;
	@UiField
	TextBox currentUsername;
	@UiField
	TextBox tokenClear;
	@UiField
	PasswordTextBox tokenHide;
	@UiField
	Form formTokenUsername;
	@UiField
	ControlGroup currentUsernameInfo;
	@UiField
	Button advancedTokenGeneration;
	@UiField
	Button hideAdvancedTokenGeneration;
	@UiField
	VerticalPanel advancedPanel;
	@UiField
	ListBox createdQualifiedTokens;
	@UiField
	Button createNewQualifiedToken;
	@UiField
	ControlGroup newQualifiedTokenGroup;
	@UiField
	AlertBlock alertArea;
	@UiField
	TextBox tokenQualifier;
	@UiField
	ControlGroup qualifiedTokensGroupPanel;

	public TokenWidget() {

		super();
		initWidget(uiBinder.createAndBindUi(this));

		infoButton.addStyleName("token-info-button");

		popover.setPlacement(Placement.BOTTOM);
		popover.setHeading(aboutTokenGeneratorHeader.getHTML());
		popover.setText(aboutTokenGeneratorBody.getHTML());
		popover.setHtml(true);

		tokenClear.addStyleName("input-text");
		tokenHide.addStyleName("input-text");
		currentUsername.addStyleName("input-text");
		showToken.addStyleName("float-right-button");
		generateButton.addStyleName("float-right-button");
		alertArea.addStyleName("alert-area-style");

		createdQualifiedTokens.addItem("Select a Qualifier");
		createdQualifiedTokens.getElement().getElementsByTagName("option").getItem(0).setAttribute("disabled", "disabled");

		// automatically require the token and the qualified tokens list
		requireTokenAndQualifiedTokens();		
	}

	/**
	 * Used to require the token for this context and the list of qualified tokens.
	 */
	private void requireTokenAndQualifiedTokens(){

		loader.setVisible(true);
		formTokenUsername.setVisible(false);

		tokenServices.getServiceToken(new AsyncCallback<TokenBean>() {
			@Override
			public void onSuccess(TokenBean tokenBean) {

				if(tokenBean != null){

					// save information
					notQualifiedTokenBean = tokenBean;
					String username = tokenBean.getUsername();

					// username textbox
					currentUsername.setText(username);

					// textbox hide content
					tokenHide.setText(notQualifiedTokenBean.getToken());
					tokenHide.setVisible(true);
					if(tokenBean.getContext() != null && !tokenBean.getContext().isEmpty())
						tokenHide.setTitle(MESSAGE_TOKEN_FOR_CONTEXT + tokenBean.getContext());
					else 
						tokenHide.setTitle("");

					// textbox clear handlers
					tokenClear.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {

							tokenClear.selectAll();

						}
					});

					tokenClear.addDoubleClickHandler(new DoubleClickHandler() {

						@Override
						public void onDoubleClick(DoubleClickEvent event) {

							tokenClear.selectAll();

						}
					});

					tokenClear.setVisible(false);

					// show token button
					showToken.setIcon(IconType.EYE_OPEN);
					showToken.setTitle("Show token");
					showToken.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {

							showTokenHandlerBody(showToken, tokenHide, tokenClear);

						}

					});

					// set title
					if(tokenBean.getContext() != null){
						tokenClear.setTitle(MESSAGE_TOKEN_FOR_CONTEXT + tokenBean.getContext());
						tokenHide.setTitle(MESSAGE_TOKEN_FOR_CONTEXT + tokenBean.getContext());
					}else{
						tokenClear.setTitle("");
						tokenHide.setTitle("");
					}

					// retrieve qualified tokens
					tokenServices.getQualifiedTokens(new AsyncCallback<List<QualifiedToken>>() {

						@Override
						public void onSuccess(final List<QualifiedToken> qualifiedTokens) {

							GWT.log("Qualified tokens are : " + qualifiedTokens);

							if(qualifiedTokens == null){

								showServiceUnavailableError();

							}else{

								for (QualifiedToken qualifiedToken : qualifiedTokens) {
									createdQualifiedTokens.addItem(qualifiedToken.getQualifier());
									qualifiedTokensList.add(qualifiedToken);
								}

								// add handler for listbox change event
								createdQualifiedTokens.addChangeHandler(new ChangeHandler() {

									@Override
									public void onChange(ChangeEvent event) {
										String selectedItem = createdQualifiedTokens.getSelectedItemText();

										for (QualifiedToken qualifiedToken : qualifiedTokensList) {
											if(qualifiedToken.getQualifier().equals(selectedItem)){

												if(tokenHide.isVisible())
													tokenHide.setText(qualifiedToken.getToken());
												else
													tokenClear.setText(qualifiedToken.getToken());

												// set title to textbox
												tokenClear.setTitle("Your qualified token with label " + qualifiedToken.getQualifier());
												tokenHide.setTitle("Your qualified token with label " + qualifiedToken.getQualifier());

												break;
											}
										}
									}
								});

								if(qualifiedTokens.size() > 0)
									qualifiedTokensGroupPanel.setVisible(true);
								else
									qualifiedTokensGroupPanel.setVisible(false);


								if(maxNumberQualifiedTokens > qualifiedTokens.size())
									createNewQualifiedToken.setVisible(true);


								loader.setVisible(false);
								formTokenUsername.setVisible(true);
							}
						}

						@Override
						public void onFailure(Throwable caught) {

							showServiceUnavailableError();
						}
					});

				}else{

					showServiceUnavailableError();

				}
			}

			@Override
			public void onFailure(Throwable caught) {

				showServiceUnavailableError();

			}
		});
	}

	/**
	 * Show errors/info
	 */
	private void showInfo(String message, AlertType type){

		// add the error message
		alertArea.setText(message);
		alertArea.setType(type);
		alertArea.setVisible(true);
		alertArea.setClose(false);

		Timer t = new Timer() {

			@Override
			public void run() {

				alertArea.setVisible(false);

			}
		};
		t.schedule(3000);
	}

	/**
	 * Service is not available... show error
	 */
	private void showServiceUnavailableError(){

		loader.setVisible(false);
		serviceUnavailablePanel.setVisible(true);

	}

	@UiHandler("createNewQualifiedToken")
	void onAddQualifiedTokenClick(ClickEvent ce){

		// show qualifier textbox/ generate button
		newQualifiedTokenGroup.setVisible(true);
		generateButton.setVisible(true);

	}

	/**
	 * Body for the handler that shows the token.
	 * @param showButton
	 * @param pwTextBox
	 * @param tb
	 */
	private void showTokenHandlerBody(final Button showButton, final PasswordTextBox pwTextBox, final TextBox tb){

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
		tb.setText(pwTextBox.getText());
		pwTextBox.setVisible(false);
		currentUsernameInfo.setVisible(true);

	}

	/**
	 * Body for the handler that hides the token.
	 * @param showButton
	 * @param tb
	 * @param pwTextBox
	 */
	private void hideTokenHandlerBody(final Button showButton, final TextBox tb, final PasswordTextBox pwTextBox){

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
		currentUsernameInfo.setVisible(false);

	}

	@UiHandler("advancedTokenGeneration")
	public void onAdvancedButtonClick(ClickEvent ce){

		// show hide button, hide advanced button
		hideAdvancedTokenGeneration.setVisible(true);
		advancedTokenGeneration.setVisible(false);
		advancedPanel.setVisible(true);

	}

	@UiHandler("hideAdvancedTokenGeneration")
	public void onHideAdvancedButtonClick(ClickEvent ce){
		// hide hide button, show advanced button
		hideAdvancedTokenGeneration.setVisible(false);
		advancedTokenGeneration.setVisible(true);
		advancedPanel.setVisible(false);

		// set back the not qualified token
		tokenClear.setText(notQualifiedTokenBean.getToken());
		tokenHide.setText(notQualifiedTokenBean.getToken());

		// set title
		if(notQualifiedTokenBean.getContext() != null){ 
			tokenClear.setTitle(MESSAGE_TOKEN_FOR_CONTEXT + notQualifiedTokenBean.getContext());
			tokenHide.setTitle(MESSAGE_TOKEN_FOR_CONTEXT + notQualifiedTokenBean.getContext());
		}else{
			tokenClear.setTitle("");
			tokenHide.setTitle("");
		}

		// hide creation part form
		newQualifiedTokenGroup.setVisible(false);
		generateButton.setVisible(false);

		// reset listbox of qualified tokens
		if(createdQualifiedTokens != null && createdQualifiedTokens.getItemCount() > 0)
			createdQualifiedTokens.setSelectedIndex(0);
	}

	@UiHandler("generateButton")
	public void onGenerateClick(ClickEvent e) {

		String insertedQualifier = tokenQualifier.getText().trim();
		if(validateQualifier(insertedQualifier)){

			// check if it already present among the ones created
			for(QualifiedToken qualifiedToken : qualifiedTokensList)
				if(qualifiedToken.getQualifier().equals(insertedQualifier))
					return;

			requireQualifiedToken(insertedQualifier);
		}
		else{
			showInfo(ERROR_MESSAGE_INVALID_QUALIFIER, AlertType.ERROR);
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

	/**
	 * Require a new qualified token
	 * @param qualifier
	 */
	private void requireQualifiedToken(final String qualifier){

		// disable generator and hide buttons
		tokenQualifier.setEnabled(false);
		generateButton.setEnabled(false);
		hideAdvancedTokenGeneration.setEnabled(false);

		// remote call
		tokenServices.createQualifiedToken(qualifier, new AsyncCallback<QualifiedToken>() {

			@Override
			public void onSuccess(QualifiedToken createdToken) {

				if(createdToken != null){

					GWT.log("Created token is " + createdToken);

					qualifiedTokensGroupPanel.setVisible(true);
					qualifiedTokensList.add(createdToken);
					createdQualifiedTokens.addItem(createdToken.getQualifier());
					createdQualifiedTokens.setSelectedIndex(0);
					tokenQualifier.setText("");

					// if maximum number is reached, remove the add button
					if(qualifiedTokensList.size() == maxNumberQualifiedTokens){
						createNewQualifiedToken.removeFromParent();
						generateButton.removeFromParent();
						newQualifiedTokenGroup.removeFromParent();
					}

					showInfo(SUCCESS_MESSAGE_QUALIFIED_TOKEN + " '" + qualifier + "'", AlertType.SUCCESS);

				}else
					showInfo(ERROR_MESSAGE_FAILED_CREATION + " '" + qualifier + "'", AlertType.ERROR);


				tokenQualifier.setEnabled(true);
				generateButton.setEnabled(true);
				hideAdvancedTokenGeneration.setEnabled(true);

			}

			@Override
			public void onFailure(Throwable caught) {
				showInfo(ERROR_MESSAGE_FAILED_CREATION + " '" + qualifier + "'", AlertType.ERROR);
				tokenQualifier.setEnabled(true);
				generateButton.setEnabled(true);
				hideAdvancedTokenGeneration.setEnabled(true);
			}
		});
	}

}
