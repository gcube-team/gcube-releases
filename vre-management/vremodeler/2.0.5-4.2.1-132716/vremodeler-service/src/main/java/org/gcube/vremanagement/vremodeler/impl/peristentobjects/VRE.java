package org.gcube.vremanagement.vremodeler.impl.peristentobjects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable
public class VRE {

	VRE(){}
	
	
	public VRE(String id, String name, String description, String vreDesigner,
			String vreManager, Calendar intervalFrom, Calendar intervalTo,
			String status) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.vreDesigner = vreDesigner;
		this.vreManager = vreManager;
		this.intervalFrom = intervalFrom.getTimeInMillis();
		this.intervalTo = intervalTo.getTimeInMillis();
		this.status = status;
	}



	@DatabaseField(width = 60,id = true)
	private String id;
	
	@DatabaseField(canBeNull=false)
	private String name;
	
	@DatabaseField(canBeNull=false)
	private String description;
	
	@DatabaseField(canBeNull=false)
	private String vreDesigner;
	
	@DatabaseField(canBeNull=false)
	private String vreManager;
	
	@DatabaseField(canBeNull=false)
	private long intervalFrom;
	
	@DatabaseField(canBeNull=false)
	private long intervalTo;
	
	@DatabaseField(canBeNull=false)
	private String status;
	
	@ForeignCollectionField(eager = false)
	private ForeignCollection<VreGhnRelation> ghns;

	@ForeignCollectionField(eager = false)
	private ForeignCollection<VreCollectionRelation> collections;
	
	@ForeignCollectionField(eager = false)
	private ForeignCollection<VreFunctionalityRelation> functionalityRelation;
	
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private HashMap<String, ArrayList<String>> selectableResourcesMap= new HashMap<String, ArrayList<String>>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVreDesigner() {
		return vreDesigner;
	}

	public void setVreDesigner(String vreDesigner) {
		this.vreDesigner = vreDesigner;
	}

	public String getVreManager() {
		return vreManager;
	}

	public void setVreManager(String vreManager) {
		this.vreManager = vreManager;
	}

	public Calendar getIntervalFrom() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.intervalFrom);
		return calendar;
	}

	public void setIntervalFrom(Calendar intervalFrom) {
		this.intervalFrom = intervalFrom.getTimeInMillis();
	}

	public Calendar getIntervalTo() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.intervalTo);
		return calendar;
	}

	public void setIntervalTo(Calendar intervalTo) {
		this.intervalTo = intervalTo.getTimeInMillis();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ForeignCollection<VreGhnRelation> getGhns() {
		return ghns;
	}

	public void setGhns(ForeignCollection<VreGhnRelation> ghns) {
		this.ghns = ghns;
	}

	public ForeignCollection<VreCollectionRelation> getCollections() {
		return collections;
	}

	public void setCollections(ForeignCollection<VreCollectionRelation> collections) {
		this.collections = collections;
	}


	public ForeignCollection<VreFunctionalityRelation> getFunctionalityRelation() {
		return functionalityRelation;
	}


	public void setFunctionalityRelation(
			ForeignCollection<VreFunctionalityRelation> functionalityRelation) {
		this.functionalityRelation = functionalityRelation;
	}


	public HashMap<String, ArrayList<String>> getSelectableResourcesMap() {
		return selectableResourcesMap;
	}


	public void setSelectableResourcesMap(
			HashMap<String, ArrayList<String>> selectableResourcesMap) {
		this.selectableResourcesMap = selectableResourcesMap;
	}
	
}
