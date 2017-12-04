/**
 * 
 */
package org.gcube.data.tm.activationrecord;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.gcube.data.tm.Constants;
import org.gcube.data.tm.utils.BindParametersWrapper;

/**
 * The body of an activation record.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name="Record", namespace=Constants.COMMON_NS)
@XmlType(propOrder={"createdBy","parameters"})
public class ActivationRecordBody {

	private String createdBy;
	private BindParametersWrapper parameters;
	
	/**Creates an instance.*/
	public ActivationRecordBody() {} //necessary for deserialisation
	
	/**
	 * Returns the identifier of record's creator.
	 * @return the identifier.
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	
	/**
	 * Sets the identifier of the record's creator.
	 * @param id the identifier.
	 */
	@XmlElement public void setCreatedBy(String id) {
		this.createdBy = id;
	}
	
	/**
	 * Returns the parameters of the activation.
	 * @return the parameters.
	 */
	public BindParametersWrapper getParameters() {
		return parameters;
	}
	
	/**
	 * Sets the parameters of the activation.
	 * @param parameters the parameters.
	 */
	@XmlElement 
	public void setParameters(BindParametersWrapper parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * Creates an instance from the identifier of the record's creator and
	 * the activation's parameters.
	 * @param id the identifier.
	 * @param params the parameters.
	 */
	public ActivationRecordBody(String id,BindParametersWrapper params) {
		createdBy=id;
		parameters=params;
	}

}
