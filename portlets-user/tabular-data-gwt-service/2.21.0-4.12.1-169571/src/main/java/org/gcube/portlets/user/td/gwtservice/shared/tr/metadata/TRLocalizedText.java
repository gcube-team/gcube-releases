package org.gcube.portlets.user.td.gwtservice.shared.tr.metadata;

import java.io.Serializable;



/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class TRLocalizedText implements  Serializable {
	
	private static final long serialVersionUID = 5208229342328376604L;

	private int id;
	private String value;
	private String localeCode;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLocaleCode() {
		return localeCode;
	}

	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "TRLocalizedText [id=" + id + ", value=" + value
				+ ", localeCode=" + localeCode + "]";
	}
	
	
	
}
