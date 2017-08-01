package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LayerMessengerForJSTREE {
	private static Logger logger = LoggerFactory.getLogger(LayerMessengerForJSTREE.class);
	private boolean icon = false;
	private boolean children = false;
	private String id = null;
	private StateClass state = new StateClass();
	private String text = null;
	
	

	public LayerMessengerForJSTREE() {
		super();
		logger.trace("Initialized default contructor for LayerMessengerForJSTREE");
	}

	public boolean isIcon() {
		return icon;
	}

	public void setIcon(boolean icon) {
		this.icon = icon;
	}

	public boolean isChildren() {
		return children;
	}

	public void setChildren(boolean children) {
		this.children = children;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public StateClass getState() {
		return state;
	}

	public void setState(boolean disabled, boolean opened, boolean selected) {
		this.state = new StateClass(disabled, opened, selected);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}

class StateClass {
	private boolean disabled = false;
	private boolean opened = false;
	private boolean selected = false;

	public StateClass(boolean disabled, boolean opened, boolean selected) {
		super();
		this.disabled = disabled;
		this.opened = opened;
		this.selected = selected;
	}

	public StateClass() {
		super();
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}