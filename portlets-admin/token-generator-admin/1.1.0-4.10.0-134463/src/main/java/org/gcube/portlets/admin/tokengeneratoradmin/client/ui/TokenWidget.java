package org.gcube.portlets.admin.tokengeneratoradmin.client.ui;


import java.util.List;

import org.gcube.portlets.admin.tokengeneratoradmin.client.TokenServiceAdmin;
import org.gcube.portlets.admin.tokengeneratoradmin.client.TokenServiceAdminAsync;
import org.gcube.portlets.admin.tokengeneratoradmin.shared.NodeToken;
import org.gcube.portlets.admin.tokengeneratoradmin.shared.PortRange;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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

	// info to show for popover
	private final static HTML aboutTokenGeneratorHeader = new HTML("<span style=\"font-size:14px; font-weight:bold\">"+"About this Token"+"</span>");
	private final static HTML aboutTokenGeneratorBody = new HTML("<p>"+"This Token is needed to activate a specific node in the Infrastructure."+"</p>");
	private static final String MISSING_NODE_ADDRESS = "Node address is missing!";
	private static final String FAILED_TO_CREATE_TOKEN = "Node Token creation failed";
	protected static final String TOKEN_CREATED = "Node Token created";
	private static final String REQUESTING_NEW_TOKEN = "Requesting Token...";
	private static final String INVALID_PORT_NUMBER = "Please insert a valid port number! ";

	private PortRange portRange;

	private static InviteWidgetUiBinder uiBinder = GWT.create(InviteWidgetUiBinder.class);
	interface InviteWidgetUiBinder extends UiBinder<Widget, TokenWidget> {}
	private final TokenServiceAdminAsync tokenServices = GWT.create(TokenServiceAdmin.class);

	@UiField 
	LoadingText loader;
	@UiField
	VerticalPanel serviceUnavailablePanel;
	@UiField 
	HTMLPanel tokenPanel;
	@UiField 
	Button generateButton;
	@UiField 
	Button infoButton;
	@UiField
	Popover popover;
	@UiField
	Form formTokenUsername;
	@UiField
	AlertBlock alertArea;
	@UiField
	TextBox nodeAddress;
	@UiField
	TextBox port;
	@UiField
	TextBox token;
	@UiField
	ControlGroup generatedTokenGroup;
	@UiField
	ListBox contexts;

	public TokenWidget() {

		super();
		initWidget(uiBinder.createAndBindUi(this));

		infoButton.addStyleName("token-info-button");

		popover.setPlacement(Placement.BOTTOM);
		popover.setHeading(aboutTokenGeneratorHeader.getHTML());
		popover.setText(aboutTokenGeneratorBody.getHTML());
		popover.setHtml(true);

		generateButton.addStyleName("generate-button-style");

		// automatically require the current user's username
		requireInfo();		
	}

	/**
	 * Used to require the token for this context and the list of qualified tokens.
	 */
	private void requireInfo(){

		loader.setVisible(true);
		formTokenUsername.setVisible(false);

		// require port range
		tokenServices.getRange(new AsyncCallback<PortRange>() {

			@Override
			public void onSuccess(PortRange result) {

				portRange = result;
				port.setPlaceholder("Insert a port number [" + portRange.getStart() + ", " +  portRange.getEnd() + "]");
				port.setTitle("Insert a port number [" + portRange.getStart() + ", " +  portRange.getEnd() + "]");

				tokenServices.retrieveListContexts(new AsyncCallback<List<String>>() {

					@Override
					public void onSuccess(List<String> result) {

						if(result != null && !result.isEmpty()){

							loader.setVisible(false);
							formTokenUsername.setVisible(true);
							contexts.clear();

							for (String context : result) {
								contexts.addItem(context);
							}

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

	@UiHandler("generateButton")
	public void onGenerateClick(ClickEvent ce) {

		// freeze values
		nodeAddress.setEnabled(false);
		port.setEnabled(false);
		generateButton.setEnabled(false);
		generatedTokenGroup.setVisible(false);

		// try to parse the port number
		short portNumber = -1;
		try{

			portNumber = Short.parseShort(port.getText().trim());

			if(portNumber < portRange.getStart() || portNumber > portRange.getEnd())
				throw new Exception("Invalid port number");

		}catch(Exception e){
			GWT.log("Invalid port number", e);
			portNumber = -1;
		}

		if(nodeAddress.getText() == null || nodeAddress.getText().trim().isEmpty()){

			showInfo(MISSING_NODE_ADDRESS, AlertType.WARNING);
			nodeAddress.setEnabled(true);
			port.setEnabled(true);
			generateButton.setEnabled(true);

		}else if(portNumber == -1){

			showInfo(INVALID_PORT_NUMBER + "(this value must be into the interval [" + portRange.getStart() + ", " +  portRange.getEnd() + "])", AlertType.WARNING);
			nodeAddress.setEnabled(true);
			port.setEnabled(true);
			generateButton.setEnabled(true);

		}else if(contexts.getSelectedIndex() == -1){
			showInfo("Please select a valid context", AlertType.WARNING);
			nodeAddress.setEnabled(true);
			port.setEnabled(true);
			generateButton.setEnabled(true);
		}
		else{

			// Else ok...
			showInfo(REQUESTING_NEW_TOKEN, AlertType.INFO);
			tokenServices.createNodeToken(nodeAddress.getText().trim(), portNumber, contexts.getSelectedItemText(),
					new AsyncCallback<NodeToken>() {

				@Override
				public void onSuccess(NodeToken result) {

					if(result != null){

						if(result.getError() != null){

							showInfo(result.getError(), AlertType.ERROR);

						}else{
							token.setText(result.getToken());
							token.setTitle("Generated Token for node " + result.getNodeIp() + ", port " + result.getPort() + " and context " + result.getContext());
							generatedTokenGroup.setVisible(true);
							showInfo(TOKEN_CREATED, AlertType.SUCCESS);
						}

					}else{

						showInfo(FAILED_TO_CREATE_TOKEN, AlertType.ERROR);

					}

					nodeAddress.setEnabled(true);
					port.setEnabled(true);
					generateButton.setEnabled(true);

				}

				@Override
				public void onFailure(Throwable caught) {

					showInfo(FAILED_TO_CREATE_TOKEN, AlertType.ERROR);
					nodeAddress.setEnabled(true);
					port.setEnabled(true);
					generateButton.setEnabled(true);

				}
			});
		}
	}
}
