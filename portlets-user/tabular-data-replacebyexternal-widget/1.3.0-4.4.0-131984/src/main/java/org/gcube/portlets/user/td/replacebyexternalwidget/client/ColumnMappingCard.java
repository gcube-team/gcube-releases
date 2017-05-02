package org.gcube.portlets.user.td.replacebyexternalwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalColumnsMapping;
import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Command;

public class ColumnMappingCard extends WizardCard {
	protected ReplaceByExternalSession replaceByExternalSession;
	protected ColumnMappingCard thisCard;
	protected ColumnMappingPanel columnMappingPanel;

	public ColumnMappingCard(final ReplaceByExternalSession replaceByExternalSession) {
		super("Mapping beetween Tabular Resources", "");
		Log.debug("ColumnMappingCard");
		this.replaceByExternalSession = replaceByExternalSession;
		thisCard = this;

		columnMappingPanel = new ColumnMappingPanel(thisCard,res);
		
		setContent(columnMappingPanel);

	}

	@Override
	public void setup() {
		Log.debug("ColumnMappingCard Call Setup ");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("ColumnMappingCard Call sayNextCard");
				setMapping();
				
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.debug("Remove ColumnMappingCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setEnableNextButton(true);
		getWizardWindow().setEnableBackButton(true);
		
	}
	
	
	
	public ReplaceByExternalSession getReplaceByExternalSession() {
		return replaceByExternalSession;
	}

	public void setReplaceByExternalSession(
			ReplaceByExternalSession replaceByExternalSession) {
		this.replaceByExternalSession = replaceByExternalSession;
	}

	protected void setMapping() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		
		ArrayList<ReplaceByExternalColumnsMapping>  columnsMatch=columnMappingPanel.getSelectedMap();
		if(columnsMatch==null || columnsMatch.size()<=0){
			Log.debug("No columns match created: "
				+columnsMatch);
			UtilsGXT3
					.info("Attentions",
							"Creates a valid column map");
			getWizardWindow().setEnableNextButton(true);
			getWizardWindow().setEnableBackButton(true);
			
		} else {
			Log.debug("ColumnsMatch created: "+columnsMatch);
			replaceByExternalSession.setColumnsMatch(columnsMatch);
			replaceByExternalSession.setExternalColumns(columnMappingPanel.getExternalColumns());
			goNext();
		}
		
		
	}

	protected void goNext() {
		try {
			Log.info("NextCard ReplaceColumnSelectionCard");
			ReplaceColumnSelectionCard replaceColumnSelectionCard = new ReplaceColumnSelectionCard(
					replaceByExternalSession);
			getWizardWindow().addCard(
					replaceColumnSelectionCard);
			getWizardWindow().nextCard();
		} catch (Throwable e) {
			Log.error("goNext: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

}
