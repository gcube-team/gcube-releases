package org.gcube.portlets.widgets.netcdfbasicwidgets.client.widgets;

import java.util.ArrayList;

import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.NetCDFDataEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.NetCDFDataEvent.NetCDFDataEventHandler;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SampleVariableDataEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SampleVariableDataEvent.SampleVariableDataEventHandler;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.model.NetCDFDataModel;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.resource.NetCDFBasicResources;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.VariableData;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;

public class SamplePanel extends SimplePanel implements NetCDFDataEventHandler {
	private static final String HEIGHT = "350px";
	private static final String WIDTH = "700px";
	private NetCDFDataModel netCDFDataModel;

	private ListBox variablesBox;
	private TextArea sampleData;
	private ArrayList<VariableData> variables;

	public SamplePanel(NetCDFDataModel netCDFDataModel) {
		this.netCDFDataModel = netCDFDataModel;
		init();
		create();
	}

	private void init() {
		setHeight(HEIGHT);
		setWidth(WIDTH);
	}

	private void create() {
		netCDFDataModel.addNetCDFDataEventHandler(this);

		// ////////
		// Form
		FlexTable sampleFlexTable = new FlexTable();
		sampleFlexTable.setCellSpacing(2);

		// Add a drop box with the list types
		variablesBox = new ListBox();
		variablesBox.setEnabled(false);
		variablesBox.ensureDebugId("samplePanelVariablesBox");
		variablesBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				sampleData.setValue("");
				int index = variablesBox.getSelectedIndex();
				String value = variablesBox.getValue(index);
				retrieveSample(value);
			}

		});

		sampleFlexTable.setHTML(0, 0, "Variable:");
		sampleFlexTable.setWidget(0, 1, variablesBox);

		sampleData = new TextArea();
		sampleData.getElement().setId("netcdfSampleTextArea");
		sampleData.setStylePrimaryName(NetCDFBasicResources.INSTANCE.netCDFBasicCSS().getSampleDataTextArea());
		sampleData.setReadOnly(true);
		
		
		sampleFlexTable.getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
		sampleFlexTable.setHTML(1, 0, "Sample:");
		sampleFlexTable.setWidget(1, 1, sampleData);

		setWidget(sampleFlexTable);

	}

	private void retrieveSample(String selectedValue) {
		VariableData variableRequested = null;
		if (Integer.valueOf(selectedValue) != -1) {
			for (VariableData variableData : variables) {
				if (variableData.getId() == Integer.valueOf(selectedValue)) {
					variableRequested = variableData;
					break;
				}

			}

			if (variableRequested != null) {
				SampleVariableDataEventHandler handler = new SampleVariableDataEventHandler() {

					@Override
					public void onSample(SampleVariableDataEvent event) {
						sampleData.setValue(event.getSampleValues().getArrayData().asString());
					}
				};

				netCDFDataModel.retrieveSampleOfVariable(handler, variableRequested);
			}
		}
	}

	@Override
	public void onNetCDFDataReady(NetCDFDataEvent event) {
		variables = event.getNetCDFData().getVariables();
		variablesBox.clear();
		variablesBox.addItem(" ", "-1");
		for (VariableData varData : variables) {
			variablesBox.addItem(varData.getFullName(), String.valueOf(varData.getId()));
		}
		variablesBox.setEnabled(true);

	}

}
