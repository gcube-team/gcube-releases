package org.gcube.portlets.admin.vredefinition.client.presenter;

import java.util.List;

import org.gcube.portlets.admin.vredefinition.client.VREDefinitionServiceAsync;
import org.gcube.portlets.admin.vredefinition.client.event.TreeNodeFunctionalityEvent;
import org.gcube.portlets.admin.vredefinition.client.model.VREDefinitionModel;
import org.gcube.portlets.admin.vredefinition.client.model.VREFunctionalityModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class VREFunctionalitiesPresenter implements Presenter{

	public interface Display {
		TreePanel<ModelData> getTreeFunctionalities();
		void setData(List<ModelData> categories);
		Widget asWidget();
	}

	private final VREDefinitionServiceAsync rpcService;
	private final Display display;
	private final VREDefinitionModel vreModel;
	private LayoutContainer container;
	private final HandlerManager eventBus;

	public VREFunctionalitiesPresenter(VREDefinitionServiceAsync rpcService,VREDefinitionModel vreModel, 
			HandlerManager eventBus, Display display) {	
		this.display = display;
		this.rpcService = rpcService;
		this.vreModel = vreModel;
		this.eventBus = eventBus;

	}

	public void bind() {
		display.getTreeFunctionalities().addListener(Events.OnClick, new Listener<TreePanelEvent<ModelData>>() {

			public void handleEvent(TreePanelEvent<ModelData> be) {
				if (be.getType() == Events.OnClick) {
					VREFunctionalityModel selectedModel = (VREFunctionalityModel)be.getItem();	
					eventBus.fireEvent(new TreeNodeFunctionalityEvent(selectedModel));
				}
			}
		});
	}



	public boolean doSave() {

		TreePanel<ModelData> items = display.getTreeFunctionalities();	
		for(ModelData item : items.getStore().getAllItems()) {
			item.set("isSelected", false);
		}

		for(ModelData item : items.getCheckedSelection()) {	
			item.set("isSelected",true);
		}

		vreModel.setVREFunctionalities(items.getStore().getRootItems());
		return validateVREFunctionalitiesSelected();
	}

	private boolean validateVREFunctionalitiesSelected() {

		List<ModelData> categories = vreModel.getVREFunctionalities();
		boolean check = false;
		for(ModelData category : categories) {
			for(ModelData func : ((VREFunctionalityModel)category).getChildren())
				if (((VREFunctionalityModel)func).isSelected()){
					check = true;
					break;
				}
		}

		if(!check) 
			MessageBox.alert("Alert", "Please, select at least one Functionality", null);

		return check;
	}


	public void go(LayoutContainer container) {
		this.container = container;

		container.removeAll();
		container.add(display.asWidget());
		container.layout();

		if(vreModel.getVREFunctionalities() == null) {
			fetchFunctionalities();
			container.mask("Loading data...","loading-indicator");		
		} else { 
			display.setData(vreModel.getVREFunctionalities());
			bind();
		}

	}

	private void fetchFunctionalities() {
		rpcService.getFunctionality(false, new AsyncCallback<VREFunctionalityModel>() {


			public void onSuccess(VREFunctionalityModel result) {
				container.unmask();
				if (result == null) {
					MessageBox.info("Service Error", "There is no instance of the underlying service running, please try again in a short while", null);
				}	
				else{
					display.setData(result.getChildren());
					bind();
				}
			}


			public void onFailure(Throwable caught) {

			}
		});
	}


	public Widget display() {
		// TODO Auto-generated method stub
		return display.asWidget();
	}

}
