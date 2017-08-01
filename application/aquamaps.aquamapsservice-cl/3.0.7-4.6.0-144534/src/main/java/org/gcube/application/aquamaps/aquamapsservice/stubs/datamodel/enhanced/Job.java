package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.EnvelopeFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.EnvelopeWeightsArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.PerturbationArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.json.JSONException;


public class Job extends DataModel{
	
	
	private int id;
	private String name;
	private String author;
	private SubmittedStatus status=SubmittedStatus.Pending;
	private Long date=0l;
	private List<AquaMapsObject> aquaMapsObjectList=new ArrayList<AquaMapsObject>();
	
	private Boolean isGis=false;
	private String wmsContextId;
	
	
	private Resource sourceHSPEN=new Resource(ResourceType.HSPEN,1);
	private Resource sourceHCAF=new Resource(ResourceType.HCAF,1);
	private Resource sourceHSPEC=new Resource(ResourceType.HSPEC,1);
	private Set<Species> selectedSpecies=new TreeSet<Species> ();
	
	private Map<String,Map<String,Perturbation>> envelopeCustomization=new HashMap<String, Map<String,Perturbation>>();
	
	// Map<specId,Map<fieldName,Perturbation>> envelopeCustomization=new HashMap<specId, Map<fieldName,Perturbation>>();
	
	private Map<String,Map<EnvelopeFields,Field>> envelopeWeights=new HashMap<String, Map<EnvelopeFields,Field>>();
	
	private Set<Area> selectedAreas=new HashSet<Area>();
	
		
	
	/**
	 * @param weights the weights to set
	 */
	
	
	
	
	private List<File> related=new ArrayList<File>();	
	
	
	
