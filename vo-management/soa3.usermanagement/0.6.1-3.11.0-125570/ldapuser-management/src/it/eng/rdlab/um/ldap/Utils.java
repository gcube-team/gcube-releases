package it.eng.rdlab.um.ldap;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Utils 
{
	public static List<String> parseDN (String dn)
	{
		Log log = LogFactory.getLog(Utils.class);
		
		log.debug("Element list String dn"+dn);
		String [] elementList = dn.split(",");
		List<String> response = new ArrayList<String>();
		
		for (String element : elementList)
		{
			String [] keyValue = element.split("=");
			
			if (keyValue.length != 2) {
				log.error("the keyValue doesn't exact!! because keyValue.length != 2 but ="+keyValue.length);
				
				
				return null;
			}
			else
			{
				String key = keyValue[0].trim().toLowerCase();
				log.debug("Addedx "+key);
				response.add(key);
			}
		}
		 
		return response;
		
	}

}
