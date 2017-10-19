package org.gcube.portlets.user.td.gwtservice.shared.tr;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ConditionCodeMap {

	public static ConditionCode mapConditionCode(int conditionCode) {
		ConditionCode[] conditions = ConditionCode.values();
		for (ConditionCode cond : conditions) {
			if (cond.getValue() == conditionCode) {
				return cond;
			}
		}

		return ConditionCode.GenericValidity;

	}
}
