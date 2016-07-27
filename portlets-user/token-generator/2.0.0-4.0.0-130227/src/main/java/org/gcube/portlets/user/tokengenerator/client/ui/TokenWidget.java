package org.gcube.portlets.user.tokengenerator.client.ui;


import org.gcube.portlets.user.tokengenerator.client.TokenService;
import org.gcube.portlets.user.tokengenerator.client.TokenServiceAsync;
import org.gcube.portlets.user.tokengenerator.shared.UserBean;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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

	public static final String ERROR_MESSAGE = "Sorry, it is not possible to contact the service!";

	// save the token
	private String token;

	// info to show
	private final static HTML aboutTokenGeneratorHeader = new HTML(
			"<span style=\"font-size:14px; font-weight:bold\">"+"About this Token"+"</span>"
			);

	private final static HTML aboutTokenGeneratorBody = new HTML(
			"<p>"+"This Token is needed for external VRE service calls."+"</p>"
			);

	private static InviteWidgetUiBinder uiBinder = GWT.create(InviteWidgetUiBinder.class);

	interface InviteWidgetUiBinder extends UiBinder<Widget, TokenWidget> {	}

	/**
	 * Create a remote service proxy to talk to the server-side Token service.
	 */
	private final TokenServiceAsync tokenService = GWT.create(TokenService.class);

	@UiField 
	LoadingText loader;
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

	public TokenWidget() {

		super();
		initWidget(uiBinder.createAndBindUi(this));

		// change style to the info button
		infoButton.addStyleName("token-info-button");

		// change popover direction
		popover.setPlacement(Placement.BOTTOM);

		// change popover text header
		popover.setHeading(aboutTokenGeneratorHeader.getHTML());

		// change popover text content
		popover.setText(aboutTokenGeneratorBody.getHTML());

		// set the popover's text to be html
		popover.setHtml(true);

		// add some css
		tokenClear.addStyleName("input-text");
		tokenHide.addStyleName("input-text");
		currentUsername.addStyleName("input-text");

		// automatically check/require the token
		requireToken();		
	}


	@UiHandler("generateButton")
	public void onGenerateClick(ClickEvent e) {

		requireToken();

	}

	/**
	 * Used to check if the token exists or to require it.
	 */
	private void requireToken(){

		loader.setVisible(true);
		generateButton.setVisible(false);

		// hide the form
		formTokenUsername.setVisible(false);

		tokenService.getServiceToken(new AsyncCallback<UserBean>() {
			@Override
			public void onSuccess(UserBean bean) {

				// hide the loader
				loader.setVisible(false);

				if(bean != null){

					// show the form
					formTokenUsername.setVisible(true);

					token = bean.getToken();
					String username = bean.getUsername();

					// username textbox
					currentUsername.setTitle("Your current username");
					currentUsername.setText(username);

					// textbox hide content
					tokenHide.setTitle("Your token for this VRE");
					tokenHide.setText(token);
					tokenHide.setVisible(true);

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
					tokenClear.setTitle("Your token for this VRE");
					tokenClear.setVisible(false);

					// add show button
					showToken.setIcon(IconType.EYE_OPEN);
					showToken.setTitle("Show token");

					// add copy handler
					showToken.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {

							showTokenHandlerBody(showToken, tokenHide, tokenClear);

						}

					});

					showToken.addStyleName("token-button-show-hide");

					// hide the code generation button
					generateButton.setVisible(false);
				}
				else{

					// show the code generation button
					generateButton.setVisible(true);
					generateButton.setTitle("Require a token");

				}
			}

			@Override
			public void onFailure(Throwable caught) {

				// hide the loader
				loader.setVisible(false);

				// hide the request button
				generateButton.setVisible(false);

				// add the error message
				Alert alert = new Alert(ERROR_MESSAGE);
				alert.setType(AlertType.ERROR);
				alert.setClose(false);

				// add to the placeholder
				placeHolder.add(alert);

			}
		});
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
		showButton.setTitle("Hide token");
		showButton.setText("Hide");

		// show username info
		currentUsernameInfo.setVisible(true);

		showButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				hideTokenHandlerBody(showButton, tb, pwTextBox);
			}
		});

		// show tb and hide pwTextBox
		tb.setVisible(true);
		tb.setText(token);
		pwTextBox.setVisible(false);

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
		showButton.setTitle("Show token");
		showButton.setText("Show");

		// hide username info
		currentUsernameInfo.setVisible(false);

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
