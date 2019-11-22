package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.dminfo;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ServiceInfoData implements Serializable {

	private static final long serialVersionUID = 4732143678328978038L;
	private String key;
	private String value;
	private String category;

	public ServiceInfoData() {
		super();
	}

	public ServiceInfoData(String key, String value, String category) {
		super();
		this.key = key;
		this.value = value;
		this.category = category;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "ServiceInfoData [key=" + key + ", value=" + value + ", category=" + category + "]";
	}

}
