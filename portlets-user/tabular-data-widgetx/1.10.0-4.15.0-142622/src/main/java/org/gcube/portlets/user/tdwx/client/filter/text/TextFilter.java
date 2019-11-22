package org.gcube.portlets.user.tdwx.client.filter.text;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.tdwx.client.filter.text.TextMenu.TextItem;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 * @param <M>
 */
public class TextFilter<M> extends Filter<M, String> {
	/**
	 * The default locale-sensitive messages used by this class.
	 */
	/*public class DefaultTextFilterMessages implements TextFilterMessages {

		@Override
		public String emptyText() {
			return DefaultMessages.getMessages().stringFilter_emptyText();
		}

	}*/

	/**
	 * The locale-sensitive messages used by this class.
	 */
	/*public interface TextFilterMessages {
		String emptyText();
	}*/

	private List<TextItem> textItems = new ArrayList<TextItem>();
	private TextMenu<M> textMenu;
	
	private TextFilterMessages msgs;

	/**
	 * Creates a text filter for the specified value provider. See
	 * {@link Filter#Filter(ValueProvider)} for more information.
	 * 
	 * @param valueProvider
	 *            the value provider
	 */
	public TextFilter(ValueProvider<? super M, String> valueProvider) {
		super(valueProvider);
		msgs = GWT.create(TextFilterMessages.class);
		setHandler(new TextFilterHandler());

		textItems.add(TextItem.CONTAINS);
		textItems.add(TextItem.BEGINS);
		textItems.add(TextItem.ENDS);
		textItems.add(TextItem.SOUNDEX);

		textMenu = new TextMenu<M>(this);
		menu = textMenu;
		textMenu.setTextItems(textItems, msgs);
		
	}

	/**
	 * Sets the contains value.
	 * 
	 * @param value
	 *            the value
	 */
	public void setContainsValue(String value) {
		textMenu.ct.setValue(value);
	}
	
	/**
	 * Sets the begins value.
	 * 
	 * @param value
	 *            the value
	 */
	public void setBeginsValue(String value) {
		textMenu.bg.setValue(value);
	}
	
	/**
	 * Sets the ends value.
	 * 
	 * @param value
	 *            the value
	 */
	public void setEndsValue(String value) {
		textMenu.en.setValue(value);
	}
	
	
	/**
	 * Sets the soundex value.
	 * 
	 * @param value
	 *            the value
	 */
	public void setSoundexValue(String value) {
		textMenu.sd.setValue(value);
	}

	public void setValue(List<FilterConfig> values) {
		textMenu.setValue(values);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FilterConfig> getFilterConfig() {
		return (List<FilterConfig>) getValue();
	}

	/**
	 * Returns the locale-sensitive messages used by this class.
	 * 
	 * @return the local-sensitive messages used by this class.
	 */
	public TextFilterMessages getMessages() {
		return msgs;
	}

	@Override
	public Object getValue() {
		return textMenu.getValue();
	}

	

	@Override
	public boolean isActivatable() {
		if (textMenu.ct != null
				&& textMenu.ct.getCurrentValue() != null) {
			return true;
		}
		if (textMenu.bg != null
				&& textMenu.bg.getCurrentValue() != null) {
			return true;
		}
		if (textMenu.en != null
				&& textMenu.en.getCurrentValue() != null) {
			return true;
		}
		if (textMenu.sd != null
				&& textMenu.sd.getCurrentValue() != null) {
			return true;
		}
		return false;
	}

	public void setMessages(TextFilterMessages messages) {
		this.msgs = messages;
		textMenu.setEmptyText(messages.enterFilterText());
	}


	

	@Override
	protected Class<String> getType() {
		return String.class;
	}

	@Override
	protected boolean validateModel(M model) {
		boolean isValid = true;
		String modelValue = getValueProvider().getValue(model);

		if (textMenu.ct != null) {
			String filterValue = textMenu.ct.getCurrentValue();
			String v = filterValue == null ? "" : filterValue.toString();
			if (v.length() == 0
					&& (modelValue == null || modelValue.length() == 0)) {
				isValid = true;
			} else if (modelValue == null) {
				isValid = false;
			} else {
				isValid = modelValue.toLowerCase().indexOf(v.toLowerCase()) > -1;
			}

		}
		if (textMenu.bg != null) {
			String filterValue = textMenu.bg.getCurrentValue();
			String v = filterValue == null ? "" : filterValue.toString();
			if (v.length() == 0
					&& (modelValue == null || modelValue.length() == 0)) {
				isValid = true;
			} else if (modelValue == null) {
				isValid = false;
			} else {
				isValid = modelValue.toLowerCase().indexOf(v.toLowerCase()) > -1;
			}

		}
		if (textMenu.en != null) {
			String filterValue = textMenu.en.getCurrentValue();
			String v = filterValue == null ? "" : filterValue.toString();
			if (v.length() == 0
					&& (modelValue == null || modelValue.length() == 0)) {
				isValid = true;
			} else if (modelValue == null) {
				isValid = false;
			} else {
				isValid = modelValue.toLowerCase().indexOf(v.toLowerCase()) > -1;
			}

		}
		
		
		if (textMenu.sd != null) {
			String filterValue = textMenu.sd.getCurrentValue();
			String v = filterValue == null ? "" : filterValue.toString();
			if (v.length() == 0
					&& (modelValue == null || modelValue.length() == 0)) {
				isValid = true;
			} else if (modelValue == null) {
				isValid = false;
			} else {
				isValid = modelValue.toLowerCase().indexOf(v.toLowerCase()) > -1;
			}

		}

		return isValid;
	}

	@Override
	public void setFilterConfig(List<FilterConfig> configs) {
		boolean hasValue = false;
		for (int i = 0; i < configs.size(); i++) {
			FilterConfig config = configs.get(i);
			if (config.getValue() != null && !"".equals(config.getValue())) {
				hasValue = true;
			}
		}
		setValue(configs);
		setActive(hasValue, false);
	}
	
	
	@Override
	protected void fireUpdate() {
	    super.fireUpdate();
	}	 
	
}
