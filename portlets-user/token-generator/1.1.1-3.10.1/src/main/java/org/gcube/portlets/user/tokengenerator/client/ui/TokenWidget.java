package org.gcube.portlets.user.tokengenerator.client.ui;

import org.gcube.portlets.user.tokengenerator.client.TokenService;
import org.gcube.portlets.user.tokengenerator.client.TokenServiceAsync;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.AppendButton;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author Massimiliano Assante, ISTI-CNR
 * @author Costantino Perciante, ISTI-CNR
 */
public class TokenWidget extends Composite {

	public static final String DISPLAY_NAME =  "Service Auth. Token";
	public static final String ERROR_MESSAGE = "Sorry, it is not possible to contact the service!";

	// textbox id (hidden text)
	private final static String hiddenTextBoxId = "textBoxTokenId";

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

	@UiField Label header;
	@UiField LoadingText loader;
	@UiField HTMLPanel tokenPanel;
	@UiField Button generateButton;
	@UiField VerticalPanel placeHolder;
	@UiField Button infoButton;
	@UiField Popover popover;

	public TokenWidget() {

		super();
		initWidget(uiBinder.createAndBindUi(this));
		header.setText(DISPLAY_NAME);

		// change style to the info button
		infoButton.addStyleName("info-button");

		// change popover direction
		popover.setPlacement(Placement.BOTTOM);

		// change popover text header
		popover.setHeading(aboutTokenGeneratorHeader.getHTML());

		// change popover text content
		popover.setText(aboutTokenGeneratorBody.getHTML());

		// set the popover's text to be html
		popover.setHtml(true);

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

		tokenService.getServiceToken(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String code) {

				// hide the loader
				loader.setVisible(false);

				if(code != null){

					// save it
					token = code;

					AppendButton ap = new AppendButton();

					// textbox hide content
					final PasswordTextBox pwTextBox = new PasswordTextBox();
					pwTextBox.getElement().setId(hiddenTextBoxId);
					pwTextBox.setTitle("Your token for this VRE");
					pwTextBox.setReadOnly(true);
					pwTextBox.setText(token);
					pwTextBox.setVisible(true);
					pwTextBox.getElement().getStyle().setWidth(140, Unit.PX);
					ap.add(pwTextBox);

					final TextBox tb = new TextBox();
					tb.setTitle("Your token for this VRE");
					tb.setReadOnly(false);
					tb.setVisible(false);
					tb.getElement().getStyle().setWidth(140, Unit.PX);
					ap.add(tb);

					// determine if we can show the "copy to clipboard" button
					/*if(copyToClipBoardisSupported()){

						// copy button
						final Button copyButton = new Button("Copy", IconType.COPY);
						copyButton.getElement().getStyle().setWidth(50, Unit.PX);
						copyButton.setTitle("Copy to clipboard");

						// add copy handler
						copyButton.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {

								event.preventDefault();

								boolean copied = copyToClipboard(hiddenTextBoxId);

								// remove title
								copyButton.setTitle(null);

								// add tooltip for error/success messages
								final Tooltip tooltip = new Tooltip();
								tooltip.setWidget(copyButton);
								tooltip.setTrigger(Trigger.MANUAL);
								tooltip.setPlacement(Placement.TOP);

								if(copied){

									tooltip.setText("Copied to clipboard!");
									tooltip.reconfigure();

									Tooltip.changeVisibility(event.getRelativeElement(), VisibilityChange.TOGGLE.get());

								}
								else{

									tooltip.setText("Not copied to clipboard!");
									tooltip.reconfigure();
									Tooltip.changeVisibility(event.getRelativeElement(), VisibilityChange.TOGGLE.get());
								}

								// Hide the tooltip after a while ...
								Timer t = new Timer() {
									@Override
									public void run() {

										tooltip.hide();
										copyButton.setTitle("Copy to clipboard");
									}
								};

								// Schedule the timer to run once in 3 seconds.
								t.schedule(3000);
							}
						});

						ap.add(copyButton);
					}
					else{*/

					// add show button
					final Button showButton = new Button("Show", IconType.EYE_OPEN);
					showButton.getElement().getStyle().setWidth(50, Unit.PX);
					showButton.setTitle("Show token");

					// add copy handler
					showButton.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {

							showTokenHandlerBody(showButton, pwTextBox, tb);

						}

					});

					ap.add(showButton);

					//}

					placeHolder.clear();
					placeHolder.add(ap);

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
	 * Copy to clipboard function
	 */
	private static native boolean copyToClipboard(String elementId)/*-{

		try{
			// retrieve textArea by id
			var textArea = $wnd.$('#'+ elementId)[0];	

			// copy the text to a temporary area and then delete it
			var temp = $wnd.$("<input>")
			$wnd.$("body").append(temp)
			temp.focus();
			temp.val($wnd.$(textArea).val()).select();
			var res = document.execCommand('copy');
			temp.remove();
			return res;
		}catch(err){
			return false;
		}

	}-*/;

	/**
	 * Check if copy to clipboard is supported.
	 * @return
	 */
	private static native boolean copyToClipBoardisSupported()/*-{

		try{
			var supported = document.queryCommandSupported('copy');
			console.log("Is copying to clipboard supported? " + supported);
			return supported;
		}catch(err){

		}
		return false;

	}-*/;

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
