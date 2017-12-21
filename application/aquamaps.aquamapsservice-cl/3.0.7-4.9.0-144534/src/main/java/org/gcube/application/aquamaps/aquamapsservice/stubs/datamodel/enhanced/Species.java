package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.EnvelopeFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HspenFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.SpeciesArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("Species")
public class Species extends DataModel implements Comparable<Species>{

	private String id;
	@XStreamImplicit
	private List<Field> attributesList=new ArrayList<Field>();
	
	public List<Field> getAttributesList() {
		if(attributesList==null)attributesList=new ArrayList<Field>();
		return attributesList;
	}
	public void setAttributesList(List<Field> attributesList) {
		this.attributesList = attributesList;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}

	public Field getFieldbyName(String fieldName){
		for(Field field:getAttributesList()){
			if(field.name().equals(fieldName)) return field;
		}
		return new Field(fieldName,Field.VOID);	
	}

	public void addField(Field toAddField){
		getAttributesList().add(toAddField);
	}

	public Envelope extractEnvelope(){
		Envelope toReturn=new Envelope();
		for(EnvelopeFields envelopeField:EnvelopeFields.values()){
			for(HspenFields paramName:toReturn.getValueNames(envelopeField)){
				toReturn.setValue(envelopeField, paramName,Double.parseDouble(this.getFieldbyName(paramName.toString()).value()));
			}
		}
		Double e=getFieldbyName(HspenFields.emostlong+"").getValueAsDouble();
		if(e!=null)toReturn.getBoundingBox().setE(e);
		Double n=getFieldbyName(HspenFields.nmostlat+"").getValueAsDouble();
		if(n!=null)toReturn.getBoundingBox().setN(n);
		Double w=getFieldbyName(HspenFields.wmostlong+"").getValueAsDouble();
		if(w!=null)toReturn.getBoundingBox().setW(w);
		Double s=getFieldbyName(HspenFields.smostlat+"").getValueAsDouble();
		if(s!=null)toReturn.getBoundingBox().setS(s);
		
		
		toReturn.setFaoAreas(this.getFieldbyName(HspenFields.faoareas+"").value());
		toReturn.setPelagic(Boolean.parseBoolean(this.getFieldbyName(HspenFields.pelagic+"").value()));
		toReturn.setUseBottomSeaTempAndSalinity(this.getFieldbyName(HspenFields.layer+"").value().equalsIgnoreCase("b"));
		return toReturn;
	}


	public JSONObject toJSONObject() throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(SpeciesOccursumFields.speciesid+"", id);
		JSONArray array=new JSONArray();
		for(Field f:getAttributesList())
			array.put(f.toJSONObject());
		obj.put("Fields", array);
		return obj;
	}
	public Species (JSONObject obj) throws JSONException{
		this(obj.getString(SpeciesOccursumFields.speciesid+""));
		JSONArray array=obj.getJSONArray("Fields");
		for(int i=0;i<array.length();i++)
			this.addField(new Field(array.getJSONObject(i)));
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Species))
			return false;
		Species other = (Species) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	public Species(org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Species toLoad){
		super();
		this.setId(toLoad.id());
		this.getAttributesList().addAll(toLoad.additionalField().theList());
	}

	public static List<Species> load(SpeciesArray toLoad){
		ArrayList<Species> toReturn = new ArrayList<Species>();
		if((toLoad!=null)&&(toLoad.theList()!=null))
			for(org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Species s:toLoad.theList())
				toReturn.add(new Species(s));
		return toReturn;
	}

	public static SpeciesArray toStubsVersion(Set<Species> toConvert){
		List<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Species> list=new ArrayList<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Species>();
		if(toConvert!=null)
			for(Species obj:toConvert)
				list.add(obj.toStubsVersion());
		return new SpeciesArray(list);
	}

	public org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Species toStubsVersion(){
		org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Species toReturn=new org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Species();
		toReturn.additionalField(new FieldArray(this.getAttributesList()));
		toReturn.id(this.id);
		return toReturn;
	}

	public Species(String speciesId){
		this.id=speciesId;
	}
	
	@Override
	public int compareTo(Species arg0) {
		if(arg0==null) throw new NullPointerException("Cannot compare a null Species");
		if(id==null||arg0.getId()==null) throw new NullPointerException("Either current or compared Species has null Id");
		else return this.id.compareTo(arg0.getId());
	}
	
	public String getScientificName(){
		return getFieldbyName(SpeciesOccursumFields.genus+"").value()+"_"+getFieldbyName(SpeciesOccursumFields.species+"").value();		
	}
	
}
