package org.gcube.portlets.user.td.chartswidget.client.toprating;

import org.gcube.portlets.user.td.chartswidget.client.properties.ValueOperationTypePropertiesCombo;
import org.gcube.portlets.user.td.chartswidget.client.store.ValueOperationElement;
import org.gcube.portlets.user.td.chartswidget.client.store.ValueOperationStore;
import org.gcube.portlets.user.td.chartswidget.client.store.ValueOperationType;
import org.gcube.portlets.user.td.gwtservice.shared.chart.ChartTopRatingSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TopRatingConfigCard extends WizardCard {

	private TopRatingConfigCard thisCard;
	private ChartTopRatingSession chartTopRatingSession;
	private ComboBox<ValueOperationElement> comboOperationValue;
	private FieldLabel comboOperationValueLabel;
	private ValueOperationType valueOperationTypeSelected;
	private TextField sampleSize;

	public TopRatingConfigCard(final ChartTopRatingSession chartTopRatingSession) {
		super("Top Rating Config", "");
		this.thisCard = this;
		if (chartTopRatingSession == null) {
			Log.error("TopRatingSession is null");
		}
		this.chartTopRatingSession = chartTopRatingSession;
		valueOperationTypeSelected = null;
		FormPanel panel = createPanel();
		setContent(panel);

	}

	protected FormPanel createPanel() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));

		VerticalLayoutContainer content = new VerticalLayoutContainer();
		panel.add(content);

		// comboOperationValue
		ValueOperationTypePropertiesCombo props = GWT
				.create(ValueOperationTypePropertiesCombo.class);
		ListStore<ValueOperationElement> storeComboValueOperation = new ListStore<ValueOperationElement>(
				props.id());
		storeComboValueOperation.addAll(ValueOperationStore
				.getValuesOperationType());

		comboOperationValue = new ComboBox<ValueOperationElement>(
				storeComboValueOperation, props.label());
		Log.trace("ComboOperationValue created");

		addHandlersForComboChartType(props.label());

		comboOperationValue.setEmptyText("Select a chart type...");
		comboOperationValue.setWidth(191);
		comboOperationValue.setTypeAhead(true);
		comboOperationValue.setTriggerAction(TriggerAction.ALL);

		comboOperationValueLabel = new FieldLabel(comboOperationValue,
				"Chart Type");

		// Field SampleSize
		sampleSize = new TextField();
		sampleSize.setValue("25");

		FieldLabel sampleSizeLabel = new FieldLabel(sampleSize, "Sample Size");

		//
		content.add(comboOperationValueLabel, new VerticalLayoutData(1, -1,
				new Margins(0)));
		content.add(sampleSizeLabel, new VerticalLayoutData(1, -1, new Margins(
				0)));

		return panel;
	}

	private void addHandlersForComboChartType(
			final LabelProvider<ValueOperationElement> labelProvider) {
		comboOperationValue
				.addSelectionHandler(new SelectionHandler<ValueOperationElement>() {
					public void onSelection(
							SelectionEvent<ValueOperationElement> event) {
						Info.display(
								"Operation Selected",
								"You selected "
										+ (event.getSelectedItem() == null ? "nothing"
												: labelProvider.getLabel(event
														.getSelectedItem())
														+ "!"));
						Log.debug("Combo selected: " + event.getSelectedItem());
						ValueOperationElement measureType = event
								.getSelectedItem();
						updateValueOperation(measureType.getType());
					}

				});

	}

	private void updateValueOperation(ValueOperationType type) {
		valueOperationTypeSelected = type;
	}

	@Override
	public void setup() {
		Log.debug("TopRatingConfigCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("TopRatingConfigCard Call sayNextCard");
				checkData();
			}

		};

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove TopRatingConfigCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setNextButtonCommand(sayNextCard);

		setEnableBackButton(true);
		setEnableNextButton(true);
	}

	protected void checkData() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);

		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(true);

			}
		};

		if (valueOperationTypeSelected == null) {
			AlertMessageBox d = new AlertMessageBox("Attention",
					"No operation selected");
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
			return;
		} else {
			chartTopRatingSession.setValueOperation(valueOperationTypeSelected
					.toString());
			if (sampleSize.getValue() == null
					|| sampleSize.getValue().isEmpty()) {
				showIntegerAlert();
				return;
			} else {
				Integer sSize=25;
				try {
					sSize = new Integer(sampleSize.getValue());
				} catch (NumberFormatException e) {
					showIntegerAlert();
					return;
				}
				chartTopRatingSession.setSampleSize(sSize);
				goNext();
				
			}
		}

	}

	protected void showIntegerAlert() {
		AlertMessageBox d = new AlertMessageBox("Attention",
				"Insert a valid sample size (Integer)!");
		d.addHideHandler(new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(true);

			}
		});
		d.setModal(false);
		d.show();
		return;
	}

	protected void goNext() {
		try {
			TopRatingOperationInProgressCard topRatingOperationInProgressCard = new TopRatingOperationInProgressCard(
					chartTopRatingSession);
			getWizardWindow().addCard(topRatingOperationInProgressCard);
			getWizardWindow().nextCard();

		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
		}

	}

	@Override
	public void dispose() {

	}

}
