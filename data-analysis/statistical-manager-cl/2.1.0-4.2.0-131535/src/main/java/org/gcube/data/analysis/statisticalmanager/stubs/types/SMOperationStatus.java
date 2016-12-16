package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(namespace = TYPES_WSDL_NAMESPACE)

@XmlEnum(String.class)
public enum SMOperationStatus {
	
	PENDING,
	RUNNING,
	STOPPED,
	COMPLETED,
	FAILED
}
