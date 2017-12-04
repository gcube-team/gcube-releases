/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external;

import java.util.ArrayList;

import org.gcube.portlets.user.td.columnwidget.client.dimension.CodelistSelectionDialog;
import org.gcube.portlets.user.td.columnwidget.client.dimension.CodelistSelectionListener;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.SetColumnTypeDialogManager;

import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 18, 2014
 *
 */
public class DialogCodelistResolver implements CodelistSelectionListener{


	private SetColumnTypeDialogManager setColumnDialog;
	private CodelistSelectionDialog dialog;
	private TabResource tabularResourceSelected;
	private TdTemplateController controller;
	
	private int zIndexOffset = 30;

	/**
	 * @param columnTypeMng
	 * @param controller 
	 */
	public DialogCodelistResolver(SetColumnTypeDialogManager columnTypeMng, TdTemplateController controller) {
		this.controller = controller;
		this.dialog = new CodelistSelectionDialog(new SimpleEventBus());
		dialog.addListener(this);
		this.setColumnDialog = columnTypeMng;
	}
	
	public void show(){

		dialog.show();
		dialog.focus();
		int zIndex = controller.getWindowZIndex();
		int newZindex = zIndex+zIndexOffset;
		dialog.setZIndex(newZindex);
		GWT.log("Window Zindex is: "+zIndex +" setting dialog zIndex "+newZindex);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.columnwidget.client.dimension.CodelistSelectionListener#selected(org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource)
	 */
	@Override
	public void selected(TabResource tabResource) {
		this.tabularResourceSelected = tabResource;
		setColumnDialog.setTabularResourceName(tabularResourceSelected.getName());
		retrieveColumnData(tabResource);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.columnwidget.client.dimension.CodelistSelectionListener#aborted()
	 */
	@Override
	public void aborted() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.columnwidget.client.dimension.CodelistSelectionListener#failed(java.lang.String, java.lang.String)
	 */
	@Override
	public void failed(String reason, String detail) {
		Info.display("Error", "Sorry an error occurred on retrieving tables");
		
	}
	
	protected void retrieveColumnData(TabResource tabResource) {
	
		TDGWTServiceAsync.INSTANCE.getColumnsForDimension(
				tabResource.getTrId(),
				new AsyncCallback<ArrayList<ColumnData>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Error retrieving columns: "+ caught.getLocalizedMessage());

					}

					@Override
					public void onSuccess(ArrayList<ColumnData> result) {
						GWT.log("Loaded "+result.size()+" column data for dimension");
						setColumnDialog.initComboSetReference(result);
					}
		});

	}

	public TabResource getTabularResourceSelected() {
		return tabularResourceSelected;
	}
}
