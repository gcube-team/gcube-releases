package org.gcube.portlets.user.td.gwtservice.shared.tr;


/**
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ConditionCodeMap {
	
	/**
	 * 
	 * @param conditionCode
	 * @return
	 */
	public static ConditionCode mapConditionCode(int conditionCode) {
		ConditionCode[] conditions=ConditionCode.values();
		for(ConditionCode cond:conditions){
			if(cond.getValue()==conditionCode){
				return cond;
			}
		}
	
		return ConditionCode.GenericValidity;

	}
}
