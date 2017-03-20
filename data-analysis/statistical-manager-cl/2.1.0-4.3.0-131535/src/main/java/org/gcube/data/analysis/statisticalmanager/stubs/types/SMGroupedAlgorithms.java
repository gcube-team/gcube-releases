package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(namespace=TYPES_WSDL_NAMESPACE)
public class SMGroupedAlgorithms implements Serializable {

	private static final long serialVersionUID = 79142466495706764L;


	@XmlElement()
	private String  category;
	
	
	@XmlElement( name="list")
	private List<SMAlgorithm> thelist=null;
	
	   public SMGroupedAlgorithms() {
	    	if(thelist!=null)
	    	{
	    		thelist= new ArrayList<SMAlgorithm> (); 
	    	}
	    }

	    public SMGroupedAlgorithms(
	           String category,
	           List<SMAlgorithm> list) {
	           this.category = category;
	   		if(list!=null)thelist= new ArrayList<SMAlgorithm> ();

//	           this.thelist = list;
	    }
	

	public List<SMAlgorithm>  thelist() {
		return thelist;
	}

	/**
	 * @param resource the resource to set
	 */
	public void thelist(List<SMAlgorithm>list) {
		this.thelist = new ArrayList<SMAlgorithm> (list);
	}
	
	public String category() {
		return category;
	}

	/**
	 * @param resource the resource to set
	 */
	public void category(String category) {
		this.category = category;
	}
}
