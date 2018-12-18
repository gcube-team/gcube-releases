package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;
import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMTable;


@XmlRootElement(namespace=TYPES_WSDL_NAMESPACE)

public class SMTables {
	@XmlElement()//namespace = TYPES_NAMESPACE)
	private List<SMTable> list;
	
	
	  public SMTables() {
		  if(list==null)
			  list= new ArrayList<SMTable>();
	    }

	    public SMTables(
	    		List<SMTable>list) {
	    	if(list!=null)
				  this.list= new ArrayList<SMTable>();
	    }

	public List<SMTable> list() {
		return list;
	}

	/**
	 * @param resource the resource to set
	 */
	public void list(List<SMTable> list) {
		if(list!=null)
			  this.list= new ArrayList<SMTable>();
	}
}
