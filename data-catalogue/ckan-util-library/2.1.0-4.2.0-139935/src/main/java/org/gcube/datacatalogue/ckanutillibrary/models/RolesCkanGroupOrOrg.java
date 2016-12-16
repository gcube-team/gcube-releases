package org.gcube.datacatalogue.ckanutillibrary.models;

/**
 * Roles that user can have into organizations/groups.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum RolesCkanGroupOrOrg{
	MEMBER,
	EDITOR,
	ADMIN;

	public static String convertToCkanCapacity(RolesCkanGroupOrOrg role){

		if(role == null)
			return null;
		else
			return role.toString().toLowerCase();

	}

	public static RolesCkanGroupOrOrg convertFromCapacity(String capacity){

		if(capacity == null)
			return null;
		else
			return RolesCkanGroupOrOrg.valueOf(capacity.toUpperCase());

	}
}