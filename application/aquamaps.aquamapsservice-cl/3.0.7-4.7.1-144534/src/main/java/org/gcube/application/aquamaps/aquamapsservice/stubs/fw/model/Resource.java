package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XmlRootElement(namespace=aquamapsTypesNS)
@XStreamAlias("Resource")
public class Resource{

	private static Logger logger = LoggerFactory.getLogger(Resource.class);
	private static final AlgorithmType DEFAULT_ALGORITHM_TYPE=AlgorithmType.NativeRange;
	
	@XmlElement(namespace=aquamapsTypesNS)
	private int searchId=1;
	@XmlElement(namespace=aquamapsTypesNS)
	private String title;
	@XmlElement(namespace=aquamapsTypesNS)	
	private String tableName;
	@XmlElement(namespace=aquamapsTypesNS)	
	private String description;
	@XmlElement(namespace=aquamapsTypesNS)	
	private String author;
	@XmlElement(namespace=aquamapsTypesNS)
	private String disclaimer;
	@XmlElement(namespace=aquamapsTypesNS)
	private String provenance;
	@XmlElement(namespace=aquamapsTypesNS,name="date")
	private Long generationTime=0l;
	@XmlElement(namespace=aquamapsTypesNS)
	private String sourceHCAFIds;
	@XmlElement(namespace=aquamapsTypesNS)
	private String sourceHSPENIds;
	@XmlElement(namespace=aquamapsTypesNS)
	private String sourceHSPECIds;
	@XmlElement(namespace=aquamapsTypesNS)
	private String sourceOccurrenceCellsIds;
	@XmlElement(namespace=aquamapsTypesNS)
	private String parameters;
	@XmlElement(namespace=aquamapsTypesNS)
	private String status=ResourceStatus.Completed+"";
	@XmlElement(namespace=aquamapsTypesNS)
	private String sourceHSPECTables;
	@XmlElement(namespace=aquamapsTypesNS)
	private String sourceHSPENTables;
	@XmlElement(namespace=aquamapsTypesNS)
	private String sourceHCAFTables;
	@XmlElement(namespace=aquamapsTypesNS)
	private String sourceOccurrenceCellsTables;
	@XmlElement(namespace=aquamapsTypesNS)
	private String type;
	@XmlElement(namespace=aquamapsTypesNS)
	private String algorithm=DEFAULT_ALGORITHM_TYPE+"";
	@XmlElement(namespace=aquamapsTypesNS)
	private Boolean defaultSource=false;
	@XmlElement(namespace=aquamapsTypesNS,name="percent")
	private Long rowCount=0l;

	

	public Resource(String type,int searchId) {		
		this.type=type;
		this.searchId=searchId;
	}



	public Resource() {
		// TODO Auto-generated constructor stub
	}
	
	


	



	public String type() {
		return type;
	}



	public void type(String type) {
		this.type = type;
	}



	public int searchId() {
		return searchId;
	}



	public void searchId(int searchId) {
		this.searchId = searchId;
	}



	public String title() {
		return title;
	}



	public void title(String title) {
		this.title = title;
	}



	public String tableName() {
		return tableName;
	}



	public void tableName(String tableName) {
		this.tableName = tableName;
	}



	public String description() {
		return description;
	}



	public void description(String description) {
		this.description = description;
	}



	public String author() {
		return author;
	}



	public void author(String author) {
		this.author = author;
	}



	public String disclaimer() {
		return disclaimer;
	}



