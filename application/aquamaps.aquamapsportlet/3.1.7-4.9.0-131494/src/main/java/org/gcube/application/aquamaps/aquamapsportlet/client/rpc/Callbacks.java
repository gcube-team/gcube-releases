package org.gcube.application.aquamaps.aquamapsportlet.client.rpc;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.widgets.MessageBox;

public class Callbacks {

	
	public static AsyncCallback<Msg> setSpeciesFilterCallback= new AsyncCallback<Msg>(){

		public void onFailure(Throwable caught) {
			AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().species.toAddSpecies.getId());
			AquaMapsPortlet.get().showMessage("Unable to set requested filters parameters");
			Log.error("[setSpeciesFilterCallback]", caught);
		}

		public void onSuccess(Msg result) {
			AquaMapsPortlet.get().species.toAddSpecies.getStore().reload();
			AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().species.toAddSpecies.getId());
			if(!result.getStatus()) AquaMapsPortlet.get().showMessage(result.getMsg());						
			Log.debug("[setSpeciesFilterCallback] - "+result.getMsg());
			
		}
		
	};

	
	public static AsyncCallback<Msg> speciesSelectionChangeCallback=new AsyncCallback<Msg>(){

		public void onFailure(Throwable caught) {			
			AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().species.toAddSpecies.getId());
			AquaMapsPortlet.get().showMessage("Unable to do requested selection change");
			Log.error("[speciesSelectionChangeCallback]", caught);
		}

		public void onSuccess(Msg result) {			
			MessageBox.hide();
			AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().species.toAddSpecies.getId());
			AquaMapsPortlet.get().species.selectedSpecies.getStore().reload();
			if(!result.getStatus()) AquaMapsPortlet.get().showMessage(result.getMsg());						
			Log.debug("[speciesSelectionChangeCallback] - "+result.getMsg());		
		}
		
	};
	


	public static AsyncCallback<Msg> areaSelectionChangeCallback=new AsyncCallback<Msg>(){

		public void onFailure(Throwable caught) {
			AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().area.toAddAreasPanel.getId());
			AquaMapsPortlet.get().showMessage("Unable to do requested selection change");
			Log.error("[areaSelectionChangeCallback]", caught);
		}

		public void onSuccess(Msg result) {			
			AquaMapsPortlet.get().hideLoading(AquaMapsPortlet.get().area.toAddAreasPanel.getId());
			AquaMapsPortlet.get().area.selectedAreas.getStore().reload();
			if(!result.getStatus()) AquaMapsPortlet.get().showMessage(result.getMsg());						
			Log.debug("[areaSelectionChangeCallback] - "+result.getMsg());		
		}
		
	};
	
}
