package org.gcube.portlets.user.templates.client.components;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ExtButton extends Button {
	private ComponentType type;

	public ExtButton(ComponentType type) {
		super();
		this.type = type;
	}

	public ExtButton(String text, AbstractImagePrototype icon,
			SelectionListener<ButtonEvent> listener, ComponentType type) {
		super(text, icon, listener);
		this.type = type;
	}

	public ExtButton(String text, AbstractImagePrototype icon, ComponentType type) {
		super(text, icon);
		this.type = type;
	}

	public ExtButton(String text, SelectionListener<ButtonEvent> listener, ComponentType type) {
		super(text, listener);
		this.type = type;

	}

	public ExtButton(String text, ComponentType type) {
		super(text);
		this.type = type;
	}

	public ComponentType getComponentType() {
		return type;
	}

	public void setComponentType(ComponentType type) {
		this.type = type;
	}
	
}
