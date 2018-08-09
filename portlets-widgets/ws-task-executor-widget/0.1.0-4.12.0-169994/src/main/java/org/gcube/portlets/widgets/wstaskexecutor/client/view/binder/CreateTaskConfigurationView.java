package org.gcube.portlets.widgets.wstaskexecutor.client.view.binder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.workspacetaskexecutor.shared.FilterOperator;
import org.gcube.common.workspacetaskexecutor.shared.TaskOperator;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameter;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameterType;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.portlets.widgets.wstaskexecutor.client.WsTaskExecutorWidget;
import org.gcube.portlets.widgets.wstaskexecutor.client.dialog.DialogResult;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.DeleteCustomFieldEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.DeleteCustomFieldEventHandler;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.ShowListOfTaskConfigurationsEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.view.LoaderIcon;
import org.gcube.portlets.widgets.wstaskexecutor.shared.GcubeScope;
import org.gcube.portlets.widgets.wstaskexecutor.shared.GcubeScopeType;
import org.gcube.portlets.widgets.wstaskexecutor.shared.SelectableOperator;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.Fieldset;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Pager;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class CreateTaskConfigurationView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2018
 */
public abstract class CreateTaskConfigurationView extends Composite {

	/** The ui binder. */
	private static CreateTaskConfigurationViewUiBinder uiBinder = GWT.create(CreateTaskConfigurationViewUiBinder.class);

	/**
	 * The Interface CreateTaskConfigurationViewUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * May 4, 2018
	 */
	interface CreateTaskConfigurationViewUiBinder
		extends UiBinder<Widget, CreateTaskConfigurationView> {
	}

	/** The pager. */
	@UiField
	Pager pager;


	@UiField
	ListBox field_select_scope;

	@UiField
	ListBox field_select_task_id;


	@UiField
	ControlGroup cg_select_vre;

	@UiField
	ControlGroup cg_select_task_id;

	@UiField
	ControlGroup cg_parameters_control;

	@UiField
	Fieldset form_unit_fields;

	@UiField
	Controls task_parameters_control;

	@UiField
	Button addCustomFieldButton;

	@UiField
	HTMLPanel html_panel_field;

	@UiField
	TextArea field_task_description;


	/** The map VR es. */
	private Map<String, GcubeScope> mapScopes = new HashMap<String, GcubeScope>();

	private Map<String,  List<TaskOperator>> mapOperators = new HashMap<String, List<TaskOperator>>();

	private String currentScope;

	public final static HandlerManager eventBus = new HandlerManager(null);

	// added custom field entries (by the user)
	private List<CustomFieldEntry> customFieldEntriesList = new ArrayList<CustomFieldEntry>();


	private WSItem wsItem;


	private boolean isEditConfiguration;


	private TaskConfiguration editConfiguration;


	private SelectableOperator selectableOperators;

	/**
	 * Submit handler.
	 */
	public abstract void submitHandler();

	/**
	 * Sets the error.
	 *
	 * @param visible the visible
	 * @param error the error
	 */
	public abstract void setError(boolean visible, String error);



