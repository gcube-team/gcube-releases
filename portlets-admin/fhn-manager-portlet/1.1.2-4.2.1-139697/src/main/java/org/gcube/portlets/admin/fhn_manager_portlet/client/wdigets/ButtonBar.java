package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets;

import org.gcube.portlets.admin.fhn_manager_portlet.client.FhnManagerController;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.CascadedEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.PinResourceEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RemoveElementEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.ShowCreationFormEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.ShowMessageEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.StartNodeEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.StopNodeEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data.AdvancedGrid;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNodeStatus;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.github.gwtbootstrap.client.ui.Brand;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class ButtonBar extends Composite {
	@UiField FocusPanel reload;
	@UiField FocusPanel pinResource;
	@UiField FocusPanel createNew;
	@UiField FocusPanel deleteSelected;
	@UiField FocusPanel startNode;
	@UiField FocusPanel stopNode;
	@UiField Brand title;

	public enum ButtonBarElement{
		PIN,REFRESH,CREATE,DELETE,START,STOP
	}


	private static ButtonBarUiBinder uiBinder = GWT
			.create(ButtonBarUiBinder.class);

	interface ButtonBarUiBinder extends UiBinder<Widget, ButtonBar> {
	}


	private AdvancedGrid theGrid;	
	private ObjectType objectType;

	public ButtonBar(AdvancedGrid theGrid,ObjectType type) {
		initWidget(uiBinder.createAndBindUi(this));
		this.theGrid=theGrid;		
		this.objectType=type;
		title.setText(type.getLabel()+"s");
	}

	@UiHandler("reload")
	void handleClick(ClickEvent e){
		theGrid.fireRefreshData();		
	}


	@UiHandler("pinResource")
	void handlePinClick(ClickEvent e){
		if(theGrid.hasSelection()){
			Storable stor=theGrid.getSelected();
			FhnManagerController.eventBus.fireEvent(new PinResourceEvent(stor));
		}else FhnManagerController.eventBus.fireEvent(new ShowMessageEvent("Pin Resource","Please select a resource from the list."));
	}

	@UiHandler("createNew")
	void handleCreateClcik(ClickEvent e){
		FhnManagerController.eventBus.fireEvent(new ShowCreationFormEvent(objectType));
	}

	@UiHandler("deleteSelected")
	void handleDeleteClcik(ClickEvent e){
		if(theGrid.hasSelection()){
			RemoveElementEvent removeEvent=new RemoveElementEvent(objectType, theGrid.getSelected().getKey());
			removeEvent.setCascade(theGrid.getRefreshEvent());
			FhnManagerController.eventBus.fireEvent(removeEvent);
		}
		else FhnManagerController.eventBus.fireEvent(new ShowMessageEvent("Delete Resource","Please select a resource from the list."));
	}


	@UiHandler("startNode")
	void handleStartClick(ClickEvent e){
		if(theGrid.hasSelection()){
			Storable stor=theGrid.getSelected();
			if(!stor.getType().equals(ObjectType.REMOTE_NODE)) FhnManagerController.eventBus.fireEvent(new ShowMessageEvent("Start Node","Operation not available for the selected resource type."));
			else{
				RemoteNode node=(RemoteNode) stor;
				if(node.getStatus().equals(RemoteNodeStatus.active)) FhnManagerController.eventBus.fireEvent(new ShowMessageEvent("Start Node","The selected node is already running."));
				else {
					StartNodeEvent event=new StartNodeEvent(theGrid.getSelected().getKey());
					event.setCascade(theGrid.getRefreshEvent());
					FhnManagerController.eventBus.fireEvent(event);
				}
			}
		}else FhnManagerController.eventBus.fireEvent(new ShowMessageEvent("Start Node","Please select a node from the list."));
	}


	@UiHandler("stopNode")
	void handleStopClick(ClickEvent e){
		if(theGrid.hasSelection()){
			Storable stor=theGrid.getSelected();
			if(!stor.getType().equals(ObjectType.REMOTE_NODE)) FhnManagerController.eventBus.fireEvent(new ShowMessageEvent("Stop Node","Operation not available for the selected resource type."));
			else{
				RemoteNode node=(RemoteNode) stor;
				if(node.getStatus().equals(RemoteNodeStatus.inactive)) FhnManagerController.eventBus.fireEvent(new ShowMessageEvent("Stop Node","The selected node is not running."));
				else {
					StopNodeEvent event=new StopNodeEvent(theGrid.getSelected().getKey());
					event.setCascade(theGrid.getRefreshEvent());
					FhnManagerController.eventBus.fireEvent(event);
				}
			}
		}else FhnManagerController.eventBus.fireEvent(new ShowMessageEvent("Stop Node","Please select a node from the list."));
	}



	public void setVisibleState(ButtonBarElement type,boolean visibleState){
		getButton(type).setVisible(visibleState);
	}

	private FocusPanel getButton(ButtonBarElement type){
		switch(type){
		case CREATE : return createNew;
		case DELETE : return deleteSelected;
		case PIN : return pinResource;
		case REFRESH : return reload;
		case START : return startNode;
		case STOP : return stopNode;
		}
		return null;
	}



}
