/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.experimentArea;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.Services;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.StatisticalManagerExperimentsWidget;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.StatisticalManagerWidgetServiceAsync;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.SubmissionParameters;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.Operator;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.TableItemSimple;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.ColumnListParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.ColumnParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.Parameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.TabularParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.form.AbstractField;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.form.OperatorFieldWidget;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.form.TabularField;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.resources.Images;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;


/**
 * @author ceras
 *
 */
public class WorkflowOperatorPanel extends LayoutContainer {

	/**
	 * @author ceras
	 *
	 */
	public interface WorkflowOperatorPanelHandler {
		public void startComputation(String computationTitle, String computationDescription);
	}

	private static final String START_BUTTON_TOOLTIP = "Start Computation";

	private static final ImageResource PRELOAD_IMAGE = Services.getResources().loaderBig();

	private Operator operator;
	private FormPanel parametersPanel;
	private FieldSet parametersFieldSet;
	private Map<String, OperatorFieldWidget> fieldWidgetsMap = new HashMap<String, OperatorFieldWidget>();
//	private Map<Parameter, String> parameterValues = new HashMap<Parameter, String>();
	private TextField<String> titleField;
	protected  Logger logger = Logger.getLogger("logger");
	private String defaultComputationTitle;


	public WorkflowOperatorPanel(StatisticalManagerWidgetServiceAsync service, Operator operator, WorkflowOperatorPanelHandler handler) {
		this(operator);
	}
	/**
	 * 
	 */
	public WorkflowOperatorPanel(Operator operator) {
		super();
		logger.log(Level.SEVERE,"WorkflowOperatorPanel Constructore");

		this.operator = operator;
		
		this.defaultComputationTitle = getDefaultComputationTitle();
		//lc.setLayout(new FitLayout());
//		this.setHeight(50);
		this.addStyleName("workflow");

		Image img = new Image(Images.operatorImagesMap.get(operator.hasImage() ? operator.getId() : "DEFAULT_IMAGE"));
		img.addStyleName("workflow-icon");
		this.add(img);

		Html title = new Html(operator.getName());
		title.addStyleName("workflow-title");
		this.add(title);

		String descr = operator.getDescription();
		descr = (descr==null || descr.contentEquals("")) ? "no-description" : operator.getDescription();
		Html description = new Html(descr);
		description.addStyleName("workflow-description");
		this.add(description);
		
		addTitleField();
		logger.log(Level.SEVERE,"Parameter panel");

		parametersPanel = new FormPanel() {
			@Override
			public boolean isValid(boolean preventMark) {
				boolean flag = super.isValid(preventMark);

				if (flag) {
					for (Map.Entry<String, OperatorFieldWidget> entry : fieldWidgetsMap.entrySet()) {
						OperatorFieldWidget fieldWidget = entry.getValue();
						flag = flag && fieldWidget.isValid();
					}
				}
				return flag;
			}
			
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.widget.form.FormPanel#onRender(com.google.gwt.user.client.Element, int)
			 */
			@Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				this.getBody().setBorders(false);
			}
		};
		parametersPanel.expand();
		parametersPanel.setHeaderVisible(false);
		parametersPanel.setBorders(false);
		
		parametersPanel.setStyleAttribute("margin", "20px");
		parametersFieldSet = new FieldSet();
		parametersFieldSet.setHeading("Parameters");
		parametersPanel.add(parametersFieldSet);
		
		this.add(parametersPanel);
		logger.log(Level.SEVERE,"loadOperatprParameterl");

