package it.eng.rdlab.um.ldap.user.bean;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.rdlab.um.beans.GenericModelWrapper;
import it.eng.rdlab.um.ldap.LdapAbstractModelWrapper;
import it.eng.rdlab.um.ldap.validators.LdapUserValidator;
import it.eng.rdlab.um.user.beans.UserModel;

public class LdapUserModelWrapper extends LdapAbstractModelWrapper {

	private Log log;
	
	public LdapUserModelWrapper(UserModel userModel) 
	{
		super (userModel);
		this.log = LogFactory.getLog(this.getClass());
		initModel(userModel);
	}
	
	@SuppressWarnings("unchecked")
	private void initModel (UserModel userModel)
	{
		log.debug("Loading uid");
		this.attributeMap.put(LdapUserModel.UID, userModel.getUserId());
		log.debug("Loading password");
		char [] password = userModel.getPassword();
		if (password != null && (password.length>1 || password [0] != ' ')) 
		{
			String passwordString = new String (password);
			
			if (!passwordString.equals(LdapUserModel.ENCRYPTED_PASSWORD_LABEL)) this.attributeMap.put(LdapUserModel.PASSWORD, passwordString);
			else log.debug("Encrypted_password label_found");
		}
					
		log.debug("Loading dn");
		this.dn = userModel.getFullname();
	}
	
	
	@Override
	public boolean validateData() 
	{
		return LdapUserValidator.validate(this.dn, this.objectClasses, this.attributeMap);
	}

}
