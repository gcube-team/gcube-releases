package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AnalysisType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;

public class AnalysisRequest {

	private AnalysisType toPerformAnalysis=null;
	
	private String[] hcafTables=null;
	private String[] hcafLabels=null;
	
	private String[] hspecTables=null;
	private String[] hspecLabels=null;
	
	private String[] hspenTables=null;
	private String[] hspenLabels=null;
	
	private String[] occurrenceTables=null;
	private String[] occurrenceLabels=null;
	
	private float hspecThreshold=0.8f; 
	
	private AnalysisWorker toNotify;
	public static final List<AnalysisRequest> getRequests(Analysis toPerform, AnalysisWorker toNotify)throws Exception{
		ArrayList<AnalysisRequest> toReturn=new ArrayList<AnalysisRequest>();
		List<Resource> sources=new ArrayList<Resource>();
		for(Integer id:toPerform.getSources())
			sources.add(SourceManager.getById(id));
		
		HashMap<ResourceType,ArrayList<String>> sourceTables=new HashMap<ResourceType, ArrayList<String>>();
		HashMap<ResourceType,ArrayList<String>> sourceLabels=new HashMap<ResourceType, ArrayList<String>>();
		for(Resource r:sources){
			if(!sourceTables.containsKey(r.getType())){
				sourceTables.put(r.getType(), new ArrayList<String>());
				sourceLabels.put(r.getType(), new ArrayList<String>());
			}
			sourceTables.get(r.getType()).add(r.getTableName());
			sourceLabels.get(r.getType()).add(r.getTitle());
		}	
		
		
		String[] hcafTables=null;
		String[] hcafLabels=null;
		
		String[] hspecTables=null;
		String[] hspecLabels=null;
		
		String[] hspenTables=null;
		String[] hspenLabels=null;
		
		String[] occurrenceTables=null;
		String[] occurrenceLabels=null;
		
		
		
		
		
		for(ResourceType type:ResourceType.values()){
			if((sourceTables.containsKey(type)!=sourceLabels.containsKey(type))) // tables
				throw new Exception("Incoherent labels/tables for "+type+", TABLES : "+sourceTables.keySet()+", LABELS : "+sourceLabels.keySet());
			else if(sourceTables.containsKey(type)){
				if(sourceTables.get(type).size()!=sourceLabels.get(type).size()) 
					throw new Exception("Incoherent labels/tables for "+type+", TABLES : "+sourceTables.get(type)+", LABELS : "+sourceLabels.get(type));
				if(sourceTables.get(type).size()<2) throw new Exception("Not enough sources for "+type);
				
				switch(type){
				case HCAF : 			hcafTables=listToString(sourceTables.get(type));
										hcafLabels=listToString(sourceLabels.get(type));
										break;
				case HSPEN : 			hspenTables=listToString(sourceTables.get(type));
										hspenLabels=listToString(sourceLabels.get(type));
										break;
				case HSPEC : 			hspecTables=listToString(sourceTables.get(type));
										hspecLabels=listToString(sourceLabels.get(type));
										break;
				case OCCURRENCECELLS : 	occurrenceTables=listToString(sourceTables.get(type));
										occurrenceLabels=listToString(sourceLabels.get(type));
										break;
				}
			}
		}
		
		for(AnalysisType type:toPerform.getType())
			toReturn.add(
					new AnalysisRequest(type, hcafTables, hcafLabels, hspecTables, hspecLabels, 
							hspenTables, hspenLabels, occurrenceTables, occurrenceLabels, 0.8f,toNotify));
		return toReturn;
	}
	
	private static final String[] listToString(List<String> toCopy){
		String[] toReturn=new String[toCopy.size()];		
		for(int i=0;i<toCopy.size();i++){
			toReturn[i]=toCopy.get(i);
		}
		return toReturn;
	}
	
	
	private AnalysisRequest(AnalysisType toPerformAnalysis, String[] hcafTables,
			String[] hcafLabels, String[] hspecTables, String[] hspecLabels,
			String[] hspenTables, String[] hspenLabels,
			String[] occurrenceTables, String[] occurrenceLabels,
			float hspecThreshold, AnalysisWorker toNotify) {
		super();
		this.toPerformAnalysis = toPerformAnalysis;
		this.hcafTables = hcafTables;
		this.hcafLabels = hcafLabels;
		this.hspecTables = hspecTables;
		this.hspecLabels = hspecLabels;
		this.hspenTables = hspenTables;
		this.hspenLabels = hspenLabels;
		this.occurrenceTables = occurrenceTables;
		this.occurrenceLabels = occurrenceLabels;
		this.hspecThreshold = hspecThreshold;
		this.toNotify=toNotify;
	}



	public String[] getLabels(ResourceType type){
		switch(type){
		case HCAF:return hcafLabels;
		case HSPEC:return hspecLabels;
		case HSPEN:return hspenLabels;
		default : return occurrenceLabels;
		}
	}
	public String[] getTables(ResourceType type){
		switch(type){
		case HCAF:return hcafTables;
		case HSPEC:return hspecTables;
		case HSPEN:return hspenTables;
		default : return occurrenceTables;
		}
	}
	public AnalysisType getToPerformAnalysis() {
		return toPerformAnalysis;
	}
	public float getHspecThreshold() {
		return hspecThreshold;
	}
	public void notify(AnalysisResponseDescriptor descriptor,Analyzer analyzer){
		toNotify.notifyGenerated(descriptor, analyzer);
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AnalysisRequest [toPerformAnalysis=");
		builder.append(toPerformAnalysis);
		builder.append(", hcafTables=");
		builder.append(Arrays.toString(hcafTables));
		builder.append(", hcafLabels=");
		builder.append(Arrays.toString(hcafLabels));
		builder.append(", hspecTables=");
		builder.append(Arrays.toString(hspecTables));
		builder.append(", hspecLabels=");
		builder.append(Arrays.toString(hspecLabels));
		builder.append(", hspenTables=");
		builder.append(Arrays.toString(hspenTables));
		builder.append(", hspenLabels=");
		builder.append(Arrays.toString(hspenLabels));
		builder.append(", occurrenceTables=");
		builder.append(Arrays.toString(occurrenceTables));
		builder.append(", occurrenceLabels=");
		builder.append(Arrays.toString(occurrenceLabels));
		builder.append(", hspecThreshold=");
		builder.append(hspecThreshold);
		builder.append(", toNotify=");
		builder.append(toNotify);
		builder.append("]");
		return builder.toString();
	}
	
	
}
