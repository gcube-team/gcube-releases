package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets;

import org.gcube.portlets.admin.fhn_manager_portlet.client.FhnManagerController;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.OpenPinnedResourceEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RemovePinnedEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class PinnedResource extends Composite {

	
	private static PinnedResourceUiBinder uiBinder = GWT
			.create(PinnedResourceUiBinder.class);

	interface PinnedResourceUiBinder extends UiBinder<Widget, PinnedResource> {
	}

	
	@UiHandler("close")
	public void closeClickHandler(ClickEvent e){
		FhnManagerController.eventBus.fireEvent(new RemovePinnedEvent(instance,pinned));
	}
	
	
	@UiHandler("button")
	public void clickHandler(ClickEvent e){
		FhnManagerController.eventBus.fireEvent(new OpenPinnedResourceEvent(pinned));
	}
	private Storable pinned;
	private PinnedResource instance=null;
	
	@UiField
	FlowPanel main;
	@UiField
	HTMLPanel label;
	
	public PinnedResource(Storable pinned) {
		initWidget(uiBinder.createAndBindUi(this));
		this.pinned=pinned;
		this.instance=this;
		
		String icon=null;
		
		switch(pinned.getType()){
		case REMOTE_NODE : icon="fa-hdd-o";
		break;
		case SERVICE_PROFILE : icon="fa-tag";
		break;
		case VM_PROVIDER : icon="fa-globe";
		break;
		case VM_TEMPLATES : icon="fa-cogs";
		
		}
		
		String toShowName=pinned.getName().length()>8?pinned.getName().substring(0, 8)+"...":pinned.getName();
		
//		String toShowName=pinned.getName();
		
		label.getElement().setInnerHTML("<a><i class=\"fa "+icon+" fa-2x pull-left\">" +
				"</i><span style=\"width: 80; word-wrap: break-word;\">"+toShowName+"</span></a>");
	}

}
