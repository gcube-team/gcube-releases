/**
 * 
 */
package org.gcube.portlets.user.td.excelexportwidget.client;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.excel.ExcelExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TableType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class MeasureColumnSelectionCard extends WizardCard {

	private MeasureColumnSelectionCard thisCard;
	private ExcelExportSession exportSession;
	private MeasureColumnSelectionPanel measureColumnSelectionPanel;

	// private Agencies agency;

	public MeasureColumnSelectionCard(final ExcelExportSession exportSession) {
		super("Measure column selection", "");

		this.exportSession = exportSession;
		thisCard = this;
		
		final AutoProgressMessageBox box = new AutoProgressMessageBox("Wait", "Retrieving Information, please wait...");
		box.setProgressText("Retrieving...");
		box.auto();
		box.show();
		retrieveTabularDataInfo(box);
		
	
	}
	
	
	public void init(){

		this.measureColumnSelectionPanel = new MeasureColumnSelectionPanel(
				thisCard, res, exportSession);

		measureColumnSelectionPanel
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					public void onSelection(SelectionEvent<ColumnData> event) {
						exportSession
								.setObsValueColumn(measureColumnSelectionPanel
										.getSelectedItem());
						getWizardWindow().setEnableNextButton(true);

					}

				});

		setContent(measureColumnSelectionPanel);
		forceLayout();

	}
	
	private void retrieveTabularDataInfo(final AutoProgressMessageBox box) {
		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(new AsyncCallback<TabResource>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					getEventBus().fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
				} else {
					if (caught instanceof TDGWTIsLockedException) {
						Log.error(caught.getLocalizedMessage());
						showErrorAndHide("Error Locked", caught.getLocalizedMessage(), "", caught);
					} else {
						Log.error(
								"No Tabular Resource Information retrived from server " + caught.getLocalizedMessage());
						box.hide();
						showErrorAndHide("Error", "Error retrieving tabular resource information: ",
								caught.getLocalizedMessage(), caught);
					}
				}
			}

			public void onSuccess(TabResource result) {
				Log.debug("Tabular Resource Information retrieved");
				exportSession.setTabResource(result);
				exportSession.setExportType(TableType.getColumnDataTypeFromId(result.getTableTypeName()));
				exportSession.setAgencyId(result.getAgency());
				box.hide();
				init();
			}
		});
	}
	
	

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				ExcelTableDetailCard sdmxTableDetailCard = new ExcelTableDetailCard(
						exportSession);
				getWizardWindow().addCard(sdmxTableDetailCard);
				Log.info("NextCard ExcelTableDetailCard");
				getWizardWindow().nextCard();

			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove measureColumnSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		// getWizardWindow().setEnableNextButton(false);

		setBackButtonVisible(false);
		setEnableBackButton(false);

		if (measureColumnSelectionPanel == null
				|| measureColumnSelectionPanel.getSelectedItem() == null) {
			setEnableNextButton(false);
		} else {
			setEnableNextButton(true);
		}
	}

}
