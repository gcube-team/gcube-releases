package org.gcube.data.analysis.statisticalmanager.stubs.types;
import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(namespace=TYPES_WSDL_NAMESPACE)

public class SMParameters {
	@XmlElement()
	private List<SMParameter> list;
	 public SMParameters() {
	    }
	
	 public SMParameters(
			 List<SMParameter> list) {
		 if(list==null)
	           this.list = new ArrayList<SMParameter>( list);
	    }

	public List<SMParameter> list() {
		return list;
	}

	/**
	 * @param resource the resource to set
	 */
	public void list(List<SMParameter> parameters) {
		 if(list==null)
	           this.list = new ArrayList<SMParameter>( parameters);
	}
}
