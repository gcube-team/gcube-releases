package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=aquamapsTypesNS)
public class Submitted {
	
	@XmlElement(namespace=aquamapsTypesNS)
	private String author;
	@XmlElement(namespace=aquamapsTypesNS)
	private long startTime;
	@XmlElement(namespace=aquamapsTypesNS)
	private long endTime;
	@XmlElement(namespace=aquamapsTypesNS)
	private long submissionTime;
	@XmlElement(namespace=aquamapsTypesNS)
	private boolean gisEnabled;
	@XmlElement(namespace=aquamapsTypesNS)
	private String publishedIds;
	@XmlElement(namespace=aquamapsTypesNS)
	private String gisReferences;
	@XmlElement(namespace=aquamapsTypesNS)
	private boolean isAquaMap;
	@XmlElement(namespace=aquamapsTypesNS)
	private int jobId;
	@XmlElement(namespace=aquamapsTypesNS)
	private boolean saved;
	@XmlElement(namespace=aquamapsTypesNS)
	private int searchid;
	@XmlElement(namespace=aquamapsTypesNS)
	private String selectionCriteria;
	@XmlElement(namespace=aquamapsTypesNS)
	private int sourceHCAF;
	@XmlElement(namespace=aquamapsTypesNS)
	private int sourceHSPEC;
	@XmlElement(namespace=aquamapsTypesNS)
	private int sourceHSPEN;
	@XmlElement(namespace=aquamapsTypesNS)
	private String status;
	@XmlElement(namespace=aquamapsTypesNS)
	private String title;
	@XmlElement(namespace=aquamapsTypesNS)
	private String type;
	@XmlElement(namespace=aquamapsTypesNS)
	private String speciesCoverage;
	@XmlElement(namespace=aquamapsTypesNS)
	private boolean Customized;
	@XmlElement(namespace=aquamapsTypesNS)
	private String fileSetId;
	@XmlElement(namespace=aquamapsTypesNS)
	private boolean forceRegeneration;
	
	
	public Submitted() {
		// TODO Auto-generated constructor stub
	}


	public Submitted(String author, long startTime, long endTime,
			long submissionTime, boolean gisEnabled, String publishedIds,
			String gisReferences, boolean isAquaMap, int jobId, boolean saved,
			int searchid, String selectionCriteria, int sourceHCAF,
			int sourceHSPEC, int sourceHSPEN, String status, String title,
			String type, String speciesCoverage, boolean customized,
			String fileSetId, boolean forceRegeneration) {
		super();
		this.author = author;
		this.startTime = startTime;
		this.endTime = endTime;
		this.submissionTime = submissionTime;
		this.gisEnabled = gisEnabled;
		this.publishedIds = publishedIds;
		this.gisReferences = gisReferences;
		this.isAquaMap = isAquaMap;
		this.jobId = jobId;
		this.saved = saved;
		this.searchid = searchid;
		this.selectionCriteria = selectionCriteria;
		this.sourceHCAF = sourceHCAF;
		this.sourceHSPEC = sourceHSPEC;
		this.sourceHSPEN = sourceHSPEN;
		this.status = status;
		this.title = title;
		this.type = type;
		this.speciesCoverage = speciesCoverage;
		Customized = customized;
		this.fileSetId = fileSetId;
		this.forceRegeneration = forceRegeneration;
	}


	/**
	 * @return the author
	 */
	public String author() {
		return author;
	}


	/**
	 * @param author the author to set
	 */
	public void author(String author) {
		this.author = author;
	}


	/**
	 * @return the startTime
	 */
	public long startTime() {
		return startTime;
	}


	/**
	 * @param startTime the startTime to set
	 */
	public void startTime(long startTime) {
		this.startTime = startTime;
	}


	/**
	 * @return the endTime
	 */
	public long endTime() {
		return endTime;
	}


	/**
	 * @param endTime the endTime to set
	 */
	public void endTime(long endTime) {
		this.endTime = endTime;
	}


	/**
	 * @return the submissionTime
	 */
	public long submissionTime() {
		return submissionTime;
	}


	/**
	 * @param submissionTime the submissionTime to set
	 */
	public void submissionTime(long submissionTime) {
		this.submissionTime = submissionTime;
	}


	/**
	 * @return the gisEnabled
	 */
	public boolean gisEnabled() {
		return gisEnabled;
	}


	/**
	 * @param gisEnabled the gisEnabled to set
	 */
	public void gisEnabled(boolean gisEnabled) {
		this.gisEnabled = gisEnabled;
	}


	/**
	 * @return the publishedIds
	 */
	public String publishedIds() {
		return publishedIds;
	}


	/**
	 * @param publishedIds the publishedIds to set
	 */
	public void publishedIds(String publishedIds) {
		this.publishedIds = publishedIds;
	}


	/**
	 * @return the gisReferences
	 */
	public String gisReferences() {
		return gisReferences;
	}


	/**
	 * @param gisReferences the gisReferences to set
	 */
	public void gisReferences(String gisReferences) {
		this.gisReferences = gisReferences;
	}


