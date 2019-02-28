package org.gcube.data.analysis.statisticalmanager.stubs.types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMPagedRequest;

@XmlType(name="SMComputationsRequest")
public class SMComputationsRequest extends SMPagedRequest {

	public SMComputationsRequest() {
		super();

		if(parameters==null)
			parameters= new ArrayList<SMTypeParameter>();
	}

	public SMComputationsRequest(List<SMTypeParameter> parameters) {
		if(parameters!=null)
			this.parameters =new ArrayList<SMTypeParameter>(parameters) ;
	}

	@XmlElement()
	private List<SMTypeParameter> parameters;

	public List<SMTypeParameter> parameters() {
		return parameters;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void parameters(List<SMTypeParameter> parameters) {
		if(parameters!=null)
			this.parameters =new ArrayList<SMTypeParameter>(parameters) ;
	}

}
