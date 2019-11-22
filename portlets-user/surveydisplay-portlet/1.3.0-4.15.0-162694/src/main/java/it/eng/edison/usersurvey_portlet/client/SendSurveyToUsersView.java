package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Typeahead;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class SendSurveyToUsersView.
 */
public class SendSurveyToUsersView extends Composite {

	/** The ui binder. */
	private static SendSurveyToUsersUiBinder uiBinder = GWT.create(SendSurveyToUsersUiBinder.class);
	
	/** The greeting service. */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/**
	 * The Interface SendSurveyToUsersUiBinder.
	 */
	interface SendSurveyToUsersUiBinder extends UiBinder<Widget, SendSurveyToUsersView> {
	}

	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;
	
	/** The well form manage. */
	@UiField WellForm wellFormManage;
	
	/** The title survey heading. */
	@UiField Heading inviteSurveyHeading, titleSurveyHeading;
	
	/** The remove all members. */
	@UiField Button backToHomeSurveyButton, sendSurveyButton, addUserToSendSurveyButton, addAllMembers, removeAllMembers;
	
	/** The invite users to survey typeahead. */
	@UiField Typeahead inviteUsersToSurveyTypeahead;
	
	/** The invite users to survey text box. */
	@UiField TextBox inviteUsersToSurveyTextBox;
	
	/** The to label. */
	@UiField Label toLabel;
	
/** The flex table general. */
	private FlexTable addRemoveUsersInviteListFlexTable, sendSurveyAndInvitedUsersFlexTable, flexTableGeneral; 
	
	/** The users invite survey list. */
	private List<String> usersInviteSurveyList;
	
	/** The row. */
	private int row = 0;
	
	/** The user DTO. */
	private UserDTO userDTO;
	
	/** The oracle. */
	private MultiWordSuggestOracle oracle = null;
	
	/** The send survey to users map. */
	private HashMap<String, String> sendSurveyToUsersMap = null;
	
	/** The id survey selected. */
	private int idSurveySelected;
	
	/** The title survey. */
	private String titleSurvey;
	
	/** The user list current company. */
	private List<String> userListCurrentCompany;
	
	/** The current URL. */
	private String currentURL;
	
	/** The all VRE members map. */
	private HashMap<String, String> allVREMembersMap;
	
	/** The is anonymous. */
	private boolean isAnonymous;
	
	/** The survey sender. */
	private String userInviteSurvey, surveySender;
	
	/** The user invite to fill survey. */
	private Map<String,String> userInviteToFillSurvey;
	
	/** The user invite to fill survey list. */
	private List<String> userInviteToFillSurveyList;
    
    /** The html title column. */
    private HTML htmlTitleColumn;
    
    /** The paragraph usr. */
    private Paragraph paragraphUsr;
	
