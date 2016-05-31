package it.eng.rdlab.um.ldap.service;

import it.eng.rdlab.um.ldap.LdapDataModelWrapper;

import java.util.List;

import javax.naming.directory.ModificationItem;

public interface LdapDataModelComparator 
{

	public abstract List<ModificationItem> compare(LdapDataModelWrapper dataModel1, LdapDataModelWrapper dataModel2);

}