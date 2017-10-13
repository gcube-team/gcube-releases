package org.gcube.portlets.user.tokengenerator.client.ui.subpanels;

import org.gcube.portlets.user.tokengenerator.client.TokenService;
import org.gcube.portlets.user.tokengenerator.client.TokenServiceAsync;
import org.gcube.portlets.user.tokengenerator.client.events.CloseAccordionEvent;
import org.gcube.portlets.user.tokengenerator.client.events.CloseAccordionEventHandler;
import org.gcube.portlets.user.tokengenerator.client.ui.LoadingText;
import org.gcube.portlets.user.tokengenerator.shared.TokenBean;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Hero;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TokenContexPanel extends Composite {

	private static TokenContexPanelUiBinder uiBinder = GWT
			.create(TokenContexPanelUiBinder.class);

	interface TokenContexPanelUiBinder extends
	UiBinder<Widget, TokenContexPanel> {
	}

	@UiField
	ControlGroup username;

	@UiField
	TextBox currentUsername;

	@UiField
	TextBox tokenClear;

	@UiField
	PasswordTextBox tokenHide;

	@UiField
	Button showToken;

	@UiField
	Form formTokenUsername;
	
	@UiField
	VerticalPanel serviceUnavailablePanel;
	
	@UiField
	LoadingText loader;
	
	@UiField
	Hero hero;
	
	@UiField
	Paragraph tokenDescription;
	
	private final TokenServiceAsync tokenServices = GWT.create(TokenService.class);
	private static final String MESSAGE_TOKEN_FOR_CONTEXT = "Your Token for ";
	private TokenBean tokenContext;
	private HandlerManager eventBus;
	private TokenContexPanel parent;

	public TokenContexPanel(HandlerManager eventBus) {
		
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
		this.parent = this;
		tokenClear.addStyleName("input-text");
		tokenHide.addStyleName("input-text");
		currentUsername.addStyleName("input-text");
		showToken.addStyleName("float-right-button");
		hero.addStyleName("hero-style-custom");
		tokenDescription.getElement().getStyle().setTextAlign(TextAlign.JUSTIFY);
		requireContextToken();	
		bind();
	}

	/**
	 * Bind on events
	 */
	private void bind() {

		eventBus.addHandler(CloseAccordionEvent.TYPE, new CloseAccordionEventHandler() {

			@Override
			public void onCloseAccordion(CloseAccordionEvent event) {

				if(parent.equals(event.getClosedPanel())){				
					if(tokenClear.isVisible())
						showToken.click();
				}
			}
		});
	}

	/**
	 * Used to require the token for this context and the list of qualified tokens.
	 */
	private void requireContextToken(){

		loader.setVisible(true);
		tokenServices.getServiceToken(new AsyncCallback<TokenBean>() {

			@Override
			public void onSuccess(TokenBean tokenBean) {

				if(tokenBean != null){

					// save information
					tokenContext = tokenBean;
					String username = tokenBean.getUsername();
					currentUsername.setText(username);

					// textbox hide content
					tokenHide.setText(tokenContext.getToken());
					tokenHide.setVisible(true);
					if(tokenBean.getContextName() != null && !tokenBean.getContextName().isEmpty())
						tokenHide.setTitle(MESSAGE_TOKEN_FOR_CONTEXT + tokenBean.getContextName());
					else 
						tokenHide.setTitle("");

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

					showToken.setIcon(IconType.EYE_OPEN);
					showToken.setTitle("Show token");
					showToken.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							showTokenHandlerBody(showToken, tokenHide, tokenClear);
						}
					});
					
					if(tokenBean.getContextName() != null){
						tokenClear.setTitle(MESSAGE_TOKEN_FOR_CONTEXT + tokenBean.getContextName());
						tokenHide.setTitle(MESSAGE_TOKEN_FOR_CONTEXT + tokenBean.getContextName());
					}else{
						tokenClear.setTitle("");
						tokenHide.setTitle("");
					}

					loader.setVisible(false);
					formTokenUsername.setVisible(true);
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
	 * Service is not available... show error
	 */
	private void showServiceUnavailableError(){

		loader.setVisible(false);
		formTokenUsername.setVisible(false);
		serviceUnavailablePanel.setVisible(true);

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
		username.setVisible(true);

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
		username.setVisible(false);

	}

}