	/**
	 * Instantiates a new send survey to users view.
	 */
	public SendSurveyToUsersView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	/**
	 * Instantiates a new send survey to users view.
	 *
	 * @param idSurveySelected the id survey selected
	 * @param titleSurvay the title survay
	 * @param isAnonymous the is anonymous
	 * @param surveySender the survey sender
	 */
	public SendSurveyToUsersView(int idSurveySelected, String titleSurvay, boolean isAnonymous, String surveySender) {
		this();
		this.idSurveySelected = idSurveySelected;
		this.isAnonymous = isAnonymous;
		this.currentURL = Window.Location.getHref();
		this.titleSurvey = titleSurvay;
		this.surveySender = surveySender;
		
		toLabel.addStyleName("toLabelSendSurveyToUsers");
		addUserToSendSurveyButton.addStyleName("addUserToSendSurveyButtonSendSurveyToUsers");
		inviteUsersToSurveyTextBox.addStyleName("inviteUsersToSurveyTextBox");
		addAllMembers.addStyleName("allMembersAddRemoveButton");
		removeAllMembers.addStyleName("allMembersAddRemoveButton");
		
		addAllMembers.setVisible(true);
		removeAllMembers.setVisible(false);
		
		userDTO = new UserDTO();
		sendSurveyToUsersMap = new HashMap<String, String>();
		
		titleSurveyHeading.setText("\"" + titleSurvey + "\"");
		usersInviteSurveyList = new ArrayList<>();
		addRemoveUsersInviteListFlexTable = new FlexTable();
		sendSurveyAndInvitedUsersFlexTable = new FlexTable();
		flexTableGeneral = new FlexTable();
		flexTableGeneral.addStyleName("SurveyStyling");

		flexTableGeneral.setWidget(0, 0, verticalPanel);
		flexTableGeneral.setWidget(0, 1, sendSurveyAndInvitedUsersFlexTable);
		RootPanel.get("displaysurvey-div").add(flexTableGeneral);
		drowInvitedUsersToSurvey(idSurveySelected);
		oracle = (MultiWordSuggestOracle) inviteUsersToSurveyTypeahead
                .getSuggestOracle();
 		
		  greetingService.getUserListCurrentCompany(idSurveySelected, new AsyncCallback<UserDTO>() {
			@Override
			public void onSuccess(UserDTO result) {
				userDTO = result;
				setUserDTO(result);
				allVREMembersMap = new HashMap<String, String>(userDTO.getListUserMap());
				userListCurrentCompany = new ArrayList<String>(userDTO.getListUserMap().keySet());
		        oracle.addAll(userListCurrentCompany); 
			}
			 
			
			@Override
			public void onFailure(Throwable caught) {
				
			}

		});
	}
	
	/**
	 * On click back to home survey button.
	 *
	 * @param event the event
	 */
	@UiHandler("backToHomeSurveyButton")
	void onClickBackToHomeSurveyButton(ClickEvent event){
		backToHomepage();
	}
	
	/**
	 * On click send survey button.
	 *
	 * @param event the event
	 */
	@UiHandler("sendSurveyButton")
	void onClickSendSurveyButton(ClickEvent event){
		if(getUsersInviteSurveyList().isEmpty() || getUsersInviteSurveyList() == null){
			Window.alert("Please add users to invite list");
			return;
		}
		greetingService.sendSurveyToUsers(getCurrentURL(), getIdSurveySelected(), isAnonymous(), getSurveySender(), getUsersInviteSurveyList(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				Window.Location.reload();
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	/**
	 * On click add user to send survey button.
	 *
	 * @param event the event
	 */
	@UiHandler("addUserToSendSurveyButton")
	void onClickAddUserToSendSurveyButton (ClickEvent event){
		if(inviteUsersToSurveyTextBox.getValue().isEmpty() || inviteUsersToSurveyTextBox.getValue() == null){
			Window.alert("Please search an user");
			return;
		}

		Button removeButton = new Button();
		removeButton.setIcon(IconType.MINUS_SIGN_ALT);
		removeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int rowIndex = addRemoveUsersInviteListFlexTable.getCellForEvent(event).getRowIndex();
				usersInviteSurveyList.remove(rowIndex);
				addRemoveUsersInviteListFlexTable.removeRow(rowIndex);
				if ( getRow()>0){
					setRow(row-1);
				}
			}
		});

		userInviteSurvey = inviteUsersToSurveyTextBox.getValue();
		addRemoveUsersInviteListFlexTable.setText(row, 0, userInviteSurvey);
		addRemoveUsersInviteListFlexTable.setWidget(row, 1, removeButton);
		this.setRow(row+1);

		verticalPanel.add(addRemoveUsersInviteListFlexTable);
		usersInviteSurveyList.add(userInviteSurvey);
		inviteUsersToSurveyTextBox.setText("");
	}
	
