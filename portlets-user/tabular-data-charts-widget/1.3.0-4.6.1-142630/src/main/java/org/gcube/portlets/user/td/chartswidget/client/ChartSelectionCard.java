package org.gcube.portlets.user.td.chartswidget.client;

import org.gcube.portlets.user.td.chartswidget.client.help.ChartHelp;
import org.gcube.portlets.user.td.chartswidget.client.properties.ChartTypePropertiesCombo;
import org.gcube.portlets.user.td.chartswidget.client.store.ChartTypeElement;
import org.gcube.portlets.user.td.chartswidget.client.store.ChartTypeStore;
import org.gcube.portlets.user.td.chartswidget.client.toprating.TopRatingColumnSelectionCard;
import org.gcube.portlets.user.td.gwtservice.shared.chart.ChartSession;
import org.gcube.portlets.user.td.gwtservice.shared.chart.ChartTopRatingSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.charts.ChartType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TableType;
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
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ChartSelectionCard extends WizardCard {

	private ChartSelectionCard thisCard;
	private ChartSession chartSession;
	private ChartType chartTypeSelected;
	private ComboBox<ChartTypeElement> comboChartType;
	private FieldLabel comboChartTypeLabel;
	private ChartHelp chartHelp;
	

	public ChartSelectionCard(final ChartSession chartSession) {
		super("Chart Selection", "");
		this.thisCard = this;
		if (chartSession == null) {
			Log.error("ChartSession is null");
		}
		this.chartSession = chartSession;
		this.chartTypeSelected = null;
		FormPanel panel = createPanel();
		setCenterWidget(panel, new MarginData(0));

	}

	protected FormPanel createPanel() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));

		VerticalLayoutContainer content = new VerticalLayoutContainer();
		panel.add(content);

		// comboChartType
		ChartTypePropertiesCombo propsChartType = GWT
				.create(ChartTypePropertiesCombo.class);
		ListStore<ChartTypeElement> storeComboChartType = new ListStore<ChartTypeElement>(
				propsChartType.id());
		storeComboChartType.addAll(ChartTypeStore.getChartsType());

		comboChartType = new ComboBox<ChartTypeElement>(storeComboChartType,
				propsChartType.label());
		Log.trace("ComboMeasureType created");

		addHandlersForComboChartType(propsChartType.label());

		comboChartType.setEmptyText("Select a chart type...");
		comboChartType.setWidth(191);
		comboChartType.setTypeAhead(true);
		comboChartType.setTriggerAction(TriggerAction.ALL);

		comboChartTypeLabel = new FieldLabel(comboChartType, "Chart Type");
		
		chartHelp=new ChartHelp();

		content.add(comboChartTypeLabel, new VerticalLayoutData(1, -1,
				new Margins(0)));
		content.add(chartHelp, new VerticalLayoutData(1,-1, new Margins(0)));
		return panel;
	}

	private void addHandlersForComboChartType(
			final LabelProvider<ChartTypeElement> labelProvider) {
		comboChartType
				.addSelectionHandler(new SelectionHandler<ChartTypeElement>() {
					public void onSelection(
							SelectionEvent<ChartTypeElement> event) {
						Info.display(
								"Chart Selected",
								"You selected "
										+ (event.getSelectedItem() == null ? "nothing"
												: labelProvider.getLabel(event
														.getSelectedItem())
														+ "!"));
						Log.debug("ComboChartType selected: "
								+ event.getSelectedItem());
						ChartTypeElement measureType = event.getSelectedItem();
						updateChartType(measureType.getType());
					}

				});

	}

	private void updateChartType(ChartType type) {
		chartTypeSelected = type;
		chartHelp.updateChartType(type);
	}

	@Override
	public void setup() {
		Log.debug("ChartSelectionCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("ChartSelectionCard Call sayNextCard");
				checkData();
			}

		};

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove ChartSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setNextButtonCommand(sayNextCard);

		setEnableBackButton(false);
		setEnableNextButton(true);
	}

	protected void checkData() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		
		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(false);

			}
		};

		if(chartTypeSelected==null){
			AlertMessageBox d = new AlertMessageBox("Attention",
					"No chart selected");
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
			return;
		}
		
		switch (chartTypeSelected) {
		case TopRating:
			checkTopRating();
			break;
		default:	
			AlertMessageBox d = new AlertMessageBox("Attention",
					"No chart selected");
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
			break;

		}

	}

	protected void checkTopRating() {
		String tableTypeName = chartSession.getTrId().getTableTypeName();
		if (tableTypeName.compareTo(TableType.DATASET.toString()) == 0) {
			try {
				ChartTopRatingSession chartTopRatingSession = new ChartTopRatingSession();
				chartTopRatingSession.setTrId(chartSession.getTrId());
				TopRatingColumnSelectionCard topRatingColumnSelectionCard = new TopRatingColumnSelectionCard(
						chartSession, chartTopRatingSession);
				getWizardWindow().addCard(topRatingColumnSelectionCard);
				getWizardWindow().nextCard();

			} catch (Exception e) {
				Log.error("sayNextCard :" + e.getLocalizedMessage());
			}
		} else {
			AlertMessageBox d = new AlertMessageBox("Attention",
					"Chart not applicable, table is not a Dataset!");
			d.addHideHandler(new HideHandler() {

				public void onHide(HideEvent event) {
					getWizardWindow().setEnableNextButton(true);
					getWizardWindow().setEnableBackButton(false);

				}
			});
			d.setModal(false);
			d.show();
		}

	}

	@Override
	public void dispose() {

	}

}
