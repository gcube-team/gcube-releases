package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AnalysisType;

public class AnalysisResponseDescriptor {

	private HashMap<String,ArrayList<ImageDescriptor>> categorizedImages=new HashMap<String, ArrayList<ImageDescriptor>>();
	
	private HashMap<AnalysisType,Boolean> results=new HashMap<AnalysisType, Boolean>();
	private HashMap<AnalysisType,String> messages=new HashMap<AnalysisType, String>();
	private AnalysisType type;
	
	public AnalysisResponseDescriptor(AnalysisType type) {
		this.type=type;
	}
	public HashMap<String, ArrayList<ImageDescriptor>> getCategorizedImages() {
		return categorizedImages;
	}
	public HashMap<AnalysisType, String> getMessages() {
		return messages;
	}
	public HashMap<AnalysisType, Boolean> getResults() {
		return results;
	}
	public void append(AnalysisResponseDescriptor toAppend){
		categorizedImages.putAll(toAppend.getCategorizedImages());
		results.putAll(toAppend.results);
		messages.putAll(toAppend.messages);
	}
	public AnalysisType getType() {
		return type;
	}
}
