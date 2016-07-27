package org.gcube.vremanagement.vremodeler.impl.peristentobjects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class VreCollectionRelation {

	public static final String COLLECTION_ID_FIELD = "collection_id";
	public static final String VRE_ID_FIELD = "vre_id";
	
	@DatabaseField(foreign=true,canBeNull=false, columnName= VRE_ID_FIELD)
	private VRE vre;
	@DatabaseField(foreign=true,canBeNull=false, columnName= COLLECTION_ID_FIELD)
	private Collection collection;
	
	VreCollectionRelation(){}
	
	public VreCollectionRelation(VRE vre, Collection collection) {
		super();
		this.vre = vre;
		this.collection = collection;
	}
	
	public VRE getVre() {
		return vre;
	}
	public void setVre(VRE vre) {
		this.vre = vre;
	}
	public Collection getCollection() {
		return collection;
	}
	public void setGhn(Collection ghn) {
		this.collection = ghn;
	}
	
}
