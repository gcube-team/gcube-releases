package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author francesco
 * 
 */
public class ToggleButtonPanel extends Composite implements HasWidgets, HasClickHandlers {

	private List<ClickHandler> handlers = new ArrayList<ClickHandler>();
	private final Panel panel;
	
	public ToggleButtonPanel() {
		this(new VerticalPanel());
	}

	public ToggleButtonPanel(Panel panel) {
		this.panel = panel;
		initWidget(panel);
	}

	@Override
	public void add(Widget w) {
		if (w instanceof ToggleButton) {
			ToggleButton button = (ToggleButton) w;
			button.addClickHandler(handler);
		}
		panel.add(w);
	}

	@Override
	public void clear() {
		panel.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return panel.iterator();
	}

	@Override
	public boolean remove(Widget w) {
		return panel.remove(w);
	}

	@Override
	public void setWidth(String width) {
		panel.setWidth(width);
	};

	@Override
	public void setHeight(String height) {
		panel.setHeight(height);
	}


	private ClickHandler handler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Iterator<Widget> itr = panel.iterator();
			while (itr.hasNext()) {
				Widget w = itr.next();
				if (w instanceof ToggleButton) {
					ToggleButton button = (ToggleButton) w;
					button.setDown(false);
					if (event.getSource().equals(button)) {
						button.setDown(true);
					}
				}
			}

			for (ClickHandler h : handlers) {
				h.onClick(event);
			}
		}
	};



	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		handlers.add(handler);
		return new HandlerRegistration() {

			@Override
			public void removeHandler() {
				handlers.remove(handler);
			}
		};
	}
	
	public ToggleButton getDown(){
		
		Iterator<Widget> it = iterator();
		while (it!=null && it.hasNext()) {
			Widget w = (Widget) it.next();
			if (w instanceof ToggleButton) {
				ToggleButton t = (ToggleButton) w;
				if(t.isDown())
					return t;
			}
		}
		return null;
	}

}
