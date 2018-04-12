package org.gcube.portlets.user.databasesmanager.client.datamodel;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Result extends BaseModelData implements IsSerializable {

	private static final long serialVersionUID = 1L;

	// private String index;
	// private String value;

	public Result() {
	}

	public Result(String index, String value) {
		set("index", index);
		set("value", value);
	}

	public String getValue() {
		return get("value");
	}

	public String getIndex() {
		return get("index");
	}
}
