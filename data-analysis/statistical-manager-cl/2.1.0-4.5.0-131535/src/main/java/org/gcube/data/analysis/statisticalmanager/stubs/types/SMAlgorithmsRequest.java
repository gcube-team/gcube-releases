package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = TYPES_WSDL_NAMESPACE)
public class SMAlgorithmsRequest {
	@XmlElement(name ="parameters")
	private List<SMTypeParameter> list;
	
	 public SMAlgorithmsRequest() {
		 if(list==null)
			 list=new ArrayList<SMTypeParameter>();
	    }
	 
	public SMAlgorithmsRequest(List<SMTypeParameter>  typeParameters) {
		this.list = typeParameters;
	}

	public List<SMTypeParameter>  parameters() {
		return list;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void parameters(List<SMTypeParameter>  parameters) {
		if(parameters!=null)
		this.list = new ArrayList<SMTypeParameter>(parameters);
	}

}
