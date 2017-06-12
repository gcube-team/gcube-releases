package org.gcube.portlets.user.td.mapwidget.client;



import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.map.MapCreationSession;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
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
public class MapWidgetTD  extends WizardWindow  {

	protected MapCreationSession mapCreationSession;
	
	/**
	 * 
	 * @param title
	 * @param eventBus
	 */
	public MapWidgetTD(TRId trId, UserInfo userInfo, String title, EventBus eventBus)	{
		super(title,eventBus);
		
		mapCreationSession= new MapCreationSession();
		mapCreationSession.setTrId(trId);
		mapCreationSession.setUsername(userInfo.getUsername());
		retrieveColumns();
	}
	
	
	private void startMapCreation() {
		MapWidgetConfigCard mapWidgetConfigCard=new MapWidgetConfigCard(mapCreationSession);
		addCard(mapWidgetConfigCard);
		mapWidgetConfigCard.setup();
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
								caught.getLocalizedMessage(), "", caught);
					} else {
						Log.error("No load columns: "
								+ caught.getLocalizedMessage());
						showErrorAndHide("Error","No load columns.",
								caught.getLocalizedMessage(), caught);
					}
				}
			}

			public void onSuccess(ArrayList<ColumnData> result) {
				Log.trace("loaded " + result.size() + " columns");
				ArrayList<ColumnData> geometryColumnsList=new ArrayList<ColumnData>();
				boolean existsGeometryColumn=false;
				int countGeometryColumns=0;
				for(ColumnData column:result){
					if(column.getDataTypeName().compareTo(ColumnDataType.Geometry.toString())==0){
						existsGeometryColumn=true;
						countGeometryColumns++;
						geometryColumnsList.add(column);
					}
				}
				mapCreationSession.setColumns(result);
				mapCreationSession.setGeometryColumns(geometryColumnsList);
				mapCreationSession.setExistsGeometryColumn(existsGeometryColumn);
				mapCreationSession.setCountGeometryColumns(countGeometryColumns);
				
				if(!existsGeometryColumn){
					Log.info("Attention, no Geometry Column present!");
					showErrorAndHide("Attention",
							"No Geometry Column Present!","", new Throwable("No Geometry Column Present!"));
					close(false);
				} else {
					if(countGeometryColumns==1){
						mapCreationSession.setGeometry(geometryColumnsList.get(0));
					}
					startMapCreation();
				}
			}

		
		});
	}
}