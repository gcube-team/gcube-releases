package org.gcube.application.aquamaps.aquamapsportlet.client.filters;

import org.gcube.application.aquamaps.aquamapsportlet.client.ColumnDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.Stores;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.selections.ExtendedLiveGrid;

import com.google.gwt.core.client.GWT;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Template;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;

public abstract class JobSelection extends Window {

	
	final Template template = new Template("<div class=\"x-combo-list-item\">" +  
            "<img src=\""+GWT.getModuleBaseURL() + "/img/cog_{status}.png\"> " +  
            "{title}-{date}:{author}<div class=\"x-clear\"></div></div>");  
	
	
	ExtendedLiveGrid grid= new ExtendedLiveGrid("Available jobs",Stores.jobStore(),ColumnDefinitions.submittedColumnModel(),true);
	
	private JobSelection instance=this;
	
	public JobSelection() {
		super("Choose a job");
		this.setLayout(new FitLayout());
		
		grid.setHeight(400);
		grid.setWidth(600);
		grid.useAllButton.hide();	
		grid.addGridRowListener(new GridRowListenerAdapter(){
			
			public void onRowDblClick(GridPanel grid, int rowIndex,
					EventObject e) {
				Record selectedRecord=grid.getSelectionModel().getSelected();
				instance.setParameter(Tags.submittedJobId, selectedRecord.getAsString(SubmittedFields.searchid+""));				
			}
		});
		grid.getStore().reload();
				
		this.setClosable(true);
		this.add(grid);
	}
	
	
	public abstract void setParameter(String paramName,String paramValue);
	
}
