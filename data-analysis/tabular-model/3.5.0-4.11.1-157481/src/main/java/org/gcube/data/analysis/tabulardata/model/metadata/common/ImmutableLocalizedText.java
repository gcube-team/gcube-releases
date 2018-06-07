package org.gcube.data.analysis.tabulardata.model.metadata.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.metadata.Locales;

@XmlRootElement(name="LocalizedText")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImmutableLocalizedText implements Serializable, LocalizedText {

	private static final long serialVersionUID = 5174487267101638176L;

	@XmlAttribute(name = "value")
	private String value;

	@XmlAttribute(name = "locale")
	private String locale = null;

	@SuppressWarnings("unused")
	private ImmutableLocalizedText() {}

	public ImmutableLocalizedText(String value) {
		this(value,"en");
	};

	/**
	 * 
	 * @param value text value
	 * @param locale ISO639-1 locale code
	 */
	public ImmutableLocalizedText(String value, String locale) {
		setLocale(locale);
		this.value = value;
	}
	
	private void setLocale(String locale){
		if (Locales.isValid(locale))
			this.locale = locale;
		else throw new IllegalArgumentException("The provided string is not a valid ISO639-1 locale code");
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText#getValue()
	 */
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText#getLocale()
	 */
	public String getLocale(){
		return locale;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ImmutableLocalizedText other = (ImmutableLocalizedText) obj;
		if (locale == null) {
			if (other.locale != null)
				return false;
		} else if (!locale.equals(other.locale))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LocalizedText [value=");
		builder.append(value);
		builder.append(", localeCode=");
		builder.append(locale);
		builder.append("]");
		return builder.toString();
	}

}
