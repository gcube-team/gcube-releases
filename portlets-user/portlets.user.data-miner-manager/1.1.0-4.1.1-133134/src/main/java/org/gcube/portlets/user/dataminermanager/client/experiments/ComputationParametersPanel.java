/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.experiments;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;
import org.gcube.portlets.user.dataminermanager.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminermanager.client.events.SessionExpiredEvent;
import org.gcube.portlets.user.dataminermanager.client.parametersfield.AbstractFld;
import org.gcube.portlets.user.dataminermanager.client.parametersfield.ColumnFld;
import org.gcube.portlets.user.dataminermanager.client.parametersfield.ColumnListFld;
import org.gcube.portlets.user.dataminermanager.client.parametersfield.OperatorFieldWidget;
import org.gcube.portlets.user.dataminermanager.client.parametersfield.TabularFld;
import org.gcube.portlets.user.dataminermanager.client.rpc.DataMinerPortletServiceAsync;
import org.gcube.portlets.user.dataminermanager.client.util.UtilsGXT3;
import org.gcube.portlets.user.dataminermanager.shared.Constants;
import org.gcube.portlets.user.dataminermanager.shared.exception.SessionExpiredServiceException;
import org.gcube.portlets.user.dataminermanager.shared.parameters.ColumnListParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.ColumnParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.ParameterType;
import org.gcube.portlets.user.dataminermanager.shared.process.Operator;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.FormPanel;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationParametersPanel extends SimpleContainer {

	public interface ComputationParametersPanelHandler {
		public void startComputation();
	}

	private static final String START_BUTTON_TOOLTIP = "Start Computation";

	private static final ImageResource PRELOAD_IMAGE = DataMinerManager.resources
			.loaderBig();

	private VerticalLayoutContainer v;
	private Operator operator;
	private FormPanel parametersPanel;
	private FieldSet parametersFieldSet;
	private VerticalLayoutContainer vParameters;
	private Map<String, OperatorFieldWidget> fieldWidgetsMap;
	private ComputationParametersPanelHandler handler = null;

	private TextButton submit;

	public ComputationParametersPanel(Operator operator) {
		super();
		this.operator = operator;
		fieldWidgetsMap = new HashMap<>();
		try {
			init();
			create();
		} catch (Throwable e) {
			Log.error("ComputationParametersPanel" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void init() {
		setStylePrimaryName("workflow");
		setResize(true);

	}

	private void create() {
		v = new VerticalLayoutContainer();
		add(v);

		Image img = new Image(GWT.getModuleBaseURL() + "../images/operators/"
				+ (operator.hasImage() ? operator.getId() : "DEFAULT_IMAGE")
				+ ".png");
		img.setStylePrimaryName("workflow-icon");// -15
		v.add(img, new VerticalLayoutData(-1, -1, new Margins(-15, 0, 0, 5)));

		String locationRef = "";
		try {
			String location = Location.getHref();
			String[] locationData = location.split("\\?");
			locationRef = locationData[0];
		} catch (Throwable e) {
			Log.error("Error retrieving location: " + e.getLocalizedMessage());
		}

		HtmlLayoutContainer title = new HtmlLayoutContainer("<span><a href='"
				+ locationRef + "?" + Constants.DATA_MINER_OPERATOR_ID + "="
				+ operator.getId() + "'>" + operator.getName() + "</a></span>");
		title.addStyleName("workflow-title");
		v.add(title, new VerticalLayoutData(-1, -1, new Margins(20, 0, 0, -25)));

		String descr = operator.getDescription();
		descr = (descr == null || descr.contentEquals("")) ? "no-description"
				: operator.getDescription();// display:block;clear:both;'
		HtmlLayoutContainer description = new HtmlLayoutContainer(
				"<span style='padding-left:10px;padding-right:10px;display:inline-block;'>"
						+ descr + "</span>");
		description.addStyleName("workflow-description");
		v.add(description, new VerticalLayoutData(-1, -1, new Margins(0)));

		// addTitleField();

		parametersPanel = new FormPanel() {
			@Override
			public boolean isValid(boolean preventMark) {
				boolean flag = super.isValid(preventMark);

				if (flag) {
					for (Map.Entry<String, OperatorFieldWidget> entry : fieldWidgetsMap
							.entrySet()) {
						OperatorFieldWidget fieldWidget = entry.getValue();
						flag = flag && fieldWidget.isValid();
					}
				}
				return flag;
			}

		};
		// parametersPanel = new FramedPanel();
		parametersPanel.setBorders(false);
		parametersPanel.getElement().getStyle().setPaddingBottom(20, Unit.PX);

		parametersFieldSet = new FieldSet();
		parametersFieldSet.setHeadingText("Parameters");

		vParameters = new VerticalLayoutContainer();

		Image imgLoading = new Image(PRELOAD_IMAGE);
		vParameters.add(imgLoading, new VerticalLayoutData(1, -1,
				new Margins(0)));

		parametersFieldSet.add(vParameters, new MarginData(new Margins(0)));
		parametersFieldSet.setCollapsible(false);
		parametersPanel.add(parametersFieldSet);

		v.add(parametersPanel, new VerticalLayoutData(1, -1, new Margins(5)));

		submit = new TextButton("Start Computation");
		submit.setToolTip(START_BUTTON_TOOLTIP);
		submit.setIcon(DataMinerManager.resources.startComputation());

		submit.getElement().getStyle().setMarginLeft(20, Unit.PX);
		submit.getElement().getStyle().setMarginBottom(20, Unit.PX);

		submit.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				if (handler != null && parametersPanel.isValid()) {
					handler.startComputation(); // TODO insert
												// description
				}

			}
		});

		v.add(submit);
		// , new VerticalLayoutData(-1, -1, new Margins(0, 0, 20, 20)));
		submit.setVisible(false);

		forceLayout();
		loadOperatorParameters();

	}

	/*
	 * private void addTitleField() { titleField = new TextField();
	 * titleField.setWidth(250); titleField.setValue(defaultComputationTitle);
	 * FieldLabel titleLabel = new FieldLabel(titleField, "Computation Title");
	 * titleLabel.addStyleName("workflow-computation-title"); v.add(titleLabel,
	 * new VerticalLayoutData(-1, -1, new Margins(0))); }
	 */

	/**
	 * @param parametersPanel
	 */
	private void loadOperatorParameters() {
		DataMinerPortletServiceAsync.INSTANCE.getParameters(operator,
				new AsyncCallback<List<Parameter>>() {
					@Override
					public void onSuccess(List<Parameter> result) {
						Log.debug("Retrieved Parameters!");
						operator.setOperatorParameters(result);
						vParameters.clear();
						showForm();
					}

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof SessionExpiredServiceException) {
							UtilsGXT3.alert("Error", "Expired Session!");
							EventBusProvider.INSTANCE
									.fireEvent(new SessionExpiredEvent());
						} else {

							Log.error("Impossible to retrieve parameters. "
									+ caught.getLocalizedMessage());
							caught.printStackTrace();
							UtilsGXT3.alert("Error ",
									"Impossible to retrieve parameters. "
											+ caught.getLocalizedMessage());
						}
					}
				});

	}

	/**
	 * 
	 */
	protected void showForm() {
		try {

			// field widgets creation
			for (Parameter p : operator.getOperatorParameters()) {
				OperatorFieldWidget fieldWidget = new OperatorFieldWidget(p);
				fieldWidgetsMap.put(p.getName(), fieldWidget);
				vParameters.add(fieldWidget.getParameterLabel(),
						new VerticalLayoutData(1, -1, new Margins(0)));
			}

			for (Parameter p : operator.getOperatorParameters()) {
				if (p.getTypology().compareTo(ParameterType.COLUMN) == 0
						|| p.getTypology().compareTo(
								ParameterType.COLUMN_LIST) == 0) {
					// search for the table parameter which it depends
					String tabParamName = (p.getTypology().compareTo(
							ParameterType.COLUMN) == 0 ? ((ColumnParameter) p)
							.getReferredTabularParameterName()
							: ((ColumnListParameter) p)
									.getReferredTabularParameterName());

					try {
						// get the column field and the tabular field referred
						TabularFld tabularField = (TabularFld) fieldWidgetsMap
								.get(tabParamName).getField();
						if (tabularField != null) {
							AbstractFld field = fieldWidgetsMap
									.get(p.getName()).getField();
							if (field instanceof ColumnFld) {
								ColumnFld columnField = (ColumnFld) field;
								tabularField
										.addTabularFldChangeEventHandler(columnField);
							} else {
								if (field instanceof ColumnListFld) {
									ColumnListFld columnListField = (ColumnListFld) field;
									tabularField
											.addTabularFldChangeEventHandler(columnListField);
								} else {

								}
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
						UtilsGXT3
								.alert("Error",
										"Column parameter \""
												+ p.getName()
												+ " refers to a tabular parameter that doesn't exists ("
												+ tabParamName + ")\"");
					}
				}
			}
			parametersPanel.getElement().getStyle()
					.setPaddingBottom(0, Unit.PX);
			submit.setVisible(true);
			parametersPanel.forceLayout();
			forceLayout();

		} catch (Throwable e) {
			Log.error("Error in show form:" + e.getLocalizedMessage());
			Log.error("Error: " + Arrays.asList(e.getStackTrace()).toString());
			e.printStackTrace();
		}
	}

	public void updateOperatorParametersValues() {
		for (Map.Entry<String, OperatorFieldWidget> entry : fieldWidgetsMap
				.entrySet()) {
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

	public void setHandler(ComputationParametersPanelHandler handler) {
		this.handler = handler;
	}

}