	/**
	 * On click add all members.
	 *
	 * @param event the event
	 */
	@UiHandler("addAllMembers")
	void onClickAddAllMembers(ClickEvent event){
		removeAllMembers.setVisible(true);
		addAllMembers.setVisible(false);
		for(int i=0; i<allVREMembersMap.size(); i++){
			String userInviteSurvey;
			Button removeButton = new Button();
			removeButton.setIcon(IconType.MINUS_SIGN_ALT);
			removeButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					int rowIndex = addRemoveUsersInviteListFlexTable.getCellForEvent(event).getRowIndex();
					getUsersInviteSurveyList().remove(rowIndex);
					addRemoveUsersInviteListFlexTable.removeRow(rowIndex);
					if ( getRow()>0){
						setRow(row-1);
					}
				}
			});
			
			userInviteSurvey = userListCurrentCompany.get(i);
			addRemoveUsersInviteListFlexTable.setText(row, 0, userInviteSurvey);
			addRemoveUsersInviteListFlexTable.setWidget(row, 1, removeButton);
			this.setRow(row+1);
			
			verticalPanel.add(addRemoveUsersInviteListFlexTable);
			this.getUsersInviteSurveyList().add(userInviteSurvey);
			inviteUsersToSurveyTextBox.setText("");
		}
	}
	
	/**
	 * On click remove all members.
	 *
	 * @param event the event
	 */
	@UiHandler("removeAllMembers")
	void onClickRemoveAllMembers(ClickEvent event){
		addAllMembers.setVisible(true);
		removeAllMembers.setVisible(false);
		if(addRemoveUsersInviteListFlexTable != null || !getUsersInviteSurveyList().isEmpty()){
			addRemoveUsersInviteListFlexTable.removeAllRows();
			addRemoveUsersInviteListFlexTable.clear();
			this.getUsersInviteSurveyList().clear();
			this.setRow(0);
		}
	}
	
	/**
	 * Drow invited users to survey.
	 *
	 * @param idSurvey the id survey
	 */
	private void drowInvitedUsersToSurvey(int idSurvey){
		userInviteToFillSurvey = new HashMap<>();
		userInviteToFillSurveyList = new ArrayList<>();
		htmlTitleColumn = new HTML("<h4>Users already invited to answer this questionnaire</h4>", true);
		sendSurveyAndInvitedUsersFlexTable.setWidget(0, 0, htmlTitleColumn);
		htmlTitleColumn.addStyleName("htmlTitleInvitedToFillSurvey");

		greetingService.getUsersInvitedToFillSurvey(idSurvey, new AsyncCallback<Map<String,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				userInviteToFillSurvey = result;
				userInviteToFillSurveyList.addAll(userInviteToFillSurvey.values());
				paragraphUsr = new Paragraph();
				paragraphUsr.addStyleName("data");
				sendSurveyAndInvitedUsersFlexTable.getCellFormatter().addStyleName(0, 0, "sendSurveyAndInvitedUsersTitle");

				if(userInviteToFillSurveyList != null && !userInviteToFillSurveyList.isEmpty()){
					for(int i = 0; i<userInviteToFillSurveyList.size(); i++){
						Paragraph paragraphUsr = new Paragraph(userInviteToFillSurveyList.get(i));
						paragraphUsr.addStyleName("data");
						sendSurveyAndInvitedUsersFlexTable.setWidget(i+1, 0, paragraphUsr);
						if((i % 2) == 0){
							sendSurveyAndInvitedUsersFlexTable.getCellFormatter().addStyleName(i+1, 0, "sendSurveyAndInvitedUsersEvenRows");
						} else {
							sendSurveyAndInvitedUsersFlexTable.getCellFormatter().addStyleName(i+1, 0, "sendSurveyAndInvitedUsersOddRows");
						}
					}
				}
			}
		});
	}
	
	/**
	 * Back to homepage.
	 */
	private void backToHomepage(){
		  RootPanel.get("displaysurvey-div").clear();
		  SurveyStart surveyStart = new SurveyStart();
		  RootPanel.get("displaysurvey-div").add(surveyStart);
	}

	/**
	 * Gets the flex table.
	 *
	 * @return the flex table
	 */
	public FlexTable getFlexTable() {
		return addRemoveUsersInviteListFlexTable;
	}

	/**
	 * Sets the flex table.
	 *
	 * @param flexTable the new flex table
	 */
	public void setFlexTable(FlexTable flexTable) {
		this.addRemoveUsersInviteListFlexTable = flexTable;
	}

	/**
	 * Gets the users invite survey list.
	 *
	 * @return the users invite survey list
	 */
	public List<String> getUsersInviteSurveyList() {
		return usersInviteSurveyList;
	}

	/**
	 * Sets the users invite survey list.
	 *
	 * @param usersInviteSurveyList the new users invite survey list
	 */
	public void setUsersInviteSurveyList(List<String> usersInviteSurveyList) {
		this.usersInviteSurveyList = usersInviteSurveyList;
	}

	/**
	 * Gets the row.
	 *
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Sets the row.
	 *
	 * @param row the new row
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * Gets the send survey to users map.
	 *
	 * @return the send survey to users map
	 */
	public HashMap getSendSurveyToUsersMap() {
		return sendSurveyToUsersMap;
	}

	/**
	 * Sets the send survey to users map.
	 *
	 * @param sendSurveyToUsersMap the new send survey to users map
	 */
	public void setSendSurveyToUsersMap(HashMap sendSurveyToUsersMap) {
		this.sendSurveyToUsersMap = sendSurveyToUsersMap;
	}

	/**
	 * Gets the id survey selected.
	 *
	 * @return the id survey selected
	 */
	public int getIdSurveySelected() {
		return idSurveySelected;
	}

	/**
	 * Sets the id survey selected.
	 *
	 * @param idSurveySelected the new id survey selected
	 */
	public void setIdSurveySelected(int idSurveySelected) {
		this.idSurveySelected = idSurveySelected;
	}

	/**
	 * Gets the current URL.
	 *
	 * @return the current URL
	 */
	public String getCurrentURL() {
		return currentURL;
	}

	/**
	 * Sets the current URL.
	 *
	 * @param currentURL the new current URL
	 */
	public void setCurrentURL(String currentURL) {
		this.currentURL = currentURL;
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

	/**
	 * Gets the all VRE members map.
	 *
	 * @return the all VRE members map
	 */
	public HashMap getallVREMembersMap() {
		return allVREMembersMap;
	}

	/**
	 * Sets the all VRE users map.
	 *
	 * @param allVREMembersMap the new all VRE users map
	 */
	public void setAllVREUsersMap(HashMap allVREMembersMap) {
		this.allVREMembersMap = allVREMembersMap;
	}

	/**
	 * Checks if is anonymous.
	 *
	 * @return true, if is anonymous
	 */
	public boolean isAnonymous() {
		return isAnonymous;
	}

	/**
	 * Sets the anonymous.
	 *
	 * @param isAnonymous the new anonymous
	 */
	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	/**
	 * Gets the user invite to fill survey.
	 *
	 * @return the user invite to fill survey
	 */
	public Map<String, String> getUserInviteToFillSurvey() {
		return userInviteToFillSurvey;
	}

	/**
	 * Sets the user invite to fill survey.
	 *
	 * @param userInviteToFillSurvey the user invite to fill survey
	 */
	public void setUserInviteToFillSurvey(Map<String, String> userInviteToFillSurvey) {
		this.userInviteToFillSurvey = userInviteToFillSurvey;
	}

	/**
	 * Gets the survey sender.
	 *
	 * @return the survey sender
	 */
	public String getSurveySender() {
		return surveySender;
	}

	/**
	 * Sets the survey sender.
	 *
	 * @param surveySender the new survey sender
	 */
	public void setSurveySender(String surveySender) {
		this.surveySender = surveySender;
	}

}
