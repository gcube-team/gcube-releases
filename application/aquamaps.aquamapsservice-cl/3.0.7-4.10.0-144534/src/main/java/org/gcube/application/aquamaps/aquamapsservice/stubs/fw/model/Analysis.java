package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AnalysisType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(namespace=aquamapsTypesNS)
public class Analysis {
	
	
	private static Logger logger = LoggerFactory.getLogger(Analysis.class);
	
	@XmlElement(namespace=aquamapsTypesNS)
	private String id;
	@XmlElement(namespace=aquamapsTypesNS)
	private String title;
	@XmlElement(namespace=aquamapsTypesNS)
	private String author;
	@XmlElement(namespace=aquamapsTypesNS)
	private String description;
	@XmlElement(namespace=aquamapsTypesNS)
	private String status;
	
	@XmlElement(namespace=aquamapsTypesNS)
	private Long startTime=0l;
	@XmlElement(namespace=aquamapsTypesNS)
	private Long endTime=0l;
	@XmlElement(namespace=aquamapsTypesNS)
	private Long submissionTime=0l;
	@XmlElement(namespace=aquamapsTypesNS)
	private Double currentPhasePercent=0d;
	

	@XmlElement(namespace=aquamapsTypesNS)
	private ArrayList<Integer> reportID=new ArrayList<Integer>();
	@XmlElement(namespace=aquamapsTypesNS)
	private ArrayList<AnalysisType> type=new ArrayList<AnalysisType>();
	@XmlElement(namespace=aquamapsTypesNS)
	private ArrayList<AnalysisType> performedAnalysis=new ArrayList<AnalysisType>();
	@XmlElement(namespace=aquamapsTypesNS)
	private String archiveLocation;
	@XmlElement(namespace=aquamapsTypesNS)
	private ArrayList<Integer> sources=new ArrayList<Integer>();
	
	
	
	public Analysis() {
		// TODO Auto-generated constructor stub
	}







	public String id() {
		return id;
	}



	public void id(String id) {
		this.id = id;
	}



	public String title() {
		return title;
	}



	public void title(String title) {
		this.title = title;
	}



	public String author() {
		return author;
	}



	public void author(String author) {
		this.author = author;
	}



	public String description() {
		return description;
	}



	public void description(String description) {
		this.description = description;
	}



	public String status() {
		return status;
	}



	public void status(String status) {
		this.status = status;
	}



	public Long submissiontime() {
		return submissionTime;
	}



	public void submissiontime(Long submissiontime) {
		this.submissionTime = submissiontime;
	}



	public Long endtime() {
		return endTime;
	}



	public void endtime(Long endtime) {
		this.endTime = endtime;
	}



	public Long starttime() {
		return startTime;
	}



	public void starttime(Long starttime) {
		this.startTime = starttime;
	}



	public Double currentphasepercent() {
		return currentPhasePercent;
	}



	public void currentphasepercent(Double currentphasepercent) {
		this.currentPhasePercent = currentphasepercent;
	}



	public ArrayList<Integer> reportID() {
		return reportID;
	}



	public void reportID(List<Integer> reportID) {
		this.reportID.clear();
		this.reportID.addAll(reportID);
		Collections.sort(this.reportID);
	}



	public ArrayList<AnalysisType> type() {
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
	
	public void type(ArrayList<AnalysisType> type) {
		this.type.clear();
		this.type.addAll(type);
		Collections.sort(this.type);
	}


	public void type(List<String> typeStrings){
		ArrayList<AnalysisType> types=new ArrayList<AnalysisType>();
		for(String s:typeStrings)
			types.add(AnalysisType.valueOf(s));
		type(types);
	}

	public String archiveLocation() {
		return archiveLocation;
	}



	public void archiveLocation(String archiveLocation) {
		this.archiveLocation = archiveLocation;
	}



	public ArrayList<Integer> sources() {
		return sources;
	}



	public void sources(List<Integer> sources) {
		this.sources.clear();
		this.sources.addAll(sources);		
	}
	
	
	public void performedAnalysis(ArrayList<AnalysisType> performedAnalysis) {
		this.performedAnalysis.clear();
		this.performedAnalysis.addAll(performedAnalysis);
		Collections.sort(this.performedAnalysis);
	}

	public ArrayList<AnalysisType> performedAnalysis() {
		return performedAnalysis;
	}

	public void performedAnalysis(List<String> toSet){
		ArrayList<AnalysisType> types=new ArrayList<AnalysisType>();
		for(String s:toSet)
			types.add(AnalysisType.valueOf(s));
		performedAnalysis(types);
	}
	
	public void addPerformedAnalysis(AnalysisType toAdd){
		performedAnalysis.add(toAdd);
		Collections.sort(performedAnalysis);
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
				+ ((currentPhasePercent == null) ? 0 : currentPhasePercent
						.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((reportID == null) ? 0 : reportID.hashCode());
		result = prime * result + ((sources == null) ? 0 : sources.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((submissionTime == null) ? 0 : submissionTime.hashCode());
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
		if (currentPhasePercent == null) {
			if (other.currentPhasePercent != null)
				return false;
		} else if (!currentPhasePercent.equals(other.currentPhasePercent))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
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
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (status != other.status)
			return false;
		if (submissionTime == null) {
			if (other.submissionTime != null)
				return false;
		} else if (!submissionTime.equals(other.submissionTime))
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
