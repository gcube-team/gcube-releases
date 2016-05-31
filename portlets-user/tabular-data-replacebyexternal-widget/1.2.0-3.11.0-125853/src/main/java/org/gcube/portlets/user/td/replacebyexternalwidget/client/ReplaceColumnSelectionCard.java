package org.gcube.portlets.user.td.replacebyexternalwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalSession;
import org.gcube.portlets.user.td.replacebyexternalwidget.client.grid.ReplaceColumnGridPanel;
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
public class ReplaceColumnSelectionCard extends WizardCard {

	protected ReplaceByExternalSession replaceByExternalSession;
	private ReplaceColumnSelectionCard thisCard;
	private ReplaceColumnGridPanel replaceColumnGridPanel;

	public ReplaceByExternalSession getReplaceByExternalSession() {
		return replaceByExternalSession;
	}

	public ReplaceColumnSelectionCard(
			final ReplaceByExternalSession replaceByExternalSession) {
		super("Replace Column Selection", "");
		this.thisCard=this;
		if (replaceByExternalSession == null) {
			Log.error("ReplaceByExternalSession  is null");
		}
		this.replaceByExternalSession = replaceByExternalSession;

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
		replaceColumnGridPanel = new ReplaceColumnGridPanel(this);

		replaceColumnGridPanel
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					public void onSelection(SelectionEvent<ColumnData> event) {
						
					}

				});

		content.add(replaceColumnGridPanel);

		return panel;
	}

	@Override
	public void setup() {
		Log.debug("ReplaceColumnSelectionCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("ReplaceColumnSelectionCard Call sayNextCard");
				checkData();
			}

		};
		
		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove ReplaceColumnSelectionCard");
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

		ColumnData columnReplace = replaceColumnGridPanel.getSelectedItem();
		if (columnReplace == null) {
			d = new AlertMessageBox("Attention", "No columns selected");
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
		} else {
			replaceByExternalSession.setReplaceColumn(columnReplace);
			goNext();
		}

	}

	protected void goNext() {
		try {
			Log.info("NextCard ReplaceByExternalOperationInProgressCard");
			ReplaceByExternalOperationInProgressCard progressCard = new ReplaceByExternalOperationInProgressCard(
					replaceByExternalSession);
			getWizardWindow().addCard(
					progressCard);
			getWizardWindow().nextCard();
		} catch (Throwable e) {
			Log.error("goNext: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {

	}

	

}