	/**
	 * @return the isAquaMap
	 */
	public boolean aquaMap() {
		return isAquaMap;
	}


	/**
	 * @param isAquaMap the isAquaMap to set
	 */
	public void aquaMap(boolean isAquaMap) {
		this.isAquaMap = isAquaMap;
	}


	/**
	 * @return the jobId
	 */
	public int jobId() {
		return jobId;
	}


	/**
	 * @param jobId the jobId to set
	 */
	public void jobId(int jobId) {
		this.jobId = jobId;
	}


	/**
	 * @return the saved
	 */
	public boolean saved() {
		return saved;
	}


	/**
	 * @param saved the saved to set
	 */
	public void saved(boolean saved) {
		this.saved = saved;
	}


	/**
	 * @return the searchid
	 */
	public int searchid() {
		return searchid;
	}


	/**
	 * @param searchid the searchid to set
	 */
	public void searchid(int searchid) {
		this.searchid = searchid;
	}


	/**
	 * @return the selectionCriteria
	 */
	public String selectionCriteria() {
		return selectionCriteria;
	}


	/**
	 * @param selectionCriteria the selectionCriteria to set
	 */
	public void selectionCriteria(String selectionCriteria) {
		this.selectionCriteria = selectionCriteria;
	}


	/**
	 * @return the sourceHCAF
	 */
	public int sourceHCAF() {
		return sourceHCAF;
	}


	/**
	 * @param sourceHCAF the sourceHCAF to set
	 */
	public void sourceHCAF(int sourceHCAF) {
		this.sourceHCAF = sourceHCAF;
	}


	/**
	 * @return the sourceHSPEC
	 */
	public int sourceHSPEC() {
		return sourceHSPEC;
	}


	/**
	 * @param sourceHSPEC the sourceHSPEC to set
	 */
	public void sourceHSPEC(int sourceHSPEC) {
		this.sourceHSPEC = sourceHSPEC;
	}


	/**
	 * @return the sourceHSPEN
	 */
	public int sourceHSPEN() {
		return sourceHSPEN;
	}


	/**
	 * @param sourceHSPEN the sourceHSPEN to set
	 */
	public void sourceHSPEN(int sourceHSPEN) {
		this.sourceHSPEN = sourceHSPEN;
	}


	/**
	 * @return the status
	 */
	public String status() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void status(String status) {
		this.status = status;
	}


	/**
	 * @return the title
	 */
	public String title() {
		return title;
	}


	/**
	 * @param title the title to set
	 */
	public void title(String title) {
		this.title = title;
	}


	/**
	 * @return the type
	 */
	public String type() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void type(String type) {
		this.type = type;
	}


	/**
	 * @return the speciesCoverage
	 */
	public String speciesCoverage() {
		return speciesCoverage;
	}


	/**
	 * @param speciesCoverage the speciesCoverage to set
	 */
	public void speciesCoverage(String speciesCoverage) {
		this.speciesCoverage = speciesCoverage;
	}


	/**
	 * @return the customized
	 */
	public boolean customized() {
		return Customized;
	}


	/**
	 * @param customized the customized to set
	 */
	public void customized(boolean customized) {
		Customized = customized;
	}


	/**
	 * @return the fileSetId
	 */
	public String fileSetId() {
		return fileSetId;
	}


	/**
	 * @param fileSetId the fileSetId to set
	 */
	public void fileSetId(String fileSetId) {
		this.fileSetId = fileSetId;
	}


	/**
	 * @return the forceRegeneration
	 */
	public boolean forceRegeneration() {
		return forceRegeneration;
	}


	/**
	 * @param forceRegeneration the forceRegeneration to set
	 */
	public void forceRegeneration(boolean forceRegeneration) {
		this.forceRegeneration = forceRegeneration;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Submitted [author=");
		builder.append(author);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", submissionTime=");
		builder.append(submissionTime);
		builder.append(", gisEnabled=");
		builder.append(gisEnabled);
		builder.append(", publishedIds=");
		builder.append(publishedIds);
		builder.append(", gisReferences=");
		builder.append(gisReferences);
		builder.append(", isAquaMap=");
		builder.append(isAquaMap);
		builder.append(", jobId=");
		builder.append(jobId);
		builder.append(", saved=");
		builder.append(saved);
		builder.append(", searchid=");
		builder.append(searchid);
		builder.append(", selectionCriteria=");
		builder.append(selectionCriteria);
		builder.append(", sourceHCAF=");
		builder.append(sourceHCAF);
		builder.append(", sourceHSPEC=");
		builder.append(sourceHSPEC);
		builder.append(", sourceHSPEN=");
		builder.append(sourceHSPEN);
		builder.append(", status=");
		builder.append(status);
		builder.append(", title=");
		builder.append(title);
		builder.append(", type=");
		builder.append(type);
		builder.append(", speciesCoverage=");
		builder.append(speciesCoverage);
		builder.append(", Customized=");
		builder.append(Customized);
		builder.append(", fileSetId=");
		builder.append(fileSetId);
		builder.append(", forceRegeneration=");
		builder.append(forceRegeneration);
		builder.append("]");
		return builder.toString();
	}
	
	
}
