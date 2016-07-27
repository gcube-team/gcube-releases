package org.cotrix.gcube.extension;

import java.util.ArrayList;
import java.util.Collection;

import org.cotrix.domain.dsl.Roles;
import org.cotrix.domain.user.Role;

public enum PortalRole {

	VRE_Designer("VRE-Designer", Roles.MANAGER),
	VRE_MANAGER("VRE-Manager",Roles.MANAGER),
	VO_ADMIN("VO-Admin",Roles.MANAGER);
	
	public final String value;
	public final Role internal;
	
	PortalRole(String value,Role internal) {
		this.value=value;
		this.internal=internal;
	}
	
	public static Collection<Role> roles() {
		Collection<Role> roles = new ArrayList<>(values().length);
		for (PortalRole portalRole:values()) roles.add(portalRole.internal);
		return roles;
	}
}
