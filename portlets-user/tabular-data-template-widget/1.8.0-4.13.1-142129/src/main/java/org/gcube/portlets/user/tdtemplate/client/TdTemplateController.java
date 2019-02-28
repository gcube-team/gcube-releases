/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.td.widgetcommonevent.client.event.ExpressionWrapperEvent;
import org.gcube.portlets.user.tdtemplate.client.event.BackToTemplateCreatedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.BackToTemplateCreatedEventHandler;
import org.gcube.portlets.user.tdtemplate.client.event.SaveAsTemplateCreatedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.SaveAsTemplateCreatedEventHandler;
import org.gcube.portlets.user.tdtemplate.client.event.SaveTemplateCreatedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.SaveTemplateCreatedEventHandler;
import org.gcube.portlets.user.tdtemplate.client.event.ShowedReportTemplateCreatedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.ShowedReportTemplateCreatedEventHandler;
import org.gcube.portlets.user.tdtemplate.client.event.TemplateCreatedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.TemplateCreatedEventHandler;
import org.gcube.portlets.user.tdtemplate.client.locale.LocaleViewManager;
import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;
import org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService;
import org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateServiceAsync;
import org.gcube.portlets.user.tdtemplate.client.templateactions.TemplatePanelActionEdit;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.TemplateGenerator;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnDefinitionView;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateFormSwitcherPanel;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.utility.CoordinateCalculator;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.utility.Point;
import org.gcube.portlets.user.tdtemplate.shared.ClientReportTemplateSaved;
import org.gcube.portlets.user.tdtemplate.shared.SPECIAL_CATEGORY_TYPE;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TdFlowModel;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTDataType;
import org.gcube.portlets.user.tdtemplate.shared.TdTTemplateType;
import org.gcube.portlets.user.tdtemplate.shared.TdTTimePeriod;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;
import org.gcube.portlets.user.tdtemplate.shared.validator.ViolationDescription;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public class TdTemplateController implements TdTemplateControllerState{
	
	public static final String CONFIRM = "Confirm?";
	public static final String ALL_POST_ACTIONS_AND_LABELS_CREATED_WILL_BE_CANCELLED = "All post-actions and labels created will be cancelled!!";
	//	private static EventBus privateTaskBus = new SimpleEventBus();
	private static EventBus commonBus = new SimpleEventBus();
	protected TemplateFormSwitcherPanel switcher;
	  // Create a table to layout the content
	protected final LayoutContainer mainPanel = new LayoutContainer();
	protected int width = TdTemplateConstants.WINDOW_WIDTH+5;
	protected int height = TdTemplateConstants.WINDOW_HEIGHT+5;

	protected static TemplateRuleHandler templateRuleHandler;

	public static final TdTemplateServiceAsync tdTemplateServiceAsync = GWT.create(TdTemplateService.class);
	
    protected static TemplateGenerator tdGeneretor;
    protected Window window = new Window();
    
	protected TdTemplatePrivateEventsBinder binder = new TdTemplatePrivateEventsBinder();
	
	protected List<String> listLocales;
	
	protected List<TdTTimePeriod> listPeriodTypes;
	
	private LocaleViewManager localeViewManager;
	
    private EventBus controllerBus = new SimpleEventBus();
    
    protected ToolBar submitTool = new ToolBar();
    
    public static final int MAX_ZINDEX = 1012;
    
    protected String messageBoxTitle = TdTemplateConstants.TEMPLATE_CREATED;
    
    private Map<String, Map<String, String>> timeFormatsIndexer = new HashMap<String, Map<String, String>>(); //periodName - Map Value Formats
    
	/**
	 * The Enum CREATION_STATE.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Mar 26, 2015
	 */
	public static enum CREATION_STATE {TEMPLATE_CREATION, TEMPLATE_UPDATE, POST_OPERATION_CREATE, POST_OPERATION_UPDATE}
	protected TemplateCreationCardLayout cardTemplate;
	protected TdTemplateControllerActions actionsController;
	protected TemplatePanelActionEdit templatePanelActionUpdater;
    /**
	 * 
	 */
	public TdTemplateController() {
		initController();
		loadLocales();
		laodTimePeriods();
		
		controllerBus.addHandler(TemplateCreatedEvent.TYPE, new TemplateCreatedEventHandler() {
			
			@Override
			public void onTemplateCreated(TemplateCreatedEvent templateCreatedEvent) {
				boolean isV = templateCreatedEvent.isValidate();
				boolean isS = templateCreatedEvent.isSave();
				
				List<TdColumnDefinition> columns = getTdColumnDefintions();
				if(isV)
					validateTemplate(columns, isS);
				else
					createTemplate(columns, isS);
			}
		});
		
		controllerBus.addHandler(SaveTemplateCreatedEvent.TYPE, new SaveTemplateCreatedEventHandler() {
			
			@Override
			public void onSaveTemplate(SaveTemplateCreatedEvent saveTemplateCreatedEvent) {
				
				window.mask("Saving template...");
				tdTemplateServiceAsync.saveTemplate(saveTemplateCreatedEvent.isUpdate(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						window.unmask();
						MessageBox.alert(TdTemplateConstants.TEMPLATE_ERROR, caught.getMessage(), null).show();
					}

					@Override
					public void onSuccess(Void result) {
						
						String messageBoxOkMsg = "Template saved successfully";
						Image img = TdTemplateAbstractResources.okicon().createImage();
						DialogResult box = getDialog(img, messageBoxTitle, messageBoxOkMsg);
						box.show();
						
						box.addListener(Events.Hide, new Listener<BaseEvent>() {

							@Override
							public void handleEvent(BaseEvent be) {
								window.hide();
							}
						});
					}
				});
				
			}
		});
		
		controllerBus.addHandler(SaveAsTemplateCreatedEvent.TYPE, new SaveAsTemplateCreatedEventHandler() {
			
			@Override
			public void onSaveAsTemplate(final SaveAsTemplateCreatedEvent saveTemplateCreatedEvent) {
				
				window.mask("Saving template as "+saveTemplateCreatedEvent.getNewTemplateName()+ " ...");
				tdTemplateServiceAsync.saveTemplateAs(saveTemplateCreatedEvent.getNewTemplateName(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						window.unmask();
						MessageBox.alert(TdTemplateConstants.TEMPLATE_ERROR, caught.getMessage(), null).show();
					}

					@Override
					public void onSuccess(Void result) {
						
						String messageBoxOkMsg = "Template "+saveTemplateCreatedEvent.getNewTemplateName()+" saved successfully";
						Image img = TdTemplateAbstractResources.okicon().createImage();
						DialogResult box = getDialog(img, messageBoxTitle, messageBoxOkMsg);
						box.show();
						
						box.addListener(Events.Hide, new Listener<BaseEvent>() {

							@Override
							public void handleEvent(BaseEvent be) {
								window.hide();
							}
						});
					}
				});
				
			}
		});
		
		
		controllerBus.addHandler(ShowedReportTemplateCreatedEvent.TYPE, new ShowedReportTemplateCreatedEventHandler() {
			
			@Override
			public void onShowedReport(ShowedReportTemplateCreatedEvent showedReportTemplateCreatedEvent) {
				
				boolean isS = showedReportTemplateCreatedEvent.isSave();
				
				if(isS)
					window.hide();
				else{
					showTemplateViewToActions();
				}
					
			}
		});
		
		controllerBus.addHandler(BackToTemplateCreatedEvent.TYPE, new BackToTemplateCreatedEventHandler() {
			
			@Override
			public void onBack(BackToTemplateCreatedEvent backToTemplateCreatedEvent) {
				
				MessageBox.confirm(CONFIRM, ALL_POST_ACTIONS_AND_LABELS_CREATED_WILL_BE_CANCELLED, null).addCallback(new Listener<MessageBoxEvent>() {
					
					@Override
					public void handleEvent(MessageBoxEvent be) {
						//IF CANCELLED
						String clickedButton = be.getButtonClicked().getItemId();
						if(clickedButton.equals(Dialog.YES)){
//							activeCard(CREATION_STATE.TEMPLATE_CREATION);
							removeAllActions();
						}
					}
				});
			}
		});
	}
	
	private void removeAllActions(){
		window.mask("Removing Actions..");
		tdTemplateServiceAsync.removeAllActions(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				window.unmask();
				MessageBox.alert(TdTemplateConstants.TEMPLATE_ERROR, caught.getMessage(), null).show();
			}

			@Override
			public void onSuccess(Boolean result) {
				window.unmask();
				activeCard(CREATION_STATE.TEMPLATE_CREATION);
				getTdGeneretor().getTemplatePanel().validateTemplate();
			}
		});
	}
	
	/**
	 * IT'S CALLED ONLY FIRST TIME WHEN TEMPLATE IS CREATED OR UPDATED
	 */
	protected void showTemplateViewToActions(){
		activeCard(CREATION_STATE.POST_OPERATION_CREATE);
		templatePanelActionUpdater.reloadColumnsFromService();
	}
	

    /**
	 * 
	 * @param bus
	 */
	public static void bindCommonBus(EventBus bus){
		commonBus = bus;
		bindCommonEvents();
	}

	public int getWindowZIndex(){
		return window.el().getZIndex();
	}
	
	public int getWindowPositionX(){
		return window.el().getX();
	}
	
	public int  getWindowPositionY(){
		return window.el().getY();
	}

	protected static void bindCommonEvents() {
	
		commonBus.addHandler(ExpressionWrapperEvent.TYPE, new ExpressionWrapperEvent.ExpressionWrapperEventHandler() {
			
			@Override
			public void onExpression(ExpressionWrapperEvent event) {
			
			}
		});
	}
    

	@Override
	public void submitTemplateDefinition(){
		
		List<TdColumnDefinition> columns = getTdColumnDefintions();
		validateTemplate(columns, false);
	}
	

	public void createTemplate(List<TdColumnDefinition> columns, final boolean save){
		
		if(columns!=null && columns.size()>0){
		
			window.mask("Creating template");
			TdFlowModel flowAttached = getTdGeneretor().getTemplatePanel().getFlow();
			
			List<TabularDataAction> actions = actionsController.getActions();
			
			tdTemplateServiceAsync.submitTemplate(columns, flowAttached, save, actions, new AsyncCallback<ClientReportTemplateSaved>() {
	
				@Override
				public void onFailure(Throwable caught) {
					window.unmask();
					MessageBox.alert(TdTemplateConstants.TEMPLATE_ERROR, caught.getMessage(), null).show();
				}
	
				@Override
				public void onSuccess(ClientReportTemplateSaved result) {
					window.unmask();
					String messageBoxOkMsg =save?"Template saved successfully":"Template created successfully";
					showReportTemplateSaved(result, save, messageBoxOkMsg);
				}
			});
		}
		
	}
	
	protected void showReportTemplateSaved(ClientReportTemplateSaved result, final boolean saved, String messageBoxOkMsg){
		DialogResult box = null;
		boolean error = false;
		Image img = null;
		
		if(result.isError() && result.getListErrorColumn()!=null){
			String withError = "with";
			int size = result.getListErrorColumn().size();
			withError+= size>1?" errors":" one error";
			String column = size>1?" columns ":" column ";
			
			messageBoxTitle += " "+withError;
			messageBoxOkMsg = "Skipped the following " +column +":<br/>";
			
			for (TdColumnDefinition tdColumnDefinition : result.getListErrorColumn()) {
				TdTColumnCategory category = tdColumnDefinition.getCategory();
				messageBoxOkMsg+="<br/>*Column "+(tdColumnDefinition.getIndex()+1)+ " ("+category.getName()+") has generated an error";
			}
			
			error = true;
		}else{
			GWT.log("Template created/saved correctly");
		}

		if(error)
			img = TdTemplateAbstractResources.error().createImage();
		else
			img = TdTemplateAbstractResources.okicon().createImage();
		
		box = getDialog(img, messageBoxTitle, messageBoxOkMsg);
		box.show();
		
		box.addListener(Events.Hide, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				controllerBus.fireEvent(new ShowedReportTemplateCreatedEvent(false, saved));
			}
		});
	}
	
	/**
	 * 
	 */
	protected void validateTemplate(final List<TdColumnDefinition> columns, final boolean save) {
		
		List<TdTColumnCategory> listCategory = new ArrayList<TdTColumnCategory>();
		for (TdColumnDefinition tdColumnDefinition : columns) {
			listCategory.add(tdColumnDefinition.getCategory());
		}
		
		window.mask("Validating template");
		
		tdTemplateServiceAsync.isValidTemplate(listCategory, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				window.unmask();
				MessageBox.alert(TdTemplateConstants.TEMPLATE_ERROR, "Sorry, an error occurred when validating template, try again", null).show();
				
			}

			@Override
			public void onSuccess(Boolean result) {
				window.unmask();
				
				if(result){
					createTemplate(columns, save);
				}else{
					showViolations(columns);
				}
				
			}

			
		});

	}
	
	protected void showViolations(final List<TdColumnDefinition> columns) {
		
		tdTemplateServiceAsync.getTemplateConstraintsViolations(new AsyncCallback<List<ViolationDescription>>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert(TdTemplateConstants.TEMPLATE_ERROR, "Sorry, an error occurred on loading template violations, try again", null).show();
				
			}

			@Override
			public void onSuccess(List<ViolationDescription> result) {
				
				if(result.size()==0){
					createTemplate(columns, false);
					return;
				}
				
				String alert ="";
				for (ViolationDescription violationDescription : result)
					alert+="* "+violationDescription.getDescription()+"<br/>";
				
				DialogResult box = getDialog(TdTemplateAbstractResources.alerticon().createImage(), "Template Constraints Violations:", alert);
				box.show();
			}
		});
		
	}
	
	protected DialogResult getDialog(Image img, String title, String msg){
		
		DialogResult box = new DialogResult(img, title, msg);
		box.getElement().getStyle().setZIndex(getWindowZIndex()+1);
		box.show();
		
		Point pointTop = new Point(getWindowPositionX(), getWindowPositionY());
		CoordinateCalculator calculator = new CoordinateCalculator(pointTop, window.getWidth(), window.getHeight(), box.getWidth(), box.getHeight());
		
		if(calculator!=null){
			try{
				Point point = calculator.getViewPoint();
				box.setPosition(point.getX(), point.getY());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		box.hide();
		return box;
	}

	protected List<TdColumnDefinition> getTdColumnDefintions(){
		
		List<ColumnDefinitionView> list = getTdGeneretor().getListColumnDefinition();

		List<TdColumnDefinition> columns = new ArrayList<TdColumnDefinition>(list.size());
		
		GWT.log("Column definitions size is: "+list.size());
		
		for (ColumnDefinitionView cDefView : list) {
			TdColumnDefinition columnDefinition = createTdColumnDefinitionFromView(cDefView);
			columns.add(columnDefinition);
		}
		
		return columns;
	}
	
	public static TdColumnDefinition createTdColumnDefinitionFromView(ColumnDefinitionView cDefView){
		
		TdTDataType dataType = cDefView.getSelectedDataType();
		
		//HAS IT A FORMAT REFERENCE?
		if(dataType.getFormatReferenceIndexer()!=null){
			dataType.setFormatReference(cDefView.getSelectedDataTypeFormat());
		}
		
		String columnName = !cDefView.getColumnName().isEmpty()?cDefView.getColumnName():"Column "+cDefView.getColumnIndex();
		
		TdColumnDefinition columnDefinition = new TdColumnDefinition(cDefView.getColumnIndex(), null, columnName, cDefView.getSelectedColumnCategory(), dataType, cDefView.getSpecialCategoryType());
		
		List<TemplateExpression> rules = cDefView.getRulesExpressions();
		
		GWT.log("RulesExpression is: "+rules);
		if(rules!=null && rules.size()>0){
			GWT.log("setting rules");
			columnDefinition.setRulesExtends(rules);
		}else
			GWT.log("rules is null or empty, skipping");
		
		if(cDefView.getSpecialCategoryType().equals(SPECIAL_CATEGORY_TYPE.NONE)){
//			if(cDefView.getRulesExpression()!=null){
//				columnDefinition.setRulesExtends(cDefView.getRulesExpression());
//			}
//			columnDefinition.setRuleExpression(expression);
		}else if(cDefView.getSpecialCategoryType().equals(SPECIAL_CATEGORY_TYPE.DIMENSION)){
			columnDefinition.setColumnDataReference(cDefView.getReferenceColumnData());
		}else if(cDefView.getSpecialCategoryType().equals(SPECIAL_CATEGORY_TYPE.CODENAME)){
			columnDefinition.setLocale(cDefView.getSelectedLocale());
		}else if(cDefView.getSpecialCategoryType().equals(SPECIAL_CATEGORY_TYPE.TIMEDIMENSION)){
//			System.out.println("***TIMEPERIOD : "+cDefView.getTimePeriod());
			
			//TODO ADD MAP TIME VALUE
			Map<String, String> valueFormats = new HashMap<String, String>();
			valueFormats.put(cDefView.getTimePeriodFormat().getId(), ""); //the formatIdentifier
			
			GWT.log("Value Format : "+cDefView.getTimePeriodFormat() + ", to Time Period: "+cDefView.getTimePeriod());
			columnDefinition.setTimePeriod(new TdTTimePeriod(cDefView.getTimePeriod(),valueFormats));
		}
		
		return columnDefinition;
	}

	/**
	 * 
	 */
	protected void loadLocales() {
		
		TdTemplateController.tdTemplateServiceAsync.getAllowedLocales(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error: "+caught.getMessage());
			}

			@Override
			public void onSuccess(List<String> result) {
				GWT.log("Allowed Locales loaded are: "+result.size());
				if (result != null){
					listLocales = result;
				}
				
			}
		});
	}
	
	
	private void laodTimePeriods(){
		
		TdTemplateController.tdTemplateServiceAsync.getTimeDimensionPeriodTypes(new AsyncCallback<List<TdTTimePeriod>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error: "+caught.getMessage());
				
			}

			@Override
			public void onSuccess(List<TdTTimePeriod> result) {
				GWT.log("Loaded period types are: "+result.size());
				if (result != null){
					listPeriodTypes = result;
					
					for (TdTTimePeriod tdTTimePeriod : listPeriodTypes) {
						timeFormatsIndexer.put(tdTTimePeriod.getName(), tdTTimePeriod.getValueFormats());
					}
					
					printTimeFormats();
				}
			}
		});
	}
	
	private void printTimeFormats(){
		for (String key : timeFormatsIndexer.keySet()) {
			GWT.log("Key: "+key +" value: "+timeFormatsIndexer.get(key).toString());
		}
	}
	
	/**
	 * 
	 * @param periodTypeName
	 * @return
	 */
	public Map<String, String> getValueFormatsForPeriodTypeName(String periodTypeName){
		
//		GWT.log("getValueFormatsForPeriodTypeName for periodTypeName "+periodTypeName);
		return timeFormatsIndexer.get(periodTypeName);	
	}

	protected void initController(){
		
//		initToolbarSubmit(CREATION_STATE.TEMPLATE_CREATION);
//		binder = new TdTemplatePrivateEventsBinder();
		binder.bindEvents(this);
		bindCommonEvents();
		
		switcher = new TemplateFormSwitcherPanel(this);
		mainPanel.add(switcher);
//		window.layout();
		
		tdTemplateServiceAsync.getTemplateTypes(new AsyncCallback<List<TdTTemplateType>>() {
			
			@Override
			public void onSuccess(List<TdTTemplateType> result) {
				switcher.setTemplates(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("An error occurred on recovering template types, "+caught.getMessage());
				displayError("Error", "Sorry an error occurred on contacting the service");
			}
		});
		
		
		tdTemplateServiceAsync.getOnErrorValues(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("An error occurred on recovering on errors, "+caught.getMessage());
				displayError("Error", "Sorry an error occurred on contacting the service");
			}

			@Override
			public void onSuccess(List<String> onErrors) {
				switcher.setOnErrors(onErrors);
				
			}
		});
	}
	
	public void displayError(String title, String error){
		MessageBox.alert("Error", error, null).show();
	}
	
	@Override
	public void doInitTemplate(TemplateSwitcherInteface switcherInterface) {

		this.templatePanelActionUpdater = new TemplatePanelActionEdit(switcher, this);
		this.actionsController = new TdTemplateControllerActions(this, templatePanelActionUpdater);
		this.templatePanelActionUpdater.setActionControlleEventBus(actionsController.getEventBus());
		
		switcher.mask("Creating template", TdTemplateConstants.LOADINGSTYLE);
//		System.out.println("templateType "+switcherInterface.getType());
//		System.out.println("numcol "+switcherInterface.getNumberOfColumns());
		tdGeneretor = new TemplateGenerator(switcherInterface, this);
		tdGeneretor.initTemplatePanel();

		initLocaleManager(switcherInterface);

//		
//		this.templatePanelActionUpdater = new TemplatePanelActionUpdater(switcherInterface, this);
//		this.actionsController = new TdTemplateControllerActions(this, templatePanelActionUpdater);
//		this.templatePanelActionUpdater.setActionControlleEventBus(actionsController.getEventBus());
		
//		switcher.hide();
		
		int index = mainPanel.indexOf(switcher);
		if(index!=-1)
			mainPanel.remove(switcher);
		
		mainPanel.mask("Building template");
		mainPanel.setLayout(new FitLayout());
		
		activeCard(CREATION_STATE.TEMPLATE_CREATION);
		
		window.setHeading(TdTemplateConstants.TEMPLATECREATOR+": " +switcher.getName() +" - Type: "+switcherInterface.getType());
		mainPanel.unmask();
	
	}
	
	protected void activeCard(CREATION_STATE state){
		
		actionsController.reset();
		
		if(templatePanelActionUpdater!=null)
			templatePanelActionUpdater.reset();
		
		TemplateCreationCardLayout card = getCardLayout(state);
		mainPanel.add(card);
		mainPanel.layout(true);
	}
	
	protected TemplateCreationCardLayout getCardLayout(CREATION_STATE state){

		if(cardTemplate==null)
			cardTemplate = new TemplateCreationCardLayout(getTdGeneretor().getTemplatePanel(), templatePanelActionUpdater, window, submitTool, controllerBus);
		
		cardTemplate.changeWizardView(state);

		return cardTemplate;
	}
	
	protected void initLocaleManager(TemplateSwitcherInteface switcherInterface){
		if(switcherInterface.getType().compareTo("CODELIST")==0){
			localeViewManager = new LocaleViewManager(listLocales, true);
		}else
			localeViewManager = new LocaleViewManager(listLocales, false);
	}
	
	
	/**
	 * Use for GXT 2.5
	 * @return
	 */
	public Window getWindowTemplatePanel() {
		
		window.setIcon(TdTemplateAbstractResources.newtemplate());
		window.setResizable(true);
		window.setAnimCollapse(true);
		window.setMaximizable(true);
		window.setHeading(TdTemplateConstants.TEMPLATECREATOR);
		window.setSize((width+20)+"px", (height+40)+"px");
		window.setLayout(new FitLayout());
//		window.setBottomComponent(submitTool);
		window.setBottomComponent(submitTool);
		
//		window.addListener(Events.Render, new Listener<BaseEvent>() {
//
//			@Override
//			public void handleEvent(BaseEvent be) {
//				window.setBottomComponent(submitTool);
//				window.layout(true);
//			}
//		
//		});
	    window.add(mainPanel);
	    window.setScrollMode(Scroll.AUTO);
//	    window.setZIndex(MAX_ZINDEX);
		return window;
	}
	
	protected void go(RootPanel rootPanel){
		
//		final DialogBox dialogBox = getDialogBoxTemplatePanel();
		final Window dialogBox = getWindowTemplatePanel();
		com.google.gwt.user.client.ui.Button openTemplateCreator = new com.google.gwt.user.client.ui.Button("Open Template Creator", new ClickHandler() {
	          public void onClick(ClickEvent event) {
	              dialogBox.show();
	          }
		    });
		rootPanel.add(openTemplateCreator);
	}

	public static EventBus getCommonBus() {
		return commonBus;
	}

	/**
	 * @return the tdGeneretor
	 */
	public static TemplateGenerator getTdGeneretor() {
		return tdGeneretor;
	}

	@Override
	public TemplateRuleHandler getTemplateRuleUpdater() {
		return templateRuleHandler;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.TdTemplateControllerState#setExpressionDialogIndexUpdate(int)
	 */
	@Override
	public void setExpressionDialogIndexesUpdate(TemplateRuleHandler templateRuleIndexer) {
		TdTemplateController.templateRuleHandler = templateRuleIndexer;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.TdTemplateControllerState#doUpdateTemplate()
	 */
	@Override
	public void doUpdateTemplate() {

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.TdTemplateControllerState#getSubmitTool()
	 */
	@Override
	public ToolBar getSubmitTool() {
		return cardTemplate.getSubmitTool();
	}

	public List<String> getAllowedLocales() {
		return listLocales;
	}

	public LocaleViewManager getLocaleViewManager() {
		return localeViewManager;
	}

	public List<TdTTimePeriod> getListPeriodTypes() {
		return listPeriodTypes;
	}
	
    public EventBus getInternalBus(){
    	return binder.getPrivateTaskBus();
    }
	
    /**
	 * @return the templateRuleHandler
	 */
	public static TemplateRuleHandler getTemplateRuleHandler() {
		return templateRuleHandler;
	}

	/**
	 * @param templateRuleHandler the templateRuleHandler to set
	 */
	public static void setTemplateRuleHandler(TemplateRuleHandler templateRuleHandler) {
		TdTemplateController.templateRuleHandler = templateRuleHandler;
	}
}
