package org.gcube.application.aquamaps.aquamapsspeciesview.client;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.AquaMapsSpeciesViewConstants;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.Response;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveOperationProgress;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveRequest;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.species.SpeciesResultsPanel;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PortletCommon {

	public static GridCellRenderer<ModelData> booleanRenderer=new GridCellRenderer<ModelData>() {
		@Override
		public Object render(ModelData model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<ModelData> store, Grid<ModelData> grid) {
			try{
				return Integer.parseInt((String)model.get(property))==1;
			}catch (NumberFormatException e){
				return model.get(property);
			}
		}
	};
	
	public static GridCellRenderer<ModelData> timeRenderer=new GridCellRenderer<ModelData>() {
		public Object render(ModelData model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<ModelData> store, Grid<ModelData> grid) {
			try{
				return AquaMapsSpeciesViewConstants.timeFormat.format(new Timestamp(Long.parseLong((String)model.get(property))));
			}catch(Exception e){
				Log.warn("Impossible to parse timestamp "+e.getMessage());
				return "N/A";
			}
		}
	};
	
    public final static Map<EventType, String> eventTypeNames = new HashMap<EventType, String>();
    static {    	
        eventTypeNames.put(Events.Activate, "Events.Activate");
        eventTypeNames.put(Events.Add, "Events.Add");
        eventTypeNames.put(Events.Adopt, "Events.Adopt");
        eventTypeNames.put(Events.AfterEdit, "Events.AfterEdit");
        eventTypeNames.put(Events.AfterLayout, "Events.AfterLayout");
        eventTypeNames.put(Events.ArrowClick, "Events.ArrowClick");
        eventTypeNames.put(Events.Attach, "Events.Attach");
        eventTypeNames.put(Events.AutoHide, "Events.AutoHide");
        eventTypeNames.put(Events.BeforeAdd, "Events.BeforeAdd");
        eventTypeNames.put(Events.BeforeAdopt, "Events.BeforeAdopt");
        eventTypeNames.put(Events.BeforeBind, "Events.BeforeBind");
        eventTypeNames.put(Events.BeforeCancelEdit, "Events.BeforeCancelEdit");
        eventTypeNames.put(Events.BeforeChange, "Events.BeforeChange");
        eventTypeNames.put(Events.BeforeCheckChange, "Events.BeforeCheckChange");
        eventTypeNames.put(Events.BeforeClose, "Events.BeforeClose");
        eventTypeNames.put(Events.BeforeCollapse, "Events.BeforeCollapse");
        eventTypeNames.put(Events.BeforeComplete, "Events.BeforeComplete");
        eventTypeNames.put(Events.BeforeEdit, "Events.BeforeEdit");
        eventTypeNames.put(Events.BeforeExpand, "Events.BeforeExpand");
        eventTypeNames.put(Events.BeforeHide, "Events.BeforeHide");
        eventTypeNames.put(Events.BeforeLayout, "Events.BeforeLayout");
        eventTypeNames.put(Events.BeforeOpen, "Events.BeforeOpen");
        eventTypeNames.put(Events.BeforeOrphan, "Events.BeforeOrphan");
        eventTypeNames.put(Events.BeforeQuery, "Events.BeforeQuery");
        eventTypeNames.put(Events.BeforeRemove, "Events.BeforeRemove");
        eventTypeNames.put(Events.BeforeRender, "Events.BeforeRender");
        eventTypeNames.put(Events.BeforeSelect, "Events.BeforeSelect");
        eventTypeNames.put(Events.BeforeShow, "Events.BeforeShow");
        eventTypeNames.put(Events.BeforeStartEdit, "Events.BeforeStartEdit");
        eventTypeNames.put(Events.BeforeStateRestore,"Events.BeforeStateRestore");
        eventTypeNames.put(Events.BeforeStateSave, "Events.BeforeStateSave");
        eventTypeNames.put(Events.BeforeSubmit, "Events.BeforeSubmit");
        eventTypeNames.put(Events.Bind, "Events.Bind");
        eventTypeNames.put(Events.Blur, "Events.Blur");
        eventTypeNames.put(Events.BodyScroll, "Events.BodyScroll");
        eventTypeNames.put(Events.BrowserEvent, "Events.BrowserEvent");
        eventTypeNames.put(Events.CancelEdit, "Events.CancelEdit");
        eventTypeNames.put(Events.CellClick, "Events.CellClick");
        eventTypeNames.put(Events.CellDoubleClick, "Events.CellDoubleClick");
        eventTypeNames.put(Events.CellMouseDown, "Events.CellMouseDown");
        eventTypeNames.put(Events.CellMouseUp, "Events.CellMouseUp");
        eventTypeNames.put(Events.Change, "Events.Change");
        eventTypeNames.put(Events.CheckChange, "Events.CheckChange");
        eventTypeNames.put(Events.CheckChanged, "Events.CheckChanged");
        eventTypeNames.put(Events.Clear, "Events.Clear");
        eventTypeNames.put(Events.Close, "Events.Close");
        eventTypeNames.put(Events.Collapse, "Events.Collapse");
        eventTypeNames.put(Events.ColumnClick, "Events.ColumnClick");
        eventTypeNames.put(Events.ColumnResize, "Events.ColumnResize");
        eventTypeNames.put(Events.Complete, "Events.Complete");
        eventTypeNames.put(Events.ContextMenu, "Events.ContextMenu");
        eventTypeNames.put(Events.Deactivate, "Events.Deactivate");
        eventTypeNames.put(Events.Detach, "Events.Detach");
        eventTypeNames.put(Events.Disable, "Events.Disable");
        eventTypeNames.put(Events.DoubleClick, "Events.DoubleClick");
        eventTypeNames.put(Events.DragCancel, "Events.DragCancel");
        eventTypeNames.put(Events.DragEnd, "Events.DragEnd");
        eventTypeNames.put(Events.DragEnter, "Events.DragEnter");
        eventTypeNames.put(Events.DragFail, "Events.DragFail");
        eventTypeNames.put(Events.DragLeave, "Events.DragLeave");
        eventTypeNames.put(Events.DragMove, "Events.DragMove");
        eventTypeNames.put(Events.DragStart, "Events.DragStart");
        eventTypeNames.put(Events.Drop, "Events.Drop");
        eventTypeNames.put(Events.EffectCancel, "Events.EffectCancel");
        eventTypeNames.put(Events.EffectComplete, "Events.EffectComplete");
        eventTypeNames.put(Events.EffectStart, "Events.EffectStart");
        eventTypeNames.put(Events.Enable, "Events.Enable");
        eventTypeNames.put(Events.Exception, "Events.Exception");
        eventTypeNames.put(Events.Expand, "Events.Expand");
        eventTypeNames.put(Events.Focus, "Events.Focus");
        eventTypeNames.put(Events.HeaderChange, "Events.HeaderChange");
        eventTypeNames.put(Events.HeaderClick, "Events.HeaderClick");
        eventTypeNames.put(Events.HeaderContextMenu, "Events.HeaderContextMenu");
        eventTypeNames.put(Events.HeaderDoubleClick, "Events.HeaderDoubleClick");
        eventTypeNames.put(Events.HeaderMouseDown, "Events.HeaderMouseDown");
        eventTypeNames.put(Events.HiddenChange, "Events.HiddenChange");
        eventTypeNames.put(Events.Hide, "Events.Hide");
        eventTypeNames.put(Events.Invalid, "Events.Invalid");
        eventTypeNames.put(Events.KeyDown, "Events.KeyDown");
        eventTypeNames.put(Events.KeyPress, "Events.KeyPress");
        eventTypeNames.put(Events.KeyUp, "Events.KeyUp");
        eventTypeNames.put(Events.LiveGridViewUpdate,"Events.LiveGridViewUpdate");
        eventTypeNames.put(Events.Maximize, "Events.Maximize");
        eventTypeNames.put(Events.MenuHide, "Events.MenuHide");
        eventTypeNames.put(Events.MenuShow, "Events.MenuShow");
        eventTypeNames.put(Events.Minimize, "Events.Minimize");
        eventTypeNames.put(Events.Move, "Events.Move");
        eventTypeNames.put(Events.OnBlur, "Events.OnBlur");
        eventTypeNames.put(Events.OnChange, "Events.OnChange");
        eventTypeNames.put(Events.OnClick, "Events.OnClick");
        eventTypeNames.put(Events.OnContextMenu, "Events.OnContextMenu");
        eventTypeNames.put(Events.OnDoubleClick, "Events.OnDoubleClick");
        eventTypeNames.put(Events.OnError, "Events.OnError");
        eventTypeNames.put(Events.OnFocus, "Events.OnFocus");
        eventTypeNames.put(Events.OnKeyDown, "Events.OnKeyDown");
        eventTypeNames.put(Events.OnKeyPress, "Events.OnKeyPress");
        eventTypeNames.put(Events.OnKeyUp, "Events.OnKeyUp");
        eventTypeNames.put(Events.OnLoad, "Events.OnLoad");
        eventTypeNames.put(Events.OnLoseCapture, "Events.OnLoseCapture");
        eventTypeNames.put(Events.OnMouseDown, "Events.OnMouseDown");
        eventTypeNames.put(Events.OnMouseMove, "Events.OnMouseMove");
        eventTypeNames.put(Events.OnMouseOut, "Events.OnMouseOut");
        eventTypeNames.put(Events.OnMouseOver, "Events.OnMouseOver");
        eventTypeNames.put(Events.OnMouseUp, "Events.OnMouseUp");
        eventTypeNames.put(Events.OnMouseWheel, "Events.OnMouseWheel");
        eventTypeNames.put(Events.OnScroll, "Events.OnScroll");
        eventTypeNames.put(Events.Open, "Events.Open");
        eventTypeNames.put(Events.Orphan, "Events.Orphan");
        eventTypeNames.put(Events.Ready, "Events.Ready");
        eventTypeNames.put(Events.Refresh, "Events.Refresh");
        eventTypeNames.put(Events.Register, "Events.Register");
        eventTypeNames.put(Events.Remove, "Events.Remove");
        eventTypeNames.put(Events.Render, "Events.Render");
        eventTypeNames.put(Events.Resize, "Events.Resize");
        eventTypeNames.put(Events.ResizeEnd, "Events.ResizeEnd");
        eventTypeNames.put(Events.ResizeStart, "Events.ResizeStart");
        eventTypeNames.put(Events.Restore, "Events.Restore");
        eventTypeNames.put(Events.RowClick, "Events.RowClick");
        eventTypeNames.put(Events.RowDoubleClick, "Events.RowDoubleClick");
        eventTypeNames.put(Events.RowMouseDown, "Events.RowMouseDown");
        eventTypeNames.put(Events.RowMouseUp, "Events.RowMouseUp");
        eventTypeNames.put(Events.RowUpdated, "Events.RowUpdated");
        eventTypeNames.put(Events.Scroll, "Events.Scroll");
        eventTypeNames.put(Events.Select, "Events.Select");
        eventTypeNames.put(Events.SelectionChange, "Events.SelectionChange");
        eventTypeNames.put(Events.Show, "Events.Show");
        eventTypeNames.put(Events.SortChange, "Events.SortChange");
        eventTypeNames.put(Events.SpecialKey, "Events.SpecialKey");
        eventTypeNames.put(Events.StartEdit, "Events.StartEdit");
        eventTypeNames.put(Events.StateChange, "Events.StateChange");
        eventTypeNames.put(Events.StateRestore, "Events.StateRestore");
        eventTypeNames.put(Events.StateSave, "Events.StateSave");
        eventTypeNames.put(Events.Submit, "Events.Submit");
        eventTypeNames.put(Events.Toggle, "Events.Toggle");
        eventTypeNames.put(Events.TriggerClick, "Events.TriggerClick");
        eventTypeNames.put(Events.TwinTriggerClick, "Events.TwinTriggerClick");
        eventTypeNames.put(Events.UnBind, "Events.UnBind");
        eventTypeNames.put(Events.Unregister, "Events.Unregister");
        eventTypeNames.put(Events.Update, "Events.Update");
        eventTypeNames.put(Events.Valid, "Events.Valid");
        eventTypeNames.put(Events.ValidateDrop, "Events.ValidateDrop");
        eventTypeNames.put(Events.ValidateEdit, "Events.ValidateEdit");
        eventTypeNames.put(Events.ViewReady, "Events.ViewReady");
    }
    
    public static AsyncCallback<Response> refreshSpeciesCallback=new AsyncCallback<Response>() {
		
		@Override
		public void onSuccess(Response result) {
			AquaMapsSpeciesView.get().mainPanel.unmask();
			if(result.getStatus())
				SpeciesResultsPanel.getInstance().reload();
			else MessageBox.alert("Error", "Something went wrong, please retry", null);
		}
		
		@Override
		public void onFailure(Throwable caught) {
			AquaMapsSpeciesView.get().mainPanel.unmask();
			MessageBox.info("Search Species", "Unable to update settings, session might be timed out. </br> Please try refreshing page.",null);
			Log.error("Unexpected exception ",caught);
		}
	}; 
	
	
	public static final String evaluateBoolean(Object toParse){
		try{
			return ((Integer.parseInt((String)toParse))==1)+"";
		}catch (Exception e){
			return "N/A";
		}
	}
	
	public static void sendSaveRequest(SaveRequest toSend){
		AquaMapsSpeciesView.localService.saveOperationRequest(toSend, new AsyncCallback<SaveOperationProgress>() {
			
			@Override
			public void onSuccess(SaveOperationProgress result) {
				final MessageBox box = MessageBox.progress("Please wait",  
			            "Save data to workspace", "Retrieving data...");
				
				final Timer t = new Timer() {  
					
			          @Override  
			          public void run() {
			        	  AquaMapsSpeciesView.localService.getSaveProgress(new AsyncCallback<SaveOperationProgress>() {
			        		  @Override
			        		public void onFailure(Throwable caught) {
			        			  if(box.isVisible()) box.close();
		        				  Info.display("Save Operation", "Unable to complete, "+caught.getMessage());
		        				  Log.error("Unexpected Error while retrieving progress",caught);
		        				  cancel();
			        		}
			        		  @Override
			        		public void onSuccess(SaveOperationProgress result) {
			        			  switch(result.getState()){
			        			  case COMPLETED : {
			        				  if(box.isVisible()) box.close();
			        				  Info.display("Save Operation", "Complete");
			        				  cancel();
				        			  break;	
			        			  }
			        			  case ERROR : {
			        				  if(box.isVisible()) box.close();
			        				  Info.display("Save Operation", "Unable to complete, "+result.getFailureReason());
			        				  Log.error("Unexpected Error in operation",result.getFailureDetails());
			        				  cancel();
			        				  break;
			        			  }
			        			  case RETRIEVING_FILES :{
			        				  //still retrieving, do nothing
			        				  break;
			        			  }
			        			  case SAVING_FILES : {
			        				  box.updateProgress((double)result.getSavedCount()/result.getToSaveCount(), 
			        						  "Saving "+result.getSavedCount()+" of "+result.getToSaveCount());			        				  
			        				  break;
			        			  }
			        			  }
			        		}
						});
			            
			          }  
			        };  
			    t.scheduleRepeating(500);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Info.display("Save request", "Unable to save request, "+caught.getMessage());
				Log.error("Unexpected Error while sending save request",caught);
			}
		});
	}
}
