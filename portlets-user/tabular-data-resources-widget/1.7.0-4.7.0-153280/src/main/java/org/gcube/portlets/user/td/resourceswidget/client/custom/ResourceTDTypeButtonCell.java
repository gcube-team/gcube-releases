package org.gcube.portlets.user.td.resourceswidget.client.custom;


import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.cell.core.client.ResizeCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.HasSelectHandlers;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ResourceTDTypeButtonCell extends ResizeCell<ResourceTDType> implements HasSelectHandlers {
 
        private final ResourceTDTypeButtonCellAppearance appearance;
       
        
        public ResourceTDTypeButtonCell() {
                this(GWT.<ResourceTDTypeButtonCellAppearance> create(ResourceTDTypeButtonCellAppearance.class));
        }
 
        public ResourceTDTypeButtonCell(ResourceTDTypeButtonCellAppearance appearance) {
                super("click");
                this.appearance = appearance;
        }
        
        
        @Override
        public HandlerRegistration addSelectHandler(SelectHandler handler) {
                return addHandler(handler, SelectEvent.getType());
        }
 
       
        
        @Override
        public void onBrowserEvent(Context context,
                        Element parent, ResourceTDType value, NativeEvent event,
                        ValueUpdater<ResourceTDType> valueUpdater) {
        	Log.debug("Received Browse Event"); 
        	Element target = event.getEventTarget().cast();
            // ignore the parent element
                
            if (isDisableEvents() || !parent.getFirstChildElement().isOrHasChild(target)) {
            	Log.debug("Events Disabled");
            	return;
            }       
            
            XElement p = parent.cast();
 
            String eventType = event.getType();
            if ("click".equals(eventType)) {
              onClick(context, p, value, event, valueUpdater);
            }
        }
 
        private void onClick(Context context, XElement p, ResourceTDType value, NativeEvent event, ValueUpdater<ResourceTDType> valueUpdater) {
          if (!isDisableEvents() && fireCancellableEvent(context, new BeforeSelectEvent(context))) {
            fireEvent(context, new SelectEvent(context));
          }
        }

		@Override
		public void render(Context context,
				ResourceTDType value, SafeHtmlBuilder sb) {
            this.appearance.render(value,sb);
			
		}
 
        
}