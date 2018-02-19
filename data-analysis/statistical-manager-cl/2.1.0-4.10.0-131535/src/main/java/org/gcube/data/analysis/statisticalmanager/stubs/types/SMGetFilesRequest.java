package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMPagedRequest;

@XmlRootElement(namespace=TYPES_WSDL_NAMESPACE)
public class SMGetFilesRequest extends  SMPagedRequest{
	 public SMGetFilesRequest() {
		 super();
	    }
}
