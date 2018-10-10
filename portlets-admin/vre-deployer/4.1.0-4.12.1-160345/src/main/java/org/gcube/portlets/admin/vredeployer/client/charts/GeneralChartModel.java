package org.gcube.portlets.admin.vredeployer.client.charts;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GeneralChartModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected GeneralChartModel() {

	}

	public GeneralChartModel(String name, String path,SimpleChartModel example) {
		setName(name);
		setPath(path);
		setExample(example);
	}

	public void setName(String name) {
		set("name", name);
	}

	public void setPath(String path) {
		set("path", path);
	}

	public void setExample(SimpleChartModel example) {
		set("example", example);
	}

	public String getName() {
		return get("name");
	}

	public String getPath() {
		return get("path");
	}

	public SimpleChartModel getExample() {
		return get("example");
	}
}

