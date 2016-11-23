package org.gcube.application.aquamaps.aquamapsportlet.client.selections;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.ResourceFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientResourceType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ComponentListenerAdapter;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;

public class ResourcePickerPopUp extends Window {

	ResourceGrid grid; 
	
	private ResourcePickerPopUp instance=this;
	
	public ResourcePickerPopUp(final ClientResourceType type) {
		this.setTitle("Select a source");
		this.setLayout(new FitLayout());
		grid=new ResourceGrid(AquaMapsPortletCostants.resourceNames.get(type),type);
		grid.setSize(530,420);
		grid.addGridRowListener(new GridRowListenerAdapter(){
			
			public void onRowDblClick(GridPanel grid, int rowIndex,
					EventObject e) {
				Record selectedRecord=grid.getStore().getAt(rowIndex);
				AquaMapsPortlet.get().showLoading("Setting selected source..", instance.getId());
				AquaMapsPortlet.localService.setSource(selectedRecord.getAsInteger(ResourceFields.searchid+""), type, new AsyncCallback<Msg>() {

					public void onFailure(Throwable arg0) {
						AquaMapsPortlet.get().hideLoading(instance.getId());
						instance.close();
						AquaMapsPortlet.get().showMessage("Unable to set selected source to current Job");
					}

					public void onSuccess(Msg arg0) {
						AquaMapsPortlet.get().hideLoading(instance.getId());
						instance.close();
						AquaMapsPortlet.get().advGeneration.objectPanel.reload();
					}
				});

				
			}
		});
		this.addListener(new ComponentListenerAdapter(){
			@Override
			public void onShow(Component component) {
				grid.getStore().reload();
			}
		});	
		
		
		this.add(grid);
		this.setSize(300,400);
	}
	
}
