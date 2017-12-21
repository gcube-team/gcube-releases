package org.gcube.application.aquamaps.aquamapsportlet.client.details;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.ColumnDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.Stores;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientObject;
import org.gcube.application.aquamaps.aquamapsportlet.client.selections.ExtendedLiveGrid;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.WindowListenerAdapter;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;

public class AdditionalDetailsPopup extends Window {

	private ExtendedLiveGrid species=new ExtendedLiveGrid("Associated Species",Stores.selectedSpeciesStore(),ColumnDefinitions.availableSpeciesColumnModel(),false);
	private AdditionalDetailsPopup instance = this;
	public AdditionalDetailsPopup(final ClientObject obj) {
		this.setFrame(true);
		this.setLayout(new AnchorLayout());
		this.setSize(350, 450);
		this.setTitle(obj.getName()+"Additional Details");
		UrlParam[] defaultParams=species.getStore().getBaseParams();
		UrlParam[] pars=new UrlParam[defaultParams.length+1];
		for(int i=0;i<defaultParams.length;i++){
			pars[i]=defaultParams[i];
		}
		pars[defaultParams.length]=new UrlParam(Tags.AQUAMAPS_ID,obj.getId());
		species.getStore().setBaseParams(pars);
		this.add(species,new AnchorLayoutData("100% 100%"));
		this.addListener(new WindowListenerAdapter(){
			@Override
			public void onShow(Component component) {
				AquaMapsPortlet.get().showLoading("Loading selected species Ids..",instance.getId());
				AquaMapsPortlet.remoteService.getAquaMapsObject(obj.getId(),true, new AsyncCallback<ClientObject>(){

					public void onFailure(Throwable caught) {
						AquaMapsPortlet.get().hideLoading(instance.getId());
						AquaMapsPortlet.get().showMessage("Unable to show species details, please retry later");
						Log.error("[getAquaMapsObjectCallback-fetchSpecies]", caught);
					}

					public void onSuccess(ClientObject result) {
						Log.debug("[getAquaMapsObjectCallback-fetchSpecies] - success");
						species.getStore().reload();
						AquaMapsPortlet.get().hideLoading(instance.getId());
					}				
				});
				species.getStore().reload();
			}
		});
		species.useAllButton.hide();
	}
	
	
}
