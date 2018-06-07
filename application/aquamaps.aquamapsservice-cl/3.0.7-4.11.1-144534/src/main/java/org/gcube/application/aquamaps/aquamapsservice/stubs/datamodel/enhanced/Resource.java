package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.MetaSourceFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.ResourceArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("Resource")
public class Resource extends DataModel{

	private static final Logger logger = LoggerFactory.getLogger(Resource.class);
	
	private ResourceType type=ResourceType.HCAF;
	
	private static final AlgorithmType DEFAULT_ALGORITHM_TYPE=AlgorithmType.NativeRange;
	
	
	private int searchId=1;

	private String title;
	private String tableName;
	private String description;
	private String author;
	private String disclaimer;
	private String provenance;
	private Long generationTime=0l;
	private ArrayList<Integer> sourceHCAFIds=new ArrayList<Integer>();
	private ArrayList<Integer> sourceHSPENIds=new ArrayList<Integer>();
	private ArrayList<Integer> sourceHSPECIds=new ArrayList<Integer>();
	private ArrayList<Integer> sourceOccurrenceCellsIds=new ArrayList<Integer>();
	private ArrayList<Field> parameters=new ArrayList<Field>();
	private ResourceStatus status=ResourceStatus.Completed;
	private ArrayList<String> sourceHSPECTables=new ArrayList<String>();
	private ArrayList<String> sourceHSPENTables=new ArrayList<String>();
	private ArrayList<String> sourceHCAFTables=new ArrayList<String>();
	private ArrayList<String> sourceOccurrenceCellsTables=new ArrayList<String>();
	private AlgorithmType algorithm=DEFAULT_ALGORITHM_TYPE;
	private Boolean defaultSource=false;
	private Long rowCount=0l;

	

	public Resource(ResourceType type,int searchId) {		
		this.type=type;
		this.searchId=searchId;
	}



