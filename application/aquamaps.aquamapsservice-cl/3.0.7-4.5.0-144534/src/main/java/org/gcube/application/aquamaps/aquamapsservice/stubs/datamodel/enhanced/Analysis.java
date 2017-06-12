package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.AnalysisFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AnalysisType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Analysis extends DataModel{

	private static final Logger logger = LoggerFactory.getLogger(Analysis.class);
	
	
	private String id;
	private String title;
	private String author;
	private String description;
	private SubmittedStatus status=SubmittedStatus.Pending;
	
	private Long submissiontime=0l;
	private Long endtime=0l;
	private Long starttime=0l;
	private Double currentphasepercent=0d;
	
	
	
	private ArrayList<Integer> reportID=new ArrayList<Integer>();
	
	private ArrayList<AnalysisType> type=new ArrayList<AnalysisType>();
	
	private String archiveLocation;
	private ArrayList<Integer> sources=new ArrayList<Integer>();
	
	
	private ArrayList<AnalysisType> performedAnalysis=new ArrayList<AnalysisType>();
	
	public Analysis() {
		// TODO Auto-generated constructor stub
	}


	
	public static ArrayList<Analysis> loadResultSet(ResultSet rs)throws Exception{
		ArrayList<Analysis> toReturn= new ArrayList<Analysis>();
		while(rs.next()){
			toReturn.add(new Analysis(Field.loadRow(rs)));
		}
		return toReturn;
	}
	public Analysis(ResultSet rs)throws Exception{
		this(Field.loadRow(rs));
	}
	public Analysis(List<Field> row){
		for(Field f: row)	
			try{
				this.setField(f);
			}catch(Exception e){
				//skips wrong fields
			}
	}
	
	public boolean setField(Field f) throws JSONException{
		try{
		switch(AnalysisFields.valueOf(f.name().toLowerCase())){
		case author:setAuthor(f.value());
		break;
		case currentphasepercent:setCurrentphasepercent(f.getValueAsDouble());
		break;
		case description:setDescription(f.value());
		break;
		case endtime:setEndtime(f.getValueAsLong());
		break;

		case reportid:setReportID(CSVUtils.CSVTOIntegerList(f.value()));
		break;
		case starttime:setStarttime(f.getValueAsLong());
		break;

		case submissiontime:setSubmissiontime(f.getValueAsLong());
		break;
		case id: setId(f.value());
		break;
		case archivelocation : setArchiveLocation(f.value());
		break;
		case sources : setSources(CSVUtils.CSVTOIntegerList(f.value()));
		break;
		case status : setStatus(SubmittedStatus.valueOf(f.value()));
		break;
		case title : setTitle(f.value());
		break;
		case type : setType(CSVUtils.CSVToStringList((f.value())));
		break;
		case performedanalysis : setPerformedAnalysis(CSVUtils.CSVToStringList((f.value())));
		break;
		default : return false;
		}
	}catch(Exception e){logger.warn("Unable to parse field "+f.toJSONObject(),e);}
		return true;
	}

	public Field getField(AnalysisFields fieldName) throws JSONException{
		switch(fieldName){
		case author:return new Field(fieldName+"",getAuthor(),FieldType.STRING);
		case currentphasepercent:return new Field(fieldName+"",getCurrentphasepercent()+"",FieldType.DOUBLE);
		case description:return new Field(fieldName+"",getDescription(),FieldType.STRING);
		case id:return new Field(fieldName+"",getId(),FieldType.STRING);
		case reportid:return new Field(fieldName+"",CSVUtils.listToCSV(reportID),FieldType.STRING);
		case starttime:return new Field(fieldName+"",getStarttime()+"",FieldType.LONG);
		case submissiontime:return new Field(fieldName+"",getSubmissiontime()+"",FieldType.LONG);
		case archivelocation : return new Field(fieldName+"",getArchiveLocation(),FieldType.STRING);
		case endtime : return new Field(fieldName+"",getEndtime()+"",FieldType.INTEGER);
		case sources : return new Field(fieldName+"",CSVUtils.listToCSV(sources),FieldType.STRING);
		case status : return new Field(fieldName+"",getStatus()+"",FieldType.STRING);
		case title : return new Field(fieldName+"",getTitle(),FieldType.STRING);
		case type : return new Field(fieldName+"",CSVUtils.listToCSV(getType()),FieldType.STRING);
		case performedanalysis: return new Field(fieldName+"",CSVUtils.listToCSV(getPerformedAnalysis()),FieldType.STRING);
		default : return null;
		}
		
	}

	public List<Field> toRow() throws JSONException{
		List<Field> toReturn= new ArrayList<Field>();
		for(AnalysisFields f : AnalysisFields.values())
			toReturn.add(getField(f));
		return toReturn;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getAuthor() {
		return author;
	}



	public void setAuthor(String author) {
		this.author = author;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public SubmittedStatus getStatus() {
		return status;
	}



	public void setStatus(SubmittedStatus status) {
		this.status = status;
	}



	public Long getSubmissiontime() {
		return submissiontime;
	}



	public void setSubmissiontime(Long submissiontime) {
		this.submissiontime = submissiontime;
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



	public Double getCurrentphasepercent() {
		return currentphasepercent;
	}



	public void setCurrentphasepercent(Double currentphasepercent) {
		this.currentphasepercent = currentphasepercent;
	}



	public ArrayList<Integer> getReportID() {
		return reportID;
	}



	public void setReportID(List<Integer> reportID) {
		this.reportID.clear();
		this.reportID.addAll(reportID);
		Collections.sort(this.reportID);
	}



	public ArrayList<AnalysisType> getType() {
		return type;
	}



	public void addReportId(Integer id){
		this.reportID.add(id);
		Collections.sort(reportID);
	}
	public void removeReportId(Integer id){
		reportID.remove(id);
		Collections.sort(reportID);
	}
	
	public void setType(ArrayList<AnalysisType> type) {
		this.type.clear();
		this.type.addAll(type);
		Collections.sort(this.type);
	}


	public void setType(List<String> typeStrings){
		ArrayList<AnalysisType> types=new ArrayList<AnalysisType>();
		for(String s:typeStrings)
			types.add(AnalysisType.valueOf(s));
		setType(types);
	}

	public String getArchiveLocation() {
		return archiveLocation;
	}



	public void setArchiveLocation(String archiveLocation) {
		this.archiveLocation = archiveLocation;
	}



	public ArrayList<Integer> getSources() {
		return sources;
	}



	public void setSources(List<Integer> sources) {
		this.sources.clear();
		this.sources.addAll(sources);		
	}
	
	
	public void setPerformedAnalysis(ArrayList<AnalysisType> performedAnalysis) {
		this.performedAnalysis.clear();
		this.performedAnalysis.addAll(performedAnalysis);
		Collections.sort(this.performedAnalysis);
	}

	public ArrayList<AnalysisType> getPerformedAnalysis() {
		return performedAnalysis;
	}

	public void setPerformedAnalysis(List<String> toSet){
		ArrayList<AnalysisType> types=new ArrayList<AnalysisType>();
		for(String s:toSet)
			types.add(AnalysisType.valueOf(s));
		setPerformedAnalysis(types);
	}
	
	public void addPerformedAnalysis(AnalysisType toAdd){
		performedAnalysis.add(toAdd);
		Collections.sort(performedAnalysis);
	}
	
	
	public org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Analysis toStubsVersion(){
		org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Analysis toReturn=new org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Analysis();
		toReturn.archiveLocation(archiveLocation);
		toReturn.author(author);
		toReturn.currentphasepercent(currentphasepercent);
		toReturn.description(description);
		toReturn.endtime(endtime);
		toReturn.id(id);
		toReturn.reportID(reportID);
		toReturn.sources(sources);
		toReturn.starttime(starttime);
		toReturn.status(status+"");
		toReturn.submissiontime(submissiontime);
		toReturn.title(title);
		toReturn.type(type);
		toReturn.performedAnalysis(performedAnalysis);
		return toReturn;		
	}
	
	public Analysis(org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Analysis stubs){
		setArchiveLocation(stubs.archiveLocation());
		setAuthor(stubs.author());
		setCurrentphasepercent(stubs.currentphasepercent());
		setDescription(stubs.description());
		setEndtime(stubs.endtime());
		setId(stubs.id());
		setReportID(stubs.reportID());
		setSources(stubs.sources());
		setStarttime(stubs.starttime());
		setStatus(SubmittedStatus.valueOf(stubs.status()));
		setSubmissiontime(stubs.submissiontime());
		setTitle(stubs.title());		
		setType(stubs.type());
		setPerformedAnalysis(stubs.performedAnalysis());
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((archiveLocation == null) ? 0 : archiveLocation.hashCode());
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime
				* result
				+ ((currentphasepercent == null) ? 0 : currentphasepercent
						.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((endtime == null) ? 0 : endtime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((reportID == null) ? 0 : reportID.hashCode());
		result = prime * result + ((sources == null) ? 0 : sources.hashCode());
		result = prime * result
				+ ((starttime == null) ? 0 : starttime.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((submissiontime == null) ? 0 : submissiontime.hashCode());
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
		Analysis other = (Analysis) obj;
		if (archiveLocation == null) {
			if (other.archiveLocation != null)
				return false;
		} else if (!archiveLocation.equals(other.archiveLocation))
			return false;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (currentphasepercent == null) {
			if (other.currentphasepercent != null)
				return false;
		} else if (!currentphasepercent.equals(other.currentphasepercent))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (endtime == null) {
			if (other.endtime != null)
				return false;
		} else if (!endtime.equals(other.endtime))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (reportID == null) {
			if (other.reportID != null)
				return false;
		} else if (!reportID.equals(other.reportID))
			return false;
		if (sources == null) {
			if (other.sources != null)
				return false;
		} else if (!sources.equals(other.sources))
			return false;
		if (starttime == null) {
			if (other.starttime != null)
				return false;
		} else if (!starttime.equals(other.starttime))
			return false;
		if (status != other.status)
			return false;
		if (submissiontime == null) {
			if (other.submissiontime != null)
				return false;
		} else if (!submissiontime.equals(other.submissiontime))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}



	


	
	
}
