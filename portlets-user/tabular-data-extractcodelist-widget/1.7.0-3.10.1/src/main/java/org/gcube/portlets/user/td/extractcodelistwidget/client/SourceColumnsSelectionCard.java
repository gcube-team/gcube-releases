package org.gcube.portlets.user.td.extractcodelistwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.extractcodelistwidget.client.grid.ColumnDataGridPanel;
import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistSession;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
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
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SourceColumnsSelectionCard extends WizardCard {
	private static ExtractCodelistMessages msgs = GWT
			.create(ExtractCodelistMessages.class);
	private CommonMessages msgsCommon;
	private ExtractCodelistSession extractCodelistSession;
	private ColumnDataGridPanel columnsGridPanel;

	public SourceColumnsSelectionCard(
			final ExtractCodelistSession extractCodelistSession) {
		super(msgs.sourceColumnsSelectionCardHead(), "");
		if (extractCodelistSession == null) {
			Log.error("ExtractCodelistSession is null");
		}
		this.extractCodelistSession = extractCodelistSession;

		initMessages();
		createPanel();

	}

	protected void initMessages() {
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void createPanel() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));

		VerticalLayoutContainer content = new VerticalLayoutContainer();
		panel.add(content);

		columnsGridPanel = new ColumnDataGridPanel(this);

		columnsGridPanel
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					public void onSelection(SelectionEvent<ColumnData> event) {

					}

				});

		content.add(columnsGridPanel);

		setContent(panel);

	}

	@Override
	public void setup() {
		Log.debug("SourceColumnsSelectionCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("SourceColumnsSelectionCard Call sayNextCard");
				checkData();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);
		setEnableBackButton(false);
		setBackButtonVisible(false);
		setEnableNextButton(true);
		setNextButtonVisible(true);
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

		ArrayList<ColumnData> columns = columnsGridPanel.getSelectedItems();
		if (columns.size() == 0) {
			d = new AlertMessageBox(msgsCommon.attention(),
					msgs.attentionNoColumnSelected());
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
		} else {
			extractCodelistSession.setSourceColumns(columns);
			goNext();
		}

	}

	protected void goNext() {
		try {
			TargetColumnsSelectionCard destCard = new TargetColumnsSelectionCard(
					extractCodelistSession);
			getWizardWindow().addCard(destCard);
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {

	}

}
