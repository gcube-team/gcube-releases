package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HspecGroupGenerationRequestType {

	@XmlElement
	private String author;
	@XmlElement
	private String generationName;
	@XmlElement
	private String description;
	@XmlElement
	private String hspenIds;
	@XmlElement
	private String hcafIds;
	@XmlElement
	private String executionParameters;
	@XmlElement
	private String generationParameters;
	@XmlElement
	private String occurrenceCellsIds;
	@XmlElement
	private String submissionBackend;
	@XmlElement
	private String executionEnvironment;
	@XmlElement
	private String backendUrl;
	@XmlElement
	private String environmentConfiguration;
	@XmlElement
	private String logic;
	@XmlElement
	private int numPartitions;
	@XmlElement
	private String algorithms;
	
	public HspecGroupGenerationRequestType() {
		// TODO Auto-generated constructor stub
	}

	public HspecGroupGenerationRequestType(String author,
			String generationName, String description, String hspenIds,
			String hcafIds, String executionParameters,
			String generationParameters, String occurrenceCellsIds,
			String submissionBackend, String executionEnvironment,
			String backendUrl, String environmentConfiguration, String logic,
			int numPartitions, String algorithms) {
		super();
		this.author = author;
		this.generationName = generationName;
		this.description = description;
		this.hspenIds = hspenIds;
		this.hcafIds = hcafIds;
		this.executionParameters = executionParameters;
		this.generationParameters = generationParameters;
		this.occurrenceCellsIds = occurrenceCellsIds;
		this.submissionBackend = submissionBackend;
		this.executionEnvironment = executionEnvironment;
		this.backendUrl = backendUrl;
		this.environmentConfiguration = environmentConfiguration;
		this.logic = logic;
		this.numPartitions = numPartitions;
		this.algorithms = algorithms;
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
	 * @return the generationName
	 */
	public String generationName() {
		return generationName;
	}

	/**
	 * @param generationName the generationName to set
	 */
	public void generationName(String generationName) {
		this.generationName = generationName;
	}

	/**
	 * @return the description
	 */
	public String description() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void description(String description) {
		this.description = description;
	}

	/**
	 * @return the hspenIds
	 */
	public String hspenIds() {
		return hspenIds;
	}

	/**
	 * @param hspenIds the hspenIds to set
	 */
	public void hspenIds(String hspenIds) {
		this.hspenIds = hspenIds;
	}

	/**
	 * @return the hcafIds
	 */
	public String hcafIds() {
		return hcafIds;
	}

	/**
	 * @param hcafIds the hcafIds to set
	 */
	public void hcafIds(String hcafIds) {
		this.hcafIds = hcafIds;
	}

	/**
	 * @return the executionParameters
	 */
	public String executionParameters() {
		return executionParameters;
	}

	/**
	 * @param executionParameters the executionParameters to set
	 */
	public void executionParameters(String executionParameters) {
		this.executionParameters = executionParameters;
	}

	/**
	 * @return the generationParameters
	 */
	public String generationParameters() {
		return generationParameters;
	}

	/**
	 * @param generationParameters the generationParameters to set
	 */
	public void generationParameters(String generationParameters) {
		this.generationParameters = generationParameters;
	}

	/**
	 * @return the occurrenceCellsIds
	 */
	public String occurrenceCellsIds() {
		return occurrenceCellsIds;
	}

	/**
	 * @param occurrenceCellsIds the occurrenceCellsIds to set
	 */
	public void occurrenceCellsIds(String occurrenceCellsIds) {
		this.occurrenceCellsIds = occurrenceCellsIds;
	}

	/**
	 * @return the submissionBackend
	 */
	public String submissionBackend() {
		return submissionBackend;
	}

	/**
	 * @param submissionBackend the submissionBackend to set
	 */
	public void submissionBackend(String submissionBackend) {
		this.submissionBackend = submissionBackend;
	}

	/**
	 * @return the executionEnvironment
	 */
	public String executionEnvironment() {
		return executionEnvironment;
	}

	/**
	 * @param executionEnvironment the executionEnvironment to set
	 */
	public void executionEnvironment(String executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}

	/**
	 * @return the backendUrl
	 */
	public String backendUrl() {
		return backendUrl;
	}

	/**
	 * @param backendUrl the backendUrl to set
	 */
	public void backendUrl(String backendUrl) {
		this.backendUrl = backendUrl;
	}

	/**
	 * @return the environmentConfiguration
	 */
	public String environmentConfiguration() {
		return environmentConfiguration;
	}

	/**
	 * @param environmentConfiguration the environmentConfiguration to set
	 */
	public void environmentConfiguration(String environmentConfiguration) {
		this.environmentConfiguration = environmentConfiguration;
	}

	/**
	 * @return the logic
	 */
	public String logic() {
		return logic;
	}

	/**
	 * @param logic the logic to set
	 */
	public void logic(String logic) {
		this.logic = logic;
	}

	/**
	 * @return the numPartitions
	 */
	public int numPartitions() {
		return numPartitions;
	}

	/**
	 * @param numPartitions the numPartitions to set
	 */
	public void numPartitions(int numPartitions) {
		this.numPartitions = numPartitions;
	}

	/**
	 * @return the algorithms
	 */
	public String algorithms() {
		return algorithms;
	}

	/**
	 * @param algorithms the algorithms to set
	 */
	public void algorithms(String algorithms) {
		this.algorithms = algorithms;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HspecGroupGenerationRequestType [author=");
		builder.append(author);
		builder.append(", generationName=");
		builder.append(generationName);
		builder.append(", description=");
		builder.append(description);
		builder.append(", hspenIds=");
		builder.append(hspenIds);
		builder.append(", hcafIds=");
		builder.append(hcafIds);
		builder.append(", executionParameters=");
		builder.append(executionParameters);
		builder.append(", generationParameters=");
		builder.append(generationParameters);
		builder.append(", occurrenceCellsIds=");
		builder.append(occurrenceCellsIds);
		builder.append(", submissionBackend=");
		builder.append(submissionBackend);
		builder.append(", executionEnvironment=");
		builder.append(executionEnvironment);
		builder.append(", backendUrl=");
		builder.append(backendUrl);
		builder.append(", environmentConfiguration=");
		builder.append(environmentConfiguration);
		builder.append(", logic=");
		builder.append(logic);
		builder.append(", numPartitions=");
		builder.append(numPartitions);
		builder.append(", algorithms=");
		builder.append(algorithms);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
