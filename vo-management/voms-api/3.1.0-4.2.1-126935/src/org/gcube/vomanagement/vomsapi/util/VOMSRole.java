package org.gcube.vomanagement.vomsapi.util;


/**
 * This class models a VOMS Role.
 * The groupName must be provided with the initial '/' and without the final one. E.g: /exampleVO/group1/subGroup1.
 * 
 * @author roccetti
 *
 */
public class VOMSRole implements VOMSFQANInfo {
	
	/**
	 * the VOMS group
	 * */
	private String groupName;
	
	/**
	 * the VOMS VO name
	 * */
	public String voName;
	
	/**
	 * the VOMS role name
	 * */
	private String roleName;

	/**
	 * the VOMS capability name
	 */
	//private String capability;
	
	/**
	 * Constructor
	 * 
	 * @param groupName the VOMS group
	 * @param voName the VOMS VO name
	 * @param roleName the VOMS role name
	 */
	public VOMSRole(String groupName, String voName, String roleName) 
	{
		super();
		this.groupName = groupName;
		this.voName = voName;
		this.roleName = roleName;
		//this.capability = null;
	}
	
	/**
	 * Constructor
	 * 
	 * @param VOMSRole the VOMS role where to extract information
	 */
	public VOMSRole(String VOMSRole) 
	{
		super();
		if(VOMSRole == null) throw new NullPointerException("VOMS role cannot be null.");
		if(VOMSRole.equals("")) throw new IllegalArgumentException("VOMS role cannot be an empty string.");
		
		String[] voName_groupRoleName = VOMSRole.split(":");
		if(voName_groupRoleName.length != 2) throw new IllegalArgumentException("This is not a valid VOMS role structure.");
		this.voName = voName_groupRoleName[0];
		
		String group_role = voName_groupRoleName[1];
		String[] groupName_RoleName = group_role.split("/Role=");
		
		if(groupName_RoleName.length != 2) throw new IllegalArgumentException("This is not a valid VOMS role structure.");
		if(!groupName_RoleName[0].startsWith("/")) throw new IllegalArgumentException("This is not a valid VOMS role structure.");
		this.groupName = groupName_RoleName[0];
		this.roleName = groupName_RoleName[1];
		//this.capability = null;
		
		}
	
	/**
	 * @return the VOMS representation of this role
	 * */
	public String toString() 
	{	
		return this.getString();
	}
	
	@Override
	public String getString ()
	{
		StringBuilder response = new StringBuilder(this.voName);
		response.append(":").append(this.groupName).append("/Role=").append(this.roleName);		
		return response.toString();
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the voName
	 */
	@Override
	public String getVoName() {
		return voName;
	}

	/**
	 * @param voName the voName to set
	 */
	@Override
	public void setVoName(String voName) {
		this.voName = voName;
	}
	
	/**
	 * 
	 * @param capability the name of the capability
	 */
	
	public void setCapability (String capability)
	{
		//this.capability = capability;
	}
	
	/**
	 * 
	 * @return the capability (null if not set)
	 */
	
	public String getCapability ()
	{
		return null;
	}

	@Override
	public String getFQAN() {
		StringBuilder response = new StringBuilder(this.groupName);
		response.append("/Role=").append(this.roleName);	
		return response.toString();
	}

}
