package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = TYPES_WSDL_NAMESPACE)
public class SMComputationRequest {
	@XmlElement()
	private String user;

	@XmlElement()
	private String title;

	@XmlElement()
	private String description;

	@XmlElement()
	private SMComputationConfig config;

	public SMComputationRequest() {
	}

	public SMComputationRequest(SMComputationConfig config, String description,
			String title, String user) {
		this.user = user;
		this.title = title;
		this.description = description;
		this.config = config;
	}

	public String user() {
		return user;
	}

	public void user(String user) {
		this.user = user;
	}

	public String title() {
		return title;
	}

	public void title(String title) {
		this.title = title;
	}

	public String description() {
		return description;
	}

	public void description(String description) {
		this.description = description;
	}

	public SMComputationConfig config() {
		return config;
	}

	public void config(SMComputationConfig config) {
		this.config = config;
	}
}
