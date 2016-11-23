package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.DataModel;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SourceGenerationPhase;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.HspecGroupGenerationRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SourceGenerationRequest extends DataModel{

	//*************ADDITIONAL PARAMETER FIELDS NAMES 
	
	private static final Logger logger = LoggerFactory.getLogger(SourceGenerationRequest.class);
	
	//********************** Generation Parameters
	
	public static final String FIRST_HCAF_ID="FIRST_HCAF_ID";
	public static final String SECOND_HCAF_ID="SECOND_HCAF_ID";
	public static final String NUM_INTERPOLATIONS="NUM_INTERPOLATIONS";
	public static final String FIRST_HCAF_TIME="FIRST_HCAF_TIME";
	public static final String SECOND_HCAF_TIME="SECOND_HCAF_TIME";
	
	//********************** Execution Parameters
	public static final String COMBINE_MATCHING="COMBINE_MATCHING";
	public static final String FORCE_MAPS_REGENERATION="FORCE_MAPS_REGENERATION";
	public static final String GENERATE_MAPS="GENERATE_MAPS";
	public static final String GIS_ENABLED="GIS_ENABLED";
	

	//********************** Categories
	
	
	public static final ArrayList<String> generationParametersNames=new ArrayList<String>();
	public static final ArrayList<String> executionParametersNames=new ArrayList<String>();
	
	
	static{
		generationParametersNames.add(FIRST_HCAF_TIME);
		generationParametersNames.add(FIRST_HCAF_ID);
		generationParametersNames.add(NUM_INTERPOLATIONS);
		generationParametersNames.add(SECOND_HCAF_ID);
		generationParametersNames.add(SECOND_HCAF_TIME);
		
		executionParametersNames.add(COMBINE_MATCHING);
		executionParametersNames.add(FORCE_MAPS_REGENERATION);
		executionParametersNames.add(GENERATE_MAPS);
		executionParametersNames.add(GIS_ENABLED);
	}
	

	//*********************** INstance fields
	
	private String author;
	private String generationname;
	private String id;
	private String description;
	private SourceGenerationPhase phase=SourceGenerationPhase.pending;

	private Long submissiontime=0l;
	private Long endtime=0l;
	private Long starttime=0l;
	private Double currentphasepercent=0d;


	private ArrayList<Integer> hcafIds=new ArrayList<Integer>();
	private ArrayList<Integer> hspenIds=new ArrayList<Integer>();
	private ArrayList<Integer> occurrenceCellIds=new ArrayList<Integer>();
	private ArrayList<Field> generationParameters=new ArrayList<Field>();
	private ArrayList<Field> executionParameters=new ArrayList<Field>();
	

	private ArrayList<Integer> generatedSources=new ArrayList<Integer>();
	private ArrayList<Integer> reportID=new ArrayList<Integer>();
	private ArrayList<Integer> jobIds=new ArrayList<Integer>();

	private String submissionBackend;
	private String executionEnvironment;
	private String backendURL;
	private HashMap<String, String> environmentConfiguration=new HashMap<String, String>();
	private LogicType logic;
	private Integer numPartitions=0;
	private ArrayList<AlgorithmType> algorithms=new ArrayList<AlgorithmType>();
	

	private Integer toGenerateTableCount=0;
	private Integer evaluatedComputationCount=0;
	
	
	
	
	
	public ArrayList<Integer> getGeneratedSources() {
		return generatedSources;
	}
	public void setGeneratedSources(ArrayList<Integer> generatedSources) {
		this.generatedSources = generatedSources;
		Collections.sort(this.generatedSources);
	}
	
	
	
	public Long getEndtime() {
		return endtime;
	}
	public void setEndtime(Long endtime) {
		this.endtime = endtime;
	}
	public Long getStarttime() {
		return starttime;
	}
	public void setStarttime(Long starttime) {
		this.starttime = starttime;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getGenerationname() {
		return generationname;
	}
	public void setGenerationname(String generationname) {
		this.generationname = generationname;
	}
	public ArrayList<AlgorithmType> getAlgorithms() {
		return algorithms;
	}
	public void setAlgorithms(ArrayList<AlgorithmType> algorithms) {
		this.algorithms = algorithms;
		Collections.sort(this.algorithms);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public SourceGenerationPhase getPhase() {
		return phase;
	}
	public void setPhase(SourceGenerationPhase phase) {
		this.phase = phase;
	}
	public Long getSubmissiontime() {
		return submissiontime;
	}
	public void setSubmissiontime(Long submissiontime) {
		this.submissiontime = submissiontime;
	}
	public Double getCurrentphasepercent() {
		return currentphasepercent;
	}
	public void setCurrentphasepercent(Double currentphasepercent) {
		this.currentphasepercent = currentphasepercent;
	}
	
public void setEvaluatedComputationCount(Integer evaluatedComputationCount) {
	this.evaluatedComputationCount = evaluatedComputationCount;
}
public void setToGenerateTableCount(Integer toGenerateTableCount) {
	this.toGenerateTableCount = toGenerateTableCount;
}
public Integer getEvaluatedComputationCount() {
	return evaluatedComputationCount;
}
public Integer getToGenerateTableCount() {
	return toGenerateTableCount;
}

public void addReportId(Integer id){
	this.reportID.add(id);
	Collections.sort(reportID);
}
public void removeReportId(Integer id){
	reportID.remove(id);
	Collections.sort(reportID);
}

	public static ArrayList<SourceGenerationRequest> loadResultSet(ResultSet rs)throws Exception{
		ArrayList<SourceGenerationRequest> toReturn= new ArrayList<SourceGenerationRequest>();
		while(rs.next()){
			toReturn.add(new SourceGenerationRequest(Field.loadRow(rs)));
		}
		return toReturn;
	}
	public String getSubmissionBackend() {
		return submissionBackend;
	}
	public void setSubmissionBackend(String submissionBackend) {
		this.submissionBackend = submissionBackend;
	}
	public LogicType getLogic() {
		return logic;
	}
	public void setLogic(LogicType logic) {
		this.logic = logic;
	}
	public void setReportID(List<Integer> reportID) {
		this.reportID.clear();
		this.reportID.addAll(reportID);
		Collections.sort(this.reportID);
	}
	public ArrayList<Integer> getReportID() {
		return reportID;
	}
	public void setJobIds(ArrayList<Integer> jobIds) {
		this.jobIds = jobIds;
		Collections.sort(this.jobIds);
	}
	public ArrayList<Integer> getJobIds() {
		return jobIds;
	}
	public Integer getNumPartitions() {
		return numPartitions;
	}
	public void setNumPartitions(Integer numPartitions) {
		this.numPartitions = numPartitions;
	}
	public String getExecutionEnvironment() {
		return executionEnvironment;
	}
	public void setExecutionEnvironment(String executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}
	public String getBackendURL() {
		return backendURL;
	}
	public void setBackendURL(String backendURL) {
		this.backendURL = backendURL;
	}
	public HashMap<String, String> getEnvironmentConfiguration() {
		return environmentConfiguration;
	}
	public void setEnvironmentConfiguration(
			HashMap<String, String> environmentConfiguration) {
		this.environmentConfiguration = environmentConfiguration;
	}
	

	public SourceGenerationRequest(ResultSet rs)throws Exception{
		this(Field.loadRow(rs));
	}
	public SourceGenerationRequest(List<Field> row){
		for(Field f: row)	
			try{
				this.setField(f);
			}catch(Exception e){
				//skips wrong fields
			}
	}

	public boolean setField(Field f) throws JSONException{
		try{
		switch(SourceGenerationRequestFields.valueOf(f.name().toLowerCase())){
		case algorithms:{	ArrayList<AlgorithmType> parsed= new ArrayList<AlgorithmType>();
							for (String s:CSVUtils.CSVToStringList(f.value())) parsed.add(AlgorithmType.valueOf(s));
							setAlgorithms(parsed);
		break;}
		case author:setAuthor(f.value());
		break;
		case backendurl:setBackendURL(f.value());
		break;
		case currentphasepercent:setCurrentphasepercent(f.getValueAsDouble());
		break;
		case description:setDescription(f.value());
		break;
		case endtime:setEndtime(f.getValueAsLong());
		break;
		case environmentconfiguration:setEnvironmentConfiguration((HashMap<String, String>) AquaMapsXStream.getXMLInstance().fromXML(f.value()));
		break;
		case executionenvironment:setExecutionEnvironment(f.value());
		break;
		case generatedsourcesid:setGeneratedSources(CSVUtils.CSVTOIntegerList(f.value()));
		break;
		case generationname:setGenerationname(f.value());
		break;
		
		case jobids:setJobIds(CSVUtils.CSVTOIntegerList(f.value()));
		break;
		case logic:setLogic(LogicType.valueOf(f.value()));
		break;
		case numpartitions:setNumPartitions(f.getValueAsInteger());
		break;
		case phase:setPhase(SourceGenerationPhase.valueOf(f.value()));
		break;
		case reportid:setReportID(CSVUtils.CSVTOIntegerList(f.value()));
		break;
		case starttime:setStarttime(f.getValueAsLong());
		break;
		case submissionbackend:setSubmissionBackend(f.value());
		break;
		case submissiontime:setSubmissiontime(f.getValueAsLong());
		break;
		case sourcehcafids : setHcafIds(CSVUtils.CSVTOIntegerList(f.value()));
		break;
		case sourcehspenids : setHspenIds(CSVUtils.CSVTOIntegerList(f.value()));
		break;
		case sourceoccurrencecellsids : setOccurrenceCellIds(CSVUtils.CSVTOIntegerList(f.value()));
		break;
		case id: setId(f.value());
		break;
		case evaluatedcomputationcount:setEvaluatedComputationCount(f.getValueAsInteger());
		break;
		case togeneratetablescount:setToGenerateTableCount(f.getValueAsInteger());
		break;
		case generationparameters : setGenerationParameters(Field.fromJSONArray(new JSONArray(f.value())));
		break;
		case executionparameters : setExecutionParameters(Field.fromJSONArray(new JSONArray(f.value())));
		break;
		default : return false;
		}
	}catch(Exception e){logger.warn("Unable to parse field "+f.toJSONObject(),e);}
		return true;
	}

	public Field getField(SourceGenerationRequestFields fieldName) throws JSONException{
		switch(fieldName){
		case algorithms:return new Field(fieldName+"",CSVUtils.listToCSV(getAlgorithms()),FieldType.STRING);
		case author:return new Field(fieldName+"",getAuthor(),FieldType.STRING);
		case backendurl:return new Field(fieldName+"",getBackendURL(),FieldType.STRING);
		case currentphasepercent:return new Field(fieldName+"",getCurrentphasepercent()+"",FieldType.DOUBLE);
		case description:return new Field(fieldName+"",getDescription(),FieldType.STRING);
		case endtime:return new Field(fieldName+"",getEndtime()+"",FieldType.INTEGER);
		case environmentconfiguration:return new Field(fieldName+"",AquaMapsXStream.getXMLInstance().toXML(getEnvironmentConfiguration()),FieldType.STRING);
		case executionenvironment:return new Field(fieldName+"",getExecutionEnvironment(),FieldType.STRING);
		case generatedsourcesid:return new Field(fieldName+"",CSVUtils.listToCSV(generatedSources),FieldType.STRING);
		case generationname:return new Field(fieldName+"",getGenerationname(),FieldType.STRING);
		case id:return new Field(fieldName+"",getId(),FieldType.STRING);
		case jobids:return new Field(fieldName+"",CSVUtils.listToCSV(jobIds),FieldType.STRING);
		case logic:return new Field(fieldName+"",getLogic()+"",FieldType.STRING);
		case numpartitions:return new Field(fieldName+"",getNumPartitions()+"",FieldType.INTEGER);
		case phase:return new Field(fieldName+"",getPhase()+"",FieldType.STRING);
		case reportid:return new Field(fieldName+"",CSVUtils.listToCSV(reportID),FieldType.STRING);
		case starttime:return new Field(fieldName+"",getStarttime()+"",FieldType.LONG);
		case submissionbackend:return new Field(fieldName+"",getSubmissionBackend(),FieldType.STRING);
		case submissiontime:return new Field(fieldName+"",getSubmissiontime()+"",FieldType.LONG);
		case sourcehcafids:return new Field(fieldName+"",CSVUtils.listToCSV(hcafIds),FieldType.STRING);
		case sourcehspenids: return new Field(fieldName+"",CSVUtils.listToCSV(hspenIds),FieldType.STRING);
		case sourceoccurrencecellsids: return new Field(fieldName+"",CSVUtils.listToCSV(occurrenceCellIds),FieldType.STRING);
		case evaluatedcomputationcount : return new Field(fieldName+"",getEvaluatedComputationCount()+"",FieldType.INTEGER);
		case togeneratetablescount : return new Field(fieldName+"",getToGenerateTableCount()+"",FieldType.INTEGER);
		case generationparameters : return new Field(fieldName+"",Field.toJSONArray(getGenerationParameters()).toString(),FieldType.STRING);
		case executionparameters : return new Field(fieldName+"",Field.toJSONArray(getExecutionParameters()).toString(),FieldType.STRING);
		default : return null; 
		}
		
	}

	public List<Field> toRow() throws JSONException{
		List<Field> toReturn= new ArrayList<Field>();
		for(SourceGenerationRequestFields f : SourceGenerationRequestFields.values())
			toReturn.add(getField(f));
		return toReturn;
	}

	@Deprecated
	public SourceGenerationRequest() {
		// TODO Auto-generated constructor stub
	}

	public SourceGenerationRequest(HspecGroupGenerationRequestType request) throws JSONException{
		ArrayList<AlgorithmType> parsed= new ArrayList<AlgorithmType>();
		for (String s:CSVUtils.CSVToStringList(request.algorithms())) parsed.add(AlgorithmType.valueOf(s));
		setAlgorithms(parsed);
		setAuthor(request.author());
		setBackendURL(request.backendUrl());
		setDescription(request.description());
		setEnvironmentConfiguration((HashMap<String, String>) AquaMapsXStream.getXMLInstance().fromXML(request.environmentConfiguration()));
		setExecutionEnvironment(request.executionEnvironment());
		this.setGenerationname(request.generationName());
		setHcafIds(CSVUtils.CSVTOIntegerList(request.hcafIds()));
		setHspenIds(CSVUtils.CSVTOIntegerList(request.hspenIds()));
		setOccurrenceCellIds(CSVUtils.CSVTOIntegerList(request.occurrenceCellsIds()));
		setExecutionParameters(Field.fromJSONArray(new JSONArray(request.executionParameters())));
		setGenerationParameters(Field.fromJSONArray(new JSONArray(request.generationParameters())));
		this.setLogic(LogicType.valueOf(request.logic()));
		this.setNumPartitions(request.numPartitions());
		this.setSubmissionBackend(request.submissionBackend());
	}
	
	public HspecGroupGenerationRequestType toStubsVersion() throws JSONException{
		
		return new HspecGroupGenerationRequestType(
				author, 
				generationname, 
				description, 
				CSVUtils.listToCSV(hspenIds),
				CSVUtils.listToCSV(hcafIds),
				Field.toJSONArray(executionParameters).toString(), 
				Field.toJSONArray(generationParameters).toString(), 
				CSVUtils.listToCSV(occurrenceCellIds),
				submissionBackend,
				executionEnvironment, 
				backendURL,
				AquaMapsXStream.getXMLInstance().toXML(environmentConfiguration),
				logic+"", 
				numPartitions, 
				CSVUtils.listToCSV(algorithms));
	}
	
	public ArrayList<Integer> getHcafIds() {
		return hcafIds;
	}
	public ArrayList<Integer> getHspenIds() {
		return hspenIds;
	}
	public ArrayList<Integer> getOccurrenceCellIds() {
		return occurrenceCellIds;
	}
	public void setHcafIds(ArrayList<Integer> hcafIds) {
		this.hcafIds = hcafIds;
		Collections.sort(this.hcafIds);
	}
	public void setHspenIds(ArrayList<Integer> hspenIds) {
		this.hspenIds = hspenIds;
		Collections.sort(this.hspenIds);
	}
	public void setOccurrenceCellIds(ArrayList<Integer> occurrenceCellIds) {
		this.occurrenceCellIds = occurrenceCellIds;
		Collections.sort(this.occurrenceCellIds);
	}
	public ArrayList<Field> getExecutionParameters() {
		return executionParameters;
	}
	public void setExecutionParameters(ArrayList<Field> executionParameters) {
		this.executionParameters = executionParameters;
		Collections.sort(this.executionParameters);
	}
	
	public ArrayList<Field> getGenerationParameters() {
		return generationParameters;
	}
	public void setGenerationParameters(ArrayList<Field> generationParameters) {
		this.generationParameters = generationParameters;
		Collections.sort(this.generationParameters);
	}
	
	public void addSource(Resource toAdd) throws Exception{
		ArrayList<Integer> toModifyIds=null;
		switch(toAdd.getType()){
		case HCAF : 	toModifyIds=hcafIds;
		break;
		case HSPEN : 	toModifyIds=hspenIds;
		break;		
		case OCCURRENCECELLS : 	toModifyIds=occurrenceCellIds;
		break;
		default : throw new Exception("Invalid resource type "+toAdd);
		}
		if(!toModifyIds.contains(toAdd.getSearchId())){
				toModifyIds.add(toAdd.getSearchId());
				Collections.sort(toModifyIds);
		}
	}
	
	
	public void addParameter(Field f)throws Exception{
		if(generationParametersNames.contains(f.name())){ 
			generationParameters.add(f);
			Collections.sort(generationParameters);
		}else if(executionParametersNames.contains(f.name())){ 
			executionParameters.add(f);
			Collections.sort(executionParameters);
		} else throw new Exception("Invalid Field name "+f);
	}
}
