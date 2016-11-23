package org.gcube.datapublishing.sdmx.impl.model;

import org.gcube.datapublishing.sdmx.api.model.GCubeSDMXDatasourceDescriptor;

public class GCubeSDMXDatasourceDescriptorImpl implements
		GCubeSDMXDatasourceDescriptor {

	private String rest_url_V2_1;
	private String rest_url_V2;
	private String rest_url_V1;
	private String publishInterfaceUrl;

	public String getRest_url_V2_1() {
		return rest_url_V2_1;
	}

	public void setRest_url_V2_1(String rest_url_V2_1) {
		this.rest_url_V2_1 = rest_url_V2_1;
	}

	public String getRest_url_V2() {
		return rest_url_V2;
	}

	public void setRest_url_V2(String rest_url_V2) {
		this.rest_url_V2 = rest_url_V2;
	}

	public String getRest_url_V1() {
		return rest_url_V1;
	}

	public void setRest_url_V1(String rest_url_V1) {
		this.rest_url_V1 = rest_url_V1;
	}

	public String getPublishInterfaceUrl() {
		return publishInterfaceUrl;
	}

	public void setPublishInterfaceUrl(String publishInterfaceUrl) {
		this.publishInterfaceUrl = publishInterfaceUrl;
	}

}
