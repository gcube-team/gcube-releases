package org.gcube.portlets.user.td.chartswidget.client.toprating;

import org.gcube.portlets.user.td.gwtservice.shared.chart.ChartSession;
import org.gcube.portlets.user.td.gwtservice.shared.chart.ChartTopRatingSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.FormPanel;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TopRatingColumnSelectionCard extends WizardCard {

	private ChartTopRatingSession chartTopRatingSession;
	private TopRatingColumnSelectionCard thisCard;
	private TopRatingColumnSelectionGridPanel topRatingColumnSelectionGridPanel;
	private ChartSession chartSession;

	public TopRatingColumnSelectionCard(final ChartSession chartSession,
		final ChartTopRatingSession chartTopRatingSession) {
		super("Dimension Column Selection", "");
		this.thisCard=this;
		if (chartTopRatingSession == null) {
			Log.error("ChartTopRatingSession is null");
		}
		this.chartSession=chartSession;
		this.chartTopRatingSession = chartTopRatingSession;
		
		FormPanel panel = createPanel();
		setCenterWidget(panel, new MarginData(0));

	}

	protected FormPanel createPanel() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));

		VerticalLayoutContainer content = new VerticalLayoutContainer();
		panel.add(content);

		// Column Selection Grid
		topRatingColumnSelectionGridPanel = new TopRatingColumnSelectionGridPanel(this, chartSession);

		topRatingColumnSelectionGridPanel
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					public void onSelection(SelectionEvent<ColumnData> event) {
						
					}

				});

		content.add(topRatingColumnSelectionGridPanel);

		return panel;
	}

	@Override
	public void setup() {
		Log.debug("TopRatingColumnSelectionCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("TopRatingColumnSelectionCard Call sayNextCard");
				checkData();
			}

		};
		
		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove TopRatingColumnSelectionCard");
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
		AlertMessageBox d;
		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(true);

			}
		};

		ColumnData columnSelected = topRatingColumnSelectionGridPanel.getSelectedItem();
		if (columnSelected == null) {
			d = new AlertMessageBox("Attention", "No columns selected");
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
		} else {
			chartTopRatingSession.setColumn(columnSelected);
			goNext();
		}

	}

	protected void goNext() {
		try {
		    TopRatingConfigCard topRatingConfigCard = new TopRatingConfigCard(
					chartTopRatingSession);
			getWizardWindow().addCard(topRatingConfigCard);
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
		}
	}

	@Override
	public void dispose() {

	}

	
}
