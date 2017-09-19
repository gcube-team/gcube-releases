package org.gcube.application.aquamaps.publisher;

public class MetaInformations {

	private String author;
	private String description;
	private String disclaimer;
	private String title;
	private Long date;
	private String algorithm;
	private Long dataGenerationTime;
	
	public MetaInformations(String author, String description,
			String disclaimer, String title, Long date, String algorithm,
			Long dataGenerationTime) {
		super();
		this.author = author;
		this.description = description;
		this.disclaimer = disclaimer;
		this.title = title;
		this.date = date;
		this.algorithm = algorithm;
		this.dataGenerationTime = dataGenerationTime;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getDisclaimer() {
		return disclaimer;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the date
	 */
	public Long getDate() {
		return date;
	}

	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @return the dataGenerationTime
	 */
	public Long getDataGenerationTime() {
		return dataGenerationTime;
	}
	
	
}
