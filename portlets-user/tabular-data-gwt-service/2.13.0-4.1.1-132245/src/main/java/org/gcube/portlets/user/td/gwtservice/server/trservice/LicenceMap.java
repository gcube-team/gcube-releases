package org.gcube.portlets.user.td.gwtservice.server.trservice;

import org.gcube.data.analysis.tabulardata.commons.utils.Licence;



/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
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