	/**
	 * Sets the confirm.
	 *
	 * @param visible the visible
	 * @param msg the msg
	 */
	public abstract void setConfirm(boolean visible, String msg);


	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 *
	 * @param wsItem the folder
	 * @param conf the conf
	 */
	public CreateTaskConfigurationView(final WSItem wsItem, TaskConfiguration conf, SelectableOperator selectableOperators) {
		this.wsItem = wsItem;
		this.selectableOperators = selectableOperators;

		if(conf!=null)
			this.isEditConfiguration = true;

		this.editConfiguration = conf;

		initWidget(uiBinder.createAndBindUi(this));

		bindEvents();

		if(isEditConfiguration){

			String vreName = editConfiguration.getScope().substring(editConfiguration.getScope().lastIndexOf("/")+1, editConfiguration.getScope().length());
			GcubeScope gcubeScope = new GcubeScope(vreName, editConfiguration.getScope(), GcubeScopeType.VRE);
			mapScopes.put(gcubeScope.getScopeName(), gcubeScope);
			field_select_scope.addItem(gcubeScope.getScopeTitle(), editConfiguration.getScope());
			field_select_scope.setSelectedValue(editConfiguration.getScope());

			field_select_task_id.addItem(editConfiguration.getTaskName(), editConfiguration.getTaskId());
			field_select_task_id.setSelectedValue(editConfiguration.getTaskId());
			//field_task_id.setValue(editConfiguration.getTaskId());
			field_task_description.setValue(editConfiguration.getTaskDescription());

			List<TaskParameter> params = editConfiguration.getListParameters();
			for (TaskParameter taskParameter : params) {
				appendCustomField(taskParameter.getKey(), Arrays.asList(taskParameter.getValue()), taskParameter.getType().getType(), false);
			}

			pager.getRight().setText("Update Configuration");
		}
		else{

			WsTaskExecutorWidget.wsTaskService.getListOfScopesForLoggedUser(new AsyncCallback<List<GcubeScope>>() {

				@Override
				public void onSuccess(List<GcubeScope> result) {


					for (GcubeScope gcubeScope : result) {
						String toValue = gcubeScope.getScopeTitle();
						mapScopes.put(gcubeScope.getScopeName(), gcubeScope);
						field_select_scope.addItem(toValue, gcubeScope.getScopeName());

					}

					if(result.size()>0){

//						if(isEditConfiguration){
//							//String vreName = FormatUtil.toVREName(editConfiguration.getScope());
//							field_select_scope.setSelectedValue(editConfiguration.getScope());
//						}
//						else
						field_select_scope.setSelectedValue(result.get(0).getScopeName());

						//field_select_vre.setSelectedIndex(0);
						//field_select_vre.fireEvent(DomEvent.fireNativeEvent(nativeEvent, handlerSource););
						DomEvent.fireNativeEvent(Document.get().createChangeEvent(), field_select_scope);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub

				}
			});

			addEmptyCustomFieldEvent(null);
		}

	}

	/**
	 * Adds the empty custom field event.
	 *
	 * @param e the e
	 */
	@UiHandler("addCustomFieldButton")
	void addEmptyCustomFieldEvent(ClickEvent e){

		appendCustomField(null, null, null, true);
	}

	/**
	 * Append custom field.
	 *
	 * @param key the key
	 * @param values the values
	 * @param parameterType the parameter type
	 * @param removable the removable
	 */
	private void appendCustomField(String key, List<String> values, String parameterType, boolean removable){

		CustomFieldEntry toAdd = new CustomFieldEntry(eventBus, key, values, parameterType, removable);
		customFieldEntriesList.add(toAdd);
		cg_parameters_control.add(toAdd);
	}

	/**
	 * @param taskOperator
	 */
	private void fillParametersToOperator(TaskOperator taskOperator) {

		customFieldEntriesList.clear();
		cg_parameters_control.clear();

		int countFileParameter=0;
		boolean addedPublicLink = false;

		for (TaskParameter operator : taskOperator.getInputOperators()) {
			addedPublicLink = false;
			if(!wsItem.isFolder()){
				//IF ITEM IS A FILE THEN I'M ISTANCING ITS FIRST FILE PARAMETER OCCURED (IF IT EXISTS)
				//WITH THE PUBLIC LINK OF THE FILE IF A DEFAULT VALUE FOR THE PARAMETER IS MISSING
				if(operator.getType().getType().equals("FILE")){
					countFileParameter++;
					if(countFileParameter==1){
						List<String> linkValues = new ArrayList<String>();

						String publicLinkParamName = operator.getKey()!=null? operator.getKey():"publicLink";
						//USING DEFAULT VALUES IF EXISTS
						if(operator.getDefaultValues()!=null && operator.getDefaultValues().size()>0){
							linkValues = operator.getDefaultValues();
						}else{
							//USING PUBLIC LINK OF SELECTED FILE
							linkValues.add(wsItem.getPublicLink());
						}

						appendCustomField(publicLinkParamName, linkValues, operator.getType().getType(), false);
						addedPublicLink = true;
					}
				}
			}

			if(!addedPublicLink)
				appendCustomField(operator.getKey(), operator.getDefaultValues(), operator.getType().getType(), false);
		}


		if(wsItem.isFolder()){
			List<String> pItemId = new ArrayList<String>(1);
			pItemId.add(wsItem.getItemId());
			appendCustomField("folderId", pItemId, "OBJECT", true);
		}


	}

	private void loadAlgorithmsForSelectedScope(){

		final GcubeScope selScope = getSelectedScope();

		if(selScope==null)
			return;

		List<TaskOperator> operators = mapOperators.get(selScope.getScopeName());

		if(operators!=null){
			fillSelectOperator(operators);
			return;
		}

		LoaderIcon loader = new LoaderIcon("Please wait, loading Algorithms...");
		final DialogResult loaderBox = new DialogResult(null, "Please wait", loader, false);
		loaderBox.getElement().getStyle().setZIndex(Integer.MAX_VALUE-5000);
		pager.getRight().setVisible(false);

		String[] filterForParameterNames = null;
		FilterOperator filterOperator = null;

//		if(!wsItem.isFolder()){
//			//is file gets only algorithm having FILE as parameter
//			filterForParameterNames = new String[2];
//			filterForParameterNames[0] = "FILE";
//		}

//		filterForParameterNames = new String[2];
//		filterForParameterNames[0] = "FILE";
//		filterForParameterNames[1] = "TABULAR";

		WsTaskExecutorWidget.wsTaskService.getListOperatorsPerScope(selScope.getScopeName(), selectableOperators, new AsyncCallback<List<TaskOperator>>() {

			@Override
			public void onFailure(Throwable caught) {
				loaderBox.hide();
				field_select_task_id.setEnabled(false);
				Window.alert("Error: "+caught.getMessage());
				pager.getRight().setVisible(false);

			}

			@Override
			public void onSuccess(List<TaskOperator> result) {
				loaderBox.hide();
				field_select_task_id.setEnabled(true);
				if(result==null || result.size()==0){
					new DialogResult(null, "Warning", "No operator available in the scope: "+selScope.getScopeName()).center();
					return;
				}

				pager.getRight().setVisible(true);
				mapOperators.put(selScope.getScopeName(), result);
				fillSelectOperator(result);
			}
		});

		loaderBox.center();

	}

	private void fillSelectOperator(List<TaskOperator> operators){
		field_select_task_id.clear();
		for (TaskOperator taskOperator : operators) {
			field_select_task_id.addItem(taskOperator.getName(), taskOperator.getId());
		}

		if(isEditConfiguration){
			//String vreName = FormatUtil.toVREName(editConfiguration.getScope());
			field_select_task_id.setSelectedValue(editConfiguration.getTaskId());
		}

		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), field_select_task_id);
	}


	/**
	 * Bind events.
	 */
	private void bindEvents() {

		// when a custom field is removed, remove it from the list
		eventBus.addHandler(DeleteCustomFieldEvent.TYPE, new DeleteCustomFieldEventHandler() {

			@Override
			public void onRemoveEntry(DeleteCustomFieldEvent event) {
				customFieldEntriesList.remove(event.getRemovedEntry());
				cg_parameters_control.remove(event.getRemovedEntry());
			}
		});

		pager.getLeft().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				WsTaskExecutorWidget.eventBus.fireEvent(new ShowListOfTaskConfigurationsEvent(wsItem));
			}
		});

		pager.getRight().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setError(false, "");
				boolean isValid = validateSubmit();
				if(isValid)
					submitHandler();

			}
		});

		field_select_scope.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				loadAlgorithmsForSelectedScope();
			}
		});

		field_select_task_id.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				setValueDescription("");
				String taskId = getTaskId();
				GWT.log("Task Id: "+taskId);
				GcubeScope selScope = getSelectedScope();
				List<TaskOperator> operators = mapOperators.get(selScope.getScopeName());
				GWT.log("Operatos for scope '"+selScope.getScopeName() +"' are: "+operators);
				for (TaskOperator taskOperator : operators) {
					if(taskId.compareTo(taskOperator.getId())==0){
						setValueDescription(taskOperator.getBriefDescription());
						fillParametersToOperator(taskOperator);
					}
				}

			}
		});

	}

	/**
	 *
	 */
	private void setValueDescription(String value) {

		field_task_description.setValue(value);

	}

	/**
	 * Validate submit.
	 *
	 * @return true, if successful
	 */
	protected boolean validateSubmit() {
		cg_select_task_id.setType(ControlGroupType.NONE);
		//cg_parameters_control.setType(ControlGroupType.NONE);
		//cg_remote_path.setType(ControlGroupType.NONE);

		if(field_select_scope.getSelectedItemText()==null){
			cg_select_vre.setType(ControlGroupType.ERROR);
			setError(true, "You must select a Scope!");
			return false;
		}

		if(field_select_scope.getSelectedItemText() == null || field_select_scope.getSelectedItemText().isEmpty()){
			cg_select_task_id.setType(ControlGroupType.ERROR);
			setError(true, "You must select an Algorithm!");
			return false;
		}

		for (CustomFieldEntry cFE : customFieldEntriesList) {
			cFE.getControlGroup().setType(ControlGroupType.NONE);
			if(cFE.getKey()==null || cFE.getKey().isEmpty()){
				cFE.getControlGroup().setType(ControlGroupType.ERROR);
				//cg_parameters_control.setType(ControlGroupType.ERROR);
				setError(true, "You must type a valid key parameter!");
				return false;
			}
		}

		return true;
	}


	/**
	 * Gets the selected scope.
	 *
	 * @return the selected scope
	 */
	public GcubeScope getSelectedScope(){
		//String item = field_select_scope.getSelectedItemText();
		String text = field_select_scope.getSelectedValue();
		GWT.log("Selected scope: "+text);
		GWT.log("Selected scope: "+mapScopes.get(text));
		return mapScopes.get(text);
	}


	/**
	 * Gets the task id.
	 *
	 * @return the task id
	 */
	public String getTaskId(){
		String taskId = field_select_task_id.getSelectedValue();
		GWT.log("Selected task Id: "+taskId);
		return taskId;

	}


	/**
	 * Gets the task name.
	 *
	 * @return the task name
	 */
	public String getTaskName(){
		return field_select_task_id.getSelectedItemText();
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public List<TaskParameter> getParameters(){

		List<TaskParameter> listParameters = new ArrayList<TaskParameter>();
		for (CustomFieldEntry cFE : customFieldEntriesList) {
			TaskParameter tp = new TaskParameter(cFE.getKey(), cFE.getValue(), null, new TaskParameterType(cFE.getType()));
			listParameters.add(tp);
		}
		return listParameters;
	}


	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription(){
		return field_task_description.getValue();
	}



	/**
	 * @return the isEditConfiguration
	 */
	public boolean isEditConfiguration() {

		return isEditConfiguration;
	}


	/**
	 * @return the editConfiguration
	 */
	public TaskConfiguration getEditConfiguration() {

		return editConfiguration;
	}

}
