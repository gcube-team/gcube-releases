package org.gcube.common.gis.datamodel.enhanced;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.TransectInfoType;


public class TransectInfo {
	private boolean enabled;
	private String table;
	private int maxelements;
	private int minimumgap;
	private ArrayList<String> fields;
	
	public TransectInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public TransectInfo(boolean enabled, String table, int maxelements,
			int minimumgap, ArrayList<String> fields) {
		super();
		this.enabled = enabled;
		this.table = table;
		this.maxelements = maxelements;
		this.minimumgap = minimumgap;
		this.fields = fields;
	}

	public TransectInfo(TransectInfoType toLoad){
		if(toLoad.fields()!=null&&toLoad.fields().items()!=null)this.setFields(new ArrayList<String>(toLoad.fields().items()));
		this.setMaxelements(toLoad.maxElements());
		this.setMinimumgap(toLoad.minimumGap());
		this.setTable(toLoad.table());
		this.setEnabled(toLoad.enabled());
	}
	
	public TransectInfoType toStubsVersion() {
		TransectInfoType res = new TransectInfoType();
		res.enabled(this.isEnabled());
		res.fields(new StringArray(this.getFields()));
		res.maxElements(this.getMaxelements());
		res.minimumGap(this.getMinimumgap());
		res.table(this.getTable());
		return res;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public int getMaxelements() {
		return maxelements;
	}

	public void setMaxelements(int maxelements) {
		this.maxelements = maxelements;
	}

	public int getMinimumgap() {
		return minimumgap;
	}

	public void setMinimumgap(int minimumgap) {
		this.minimumgap = minimumgap;
	}

	public ArrayList<String> getFields() {
		return fields;
	}

	public void setFields(ArrayList<String> fields) {
		this.fields = fields;
	}
}
