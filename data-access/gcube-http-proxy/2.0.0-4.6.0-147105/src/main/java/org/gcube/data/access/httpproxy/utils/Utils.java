package org.gcube.data.access.httpproxy.utils;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	public static String getAddress (String path,ServletRequest req, boolean setParameters)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		String response = null;
		
		logger.debug("Path = "+path);
		
		if (path != null)
		{
			StringBuilder finalAddressBuilder = new StringBuilder(path);
			finalAddressBuilder.deleteCharAt(0);
			if (finalAddressBuilder.indexOf("http//") != 0) finalAddressBuilder.insert(0, "http://");
			
			if (setParameters) setParameters(finalAddressBuilder, req.getParameterMap());
			
			String finalAddress = finalAddressBuilder.toString();
			logger.debug("Final address "+finalAddress);
			response = finalAddress;
		}
		
		logger.debug("Final address "+response);
		return response;

	}
	
	
	private static void setParameters (StringBuilder finalAddressBuilder,Map<String, String[]> parameters)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		
		if (parameters == null || parameters.isEmpty())
		{
			logger.debug("No parameters found");
		}
		else
		{
			logger.debug("Getting parameters");
			finalAddressBuilder.append("?");
			Iterator<String> paramsNames = parameters.keySet().iterator();
			
			while (paramsNames.hasNext())
			{
				String name = paramsNames.next();
				logger.debug("Parameter name = "+name);
				String[] values = parameters.get(name);
				
				if (values != null && values.length>0)
				{
					String value = values[0];
					logger.debug("Value = "+value);
					finalAddressBuilder.append(name).append("=");
					finalAddressBuilder.append(value).append("&");
				}
				else logger.debug("Invalid parameter");
				

			}
			
			finalAddressBuilder.deleteCharAt(finalAddressBuilder.length()-1);
		}
				
	}

	
}