		loadOperatorParameters();
	}

	/**
	 * 
	 */
	private void addTitleField() {
		logger.log(Level.SEVERE,"call addTitleField");

		HorizontalPanel hp = new HorizontalPanel();
		hp.setStyleAttribute("margin-top", "20px");
		hp.add(new Html("<div style='margin-left:30px; margin-right:10px; margin-top:5px'>Computation Title: </div>"));
		titleField = new TextField<String>();
		titleField.setWidth(250);
		titleField.setValue(this.defaultComputationTitle);
		titleField.setFieldLabel("Computation Title");
		hp.add(titleField);
		this.add(hp);
	}
	
	/**
	 * @param parametersPanel
	 */
	private void loadOperatorParameters() {
		Services.getStatisticalService().getParameters(operator, new AsyncCallback<List<Parameter>>() {
			@Override
			public void onSuccess(List<Parameter> result) {
				operator.setOperatorParameters(result);
				parametersFieldSet.removeAll();
				showForm();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Error ", "Impossible to retrieve parameters.", null);
			}
		});
		Image img = new Image(PRELOAD_IMAGE);
		img.setStyleName("workflow-parameters-preload");
		parametersFieldSet.add(img);
	}

	/**
	 * 
	 */
	protected void showForm() {
		logger.log(Level.SEVERE,"Parameter showForm");

		// field widgets creation
		
		
		for (Parameter p : operator.getOperatorParameters()) {
			OperatorFieldWidget fieldWidget = new OperatorFieldWidget(p);
			fieldWidgetsMap.put(p.getName(), fieldWidget);
			parametersFieldSet.add(fieldWidget);
		}
		
		for (Parameter p: operator.getOperatorParameters()) {
			if (p.isColumn() || p.isColumnList()) {
				// search for the table parameter which it depends
				String tabParamName = (p.isColumn()
						? ((ColumnParameter)p).getReferredTabularParameterName()
						: ((ColumnListParameter)p).getReferredTabularParameterName());
				
				try {
					// get the column field and the tabular field referred
					TabularField tabularField = (TabularField)fieldWidgetsMap.get(tabParamName).getField();
					AbstractField field = fieldWidgetsMap.get(p.getName()).getField();
					tabularField.addChangeListener(field);				
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox.alert("Error", "Column parameter \""+p.getName()+" refers to a tabular parameter that doesn't exists ("+tabParamName+")\"", null);
				}
			}
		}
		
		// look for assignable default table
		Map<String, TableItemSimple> userSelectedTables=StatisticalManagerExperimentsWidget.instance().getListSelectedList();
		if(userSelectedTables.size()==1){ // only set default if one table is specified			
			TableItemSimple toSelect=userSelectedTables.values().iterator().next();
			for(Entry<String,OperatorFieldWidget> entry:fieldWidgetsMap.entrySet()){
				if(entry.getValue().getField() instanceof TabularField){
					TabularParameter param=(TabularParameter)entry.getValue().getParameter();
					if(param.getTemplates().contains("GENERIC")){
						((TabularField)entry.getValue().getField()).forceSelection(toSelect);
					}
				}
			}
		}
		
		
		final Button submit = new Button("Start Computation");
		submit.setToolTip(START_BUTTON_TOOLTIP);
		submit.setIcon(Images.startComputation());
		submit.setStyleAttribute("margin-left","20px");
		submit.setStyleAttribute("margin-bottom","20px");
//		startButton.setScale(ButtonScale.MEDIUM);
//		startButton.setEnabled(false);
		submit.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				
					String value = titleField.getValue();
					String title = (value==null || value.contentEquals("")) ? defaultComputationTitle : value;
//					handler.startComputation(title, title); // TODO insert description
					updateOperatorParametersValues();
					StatisticalManagerExperimentsWidget.instance().submit(new SubmissionParameters(getOperator(), title, title));
				
			}
		});
		
		this.add(submit);
		this.layout();
		
		FormButtonBinding binding = new FormButtonBinding(parametersPanel);		
		binding.addButton(submit);
		
		parametersPanel.layout();
	}
	
	public void updateOperatorParametersValues() {
		for (Map.Entry<String, OperatorFieldWidget> entry : fieldWidgetsMap.entrySet()) {
			OperatorFieldWidget fieldWidget = entry.getValue();
			fieldWidget.updateOperatorParameterValue();
		}
	}
	
	/**
	 * @return the operator
	 */
	public Operator getOperator() {
		return operator;
	}
	
	
	public String getDefaultComputationTitle() {
		String name = this.operator.getName();
		String date = DateTimeFormat.getShortDateTimeFormat().format(new Date());
		return name+"-"+date;
	}
}
