package org.gcube.common.authorization.library;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.policies.Service2ServicePolicy;
import org.gcube.common.authorization.library.policies.User2ServicePolicy;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorization.library.provider.ExternalServiceInfo;
import org.gcube.common.authorization.library.provider.ServiceInfo;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.scope.api.ServiceMap;
import org.gcube.common.scope.impl.DefaultServiceMap;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationEntry {

	@XmlElementRefs({
		@XmlElementRef(type = UserInfo.class),
		@XmlElementRef(type = ServiceInfo.class),
		@XmlElementRef(type = ExternalServiceInfo.class),
		@XmlElementRef(type = ContainerInfo.class)
	})
	ClientInfo clientInfo;
	
	private String context;
	@XmlElementRefs({@XmlElementRef(type=DefaultServiceMap.class)})
	private ServiceMap map;
	@XmlElementRefs({
		@XmlElementRef(type = Service2ServicePolicy.class),
		@XmlElementRef(type = User2ServicePolicy.class),
	})
	private List<Policy> policies = new ArrayList<Policy>();
	
	
	@XmlElement
	private String qualifier;
	
	
	
	protected AuthorizationEntry(){}
	
	public AuthorizationEntry(ClientInfo clientInfo, String context, List<Policy> policies, String qualifier) {
		super();
		this.clientInfo = clientInfo;
		this.context = context;
		this.policies = policies;
		this.qualifier = qualifier;
	}

	public ClientInfo getClientInfo() {
		return clientInfo;
	}

	public String getContext() {
		return context;
	}
		
	public ServiceMap getMap() {
		return map;
	}
	
	public String getQualifier() {
		return qualifier;
	}

	public void setMap(ServiceMap map) {
		this.map = map;
	}

	public List<Policy> getPolicies() {
		return policies;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientInfo == null) ? 0 : clientInfo.hashCode());
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result
				+ ((qualifier == null) ? 0 : qualifier.hashCode());
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
		AuthorizationEntry other = (AuthorizationEntry) obj;
		if (clientInfo == null) {
			if (other.clientInfo != null)
				return false;
		} else if (!clientInfo.equals(other.clientInfo))
			return false;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (qualifier == null) {
			if (other.qualifier != null)
				return false;
		} else if (!qualifier.equals(other.qualifier))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AuthorizationEntry [clientInfo=" + clientInfo + ", context="
				+ context + ", map=" + map + ", qualifier=" + qualifier
				+ ", policies=" + policies + "]";
	}

		
}
