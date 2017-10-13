/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;
import org.gcube.portlets.user.tdtemplate.client.templateactions.TemplatePanelActionEdit;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.flow.WindowFlowCreate;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.updater.TemplateUpdaterFormSwitcherPanel;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.updater.TemplateUpdaterGenerator;
import org.gcube.portlets.user.tdtemplate.shared.ClientReportTemplateSaved;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TdFlowModel;
import org.gcube.portlets.user.tdtemplate.shared.TdTTemplateType;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateUpdater;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataActionDescription;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public class TdTemplateControllerUpdater extends TdTemplateController {

	private TemplateUpdaterFormSwitcherPanel switcherUpdater;
	private boolean onErrorLoaded = false;
	private boolean onTemplatesLoaded = false;

	private long templateId;
	private List<TdTTemplateType> templatesType;
	private List<String> errors;
	private TdTemplateControllerUpdater INSTANCE = this;
	
	private TdTemplateUpdater templateUpdater;
//	private TdTemplatePrivateEventsBinder binder;
	
	private TemplateUpdaterGenerator tug;
	
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.TdTemplateController#initController()
	 */
	@Override
	protected void initController() {
		GWT.log("Skypping default init, because is a template updating");
	}
	
	public TdTemplateControllerUpdater(long templateId){
		this.templateId = templateId;
//		initToolbarSubmit(CREATION_STATE.TEMPLATE_UPDATE);
		binder = new TdTemplatePrivateEventsBinder();
		binder.bindEvents(INSTANCE);
		bindCommonEvents();
		
//		window.mask("Loading Data");
		
		switcherUpdater = new TemplateUpdaterFormSwitcherPanel(this);
		super.switcher = switcherUpdater;
		
		mainPanel.add(switcherUpdater);

		tdTemplateServiceAsync.getTemplateTypes(new AsyncCallback<List<TdTTemplateType>>() {
			
			
			@Override
			public void onSuccess(List<TdTTemplateType> result) {
				templatesType = result;
				onTemplatesLoaded = true;
				loadTemplateUpdater();
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("An error occurred on recovering template types, "+caught.getMessage());
				onTemplatesLoaded = true;
				loadTemplateUpdater();
			}
		});
		
		
		tdTemplateServiceAsync.getOnErrorValues(new AsyncCallback<List<String>>() {


			@Override
			public void onFailure(Throwable caught) {
				GWT.log("An error occurred on recovering on errors, "+caught.getMessage());
				onErrorLoaded = true;
				loadTemplateUpdater();
			}

			@Override
			public void onSuccess(List<String> onErrors) {
				errors = onErrors;
				onErrorLoaded = true;
				loadTemplateUpdater();
				
			}
		});
	}

	/**
	 * 
	 */
	protected void loadTemplateUpdater() {
		
		if(onErrorLoaded && onTemplatesLoaded){
		
			tdTemplateServiceAsync.getTemplateUpdaterForTemplateId(templateId, new AsyncCallback<TdTemplateUpdater>() {
	
				@Override
				public void onFailure(Throwable caught) {
//					window.unmask();
					displayError("Error", "Sorry an error occurred on contacting the service");
				}
	
				@Override
				public void onSuccess(TdTemplateUpdater result) {
//					window.unmask();
					GWT.log("Loaded updater: "+result);
					templateUpdater = result;
					TdTemplateDefinition templateDefinition = result.getTemplateDefinition();
					switcherUpdater.setServerId(templateDefinition.getServerId());
					switcherUpdater.setAgency(templateDefinition.getAgency());
					switcherUpdater.setName(templateDefinition.getTemplateName());
					switcherUpdater.setDescription(templateDefinition.getTemplateDescription());
					
					switcherUpdater.setTemplates(templatesType,templateDefinition.getTdTTemplateType());
					
					switcherUpdater.setOnErrors(errors,templateDefinition.getOnError());
					switcherUpdater.setNumberOfColumns(result.getListColumns().size());
					window.layout(true);
					
	//				switcher.setOnErrors(onErrors, select)
					
				}
			});
		
		}
		
	}
	
	@Override
	public void createTemplate(List<TdColumnDefinition> columns, final boolean save){

		if(columns.size()>0){
		
			window.mask("Updating Template Columns");
			
			TdFlowModel flowAttached = tug.getTemplatePanel().getFlow(); //??? TODO
//			ACTION?? //??? TODO
			
			List<TabularDataAction> actions = actionsController.getActions();
			
			tdTemplateServiceAsync.updateTemplate(columns, save, actions, flowAttached, new AsyncCallback<ClientReportTemplateSaved>() {

				@Override
				public void onFailure(Throwable caught) {
					window.unmask();
					MessageBox.alert(TdTemplateConstants.TEMPLATE_ERROR, TdTemplateConstants.TEMPLATE_UPDATE_ERROR, null).show();
				}

				@Override
				public void onSuccess(ClientReportTemplateSaved result) {
				    messageBoxTitle = TdTemplateConstants.TEMPLATE_UPDATED;
					window.unmask();
					
					String messageBoxOkMsg =save?"Template saved successfully":"Template updated successfully";
					showReportTemplateSaved(result, save, messageBoxOkMsg);
				}
			});
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.TdTemplateController#doInitTemplate(org.gcube.portlets.user.tdtemplate.client.template.view.TemplateSwitcherInteface)
	 */
	@Override
	public void doInitTemplate(TemplateSwitcherInteface switcherInterface) {
		this.templatePanelActionUpdater = new TemplatePanelActionEdit(switcherUpdater, this);
		this.actionsController = new TdTemplateControllerActions(this, templatePanelActionUpdater);
		this.templatePanelActionUpdater.setActionControlleEventBus(actionsController.getEventBus());
		
		switcher.mask("Updating template", TdTemplateConstants.LOADINGSTYLE);
		tug = new TemplateUpdaterGenerator(switcherUpdater, INSTANCE);
		tug.initTemplatePanel();
		
		super.initLocaleManager(switcherInterface);
		
		TdTemplateController.tdGeneretor = tug;
		
		TdTemplateController.tdTemplateServiceAsync.getFlowByTemplateId(templateId, new AsyncCallback<TdFlowModel>() {

			@Override
			public void onFailure(Throwable caught) {
				displayError("Error", "Sorry an error occurred on retrieving Flow from service");
			}

			@Override
			public void onSuccess(TdFlowModel result) {
				
				if(result!=null){
					WindowFlowCreate.geInstance().loadFlowData(result);
					tug.setFlowAsReadOnly(true);
				}else
					tug.setFlowAsVisible(false);
			}
		});
		
		int index = mainPanel.indexOf(switcherUpdater);
		
		if(index!=-1)
			mainPanel.remove(switcherUpdater);
		

		mainPanel.mask("Building template");
		mainPanel.setLayout(new FitLayout());
		
		activeCard(CREATION_STATE.TEMPLATE_CREATION);
		window.setHeading(TdTemplateConstants.TEMPLATEUPDATER+": " +switcher.getName() +" - Type: "+switcherInterface.getType());
//		window.layout(true);
		mainPanel.unmask();
	
	}
	
	/**
	 * Use for GXT 2.5
	 * @return
	 */
	@Override
	public Window getWindowTemplatePanel() {
		
		window.setIcon(TdTemplateAbstractResources.newtemplate());
		window.setResizable(true);
		window.setAnimCollapse(true);
		window.setMaximizable(true);
		window.setHeading(TdTemplateConstants.TEMPLATEUPDATER);
		window.setSize((width+20)+"px", (height+40)+"px");
		window.setLayout(new FitLayout());
		window.setBottomComponent(submitTool);
	    window.add(mainPanel);
	    window.setScrollMode(Scroll.AUTO);
		return window;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.TdTemplateControllerState#doUpdateTemplate()
	 */
	@Override
	public void doUpdateTemplate() {
		
		tug.getUpdater().setColumns(templateUpdater.getListColumns());
		
		//ARE THERE ACTIONS?
		List<TabularDataActionDescription> actionDescriptions = templateUpdater.getTabularDataActionDescription();
		
		if(actionDescriptions!=null && actionDescriptions.size()>0){
			String alertMessage = "The template \""+switcherUpdater.getName() +"\" has one or more columns created by";
			
			if(actionDescriptions.size()>1)
				alertMessage+= " post-action.";
			else
				alertMessage+= " post-actions.";
			
			alertMessage+= " Either you add new actions or press \"back\" to modify the template base columns";
			
			showTemplateViewToActions();
			
			//CONVETING ACTIONS
			ArrayList<TabularDataAction> listActions = new ArrayList<TabularDataAction>();
			for (final TabularDataActionDescription tabularDataActionDescription : actionDescriptions) {
				TabularDataAction action = new TabularDataAction() {
					
					@Override
					public String getId() {
						return tabularDataActionDescription.getId();
					}
					
					@Override
					public String getDescription() {
						return tabularDataActionDescription.getDescription();
					}
				};
				listActions.add(action);
			}
			
			actionsController.setActions(listActions);
			String msg = TdTemplateConstants.LATEST_OPERATION+ ": performed operation "+listActions.get(listActions.size()-1).getDescription()+"";
			actionsController.refreshLastOperationLabel(msg, "");
			templatePanelActionUpdater.enableUndoLastOperation(true); //ENABLING UNDO
	
//			MessageBox.info("Template Actions notification!", alertMessage, null);
			
			DialogResult dialog = getDialog(null, "Template Actions notification!", alertMessage);
//			DialogResult dialog = new DialogResult(null, "Template Actions notification!", alertMessage);
			dialog.getElement().getStyle().setZIndex(getWindowZIndex()+50);
			dialog.show();
		}
		
		cardTemplate.setIsUpdate(true);
//		tug.getTemplatePanel().getPanel().layout(true);
		mainPanel.layout(true);
	}

}
