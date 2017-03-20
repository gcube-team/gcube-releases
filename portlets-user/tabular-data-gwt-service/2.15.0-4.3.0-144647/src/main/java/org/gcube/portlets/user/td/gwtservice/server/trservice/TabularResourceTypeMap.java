package org.gcube.portlets.user.td.gwtservice.server.trservice;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TabResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TabularResourceTypeMap {
	private static Logger logger = LoggerFactory
			.getLogger(TabularResourceTypeMap.class);
	
	public static TabResourceType map(TabularResourceType tabularResourceType) {		
		if(tabularResourceType==null){
			logger.error("Tabular Resource Type is null");
			return TabResourceType.UNKNOWN;
		}
		
		switch (tabularResourceType) {
		case FLOW:
			return TabResourceType.FLOW;
		case STANDARD:
			return TabResourceType.STANDARD;
		default:
			logger.error("Tabular Resource Type is unknown");
			return TabResourceType.UNKNOWN;
			
				
		}
	}
}
