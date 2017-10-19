package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;
import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMComputation;

@XmlRootElement()
public class SMComputations {
	@XmlElement()
	private List<SMComputation> list;
	
	
	 public SMComputations() {
		 if(list==null)
			 list= new ArrayList<SMComputation>();
	    }

	    public SMComputations(
	    		List<SMComputation>  list) {
	    	if(list!= null)
	           this.list = new ArrayList<SMComputation>(list);
	    }

	public List<SMComputation> list() {
		return list;
	}

	/**
	 * @param resource the resource to set
	 */
	public void list(List<SMComputation>list) {
		this.list = list;
	}
}
