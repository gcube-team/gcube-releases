package it.eng.rdlab.um.ldap.group.bean;

import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.LdapAbstractModelWrapper;
import it.eng.rdlab.um.ldap.validators.LdapOrganizationValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class LdapOrganizationModelWrapper extends LdapAbstractModelWrapper 
{
	private Log log;

	
	public LdapOrganizationModelWrapper(GroupModel organizationModel) 
	{
		super (organizationModel);
		this.log = LogFactory.getLog(this.getClass());
		initModel(organizationModel);
	}


	private void initModel (GroupModel organizationModel)
	{
		log.debug("Loading uid");
		
		if (organizationModel.getGroupName() != null && organizationModel.getGroupName().length() > 0) this.attributeMap.put(LdapOrganizationModel.ORGANIZATION_NAME, organizationModel.getGroupName());
//		this.attributeMap.put(LdapOrganizationModel.ORGANIZATION_DESCRIPTION, organizationModel.getDescription());
		log.debug("Loading dn");
		this.dn = organizationModel.getGroupId();
	}
	
	@Override
	public boolean validateData() 
	{
		return LdapOrganizationValidator.validate(dn, this.objectClasses, attributeMap);
	}



}