	public void disclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}



	public String provenance() {
		return provenance;
	}



	public void provenance(String provenance) {
		this.provenance = provenance;
	}



	public Long generationTime() {
		return generationTime;
	}



	public void generationTime(Long generationTime) {
		this.generationTime = generationTime;
	}



	public String sourceHCAFIds() {
		return sourceHCAFIds;
	}



	public void sourceHCAFIds(String sourceHCAFIds) {
		this.sourceHCAFIds = sourceHCAFIds;
	}



	



	public String sourceHSPECIds() {
		return sourceHSPECIds;
	}



	public void sourceHSPECIds(String sourceHSPECIds) {
		this.sourceHSPECIds = sourceHSPECIds;
	}



	



	public String parameters() {
		return parameters;
	}



	public void parameters(String parameters) {
		this.parameters = parameters;
	}



	public String status() {
		return status;
	}



	public void status(String status) {
		this.status = status;
	}



	



	


	public String algorithm() {
		return algorithm;
	}



	public void algorithm(String algorithm) {
		this.algorithm = algorithm;
	}



	public Boolean defaultSource() {
		return defaultSource;
	}



	public void defaultSource(Boolean defaultSource) {
		this.defaultSource = defaultSource;
	}



	public Long rowCount() {
		return rowCount;
	}



	public void rowCount(Long rowCount) {
		this.rowCount = rowCount;
	}



	public static AlgorithmType getDefaultAlgorithmType() {
		return DEFAULT_ALGORITHM_TYPE;
	}



	



	public String sourceHSPENIds() {
		return sourceHSPENIds;
	}



	public void sourceHSPENIds(String sourceHSPENIds) {
		this.sourceHSPENIds = sourceHSPENIds;
	}



	public String sourceOccurrenceCellsIds() {
		return sourceOccurrenceCellsIds;
		
	}



	public void sourceOccurrenceCellsIds(
			String sourceOccurrenceCellsIds) {
		this.sourceOccurrenceCellsIds = sourceOccurrenceCellsIds;
	}



	public String sourceHSPECTables() {
		return sourceHSPECTables;
	}



	public void sourceHSPECTables(String sourceHSPECTables) {
		this.sourceHSPECTables = sourceHSPECTables;
	}



	public String sourceHSPENTables() {
		return sourceHSPENTables;
	}



	public void sourceHSPENTables(String sourceHSPENTables) {
		this.sourceHSPENTables = sourceHSPENTables;
	}



	public String sourceHCAFTables() {
		return sourceHCAFTables;
	}



	public void sourceHCAFTables(String sourceHCAFTables) {
		this.sourceHCAFTables = sourceHCAFTables;
	}



	public String sourceOccurrenceCellsTables() {
		return sourceOccurrenceCellsTables;
	}



	public void sourceOccurrenceCellsTables(
			String sourceOccurrenceCellsTables) {
		this.sourceOccurrenceCellsTables = sourceOccurrenceCellsTables;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Resource [searchId=");
		builder.append(searchId);
		builder.append(", title=");
		builder.append(title);
		builder.append(", tableName=");
		builder.append(tableName);
		builder.append(", description=");
		builder.append(description);
		builder.append(", author=");
		builder.append(author);
		builder.append(", disclaimer=");
		builder.append(disclaimer);
		builder.append(", provenance=");
		builder.append(provenance);
		builder.append(", generationTime=");
		builder.append(generationTime);
		builder.append(", sourceHCAFIds=");
		builder.append(sourceHCAFIds);
		builder.append(", sourceHSPENIds=");
		builder.append(sourceHSPENIds);
		builder.append(", sourceHSPECIds=");
		builder.append(sourceHSPECIds);
		builder.append(", sourceOccurrenceCellsIds=");
		builder.append(sourceOccurrenceCellsIds);
		builder.append(", parameters=");
		builder.append(parameters);
		builder.append(", status=");
		builder.append(status);
		builder.append(", sourceHSPECTables=");
		builder.append(sourceHSPECTables);
		builder.append(", sourceHSPENTables=");
		builder.append(sourceHSPENTables);
		builder.append(", sourceHCAFTables=");
		builder.append(sourceHCAFTables);
		builder.append(", sourceOccurrenceCellsTables=");
		builder.append(sourceOccurrenceCellsTables);
		builder.append(", type=");
		builder.append(type);
		builder.append(", algorithm=");
		builder.append(algorithm);
		builder.append(", defaultSource=");
		builder.append(defaultSource);
		builder.append(", rowCount=");
		builder.append(rowCount);
		builder.append("]");
		return builder.toString();
	}
	
	
}
