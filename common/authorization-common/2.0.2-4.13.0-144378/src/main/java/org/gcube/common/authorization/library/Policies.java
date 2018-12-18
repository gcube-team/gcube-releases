package org.gcube.common.authorization.library;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.policies.Service2ServicePolicy;
import org.gcube.common.authorization.library.policies.User2ServicePolicy;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Policies {

	@XmlElementRefs({
		@XmlElementRef(type = Service2ServicePolicy.class),
		@XmlElementRef(type = User2ServicePolicy.class),
	})
	List<Policy> policies = new ArrayList<Policy>();

	
	@SuppressWarnings("unused")
	private Policies(){}
	
	public Policies(List<Policy> policies) {
		super();
		this.policies = policies;
	}

	public List<Policy> getPolicies() {
		return policies;
	}
		
}
