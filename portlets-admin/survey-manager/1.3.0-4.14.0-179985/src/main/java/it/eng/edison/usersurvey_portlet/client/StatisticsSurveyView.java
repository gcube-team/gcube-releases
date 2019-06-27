package it.eng.edison.usersurvey_portlet.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;

import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyUserAnswerModel;
	
/**
 * The Class StatisticsSurveyView.
 */
public class StatisticsSurveyView extends Composite {

	/** The ui binder. */
	private static StatisticsSurveyViewUiBinder uiBinder = GWT.create(StatisticsSurveyViewUiBinder.class);
	
	/** The greeting service. */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/**
	 * The Interface StatisticsSurveyViewUiBinder.
	 */
	interface StatisticsSurveyViewUiBinder extends UiBinder<Widget, StatisticsSurveyView> {
	}

	/** The horizontal panel. */
	@UiField HorizontalPanel horizontalPanel;
	
	/** The vertical panel users. */
	@UiField VerticalPanel verticalPanel, verticalPanelUsers;
	
	/** The export to CSV button. */
	@UiField Button backToHomeSurveyButton, exportToCSVButton;
	
	/** The title survey heading. */
	@UiField Heading titleSurveyHeading;
	
	/** The description aggregate stats paragraph. */
	@UiField Paragraph descriptionAggregateStatsParagraph;
	
	/** The well form answer. */
	@UiField WellForm wellFormAnswer;
	
	/** The survey home page. */
	private SurveyHomePage surveyHomePage;
	
	/** The survey user answer model list. */
	private List<SurveyUserAnswerModel> surveyUserAnswerModelList;
	
	/** The survey question model list. */
	private List<SurveyQuestionModel> surveyQuestionModelList;
	
	/** The pie chart. */
	private PieChart pieChart;
	
	/** The survey question model. */
	private SurveyQuestionModel surveyQuestionModel;
    
    /** The flow panel. */
    private FlowPanel flowPanel;
    
    /** The options. */
    private PieChartOptions options;
    
    /** The number question. */
    private int numberQuestion;
    
    /** The data table. */
    private DataTable dataTable;
    
    /** The user DTO list. */
    private List<UserDTO> userDTOList;
    
    /** The number of members filled survey. */
    private int idSurveySelected, numberOfMembersFilledSurvey;
    
    /** The title survey. */
    private String titleSurvey;
    
    /** The user DTO. */
    private UserDTO userDTO;
    
    /** The anchor. */
    private Anchor anchor;
    
    /** The flex table general. */
    private FlexTable flexTableGeneral;
    
    /** The flex table users. */
    private FlexTable flexTableUsers;
    
    /** The flex table charts. */
    private FlexTable flexTableCharts;
    
    /** The flex table open questions. */
    private FlexTable flexTableOpenQuestions;
    
    /** The flex table open question box user and answer. */
    private FlexTable flexTableOpenQuestionBoxUserAndAnswer; 
    
    /** The html title column. */
    private HTML htmlTitleColumn;
    
    /** The is anonymous. */
    private boolean isAnonymous;
	
	/** The index guest. */
	private int indexGuest;
	
	/** The frequency map. */
	private Map<String,Integer> frequencyMap;
	
	/** The frequency user id. */
	private HashMap<Integer,Integer> frequencyUserId;
	
	/** The paragraph. */
	private Paragraph paragraph;
	
	/** The paragraph usr. */
	private Paragraph paragraphUsr;
	
	/** The paragraph ans. */
	private Paragraph paragraphAns;
	
	/** The paragraph questions. */
	private Paragraph paragraphQuestions;
	
	/** The paragraph section. */
	private Paragraph paragraphSection;
	
	/** The paragraph answer limit. */
	private Paragraph paragraphAnswerLimit;
	
	/** The flow panel see more. */
	private FlowPanel flowPanelSeeMore;
	
	/** The row current. */
	int rowCurrent = 0;
	
	/** The Constant OPEN_QUESTION_LIMIT_CHAR. */
	private static final int OPEN_QUESTION_LIMIT_CHAR = 200;

	/** The Constant UNTITLED_SECTION. */
	private static final String UNTITLED_SECTION = "Untitled Section";
	
    

