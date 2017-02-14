/**
 * 
 */
package gr.cite.gaap.datatransferobjects;

/**
 * @author vfloros
 *
 */
public class TaxonomyMessengerForJSTree {
	private boolean icon = false;
	private boolean children = false;
	private String id = null;
	private String text = null;
	private String type = "parent";
	private State state = null;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (children ? 1231 : 1237);
		result = prime * result + (icon ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaxonomyMessengerForJSTree other = (TaxonomyMessengerForJSTree) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public State getState() {
		return state;
	}

	public void setState(boolean disabled, boolean opened, boolean selected) {
		this.state = new State(disabled, opened, selected);
	}
}

class State {
	private boolean disabled = false;
	private boolean opened = false;
	private boolean selected = false;

	public State(boolean disabled, boolean opened, boolean selected) {
		super();
		this.disabled = disabled;
		this.opened = opened;
		this.selected = selected;
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
