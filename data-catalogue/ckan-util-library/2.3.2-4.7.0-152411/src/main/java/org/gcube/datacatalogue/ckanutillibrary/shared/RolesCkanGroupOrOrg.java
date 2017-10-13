package org.gcube.datacatalogue.ckanutillibrary.shared;

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
	
	/**
	 * Get the higher role between role1 and role2
	 * @param role1
	 * @param role2
	 * @return the higher role
	 */
	public static RolesCkanGroupOrOrg getHigher(RolesCkanGroupOrOrg role1, RolesCkanGroupOrOrg role2){
		return role1.ordinal() > role2.ordinal() ? role1 : role2;
	}
}