package org.gcube.smartgears.configuration.library;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.ValidationError;
import org.gcube.common.validator.Validator;
import org.gcube.common.validator.ValidatorFactory;
import org.gcube.common.validator.annotations.NotEmpty;

@XmlRootElement(name="smartgears")
public class SmartGearsConfiguration {

	@XmlAttribute @NotEmpty
	private String version;
	
	public SmartGearsConfiguration(){
	}
	
	public String version() {
		return version;
	}
	
	public SmartGearsConfiguration version(String version) {
		this.version=version;
		return this;
	}
	

	/**
	 * Validates this configuration
	 * 
	 * @throws IllegalStateException if the configuration is invalid
	 */
	public void validate() {

		List<String> msgs = new ArrayList<String>();

		Validator validator = ValidatorFactory.validator();

		for (ValidationError error : validator.validate(this))
			msgs.add(error.toString());

		if (!msgs.isEmpty())
			throw new IllegalStateException("invalid configuration: "+msgs);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SmartGearsConfiguration other = (SmartGearsConfiguration) obj;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	
	
	
}
