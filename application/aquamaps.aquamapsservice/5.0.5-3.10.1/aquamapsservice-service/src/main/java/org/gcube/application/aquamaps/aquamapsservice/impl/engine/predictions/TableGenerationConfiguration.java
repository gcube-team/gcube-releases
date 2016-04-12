package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables.HSPECGroupWorker;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;

public abstract class TableGenerationConfiguration {

	private LogicType logic;
	private AlgorithmType algorithm;
	private HashMap<ResourceType,List<Resource>> sources=new HashMap<ResourceType, List<Resource>>(); 
	
	private String maxMinHspenTable;
	
	private String submissionBackend;
	private String executionEnvironment;
	private String backendUrl;
	private HashMap<String,String> configuration;
	private int partitionsNumber;
	private String author;
	private ArrayList<Field> additionalParameters;
	private HSPECGroupWorker worker;
	
	public TableGenerationConfiguration(LogicType logic,
			AlgorithmType algorithm,List<Resource> sources, String submissionBackend,
			String executionEnvironment, String backendUrl,
			HashMap<String, String> configuration, int partitionsNumber,String author,ArrayList<Field> additionalParams,HSPECGroupWorker worker) throws Exception {
		super();
		this.logic = logic;
		this.algorithm = algorithm;
		for(Resource r:sources){
			if(!this.sources.containsKey(r.getType())) this.sources.put(r.getType(), new ArrayList<Resource>());
			this.sources.get(r.getType()).add(r);
		}		
		this.submissionBackend = submissionBackend;
		this.executionEnvironment = executionEnvironment;
		this.backendUrl = backendUrl;
		this.configuration = configuration;
		this.partitionsNumber = partitionsNumber;
		this.author=author;
		if(this.sources.containsKey(ResourceType.HSPEN))maxMinHspenTable=SourceManager.getMaxMinTable(this.sources.get(ResourceType.HSPEN).get(0));
		
		this.additionalParameters=additionalParams;
		this.worker=worker;
	}
	
	public ArrayList<Field> getAdditionalParameters() {
		return additionalParameters;
	}
	public LogicType getLogic() {
		return logic;
	}


	public AlgorithmType getAlgorithm() {
		return algorithm;
	}

	public HashMap<ResourceType, List<Resource>> getSources() {
		return sources;
	}
	
	public String getSubmissionBackend() {
		return submissionBackend;
	}

	public String getExecutionEnvironment() {
		return executionEnvironment;
	}

	public String getBackendUrl() {
		return backendUrl;
	}

	public HashMap<String, String> getConfiguration() {
		return configuration;
	}

	public int getPartitionsNumber() {
		return partitionsNumber;
	}

	public String getAuthor() {
		return author;
	}
	public String getMaxMinHspenTable() {
		return maxMinHspenTable;
	}

	@Override
	public String toString() {
		return "TableGenerationConfiguration [logic=" + logic + ", algorithm="
				+ algorithm + ", sources=" + sources + ", maxMinHspenTable="
				+ maxMinHspenTable + ", submissionBackend=" + submissionBackend
				+ ", executionEnvironment=" + executionEnvironment
				+ ", backendUrl=" + backendUrl + ", configuration="
				+ configuration + ", partitionsNumber=" + partitionsNumber
				+ ", author=" + author + ", additionalParameters="
				+ additionalParameters + "]";
	}
	
	public abstract void registerGeneratedSourcesCallback(List<String> toRegisterTables)throws Exception;
	public abstract void notifyError(Exception e);
	public abstract void release(BatchGeneratorI batch);
}
