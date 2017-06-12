package it.eng.edison.usersurvey_portlet.client;

import com.github.gwtbootstrap.client.ui.Heading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class SendAnswerSuccessView.
 */
public class SendAnswerSuccessView extends Composite {

	/** The ui binder. */
	private static SendAnswerSuccessViewUiBinder uiBinder = GWT.create(SendAnswerSuccessViewUiBinder.class);

	/**
	 * The Interface SendAnswerSuccessViewUiBinder.
	 */
	interface SendAnswerSuccessViewUiBinder extends UiBinder<Widget, SendAnswerSuccessView> {
	}

	/** The send ans success. */
	@UiField Heading sendAnsSuccess;
	
	/** The user DTO. */
	private UserDTO userDTO;
	
	/**
	 * Instantiates a new send answer success view.
	 */
	public SendAnswerSuccessView(){
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	
	/**
	 * Instantiates a new send answer success view.
	 *
	 * @param userDTO the user DTO
	 */
	public SendAnswerSuccessView(UserDTO userDTO) {
		initWidget(uiBinder.createAndBindUi(this));
		this.userDTO = userDTO;
		
		RootPanel.get("displaysurvey-div").add(sendAnsSuccess);
	}
	
	/**
	 * Gets the user DTO.
	 *
	 * @return the user DTO
	 */
	public UserDTO getUserDTO() {
		return userDTO;
	}


	/**
	 * Sets the user DTO.
	 *
	 * @param userDTO the new user DTO
	 */
	public void setUserDTO(UserDTO userDTO) {
		this.userDTO = userDTO;
	}

}
