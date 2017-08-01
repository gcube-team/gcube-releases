package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.datepicker.client.ui.DateBoxAppended;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;

/**
 * The Class CreateQuestionView.
 */
public class CreateQuestionView extends Composite {

	/** The ui binder. */
	private static CreateSurveyViewUiBinder uiBinder = GWT.create(CreateSurveyViewUiBinder.class);
	
	/** The greeting service. */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);


	/**
	 * The Interface CreateSurveyViewUiBinder.
	 */
	interface CreateSurveyViewUiBinder extends UiBinder<Widget, CreateQuestionView> {
	}

	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;
	
	/** The horizontal panel. */
	@UiField HorizontalPanel horizontalPanel; 
	
	/** The question survey. */
	@UiField WellForm questionSurvey;
	
	/** The type survey. */
	@UiField ListBox typeSurvey;
	
	/** The scale to survey list box. */
	@UiField ListBox scaleFromSurveyListBox, scaleToSurveyListBox;
	
	/** The multiple choice survey answer controls. */
	@UiField Controls textBoxSurveyAnswerControls, textAreaSurveyAnswerControls, multipleChoiceSurveyAnswerControls;
	
	/** The scale answer survey controls. */
	@UiField Controls dateAnswerSurveyControls, timeAnswerSurveyControls, scaleAnswerSurveyControls;
	
	/** The control radio controls. */
	@UiField Controls checkBoxMandatoryControls, controlRadioControls;
	
	/** The new line. */
	@UiField Controls newLine;
	
	/** The answer text box. */
	@UiField TextBox answerTextBox;
	
	/** The scale to survey text box. */
	@UiField TextBox scaleFromSurveyTextBox, scaleToSurveyTextBox;
	
	/** The time minute answer survey. */
	@UiField TextBox timeHourAnswerSurvey, timeMinuteAnswerSurvey;
	
	/** The answer text area. */
	@UiField TextArea answerTextArea;
	
	/** The question survey text area. */
	@UiField TextArea questionSurveyTextArea;
	
	/** The add answer X survey button. */
	@UiField Button addAnswerXSurveyButton;
	
	/** The add row grid button. */
	@UiField Button addRowGridButton;
	
	/** The add column grid button. */
	@UiField Button addColumnGridButton;
	
	/** The date answer survey. */
	@UiField DateBoxAppended dateAnswerSurvey;
	
	/** The scale answer survey control label to. */
	@UiField ControlLabel scaleAnswerSurveyControlLabelFrom, scaleAnswerSurveyControlLabelTo;
	
	/** The time answer survey controls label from. */
	@UiField Label timeAnswerSurveyControlsLabelFrom;
	
	/** The radio button scale survey. */
	@UiField RadioButton radioButtonScaleSurvey;
	
	/** The check box mandatory. */
	@UiField CheckBox checkBoxMandatory;

	/** The add section anchor. */
	@UiField Anchor addSectionAnchor;
	
	/** The remove section anchor. */
	@UiField Anchor removeSectionAnchor;
	
	/** The section title. */
	@UiField TextBox sectionTitle;
	
	/** The section description. */
	@UiField TextArea sectionDescription;
    
	
	/** The upload horizontal panel. */
	HorizontalPanel uploadHorizontalPanel;
	
	/** The upload vertical panel. */
	VerticalPanel uploadVerticalPanel;
	
	/** The Constant MAX_VALUE_HOUR. */
	private static final int MAX_VALUE_HOUR = 23;
	
	/** The Constant MAX_VALUE_MIN. */
	private static final int MAX_VALUE_MIN = 59;
	
    /** The Constant MIN_NUM_RANDOM. */
    public final static Integer MIN_NUM_RANDOM = 1000;
    
    /** The Constant MAX_NUM_RANDOM. */
    public final static Integer MAX_NUM_RANDOM = 10000;		
	
	/** The row other queston. */
	private int row = 0;
	
	/** The row grid. */
	private int rowGrid = 0;
	
	/** The column grid. */
	private int columnGrid = 0;
	
	/** The row other queston. */
	private int rowOtherQueston = 0;
	
	/** The id question. */
	private Integer idQuestion = 0;
	
	/** The multiple choice list. */
	private List<MultipleChoiceView> multipleChoiceList = null; 
	
	/** The row grid list. */
	private List<MultipleChoiceView> rowGridList = null; 
	
	/** The column grid list. */
	private List<MultipleChoiceView> columnGridList = null; 
	
	/** The multiple choice view other queston. */
	private MultipleChoiceView multipleChoiceView, multipleChoiceViewOtherQueston;
	
	/** The flex table. */
	private FlexTable flexTable; 
	
	/** The row grid flex table. */
	private FlexTable rowGridFlexTable; 
	
	/** The column grid flex table. */
	private FlexTable columnGridFlexTable; 

	/** The complete gridflex table. */
	private FlexTable completeGridflexTable; 
	
	/** The other question. */
	private boolean otherQuestion;
	
	/** The add image anchor. */
	private Anchor addOtherQuestonAnchor, addImageAnchor;
	
	/** The add image anchor controls. */
	private Controls addImageAnchorControls;
	
	/** The remove button other queston. */
	private Button removeButtonOtherQueston;
	
	/** The filename image. */
	private String filenameImage;
	
	/** The submit image. */
	private Button submitImage;
	
	/** The name file. */
	private String nameFile;
	
	/** The extension file. */
	private String extensionFile;
	
	/** The image loaded. */
	private boolean imageLoaded = false;
	
	/** The old image name. */
	private String oldImageName = "";
	
	/** The survey question model. */
	private SurveyQuestionModel surveyQuestionModel;
	
	/** The image file name on database paragraph. */
	private Paragraph imageFileNameOnDatabaseParagraph;
	
	/** The upload image file upload. */
	private FileUpload uploadImageFileUpload;
	
	/** The form. */
	private FormPanel form;
	
	/** The img. */
	private Image img;
	
	/** The user DTO. */
	private UserDTO userDTO = null;
	
	/** The remove Img Button. */
	private Button removeImgButton = null;
	
	/** The curr time. */
	private String currTime = null;
	
	/** The section vertical panel. */
	private VerticalPanel sectionVerticalPanel = null;;
	
	/** The section stripe HTML. */
	private HTML sectionStripeHTML = null;

	/** The id survey. */
	private Integer idSurvey = 0;  
	
	/** The br tag. */
	private HTML brTag = null;
	
	/**
	 * Instantiates a new creates the question view.
	 */
	public CreateQuestionView() {
		initWidget(uiBinder.createAndBindUi(this));
		verticalPanel.add(horizontalPanel);
		questionSurvey.setVisible(false);
		typeSurvey.setVisible(false);
		dateAnswerSurvey.setVisible(false);
		
		answerTextBox.addStyleName("textBoxes");

		typeSurveyAddItem();
		scaleSurveyAddItem();

		multipleChoiceList = new ArrayList<MultipleChoiceView>();
		rowGridList = new ArrayList<MultipleChoiceView>();
		columnGridList = new ArrayList<MultipleChoiceView>();
		flexTable = new FlexTable();
		rowGridFlexTable = new FlexTable();
		rowGridFlexTable.addStyleName("gridStyleFlexTable");
		columnGridFlexTable = new FlexTable();
		columnGridFlexTable.addStyleName("gridStyleFlexTable");
		completeGridflexTable = new FlexTable();
		completeGridflexTable.addStyleName("gridStyleFlexTable");
		multipleChoiceViewOtherQueston = new MultipleChoiceView();
		removeButtonOtherQueston = new Button("x");
		addOtherQuestonAnchor = new Anchor("or Add 'Other'");
		addOtherQuestonAnchor.setVisible(false);
		addImageAnchor = new Anchor("Add image");
		addImageAnchorControls = new Controls();
		questionSurvey.add(addImageAnchor);
		imageFileNameOnDatabaseParagraph = new Paragraph();
		removeImgButton = new Button("x");
		removeImgButton.setIcon(IconType.TRASH);
		filenameImage = new String();
		this.otherQuestion = Boolean.FALSE;
		
		brTag = new HTML("<br>");
		
		addSurveyPanel();
		
	} 
	
	/**
	 * Instantiates a new creates the question view.
	 *
	 * @param questionID the question ID
	 */
	public CreateQuestionView(int questionID) {
		
		initWidget(uiBinder.createAndBindUi(this));
		verticalPanel.add(horizontalPanel);
		questionSurvey.setVisible(false);
		typeSurvey.setVisible(false);
		dateAnswerSurvey.setVisible(false);

		answerTextBox.addStyleName("textBoxes");
		
		typeSurveyAddItem();
		scaleSurveyAddItem();
		multipleChoiceList = new ArrayList<MultipleChoiceView>();
		rowGridList = new ArrayList<MultipleChoiceView>();
		columnGridList = new ArrayList<MultipleChoiceView>();
		flexTable = new FlexTable();
		rowGridFlexTable = new FlexTable();
		rowGridFlexTable.addStyleName("gridStyleFlexTable");
		columnGridFlexTable = new FlexTable();
		columnGridFlexTable.addStyleName("gridStyleFlexTable");
		completeGridflexTable = new FlexTable();
		completeGridflexTable.addStyleName("gridStyleFlexTable");
		multipleChoiceViewOtherQueston = new MultipleChoiceView();
		removeButtonOtherQueston = new Button("x");
		addOtherQuestonAnchor = new Anchor("or Add 'Other'");
		addOtherQuestonAnchor.setVisible(false);
		
		addImageAnchor = new Anchor("Add image");
		addImageAnchorControls = new Controls();
		addImageAnchor.setVisible(true);
		imageFileNameOnDatabaseParagraph = new Paragraph();
		removeImgButton = new Button();
		removeImgButton.setIcon(IconType.TRASH);
		filenameImage = new String();
		
		this.otherQuestion = Boolean.FALSE;
		addOtherQuestonAnchor();
		addOtherQuestonAnchor.setVisible(false);
		
		this.currTime = String.valueOf(System.currentTimeMillis());
		uploadImageFileUpload = new FileUpload();
		uploadImageFileUpload.getElement().setAttribute("accept", "image/*");
		uploadImageFileUpload.setName("fileName");
		
		brTag = new HTML("<br>");
		
		addSurveyPanel();
		typeSurveyEqualsText();
		addImageAnchor();
	} 
        
	/**
	 * Instantiates a new creates the question view.
	 *
	 * @param surveyQuestionModel the survey question model
	 * @param userDTO the user DTO
	 */
	// Modify Survey
	public CreateQuestionView(SurveyQuestionModel surveyQuestionModel, UserDTO userDTO) {
		this();
		this.surveyQuestionModel = surveyQuestionModel;
		this.userDTO = userDTO;
		this.idSurvey  = surveyQuestionModel.getIdsurvey();
		answerTextBox.addStyleName("textBoxes");
		this.otherQuestion = Boolean.FALSE;
		multipleChoiceList = new ArrayList<MultipleChoiceView>();
		rowGridList = new ArrayList<MultipleChoiceView>();
		columnGridList = new ArrayList<MultipleChoiceView>();
		multipleChoiceViewOtherQueston = new MultipleChoiceView();
		removeButtonOtherQueston = new Button("x");
		addOtherQuestonAnchor();
		addOtherQuestonAnchor.setVisible(false);
		imageFileNameOnDatabaseParagraph = new Paragraph();
		img = new Image();
		removeImgButton = new Button();
		removeImgButton.setIcon(IconType.TRASH);
		visibleTypeSurveyBoxes();

		uploadImageFileUpload = new FileUpload();
		uploadImageFileUpload.getElement().setAttribute("accept", "image/*");
		uploadImageFileUpload.setName("fileName");

		filenameImage = new String();

		if((surveyQuestionModel != null) 
				&& (surveyQuestionModel.getImageFileName() != null)
				&& !(surveyQuestionModel.getImageFileName().isEmpty())){
			
			addImageAnchor.setVisible(false);
			this.imageLoaded = true;
			this.oldImageName = surveyQuestionModel.getImageFileName();
			removeImgButton.setText(" | " + "Remove image");
			removeImgButton.addStyleName("removeImgButton");


			img = new Image(Window.Location.getProtocol()+"//"+Window.Location.getHostName()+"/surveydisply-portlet/Survey/imageDownloadFromServer?imgName="+surveyQuestionModel.getImageFileName()+"&groupID="+getUserDTO().getGroupId()+"&folderID="+surveyQuestionModel.getFolderIdImage());
			img.addStyleName("imgRetriveQuestionsSurveyView");

			imageFileNameOnDatabaseParagraph.setText(getSurveyQuestionModel().getImageFileName());
			imageFileNameOnDatabaseParagraph.setVisible(true);


			removeImgButton.setVisible(true);
			removeImgButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					
					if (Window.confirm("Are you sure you want to remove image? \nThis action will delete image from system")){
						greetingService.deleteOldImage(getSurveyQuestionModel().getIdsurvey(), getSurveyQuestionModel().getFolderIdImage(), getSurveyQuestionModel().getImageFileName(), new AsyncCallback<Void>() {
							
							@Override
							public void onFailure(Throwable caught) {
								
							}
							
							@Override
							public void onSuccess(Void result) {
								imageFileNameOnDatabaseParagraph.setText("");
								questionSurvey.remove(img);
								removeImgButton.setVisible(false);
								setFilenameImage("");
								setOldImageName("");
								getSurveyQuestionModel().setImageFileName("");
								img = null;
								addImageAnchor.setVisible(true);
							}
						});
					}
				}
			});

			setFilenameImage(getSurveyQuestionModel().getImageFileName());

		}
		
		if (surveyQuestionModel.getImageFileName() == null ||
				surveyQuestionModel.getImageFileName().isEmpty()){
			imageFileNameOnDatabaseParagraph.setText("");
			imageFileNameOnDatabaseParagraph.setVisible(false);
			this.imageLoaded = false;
			setFilenameImage("");
		}
		this.currTime = String.valueOf(System.currentTimeMillis());

		addSectionAnchor.setVisible(true);
		removeSectionAnchor.setVisible(true);
		
		brTag = new HTML("<br>");

		populateSurveyQuestionFromDB(surveyQuestionModel);
		addImageAnchor();
	}

	/**
	 * On change type survey.
	 *
	 * @param event the event
	 */
	@UiHandler("typeSurvey")
	void onChangeTypeSurvey(ChangeEvent event){
		visibleTypeSurveyBoxes();
		cleanAndRemoveSection();
		cleanGridRowColListAndFlexTable();
		
		uploadImageFileUpload.getElement().setPropertyString("value", ""); 
		
		if(typeSurvey.getValue().equals("Text")){
			typeSurveyEqualsText();
			cleanAnswerXSurveyListAndFlexTable();
		} else if(typeSurvey.getValue().equals("Paragraph Text")){
			typeSurveyEqualsParagraphText();
			cleanAnswerXSurveyListAndFlexTable();
		} else if(typeSurvey.getValue().equals("Multiple Choice") 
				|| typeSurvey.getValue().equals("CheckBoxes") 
				|| typeSurvey.getValue().equals("Drop-Down")){	
			typeSurveyEqualsMultipleCheckDrop();
			
		} else if(typeSurvey.getValue().equals("Date")){
			typeSurveyEqualsDate();
			cleanAnswerXSurveyListAndFlexTable();
			
		} else if(typeSurvey.getValue().equals("Time")){
			typeSurveyEqualsTime();
			cleanAnswerXSurveyListAndFlexTable();
			
		} else if(typeSurvey.getValue().equals("Scale")){
			generateRadioButton(scaleFromSurveyListBox.getSelectedValue(), scaleToSurveyListBox.getSelectedValue());
			typeSurveyEqualsScale();
			cleanAnswerXSurveyListAndFlexTable();
			
		} else if(typeSurvey.getValue().equals("Grid")){
			typeSurveyEqualsGrid();
			cleanAnswerXSurveyListAndFlexTable();
		}
	}
	
	/**
	 * Clean and remove section.
	 */
	private void cleanAndRemoveSection() {
		if(sectionStripeHTML != null){
			removeSectionAnchor();
		}
	}

	/**
	 * On click add section anchor.
	 *
	 * @param event the event
	 */
	@UiHandler("addSectionAnchor")
	void onClickAddSectionAnchor(ClickEvent event){
		addSectionAnchor();

	}
	
	/**
	 * Adds the section anchor.
	 */
	private void addSectionAnchor() {
		addSectionAnchor.setVisible(false);
		removeSectionAnchor.setVisible(true);
		questionSurvey.insert(removeSectionAnchor,0);
		
		sectionVerticalPanel = new VerticalPanel();
		sectionVerticalPanel.setVisible(true);
		
		sectionTitle.setVisible(true);
		sectionDescription.setVisible(true);
		
		sectionVerticalPanel.add(sectionTitle);
		sectionVerticalPanel.add(sectionDescription);
		sectionVerticalPanel.addStyleName("sectionVerticalPanelCreateQuestionView");
		sectionVerticalPanel.add(new HTML("<br>"));
		questionSurvey.insert(sectionVerticalPanel, 1);
		
		sectionStripeHTML = new HTML("Section");
		sectionStripeHTML.addStyleName("sectionStripe");
		verticalPanel.insert(sectionStripeHTML, 0);
	}
	
	/**
	 * On click remove section anchor.
	 *
	 * @param event the event
	 */
	@UiHandler("removeSectionAnchor")
	void onClickRemoveSectionAnchor(ClickEvent event){
		removeSectionAnchor();
		
	}
	
	/**
	 * Removes the section anchor.
	 */
	private void removeSectionAnchor() {
		addSectionAnchor.setVisible(true);
		removeSectionAnchor.setVisible(false);
		
		//section title
		sectionTitle.setVisible(false);
		sectionTitle.setText("Untitled Section");
		
		//section description optional
		sectionDescription.setVisible(false);
		sectionDescription.setText("");
		sectionDescription.setPlaceholder("Description (optional)...");
		
		if(sectionVerticalPanel != null){
			questionSurvey.remove(sectionVerticalPanel);
		}
		
		verticalPanel.remove(sectionStripeHTML);
	}

	/**
	 * On click add answer X survey button.
	 *
	 * @param event the event
	 */
	/* Multiple Choice */
	@UiHandler("addAnswerXSurveyButton")
	void onClickAddAnswerXSurveyButton(ClickEvent event){
		addAnswerXSurveyButton();

	}

	/**
	 * Adds the answer X survey button.
	 */
	private void addAnswerXSurveyButton() {
		questionSurvey.remove(checkBoxMandatory);
		questionSurvey.remove(addAnswerXSurveyButton);
		multipleChoiceView = new MultipleChoiceView();
		Button removeButton = new Button("x");
		removeButton.addStyleName("removeButtonCreateSurveyView");
		removeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int rowIndex = flexTable.getCellForEvent(event).getRowIndex();

				getMultipleChoiceList().remove(rowIndex);
				flexTable.removeRow(rowIndex);
				if ( getRow()>0){
					setRow(row-1);
				}
			}
		});
		multipleChoiceView.getAnswerXTextBox().setValue("Option " + (row+1));
		flexTable.setWidget(row, 0, multipleChoiceView);
		flexTable.setWidget(row, 1, removeButton);
		this.setRow(row+1);
		
		this.getMultipleChoiceList().add(multipleChoiceView);
		checkBoxMandatoryAddToPanel();

	}

	/**
	 * Clean answer X survey list and flex table.
	 */
	private void cleanAnswerXSurveyListAndFlexTable(){
		flexTable.clear();
		this.getMultipleChoiceList().clear();
		this.row = 0;
	}
	
	
	/**
	 * On click add row grid button.
	 *
	 * @param event the event
	 */
	@UiHandler("addRowGridButton")
	void onClickAddRowGridButton(ClickEvent event){
		addRowGridButton();
	}
	
	/**
	 * Adds the row grid button.
	 */
	private void addRowGridButton(){
		
		multipleChoiceView = new MultipleChoiceView();
		Button removeButton = new Button("x");
		removeButton.addStyleName("removeButtonCreateSurveyView");
		removeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int rowIndex = rowGridFlexTable.getCellForEvent(event).getRowIndex();
				getRowGridList().remove(rowIndex);
				rowGridFlexTable.removeRow(rowIndex);
				if ( getRowGrid()>0){
					setRowGrid(rowGrid-1);
				}
			}
		});
		multipleChoiceView.getAnswerXTextBox().setValue("Row " + (rowGrid+1));
		rowGridFlexTable.setWidget(rowGrid, 0, multipleChoiceView);
		rowGridFlexTable.setWidget(rowGrid, 1, removeButton);
		
		rowGridFlexTable.setWidget(rowGrid+1, 0, addRowGridButton);
		completeGridflexTable.setWidget(0, 0, rowGridFlexTable);
		this.setRowGrid(rowGrid+1);
		
		this.getRowGridList().add(multipleChoiceView);
		questionSurvey.add(completeGridflexTable);
		questionSurvey.add(checkBoxMandatory);
		questionSurvey.add(multipleChoiceSurveyAnswerControls);
		questionSurvey.add(addImageAnchor);
		
		if(img != null){
			questionSurvey.add(img);
			
			FlexTable imgFilenameAndRemoveImgFlexTable = new FlexTable();
			imgFilenameAndRemoveImgFlexTable.setWidget(0,0,removeImgButton);
			questionSurvey.add(imgFilenameAndRemoveImgFlexTable);
		}
	}
	
	/**
	 * On click add column grid button.
	 *
	 * @param event the event
	 */
	@UiHandler("addColumnGridButton")
	void onClickAddColumnGridButton(ClickEvent event){
		addColumnGridButton();
	}
	
	/**
	 * Adds the column grid button.
	 */
	private void addColumnGridButton(){
		multipleChoiceView = new MultipleChoiceView();
		Button removeButton = new Button("x");
		removeButton.addStyleName("removeButtonCreateSurveyView");
		removeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int rowIndex = columnGridFlexTable.getCellForEvent(event).getRowIndex();
				getColumnGridList().remove(rowIndex);
				columnGridFlexTable.removeRow(rowIndex);
				if ( getColumnGrid()>0){
					setColumnGrid(columnGrid-1);
				}
			}
		});
		multipleChoiceView.getAnswerXTextBox().setValue("Column " + (columnGrid+1));
		columnGridFlexTable.setWidget(columnGrid, 0, multipleChoiceView);
		columnGridFlexTable.setWidget(columnGrid, 1, removeButton);
		columnGridFlexTable.setWidget(columnGrid+1, 0, addColumnGridButton);
		completeGridflexTable.setWidget(0, 1, columnGridFlexTable);
		this.setColumnGrid(columnGrid+1);
		
		this.getColumnGridList().add(multipleChoiceView);
		questionSurvey.add(completeGridflexTable);
		questionSurvey.add(checkBoxMandatory);
		questionSurvey.add(multipleChoiceSurveyAnswerControls);
		questionSurvey.add(addImageAnchor);
		
		if(img != null){
			questionSurvey.add(img);
			
			FlexTable imgFilenameAndRemoveImgFlexTable = new FlexTable();
			imgFilenameAndRemoveImgFlexTable.setWidget(0,0,removeImgButton);
			questionSurvey.add(imgFilenameAndRemoveImgFlexTable);
		}
	}
	
	/**
	 * Clean grid row col list and flex table.
	 */
	private void cleanGridRowColListAndFlexTable(){
		this.rowGridFlexTable.clear();
		this.columnGridFlexTable.clear();
		this.completeGridflexTable.clear();
		
		this.getRowGridList().clear();
		this.getColumnGridList().clear();
		
		this.rowGrid = 0;
		this.columnGrid = 0;
	}
	
	
	/**
	 * On change time hour answer survey.
	 *
	 * @param event the event
	 */
	@UiHandler("timeHourAnswerSurvey")
	void onChangeTimeHourAnswerSurvey(ChangeEvent event){
		int timeHourAnswerSurveyTemp = Integer.parseInt(timeHourAnswerSurvey.getValue());
		if(timeHourAnswerSurveyTemp > MAX_VALUE_HOUR || !timeHourAnswerSurvey.getValue().matches("[0-9]*")){
			timeHourAnswerSurvey.setValue("");
			return;
		}
	}

	/**
	 * On key press event time hour answer survey.
	 *
	 * @param event the event
	 */
	@UiHandler("timeHourAnswerSurvey")
	void onKeyPressEventTimeHourAnswerSurvey(KeyPressEvent event){
		if (!Character.isDigit(event.getCharCode())) {
			((TextBox) event.getSource()).cancelKey();
			return;
		}
	}

	/**
	 * On change time minute answer survey.
	 *
	 * @param event the event
	 */
	@UiHandler("timeMinuteAnswerSurvey")
	void onChangeTimeMinuteAnswerSurvey(ChangeEvent event){
		int timeMinuteAnswerSurveyTemp = Integer.parseInt(timeMinuteAnswerSurvey.getValue());
		if(timeMinuteAnswerSurveyTemp > MAX_VALUE_MIN || !timeMinuteAnswerSurvey.getValue().matches("[0-9]*")){
			timeMinuteAnswerSurvey.setValue("");
			return;
		}
	}

	/**
	 * On key press event time minute answer survey.
	 *
	 * @param event the event
	 */
	@UiHandler("timeMinuteAnswerSurvey")
	void onKeyPressEventTimeMinuteAnswerSurvey(KeyPressEvent event){
		if (!Character.isDigit(event.getCharCode())) {
			((TextBox) event.getSource()).cancelKey();
			return;
		}
	}

	/**
	 * On change scale from survey.
	 *
	 * @param event the event
	 */
	@UiHandler("scaleFromSurveyListBox")
	void onChangeScaleFromSurvey(ChangeEvent event){
		generateRadioButton(scaleFromSurveyListBox.getValue(), scaleToSurveyListBox.getValue());
	}

	/**
	 * On change scale to survey.
	 *
	 * @param event the event
	 */
	@UiHandler("scaleToSurveyListBox")
	void onChangeScaleToSurvey(ChangeEvent event){
		generateRadioButton(scaleFromSurveyListBox.getValue(), scaleToSurveyListBox.getValue());
	}
	
	/**
	 * Save images to DL folder by invoking ImageUploadServlet .
	 *
	 * @param idDLFolder ID of the DL folder
	 */
	public void saveImage(long idDLFolder){
		 
		if(uploadImageFileUpload != null &&
				uploadImageFileUpload.getFilename() != null &&
				!uploadImageFileUpload.getFilename().isEmpty()){
			String servletURL = GWT.getModuleBaseURL()+"imageUpload"+"?idTempFolder="+idDLFolder;
			form.setAction(GWT.getModuleBaseURL()+"imageUpload"+"?idTempFolder="+idDLFolder+"&curTimeMill="+getCurrTime());
			// set form to use the POST method, and multipart MIME encoding.
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);
			//get the filename to be uploaded
			setFilenameImage(getCurrTime()+extractFilenameFromPath(uploadImageFileUpload.getFilename()));
			form.submit();
			 
			form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
				@Override
				public void onSubmitComplete(SubmitCompleteEvent event) {
				}
			});
		} 
	}
 
	
	/**
	 * Adds the survey panel.
	 */
	private void addSurveyPanel() {
		visibleSurveyPanels();
		answerTextBox.setEnabled(false);

		timeHourAnswerSurvey.setSize(2);
		timeMinuteAnswerSurvey.setSize(2);
		timeHourAnswerSurvey.setMaxLength(2);
		timeMinuteAnswerSurvey.setMaxLength(2);

		timeAnswerSurveyControlsLabelFrom.setText(":");
		setOtherQuestion(Boolean.FALSE);
		questionSurvey.add(addImageAnchor);
		
	}
	

	/**
	 * Generate radio button.
	 *
	 * @param start the start
	 * @param end the end
	 */
	private void generateRadioButton(String start, String end){
		int startTemp = Integer.parseInt(start);
		int endTemp = Integer.parseInt(end);
		radioButtonScaleSurvey = null;
		horizontalPanel.clear();
		for(int i=startTemp; i<=endTemp; i++){
			radioButtonScaleSurvey = new RadioButton("radioGroup","" + String.valueOf(i) + "  ");
			horizontalPanel.add(radioButtonScaleSurvey);
			questionSurvey.add(horizontalPanel);
			radioButtonScaleSurvey.setEnabled(false);
		}
		questionSurvey.add(checkBoxMandatory);
		questionSurvey.add(addImageAnchor);
	}

	/**
	 * Scale survey add item.
	 */
	private void scaleSurveyAddItem(){
		scaleFromSurveyListBox.addItem("0");
		scaleFromSurveyListBox.addItem("1");

		scaleToSurveyListBox.addItem("2");
		scaleToSurveyListBox.addItem("3");
		scaleToSurveyListBox.addItem("4");
		scaleToSurveyListBox.addItem("5");
		scaleToSurveyListBox.addItem("6");
		scaleToSurveyListBox.addItem("7");
		scaleToSurveyListBox.addItem("8");
		scaleToSurveyListBox.addItem("9");
		scaleToSurveyListBox.addItem("10");
	}

	/**
	 * Type survey add item.
	 */
	private void typeSurveyAddItem(){
		typeSurvey.addItem("Text");
		typeSurvey.addItem("Paragraph Text");
		typeSurvey.addItem("Multiple Choice");
		typeSurvey.addItem("CheckBoxes");
		typeSurvey.addItem("Drop-Down");
		typeSurvey.addItem("Grid");
		typeSurvey.addItem("Scale");
		typeSurvey.addItem("Date");
		typeSurvey.addItem("Time");
	}

	/**
	 * Visible survey panels.
	 */
	private void visibleSurveyPanels() {
		verticalPanel.add(questionSurvey);
		answerTextArea.setVisible(false);
		addAnswerXSurveyButton.setVisible(false);
		multipleChoiceSurveyAnswerControls.setVisible(true);
		scaleFromSurveyListBox.setVisible(false);
		scaleToSurveyListBox.setVisible(false);
		scaleAnswerSurveyControlLabelFrom.setVisible(false);
		scaleFromSurveyTextBox.setVisible(false);
		scaleAnswerSurveyControlLabelTo.setVisible(false);
		scaleToSurveyTextBox.setVisible(false);
		timeHourAnswerSurvey.setVisible(false);
		timeMinuteAnswerSurvey.setVisible(false);
		timeAnswerSurveyControlsLabelFrom.setVisible(false);
		controlRadioControls.setVisible(false);
		checkBoxMandatory.setVisible(false);

		questionSurvey.setVisible(true);
		typeSurvey.setVisible(true);
		answerTextBox.setVisible(true);

		checkBoxMandatory.setVisible(true);
		addImageAnchor.setVisible(true);
		 
	}
	
	/**
	 * Visible type survey boxes.
	 */
	private void visibleTypeSurveyBoxes(){
		answerTextBox.setVisible(false);
		answerTextArea.setVisible(false);
		addAnswerXSurveyButton.setVisible(false);
		dateAnswerSurvey.setVisible(false);
		scaleAnswerSurveyControlLabelFrom.setVisible(false);
		scaleFromSurveyListBox.setVisible(false);
		scaleAnswerSurveyControlLabelTo.setVisible(false);
		scaleToSurveyListBox.setVisible(false);
		scaleFromSurveyTextBox.setVisible(false);
		scaleToSurveyTextBox.setVisible(false);
		timeHourAnswerSurvey.setVisible(false);
		timeMinuteAnswerSurvey.setVisible(false);
		timeAnswerSurveyControlsLabelFrom.setVisible(false);
		addOtherQuestonAnchor.setVisible(false);
		
		addImageAnchor.setVisible(true);
	}
	
	/**
	 * Type survey equals text.
	 */
	private void typeSurveyEqualsText(){
		questionSurvey.clear();
		answerTextBox.setVisible(true);
		addSectionAnchor.setVisible(true);

		questionSurvey.add(addSectionAnchor);
		questionSurvey.add(new HTML("<br>"));
		questionSurvey.add(typeSurvey);
		questionSurvey.add(newLine);
		questionSurvey.add(questionSurveyTextArea);
		questionSurvey.add(textBoxSurveyAnswerControls);
		
		answerTextBox.setEnabled(false);
		checkBoxMandatoryAddToPanel();
	}
	
	/**
	 * Type survey equals paragraph text.
	 */
	private void typeSurveyEqualsParagraphText(){
		questionSurvey.clear();
		answerTextArea.setVisible(true);

		questionSurvey.add(addSectionAnchor);
		questionSurvey.add(new HTML("<br>"));
		questionSurvey.add(typeSurvey);
		questionSurvey.add(newLine);
		questionSurvey.add(questionSurveyTextArea);
		questionSurvey.add(textBoxSurveyAnswerControls);
		questionSurvey.add(answerTextArea);
		answerTextArea.setEnabled(false);

		checkBoxMandatoryAddToPanel();
	}
	
	/**
	 * Type survey equals multiple check drop.
	 */
	private void typeSurveyEqualsMultipleCheckDrop(){
		questionSurvey.remove(checkBoxMandatoryControls);
		questionSurvey.remove(checkBoxMandatory);
		questionSurvey.remove(horizontalPanel);
		
		answerTextBox.setVisible(false);
		addAnswerXSurveyButton.setVisible(true);

		if(typeSurvey.getValue().equals("Drop-Down")){
			addOtherQuestonAnchor.setVisible(Boolean.FALSE);
			if(isOtherQuestion()){
				flexTable.remove(multipleChoiceViewOtherQueston);
				flexTable.remove(removeButtonOtherQueston);
				setOtherQuestion(Boolean.FALSE);
				getMultipleChoiceList().remove(multipleChoiceViewOtherQueston);
			}
		}
		
		questionSurvey.clear();
		questionSurvey.add(addSectionAnchor);
		questionSurvey.add(brTag);
		questionSurvey.add(typeSurvey);
		questionSurvey.add(newLine);
		questionSurvey.add(questionSurveyTextArea);
		questionSurvey.add(multipleChoiceSurveyAnswerControls);
		questionSurvey.add(addAnswerXSurveyButton);
		
		
		if(!isOtherQuestion() && !typeSurvey.getValue().equals("Drop-Down")){
			addOtherQuestonAnchor.setVisible(true);
			questionSurvey.add(addOtherQuestonAnchor);
		}
		
		checkBoxMandatoryAddToPanel();
	}
	
	
	/**
	 * Type survey equals grid.
	 */
	private void typeSurveyEqualsGrid(){
		questionSurvey.clear();
		answerTextArea.setVisible(true);
		addRowGridButton.setVisible(true);
		addColumnGridButton.setVisible(true);

		questionSurvey.add(addSectionAnchor);
		questionSurvey.add(new HTML("<br>"));
		questionSurvey.add(typeSurvey);
		questionSurvey.add(newLine);
		questionSurvey.add(questionSurveyTextArea);
		questionSurvey.add(textBoxSurveyAnswerControls);
		
		rowGridFlexTable.setWidget(0, 0, addRowGridButton);
		columnGridFlexTable.setWidget(0, 0, addColumnGridButton);
		
		completeGridflexTable.setWidget(0, 0, rowGridFlexTable);
		completeGridflexTable.setWidget(0, 1, columnGridFlexTable);
		
		questionSurvey.add(completeGridflexTable);
		
		checkBoxMandatoryAddToPanel();
	}

	
	/**
	 * Type survey equals date.
	 */
	private void typeSurveyEqualsDate() {
		questionSurvey.clear();
		answerTextBox.setVisible(false);
		dateAnswerSurvey.setVisible(true);

		questionSurvey.add(addSectionAnchor);
		questionSurvey.add(new HTML("<br>"));
		questionSurvey.add(typeSurvey);
		questionSurvey.add(newLine);
		questionSurvey.add(questionSurveyTextArea);
		questionSurvey.add(dateAnswerSurveyControls);
		questionSurvey.add(dateAnswerSurvey);
		
		dateAnswerSurvey.setReadOnly(true);

		checkBoxMandatoryAddToPanel();		
	}
	
	/**
	 * Type survey equals time.
	 */
	private void typeSurveyEqualsTime() {
		questionSurvey.clear();
		answerTextBox.setVisible(false);
		timeAnswerSurveyControlsLabelFrom.setVisible(true);
		timeHourAnswerSurvey.setVisible(true);
		timeMinuteAnswerSurvey.setVisible(true);

		questionSurvey.add(addSectionAnchor);
		questionSurvey.add(new HTML("<br>"));
		questionSurvey.add(typeSurvey);
		questionSurvey.add(newLine);
		questionSurvey.add(questionSurveyTextArea);
		questionSurvey.add(timeAnswerSurveyControls);
		
		questionSurvey.add(timeHourAnswerSurvey);
		questionSurvey.add(timeAnswerSurveyControlsLabelFrom);
		questionSurvey.add(timeMinuteAnswerSurvey);
		
		timeHourAnswerSurvey.setEnabled(false);
		timeMinuteAnswerSurvey.setEnabled(false);

		checkBoxMandatoryAddToPanel();
	}
	
	/**
	 * Type survey equals scale.
	 */
	private void typeSurveyEqualsScale() {
		questionSurvey.clear();
		answerTextBox.setVisible(false);
		scaleAnswerSurveyControlLabelFrom.setVisible(true);
		scaleFromSurveyListBox.setVisible(true);
		scaleAnswerSurveyControlLabelTo.setVisible(true);
		scaleToSurveyListBox.setVisible(true);
		scaleFromSurveyTextBox.setVisible(true);
		scaleToSurveyTextBox.setVisible(true);
		
		questionSurvey.add(addSectionAnchor);
		questionSurvey.add(new HTML("<br>"));
		questionSurvey.add(typeSurvey);
		questionSurvey.add(newLine);
		questionSurvey.add(questionSurveyTextArea);
		questionSurvey.add(dateAnswerSurveyControls);
		
		questionSurvey.add(scaleAnswerSurveyControlLabelFrom);
		questionSurvey.add(scaleFromSurveyListBox);
		questionSurvey.add(scaleAnswerSurveyControlLabelTo);
		questionSurvey.add(scaleToSurveyListBox);
		questionSurvey.add(scaleAnswerSurveyControls);
		questionSurvey.add(scaleFromSurveyTextBox);
		questionSurvey.add(scaleToSurveyTextBox);

		checkBoxMandatoryAddToPanel();
	}
	
	/**
	 * Check box mandatory add to panel.
	 */
	private void checkBoxMandatoryAddToPanel(){
		questionSurvey.add(flexTable);
		questionSurvey.add(addAnswerXSurveyButton);
		questionSurvey.add(addOtherQuestonAnchor);
		questionSurvey.add(multipleChoiceSurveyAnswerControls);
		questionSurvey.add(checkBoxMandatory);
		questionSurvey.add(dateAnswerSurveyControls);
		questionSurvey.add(addImageAnchor);
		
		if(img != null){
			questionSurvey.add(img);
		}
		
		if((getSurveyQuestionModel() != null) &&
				(getSurveyQuestionModel().getImageFileName() != null) &&
				!(getSurveyQuestionModel().getImageFileName().isEmpty())){
			imageFileNameOnDatabaseParagraph.setText(getSurveyQuestionModel().getImageFileName());
			imageFileNameOnDatabaseParagraph.setVisible(true);
			
			FlexTable imgFilenameAndRemoveImgFlexTable = new FlexTable();
			imgFilenameAndRemoveImgFlexTable.addStyleName("imgFilenameAndRemoveImgFlexTable");
			imgFilenameAndRemoveImgFlexTable.setWidget(0,0,removeImgButton);
			questionSurvey.add(imgFilenameAndRemoveImgFlexTable);
			
		} else {
			imageFileNameOnDatabaseParagraph = new Paragraph("");
			imageFileNameOnDatabaseParagraph.setVisible(false);
			questionSurvey.add(imageFileNameOnDatabaseParagraph);
		}
	}
	
	/**
	 * Populate survey question from DB.
	 *
	 * @param surveyQuestionModel the survey question model
	 */
	private void populateSurveyQuestionFromDB(SurveyQuestionModel surveyQuestionModel) {
		if(surveyQuestionModel.getQuestiontype().equals("Text")){
			typeSurveyEqualsText();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			
		}else if(surveyQuestionModel.getQuestiontype().equals("Paragraph Text")){
			typeSurveyEqualsParagraphText();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			
		}else if(surveyQuestionModel.getQuestiontype().equals("Multiple Choice")
				|| surveyQuestionModel.getQuestiontype().equals("CheckBoxes") 
				|| surveyQuestionModel.getQuestiontype().equals("Drop-Down")){
			typeSurveyEqualsText();
			typeSurveyEqualsMultipleCheckDrop();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			addAnswerMultipleCheckDrop(surveyQuestionModel);
			
		}else if(surveyQuestionModel.getQuestiontype().equals("Date")){
			typeSurveyEqualsDate();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			dateAnswerSurvey.setValue(surveyQuestionModel.getDateAnswer());
		
		}else if(surveyQuestionModel.getQuestiontype().equals("Time")){
			typeSurveyEqualsTime();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			timeHourAnswerSurvey.setValue(surveyQuestionModel.getAnswer1());
			timeMinuteAnswerSurvey.setValue(surveyQuestionModel.getAnswer2());
			
		}else if(surveyQuestionModel.getQuestiontype().equals("Scale")){
			typeSurveyEqualsScale();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			scaleFromSurveyListBox.setSelectedValue(surveyQuestionModel.getAnswer1());
			scaleToSurveyListBox.setSelectedValue(surveyQuestionModel.getAnswer2());
			scaleFromSurveyTextBox.setValue(surveyQuestionModel.getAnswer3());
			scaleToSurveyTextBox.setValue(surveyQuestionModel.getAnswer4());
			
		}else if(surveyQuestionModel.getQuestiontype().equals("Grid")){
			typeSurveyEqualsGrid();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			addRowGrid(surveyQuestionModel);
			addColumnGrid(surveyQuestionModel);
		}		
	}
	
	/**
	 * Adds the row grid.
	 *
	 * @param surveyQuestionModel the survey question model
	 */
	private void addRowGrid(SurveyQuestionModel surveyQuestionModel) {
		for(int i=0; i<surveyQuestionModel.getRowGridList().size() ; i++){
			if(surveyQuestionModel.getRowGridList() != null ){
				multipleChoiceView = new MultipleChoiceView();
				Button removeButton = new Button("x");
				removeButton.addStyleName("removeButtonCreateSurveyView");
				removeButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						int rowIndex = rowGridFlexTable.getCellForEvent(event).getRowIndex();
						getRowGridList().remove(rowIndex);
						rowGridFlexTable.removeRow(rowIndex);
						if ( getRowGrid()>0){
							setRowGrid(rowGrid-1);
						}
					}
				});
				multipleChoiceView.getAnswerXTextBox().setValue(surveyQuestionModel.getRowGridList().get(i));
				
				rowGridFlexTable.setWidget(rowGrid, 0, multipleChoiceView);
				rowGridFlexTable.setWidget(rowGrid, 1, removeButton);
				
				rowGridFlexTable.setWidget(rowGrid+1, 0, addRowGridButton);
				completeGridflexTable.setWidget(0, 0, rowGridFlexTable);
				this.setRowGrid(rowGrid+1);
				
				this.getRowGridList().add(multipleChoiceView);
				questionSurvey.add(completeGridflexTable);
				questionSurvey.add(checkBoxMandatory);
				questionSurvey.add(multipleChoiceSurveyAnswerControls);
				questionSurvey.add(addImageAnchor);
				
				if(img != null){
					questionSurvey.add(img);
				
					FlexTable imgFilenameAndRemoveImgFlexTable = new FlexTable();
					imgFilenameAndRemoveImgFlexTable.setWidget(0,0,removeImgButton);
					questionSurvey.add(imgFilenameAndRemoveImgFlexTable);
				}
			}
		}
	}
	
	/**
	 * Adds the column grid.
	 *
	 * @param surveyQuestionModel the survey question model
	 */
	private void addColumnGrid(SurveyQuestionModel surveyQuestionModel) {
		for(int i=0; i<surveyQuestionModel.getColumnGridList().size() ; i++){
			if(surveyQuestionModel.getColumnGridList() != null ){
				multipleChoiceView = new MultipleChoiceView();
				Button removeButton = new Button("x");
				removeButton.addStyleName("removeButtonCreateSurveyView");
				removeButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						int rowIndex = columnGridFlexTable.getCellForEvent(event).getRowIndex();
						getColumnGridList().remove(rowIndex);
						columnGridFlexTable.removeRow(rowIndex);
						if ( getColumnGrid()>0){
							setColumnGrid(columnGrid-1);
						}
					}
				});
				multipleChoiceView.getAnswerXTextBox().setValue(surveyQuestionModel.getColumnGridList().get(i));
				columnGridFlexTable.setWidget(columnGrid, 0, multipleChoiceView);
				columnGridFlexTable.setWidget(columnGrid, 1, removeButton);
				columnGridFlexTable.setWidget(columnGrid+1, 0, addColumnGridButton);
				completeGridflexTable.setWidget(0, 1, columnGridFlexTable);
				this.setColumnGrid(columnGrid+1);
				
				this.getColumnGridList().add(multipleChoiceView);
				questionSurvey.add(completeGridflexTable);
				questionSurvey.add(checkBoxMandatory);
				questionSurvey.add(multipleChoiceSurveyAnswerControls);
				questionSurvey.add(addImageAnchor);
				
				if(img != null){
					questionSurvey.add(img);
					
					FlexTable imgFilenameAndRemoveImgFlexTable = new FlexTable();
					imgFilenameAndRemoveImgFlexTable.setWidget(0,0,removeImgButton);
					questionSurvey.add(imgFilenameAndRemoveImgFlexTable);
				}
				
			}
		}
	}

	/**
	 * Adds the answer multiple check drop.
	 *
	 * @param surveyQuestionModel the survey question model
	 */
	private void addAnswerMultipleCheckDrop(SurveyQuestionModel surveyQuestionModel) {
		questionSurvey.remove(checkBoxMandatory);

		for(int i=0; i<surveyQuestionModel.getMultipleChoiceList().size() ; i++){
			if(surveyQuestionModel.getMultipleChoiceList() != null ){
				multipleChoiceView = new MultipleChoiceView();
				Button removeButton = new Button("x");
				removeButton.addStyleName("removeButtonCreateSurveyView");
				removeButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						int rowIndex = flexTable.getCellForEvent(event).getRowIndex();
						getMultipleChoiceList().remove(rowIndex);
						flexTable.removeRow(rowIndex);
						setOtherQuestion(Boolean.FALSE);
						addOtherQuestonAnchor.setVisible(Boolean.TRUE);
						if ( getRow()>0){
							setRow(row-1);
						}
					}
				});
				multipleChoiceView.getAnswerXTextBox().setValue(surveyQuestionModel.getMultipleChoiceList().get(i));
				if(surveyQuestionModel.getMultipleChoiceList().get(i).contains("Other...")){
					multipleChoiceView.getAnswerXTextBox().setEnabled(Boolean.FALSE);
					setOtherQuestion(Boolean.TRUE);
					addOtherQuestonAnchor.setVisible(Boolean.FALSE);
				}
				
				if(typeSurvey.getValue().equals("Drop-Down")){
					addOtherQuestonAnchor.setVisible(Boolean.FALSE);
				}
				
				flexTable.setWidget(row, 0, multipleChoiceView);
				flexTable.setWidget(row, 1, removeButton);
				this.setRow(row+1);

				questionSurvey.add(flexTable);
				this.getMultipleChoiceList().add(multipleChoiceView);
				
			}
		}
		HTML br = new HTML("<br>");

		questionSurvey.add(addAnswerXSurveyButton);
		questionSurvey.add(addOtherQuestonAnchor);
		questionSurvey.add(multipleChoiceSurveyAnswerControls);
		questionSurvey.add(checkBoxMandatory);
		questionSurvey.add(br);
		questionSurvey.add(addImageAnchor);
		
		if(img != null){
			questionSurvey.add(img);
		}
		
		if((getSurveyQuestionModel() != null) &&
				!(getSurveyQuestionModel().getImageFileName().isEmpty())){
			imageFileNameOnDatabaseParagraph.setText(getSurveyQuestionModel().getImageFileName());
			imageFileNameOnDatabaseParagraph.setVisible(true);
			
			FlexTable imgFilenameAndRemoveImgFlexTable = new FlexTable();
			imgFilenameAndRemoveImgFlexTable.setWidget(0,0,removeImgButton);
			questionSurvey.add(imgFilenameAndRemoveImgFlexTable);
		}
	}

	
	/**
	 * Adds the other queston anchor.
	 */
	private void addOtherQuestonAnchor(){
		addOtherQuestonAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				questionSurvey.remove(checkBoxMandatory);
				questionSurvey.remove(addAnswerXSurveyButton);
				rowOtherQueston = flexTable.getCellForEvent(event).getRowIndex();
				removeButtonOtherQueston.addStyleName("removeButtonCreateSurveyView");
				removeButtonOtherQueston.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						int rowIndex = flexTable.getCellForEvent(event).getRowIndex();
						
						getMultipleChoiceList().remove(rowIndex);
						flexTable.removeRow(rowIndex);
								
						if ( getRow()>0){
							setRow(getRow()-1);
						}
						
						setOtherQuestion(Boolean.FALSE);
						addOtherQuestonAnchor.setVisible(Boolean.TRUE);
					}
				});
				multipleChoiceViewOtherQueston.getAnswerXTextBox().setValue("Other...");
				multipleChoiceViewOtherQueston.getAnswerXTextBox().setEnabled(Boolean.FALSE);
				flexTable.setWidget(row, 0, multipleChoiceViewOtherQueston);
				flexTable.setWidget(row, 1, removeButtonOtherQueston);
				setRow(getRow()+1);
				
				questionSurvey.add(flexTable);
				getMultipleChoiceList().add(multipleChoiceViewOtherQueston);

				questionSurvey.add(addAnswerXSurveyButton);
				questionSurvey.add(addOtherQuestonAnchor);
				questionSurvey.add(multipleChoiceSurveyAnswerControls);
				questionSurvey.add(checkBoxMandatory);
				questionSurvey.add(addImageAnchor);
				if((getSurveyQuestionModel() != null) &&
						!(getSurveyQuestionModel().getImageFileName().isEmpty())){
					imageFileNameOnDatabaseParagraph.setText(getSurveyQuestionModel().getImageFileName());
					imageFileNameOnDatabaseParagraph.setVisible(true);
					setFilenameImage(getSurveyQuestionModel().getImageFileName());
					
					FlexTable imgFilenameAndRemoveImgFlexTable = new FlexTable();
					imgFilenameAndRemoveImgFlexTable.setWidget(0,0,removeImgButton);
					questionSurvey.add(imgFilenameAndRemoveImgFlexTable);
					
				}
				
				setOtherQuestion(Boolean.TRUE);
				addOtherQuestonAnchor.setVisible(Boolean.FALSE);
			}
		
		});
		
	}
	
	/**
	 * Adds the image anchor.
	 */
	private void addImageAnchor(){
		addImageAnchor.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				uploadImage();
				addImageAnchor.setVisible(false);
				imageFileNameOnDatabaseParagraph.setVisible(false);
			}});
	}
	
	/**
	 * Sets the text box and check box values.
	 *
	 * @param surveyQuestionModel the new text box and check box values
	 */
	private void setTextBoxAndCheckBoxValues(SurveyQuestionModel surveyQuestionModel){
		
		if(surveyQuestionModel.getSectionTitle() != null
				&& !surveyQuestionModel.getSectionTitle().isEmpty()
				&& !surveyQuestionModel.getSectionTitle().equalsIgnoreCase("")
				&& !surveyQuestionModel.getSectionTitle().equalsIgnoreCase("Untitled Section")){
			addSectionAnchor();
			sectionTitle.setText(surveyQuestionModel.getSectionTitle());
			sectionDescription.setText(surveyQuestionModel.getSectionDescription());
		} else {
			removeSectionAnchor.setVisible(false);
			sectionTitle.setVisible(false);
			sectionDescription.setVisible(false);
		}

		typeSurvey.setSelectedValue(surveyQuestionModel.getQuestiontype());
		questionSurveyTextArea.setText(surveyQuestionModel.getQuestion());
		checkBoxMandatory.setValue(surveyQuestionModel.getIsmandatory());
	}
	
	/**
	 * Upload image.
	 */
	private void uploadImage() {

		uploadHorizontalPanel = new HorizontalPanel();
		uploadVerticalPanel = new VerticalPanel();
		form = new FormPanel();
		
		final TextBox tx = new TextBox();
		tx.setVisible(false);
		tx.setName("img");
		
		uploadImageFileUpload.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				
				setFilenameImage(getCurrTime()+extractFilenameFromPath(uploadImageFileUpload.getFilename()));
				
			}
		});
		
		uploadHorizontalPanel.add(uploadImageFileUpload);
		uploadHorizontalPanel.add(tx);
		uploadVerticalPanel.add(uploadHorizontalPanel);
		form.add(uploadVerticalPanel);

		questionSurvey.add(form);
		verticalPanel.add(questionSurvey);

	}
	

	
	/**
	 * Extract filename from path.
	 *
	 * @param fileName the file name
	 * @return the string
	 */
	private String extractFilenameFromPath(String fileName){
		String fileNameExtracted = null;
		
		if(fileName != null && 
				!fileName.isEmpty()){
			int subStr1 = fileName.lastIndexOf("\\");
			fileNameExtracted = fileName.substring(subStr1+1);
		}
		return fileNameExtracted;
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
	 * Gets the type survey.
	 *
	 * @return the type survey
	 */
	public ListBox getTypeSurvey() {
		return typeSurvey;
	}

	/**
	 * Sets the type survey.
	 *
	 * @param typeSurvey the new type survey
	 */
	public void setTypeSurvey(ListBox typeSurvey) {
		this.typeSurvey = typeSurvey;
	}

	/**
	 * Gets the answer text box.
	 *
	 * @return the answer text box
	 */
	public TextBox getAnswerTextBox() {
		return answerTextBox;
	}

	/**
	 * Sets the answer text box.
	 *
	 * @param answerTextBox the new answer text box
	 */
	public void setAnswerTextBox(TextBox answerTextBox) {
		this.answerTextBox = answerTextBox;
	}

	/**
	 * Gets the answer text area.
	 *
	 * @return the answer text area
	 */
	public TextArea getAnswerTextArea() {
		return answerTextArea;
	}

	/**
	 * Sets the answer text area.
	 *
	 * @param answerTextArea the new answer text area
	 */
	public void setAnswerTextArea(TextArea answerTextArea) {
		this.answerTextArea = answerTextArea;
	}

	/**
	 * Gets the multiple choice list.
	 *
	 * @return the multiple choice list
	 */
	public List<MultipleChoiceView> getMultipleChoiceList() {
		return multipleChoiceList;
	}

	/**
	 * Sets the multiple choice list.
	 *
	 * @param multipleChoiceList the new multiple choice list
	 */
	public void setMultipleChoiceList(List<MultipleChoiceView> multipleChoiceList) {
		this.multipleChoiceList = multipleChoiceList;
	}

	/**
	 * Gets the date answer survey.
	 *
	 * @return the date answer survey
	 */
	public DateBoxAppended getDateAnswerSurvey() {
		return dateAnswerSurvey;
	}

	/**
	 * Sets the date answer survey.
	 *
	 * @param dateAnswerSurvey the new date answer survey
	 */
	public void setDateAnswerSurvey(DateBoxAppended dateAnswerSurvey) {
		this.dateAnswerSurvey = dateAnswerSurvey;
	}

	/**
	 * Gets the time hour answer survey.
	 *
	 * @return the time hour answer survey
	 */
	public TextBox getTimeHourAnswerSurvey() {
		return timeHourAnswerSurvey;
	}

	/**
	 * Sets the time hour answer survey.
	 *
	 * @param timeHourAnswerSurvey the new time hour answer survey
	 */
	public void setTimeHourAnswerSurvey(TextBox timeHourAnswerSurvey) {
		this.timeHourAnswerSurvey = timeHourAnswerSurvey;
	}

	/**
	 * Gets the time minute answer survey.
	 *
	 * @return the time minute answer survey
	 */
	public TextBox getTimeMinuteAnswerSurvey() {
		return timeMinuteAnswerSurvey;
	}

	/**
	 * Sets the time minute answer survey.
	 *
	 * @param timeMinuteAnswerSurvey the new time minute answer survey
	 */
	public void setTimeMinuteAnswerSurvey(TextBox timeMinuteAnswerSurvey) {
		this.timeMinuteAnswerSurvey = timeMinuteAnswerSurvey;
	}

	/**
	 * Sets the scale answer survey controls.
	 *
	 * @param scaleAnswerSurveyControls the new scale answer survey controls
	 */
	public void setScaleAnswerSurveyControls(Controls scaleAnswerSurveyControls) {
		this.scaleAnswerSurveyControls = scaleAnswerSurveyControls;
	}

	/**
	 * Gets the scale from survey text box.
	 *
	 * @return the scale from survey text box
	 */
	public TextBox getScaleFromSurveyTextBox() {
		return scaleFromSurveyTextBox;
	}

	/**
	 * Sets the scale from survey text box.
	 *
	 * @param scaleFromSurveyTextBox the new scale from survey text box
	 */
	public void setScaleFromSurveyTextBox(TextBox scaleFromSurveyTextBox) {
		this.scaleFromSurveyTextBox = scaleFromSurveyTextBox;
	}

	/**
	 * Gets the scale to survey text box.
	 *
	 * @return the scale to survey text box
	 */
	public TextBox getScaleToSurveyTextBox() {
		return scaleToSurveyTextBox;
	}

	/**
	 * Sets the scale to survey text box.
	 *
	 * @param scaleToSurveyTextBox the new scale to survey text box
	 */
	public void setScaleToSurveyTextBox(TextBox scaleToSurveyTextBox) {
		this.scaleToSurveyTextBox = scaleToSurveyTextBox;
	}

	/**
	 * Gets the check box mandatory.
	 *
	 * @return the check box mandatory
	 */
	public CheckBox getCheckBoxMandatory() {
		return checkBoxMandatory;
	}

	/**
	 * Sets the check box mandatory.
	 *
	 * @param checkBoxMandatory the new check box mandatory
	 */
	public void setCheckBoxMandatory(CheckBox checkBoxMandatory) {
		this.checkBoxMandatory = checkBoxMandatory;
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
	 * Gets the scale from survey list box.
	 *
	 * @return the scale from survey list box
	 */
	public ListBox getScaleFromSurveyListBox() {
		return scaleFromSurveyListBox;
	}

	/**
	 * Sets the scale from survey list box.
	 *
	 * @param scaleFromSurveyListBox the new scale from survey list box
	 */
	public void setScaleFromSurveyListBox(ListBox scaleFromSurveyListBox) {
		this.scaleFromSurveyListBox = scaleFromSurveyListBox;
	}

	/**
	 * Gets the scale to survey list box.
	 *
	 * @return the scale to survey list box
	 */
	public ListBox getScaleToSurveyListBox() {
		return scaleToSurveyListBox;
	}

	/**
	 * Sets the scale to survey list box.
	 *
	 * @param scaleToSurveyListBox the new scale to survey list box
	 */
	public void setScaleToSurveyListBox(ListBox scaleToSurveyListBox) {
		this.scaleToSurveyListBox = scaleToSurveyListBox;
	}

	/**
	 * Gets the question survey text area.
	 *
	 * @return the question survey text area
	 */
	public TextArea getQuestionSurveyTextArea() {
		return questionSurveyTextArea;
	}

	/**
	 * Sets the question survey text area.
	 *
	 * @param questionSurveyTextArea the new question survey text area
	 */
	public void setQuestionSurveyTextArea(TextArea questionSurveyTextArea) {
		this.questionSurveyTextArea = questionSurveyTextArea;
	}

	/**
	 * Gets the adds the other queston anchor.
	 *
	 * @return the adds the other queston anchor
	 */
	public Anchor getAddOtherQuestonAnchor() {
		return addOtherQuestonAnchor;
	}

	/**
	 * Sets the adds the other queston anchor.
	 *
	 * @param addOtherQuestonAnchor the new adds the other queston anchor
	 */
	public void setAddOtherQuestonAnchor(Anchor addOtherQuestonAnchor) {
		this.addOtherQuestonAnchor = addOtherQuestonAnchor;
	}

	/**
	 * Checks if is other question.
	 *
	 * @return true, if is other question
	 */
	public boolean isOtherQuestion() {
		return otherQuestion;
	}

	/**
	 * Sets the other question.
	 *
	 * @param otherQuestion the new other question
	 */
	public void setOtherQuestion(boolean otherQuestion) {
		this.otherQuestion = otherQuestion;
	}

	/**
	 * Gets the multiple choice view other queston.
	 *
	 * @return the multiple choice view other queston
	 */
	public MultipleChoiceView getMultipleChoiceViewOtherQueston() {
		return multipleChoiceViewOtherQueston;
	}

	/**
	 * Sets the multiple choice view other queston.
	 *
	 * @param multipleChoiceViewOtherQueston the new multiple choice view other queston
	 */
	public void setMultipleChoiceViewOtherQueston(MultipleChoiceView multipleChoiceViewOtherQueston) {
		this.multipleChoiceViewOtherQueston = multipleChoiceViewOtherQueston;
	}

	/**
	 * Gets the row other queston.
	 *
	 * @return the row other queston
	 */
	public int getRowOtherQueston() {
		return rowOtherQueston;
	}

	/**
	 * Sets the row other queston.
	 *
	 * @param rowOtherQueston the new row other queston
	 */
	public void setRowOtherQueston(int rowOtherQueston) {
		this.rowOtherQueston = rowOtherQueston;
	}

	/**
	 * Gets the filename image.
	 *
	 * @return the filename image
	 */
	public String getFilenameImage() {
		return filenameImage;
	}

	/**
	 * Sets the filename image.
	 *
	 * @param filenameImage the new filename image
	 */
	public void setFilenameImage(String filenameImage) {
		this.filenameImage = filenameImage;
	}

	/**
	 * Gets the submit image.
	 *
	 * @return the submit image
	 */
	public Button getSubmitImage() {
		return submitImage;
	}

	/**
	 * Sets the submit image.
	 *
	 * @param submitImage the new submit image
	 */
	public void setSubmitImage(Button submitImage) {
		this.submitImage = submitImage;
	}

	/**
	 * Gets the name file.
	 *
	 * @return the name file
	 */
	public String getNameFile() {
		return nameFile;
	}

	/**
	 * Sets the name file.
	 *
	 * @param nameFile the new name file
	 */
	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	/**
	 * Gets the extension file.
	 *
	 * @return the extension file
	 */
	public String getExtensionFile() {
		return extensionFile;
	}

	/**
	 * Sets the extension file.
	 *
	 * @param extensionFile the new extension file
	 */
	public void setExtensionFile(String extensionFile) {
		this.extensionFile = extensionFile;
	}

	/**
	 * Gets the adds the image anchor.
	 *
	 * @return the adds the image anchor
	 */
	public Anchor getAddImageAnchor() {
		return addImageAnchor;
	}

	/**
	 * Sets the adds the image anchor.
	 *
	 * @param addImageAnchor the new adds the image anchor
	 */
	public void setAddImageAnchor(Anchor addImageAnchor) {
		this.addImageAnchor = addImageAnchor;
	}

	/**
	 * Gets the adds the image anchor controls.
	 *
	 * @return the adds the image anchor controls
	 */
	public Controls getAddImageAnchorControls() {
		return addImageAnchorControls;
	}

	/**
	 * Sets the adds the image anchor controls.
	 *
	 * @param addImageAnchorControls the new adds the image anchor controls
	 */
	public void setAddImageAnchorControls(Controls addImageAnchorControls) {
		this.addImageAnchorControls = addImageAnchorControls;
	}

	/**
	 * Gets the survey question model.
	 *
	 * @return the survey question model
	 */
	public SurveyQuestionModel getSurveyQuestionModel() {
		return surveyQuestionModel;
	}

	/**
	 * Sets the survey question model.
	 *
	 * @param surveyQuestionModel the new survey question model
	 */
	public void setSurveyQuestionModel(SurveyQuestionModel surveyQuestionModel) {
		this.surveyQuestionModel = surveyQuestionModel;
	}

	/**
	 * Gets the image file name on database paragraph.
	 *
	 * @return the image file name on database paragraph
	 */
	public Paragraph getImageFileNameOnDatabaseParagraph() {
		return imageFileNameOnDatabaseParagraph;
	}

	/**
	 * Sets the image file name on database paragraph.
	 *
	 * @param imageFileNameOnDatabaseParagraph the new image file name on database paragraph
	 */
	public void setImageFileNameOnDatabaseParagraph(Paragraph imageFileNameOnDatabaseParagraph) {
		this.imageFileNameOnDatabaseParagraph = imageFileNameOnDatabaseParagraph;
	}

	/**
	 * Checks if is image loaded.
	 *
	 * @return true, if is image loaded
	 */
	public boolean isImageLoaded() {
		return imageLoaded;
	}

	/**
	 * Sets the image loaded.
	 *
	 * @param imageLoaded the new image loaded
	 */
	public void setImageLoaded(boolean imageLoaded) {
		this.imageLoaded = imageLoaded;
	}

	/**
	 * Gets the old image name.
	 *
	 * @return the old image name
	 */
	public String getOldImageName() {
		return oldImageName;
	}

	/**
	 * Sets the old image name.
	 *
	 * @param oldImageName the new old image name
	 */
	public void setOldImageName(String oldImageName) {
		this.oldImageName = oldImageName;
	}

	/**
	 * Gets the img.
	 *
	 * @return the img
	 */
	public Image getImg() {
		return img;
	}

	/**
	 * Sets the img.
	 *
	 * @param img the new img
	 */
	public void setImg(Image img) {
		this.img = img;
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
	 * Gets the removes the img button.
	 *
	 * @return the removes the img button
	 */
	public Button getRemoveImgButton() {
		return removeImgButton;
	}

	/**
	 * Sets the removes the img button.
	 *
	 * @param removeImgButton the new removes the img button
	 */
	public void setRemoveImgButton(Button removeImgButton) {
		this.removeImgButton = removeImgButton;
	}

	/**
	 * Gets the curr time.
	 *
	 * @return the curr time
	 */
	public String getCurrTime() {
		return currTime;
	}

	/**
	 * Sets the curr time.
	 *
	 * @param currTime the new curr time
	 */
	public void setCurrTime(String currTime) {
		this.currTime = currTime;
	}

	/**
	 * Gets the row grid flex table.
	 *
	 * @return the row grid flex table
	 */
	public FlexTable getRowGridFlexTable() {
		return rowGridFlexTable;
	}

	/**
	 * Sets the row grid flex table.
	 *
	 * @param rowGridFlexTable the new row grid flex table
	 */
	public void setRowGridFlexTable(FlexTable rowGridFlexTable) {
		this.rowGridFlexTable = rowGridFlexTable;
	}

	/**
	 * Gets the column grid flex table.
	 *
	 * @return the column grid flex table
	 */
	public FlexTable getColumnGridFlexTable() {
		return columnGridFlexTable;
	}

	/**
	 * Sets the column grid flex table.
	 *
	 * @param columnGridFlexTable the new column grid flex table
	 */
	public void setColumnGridFlexTable(FlexTable columnGridFlexTable) {
		this.columnGridFlexTable = columnGridFlexTable;
	}

	/**
	 * Gets the complete gridflex table.
	 *
	 * @return the complete gridflex table
	 */
	public FlexTable getCompleteGridflexTable() {
		return completeGridflexTable;
	}

	/**
	 * Sets the complete gridflex table.
	 *
	 * @param completeGridflexTable the new complete gridflex table
	 */
	public void setCompleteGridflexTable(FlexTable completeGridflexTable) {
		this.completeGridflexTable = completeGridflexTable;
	}

	/**
	 * Gets the row grid.
	 *
	 * @return the row grid
	 */
	public int getRowGrid() {
		return rowGrid;
	}

	/**
	 * Sets the row grid.
	 *
	 * @param rowGrid the new row grid
	 */
	public void setRowGrid(int rowGrid) {
		this.rowGrid = rowGrid;
	}

	/**
	 * Gets the column grid.
	 *
	 * @return the column grid
	 */
	public int getColumnGrid() {
		return columnGrid;
	}

	/**
	 * Sets the column grid.
	 *
	 * @param columnGrid the new column grid
	 */
	public void setColumnGrid(int columnGrid) {
		this.columnGrid = columnGrid;
	}

	/**
	 * Gets the row grid list.
	 *
	 * @return the row grid list
	 */
	public List<MultipleChoiceView> getRowGridList() {
		return rowGridList;
	}

	/**
	 * Sets the row grid list.
	 *
	 * @param rowGridList the new row grid list
	 */
	public void setRowGridList(List<MultipleChoiceView> rowGridList) {
		this.rowGridList = rowGridList;
	}

	/**
	 * Gets the column grid list.
	 *
	 * @return the column grid list
	 */
	public List<MultipleChoiceView> getColumnGridList() {
		return columnGridList;
	}

	/**
	 * Sets the column grid list.
	 *
	 * @param columnGridList the new column grid list
	 */
	public void setColumnGridList(List<MultipleChoiceView> columnGridList) {
		this.columnGridList = columnGridList;
	}

	/**
	 * Gets the id survey.
	 *
	 * @return the id survey
	 */
	public Integer getIdSurvey() {
		return idSurvey;
	}

	/**
	 * Sets the id survey.
	 *
	 * @param idSurvey the new id survey
	 */
	public void setIdSurvey(Integer idSurvey) {
		this.idSurvey = idSurvey;
	}

}