	/**
	 * Instantiates a new statistics survey view.
	 */
	public StatisticsSurveyView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * Instantiates a new statistics survey view.
	 *
	 * @param idSurveySelected the id survey selected
	 * @param titleSurvey the title survey
	 * @param isAnonymous the is anonymous
	 * @param numberOfMembersFilledSurvey the number of members filled survey
	 * @param surveyUserAnswerModelList the survey user answer model list
	 */
	public StatisticsSurveyView(int idSurveySelected, String titleSurvey, Boolean isAnonymous, int numberOfMembersFilledSurvey, List<SurveyUserAnswerModel> surveyUserAnswerModelList) {
		initWidget(uiBinder.createAndBindUi(this));
		
		flowPanel = new FlowPanel(ParagraphElement.TAG);
		
		titleSurveyHeading.setVisible(true);
		titleSurveyHeading.setText("\"" + titleSurvey + "\"");
		
		if(numberOfMembersFilledSurvey == 1){
			descriptionAggregateStatsParagraph.setText("Aggregate results for " + numberOfMembersFilledSurvey + " VRE member");
		} else {
			descriptionAggregateStatsParagraph.setText("Aggregate results for " + numberOfMembersFilledSurvey + " VRE members");
		}
		descriptionAggregateStatsParagraph.addStyleName("descriptionAggregateStatsParagraph");
		flexTableGeneral = new FlexTable();
		flexTableGeneral.addStyleName("SurveyStyling");
		flexTableUsers = new FlexTable();
		flexTableCharts = new FlexTable();
		flexTableOpenQuestions = new FlexTable();
		
		htmlTitleColumn = new HTML("<h3>List of VRE members</h3>", true);
		htmlTitleColumn.addStyleName("htmlTitleColumnStatistics");
		this.idSurveySelected = idSurveySelected;
		this.titleSurvey = titleSurvey;
		this.isAnonymous = isAnonymous;
		this.numberOfMembersFilledSurvey = numberOfMembersFilledSurvey;
		this.surveyUserAnswerModelList = surveyUserAnswerModelList;
		verticalPanel.add(wellFormAnswer);
		verticalPanel.add(descriptionAggregateStatsParagraph);
		RootPanel.get("survey-div").add(verticalPanel);
		RootPanel.get("survey-div").add(titleSurveyHeading);
		
		greetingService.getUserAnsweredSurvey(idSurveySelected, new AsyncCallback<List<UserDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<UserDTO> result) {
				setUserDTOList(result);
				int guestIndex = 0;
				for(int i=0; i<userDTOList.size(); i++){
					guestIndex = i+1;
					verticalPanelUsers = new VerticalPanel();
					horizontalPanel = new HorizontalPanel();
					
					final UserDTO userDTO = userDTOList.get(i);
					Anchor anchor = null;
					paragraph = new Paragraph();
					paragraph.addStyleName("data");

					if(!isAnonymous()){
						anchor = new Anchor(userDTO.getFullName());
					} else {
						anchor = new Anchor("Guest " + guestIndex);
					}
					
					anchor.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							RootPanel.get("survey-div").clear();
							StatisticsUserAnswersView statisticsUserAnswersView = new StatisticsUserAnswersView(userDTO.getFullName(), userDTO.getUserId(), getIdSurveySelected(), getTitleSurvey(), isAnonymous(), getNumberOfMembersFilledSurvey() , surveyQuestionModelList, getSurveyUserAnswerModelList(), userDTO);
							RootPanel.get("survey-div").add(statisticsUserAnswersView);
						}
					});
					paragraph.add(anchor);
					flexTableUsers.setWidget(i, 0, paragraph);
					if((i % 2) == 0){
						flexTableUsers.getCellFormatter().addStyleName(i, 0, "AnchorListOfVREMembersEvenRows");
					} else {
						flexTableUsers.getCellFormatter().addStyleName(i, 0, "AnchorListOfVREMembersOddRows");
					}
				}
				flowPanel.add(flexTableUsers);
				flexTableGeneral.setWidget(0, 2, htmlTitleColumn);
				flexTableGeneral.setWidget(1, 2, flowPanel);
				
				getQuestionSurvey();
			}
		});



	}

	/**
	 * Gets the question survey.
	 *
	 * @return the question survey
	 */
	private void getQuestionSurvey() {

		greetingService.getQuestionsSurvey(idSurveySelected, new AsyncCallback<List<SurveyQuestionModel>>() {

			@Override
			public void onSuccess(List<SurveyQuestionModel> result) {
				setSurveyQuestionModelList(result);
				drawOpenQuestionsAndAddToPanel();
				fillInformationIntoPieChart();

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
	 * Back to homepage.
	 */
	private void backToHomepage(){
		  RootPanel.get("survey-div").clear();
		  surveyHomePage = new SurveyHomePage();
		  RootPanel.get("survey-div").add(surveyHomePage);
	}
	
	/**
	 * On click export to CSV button button.
	 *
	 * @param event the event
	 */
	@UiHandler("exportToCSVButton")
	void onClickExportToCSVButtonButton(ClickEvent event){
					
		greetingService.exportToCSVFile(titleSurvey, isAnonymous(), getUserDTOList(), getSurveyUserAnswerModelList(), getSurveyQuestionModelList(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("The file cannot be opened created!");
			}

			@Override
			public void onSuccess(Void result) {
				 Window.Location.replace(GWT.getModuleBaseURL()+"exportCSV?titleSurvey="+titleSurvey);
//					final FormPanel form = new FormPanel();
//					form.setAction(GWT.getModuleBaseURL()+"exportCSV");
//					form.submit();
//					form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
//						@Override
//						public void onSubmitComplete(SubmitCompleteEvent event) {
//						}
//					}); 
			}
		});
	}
	
	
	/**
	 * Draw open questions and add to panel.
	 */
	private void drawOpenQuestionsAndAddToPanel(){
		
		HashMap<Integer,Integer> frequencyUserId = new HashMap<Integer,Integer>();
		for(int ii = 0; ii < getSurveyUserAnswerModelList().size(); ii++){
			for(int j = 0; j < getUserDTOList().size(); j++){
				Integer userIdDTO = (int)getUserDTOList().get(j).getUserId();
				Integer idUserAnswer = (int)getSurveyUserAnswerModelList().get(ii).getIduseranswer();
				
				if(userIdDTO.equals(idUserAnswer)){
					if(frequencyUserId.containsKey(userIdDTO)){
						frequencyUserId.put(userIdDTO, frequencyUserId.get(userIdDTO)+1);
					} else {
						frequencyUserId.put(userIdDTO, 1);
					}
				}
			}
		}
			
		htmlTitleColumn = new HTML("<h3>Open Questions</h3>", true);
		htmlTitleColumn.addStyleName("htmlTitleColumnStatistics");
		String fullNameUserAnswer = ""; 
		flexTableOpenQuestions = new FlexTable();
		Paragraph paragraphSection = null;
		
		flexTableOpenQuestionBoxUserAndAnswer = new FlexTable();
		for(int i=0; i<getSurveyQuestionModelList().size(); i++){
			if(getSurveyQuestionModelList().get(i).getQuestiontype() == "Text" 
					|| getSurveyQuestionModelList().get(i).getQuestiontype() == "Paragraph Text" ){
				final FlexTable flexTableOpenQuestionBoxUserAndAnswer = new FlexTable();
				
				// Insert section into Open Questions Column
				if(getSurveyQuestionModelList().get(i).getSectionTitle() != null
						&& !getSurveyQuestionModelList().get(i).getSectionTitle().equalsIgnoreCase("")
						&& !getSurveyQuestionModelList().get(i).getSectionTitle().equalsIgnoreCase(UNTITLED_SECTION)){
					paragraphSection = new Paragraph(getSurveyQuestionModelList().get(i).getSectionTitle());
					paragraphSection.addStyleName("sectionStripe");
				}
			
				paragraphQuestions = new Paragraph(getSurveyQuestionModelList().get(i).getQuestion());
				paragraphQuestions.addStyleName("flexTableOpenTitleQuestionBoxStatistics");
				flowPanel = new FlowPanel();
				int rowStyle = 0;
				
				for(int j=0; j<getSurveyUserAnswerModelList().size(); j++){
					if(getSurveyQuestionModelList().get(i).getNumberquestion() == getSurveyUserAnswerModelList().get(j).getNumberquestion()){
						final int row = j;
						
						/* Substitute user ID with user fullName*/
						for(int k=0; k<getUserDTOList().size(); k++){
							if(getUserDTOList().get(k).getUserId() == getSurveyUserAnswerModelList().get(j).getIduseranswer()){
								
								if(!isAnonymous()){
									fullNameUserAnswer = getUserDTOList().get(k).getFullName();
								}else {
									fullNameUserAnswer = "Guest";
								}
								paragraphUsr = new Paragraph(fullNameUserAnswer);
								paragraphUsr.addStyleName("data");
								flexTableOpenQuestionBoxUserAndAnswer.setWidget(row, 0, paragraphUsr);
								final String answer = getSurveyUserAnswerModelList().get(j).getAnswer1();
								
								//paragraph and see more panel
								FlowPanel flowPanelSeeMore = new FlowPanel();
								if(answer.length() > OPEN_QUESTION_LIMIT_CHAR){
									String limitOpenQuestionCharacter = answer.substring(0, OPEN_QUESTION_LIMIT_CHAR);
								
									paragraphAns = new Paragraph(limitOpenQuestionCharacter);
									paragraphAns.addStyleName("data");
									
									//ANCHOR
									Anchor seeMoreAnchor = new Anchor(" See more");
									seeMoreAnchor.addClickHandler(new ClickHandler() {
										
										@Override
										public void onClick(ClickEvent event) {
											rowCurrent = flexTableOpenQuestionBoxUserAndAnswer.getCellForEvent(event).getRowIndex();
											paragraphAns = new Paragraph(answer);
											paragraphAns.addStyleName("data");
											flexTableOpenQuestionBoxUserAndAnswer.setWidget(rowCurrent, 1, paragraphAns);
										}
									});
									
									flowPanelSeeMore.add(paragraphAns);
									flowPanelSeeMore.add(seeMoreAnchor);
									flexTableOpenQuestionBoxUserAndAnswer.setWidget(row, 1, flowPanelSeeMore);
								} else {
									
									paragraphAns = new Paragraph(getSurveyUserAnswerModelList().get(j).getAnswer1());
									paragraphAns.addStyleName("data");
									flexTableOpenQuestionBoxUserAndAnswer.setWidget(row, 1, paragraphAns);

								}
							} 
						}

						if((rowStyle % 2) == 0){
							flexTableOpenQuestionBoxUserAndAnswer.getCellFormatter().addStyleName(row, 0, "flexTableOpenQuestionBoxStatisticsEvenRows");
							flexTableOpenQuestionBoxUserAndAnswer.getCellFormatter().addStyleName(row, 1, "flexTableOpenQuestionBoxStatisticsEvenRows");
						} else {
							flexTableOpenQuestionBoxUserAndAnswer.getCellFormatter().addStyleName(row, 0, "flexTableOpenQuestionBoxStatisticsOddRows");
							flexTableOpenQuestionBoxUserAndAnswer.getCellFormatter().addStyleName(row, 1, "flexTableOpenQuestionBoxStatisticsOddRows");
						}
						rowStyle++;
						
						if(paragraphSection != null
								&& !paragraphSection.getText().isEmpty()
								&& !paragraphSection.getText().equalsIgnoreCase("")
								&& !paragraphSection.getText().equalsIgnoreCase(UNTITLED_SECTION)){
							flowPanel.add(paragraphSection);
						}
						
						flowPanel.add(paragraphQuestions);
						flowPanel.add(flexTableOpenQuestionBoxUserAndAnswer);
						
					}
				}
				
				flexTableOpenQuestions.setWidget(i, 0, flowPanel);

			}
		}
		flexTableGeneral.setWidget(0, 1, htmlTitleColumn);
		flexTableGeneral.setWidget(1, 1, flexTableOpenQuestions);
		RootPanel.get("survey-div").add(flexTableGeneral);
	}
	
	
	
	/**
	 * Fill information into pie chart.
	 */
	private void fillInformationIntoPieChart() {
		// Create the API Loader
		ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
		chartLoader.loadApi(new Runnable() {

			@Override
			public void run() {
				drawPieChart();
			}
		});		
	}
	
	private void fillInformationIntoBarChart() {
		// Create the API Loader
		ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
		chartLoader.loadApi(new Runnable() {

			@Override
			public void run() {
				drowGridBarChart();
			}
		});		
	}
	
	

	/**
	 * Count answer scale map.
	 *
	 * @param numberQuestion the number question
	 * @return the map
	 */
	private Map<String,Integer> countAnswerScaleMap(int numberQuestion) {
		HashMap<String,Integer> frequencyMap = new HashMap<String,Integer>();
		for(int i=0; i<surveyUserAnswerModelList.size(); i++){
			if((surveyUserAnswerModelList.get(i).getNumberquestion() == numberQuestion) 
					&& (surveyUserAnswerModelList.get(i).getQuestiontype() != null)
					&& (surveyUserAnswerModelList.get(i).getAnswer1() != null)){
				if(surveyUserAnswerModelList.get(i).getQuestiontype().contains("Scale")){
					if(frequencyMap.containsKey(surveyUserAnswerModelList.get(i).getAnswer1())){
						frequencyMap.put(surveyUserAnswerModelList.get(i).getAnswer1(), frequencyMap.get(surveyUserAnswerModelList.get(i).getAnswer1())+1);
					} else {
						 frequencyMap.put(surveyUserAnswerModelList.get(i).getAnswer1(), 1);
					}
				}
			}
		}
		
		return frequencyMap;
	}
	
	/**
	 * Count answers multiple choice and drop down.
	 *
	 * @param numberQuestion the number question
	 * @param surveyQuestionModel the survey question model
	 * @return the map
	 */
	private Map<String,Integer> countAnswersMultipleChoiceAndDropDown(int numberQuestion, SurveyQuestionModel surveyQuestionModel) {
		HashMap<String,Integer> frequencyMap = new HashMap<String,Integer>();
		for(int i=0; i<surveyUserAnswerModelList.size(); i++){
			
			if((surveyUserAnswerModelList.get(i).getNumberquestion() == numberQuestion) 
					&& (surveyUserAnswerModelList.get(i).getQuestiontype() != null)
					&& (surveyQuestionModel.getMultipleChoiceList() != null)){
				if(!surveyUserAnswerModelList.get(i).getQuestiontype().contains("CheckBoxes")){
					if(surveyQuestionModel.getMultipleChoiceList().contains(surveyUserAnswerModelList.get(i).getAnswer1())){
						if(frequencyMap.containsKey(surveyUserAnswerModelList.get(i).getAnswer1())){
							frequencyMap.put(surveyUserAnswerModelList.get(i).getAnswer1(), frequencyMap.get(surveyUserAnswerModelList.get(i).getAnswer1())+1);
						} else {
							 frequencyMap.put(surveyUserAnswerModelList.get(i).getAnswer1(), 1);
						}
					} 
				}else {
					for(int j=0; j<surveyQuestionModel.getMultipleChoiceList().size(); j++){
						if((surveyQuestionModel.getMultipleChoiceList() != null) 
								&& (surveyQuestionModel.getMultipleChoiceList().get(j) != null)){
							if(surveyUserAnswerModelList.get(i).getMultipleChoiceList().contains(surveyQuestionModel.getMultipleChoiceList().get(j))){
								if(frequencyMap.containsKey(surveyQuestionModel.getMultipleChoiceList().get(j))){
									frequencyMap.put(surveyQuestionModel.getMultipleChoiceList().get(j), frequencyMap.get(surveyQuestionModel.getMultipleChoiceList().get(j))+1);
								} else {
									frequencyMap.put(surveyQuestionModel.getMultipleChoiceList().get(j), 1);
								}

							}
						}
					}
				}
			} 
		}
		return frequencyMap;
	}
	
	/**
	 * Draw pie chart.
	 */
	private void drawPieChart() {
		
		for(int i=0; i<surveyQuestionModelList.size(); i++){
			setNumberQuestion(0);
			if(surveyQuestionModelList.get(i).getQuestiontype() == "Multiple Choice" || surveyQuestionModelList.get(i).getQuestiontype() == "Drop-Down"){

				surveyQuestionModel = new SurveyQuestionModel();
				surveyQuestionModel = surveyQuestionModelList.get(i);
				setNumberQuestion(surveyQuestionModel.getNumberquestion());
				setFrequencyMap(countAnswersMultipleChoiceAndDropDown(numberQuestion, surveyQuestionModel));

				pieChart = new PieChart();
				flowPanel = new FlowPanel(ParagraphElement.TAG);
				flowPanel.addStyleName("flowPanelStatisticsSurveyView");
				flowPanel.setHeight("200px");
				flowPanel.setWidth("350px");
				verticalPanel = new VerticalPanel();
				horizontalPanel = new HorizontalPanel();

				newDataTableValues(getFrequencyMap());
				drowChartAndAddToPanel(i);

			} else if (surveyQuestionModelList.get(i).getQuestiontype() == "CheckBoxes"){
				surveyQuestionModel = new SurveyQuestionModel();
				surveyQuestionModel = surveyQuestionModelList.get(i);
				setNumberQuestion(surveyQuestionModel.getNumberquestion());
				setFrequencyMap(countAnswersMultipleChoiceAndDropDown(numberQuestion, surveyQuestionModel));
				
				pieChart = new PieChart();
				flowPanel = new FlowPanel(ParagraphElement.TAG);
				flowPanel.addStyleName("flowPanelStatisticsSurveyView");
				flowPanel.setHeight("200px");
				flowPanel.setWidth("350px");
				verticalPanel = new VerticalPanel();
				horizontalPanel = new HorizontalPanel();

				newDataTableValues(getFrequencyMap());
				drowChartAndAddToPanel(i);

			} else if (surveyQuestionModelList.get(i).getQuestiontype() == "Scale"){
				surveyQuestionModel = new SurveyQuestionModel();
				surveyQuestionModel = surveyQuestionModelList.get(i);
				setNumberQuestion(surveyQuestionModel.getNumberquestion());
				setFrequencyMap(countAnswerScaleMap(numberQuestion));
				
				pieChart = new PieChart();
				flowPanel = new FlowPanel(ParagraphElement.TAG);
				flowPanel.addStyleName("flowPanelStatisticsSurveyView");
				flowPanel.setHeight("200px");
				flowPanel.setWidth("350px");
				verticalPanel = new VerticalPanel();
				horizontalPanel = new HorizontalPanel();

				newDataTableValues(getFrequencyMap());
				drowChartAndAddToPanel(i);
				
			}
		}
	}
	
	private void drowGridBarChart(){
		for(int i=0; i<surveyQuestionModelList.size(); i++){
			setNumberQuestion(0);
			if (surveyQuestionModelList.get(i).getQuestiontype() == "Grid"){
				
			}
		}
	}
	
	/**
	 * Drow chart and add to panel.
	 *
	 * @param i the i
	 */
	private void drowChartAndAddToPanel(int i) {
		htmlTitleColumn = new HTML("<h3>Charts</h3>", true);
		htmlTitleColumn.addStyleName("htmlTitleColumnStatistics");
		Paragraph paragraphSection = null;
		
		// Insert section into Open Questions Column
		if(getSurveyQuestionModelList().get(i).getSectionTitle() != null
				&& !getSurveyQuestionModelList().get(i).getSectionTitle().equalsIgnoreCase("")
				&& !getSurveyQuestionModelList().get(i).getSectionTitle().isEmpty()
				&& !getSurveyQuestionModelList().get(i).getSectionTitle().equalsIgnoreCase(UNTITLED_SECTION)){
			paragraphSection = new Paragraph(getSurveyQuestionModelList().get(i).getSectionTitle());
			paragraphSection.addStyleName("sectionStripe");
		}
		
		options = PieChartOptions.create();
		if(surveyQuestionModel.getQuestion() != null && !surveyQuestionModel.getQuestion().isEmpty()){
			options.setTitle(surveyQuestionModel.getQuestion());
		}else{
			options.setTitle(surveyQuestionModel.getAnswer3());
		}
		
		// Draw the chart
		pieChart.draw(dataTable, options);
		if(paragraphSection != null
				&& !paragraphSection.getText().isEmpty()
				&& !paragraphSection.getText().equalsIgnoreCase("")
				&& !paragraphSection.getText().equalsIgnoreCase(UNTITLED_SECTION)){
			flowPanel.add(paragraphSection);
		}
		flowPanel.add(pieChart);
		flexTableCharts.setWidget(i, 0, flowPanel);
		flexTableGeneral.setWidget(0, 0, htmlTitleColumn);
		flexTableGeneral.setWidget(1, 0, flexTableCharts);
		RootPanel.get("survey-div").add(flexTableGeneral);

	}
	
	/**
	 * New data table values.
	 *
	 * @param frequencyMap the frequency map
	 */
	private void newDataTableValues(Map<String,Integer> frequencyMap){
		dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Question");
		dataTable.addColumn(ColumnType.NUMBER, "Options");
		dataTable.addRows(frequencyMap.size());

		int i = 0;		
		for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
			dataTable.setValue(i, 0, entry.getKey().trim());
			dataTable.setValue(i, 1, entry.getValue().toString().trim());
			i++;
		}

	}
		
	/**
	 * Gets the pie chart.
	 *
	 * @return the pie chart
	 */
	public PieChart getPieChart() {
		return pieChart;
	}

	/**
	 * Sets the pie chart.
	 *
	 * @param pieChart the new pie chart
	 */
	public void setPieChart(PieChart pieChart) {
		this.pieChart = pieChart;
	}

	/**
	 * Gets the survey user answer model list.
	 *
	 * @return the survey user answer model list
	 */
	public List<SurveyUserAnswerModel> getSurveyUserAnswerModelList() {
		return surveyUserAnswerModelList;
	}

	/**
	 * Sets the survey user answer model list.
	 *
	 * @param surveyUserAnswerModelList the new survey user answer model list
	 */
	public void setSurveyUserAnswerModelList(List<SurveyUserAnswerModel> surveyUserAnswerModelList) {
		this.surveyUserAnswerModelList = surveyUserAnswerModelList;
	}

	/**
	 * Gets the survey question model list.
	 *
	 * @return the survey question model list
	 */
	public List<SurveyQuestionModel> getSurveyQuestionModelList() {
		return surveyQuestionModelList;
	}

	/**
	 * Sets the survey question model list.
	 *
	 * @param surveyQuestionModelList the new survey question model list
	 */
	public void setSurveyQuestionModelList(List<SurveyQuestionModel> surveyQuestionModelList) {
		this.surveyQuestionModelList = surveyQuestionModelList;
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
	 * Gets the options.
	 *
	 * @return the options
	 */
	public PieChartOptions getOptions() {
		return options;
	}

	/**
	 * Sets the options.
	 *
	 * @param options the new options
	 */
	public void setOptions(PieChartOptions options) {
		this.options = options;
	}

	/**
	 * Gets the number question.
	 *
	 * @return the number question
	 */
	public int getNumberQuestion() {
		return numberQuestion;
	}

	/**
	 * Sets the number question.
	 *
	 * @param numberQuestion the new number question
	 */
	public void setNumberQuestion(int numberQuestion) {
		this.numberQuestion = numberQuestion;
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
	 * Gets the anchor.
	 *
	 * @return the anchor
	 */
	public Anchor getAnchor() {
		return anchor;
	}

	/**
	 * Sets the anchor.
	 *
	 * @param anchor the new anchor
	 */
	public void setAnchor(Anchor anchor) {
		this.anchor = anchor;
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
	 * Gets the user DTO list.
	 *
	 * @return the user DTO list
	 */
	public List<UserDTO> getUserDTOList() {
		return userDTOList;
	}

	/**
	 * Sets the user DTO list.
	 *
	 * @param userDTOList the new user DTO list
	 */
	public void setUserDTOList(List<UserDTO> userDTOList) {
		this.userDTOList = userDTOList;
	}

	/**
	 * Gets the index guest.
	 *
	 * @return the index guest
	 */
	public int getIndexGuest() {
		return indexGuest;
	}

	/**
	 * Sets the index guest.
	 *
	 * @param indexGuest the new index guest
	 */
	public void setIndexGuest(int indexGuest) {
		this.indexGuest = indexGuest;
	}

	/**
	 * Gets the frequency map.
	 *
	 * @return the frequency map
	 */
	public Map<String, Integer> getFrequencyMap() {
		return frequencyMap;
	}

	/**
	 * Sets the frequency map.
	 *
	 * @param frequencyMap the frequency map
	 */
	public void setFrequencyMap(Map<String, Integer> frequencyMap) {
		this.frequencyMap = frequencyMap;
	}

	/**
	 * Gets the number of members filled survey.
	 *
	 * @return the number of members filled survey
	 */
	public int getNumberOfMembersFilledSurvey() {
		return numberOfMembersFilledSurvey;
	}

	/**
	 * Sets the number of members filled survey.
	 *
	 * @param numberOfMembersFilledSurvey the new number of members filled survey
	 */
	public void setNumberOfMembersFilledSurvey(int numberOfMembersFilledSurvey) {
		this.numberOfMembersFilledSurvey = numberOfMembersFilledSurvey;
	}

	 
}

