package org.gcube.vremanagement.vremodeler.impl.peristentobjects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class VreGhnRelation {

	public static final String VRE_ID_FIELD = "vre_id";
	public static final String GHN_ID_FIELD = "ghn_id";
	
	@DatabaseField(foreign=true,canBeNull=false, columnName= VRE_ID_FIELD)
	private VRE vre;
	@DatabaseField(foreign=true,canBeNull=false, columnName= GHN_ID_FIELD)
	private Ghn ghn;
	
	VreGhnRelation(){}
	
	public VreGhnRelation(VRE vre, Ghn ghn) {
		super();
		this.vre = vre;
		this.ghn = ghn;
	}
	
	public VRE getVre() {
		return vre;
	}
	public void setVre(VRE vre) {
		this.vre = vre;
	}
	public Ghn getGhn() {
		return ghn;
	}
	public void setGhn(Ghn ghn) {
		this.ghn = ghn;
	}
	
	
	
}
