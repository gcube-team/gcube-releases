/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.algorithms;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzerPortletServiceAsync;
import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzer_portlet;
import org.gcube.portlets.user.trendylyzer_portlet.client.form.AbstractField;
import org.gcube.portlets.user.trendylyzer_portlet.client.form.AlgorithmFieldWidget;
import org.gcube.portlets.user.trendylyzer_portlet.client.form.TabularField;
import org.gcube.portlets.user.trendylyzer_portlet.client.resources.Images;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.ColumnListParameter;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.ColumnParameter;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.Parameter;

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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

public class WorkflowAlgorithmPanel extends LayoutContainer {

	public interface WorkflowAlgorithmPanelHandler {
		public void startComputation(String computationTitle,
				String computationDescription);
	}

	private static final String START_BUTTON_TOOLTIP = "Extract data";
	Logger log = Logger.getLogger("");

	private static final ImageResource PRELOAD_IMAGE = TrendyLyzer_portlet.resources
			.loaderBig();

	private Algorithm algorithm;
	private FormPanel parametersPanel;
	private FieldSet parametersFieldSet;
	private Map<String, AlgorithmFieldWidget> fieldWidgetsMap = new HashMap<String, AlgorithmFieldWidget>();
	private Map<Parameter, String> parameterValues = new HashMap<Parameter, String>();
	private WorkflowAlgorithmPanelHandler handler = null;
	private TextField<String> titleField;

	private String defaultComputationTitle;

	public WorkflowAlgorithmPanel(TrendyLyzerPortletServiceAsync service,
			Algorithm algorithm, WorkflowAlgorithmPanelHandler handler) {
		this(algorithm);
		this.setHandler(handler);
	}

	/**
	 * 
	 */
	public WorkflowAlgorithmPanel(Algorithm algorithm) {
		super();
		
		this.algorithm = algorithm;
		
		this.defaultComputationTitle = getDefaultComputationTitle();
		//lc.setLayout(new FitLayout());
		this.setHeight(50);
		this.setAutoHeight(true);
		this.addStyleName("workflow");
		Image img= new Image(TrendyLyzer_portlet.resources.defaultAlg());
		
		//Image img = new Image(GWT.getModuleBaseURL()+"../images/operators/"+(algorithm.hasImage() ? algorithm.getId() : "DEFAULT_IMAGE")+".png");
		//Image img = new Image(GWT.getModuleBaseURL()+"../images/operators/DEFAULT_IMAGE.png");
		img.addStyleName("workflow-icon");
		this.add(img);

		Html title = new Html(algorithm.getName());
		title.addStyleName("workflow-title");
		this.add(title);

		String descr ="      "+ algorithm.getDescription();
//
		descr = (descr==null || descr.contentEquals("")) ? "no-description" : algorithm.getDescription();
		Html description = new Html(descr);
		description.addStyleName("workflow-description");
		this.add(description);
		
		addTitleField();
		
		parametersPanel = new FormPanel() {
			@Override
			public boolean isValid(boolean preventMark) {
				boolean flag = super.isValid(preventMark);

				if (flag) {
					for (Map.Entry<String, AlgorithmFieldWidget> entry : fieldWidgetsMap.entrySet()) {
						AlgorithmFieldWidget fieldWidget = entry.getValue();
						flag = flag && fieldWidget.isValid();
					}
				}
				return flag;
			}
			
		
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
		//parametersFieldSet.setHeading("Parameters");
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
		hp.add(new Html(
				"<div style='margin-left:30px; margin-right:10px; margin-top:5px'>Function description: </div>"));
		titleField = new TextField<String>();
		titleField.setWidth(280);
		titleField.setValue(this.defaultComputationTitle);
		titleField.setFieldLabel("Function description");
		hp.add(titleField);
		this.add(hp);
	}

	/**
	 * @param parametersPanel
	 */
	private void loadOperatorParameters() {

		TrendyLyzer_portlet.getService().getParameters(algorithm,
				new AsyncCallback<List<Parameter>>() {
					public void onSuccess(List<Parameter> result) {
						algorithm.setAlgorithmParameters(result);
						parametersFieldSet.removeAll();
						showForm();
					}

					public void onFailure(Throwable caught) {
						//log.log(Level.SEVERE, caught.getMessage());
						caught.printStackTrace();
						MessageBox.alert("Error ",
								"Impossible to retrieve parameters.", null);
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
		//log.log(Level.SEVERE, "inside showForm ");
		for (Parameter p : algorithm.getAlgorithmParameters()) {
			AlgorithmFieldWidget fieldWidget = new AlgorithmFieldWidget(p);
			fieldWidgetsMap.put(p.getName(), fieldWidget);
			parametersFieldSet.add(fieldWidget);
		}

		for (Parameter p : algorithm.getAlgorithmParameters()) {
			if (p.isColumn() || p.isColumnList()) {
				// search for the table parameter which it depends
				String tabParamName = (p.isColumn() ? ((ColumnParameter) p)
						.getReferredTabularParameterName()
						: ((ColumnListParameter) p)
								.getReferredTabularParameterName());

				try {
					// get the column field and the tabular field referred
					TabularField tabularField = (TabularField) fieldWidgetsMap
							.get(tabParamName).getField();
					AbstractField field = fieldWidgetsMap.get(p.getName())
							.getField();
					tabularField.addChangeListener(field);
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox
							.alert("Error",
									"Column parameter \""
											+ p.getName()
											+ " refers to a tabular parameter that doesn't exists ("
											+ tabParamName + ")\"", null);
				}
			}
		}

		final Button submit = new Button("Extract Data");
		submit.setToolTip(START_BUTTON_TOOLTIP);
		submit.setIcon(Images.startComputation());
		submit.setStyleAttribute("margin-left", "20px");
		submit.setStyleAttribute("margin-bottom", "20px");
		// startButton.setScale(ButtonScale.MEDIUM);
		// startButton.setEnabled(false);
		submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (handler != null) {
					String value = titleField.getValue();
					String title = (value == null || value.contentEquals("")) ? defaultComputationTitle
							: value;
					handler.startComputation(title, title); // TODO insert
															// description

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
		for (Map.Entry<String, AlgorithmFieldWidget> entry : fieldWidgetsMap
				.entrySet()) {
			AlgorithmFieldWidget fieldWidget = entry.getValue();
			fieldWidget.updateOperatorParameterValue();
		}
	}

	/**
	 * @return the operator
	 */
	public Algorithm getOperator() {
		return algorithm;
	}

	public void setHandler(WorkflowAlgorithmPanelHandler handler) {
		this.handler = handler;
	}

	public String getDefaultComputationTitle() {
		String name = this.algorithm.getName();
		String date = DateTimeFormat.getShortDateTimeFormat()
				.format(new Date());
		return name + "-" + date;
	
	}
}
