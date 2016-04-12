package org.gcube.data.analysis.tabulardata.commons.utils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.templates.model.ReferenceObject;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.metadata.Locales;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LocaleReference extends ReferenceObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String locale;
	
	@SuppressWarnings("unused")
	private LocaleReference(){}
	
	public LocaleReference(String locale){
		this.locale = locale;
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	@Override
	public boolean check(Class<? extends DataType> datatype) {
		return Locales.isValid(locale);
	}
	
	
	
}