	public Job() {
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SubmittedStatus getStatus() {
		return status;
	}
	public void setStatus(SubmittedStatus status) {
		this.status = status;
	}
	public Resource getSourceHSPEN() {
		return sourceHSPEN;
	}
	public void setSourceHSPEN(Resource sourceHSPEN) {
		this.sourceHSPEN = sourceHSPEN;
	}
	public Resource getSourceHCAF() {
		return sourceHCAF;
	}
	public void setSourceHCAF(Resource sourceHCAF) {
		this.sourceHCAF = sourceHCAF;
	}
	public Resource getSourceHSPEC() {
		return sourceHSPEC;
	}
	public void setSourceHSPEC(Resource sourceHSPEC) {
		this.sourceHSPEC = sourceHSPEC;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	
	
	
	
	public int addSpecies(Collection<Species> toAdd){
		selectedSpecies.addAll(toAdd);
		return selectedSpecies.size();
	}
	
	public int addAreas(Collection<Area> toAdd){
		selectedAreas.addAll(toAdd);
//		for(AquaMapsObject obj:aquaMapsObjectList)obj.addAreas(toAdd);
		return selectedAreas.size();
	}
	
	public List<AquaMapsObject> getAquaMapsObjectList() {
		return aquaMapsObjectList;
	}
	public void setAquaMapsObjectList(List<AquaMapsObject> aquaMapsObjectList) {
		this.aquaMapsObjectList = aquaMapsObjectList;
	}
	public Set<Species> getSelectedSpecies() {
		return selectedSpecies;
	}
	
	public Map<String, Map<String, Perturbation>> getEnvelopeCustomization() {
		return envelopeCustomization;
	}
	
	public Set<Area> getSelectedAreas() {
		return selectedAreas;
	}
	
	
	
	public List<File> getRelated() {
		return related;
	}
	public void setRelated(List<File> related) {
		this.related = related;
	}
	public int removeAreas(Collection<Area> toRemove){
		selectedAreas.removeAll(toRemove);
//		for(AquaMapsObject obj:aquaMapsObjectList)
//			obj.removeAreas(toRemove);
		return selectedAreas.size();
	}
	
	//TODO Weights
	public int removeSpecies(Collection<Species> toAdd){
		selectedSpecies.removeAll(toAdd);
		for(Species spec: toAdd) {
			envelopeWeights.remove(spec.getId());
			envelopeCustomization.remove(spec.getId());
		}
		for(AquaMapsObject obj:aquaMapsObjectList)
			if(obj.getType().equals(ObjectType.Biodiversity)) {
				int specsNumber=obj.removeSpecies(toAdd);
				if(specsNumber==0) aquaMapsObjectList.remove(obj);
				else if(specsNumber==1) {
					obj.setType(ObjectType.SpeciesDistribution);
					obj.setThreshold(0);
				}
			}
			else aquaMapsObjectList.remove(obj);
		return selectedSpecies.size();
	}
	
	public int addAquaMapsObject(Collection<AquaMapsObject> toAdd){
		aquaMapsObjectList.addAll(toAdd);
		return aquaMapsObjectList.size();
	}
	
	public int removeAquaMapsObject(Collection<AquaMapsObject> toRemove){
		aquaMapsObjectList.removeAll(toRemove);
		return aquaMapsObjectList.size();
	}
	
	public AquaMapsObject addAquaMapsObject(ObjectType type){
		AquaMapsObject toReturn=new AquaMapsObject();
		toReturn.setAuthor(this.author);
		toReturn.setType(type);
		this.aquaMapsObjectList.add(toReturn);
		return toReturn;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setEnvelopeWeights(Map<String,Map<EnvelopeFields,Field>> envelopeWeights) {
		this.envelopeWeights = envelopeWeights;
	}
	public Map<String,Map<EnvelopeFields,Field>> getEnvelopeWeights() {
		return envelopeWeights;
	}
	
	
	public Job (org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Job toLoad){
		this.setAuthor(toLoad.author());
		this.setDate(toLoad.date());
		
		if((toLoad.weights()!=null)&&(toLoad.weights().theList()!=null))
			for(org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.EnvelopeWeights weights:toLoad.weights().theList()){
				String speciesID=weights.speciesId();
				if(!envelopeWeights.containsKey(speciesID)) 
					envelopeWeights.put(speciesID, new HashMap<EnvelopeFields, Field>());
				for(Field f: weights.weights().theList())
						envelopeWeights.get(speciesID).put(EnvelopeFields.valueOf(f.name()), f);
			}
		if((toLoad.envelopeCustomization()!=null)&&(toLoad.envelopeCustomization().theList()!=null))
			for(org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Perturbation pert:toLoad.envelopeCustomization().theList()){
				String speciesID=pert.toPerturbId();
				if(!envelopeCustomization.containsKey(speciesID))
					envelopeCustomization.put(speciesID, new HashMap<String, Perturbation>());
				envelopeCustomization.get(speciesID).put(pert.field(), new Perturbation(pert));
			}

		this.setRelated(File.load(toLoad.relatedResources()));
		this.setSelectedAreas(Area.load(toLoad.selectedAreas()));
		this.setId(toLoad.id());
		this.setName(toLoad.name());
		this.setSourceHCAF(new Resource(toLoad.hcaf()));
		this.setSourceHSPEN(new Resource(toLoad.hspen()));
		this.setSourceHSPEC(new Resource(toLoad.hspec()));
		this.setStatus(SubmittedStatus.valueOf(toLoad.status()));
		
		this.setAquaMapsObjectList(AquaMapsObject.load(toLoad.aquaMapList()));
		this.getSelectedSpecies().addAll(Species.load(toLoad.selectedSpecies()));
		
		
		this.setIsGis(toLoad.gis());
		this.setWmsContextId(toLoad.groupId());
		
	}

	public org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Job toStubsVersion() throws JSONException{
		org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Job toReturn= new org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Job();
		toReturn.aquaMapList(AquaMapsObject.toStubsVersion(this.getAquaMapsObjectList()));
		toReturn.author(this.getAuthor());
		toReturn.date(date!=null?date:0);
		
		List<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Perturbation> pertList=new ArrayList<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Perturbation>();
		for(String specId: this.envelopeCustomization.keySet())
			for(String field: this.envelopeCustomization.get(specId).keySet()){
				Perturbation p=this.envelopeCustomization.get(specId).get(field);
				pertList.add(new org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Perturbation(field,specId,p.getType().toString(),p.getPerturbationValue()));
			}
		toReturn.envelopeCustomization(
				new PerturbationArray(pertList));
		
		toReturn.hcaf(this.sourceHCAF.toStubsVersion());
		toReturn.hspec(this.sourceHSPEC.toStubsVersion());
		toReturn.hspen(this.sourceHSPEN.toStubsVersion());
		toReturn.id(this.id);
		toReturn.name(this.name);
		toReturn.relatedResources(File.toStubsVersion(this.related));
		toReturn.selectedAreas(Area.toStubsVersion(this.selectedAreas));
		toReturn.selectedSpecies(Species.toStubsVersion(this.selectedSpecies));
		toReturn.status(this.status.toString());
		
		toReturn.gis(this.isGis);
		toReturn.groupId(this.getWmsContextId());
		
		List<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.EnvelopeWeights> weightList= new ArrayList<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.EnvelopeWeights>();
		for(String specId:this.envelopeWeights.keySet()){
			weightList.add(new org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.EnvelopeWeights(
					specId,new FieldArray(this.envelopeWeights.get(specId).values())));
		}
		toReturn.weights(new EnvelopeWeightsArray(weightList));
		return toReturn;
	}
	
	
	public void setSelectedSpecies(Set<Species> selectedSpecies) {
		this.selectedSpecies = selectedSpecies;
	}
	public void setEnvelopeCustomization(
			Map<String, Map<String, Perturbation>> envelopeCustomization) {
		this.envelopeCustomization = envelopeCustomization;
	}
	public void setSelectedAreas(Set<Area> selectedAreas) {
		this.selectedAreas = selectedAreas;
	}
	
	
	public void setCustomization(Species s, Field f, Perturbation p){
		Map<String, Perturbation> map=envelopeCustomization.get(s.getId());
		if(map==null)map=new HashMap<String, Perturbation>();
		map.put(f.name(), p);
		envelopeCustomization.put(s.getId(), map);
	}
	
	public void setWeights(Species s,List<Field> weights){
		Map<EnvelopeFields,Field> map=envelopeWeights.get(s.getId());
		if(map==null) map=new HashMap<EnvelopeFields, Field>();
		for(Field f:weights){
			map.put(EnvelopeFields.valueOf(f.name()), f);
		}
		envelopeWeights.put(s.getId(), map);
	}
	public Boolean getIsGis() {
		return isGis;
	}
	public void setIsGis(Boolean isGis) {
		this.isGis = isGis;
	}
	public String getWmsContextId() {
		return wmsContextId;
	}
	public void setWmsContextId(String wmsContextId) {
		this.wmsContextId = wmsContextId;
	}
	public String getCompressedCoverage(){
		return AquaMapsObject.generateMD5(selectedSpecies,"");
	}
}
