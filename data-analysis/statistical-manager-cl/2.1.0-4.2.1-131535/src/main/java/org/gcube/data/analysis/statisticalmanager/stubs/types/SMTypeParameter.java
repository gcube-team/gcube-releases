package org.gcube.data.analysis.statisticalmanager.stubs.types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.StatisticalServiceType;

@XmlRootElement()

public class SMTypeParameter {
	@XmlElement()
	private StatisticalServiceType  name;
	
	
	@XmlElement( name="values")
	private List<String> parameters=null;
	
	

    public SMTypeParameter() {
    	if (parameters==null)
    		parameters=new ArrayList<String>();
    }

    public SMTypeParameter(StatisticalServiceType name,
           List<String>  values) {
           this.name = name;
           if(values!=null)
           this.parameters = values;
    }

	public List<String>  values() {
		return parameters;
	}

	/**
	 * @param resource the resource to set
	 */
	public void values(List<String>  values) {
		if(values!=null)
		this.parameters = new ArrayList<String>(values);
	}
	
	
	
	public StatisticalServiceType name() {
		return name;
	}

	/**
	 * @param resource the resource to set
	 */
	public void name(StatisticalServiceType name) {
		this.name = name;
	}
}
