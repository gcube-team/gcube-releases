package it.eng.rdlab.um.ldap.data.beans;

import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.LdapAbstractModelWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class LdapOrganizationalUnitDataModelWrapper extends LdapAbstractModelWrapper 
{
	private Log log;
	private String ou;
	
	public LdapOrganizationalUnitDataModelWrapper(GroupModel groupModel) 
	{
		super (groupModel);
		this.log = LogFactory.getLog(this.getClass());
		initModel(groupModel);
	}
	
	private void initModel (GroupModel groupModel)
	{
		log.debug("Loading uid");
		this.ou = groupModel.getGroupName();
		this.attributeMap.put(LdapOrganizationalUnitDataModel.OU,this.ou );
		log.debug("Loading dn");
		this.dn = groupModel.getGroupId();
	}
	
	@Override
	public boolean validateData() 
	{
		return this.dn != null && this.ou != null;
	}



}
