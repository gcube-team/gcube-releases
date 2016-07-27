/**
 * 
 */
package org.cotrix.gcube.extension;

import static org.cotrix.domain.dsl.Roles.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cotrix.domain.user.Role;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 * 
 */
public class DefaultRoleMapper implements RoleMapper {

	@Override
	public Collection<Role> map(Collection<String> roles) {

		List<Role> mapped = new ArrayList<>();

		for (PortalRole role : PortalRole.values())
			if (roles.contains(role.value))
				mapped.add(role.internal);

		mapped.add(USER);

		return mapped;
	}

}
