package org.gcube.datapublishing.sdmx.is;

import java.util.List;

import org.gcube.common.resources.gcore.GenericResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISTableIDDataReader extends ISReader<GenericResource> {

	private Logger logger;
	private final String 	SECONDARY_TYPE = "SecondaryType",
							SECONDARY_TYPE_SDMX = "SDMX",
							NAME = "Name",
							NAME_VALUE = "TableAssociations",
							PROFILE = "$resource";
							
	
	public ISTableIDDataReader() 
	{
		super ();
		this.logger = LoggerFactory.getLogger(ISTableIDDataReader.class);
	}
	
	public GenericResource executeQuery ()
	{
		this.logger.debug("Generating query");
		super.newQuery(GenericResource.class);
		super.addCondition(SECONDARY_TYPE, SECONDARY_TYPE_SDMX);
		super.addCondition(NAME, NAME_VALUE);
		super.setResults(PROFILE);
		List<GenericResource> responseElements = super.submit(GenericResource.class);
		
		if (responseElements.size()>0)
		{
			this.logger.debug("Results OK");
			return responseElements.get(0);
		}
		else return null;
		
	}
	
//	public static Map<String, TableIdentificators> getAssociations (GenericResource resource)
//	{
//		this.logger.debug("Generating query");
//		super.newQuery(GenericResource.class);
//		super.addCondition(SECONDARY_TYPE, SECONDARY_TYPE_SDMX);
//		super.addCondition(NAME, NAME_VALUE);
//		super.setResults(PROFILE);
//		List<GenericResource> responseElements = super.submit(GenericResource.class);
//		
//		if (responseElements.size()>0)
//		{
//			Map<String,TableIdentificators> response = new TableAssociationResource(resource).getAssociationsTable();
//			this.logger.debug("table obtained " +response);
//			return response;
//		}
//		else return null;
//		
//	}
//	
//
//	
//	
//	public static void main(String[] args) {
//		
//		
//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
//
//		SecurityTokenProvider.instance.set("adb146d7-1b6d-43ac-9c9a-d3c7187516c8-98187548");
//		
//		ISTableIDDataReader reader = new ISTableIDDataReader();
//		reader.getAssociations();
//	}
}
