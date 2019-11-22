package org.gcube.portlets.user.td.gwtservice.server.trservice;

import org.gcube.data.analysis.tabulardata.commons.utils.Licence;



/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class LicenceMap {
	
	public static Licence map(String licence){
		Licence[] licences=Licence.values();
		for(int i=0; i<licences.length; i++){
			if(licences[i].toString().compareTo(licence)==0){
				return licences[i];
			}
		}
		return null;
	}
}
