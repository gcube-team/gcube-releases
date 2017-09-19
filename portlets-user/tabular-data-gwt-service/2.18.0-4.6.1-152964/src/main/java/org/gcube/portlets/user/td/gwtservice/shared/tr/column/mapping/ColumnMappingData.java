package org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ColumnMappingData implements Serializable {

	private static final long serialVersionUID = 733237646914552402L;

	protected DimensionRow sourceArg;
	protected DimensionRow targetArg;

	/**
	 * 
	 */
	public ColumnMappingData() {

	}

	/**
	 * 
	 * @param sourceArg
	 *            Source dimension row
	 * @param targetArg
	 *            Target dimension row
	 */
	public ColumnMappingData(DimensionRow sourceArg, DimensionRow targetArg) {
		this.sourceArg = sourceArg;
		this.targetArg = targetArg;
	}

	public DimensionRow getSourceArg() {
		return sourceArg;
	}

	public void setSourceArg(DimensionRow sourceArg) {
		this.sourceArg = sourceArg;
	}

	public DimensionRow getTargetArg() {
		return targetArg;
	}

	public void setTargetArg(DimensionRow targetArg) {
		this.targetArg = targetArg;
	}

	@Override
	public String toString() {
		return "ColumnMappingData [sourceArg=" + sourceArg + ", targetArg=" + targetArg + "]";
	}

}
