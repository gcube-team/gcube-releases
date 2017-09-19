package org.gcube.portlets.user.td.gwtservice.shared.rule.type;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.DimensionReferenceData;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class TDDimensionColumnRuleType extends TDRuleColumnType {

	private static final long serialVersionUID = -2967788094664606371L;
	private DimensionReferenceData dimensionReferenceData;

	public TDDimensionColumnRuleType() {
		super();
	}

	public TDDimensionColumnRuleType(
			DimensionReferenceData dimensionReferenceData) {
		super();
		this.dimensionReferenceData = dimensionReferenceData;
	}

	public DimensionReferenceData getDimensionReferenceData() {
		return dimensionReferenceData;
	}

	public void setDimensionReferenceData(
			DimensionReferenceData dimensionReferenceData) {
		this.dimensionReferenceData = dimensionReferenceData;
	}

	@Override
	public String toString() {
		return "TDDimensionColumnRuleType [dimensionReferenceData="
				+ dimensionReferenceData + "]";
	}

}