	public Resource (org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Resource toLoad){
		try{this.setAlgorithm(AlgorithmType.valueOf(toLoad.algorithm()));}
		catch(Exception e){this.setAlgorithm(DEFAULT_ALGORITHM_TYPE);}
		this.setAuthor(toLoad.author());
		this.setGenerationTime(toLoad.generationTime());
		this.setDescription(toLoad.description());
		this.setDisclaimer(toLoad.disclaimer());
		try{
			this.parameters.addAll(Field.fromJSONArray(new JSONArray(toLoad.parameters())));			
		}catch(Exception e){
			logger.warn("Unable to parse parameters",e);
		}
		this.setProvenance(toLoad.provenance());
		this.setSearchId(toLoad.searchId());
		try{
			this.sourceHCAFIds=CSVUtils.CSVTOIntegerList(toLoad.sourceHCAFIds());
			Collections.sort(sourceHCAFIds);
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.sourceHCAFIds(),e);
		}
		try{
			this.sourceHSPENIds=CSVUtils.CSVTOIntegerList(toLoad.sourceHSPENIds());
			Collections.sort(sourceHSPENIds);
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.sourceHSPENIds(),e);
		}
		try{
			this.sourceHSPECIds=CSVUtils.CSVTOIntegerList(toLoad.sourceHSPECIds());
			Collections.sort(sourceHSPECIds);
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.sourceHSPECIds(),e);
		}
		try{
			this.sourceOccurrenceCellsIds=CSVUtils.CSVTOIntegerList(toLoad.sourceOccurrenceCellsIds());
			Collections.sort(sourceOccurrenceCellsIds);
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.sourceOccurrenceCellsIds(),e);
		}
		
		try{
			this.sourceHCAFTables=CSVUtils.CSVToStringList(toLoad.sourceHCAFTables());
			Collections.sort(sourceHCAFTables);
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.sourceHCAFTables(),e);
		}
		try{
			this.sourceHSPECTables=CSVUtils.CSVToStringList(toLoad.sourceHSPECTables());
			Collections.sort(sourceHSPECTables);
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.sourceHSPECTables(),e);
		}
		try{
			this.sourceHSPENTables=CSVUtils.CSVToStringList(toLoad.sourceHSPENTables());
			Collections.sort(sourceHSPENTables);
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.sourceHSPENTables(),e);
		}
		try{
			this.sourceOccurrenceCellsTables=CSVUtils.CSVToStringList(toLoad.sourceOccurrenceCellsTables());
			Collections.sort(sourceOccurrenceCellsTables);
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.sourceOccurrenceCellsTables(),e);
		}
		this.setStatus(ResourceStatus.valueOf(toLoad.status()));
		this.setTableName(toLoad.tableName());
		this.setTitle(toLoad.title());
		this.setType(ResourceType.valueOf(toLoad.type()));
		this.setDefaultSource(toLoad.defaultSource());		
		setRowCount(toLoad.rowCount());
	}

	public org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Resource toStubsVersion() throws JSONException{
		org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Resource toReturn=new org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Resource();
		
		toReturn.algorithm(getAlgorithm()+"");
		toReturn.author(getAuthor());
		toReturn.generationTime(generationTime!=null?generationTime:0);
		toReturn.description(getDescription());
		toReturn.disclaimer(getDisclaimer());
		toReturn.parameters(Field.toJSONArray(parameters).toString());
		toReturn.provenance(getProvenance());
		toReturn.searchId(getSearchId());
		toReturn.sourceHCAFIds(CSVUtils.listToCSV(sourceHCAFIds));
		toReturn.sourceHSPENIds(CSVUtils.listToCSV(sourceHSPENIds));
		toReturn.sourceHSPECIds(CSVUtils.listToCSV(sourceHSPECIds));
		toReturn.sourceHSPENTables(CSVUtils.listToCSV(sourceHSPENTables));
		toReturn.sourceHCAFTables(CSVUtils.listToCSV(sourceHCAFTables));
		toReturn.sourceHSPECTables(CSVUtils.listToCSV(sourceHSPECTables));
		toReturn.status(this.getStatus()+"");
		toReturn.tableName(this.getTableName());
		toReturn.title(this.getTitle());
		toReturn.type(this.getType().toString());
		toReturn.defaultSource(this.getDefaultSource());
		toReturn.sourceOccurrenceCellsIds(CSVUtils.listToCSV(sourceOccurrenceCellsIds));
		toReturn.sourceOccurrenceCellsTables(CSVUtils.listToCSV(sourceOccurrenceCellsTables));
		toReturn.rowCount(getRowCount());
		return toReturn;
	}


	public static List<Resource> load(ResourceArray toLoad){
		List<Resource> toReturn=new ArrayList<Resource>();
		if((toLoad!=null)&&(toLoad.theList()!=null))
			for(org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Resource f: toLoad.theList())toReturn.add(new Resource(f));
		return toReturn;
	}

	public static ResourceArray toStubsVersion(List<Resource> toConvert) throws JSONException{
		List<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Resource> list=new ArrayList<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Resource>();
		if(toConvert!=null)
			for(Resource obj:toConvert)
				list.add(obj.toStubsVersion());
		return new ResourceArray(list);
	}
	
	


	
	public Resource(ResultSet rs)throws Exception{
		for(Field f: Field.loadRow(rs))	
			try{
			this.setField(f);
			}catch(Exception e){
				logger.debug("Uncompliant field "+f);
			}
	}
	
	public boolean setField(Field f) throws JSONException{
		try{
		switch(MetaSourceFields.valueOf(f.name().toLowerCase())){
		case searchid: this.setSearchId(f.getValueAsInteger());
						break;
		case title: this.setTitle(f.value());
					break;
		case tablename: this.setTableName(f.value());
						break;
		case description : this.setDescription(f.value());
						break;
		case author : 	this.setAuthor(f.value());
						break;
		case disclaimer  : this.setDisclaimer(f.value());
							break;
		case provenience : this.setProvenance(f.value());
							break;
		case generationtime : this.setGenerationTime(f.getValueAsLong());
					break;
		case sourcehcafids : this.sourceHCAFIds=CSVUtils.CSVTOIntegerList(f.value());							
							break;
		case sourcehspenids: this.sourceHSPENIds=CSVUtils.CSVTOIntegerList(f.value());
							break;
		case sourcehspecids: this.sourceHSPECIds=CSVUtils.CSVTOIntegerList(f.value());
							break;
		case parameters : this.parameters=Field.fromJSONArray(new JSONArray(f.value()));
							break;
		case status : this.setStatus(ResourceStatus.valueOf(f.value()));
							break;
		case sourcehcaftables: this.sourceHCAFTables=CSVUtils.CSVToStringList(f.value());
								break;
		case sourcehspentables: this.sourceHSPENTables=CSVUtils.CSVToStringList(f.value());
								break;
		case sourcehspectables: this.sourceHSPECTables=CSVUtils.CSVToStringList(f.value());
								break;
		case type: this.setType(ResourceType.valueOf(f.value()));
							break;
		case algorithm : try{this.setAlgorithm(AlgorithmType.valueOf(f.value()));}catch(Exception e){}
						break;
		case defaultsource : this.setDefaultSource(f.getValueAsBoolean());
			break;
		case rowcount : this.setRowCount(f.getValueAsLong());
		break;
		case sourceoccurrencecellsids: this.sourceOccurrenceCellsIds=CSVUtils.CSVTOIntegerList(f.value());
		break;
		case sourceoccurrencecellstables: this.sourceOccurrenceCellsTables=CSVUtils.CSVToStringList(f.value());
		break;
		default : return false;
		
	}
		}catch(Exception e){logger.warn("Unable to parse field "+f.toJSONObject(),e);}
		return true;
	}
	
	public Field getField(MetaSourceFields fieldName) throws JSONException{
		switch(fieldName){
		case searchid: return new Field(fieldName+"",getSearchId()+"",FieldType.INTEGER);
		case title: return new Field(fieldName+"",getTitle(),FieldType.STRING);
		case tablename: return new Field(fieldName+"",getTableName(),FieldType.STRING);
		case description : return new Field(fieldName+"",getDescription(),FieldType.STRING);
		case author : 	return new Field(fieldName+"",getAuthor(),FieldType.STRING);
		case disclaimer  : return new Field(fieldName+"",getDisclaimer(),FieldType.STRING);
		case provenience : return new Field(fieldName+"",getProvenance(),FieldType.STRING);
		case generationtime : return new Field(fieldName+"",getGenerationTime()+"",FieldType.LONG);
		case sourcehcafids : return new Field(fieldName+"",CSVUtils.listToCSV(sourceHCAFIds)+"",FieldType.STRING);
		case sourcehspenids: return new Field(fieldName+"",CSVUtils.listToCSV(sourceHSPENIds)+"",FieldType.STRING);
		case sourcehspecids: return new Field(fieldName+"",CSVUtils.listToCSV(sourceHSPECIds)+"",FieldType.STRING);
		case parameters : return new Field(fieldName+"",Field.toJSONArray(parameters).toString(),FieldType.STRING);
		case status : return new Field(fieldName+"",getStatus()+"",FieldType.STRING);
		case sourcehcaftables: return new Field(fieldName+"",CSVUtils.listToCSV(sourceHCAFTables),FieldType.STRING);
		case sourcehspentables: return new Field(fieldName+"",CSVUtils.listToCSV(sourceHSPENTables),FieldType.STRING);
		case sourcehspectables: return new Field(fieldName+"",CSVUtils.listToCSV(sourceHSPECTables),FieldType.STRING);
		case type: return new Field(fieldName+"",getType()+"",FieldType.STRING);
		case algorithm : return new Field(fieldName+"",getAlgorithm()+"",FieldType.STRING);
		case defaultsource : return new Field(fieldName+"",getDefaultSource()+"",FieldType.BOOLEAN);
		case sourceoccurrencecellsids : return new Field(fieldName+"",CSVUtils.listToCSV(sourceOccurrenceCellsIds),FieldType.STRING);
		case sourceoccurrencecellstables : return new Field(fieldName+"",CSVUtils.listToCSV(sourceOccurrenceCellsTables),FieldType.STRING);
		case rowcount: return new Field(fieldName+"",getRowCount()+"",FieldType.LONG);
		default : return null;
		
	}
	}
	
	
	public List<Field> toRow() throws JSONException{
		List<Field> toReturn= new ArrayList<Field>();
		for(MetaSourceFields f : MetaSourceFields.values())
				toReturn.add(getField(f));
		return toReturn;
	}






	public ResourceType getType() {
		return type;
	}



	public void setType(ResourceType type) {
		this.type = type;
	}



	public int getSearchId() {
		return searchId;
	}



	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getTableName() {
		return tableName;
	}



	public void setTableName(String tableName) {
		this.tableName = tableName;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public String getAuthor() {
		return author;
	}



	public void setAuthor(String author) {
		this.author = author;
	}



	public String getDisclaimer() {
		return disclaimer;
	}



	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}



	public String getProvenance() {
		return provenance;
	}



	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}



	public Long getGenerationTime() {
		return generationTime;
	}



	public void setGenerationTime(Long generationTime) {
		this.generationTime = generationTime;
	}



	public ArrayList<Integer> getSourceHCAFIds() {
		return sourceHCAFIds;
	}



	public void setSourceHCAFIds(ArrayList<Integer> sourceHCAFIds) {
		this.sourceHCAFIds = sourceHCAFIds;
	}



	



	public ArrayList<Integer> getSourceHSPECIds() {
		return sourceHSPECIds;
	}



	public void setSourceHSPECIds(ArrayList<Integer> sourceHSPECIds) {
		this.sourceHSPECIds = sourceHSPECIds;
	}



	



	public ArrayList<Field> getParameters() {
		return parameters;
	}



	public void setParameters(ArrayList<Field> parameters) {
		this.parameters = parameters;
	}



	public ResourceStatus getStatus() {
		return status;
	}



	public void setStatus(ResourceStatus status) {
		this.status = status;
	}



	



	


	public AlgorithmType getAlgorithm() {
		return algorithm;
	}



	public void setAlgorithm(AlgorithmType algorithm) {
		this.algorithm = algorithm;
	}



	public Boolean getDefaultSource() {
		return defaultSource;
	}



	public void setDefaultSource(Boolean defaultSource) {
		this.defaultSource = defaultSource;
	}



	public Long getRowCount() {
		return rowCount;
	}



	public void setRowCount(Long rowCount) {
		this.rowCount = rowCount;
	}



	public static AlgorithmType getDefaultAlgorithmType() {
		return DEFAULT_ALGORITHM_TYPE;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result
				+ ((defaultSource == null) ? 0 : defaultSource.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((disclaimer == null) ? 0 : disclaimer.hashCode());
		result = prime * result
				+ ((generationTime == null) ? 0 : generationTime.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result
				+ ((provenance == null) ? 0 : provenance.hashCode());
		result = prime * result
				+ ((rowCount == null) ? 0 : rowCount.hashCode());
		result = prime * result + searchId;
		result = prime * result
				+ ((sourceHCAFIds == null) ? 0 : sourceHCAFIds.hashCode());
		result = prime
				* result
				+ ((sourceHCAFTables == null) ? 0 : sourceHCAFTables.hashCode());
		result = prime * result
				+ ((sourceHSPECIds == null) ? 0 : sourceHSPECIds.hashCode());
		result = prime
				* result
				+ ((sourceHSPECTables == null) ? 0 : sourceHSPECTables
						.hashCode());
		result = prime * result
				+ ((sourceHSPENIds == null) ? 0 : sourceHSPENIds.hashCode());
		result = prime
				* result
				+ ((sourceHSPENTables == null) ? 0 : sourceHSPENTables
						.hashCode());
		result = prime
				* result
				+ ((sourceOccurrenceCellsIds == null) ? 0
						: sourceOccurrenceCellsIds.hashCode());
		result = prime
				* result
				+ ((sourceOccurrenceCellsTables == null) ? 0
						: sourceOccurrenceCellsTables.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (algorithm != other.algorithm)
			return false;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (defaultSource == null) {
			if (other.defaultSource != null)
				return false;
		} else if (!defaultSource.equals(other.defaultSource))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (disclaimer == null) {
			if (other.disclaimer != null)
				return false;
		} else if (!disclaimer.equals(other.disclaimer))
			return false;
		if (generationTime == null) {
			if (other.generationTime != null)
				return false;
		} else if (!generationTime.equals(other.generationTime))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (provenance == null) {
			if (other.provenance != null)
				return false;
		} else if (!provenance.equals(other.provenance))
			return false;
		if (rowCount == null) {
			if (other.rowCount != null)
				return false;
		} else if (!rowCount.equals(other.rowCount))
			return false;
		if (searchId != other.searchId)
			return false;
		if (sourceHCAFIds == null) {
			if (other.sourceHCAFIds != null)
				return false;
		} else if (!sourceHCAFIds.equals(other.sourceHCAFIds))
			return false;
		if (sourceHCAFTables == null) {
			if (other.sourceHCAFTables != null)
				return false;
		} else if (!sourceHCAFTables.equals(other.sourceHCAFTables))
			return false;
		if (sourceHSPECIds == null) {
			if (other.sourceHSPECIds != null)
				return false;
		} else if (!sourceHSPECIds.equals(other.sourceHSPECIds))
			return false;
		if (sourceHSPECTables == null) {
			if (other.sourceHSPECTables != null)
				return false;
		} else if (!sourceHSPECTables.equals(other.sourceHSPECTables))
			return false;
		if (sourceHSPENIds == null) {
			if (other.sourceHSPENIds != null)
				return false;
		} else if (!sourceHSPENIds.equals(other.sourceHSPENIds))
			return false;
		if (sourceHSPENTables == null) {
			if (other.sourceHSPENTables != null)
				return false;
		} else if (!sourceHSPENTables.equals(other.sourceHSPENTables))
			return false;
		if (sourceOccurrenceCellsIds == null) {
			if (other.sourceOccurrenceCellsIds != null)
				return false;
		} else if (!sourceOccurrenceCellsIds
				.equals(other.sourceOccurrenceCellsIds))
			return false;
		if (sourceOccurrenceCellsTables == null) {
			if (other.sourceOccurrenceCellsTables != null)
				return false;
		} else if (!sourceOccurrenceCellsTables
				.equals(other.sourceOccurrenceCellsTables))
			return false;
		if (status != other.status)
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type != other.type)
			return false;
		return true;
	}



	public ArrayList<Integer> getSourceHSPENIds() {
		return sourceHSPENIds;
	}



	public void setSourceHSPENIds(ArrayList<Integer> sourceHSPENIds) {
		this.sourceHSPENIds = sourceHSPENIds;
		Collections.sort(this.sourceHSPENIds);
	}



	public ArrayList<Integer> getSourceOccurrenceCellsIds() {
		return sourceOccurrenceCellsIds;
		
	}



	public void setSourceOccurrenceCellsIds(
			ArrayList<Integer> sourceOccurrenceCellsIds) {
		this.sourceOccurrenceCellsIds = sourceOccurrenceCellsIds;
		Collections.sort(this.sourceOccurrenceCellsIds);
	}



	public ArrayList<String> getSourceHSPECTables() {
		return sourceHSPECTables;
	}



	public void setSourceHSPECTables(ArrayList<String> sourceHSPECTables) {
		this.sourceHSPECTables = sourceHSPECTables;
		Collections.sort(this.sourceHSPECTables);
	}



	public ArrayList<String> getSourceHSPENTables() {
		return sourceHSPENTables;
	}



	public void setSourceHSPENTables(ArrayList<String> sourceHSPENTables) {
		this.sourceHSPENTables = sourceHSPENTables;
		Collections.sort(this.sourceHSPENTables);
	}



	public ArrayList<String> getSourceHCAFTables() {
		return sourceHCAFTables;
	}



	public void setSourceHCAFTables(ArrayList<String> sourceHCAFTables) {
		this.sourceHCAFTables = sourceHCAFTables;
		Collections.sort(this.sourceHCAFTables);
	}



	public ArrayList<String> getSourceOccurrenceCellsTables() {
		return sourceOccurrenceCellsTables;
	}



	public void setSourceOccurrenceCellsTables(
			ArrayList<String> sourceOccurrenceCellsTables) {
		this.sourceOccurrenceCellsTables = sourceOccurrenceCellsTables;
		Collections.sort(this.sourceOccurrenceCellsTables);
	}
	
	
	public void addSource(Resource toAdd){
		ArrayList<String> toModifyNames=null;
		ArrayList<Integer> toModifyIds=null;
		switch(toAdd.getType()){
		case HCAF : 	toModifyNames=sourceHCAFTables;
						toModifyIds=sourceHCAFIds;
		break;
		case HSPEN : 	toModifyNames=sourceHSPENTables;
						toModifyIds=sourceHSPENIds;
		break;
		case HSPEC : 	toModifyNames=sourceHSPECTables;
						toModifyIds=sourceHSPECIds;
		break;
		case OCCURRENCECELLS : 	toModifyNames=sourceOccurrenceCellsTables;
								toModifyIds=sourceOccurrenceCellsIds;
		break;
		}
		if(!toModifyIds.contains(toAdd.getSearchId())){
				toModifyIds.add(toAdd.getSearchId());
				Collections.sort(toModifyIds);
				toModifyNames.add(toAdd.getTableName());
				Collections.sort(toModifyNames);
		}
	}

	public void removeSource(Resource toRemove){
		removeSourceId(toRemove.getSearchId());
		removeSourceTableName(toRemove.getTableName());
	}
	
	
	public void removeSourceId(Integer id){
		if(sourceHCAFIds.contains(id)){
			sourceHCAFIds.remove(id);
			Collections.sort(sourceHCAFIds);
		}else if(sourceHSPECIds.contains(id)){
			sourceHSPECIds.remove(id);
			Collections.sort(sourceHSPECIds);
		} if(sourceHSPENIds.contains(id)){
			sourceHSPENIds.remove(id);
			Collections.sort(sourceHSPENIds);
		} if(sourceOccurrenceCellsIds.contains(id)){
			sourceOccurrenceCellsIds.remove(id);
			Collections.sort(sourceOccurrenceCellsIds);
		}
	}

	public void removeSourceTableName(String tableName){
		if(sourceHCAFTables.contains(tableName)){
			sourceHCAFTables.remove(tableName);
			Collections.sort(sourceHCAFTables);
		}else if(sourceHSPECTables.contains(tableName)){
			sourceHSPECTables.remove(tableName);
			Collections.sort(sourceHSPECTables);
		} if(sourceHSPENTables.contains(tableName)){
			sourceHSPENTables.remove(tableName);
			Collections.sort(sourceHSPENTables);
		} if(sourceOccurrenceCellsTables.contains(tableName)){
			sourceOccurrenceCellsTables.remove(tableName);
			Collections.sort(sourceOccurrenceCellsTables);
		}
	}
	

	@Override
	public String toString() {
		return "Resource [type=" + type + ", searchId=" + searchId + ", title="
				+ title + ", tableName=" + tableName + "]";
	}
	
	
}
