package org.gcube.application.datamanagementfacilityportlet.client.forms;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;

public class Common {

	public static ComponentPlugin plugin = new ComponentPlugin() {  
		public void init(Component component) {  
			component.addListener(Events.Render, new Listener<ComponentEvent>() {  
				public void handleEvent(ComponentEvent be) { 
					El elem = be.getComponent().el().findParent(".x-form-element", 3);  
					// should style in external CSS  rather than directly  
					if(elem!=null)elem.appendChild(XDOM.create("<div style='color: #615f5f;padding: 1 0 2 0px;'>" + be.getComponent().getData("text") + "</div>"));  
				}  
			});  
		}  
	};

	
	public static final int defaultResourcePickupWidth=300;
	
}
