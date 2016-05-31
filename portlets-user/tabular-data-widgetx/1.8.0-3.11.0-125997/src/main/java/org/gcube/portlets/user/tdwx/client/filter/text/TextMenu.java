package org.gcube.portlets.user.tdwx.client.filter.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.portlets.user.tdwx.client.resources.ResourceBundle;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterConfigBean;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent.BeforeHideHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 * @param <M>
 */
public class TextMenu<M> extends Menu {

	/**
	 * A menu of string items for use with a {@link TextFilter}.
	 * 
	 * @param <M>
	 *            the model type
	 */

	public enum TextItem {
		CONTAINS("ct"), BEGINS("bg"), ENDS("en"), SOUNDEX("sd");

		private final String key;

		private TextItem(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}

	protected TextField ct, bg, en, sd;
	//private TextMenuMessages msgs;
	
	
	private TextFilter<M> filter;
	private List<TextItem> textItems = new ArrayList<TextItem>();
	private DelayedTask updateTask = new DelayedTask() {

		@Override
		public void onExecute() {
			fireUpdate();
		}
	};

	

	/**
	 * Creates text menu for use with the specified text filter.
	 * 
	 * @param filter
	 *            the filter that uses this text menu
	 */
	public TextMenu(TextFilter<M> filter) {
		this.filter = filter;
		
		
		addBeforeHideHandler(new BeforeHideHandler() {

			@Override
			public void onBeforeHide(BeforeHideEvent event) {
				// blur the field because of empty text
				if (ct != null) {
					ct.getElement().selectNode("input").blur();
				}

				if (bg != null) {
					bg.getElement().selectNode("input").blur();
				}

				if (en != null) {
					en.getElement().selectNode("input").blur();
				}

				if (sd != null) {
					sd.getElement().selectNode("input").blur();
				}
			}
		});
	}

	/**
	 * Returns the menu's text items.
	 * 
	 * @return the text items
	 */
	public List<TextItem> getTextItems() {
		return Collections.unmodifiableList(textItems);
	}

	/**
	 * Returns the menu's value.
	 * 
	 * @return the value
	 */
	public List<FilterConfig> getValue() {
		List<FilterConfig> configs = new ArrayList<FilterConfig>();
		if (ct != null && ct.getCurrentValue() != null && ct.isCurrentValid()) {
			FilterConfig config = new FilterConfigBean();
			config.setType("string");
			config.setComparison("contains");
			config.setValue(ct.getCurrentValue().toString());
			configs.add(config);
		}

		if (bg != null && bg.getCurrentValue() != null && bg.isCurrentValid()) {
			FilterConfig config = new FilterConfigBean();
			config.setType("string");
			config.setComparison("begins");
			config.setValue(bg.getCurrentValue().toString());
			configs.add(config);
		}

		if (en != null && en.getCurrentValue() != null && en.isCurrentValid()) {
			FilterConfig config = new FilterConfigBean();
			config.setType("string");
			config.setComparison("ends");
			config.setValue(en.getCurrentValue().toString());
			configs.add(config);
		}

		if (sd != null && sd.getCurrentValue() != null && sd.isCurrentValid()) {
			FilterConfig config = new FilterConfigBean();
			config.setType("string");
			config.setComparison("soundex");
			config.setValue(sd.getCurrentValue().toString());
			configs.add(config);
		}

		return configs;
	}

	/**
	 * Sets the text to display in the menu's text fields if they do not contain
	 * a value.
	 * 
	 * @param emptyText
	 *            the text to display if the fields are empty
	 */
	public void setEmptyText(String emptyText) {
		if (ct != null) {
			ct.setEmptyText(emptyText);
		}
		if (bg != null) {
			bg.setEmptyText(emptyText);
		}
		if (en != null) {
			en.setEmptyText(emptyText);
		}
		if (sd != null) {
			sd.setEmptyText(emptyText);
		}
	}

	/**
	 * Sets the menu's text items (defaults to CONTAINS, BEGINS, ENDS, SOUNDEX).
	 * 
	 * @param textItems
	 *            the text items
	 */
	public void setTextItems(List<TextItem> textItems, TextFilterMessages msgs) {
		this.textItems = textItems;
		clear();
		ImageResource icon = null;
		String toolTip = null;
		for (TextItem item : textItems) {
			TextField field = createTextField();
			field.setEmptyText(msgs.enterFilterText());
			
			switch (item) {
			case CONTAINS:
				
				icon = ResourceBundle.INSTANCE.textContains();
				toolTip = new String(msgs.textContains());
				ct = field;
				break;
			case BEGINS:
				icon = ResourceBundle.INSTANCE.textBegins();
				toolTip = new String(msgs.textBegins());
				bg = field;
				break;
			case ENDS:
				icon = ResourceBundle.INSTANCE.textEnds();
				toolTip = new String(msgs.textEnds());
				en = field;
				break;
			case SOUNDEX:
				icon = ResourceBundle.INSTANCE.textSoundex();
				toolTip = new String(msgs.soundexAlgorithm());
				sd = field;
				break;

			}

			MenuItem menuItem = new MenuItem();
			menuItem.setCanActivate(false);
			menuItem.setHideOnClick(false);
			menuItem.setIcon(icon);
			menuItem.setWidget(field);
			menuItem.setToolTip(toolTip);

			menuItem.getElement().removeClassName(
					CommonStyles.get().unselectable());
			menuItem.getElement().getStyle().setCursor(Cursor.DEFAULT);

			add(menuItem);
		}
	}

	/**
	 * Sets the menu's values
	 * 
	 * @param values
	 *            the values
	 */
	public void setValue(List<FilterConfig> values) {
		for (FilterConfig config : values) {
			String c = config.getComparison();
			String v = config.getValue();
			if (v == null) {
				v = "";
			}

			if ("st".equals(c)) {
				ct.setValue(v);
			} else if ("bg".equals(c)) {
				bg.setValue(v);
			} else if ("en".equals(c)) {
				en.setValue(v);
			} else if ("sd".equals(c)) {
				sd.setValue(v);
			}
		}
		fireUpdate();
	}

	protected TextField createTextField() {
		TextField field = new TextField() {
			@Override
			protected void onKeyUp(Event event) {
				super.onKeyUp(event);
				onFieldKeyUp(this, event);
			}
		};

		return field;
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		updateTask.delay(filter.getUpdateBuffer());
	}

	protected void onFieldKeyUp(TextField field, Event event) {
		int kc = event.getKeyCode();
		if (kc == KeyCodes.KEY_ENTER && field.isCurrentValid()) {
			event.preventDefault();
			event.stopPropagation();
			hide(true);
			return;
		}

		updateTask.delay(filter.getUpdateBuffer());
	}

	private void fireUpdate() {
		filter.fireUpdate();
	}

}
