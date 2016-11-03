package it.eng.rdlab.um.ldap.role.bean;

import it.eng.rdlab.um.beans.GenericModelWrapper;
import it.eng.rdlab.um.ldap.LdapAbstractModelWrapper;
import it.eng.rdlab.um.ldap.validators.LdapRoleValidator;
import it.eng.rdlab.um.role.beans.RoleModel;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class LdapRoleModelWrapper extends LdapAbstractModelWrapper 
{
	private Log log;
	private List<String> roleOccupantList;
	
	public LdapRoleModelWrapper(RoleModel roleModel) 
	{
		super (roleModel);
		this.log = LogFactory.getLog(this.getClass());
		initModel(roleModel);
	}
	
	@SuppressWarnings("unchecked")
	private void initModel (RoleModel roleModel)
	{
		log.debug("Loading uid");
		if (roleModel.getRoleName() != null && roleModel.getRoleName().length() > 0)this.attributeMap.put(LdapRoleModel.ROLE_CN, roleModel.getRoleName());
		if (roleModel.getDescription() != null && roleModel.getDescription().length() > 0)this.attributeMap.put(LdapRoleModel.DESCRIPTION, roleModel.getDescription());
		log.debug("Loading dn");
		this.dn = roleModel.getRoleId();
		this.roleOccupantList = (List<String>) new GenericModelWrapper(roleModel).getObjectParameter(LdapRoleModel.ROLE_OCCUPANT);
	}
	
	@Override
	public boolean validateData() 
	{
		return LdapRoleValidator.validate(this.dn, this.objectClasses, this.attributeMap,this.roleOccupantList);
	}



}
