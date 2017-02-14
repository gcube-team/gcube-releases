package org.gcube.portlets.user.td.chartswidget.client;



import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.chart.ChartSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;



/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ChartsWidgetTD  extends WizardWindow  {

	protected ChartSession chartSession;
	
	/**
	 * 
	 * @param title
	 * @param eventBus
	 */
	public ChartsWidgetTD(TRId trId, UserInfo userInfo, String title, EventBus eventBus)	{
		super(title,eventBus);
		Log.debug("ChartWidgetTD");
		chartSession= new ChartSession();
		chartSession.setTrId(trId);
		chartSession.setUserInfo(userInfo);
		retrieveColumns();
	}
	
	
	private void startChartsCreation() {
		ChartSelectionCard chartSelectionCard=new ChartSelectionCard(chartSession);
		addCard(chartSelectionCard);
		chartSelectionCard.setup();
		show();
	}

	
	protected void retrieveColumns(){
		TDGWTServiceAsync.INSTANCE
		.getColumns(new AsyncCallback<ArrayList<ColumnData>>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus
							.fireEvent(
									new SessionExpiredEvent(
											SessionExpiredType.EXPIREDONSERVER));
				} else {
					if (caught instanceof TDGWTIsLockedException) {
						Log.error(caught.getLocalizedMessage());
						showErrorAndHide("Error Locked",
								caught.getLocalizedMessage(),"", caught);
					} else {
						Log.error("Error Retrieving columns: "
								+ caught.getLocalizedMessage());
						showErrorAndHide("Error","Error Retrieving Columns,",
								caught.getLocalizedMessage(), caught);
						
					}
				}
			}

			public void onSuccess(ArrayList<ColumnData> result) {
				Log.trace("loaded " + result.size() + " columns");
				chartSession.setColumns(result);
				startChartsCreation();
			}

		
		});
	}
}