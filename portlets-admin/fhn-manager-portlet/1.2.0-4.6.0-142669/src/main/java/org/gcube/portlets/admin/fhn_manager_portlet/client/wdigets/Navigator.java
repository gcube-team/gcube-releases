package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Navigator extends Composite{

	private static NavigatorUiBinder uiBinder = GWT
			.create(NavigatorUiBinder.class);

	interface NavigatorUiBinder extends UiBinder<Widget, Navigator> {
	}

	private static Map<ObjectType,NavigatorButton> buttons=new HashMap<ObjectType,NavigatorButton>();
	
	@UiField(provided=true)
	final NavigatorButton nodesButton=new NavigatorButton(ObjectType.REMOTE_NODE);
	
	@UiField(provided=true)
	final NavigatorButton profilesButton=new NavigatorButton(ObjectType.SERVICE_PROFILE);
	@UiField(provided=true)
	final NavigatorButton templatesButton=new NavigatorButton(ObjectType.VM_TEMPLATES);
	@UiField(provided=true)
	final NavigatorButton providersButton=new NavigatorButton(ObjectType.VM_PROVIDER);
	
	
	public Navigator() {
		initWidget(uiBinder.createAndBindUi(this));
		buttons.put(ObjectType.REMOTE_NODE, nodesButton);
		buttons.put(ObjectType.SERVICE_PROFILE, profilesButton);
		buttons.put(ObjectType.VM_PROVIDER, providersButton);
		buttons.put(ObjectType.VM_TEMPLATES, templatesButton);
	}

	public static void setActive(ObjectType type){
		for(NavigatorButton button:buttons.values())button.setActive(false);
		buttons.get(type).setActive(true);		
	}
	
}
