package org.gcube.portlets.user.td.mapwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.map.MapCreationSession;
import org.gcube.portlets.user.td.mapwidget.client.grid.ColumnDataGridPanel;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.tips.ToolTip;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class MapWidgetConfigCard extends WizardCard {

	private static final int LABEL_WIDTH = 128;
	private static final int LABEL_PAD_WIDTH = 2;

	protected MapCreationSession mapCreationSession;

	private Radio viewColumnMapTrue;
	private Radio viewColumnMapFalse;

	private ColumnDataGridPanel mapColumnGridPanel;

	public MapWidgetConfigCard(final MapCreationSession mapCreationSession) {
		super("Select column for GIS features", "");

		if (mapCreationSession == null) {
			Log.error("MapCreationSession is null");
		}
		this.mapCreationSession = mapCreationSession;

		FormPanel panel = createPanel();
		setCenterWidget(panel);

	}

	protected FormPanel createPanel() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));

		VerticalLayoutContainer verticalLayout = new VerticalLayoutContainer();
		panel.add(verticalLayout);

		// Export View Column
		viewColumnMapTrue = new Radio();
		viewColumnMapTrue.setBoxLabel("True");
		viewColumnMapTrue.setValue(true);

		viewColumnMapFalse = new Radio();
		viewColumnMapFalse.setBoxLabel("False");

		ToggleGroup mapViewColumnGroup = new ToggleGroup();
		mapViewColumnGroup.add(viewColumnMapTrue);
		mapViewColumnGroup.add(viewColumnMapFalse);

		HorizontalPanel mapViewColumnPanel = new HorizontalPanel();
		mapViewColumnPanel.add(viewColumnMapTrue);
		mapViewColumnPanel.add(viewColumnMapFalse);

		new ToolTip(mapViewColumnPanel, new ToolTipConfig(
				"Use View Columns Too"));
		FieldLabel fieldViewColumns = new FieldLabel(mapViewColumnPanel,
				"Use View Columns");
		fieldViewColumns.setLabelWidth(LABEL_WIDTH);
		fieldViewColumns.setLabelPad(LABEL_PAD_WIDTH);
		verticalLayout.add(fieldViewColumns);

		// Column Selection Grid
		mapColumnGridPanel = new ColumnDataGridPanel(this);

		mapColumnGridPanel
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					public void onSelection(SelectionEvent<ColumnData> event) {

					}

				});

		verticalLayout.add(mapColumnGridPanel);

		return panel;
	}

	protected boolean useViewColumnsInMap() {
		if (viewColumnMapTrue.getValue()) {
			return true;
		} else {
			return false;
		}
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

		getWizardWindow().setNextButtonCommand(sayNextCard);
		setEnableBackButton(false);
		setEnableNextButton(true);
	}

	protected void checkData() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		AlertMessageBox d;
		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(false);

			}
		};

		ColumnData column = mapColumnGridPanel.getSelectedItem();
		if (column == null) {
			d = new AlertMessageBox("Attention", "No columns selected");
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
		} else {
			ArrayList<ColumnData> columns=new ArrayList<ColumnData>();
			columns.add(column);
			mapCreationSession.setFeature(columns);
			mapCreationSession.setUseView(useViewColumnsInMap());
			goNext();
		}

	}

	protected void goNext() {
		try {
			if (mapCreationSession.getCountGeometryColumns() == 1) {
				MapWidgetDetailCard mapWidgetDetailCard = new MapWidgetDetailCard(
						mapCreationSession);
				getWizardWindow().addCard(mapWidgetDetailCard);
				getWizardWindow().nextCard();
			} else {
			    MapWidgetGeometrySelectionCard mapGeometrySeslectionCard = new MapWidgetGeometrySelectionCard(
						mapCreationSession);
				getWizardWindow().addCard(mapGeometrySeslectionCard);
				getWizardWindow().nextCard();
			}
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
