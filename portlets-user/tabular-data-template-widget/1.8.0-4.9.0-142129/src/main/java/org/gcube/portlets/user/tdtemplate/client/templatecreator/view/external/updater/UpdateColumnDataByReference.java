/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external.updater;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.SetColumnTypeDialogManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;



/**
 * The Class UpdateColumnDataByReference.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 16, 2015
 */
public class UpdateColumnDataByReference {
	
	
	private SetColumnTypeDialogManager columnTypeMng;
	private ColumnData tmpColumnData;

	private List<ColumnData> listColumns;
	private ColumnData columnDataRef;

	
	/**
	 * Instantiates a new update column data by reference.
	 *
	 * @param clTypeMng the cl type mng
	 * @param temporaryCD the temporary cd
	 */
	public UpdateColumnDataByReference(SetColumnTypeDialogManager clTypeMng, ColumnData temporaryCD) {
		this.columnTypeMng = clTypeMng;
		this.tmpColumnData = temporaryCD;
		loadColumns();
	}
	
	/**
	 * Load columns.
	 */
	public void loadColumns(){
		GWT.log("Loading columns references");
		
		TDGWTServiceAsync.INSTANCE.getColumnsForDimension(tmpColumnData.getTrId(),new AsyncCallback<ArrayList<ColumnData>>() {


			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error retrieving columns: "+ caught.getLocalizedMessage());
				listColumns = null;
			}

			@Override
			public void onSuccess(ArrayList<ColumnData> result) {
				GWT.log("Loaded "+result.size()+" column/s for dimension");
				listColumns = result;
				
				GWT.log("Searching column data selected: "+tmpColumnData.getColumnId() +", label: "+tmpColumnData.getLabel());
				for (ColumnData columnData : result) {
					GWT.log("Comparing with "+columnData.getColumnId() +", label: "+columnData.getLabel());
					if(columnData.getColumnId().compareTo(tmpColumnData.getColumnId())==0){
						columnDataRef = columnData;
						GWT.log("columnDataRef is: "+columnData);
						break;
					}
				}
				
				updateDialogManger();
			}

			
		});
	}
	
	/**
	 * Update dialog manger.
	 */
	private void updateDialogManger() {
		
		if(listColumns!=null && listColumns.size()>0){
			GWT.log("List columns have has size "+listColumns.size());
			columnTypeMng.updateComboSetReference(tmpColumnData, columnTypeMng.getReferenceTabularResourceName(), listColumns);
			if(columnDataRef!=null && columnDataRef.getLabel()!=null){
				GWT.log("Column data ref is "+columnDataRef);
				columnTypeMng.setSelectedReferenceAs(columnDataRef);
			}
		}
	}

}
