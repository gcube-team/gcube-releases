/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.experimentArea;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.bean.Operator;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.ColumnListParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.ColumnParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.Parameter;
import org.gcube.portlets.user.statisticalmanager.client.form.AbstractField;
import org.gcube.portlets.user.statisticalmanager.client.form.OperatorFieldWidget;
import org.gcube.portlets.user.statisticalmanager.client.form.TabularField;
import org.gcube.portlets.user.statisticalmanager.client.resources.Images;
import org.gcube.portlets.user.statisticalmanager.client.rpc.StatisticalManagerPortletServiceAsync;

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
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
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

	private static final ImageResource PRELOAD_IMAGE = StatisticalManager.resources.loaderBig();

	private Operator operator;
	private FormPanel parametersPanel;
	private FieldSet parametersFieldSet;
	private Map<String, OperatorFieldWidget> fieldWidgetsMap = new HashMap<String, OperatorFieldWidget>();
	//private Map<Parameter, String> parameterValues = new HashMap<Parameter, String>();
	private WorkflowOperatorPanelHandler handler=null;
	private TextField<String> titleField;

	private String defaultComputationTitle;


	public WorkflowOperatorPanel(StatisticalManagerPortletServiceAsync service, Operator operator, WorkflowOperatorPanelHandler handler) {
		this(operator);
		this.setHandler(handler);
	}
	/**
	 * 
	 */
	public WorkflowOperatorPanel(Operator operator) {
		super();
		
		this.operator = operator;
		
		this.defaultComputationTitle = getDefaultComputationTitle();
		//lc.setLayout(new FitLayout());
		this.setHeight(50);
		this.setAutoHeight(true);
		this.addStyleName("workflow");

		Image img = new Image(GWT.getModuleBaseURL()+"../images/operators/"+(operator.hasImage() ? operator.getId() : "DEFAULT_IMAGE")+".png");
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

		loadOperatorParameters();
	}

	/**
	 * 
	 */
	private void addTitleField() {
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
		StatisticalManager.getService().getParameters(operator, new AsyncCallback<List<Parameter>>() {
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
		// field widgets creation
		for (Parameter p : operator.getOperatorParameters()) {
			OperatorFieldWidget fieldWidget = new OperatorFieldWidget(p);
//			fields.add(fieldWidget);
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
//					e.printStackTrace();
					MessageBox.alert("Error", "Column parameter \""+p.getName()+" refers to a tabular parameter that doesn't exists ("+tabParamName+")\"", null);
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
				if (handler!=null) {
					String value = titleField.getValue();
					String title = (value==null || value.contentEquals("")) ? defaultComputationTitle : value;
					handler.startComputation(title, title); // TODO insert description
					
				}
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
	
	public void setHandler(WorkflowOperatorPanelHandler handler) {
		this.handler = handler;
	}
	
	public String getDefaultComputationTitle() {
		String name = this.operator.getName();
		//String date = DateTimeFormat.getShortDateTimeFormat().format(new Date());
		String date = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT).format(new Date());
		
		return name+"-"+date;
	}
}
