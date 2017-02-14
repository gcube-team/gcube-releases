package it.eng.rdlab.um.ldap.service;

import it.eng.rdlab.um.ldap.LdapDataModelWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapGenericDataModelComparator implements LdapDataModelComparator 
{
	private Log log = null;

	public LdapGenericDataModelComparator() 
	{
		this.log = LogFactory.getLog(LdapGenericDataModelComparator.class);
	}
	
	/* (non-Javadoc)
	 * @see it.eng.rdlab.um.ldap.service.LdapDataModelComparator#compare(it.eng.rdlab.um.ldap.LdapDataModelWrapper, it.eng.rdlab.um.ldap.LdapDataModelWrapper)
	 */
	@Override
	public List<ModificationItem> compare (LdapDataModelWrapper dataModel1, LdapDataModelWrapper dataModel2)
	{
		List<ModificationItem> response = new ArrayList<ModificationItem>();
		log.debug("Generating single value attributes maps...");
		Map<String, String> singleValueAttributes1 = dataModel1.getAttributeMap();
		Map<String, String> singleValueAttributes2 = dataModel2.getAttributeMap();
		log.debug("Maps generated");
		compareSingleValueAttributes (response,singleValueAttributes1,singleValueAttributes2);
		log.debug("Generating multi value attributes maps...");
		Map<String, List<String>> multiValueAttributes1 = dataModel1.getListAttributeMap();
		Map<String, List<String>> multiValueAttributes2 = dataModel2.getListAttributeMap();
		log.debug("Maps generated");
		compareMultiValueAttributes (response,multiValueAttributes1,multiValueAttributes2);
		return response;
	}
	

	protected void compareSingleValueAttributes (List<ModificationItem> response, Map<String, String> attributes1, Map<String, String> attributes2)
	{
		log.debug("Comparing single value attributes");
		Iterator<String> keys1 = attributes1.keySet().iterator();
		log.debug("Checking modifications");
		
		while (keys1.hasNext())
		{
			String key = keys1.next();
			log.debug("Key 1 = "+key);
			String value2 = attributes2.get(key);
			
			if (value2 == null)
			{
				log.debug(key+ " to be removed");
				response.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(key)));
			}
			else if (!attributes1.get(key).equals(value2))
			{
				log.debug(key+ " to be replaced with "+value2);
				response.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(key, value2)));
			}

			
		}
		log.debug("Check completed");
		
		Iterator<String> keys2 = attributes2.keySet().iterator();
		log.debug("Checking extra attributes...");
		
		while (keys2.hasNext())
		{
			String key = keys2.next();
			log.debug("Key 2 = "+key);
			
			if (attributes1.get(key) == null)
			{
				log.debug("Attribute "+key+" to be added");
				response.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(key,attributes2.get(key))));
			}
		}
		
		log.debug("Check completed");
	}
	
	protected void compareMultiValueAttributes (List<ModificationItem> response, Map<String, List<String>> attributes1, Map<String, List<String>> attributes2)
	{
		log.debug("Comparing multi value attributes");
		Iterator<String> keys1 = attributes1.keySet().iterator();
		log.debug("Checking modifications");
		
		while (keys1.hasNext())
		{
			String key = keys1.next();
			log.debug("Key 1 = "+key);
			List<String> attributes2Values = attributes2.get(key);
			
			if (attributes2Values == null) response.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(key)));
			else
			{	
				List<String> attributes1Values = attributes1.get(key);
				BasicAttribute basicAttribute = new BasicAttribute(key);
				boolean update = false;
				
				for (String valueString2 : attributes2Values)
				{
					
					if (!attributes1Values.contains(valueString2)) update = true;
					
					basicAttribute.add(valueString2); // to be ready if we need to update
				}
				
				if (update || (attributes1Values.size()> attributes2Values.size())) response.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, basicAttribute));

			}
			
		}
		log.debug("Check completed");
		
		Iterator<String> keys2 = attributes2.keySet().iterator();
		log.debug("Checking extra attributes...");
		
		while (keys2.hasNext())
		{
			String key = keys2.next();
			log.debug("Key 2 = "+key);
			
			if (attributes1.get(key) == null)
			{
				log.debug("Attribute "+key+" to be added");
				BasicAttribute basicAttribute = new BasicAttribute(key);
				List<String> values = attributes2.get(key);
				
				for (String value : values)
				{
					basicAttribute.add(value);
				}
				
				response.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(key,basicAttribute)));
			}
		}
		
		log.debug("Check completed");
	}

	
}
