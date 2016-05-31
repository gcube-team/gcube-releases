package it.eng.rdlab.um.ldap.group.bean;

import it.eng.rdlab.um.beans.GenericModelWrapper;
import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.ldap.LdapAbstractModelWrapper;
import it.eng.rdlab.um.ldap.validators.LdapGroupValidator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class LdapGroupModelWrapper extends LdapAbstractModelWrapper 
{
	private Log log;
	private List<String> membersList;
	
	public LdapGroupModelWrapper(GroupModel groupModel) 
	{
		super (groupModel);
		this.log = LogFactory.getLog(this.getClass());
		initModel(groupModel);
	}
	
	@SuppressWarnings("unchecked")
	private void initModel (GroupModel groupModel)
	{
		log.debug("Loading uid");
		if (groupModel.getGroupName() != null && groupModel.getGroupName().length() > 0)this.attributeMap.put(LdapGroupModel.GROUP_CN, groupModel.getGroupName());
		if (groupModel.getDescription() != null && groupModel.getDescription().length() > 0)this.attributeMap.put(LdapGroupModel.DESCRIPTION, groupModel.getDescription());
		log.debug("Loading dn");
		this.dn = groupModel.getGroupId();
		this.membersList = (List<String>) new GenericModelWrapper(groupModel).getObjectParameter(LdapGroupModel.MEMBERS_DN);
	}
	
	@Override
	public boolean validateData() 
	{
		return LdapGroupValidator.validate(this.dn, this.objectClasses, this.attributeMap,this.membersList);
	}



}
