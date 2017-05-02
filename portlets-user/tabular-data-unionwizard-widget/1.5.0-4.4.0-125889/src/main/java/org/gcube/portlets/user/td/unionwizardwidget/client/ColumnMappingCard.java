package org.gcube.portlets.user.td.unionwizardwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.union.UnionColumnsMapping;
import org.gcube.portlets.user.td.gwtservice.shared.tr.union.UnionSession;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ColumnMappingCard extends WizardCard {
	private static UnionWizardMessages msgs= GWT.create(UnionWizardMessages.class);
	private UnionSession unionSession;
	private ColumnMappingCard thisCard;
	private ColumnMappingPanel columnMappingPanel;
	private CommonMessages msgsCommon;

	public ColumnMappingCard(final UnionSession unionSession) {
		super(msgs.columnMappingCardHead(), "");
		Log.debug("ColumnMappingCard");
		this.unionSession = unionSession;
		thisCard = this;
		initMessages();
		
		columnMappingPanel = new ColumnMappingPanel(thisCard,res);
		setContent(columnMappingPanel);

	}
	
	protected void initMessages(){
		msgsCommon= GWT.create(CommonMessages.class);
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
		setBackButtonVisible(true);
		setNextButtonVisible(true);
		
	}

	protected void setMapping() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		
		ArrayList<UnionColumnsMapping>  columnsMatch=columnMappingPanel.getSelectedMap();
		if(columnsMatch==null){
			Log.debug("No columns match created: "
				+columnsMatch);
			UtilsGXT3
					.info(msgsCommon.attention(),
							msgs.attentionCreatesAValidColumnMap());
			getWizardWindow().setEnableNextButton(true);
			getWizardWindow().setEnableBackButton(true);
			
		} else {
			Log.debug("ColumnsMatch created: "+columnsMatch);
			unionSession.setColumnsMatch(columnsMatch);
			goNext();
		}
		
		
	}

	protected void goNext() {
		try {
			Log.info("NextCard ColumnSelectionCard");
			UnionOperationInProgressCard progressCard = new UnionOperationInProgressCard(
					unionSession);
			getWizardWindow().addCard(
					progressCard);
			getWizardWindow().nextCard();
		} catch (Throwable e) {
			Log.error("goNext: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	public UnionSession getUnionSession(){
		return unionSession;
	}
	

}
