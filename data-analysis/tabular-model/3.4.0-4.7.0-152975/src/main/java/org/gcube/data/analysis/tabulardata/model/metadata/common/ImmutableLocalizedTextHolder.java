package org.gcube.data.analysis.tabulardata.model.metadata.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ImmutableLocalizedTextHolder implements LocalizedTextHolder, Serializable {

	private static final long serialVersionUID = -7435564643334923290L;

	protected List<LocalizedText> texts = new ArrayList<LocalizedText>();

	protected ImmutableLocalizedTextHolder() {}

	public ImmutableLocalizedTextHolder(List<LocalizedText> texts) {
		for (LocalizedText localizedText : texts) {
			LocalizedText textWithSameLocale = getTextWithLocale(localizedText.getLocale());
			if ( textWithSameLocale != null ) this.texts.remove(textWithSameLocale);
			this.texts.add(localizedText);
		}
	}
	
	public ImmutableLocalizedTextHolder(Set<LocalizedText> texts){
		this.texts = new ArrayList<LocalizedText>(texts);
	}

	public List<LocalizedText> getTexts() {
		return Collections.unmodifiableList(texts);
	}

	public boolean hasTextWithLocale(String locale) {
		if (getTextWithLocale(locale) != null)
			return true;
		return false;
	}

	public LocalizedText getTextWithLocale(String locale) {
		for (LocalizedText text : texts) {
			if (text.getLocale().equals(locale))
				return text;
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((texts == null) ? 0 : texts.hashCode());
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
		ImmutableLocalizedTextHolder other = (ImmutableLocalizedTextHolder) obj;
		if (texts == null) {
			if (other.texts != null)
				return false;
		} else if (!texts.equals(other.texts))
			return false;
		return true;
	}

}
