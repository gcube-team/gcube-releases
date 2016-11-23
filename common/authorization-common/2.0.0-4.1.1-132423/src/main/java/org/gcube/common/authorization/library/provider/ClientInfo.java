package org.gcube.common.authorization.library.provider;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(value={UserInfo.class, ServiceInfo.class, ExternalServiceInfo.class, ContainerInfo.class})
public abstract class ClientInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract String getId();
		
	public abstract List<String> getRoles();
	
}
