package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.datepicker.client.ui.DateBoxAppended;
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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;

/**
 * The Class ManageSurveyView.
 */
public class ManageSurveyView extends Composite {

	/** The ui binder. */
	private static ManageSurveyViewUiBinder uiBinder = GWT.create(ManageSurveyViewUiBinder.class);
	
	/** The greeting service. */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/**
	 * The Interface ManageSurveyViewUiBinder.
	 */
	interface ManageSurveyViewUiBinder extends UiBinder<Widget, ManageSurveyView> {
	}

	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;
	
	/** The modify question survey button. */
	@UiField Button saveQuestionSurveyButton, backToHomeSurveyButton, modifyQuestionSurveyButton;
	
	/** The modify question survey button bottom. */
	@UiField Button addQuestionSurveyButtonBottom, saveQuestionSurveyButtonBottom, modifyQuestionSurveyButtonBottom;
	
	/** The check box anonymous. */
	@UiField CheckBox checkBoxAnonymous;
	
	/** The well form manage bottom. */
	@UiField WellForm wellFormManage, wellFormManageBottom;
	
	/** The create survey heading bottom. */
	@UiField Heading modifySurveyHeading, modifySurveyHeadingBottom, createSurveyHeadingBottom;
	
	/** The date expired survey. */
	@UiField DateBoxAppended dateExpiredSurvey;
	
	/** The cant modify survey. */
	@UiField Paragraph cantModifySurvey;
	
	/** The id survey. */
	private int idSurvey;
	
	/** The survey question model list. */
	private List<SurveyQuestionModel> surveyQuestionModelList = new ArrayList<>(); 
	
	/** The id question. */
	private Integer idQuestion;
	
	/** The title survey. */
	private String titleSurvey;
	
	/** The flex table. */
	private FlexTable flexTable; 
	
	/** The row. */
	private int row;
	
	/** The survey model. */
	private SurveyModel surveyModel = null;
	
	/** The user DTO. */
	private UserDTO userDTO = null;
	
	/** The is anonymous. */
	private boolean isAnonymous; 
	
	/** The id user. */
	private long idUser;
	
	/** The id survey temp. */
	private int idSurveyTemp = 0;
	
	/** The expired survey date. */
	private Date expiredSurveyDate;
    
    /** The question and remove button. */
    private WellForm questionAndRemoveButton;
    
    /** The multiple choice options alert. */
    private boolean multipleChoiceOptionsAlert;
    
    /** The row grid alert. */
    private boolean rowGridAlert;
    
    /** The column grid alert. */
    private boolean columnGridAlert;
    
    /** The id tmp folder. */
    private long idTmpFolder = 0;
    
    /** The question survey model. */
    private SurveyQuestionModel modifySurveyQuestionModelTemp; 
	
	/** The create title survey view. */
	CreateTitleSurveyView createTitleSurveyView = null;
	
	/** The create survey view. */
	CreateQuestionView createSurveyView = null;
	
	/** The survey question model. */
	SurveyQuestionModel surveyQuestionModel;
	
	/** The survey home page. */
	SurveyHomePage surveyHomePage = null;
    
    /** The list survey question. */
	private  List<CreateQuestionView> listSurveyQuestion = null;
    
    /** The list modify survey question model. */
    List<SurveyQuestionModel> listModifySurveyQuestionModel = null;
    
    /** The survey model list. */
    List<SurveyModel> surveyModelList = null;
    
    /**
     * Instantiates a new manage survey view.
     */
    /* Create View */
	public ManageSurveyView() {
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get("survey-div").add(verticalPanel);

		createFolderTmp();
		
		modifySurveyHeading.setVisible(false);
		modifySurveyHeadingBottom.setVisible(false);
		createSurveyHeadingBottom.setVisible(false);
		addQuestionSurveyButtonBottom.setVisible(true);
		saveQuestionSurveyButton.setVisible(true);
		saveQuestionSurveyButton.addStyleName("saveQuestionSurveyButtonManageSurveyView");
		saveQuestionSurveyButtonBottom.setVisible(true);
		saveQuestionSurveyButtonBottom.addStyleName("saveQuestionSurveyButtonManageSurveyView");
		modifyQuestionSurveyButton.setVisible(false);
		modifyQuestionSurveyButton.addStyleName("saveQuestionSurveyButtonManageSurveyView");
		modifyQuestionSurveyButtonBottom.setVisible(false);
		modifyQuestionSurveyButtonBottom.addStyleName("saveQuestionSurveyButtonManageSurveyView");
		verticalPanel.add(wellFormManage);
		
		expiredSurveyDate = new Date();
		CalendarUtil.addMonthsToDate(expiredSurveyDate, 1);
		dateExpiredSurvey.setValue(expiredSurveyDate);
		
		idQuestion = 0;
		row = 0;
		idUser = 0;
		this.multipleChoiceOptionsAlert = Boolean.FALSE;
		this.rowGridAlert = Boolean.FALSE;
		this.columnGridAlert = Boolean.FALSE;
		listSurveyQuestion = new ArrayList<CreateQuestionView>();
		surveyModel = new SurveyModel();
		userDTO = new UserDTO();
		questionAndRemoveButton = new WellForm();
		questionAndRemoveButton.addStyleName("questionAndRemoveButton");
		questionAndRemoveButton.setVisible(true);
		getUserFromSession();
		flexTable = new FlexTable();
		createTitleSurveyView = new CreateTitleSurveyView();
		verticalPanel.add(createTitleSurveyView);
		AddQuestionSurvey();
	}
	
