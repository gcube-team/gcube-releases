package org.gcube.common.authorization.library.provider;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.authorization.library.ClientType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExternalServiceInfo extends ClientInfo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String generatedBy;
	
	public ExternalServiceInfo(String id, String generatedBy) {
		super();
		this.id = id;
		this.generatedBy = generatedBy;
	}
	
	protected ExternalServiceInfo() {
		super();
	}

	@Override
	public String getId() {
		return id;
	}
	
	public String getGeneratedBy() {
		return generatedBy;
	}

	@Override
	public List<String> getRoles() {
		return Collections.emptyList();
	}

	@Override
	public ClientType getType() {
		return ClientType.EXTERNALSERVICE;
	}
}
