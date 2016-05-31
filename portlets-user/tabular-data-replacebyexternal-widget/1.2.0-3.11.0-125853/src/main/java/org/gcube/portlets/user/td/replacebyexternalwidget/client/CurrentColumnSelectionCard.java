package org.gcube.portlets.user.td.replacebyexternalwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalSession;
import org.gcube.portlets.user.td.replacebyexternalwidget.client.grid.CurrentColumnGridPanel;
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
public class CurrentColumnSelectionCard extends WizardCard {

	private ReplaceByExternalSession replaceByExternalSession;
	private CurrentColumnSelectionCard thisCard;
	private CurrentColumnGridPanel currentColumnGridPanel;
	private String colname;

	public ReplaceByExternalSession getReplaceByExternalSession() {
		return replaceByExternalSession;
	}

	public CurrentColumnSelectionCard(
			final ReplaceByExternalSession replaceByExternalSession) {
		super("Current Column Selection", "");
		this.thisCard=this;
		if (replaceByExternalSession == null) {
			Log.error("ReplaceByExternalSession  is null");
		}
		this.replaceByExternalSession = replaceByExternalSession;

		FormPanel panel = createPanel();
		setContent(panel);

	}

	public CurrentColumnSelectionCard(String colname,
			final ReplaceByExternalSession replaceByExternalSession) {
		super("Current Column Selection", "");
		this.thisCard=this;
		this.colname=colname;
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
		currentColumnGridPanel = new CurrentColumnGridPanel(colname,this);

		currentColumnGridPanel
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					public void onSelection(SelectionEvent<ColumnData> event) {
						
					}

				});

		content.add(currentColumnGridPanel);

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

		ColumnData currentColumn = currentColumnGridPanel.getSelectedItem();
		if (currentColumn == null) {
			d = new AlertMessageBox("Attention", "No columns selected");
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
		} else {
			replaceByExternalSession.setCurrentColumn(currentColumn);
			goNext();
		}

	}

	protected void goNext() {
		try {
			Log.info("NextCard TabResourcesSelectionCard");
			TabResourcesSelectionCard tabResourcesSelectionCard = new TabResourcesSelectionCard(
					replaceByExternalSession);
			getWizardWindow().addCard(
					tabResourcesSelectionCard);
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
