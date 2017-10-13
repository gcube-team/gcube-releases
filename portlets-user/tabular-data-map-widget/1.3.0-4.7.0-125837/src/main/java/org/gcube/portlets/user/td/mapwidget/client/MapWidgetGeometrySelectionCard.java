package org.gcube.portlets.user.td.mapwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.map.MapCreationSession;
import org.gcube.portlets.user.td.mapwidget.client.grid.GeometrySelectionGridPanel;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
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
public class MapWidgetGeometrySelectionCard extends WizardCard {

	protected MapCreationSession mapCreationSession;
	private MapWidgetGeometrySelectionCard thisCard;
	private GeometrySelectionGridPanel geometrySelectionGridPanel;

	public MapWidgetGeometrySelectionCard(
			final MapCreationSession mapCreationSession) {
		super("Geometry Selection", "");
		this.thisCard=this;
		if (mapCreationSession == null) {
			Log.error("MapCreationSession is null");
		}
		this.mapCreationSession = mapCreationSession;

		FormPanel panel = createPanel();
		setContent(panel);

	}

	protected FormPanel createPanel() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));

		VerticalLayoutContainer content = new VerticalLayoutContainer();
		panel.add(content);

		// Column Selection Grid
		geometrySelectionGridPanel = new GeometrySelectionGridPanel(this);

		geometrySelectionGridPanel
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					public void onSelection(SelectionEvent<ColumnData> event) {
						
					}

				});

		content.add(geometrySelectionGridPanel);

		return panel;
	}

	@Override
	public void setup() {
		Log.debug("MapWidgetConfigCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("MapWidgetConfigCard Call sayNextCard");
				checkData();
			}

		};
		
		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove MapWidgetGeometrySelectionCard");
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

		ColumnData columnGeo = geometrySelectionGridPanel.getSelectedItem();
		if (columnGeo == null) {
			d = new AlertMessageBox("Attention", "No columns selected");
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
		} else {
			mapCreationSession.setGeometry(columnGeo);
			goNext();
		}

	}

	protected void goNext() {
		try {
		    MapWidgetDetailCard mapWidgetDetailCard = new MapWidgetDetailCard(
					mapCreationSession);
			getWizardWindow().addCard(mapWidgetDetailCard);
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
		}
	}

	@Override
	public void dispose() {

	}

	public MapCreationSession getMapCreationSession() {
		return mapCreationSession;
	}

}