	/**
	 * Instantiates a new manage survey view.
	 *
	 * @param idSurveySelected the id survey selected
	 * @param titleSurvey the title survey
	 * @param isAnonymous the is anonymous
	 * @param expiredSurveyDate the expired survey date
	 * @param numberOfMembersFilledSurvey the number of members filled survey
	 * @param listModifySurveyQuestionModel the list modify survey question model
	 * @param userDTO the user DTO
	 */
	/* Modify View */
	public ManageSurveyView(int idSurveySelected, String titleSurvey, boolean isAnonymous, Date expiredSurveyDate, int numberOfMembersFilledSurvey, List<SurveyQuestionModel> listModifySurveyQuestionModel, UserDTO userDTO){
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get("survey-div").add(verticalPanel);
		
		modifySurveyHeading.setVisible(true);
		modifySurveyHeadingBottom.setVisible(false);
		createSurveyHeadingBottom.setVisible(false);
		addQuestionSurveyButtonBottom.setVisible(true);
		saveQuestionSurveyButton.setVisible(false);
		saveQuestionSurveyButtonBottom.setVisible(false);
		modifyQuestionSurveyButton.setVisible(true);
		modifyQuestionSurveyButton.addStyleName("saveQuestionSurveyButtonManageSurveyView");
		modifyQuestionSurveyButtonBottom.setVisible(true);
		modifyQuestionSurveyButtonBottom.addStyleName("saveQuestionSurveyButtonManageSurveyView");
		cantModifySurvey.setVisible(false);
		
		if(numberOfMembersFilledSurvey > 0){
			wellFormManageBottom.setVisible(false);
			modifyQuestionSurveyButton.setVisible(false);
			modifyQuestionSurveyButtonBottom.setVisible(false);
			addQuestionSurveyButtonBottom.setVisible(false);
			cantModifySurvey.setVisible(true);
			cantModifySurvey.addStyleName("descriptionAggregateStatsParagraph");
			if(numberOfMembersFilledSurvey == 1){
				cantModifySurvey.setText("You can't modify survey. " + numberOfMembersFilledSurvey + " VRE member have filled it");
			} else {
				cantModifySurvey.setText("You can't modify survey. " + numberOfMembersFilledSurvey + " VRE members have filled it");
			}
		}

		verticalPanel.add(wellFormManage);
		
		idQuestion = 0;
		row = 0;
		idUser = 0;
		checkBoxAnonymous.setValue(isAnonymous);
		
		this.multipleChoiceOptionsAlert = Boolean.FALSE;
		this.rowGridAlert = Boolean.FALSE;
		this.columnGridAlert = Boolean.FALSE;
		this.expiredSurveyDate = expiredSurveyDate;
		dateExpiredSurvey.setValue(expiredSurveyDate);
		
 	    listSurveyQuestion = new ArrayList<CreateQuestionView>();
     	
		surveyModel = new SurveyModel();
		this.userDTO = userDTO;
		surveyModel.setIdsurvey(idSurveySelected);
		questionAndRemoveButton = new WellForm();
		questionAndRemoveButton.setVisible(true);
		flexTable = new FlexTable();
		
		setListModifySurveyQuestionModel(listModifySurveyQuestionModel);
		createTitleSurveyView = new CreateTitleSurveyView();
		createTitleSurveyView.titleSurveyTextBox.setText(titleSurvey);
	
		
		FlexTable questions = addQuestionSurveyToPanel(listModifySurveyQuestionModel);
		
		questionAndRemoveButton.add(questions);
		verticalPanel.add(createTitleSurveyView);
		verticalPanel.add(questionAndRemoveButton);
		verticalPanel.add(wellFormManageBottom);
	}
	
