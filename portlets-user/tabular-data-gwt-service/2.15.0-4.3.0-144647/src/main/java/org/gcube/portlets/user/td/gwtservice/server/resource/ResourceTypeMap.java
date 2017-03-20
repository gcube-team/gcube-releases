package org.gcube.portlets.user.td.gwtservice.server.resource;

import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDType;

/**
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ResourceTypeMap {
	//private static Logger logger = LoggerFactory.getLogger(ResourceTypeMap.class);

	public static ResourceType getResourceType(ResourceTDType resourceTDType){
		switch (resourceTDType) {
		case CHART:
			return ResourceType.CHART;
		case CODELIST:
			return ResourceType.CODELIST;
		case CSV:
			return ResourceType.CSV;
		case GUESSER:
			return ResourceType.GUESSER;
		case JSON:
			return ResourceType.JSON;
		case MAP:
			return ResourceType.MAP;
		case SDMX:
			return ResourceType.SDMX;
		case GENERIC_FILE:
			return ResourceType.GENERIC_FILE;
		case GENERIC_TABLE:
			return ResourceType.GENERIC_TABLE;
		default:
			return null;
		}
	}
	
	public static ResourceTDType getResourceTDType(ResourceType resourceType){
		switch (resourceType) {
		case CHART:
			return ResourceTDType.CHART;
		case CODELIST:
			return ResourceTDType.CODELIST;
		case CSV:
			return ResourceTDType.CSV;
		case GUESSER:
			return ResourceTDType.GUESSER;
		case JSON:
			return ResourceTDType.JSON;
		case MAP:
			return ResourceTDType.MAP;
		case SDMX:
			return ResourceTDType.SDMX;
		case GENERIC_FILE:
			return ResourceTDType.GENERIC_FILE;
		case GENERIC_TABLE:
			return ResourceTDType.GENERIC_TABLE;
		default:
			return null;
		
		}
	}
	
	


}
