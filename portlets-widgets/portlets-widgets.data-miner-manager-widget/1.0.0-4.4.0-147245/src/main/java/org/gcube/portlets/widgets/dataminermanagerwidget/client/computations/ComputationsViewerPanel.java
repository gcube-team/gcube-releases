package org.gcube.portlets.widgets.dataminermanagerwidget.client.computations;

import java.util.LinkedHashMap;

import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationValue;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationValueFile;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationValueFileList;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationValueImage;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Style.Unit;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationsViewerPanel extends FramedPanel {

	private VerticalLayoutContainer v;
	private ComputationData computationData;

	public ComputationsViewerPanel(ComputationData computationData) {
		super();
		Log.debug("ComputationsViewerPanel");
		this.computationData = computationData;
		Log.debug("ComputationData: " + computationData);
		init();
		create();

	}

	private void init() {
		setItemId("ComputationsViewerPanel");
		forceLayoutOnResize = true;
		setBodyBorder(false);
		setBorders(false);
		setBodyStyle("backgroundColor:white;");
		setHeaderVisible(false);
		setResize(true);
		setHeadingText("Computations Viewer");
		setBodyStyle("backgroundColor:white;");

	}

	private void create() {
		try {
			v = new VerticalLayoutContainer();
			v.setScrollMode(ScrollMode.AUTO);
			add(v);
			createView();
			forceLayout();
		} catch (Throwable e) {
			Log.error("Error creating ComputationsViewerPanel: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void createView() {

		if (computationData == null) {
			Log.error("ComputationData is null!");
			UtilsGXT3.alert("Error",
					"No information on computation is retrieved!");
			return;
		}

		if (computationData.getComputationId() == null
				|| computationData.getComputationId().getId() == null
				|| computationData.getComputationId().getId().isEmpty()) {
			Log.error("Error in computationId: " + computationData);
			UtilsGXT3.alert("Error",
					"No information on computation id is retrieved!");
			return;
		}

		SimpleContainer sectionTitle = new SimpleContainer();
		// title
		HtmlLayoutContainer title = new HtmlLayoutContainer(
				"<center>Computation Report of <b>"
						+ computationData.getComputationId().getId()
						+ "</b></center>");
		sectionTitle.add(title, new MarginData());
		sectionTitle.getElement().getStyle().setMarginRight(20, Unit.PX);
		// v.add(sectionTitle, new VerticalLayoutData(1, -1, new
		// Margins(0)));
		v.add(sectionTitle, new VerticalLayoutData(-1, -1, new Margins(10)));

		if (computationData.getOutputParameters() != null
				&& !computationData.getOutputParameters().isEmpty()) {
			FieldSet outputFieldSet = outputView();
			v.add(outputFieldSet, new VerticalLayoutData(-1, -1,
					new Margins(10)));
		}

		if (computationData.getInputParameters() != null
				&& !computationData.getInputParameters().isEmpty()) {
			FieldSet inputFieldSet = inputView();
			v.add(inputFieldSet,
					new VerticalLayoutData(-1, -1, new Margins(10)));
		}

		FieldSet detailsFieldSet = detailsView();
		v.add(detailsFieldSet, new VerticalLayoutData(-1, -1, new Margins(10)));

		FieldSet operatorFieldSet = operatorView();
		v.add(operatorFieldSet, new VerticalLayoutData(-1, -1, new Margins(10)));

	}

	private FieldSet operatorView() {
		try {

			VerticalLayoutContainer operatorVBox = new VerticalLayoutContainer();
			TextField operatorNameField = new TextField();
			operatorNameField.setValue(computationData.getComputationId()
					.getOperatorName());
			operatorNameField.setReadOnly(true);
			FieldLabel operatorNameLabel = new FieldLabel(operatorNameField,
					"Operator Name");
			operatorNameLabel.setLabelWidth(200);
			operatorNameLabel.setLabelWordWrap(true);
			operatorVBox.add(operatorNameLabel, new VerticalLayoutData(1, -1,
					new Margins(0, 4, 0, 4)));

			TextArea operatorDescriptionField = new TextArea();
			operatorDescriptionField.setHeight(40);
			operatorDescriptionField.setValue(computationData
					.getOperatorDescription());
			operatorDescriptionField.setReadOnly(true);
			FieldLabel operatorDescriptionLabel = new FieldLabel(
					operatorDescriptionField, "Operator Description");
			operatorDescriptionLabel.setLabelWidth(200);
			operatorDescriptionLabel.setLabelWordWrap(true);
			operatorDescriptionLabel.setHeight(65);
			operatorVBox.add(operatorDescriptionLabel, new VerticalLayoutData(
					1, -1, new Margins(0, 5, 0, 5)));

			FieldSet operatorFieldSet = new FieldSet();
			operatorFieldSet.setHeadingText("Operator Details");
			operatorFieldSet.setCollapsible(true);
			operatorFieldSet.setHeight(130);
			operatorFieldSet.add(operatorVBox);
			operatorFieldSet.getElement().getStyle()
					.setMarginBottom(120, Unit.PX);
			operatorFieldSet.getElement().getStyle()
					.setMarginRight(20, Unit.PX);
			return operatorFieldSet;
		} catch (Throwable e) {
			Log.error("Error in ComputationsViewerPanel in operator: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		}
	}

	private FieldSet detailsView() {
		try {
			VerticalLayoutContainer detailsVBox = new VerticalLayoutContainer();
			TextField startDateField = new TextField();
			startDateField.setValue(computationData.getStartDate());
			startDateField.setReadOnly(true);
			FieldLabel startDateLabel = new FieldLabel(startDateField,
					"Start Date");
			startDateLabel.setLabelWidth(200);
			startDateLabel.setLabelWordWrap(true);
			detailsVBox.add(startDateLabel, new VerticalLayoutData(1, -1,
					new Margins(0, 4, 0, 4)));

			TextField endDateField = new TextField();
			endDateField.setValue(computationData.getEndDate());
			endDateField.setReadOnly(true);
			FieldLabel endDateLabel = new FieldLabel(endDateField, "End Date");
			endDateLabel.setLabelWidth(200);
			endDateLabel.setLabelWordWrap(true);
			detailsVBox.add(endDateLabel, new VerticalLayoutData(1, -1,
					new Margins(0, 4, 0, 4)));

			TextField statusField = new TextField();
			statusField.setValue(computationData.getStatus());
			statusField.setReadOnly(true);
			FieldLabel statusLabel = new FieldLabel(statusField, "Status");
			statusLabel.setLabelWidth(200);
			statusLabel.setLabelWordWrap(true);
			detailsVBox.add(statusLabel, new VerticalLayoutData(1, -1,
					new Margins(0, 4, 0, 4)));

			TextField vreField = new TextField();
			vreField.setValue(computationData.getVre());
			vreField.setReadOnly(true);
			FieldLabel vreLabel = new FieldLabel(vreField, "VRE");
			vreLabel.setLabelWidth(200);
			vreLabel.setLabelWordWrap(true);
			detailsVBox.add(vreLabel, new VerticalLayoutData(1, -1,
					new Margins(0, 4, 0, 4)));

			FieldSet detailsFieldSet = new FieldSet();
			detailsFieldSet.setHeadingText("Computation Details");
			detailsFieldSet.setCollapsible(true);
			detailsFieldSet.add(detailsVBox);
			detailsFieldSet.getElement().getStyle().setMarginRight(20, Unit.PX);
			return detailsFieldSet;
		} catch (Throwable e) {
			Log.error("Error in ComputationsViewerPanel in details: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		}
	}

	private FieldSet inputView() {
		try {
			VerticalLayoutContainer inputVBox = new VerticalLayoutContainer();
			LinkedHashMap<String, ComputationValue> input = computationData
					.getInputParameters();
			for (String key : input.keySet()) {
				ComputationValue computationValue = input.get(key);
				Log.debug("Input: [key=" + key + ", ComputationValue="
						+ computationValue + "]");
				FieldLabel fieldLabel = null;
				SimpleContainer simpleContainer;
				if(computationValue==null){
					TextField textField = new TextField();
					textField.setReadOnly(true);
					fieldLabel = new FieldLabel(textField, key);
					fieldLabel.setLabelWidth(200);
					fieldLabel.setLabelWordWrap(true);
					inputVBox.add(fieldLabel, new VerticalLayoutData(1, -1,
							new Margins(0, 4, 0, 4)));
					continue;
				}
				
				
				switch (computationValue.getType()) {
				case File:
					ComputationValueFile computationValueFile = (ComputationValueFile) computationValue;
					simpleContainer = new ComputationValueFilePanel(
							computationValueFile);
					fieldLabel = new FieldLabel(simpleContainer, key);
					fieldLabel.setLabelWidth(200);
					fieldLabel.setLabelWordWrap(true);
					break;
				case Image:
					ComputationValueImage computationValueImage = (ComputationValueImage) computationValue;
					simpleContainer = new ComputationValueImagePanel(
							computationValueImage);
					fieldLabel = new FieldLabel(simpleContainer, key);
					fieldLabel.setLabelWidth(200);
					fieldLabel.setLabelWordWrap(true);
					break;
				case FileList:
					ComputationValueFileList computationValueFileList = (ComputationValueFileList) computationValue;
					simpleContainer = new ComputationValueFileListPanel(
							computationValueFileList);
					fieldLabel = new FieldLabel(simpleContainer, key);
					fieldLabel.setLabelWidth(200);
					fieldLabel.setLabelWordWrap(true);
					break;
				case String:
				default:
					TextField textField = new TextField();
					textField.setValue(computationValue.getValue());
					textField.setReadOnly(true);
					fieldLabel = new FieldLabel(textField, key);
					fieldLabel.setLabelWidth(200);
					fieldLabel.setLabelWordWrap(true);
					break;

				}
				inputVBox.add(fieldLabel, new VerticalLayoutData(1, -1,
						new Margins(0, 4, 0, 4)));
			}

			FieldSet inputFieldSet = new FieldSet();
			inputFieldSet.setHeadingText("Input Parameters");
			inputFieldSet.setCollapsible(true);
			inputFieldSet.add(inputVBox);
			inputFieldSet.getElement().getStyle().setMarginRight(20, Unit.PX);
			return inputFieldSet;
		} catch (Throwable e) {
			Log.error("Error in ComputationsViewerPanel creating input view: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		}
	}

	private FieldSet outputView() {
		try {
			VerticalLayoutContainer outputVBox = new VerticalLayoutContainer();
			LinkedHashMap<String, ComputationValue> output = computationData
					.getOutputParameters();
			for (String key : output.keySet()) {
				ComputationValue computationValue = output.get(key);
				Log.debug("Output: [key=" + key + ", ComputationValue="
						+ computationValue + "]");
				FieldLabel fieldLabel = null;
				SimpleContainer simpleContainer;
				if(computationValue==null){
					TextField textField = new TextField();
					textField.setReadOnly(true);
					fieldLabel = new FieldLabel(textField, key);
					fieldLabel.setLabelWidth(200);
					fieldLabel.setLabelWordWrap(true);
					outputVBox.add(fieldLabel, new VerticalLayoutData(1, -1,
							new Margins(0, 4, 0, 4)));
					continue;
				}
				
				switch (computationValue.getType()) {
				case File:
					ComputationValueFile computationValueFile = (ComputationValueFile) computationValue;
					simpleContainer = new ComputationValueFilePanel(
							computationValueFile);
					fieldLabel = new FieldLabel(simpleContainer, key);
					fieldLabel.setLabelWidth(200);
					fieldLabel.setLabelWordWrap(true);
					break;
				case Image:
					ComputationValueImage computationValueImage = (ComputationValueImage) computationValue;
					simpleContainer = new ComputationValueImagePanel(
							computationValueImage);
					fieldLabel = new FieldLabel(simpleContainer, key);
					fieldLabel.setLabelWidth(200);
					fieldLabel.setLabelWordWrap(true);
					break;
				case FileList:
					ComputationValueFileList computationValueFileList = (ComputationValueFileList) computationValue;
					simpleContainer = new ComputationValueFileListPanel(
							computationValueFileList);
					fieldLabel = new FieldLabel(simpleContainer, key);
					fieldLabel.setLabelWidth(200);
					fieldLabel.setLabelWordWrap(true);
				case String:
				default:
					TextField textField = new TextField();
					textField.setValue(computationValue.getValue());
					textField.setReadOnly(true);
					fieldLabel = new FieldLabel(textField, key);
					fieldLabel.setLabelWidth(200);
					fieldLabel.setLabelWordWrap(true);
					break;

				}
				outputVBox.add(fieldLabel, new VerticalLayoutData(1, -1,
						new Margins(0, 4, 0, 4)));
			}

			FieldSet outputFieldSet = new FieldSet();
			outputFieldSet.setHeadingText("Output Result");
			outputFieldSet.setCollapsible(true);
			outputFieldSet.add(outputVBox);
			outputFieldSet.getElement().getStyle().setMarginRight(20, Unit.PX);
			return outputFieldSet;
		} catch (Throwable e) {
			Log.error("Error in ComputationsViewerPanel creating output view: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		}
	}

}
