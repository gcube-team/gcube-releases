package org.gcube.portlets.user.td.codelistmappingimportwidget.client;

import org.gcube.portlets.user.td.codelistmappingimportwidget.client.grid.ColumnDataGridPanel;
import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
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
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ColumnSelectionCard extends WizardCard {
	private static CodelistMappingMessages msgs = GWT
			.create(CodelistMappingMessages.class);
	private ColumnSelectionCard thisCard;
	private CodelistMappingSession codelistMappingSession;
	private ColumnDataGridPanel columnsGridPanel;
	private CommonMessages msgsCommon;

	public ColumnSelectionCard(
			final CodelistMappingSession codelistMappingSession) {
		super(msgs.columnSelectionCardHead(), "");
		thisCard = this;
		initMessages();
		if (codelistMappingSession == null) {
			Log.error("CodelistMappingSession is null");
		}
		this.codelistMappingSession = codelistMappingSession;

		FormPanel panel = createPanel();
		setContent(panel);

	}
	
	protected void initMessages(){
		msgsCommon = GWT.create(CommonMessages.class);
	}
	

	protected FormPanel createPanel() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));

		VerticalLayoutContainer content = new VerticalLayoutContainer();
		panel.add(content);

		TRId trId = codelistMappingSession.getConnectedTR().getTrId();
		if (trId == null) {
			Log.error("CodelistMappingSession has TRId null: "
					+ codelistMappingSession.getConnectedTR());
		}

		columnsGridPanel = new ColumnDataGridPanel(this, trId);

		columnsGridPanel
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					public void onSelection(SelectionEvent<ColumnData> event) {
						codelistMappingSession
								.setConnectedColumn(columnsGridPanel
										.getSelectedItem());
						setEnableNextButton(true);
					}

				});

		content.add(columnsGridPanel);

		return panel;
	}

	@Override
	public void setup() {
		Log.debug("ColumnSelectionCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("ColumnSelectionCard Call sayNextCard");
				checkData();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.debug("Remove ColumnSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		setEnableNextButton(false);
		setEnableBackButton(true);

	}

	protected void checkData() {
		setEnableNextButton(false);
		setEnableBackButton(false);
		AlertMessageBox d;
		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				setEnableNextButton(false);
				setEnableBackButton(true);

			}
		};

		ColumnData column = codelistMappingSession.getConnectedColumn();
		if (column == null) {
			d = new AlertMessageBox(msgsCommon.attention(), msgs.attentionNoColumnsSelected());
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
		} else {
			goNext();
		}

	}

	protected void goNext() {
		try {
			CodelistMappingTableDetailCard detailsCard = new CodelistMappingTableDetailCard(
					codelistMappingSession);
			getWizardWindow().addCard(detailsCard);
			Log.info("NextCard CodelistMappingTableDetailCard");
			getWizardWindow().nextCard();
		} catch (Throwable e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
		}
	}

	@Override
	public void dispose() {

	}

}
