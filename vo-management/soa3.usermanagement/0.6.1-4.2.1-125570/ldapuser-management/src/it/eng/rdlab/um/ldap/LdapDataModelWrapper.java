package it.eng.rdlab.um.ldap;

import it.eng.rdlab.um.beans.DataModelWrapper;

import java.util.List;
import java.util.Map;

public interface LdapDataModelWrapper  extends DataModelWrapper
{
	public boolean validateData ();
	public Map<String, String> getAttributeMap ();
	public Map<String, List<String>> getListAttributeMap ();
	public List<String> getObjectClasses ();
	public String getDistinguishedName ();
}
