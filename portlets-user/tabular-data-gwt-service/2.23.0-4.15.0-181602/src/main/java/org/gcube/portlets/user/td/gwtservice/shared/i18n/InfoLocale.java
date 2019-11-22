package org.gcube.portlets.user.td.gwtservice.shared.i18n;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InfoLocale implements Serializable {

	private static final long serialVersionUID = -8025774800803525216L;

	private String language;

	public InfoLocale() {
		super();
	}

	public InfoLocale(String language) {
		super();
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "InfoLocale [language=" + language + "]";
	}
	
	
}
