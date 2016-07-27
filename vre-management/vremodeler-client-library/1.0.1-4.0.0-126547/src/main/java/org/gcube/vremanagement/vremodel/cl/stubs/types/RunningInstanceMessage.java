package org.gcube.vremanagement.vremodel.cl.stubs.types;

import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class RunningInstanceMessage {

	@XmlElement(namespace=TYPES_NAMESPACE)
	private String serviceClass;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String serviceName;
	
	protected RunningInstanceMessage() {
		super();
	}

	public RunningInstanceMessage(String serviceClass, String serviceName) {
		super();
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
	}

	/**
	 * @return the serviceClass
	 */
	public String serviceClass() {
		return serviceClass;
	}

	/**
	 * @param serviceClass the serviceClass to set
	 */
	public void serviceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	/**
	 * @return the serviceName
	 */
	public String serviceName() {
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void serviceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	
	
}
