package org.gcube.data.analysis.tabulardata.model.metadata.common;

import java.util.List;

public interface LocalizedTextHolder {

	public abstract List<LocalizedText> getTexts();

	/**
	 * 
	 * @param locale ISO639-1 locale code
	 * @return a boolean
	 */
	public abstract boolean hasTextWithLocale(String locale);

	/**
	 * 
	 * @param locale ISO639-1 locale code
	 * @return a label or null
	 */
	public abstract LocalizedText getTextWithLocale(String locale);

}