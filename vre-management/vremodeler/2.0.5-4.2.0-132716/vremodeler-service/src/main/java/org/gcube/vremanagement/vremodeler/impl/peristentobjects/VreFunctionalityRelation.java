package org.gcube.vremanagement.vremodeler.impl.peristentobjects;

import com.j256.ormlite.field.DatabaseField;

public class VreFunctionalityRelation {
	
	public static final String VRE_ID_FIELD = "vre_id";
	public static final String FUNCTIONALITY_ID_FIELD = "functionality_id";
	
	@DatabaseField(foreign=true,canBeNull=false, columnName= VRE_ID_FIELD)
	private VRE vre;
	@DatabaseField(foreign=true,canBeNull=false, columnName= FUNCTIONALITY_ID_FIELD)
	private FunctionalityPersisted functionality;
	
	VreFunctionalityRelation(){}
	
	public VreFunctionalityRelation(VRE vre, FunctionalityPersisted functionality) {
		super();
		this.vre = vre;
		this.functionality = functionality;
	}

	public VRE getVre() {
		return vre;
	}

	public void setVre(VRE vre) {
		this.vre = vre;
	}

	public FunctionalityPersisted getFunctionality() {
		return functionality;
	}

	public void setFunctionality(FunctionalityPersisted functionality) {
		this.functionality = functionality;
	}

	
	
}
