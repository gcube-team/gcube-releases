package org.gcube.application.framework.oaipmh.constants;

import java.util.Properties;

public class ResponseConstants {
	
	public static final int RESULTS_PER_PAGE = 50;
	
	private static Properties headerProps;
//	private static Properties eprintsProps;
//	private static Properties brandingProps;
	
	static{
		headerProps = new Properties();
		headerProps.put("xmlns", "http://www.openarchives.org/OAI/2.0/");
		headerProps.put("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		headerProps.put("xsi:schemaLocation", "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd");
		
//		eprintsProps = new Properties();
//		eprintsProps.put("xmlns", "http://www.openarchives.org/OAI/1.1/eprints");
//		eprintsProps.put("xmlns:xsi", headerProps.get("xmlns:xsi"));
//		eprintsProps.put("xsi:schemaLocation", "http://www.openarchives.org/OAI/1.1/eprints http://www.openarchives.org/OAI/1.1/eprints.xsd");
//		
//		brandingProps = new Properties();
//		brandingProps.put("xmlns", "http://www.openarchives.org/OAI/2.0/branding/");
//		brandingProps.put("xmlns:xsi", headerProps.get("xmlns:xsi"));
//		brandingProps.put("xsi:schemaLocation", "http://www.openarchives.org/OAI/2.0/branding/ http://www.openarchives.org/OAI/2.0/branding.xsd");
		
	}
	
	public static Properties getHeaderProps(){
		return headerProps;
	}
	
//	public static Properties getEprintsProps(){
//		return eprintsProps;
//	}
//	
//	public static Properties getBrandingProps(){
//		return brandingProps;
//	}
	
	
	
	
}
