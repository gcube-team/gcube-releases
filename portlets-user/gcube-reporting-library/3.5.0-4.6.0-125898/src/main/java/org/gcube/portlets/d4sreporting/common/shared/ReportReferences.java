package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * Groups a list of References of the same Type
 *
 */
public class ReportReferences implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7378402633724641873L;

	private String refType;
	private ArrayList<Tuple> tuples = new ArrayList<Tuple>();
	private boolean singleRelation;
	
	public ReportReferences() {
		super();
	}
	
	public ReportReferences(String refType, ArrayList<Tuple> tuples, boolean singleRelation) {
		super();
		this.refType = refType;
		this.tuples = tuples;
		this.singleRelation = singleRelation;
	}
	
	public String getRefType() {
		return refType;
	}
	public void setRefType(String refType) {
		this.refType = refType;
	}
	public ArrayList<Tuple> getTuples() {
		return tuples;
	}
	public void setTuples(ArrayList<Tuple> tuples) {
		this.tuples = tuples;
	}
	
	public boolean isSingleRelation() {
		return singleRelation;
	}

	public void setSingleRelation(boolean singleRelation) {
		this.singleRelation = singleRelation;
	}

	@Override
	public String toString() {
		return "ReportReferences [refType=" + refType + ", tuples=" + tuples
				+ ", singleRelation=" + singleRelation + "]";
	}

	
}