	/**
	 * Creates the folder tmp.
	 */
	private void createFolderTmp() {
		greetingService.createFolder(new AsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Long result) {
				setIdTmpFolder(result);
			}});	
	}

	/**
	 * On click add question survey bottom.
	 *
	 * @param event the event
	 */
	@UiHandler("addQuestionSurveyButtonBottom")
	void onClickAddQuestionSurveyBottom(ClickEvent event) {
		AddQuestionSurvey();
	}

	/**
	 * On click save question survey.
	 *
	 * @param event the event
	 */
	@UiHandler("saveQuestionSurveyButton")
	void onClickSaveQuestionSurvey(ClickEvent event){
		saveQuestionSurveyButton();
		
	}
	
	/**
	 * On click save question survey bottom.
	 *
	 * @param event the event
	 */
	@UiHandler("saveQuestionSurveyButtonBottom")
	void onClickSaveQuestionSurveyBottom(ClickEvent event){
		saveQuestionSurveyButton();
		
	}

	/**
	 * On click modify question survey button.
	 *
	 * @param event the event
	 */
	@UiHandler("modifyQuestionSurveyButton")
	void onClickModifyQuestionSurveyButton(ClickEvent event){
		modifyQuestionSurveyButton();
	}
	
	/**
	 * On click modify question survey button bottom.
	 *
	 * @param event the event
	 */
	@UiHandler("modifyQuestionSurveyButtonBottom")
	void onClickModifyQuestionSurveyButtonBottom(ClickEvent event){
		modifyQuestionSurveyButton();
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
	 * On click check box anonymous.
	 *
	 * @param event the event
	 */
	@UiHandler("checkBoxAnonymous")
	void onClickCheckBoxAnonymous(ClickEvent event){
		
	}
	
	
	/**
	 * Adds the question survey.
	 */
	private void AddQuestionSurvey() {
		
		questionAndRemoveButton.setVisible(true);
		setIdSurveyTemp(idSurveyTemp);
		
		//new box question
		final CreateQuestionView createSurveyView = new CreateQuestionView(getIdSurveyTemp());
		Button removeButton = new Button();
		removeButton.setIcon(IconType.TRASH);
		removeButton.addStyleName("removeButtonManageSurveyView");
		removeButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
				if (Window.confirm("Are you sure you want to delete this Question?")){
					int rowIndex = flexTable.getCellForEvent(event).getRowIndex();
					getListSurveyQuestion().remove(rowIndex);
					flexTable.removeRow(rowIndex);
				
					if ( getRow()>0){
						setRow(row-1);
						createSurveyView.setIdQuestion(getRow());
					}
					if(getRow() == 0){
						questionAndRemoveButton.setVisible(false);
					}
				}
			}

		});
		flexTable.setWidget(row, 0, createSurveyView);
		flexTable.setWidget(row, 1, removeButton);
		this.setRow(row+1);
		createSurveyView.setIdQuestion(getRow());
		questionAndRemoveButton.add(flexTable);
		verticalPanel.add(questionAndRemoveButton);
		verticalPanel.add(wellFormManageBottom);
		RootPanel.get("survey-div").add(verticalPanel);
		this.getListSurveyQuestion().add(createSurveyView);
	}
	
	 
	/**
	 * Modify question survey button.
	 */
	private void modifyQuestionSurveyButton() {
		setTitleSurvey(createTitleSurveyView.getTitleSurveyTextBox().getValue());

		for(int i=0; i<getListModifySurveyQuestionModel().size(); i++){
			if((getListModifySurveyQuestionModel() != null) &&
					(getListModifySurveyQuestionModel().get(i).getFolderIdImage() != null ||
					getListModifySurveyQuestionModel().get(i).getFolderIdImage() != 0)){
				setIdTmpFolder(getListModifySurveyQuestionModel().get(i).getFolderIdImage());
			} 
		}

		if(getTitleSurvey().isEmpty()){
			Window.alert("Please insert a title");
			return;
		}else {

			addSurvey2List();

			if(isMultipleChoiceOptionsAlert()){
				Window.alert("Please insert at least one option in multiple choices questions");
				surveyQuestionModelList.clear();
				setMultipleChoiceOptionsAlert(Boolean.FALSE);
				return;
			}
			if(isRowGridAlert() || isColumnGridAlert()){
				Window.alert("Please insert at least one row and one column in grid question");
				surveyQuestionModelList.clear();
				setRowGridAlert(Boolean.FALSE);
				setColumnGridAlert(Boolean.FALSE);
				return;
			}
			greetingService.updateSurvey(idUser, surveyModel, surveyQuestionModelList, new AsyncCallback<Void>(){

				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(Void result) {
					surveyQuestionModelList = null;
					surveyModel = null;
					backToHomepage();

				}});
		}
	}
	
	
	/**
	 * Save all questions to DB.
	 */
	private void saveQuestionSurveyButton() {
		
		setTitleSurvey(createTitleSurveyView.getTitleSurveyTextBox().getValue());
		if(getTitleSurvey().isEmpty()){
			Window.alert("Please insert a title");
			return;
		}else {
		 
			addSurvey2List();
			
			if(isMultipleChoiceOptionsAlert()){
				Window.alert("Please insert at least one option in multiple choices questions");
				surveyQuestionModelList.clear();
				setMultipleChoiceOptionsAlert(Boolean.FALSE);
				return;
			}
			if(isRowGridAlert() || isColumnGridAlert()){
				Window.alert("Please insert at least one row and one column in grid question");
				surveyQuestionModelList.clear();
				setRowGridAlert(Boolean.FALSE);
				setColumnGridAlert(Boolean.FALSE);
				return;
			}
			    greetingService.saveAllSurvey(idUser, surveyModel, getIdTmpFolder(), surveyQuestionModelList, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(Void result) {
					backToHomepage();
				}
			});
		}
	}
	
	
 
	
	
	/**
	 * Adds the survey 2 list.
	 */
	private void addSurvey2List() {

		this.idUser = (int)userDTO.getUserId();
		populateSurveyModel(userDTO);
		List<CreateQuestionView> questionSurveyList = this.getListSurveyQuestion();
		CreateQuestionView currQuestion = null;
		String imageName = null;

		for (int i=0;i<questionSurveyList.size();i++){
			currQuestion  = questionSurveyList.get(i);
			
			GWT.log( i + " Sect Name="+currQuestion.getSectionName());
			
			/* Text Question */
			if(currQuestion.getTypeSurvey().getValue().equals("Text")){
				addQuestionToSurveyList(getIdSurvey(), currQuestion.getIdQuestion(), currQuestion.getCheckBoxMandatory().getValue(), currQuestion.getTypeSurvey().getValue(), 
						currQuestion.getQuestionSurveyTextArea().getValue(), currQuestion.getAnswerTextBox().getValue(), currQuestion.getFilenameImage(), getIdTmpFolder(), 
						currQuestion.getSectionName(), currQuestion.getSectionDescription());
				/* Paragraph Text Question */
			} else if(currQuestion.getTypeSurvey().getValue().equals("Paragraph Text")){
				addQuestionToSurveyList(getIdSurvey(), currQuestion.getIdQuestion(), currQuestion.getCheckBoxMandatory().getValue(), currQuestion.getTypeSurvey().getValue(), 
						currQuestion.getQuestionSurveyTextArea().getValue(), currQuestion.getAnswerTextArea().getValue(), currQuestion.getFilenameImage(), idTmpFolder, 
						currQuestion.getSectionName(), currQuestion.getSectionDescription());
				/* Multiple Choices Question */
			}else if(currQuestion.getTypeSurvey().getValue().equals("Multiple Choice") 
					|| currQuestion.getTypeSurvey().getValue().equals("CheckBoxes") 
					|| currQuestion.getTypeSurvey().getValue().equals("Drop-Down")){
				addQuestionToSurveyList(getIdSurvey(), currQuestion.getIdQuestion(), currQuestion.getCheckBoxMandatory().getValue(), currQuestion.getTypeSurvey().getValue(), 
						currQuestion.getQuestionSurveyTextArea().getValue(), currQuestion.getMultipleChoiceList(), currQuestion.getFilenameImage(), idTmpFolder, 
						currQuestion.getSectionName(), currQuestion.getSectionDescription());
				/* Grid Question */ 
			} else if (currQuestion.getTypeSurvey().getValue().equals("Grid")){
				addQuestionToSurveyList(getIdSurvey(), currQuestion.getIdQuestion(), currQuestion.getCheckBoxMandatory().getValue(), currQuestion.getTypeSurvey().getValue(), 
						currQuestion.getQuestionSurveyTextArea().getValue(), currQuestion.getRowGridList(), currQuestion.getColumnGridList(), currQuestion.getFilenameImage(), idTmpFolder, 
						currQuestion.getSectionName(), currQuestion.getSectionDescription());
				
			} else if (currQuestion.getTypeSurvey().getValue().equals("Date")){
				addQuestionToSurveyList(getIdSurvey(), currQuestion.getIdQuestion(), currQuestion.getCheckBoxMandatory().getValue(), currQuestion.getTypeSurvey().getValue(), 
						currQuestion.getQuestionSurveyTextArea().getValue(), currQuestion.getDateAnswerSurvey().getValue(), currQuestion.getFilenameImage(), idTmpFolder, 
						currQuestion.getSectionName(), currQuestion.getSectionDescription());
				/* Time Question */
			}else if (currQuestion.getTypeSurvey().getValue().equals("Time")){
				addQuestionToSurveyList(getIdSurvey(), currQuestion.getIdQuestion(), currQuestion.getCheckBoxMandatory().getValue(), currQuestion.getTypeSurvey().getValue(), 
						currQuestion.getQuestionSurveyTextArea().getValue(), currQuestion.getTimeHourAnswerSurvey().getValue(), currQuestion.getTimeMinuteAnswerSurvey().getValue(), 
						currQuestion.getFilenameImage(), idTmpFolder, 
						currQuestion.getSectionName(), currQuestion.getSectionDescription());
				/* Scale Question */
			}else if (currQuestion.getTypeSurvey().getValue().equals("Scale")){
				addQuestionToSurveyList(getIdSurvey(), currQuestion.getIdQuestion(), currQuestion.getCheckBoxMandatory().getValue(), currQuestion.getTypeSurvey().getValue(), 
						currQuestion.getQuestionSurveyTextArea().getValue(), currQuestion.getScaleFromSurveyListBox().getValue(), currQuestion.getScaleToSurveyListBox().getValue(),
						currQuestion.getScaleFromSurveyTextBox().getValue(), currQuestion.getScaleToSurveyTextBox().getValue(), currQuestion.getFilenameImage(), idTmpFolder, 
						currQuestion.getSectionName(), currQuestion.getSectionDescription());
			}

			imageName  = currQuestion.getFilenameImage();
			saveImage(currQuestion, imageName);
		}		
	}
	
	/**
	 * Delete old image.
	 *
	 * @param idSurvey the id survey
	 * @param idTmpFolder the id tmp folder
	 * @param currQuestionOldImageName the curr question old image name
	 */
	private void deleteOldImage(int idSurvey, long idTmpFolder, String currQuestionOldImageName) {
		greetingService.deleteOldImage(idSurvey, idTmpFolder,currQuestionOldImageName, new AsyncCallback<Void>() {

		@Override
		public void onFailure(Throwable caught) {
			
		}

		@Override
		public void onSuccess(Void result) {
		}
	});		
	}

	/**
	 * Back to homepage.
	 */
	private void backToHomepage(){
		  RootPanel.get("survey-div").clear();
		  surveyHomePage = new SurveyHomePage();
		  RootPanel.get("survey-div").add(surveyHomePage);
	}

	/**
	 * Adds the question survey to panel.
	 *
	 * @param listModifySurveyQuestionModel the list modify survey question model
	 * @return the flex table
	 */
	private FlexTable addQuestionSurveyToPanel(List<SurveyQuestionModel> listModifySurveyQuestionModel){
		SurveyQuestionModel modifySurveyQuestionModelTemp = null;
		final long currentFolderID = listModifySurveyQuestionModel.get(0).getFolderIdImage();
		if(listModifySurveyQuestionModel != null){
			for(int i=0; i<listModifySurveyQuestionModel.size(); i++){
				modifySurveyQuestionModelTemp = new SurveyQuestionModel();
				modifySurveyQuestionModelTemp = listModifySurveyQuestionModel.get(i);
				final int idSurveyTemp = modifySurveyQuestionModelTemp.getIdsurvey();
				questionAndRemoveButton.setVisible(true);
				final String currentFileName = modifySurveyQuestionModelTemp.getImageFileName();
				createSurveyView = new CreateQuestionView(listModifySurveyQuestionModel.get(i), getUserDTO());
				Button removeButton = new Button();
				removeButton.addStyleName("removeButtonManageSurveyView");
				removeButton.setIcon(IconType.TRASH);
				removeButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (Window.confirm("Are you sure to delete this question?")){
							int rowIndex = flexTable.getCellForEvent(event).getRowIndex();
							getListSurveyQuestion().remove(rowIndex);
							flexTable.removeRow(rowIndex);
							if ( getRow() > 0){
								setRow(row-1);
								createSurveyView.setIdQuestion(getRow());
							}
							if(getRow() == 0){
								questionAndRemoveButton.setVisible(false);
							}
						 
							if(currentFileName != null){
									greetingService.deleteOldImage(idSurveyTemp, currentFolderID, currentFileName,  new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {
										
									}

									@Override
									public void onSuccess(Void result) {
										
									}
								});
							}
						}
					}
						
				});
				flexTable.setWidget(row, 0, createSurveyView);
				flexTable.setWidget(row, 1, removeButton);
				this.setRow(row+1);

				createSurveyView.setIdQuestion(getRow());
				this.getListSurveyQuestion().add(createSurveyView);
			}
		}
		return flexTable;
	}

	/**
	 * Adds the question to survey list.
	 *
	 * @param idSurvey the id survey
	 * @param idQuestion the id question
	 * @param isMandatory the is mandatory
	 * @param questiontype the questiontype
	 * @param question the question
	 * @param dateAnswer the date answer
	 * @param fileNameImage the file name image
	 * @param folderIdImage the folder id image
	 * @param sectionTitle the section title
	 * @param sectionDescription the section description
	 */
	private void addQuestionToSurveyList(int idSurvey, Integer idQuestion, boolean isMandatory, String questiontype,
			String question, Date dateAnswer, String fileNameImage, long folderIdImage, String sectionTitle, String sectionDescription) {

		SurveyQuestionModel surveyQuestionModel = new SurveyQuestionModel();
		surveyQuestionModel.setIdsurvey(idSurvey);
		surveyQuestionModel.setNumberquestion(idQuestion);
		surveyQuestionModel.setIsmandatory(isMandatory);
		surveyQuestionModel.setQuestiontype(questiontype);
		surveyQuestionModel.setQuestion(question);
		surveyQuestionModel.setImageFileName(fileNameImage);
		surveyQuestionModel.setFolderIdImage(folderIdImage);
		surveyQuestionModel.setDateAnswer(dateAnswer);
		
		surveyQuestionModel.setSectionTitle(sectionTitle);
		surveyQuestionModel.setSectionDescription(sectionDescription);
		
		surveyQuestionModelList.add(surveyQuestionModel);
	}
	
	/**
	 * Adds the question to survey list.
	 *
	 * @param idSurvey the id survey
	 * @param idQuestion the id question
	 * @param isMandatory the is mandatory
	 * @param questiontype the questiontype
	 * @param question the question
	 * @param hour the hour
	 * @param minute the minute
	 * @param fileNameImage the file name image
	 * @param folderIdImage the folder id image
	 * @param sectionTitle the section title
	 * @param sectionDescription the section description
	 */
	private void addQuestionToSurveyList(int idSurvey, Integer idQuestion, boolean isMandatory, String questiontype,
			String question, String hour, String minute, String fileNameImage, long folderIdImage, String sectionTitle, String sectionDescription) {
		
		surveyQuestionModel = new SurveyQuestionModel();
		surveyQuestionModel.setIdsurvey(idSurvey);
		surveyQuestionModel.setNumberquestion(idQuestion);
		surveyQuestionModel.setIsmandatory(isMandatory);
		surveyQuestionModel.setQuestiontype(questiontype);
		surveyQuestionModel.setQuestion(question);
		surveyQuestionModel.setImageFileName(fileNameImage);
		surveyQuestionModel.setFolderIdImage(folderIdImage);
		surveyQuestionModel.setAnswer1(hour.trim());
		surveyQuestionModel.setAnswer2(minute.trim());
		
		surveyQuestionModel.setSectionTitle(sectionTitle);
		surveyQuestionModel.setSectionDescription(sectionDescription);
		
		surveyQuestionModelList.add(surveyQuestionModel);
	}

	/**
	 * Adds the question to survey list.
	 *
	 * @param idSurvey the id survey
	 * @param idQuestion the id question
	 * @param isMandatory the is mandatory
	 * @param questiontype the questiontype
	 * @param question the question
	 * @param answer1 the answer 1
	 * @param fileNameImage the file name image
	 * @param folderIdImage the folder id image
	 * @param sectionTitle the section title
	 * @param sectionDescription the section description
	 */
	private void addQuestionToSurveyList(int idSurvey, int idQuestion, boolean isMandatory, String questiontype, 
			String question, String answer1, String fileNameImage, long folderIdImage, String sectionTitle, String sectionDescription) {

		surveyQuestionModel = new SurveyQuestionModel();
		surveyQuestionModel.setIdsurvey(idSurvey);
		surveyQuestionModel.setNumberquestion(idQuestion);

		surveyQuestionModel.setIsmandatory(isMandatory);
		surveyQuestionModel.setQuestiontype(questiontype);
		surveyQuestionModel.setQuestion(question);
		surveyQuestionModel.setImageFileName(fileNameImage);
		surveyQuestionModel.setFolderIdImage(folderIdImage);
		surveyQuestionModel.setAnswer1(answer1.trim());
		
		surveyQuestionModel.setSectionTitle(sectionTitle);
		surveyQuestionModel.setSectionDescription(sectionDescription);
		
		surveyQuestionModelList.add(surveyQuestionModel);
		
	}
	
	/**
	 * Adds the question to survey list.
	 *
	 * @param idSurvey the id survey
	 * @param idQuestion the id question
	 * @param isMandatory the is mandatory
	 * @param questiontype the questiontype
	 * @param question the question
	 * @param scaleFromSurveyListBox the scale from survey list box
	 * @param scaleToSurveyListBox the scale to survey list box
	 * @param scaleFromSurveyTextBox the scale from survey text box
	 * @param scaleToSurveyTextBox the scale to survey text box
	 * @param fileNameImage the file name image
	 * @param folderIdImage the folder id image
	 * @param sectionTitle the section title
	 * @param sectionDescription the section description
	 */
	private void addQuestionToSurveyList(int idSurvey, Integer idQuestion, boolean isMandatory, String questiontype,
			String question, String scaleFromSurveyListBox, String scaleToSurveyListBox, String scaleFromSurveyTextBox, String scaleToSurveyTextBox, 
			String fileNameImage, long folderIdImage, String sectionTitle, String sectionDescription) {

		SurveyQuestionModel surveyQuestionModel = new SurveyQuestionModel();
		surveyQuestionModel.setIdsurvey(idSurvey);
		surveyQuestionModel.setNumberquestion(idQuestion);
		surveyQuestionModel.setIsmandatory(isMandatory);
		surveyQuestionModel.setQuestiontype(questiontype);
		surveyQuestionModel.setQuestion(question);
		surveyQuestionModel.setImageFileName(fileNameImage);
		surveyQuestionModel.setFolderIdImage(folderIdImage);
		surveyQuestionModel.setAnswer1(scaleFromSurveyListBox.trim());
		surveyQuestionModel.setAnswer2(scaleToSurveyListBox.trim());
		surveyQuestionModel.setAnswer3(scaleFromSurveyTextBox.trim());
		surveyQuestionModel.setAnswer4(scaleToSurveyTextBox.trim());
		
		surveyQuestionModel.setSectionTitle(sectionTitle);
		surveyQuestionModel.setSectionDescription(sectionDescription);
		
		surveyQuestionModelList.add(surveyQuestionModel);
	}
	
	
	/**
	 * Adds the question to survey list.
	 *
	 * @param idSurvey the id survey
	 * @param idQuestion the id question
	 * @param isMandatory the is mandatory
	 * @param questiontype the questiontype
	 * @param question the question
	 * @param answerList the answer list
	 * @param fileNameImage the file name image
	 * @param folderIdImage the folder id image
	 * @param sectionTitle the section title
	 * @param sectionDescription the section description
	 */
	private void addQuestionToSurveyList(int idSurvey, int idQuestion, boolean isMandatory, String questiontype, String question, List<MultipleChoiceView> answerList,
			String fileNameImage, long folderIdImage, String sectionTitle, String sectionDescription) {
	
		int sizeAnswerList = answerList.size();
		List<String> multipleChoiceListTemp = new ArrayList<>();
		
		for(int i=0; i<sizeAnswerList; i++){
			multipleChoiceListTemp.add(answerList.get(i).getAnswerXTextBox().getValue().trim());
		}
		
		if(multipleChoiceListTemp == null || multipleChoiceListTemp.isEmpty()){
			setMultipleChoiceOptionsAlert(Boolean.TRUE);
		}
		
		SurveyQuestionModel surveyQuestionModel = new SurveyQuestionModel();
		surveyQuestionModel.setIdsurvey(idSurvey);
		surveyQuestionModel.setNumberquestion(idQuestion);
		surveyQuestionModel.setIsmandatory(isMandatory);
		surveyQuestionModel.setQuestiontype(questiontype);
		surveyQuestionModel.setQuestion(question);
		surveyQuestionModel.setImageFileName(fileNameImage);
		surveyQuestionModel.setFolderIdImage(folderIdImage);
		surveyQuestionModel.setMultipleChoiceList(multipleChoiceListTemp);
		
		surveyQuestionModel.setSectionTitle(sectionTitle);
		surveyQuestionModel.setSectionDescription(sectionDescription);
		
		surveyQuestionModelList.add(surveyQuestionModel);
	}
	
	
	/**
	 * Adds the question to survey list.
	 *
	 * @param idSurvey the id survey
	 * @param idQuestion the id question
	 * @param isMandatory the is mandatory
	 * @param questiontype the questiontype
	 * @param question the question
	 * @param rowGridList the row grid list
	 * @param columnGridList the column grid list
	 * @param fileNameImage the file name image
	 * @param folderIdImage the folder id image
	 * @param sectionTitle the section title
	 * @param sectionDescription the section description
	 */
	private void addQuestionToSurveyList(int idSurvey, int idQuestion, boolean isMandatory, String questiontype, String question,
			List<MultipleChoiceView> rowGridList, List<MultipleChoiceView> columnGridList, String fileNameImage, long folderIdImage, 
			String sectionTitle, String sectionDescription) {
	
		int sizeRowGridList = rowGridList.size();
		List<String> rowGridListTemp = new ArrayList<>();
		
		for(int i=0; i<sizeRowGridList; i++){
			rowGridListTemp.add(rowGridList.get(i).getAnswerXTextBox().getValue().trim());
		}
		
		if(rowGridListTemp == null || rowGridListTemp.isEmpty()){
			setRowGridAlert(Boolean.TRUE);
		}
		
		int sizeColumnGridList = columnGridList.size();
		List<String> columnGridListTemp = new ArrayList<>();
		
		for(int i=0; i<sizeColumnGridList; i++){
			columnGridListTemp.add(columnGridList.get(i).getAnswerXTextBox().getValue().trim());
		}
		if(columnGridListTemp == null || columnGridListTemp.isEmpty()){
			setColumnGridAlert(Boolean.TRUE);
		}
		
		SurveyQuestionModel surveyQuestionModel = new SurveyQuestionModel();
		surveyQuestionModel.setIdsurvey(idSurvey);
		surveyQuestionModel.setNumberquestion(idQuestion);
		surveyQuestionModel.setIsmandatory(isMandatory);
		surveyQuestionModel.setQuestiontype(questiontype);
		surveyQuestionModel.setQuestion(question);
		surveyQuestionModel.setImageFileName(fileNameImage);
		surveyQuestionModel.setFolderIdImage(folderIdImage);
		
		surveyQuestionModel.setRowGridList(rowGridListTemp);
		surveyQuestionModel.setColumnGridList(columnGridListTemp);
		
		surveyQuestionModel.setSectionTitle(sectionTitle);
		surveyQuestionModel.setSectionDescription(sectionDescription);
		
		surveyQuestionModelList.add(surveyQuestionModel);
	}
	
	
	/**
	 * Save image.
	 *
	 * @param currQuestion the curr question
	 * @param imageName the image name
	 */
	public void saveImage(CreateQuestionView currQuestion, String imageName){
		if (imageName!=null){
			//check if the previous image is changed 
			if (currQuestion.getOldImageName()!=null || !currQuestion.getOldImageName().isEmpty()){
				if (!currQuestion.getOldImageName().equalsIgnoreCase(imageName)){
						//delete from DL the old image
						deleteOldImage(currQuestion.getIdSurvey(), idTmpFolder, currQuestion.getOldImageName());
						currQuestion.saveImage(idTmpFolder);
				}
		     //no image previously, saving new one	
			} else {
					currQuestion.saveImage(idTmpFolder);
			}
		}
	}
	
	
	
	/**
	 * Gets the user from session.
	 *
	 * @return the user from session
	 */
	private void getUserFromSession(){
		greetingService.getUser(new AsyncCallback<UserDTO>(){
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(UserDTO result) {
				
				setUserDTO(result);
				 
			}
		});
	}
	
	/**
	 * Populate survey model.
	 *
	 * @param userDTO the user DTO
	 */
	private void populateSurveyModel(UserDTO userDTO){
		Date today = new Date();
		surveyModel.setIdUserCreator((int)userDTO.getUserId());
		surveyModel.setTitlesurvey(getTitleSurvey());
		surveyModel.setIsAnonymous(checkBoxAnonymous.getValue());
		surveyModel.setDateSurvay(today);
		surveyModel.setExpiredDateSurvay(dateExpiredSurvey.getValue());
	}
	
	/**
	 * Gets the id survey.
	 *
	 * @return the id survey
	 */
	public int getIdSurvey() {
		return idSurvey;
	}

	/**
	 * Sets the id survey.
	 *
	 * @param idSurvey the new id survey
	 */
	public void setIdSurvey(int idSurvey) {
		this.idSurvey = idSurvey;
	}

	/**
	 * Gets the id question.
	 *
	 * @return the id question
	 */
	public Integer getIdQuestion() {
		return idQuestion;
	}

	/**
	 * Sets the id question.
	 *
	 * @param idQuestion the new id question
	 */
	public void setIdQuestion(Integer idQuestion) {
		this.idQuestion = idQuestion;
	}

	/**
	 * Gets the list survey question.
	 *
	 * @return the list survey question
	 */
	private List<CreateQuestionView> getListSurveyQuestion() {
		return listSurveyQuestion;
	}

	
	/**
	 * Gets the title survey.
	 *
	 * @return the title survey
	 */
	public String getTitleSurvey() {
		return titleSurvey;
	}

	/**
	 * Sets the title survey.
	 *
	 * @param titleSurvey the new title survey
	 */
	public void setTitleSurvey(String titleSurvey) {
		this.titleSurvey = titleSurvey;
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
	 * Gets the list modify survey question model.
	 *
	 * @return the list modify survey question model
	 */
	public List<SurveyQuestionModel> getListModifySurveyQuestionModel() {
		return listModifySurveyQuestionModel;
	}

	/**
	 * Sets the list modify survey question model.
	 *
	 * @param listModifySurveyQuestionModel the new list modify survey question model
	 */
	public void setListModifySurveyQuestionModel(List<SurveyQuestionModel> listModifySurveyQuestionModel) {
		this.listModifySurveyQuestionModel = listModifySurveyQuestionModel;
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
	 * Gets the survey model list.
	 *
	 * @return the survey model list
	 */
	public List<SurveyModel> getSurveyModelList() {
		return surveyModelList;
	}

	/**
	 * Sets the survey model list.
	 *
	 * @param surveyModelList the new survey model list
	 */
	public void setSurveyModelList(List<SurveyModel> surveyModelList) {
		this.surveyModelList = surveyModelList;
	}

	/**
	 * Gets the id user.
	 *
	 * @return the id user
	 */
	public long getIdUser() {
		return idUser;
	}

	/**
	 * Sets the id user.
	 *
	 * @param idUser the new id user
	 */
	public void setIdUser(long idUser) {
		this.idUser = idUser;
	}

	/**
	 * Gets the id survey temp.
	 *
	 * @return the id survey temp
	 */
	public int getIdSurveyTemp() {
		return idSurveyTemp;
	}

	/**
	 * Sets the id survey temp.
	 *
	 * @param idSurveyTemp the new id survey temp
	 */
	public void setIdSurveyTemp(int idSurveyTemp) {
		this.idSurveyTemp = idSurveyTemp;
	}

	/**
	 * Gets the expired survey date.
	 *
	 * @return the expired survey date
	 */
	public Date getExpiredSurveyDate() {
		return expiredSurveyDate;
	}

	/**
	 * Sets the expired survey date.
	 *
	 * @param expiredSurveyDate the new expired survey date
	 */
	public void setExpiredSurveyDate(Date expiredSurveyDate) {
		this.expiredSurveyDate = expiredSurveyDate;
	}

	/**
	 * Checks if is multiple choice options alert.
	 *
	 * @return true, if is multiple choice options alert
	 */
	public boolean isMultipleChoiceOptionsAlert() {
		return multipleChoiceOptionsAlert;
	}

	/**
	 * Sets the multiple choice options alert.
	 *
	 * @param multipleChoiceOptionsAlert the new multiple choice options alert
	 */
	public void setMultipleChoiceOptionsAlert(boolean multipleChoiceOptionsAlert) {
		this.multipleChoiceOptionsAlert = multipleChoiceOptionsAlert;
	}

	/**
	 * Gets the id tmp folder.
	 *
	 * @return the id tmp folder
	 */
	public long getIdTmpFolder() {
		return idTmpFolder;
	}

	/**
	 * Sets the id tmp folder.
	 *
	 * @param idTmpFolder the new id tmp folder
	 */
	public void setIdTmpFolder(long idTmpFolder) {
		this.idTmpFolder = idTmpFolder;
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
	 * Gets the modify survey question model temp.
	 *
	 * @return the modify survey question model temp
	 */
	public SurveyQuestionModel getModifySurveyQuestionModelTemp() {
		return modifySurveyQuestionModelTemp;
	}

	/**
	 * Sets the modify survey question model temp.
	 *
	 * @param modifySurveyQuestionModelTemp the new modify survey question model temp
	 */
	public void setModifySurveyQuestionModelTemp(SurveyQuestionModel modifySurveyQuestionModelTemp) {
		this.modifySurveyQuestionModelTemp = modifySurveyQuestionModelTemp;
	}

	/**
	 * Checks if is row grid alert.
	 *
	 * @return true, if is row grid alert
	 */
	public boolean isRowGridAlert() {
		return rowGridAlert;
	}

	/**
	 * Sets the row grid alert.
	 *
	 * @param rowGridAlert the new row grid alert
	 */
	public void setRowGridAlert(boolean rowGridAlert) {
		this.rowGridAlert = rowGridAlert;
	}

	/**
	 * Checks if is column grid alert.
	 *
	 * @return true, if is column grid alert
	 */
	public boolean isColumnGridAlert() {
		return columnGridAlert;
	}

	/**
	 * Sets the column grid alert.
	 *
	 * @param columnGridAlert the new column grid alert
	 */
	public void setColumnGridAlert(boolean columnGridAlert) {
		this.columnGridAlert = columnGridAlert;
	}

}
