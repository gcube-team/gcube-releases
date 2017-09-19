package org.gcube.portlets.user.td.tablewidget.client.custom;


import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.cell.core.client.ResizeCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.HasSelectHandlers;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi"
 *
 */
public class ActionButtonCell extends ResizeCell<String> implements HasSelectHandlers {
 
        private final ActionButtonCellAppearance appearance;
        private ImageResource icon;
        private String title;
        
        public ActionButtonCell() {
                this(GWT.<ActionButtonCellAppearance> create(ActionButtonCellAppearance.class));
        }
 
        public ActionButtonCell(ActionButtonCellAppearance appearance) {
                super("click");
                this.appearance = appearance;
        }
        
        public void setIcon(ImageResource icon) {
                this.icon = icon;
        }
        
        public void setTitle(String title) {
                this.title = title;
        }
        
        @Override
        public HandlerRegistration addSelectHandler(SelectHandler handler) {
                return addHandler(handler, SelectEvent.getType());
        }
 
        @Override
        public void render(Context context,
                        String value, SafeHtmlBuilder sb) {
                this.appearance.icon = icon;
                this.appearance.title = title;
                this.appearance.render(sb);
        }
        
        @Override
        public void onBrowserEvent(Context context,
                        Element parent, String value, NativeEvent event,
                        ValueUpdater<String> valueUpdater) {
                Element target = event.getEventTarget().cast();
            // ignore the parent element
            if (isDisableEvents() || !parent.getFirstChildElement().isOrHasChild(target)) {
              return;
            }       
            
            XElement p = parent.cast();
 
            String eventType = event.getType();
            if ("click".equals(eventType)) {
              onClick(context, p, value, event, valueUpdater);
            }
        }
 
        private void onClick(Context context, XElement p, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
          if (!isDisableEvents() && fireCancellableEvent(context, new BeforeSelectEvent(context))) {
            fireEvent(context, new SelectEvent(context));
          }
        }
 
        
}