package org.gcube.data.analysis.tabulardata.model.metadata.column;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.metadata.Locales;

@XmlRootElement(name="DataLocaleMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataLocaleMetadata implements ColumnMetadata {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2373785745389900063L;
	
	@XmlAttribute(name="locale")
	private String locale;
	
	@SuppressWarnings("unused")
	private DataLocaleMetadata() {}


	/**
	 * @param locale ISO639-1 locale code
	 */
	public DataLocaleMetadata(String locale) {
		setLocale(locale);
	}

	private void setLocale(String locale) {
		if (Locales.isValid(locale))
			this.locale = locale;
		else throw new IllegalArgumentException("The provided string is not a valid ISO639-1 locale code");
	}


	/**
	 * 
	 * @return ISO639-1 locale code
	 */
	public String getLocale() {
		return locale;
	}
	

	public boolean isInheritable() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
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
		DataLocaleMetadata other = (DataLocaleMetadata) obj;
		if (locale == null) {
			if (other.locale != null)
				return false;
		} else if (!locale.equals(other.locale))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataLocaleMetadata [locale=");
		builder.append(locale);
		builder.append("]");
		return builder.toString();
	}

}
