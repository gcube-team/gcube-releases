/**
 * 
 */
package org.gcube.data.oai.tmplugin.requests;

import static java.util.Arrays.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Base implementation for plugin requests
 * 
 * @author Fabio Simeoni
 * @author Lucio Lelii (CNR)
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Request implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement
	private String url;		
	@XmlElement
	private List<String> alternativesXPath = new ArrayList<String>();		
	@XmlElement
	private String contentXPath;		
	@XmlElement
	private String titleXPath;		
	@XmlElement
	private String metadataFormat;
	@XmlElement
	private String name;
	@XmlElement
	private String description;
	@XmlElement
	private List<String> sets = new ArrayList<String>();
	



	Request() {}

	Request(String url) throws IllegalArgumentException {

		if (url == null || url.isEmpty())
			throw new IllegalArgumentException("repository url is null or empty");

		this.url = url;
	}

	

	
	/**
	 * Returns the URL of the target OAI repository
	 */
	public String getRepositoryUrl() {
		return url;
	}
	
	
	/**
	 * Returns a name for the source to bind.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets a name for the source to bind.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * Returns a description of the source to bind.
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a description for the source to bind.
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	/**
	 * Returns the alternatives path of the source to bind.
	 */
	public List<String> getAlternativesXPath() {
		return alternativesXPath;
	}
	

	public String getContentXPath() {
		return contentXPath;
	}

	public String getTitleXPath() {
		return titleXPath;
	}
	
	public String getMetadataFormat() {
		return metadataFormat;
	}
	


	public List<String> getSets() {
		return sets;
	}
	
	/**
	 * Adds a sets
	 */
	public void addSets(String... setIds) {
		if (setIds != null)
			sets.addAll(asList(setIds));
	}
	
	/**
	 * Sets a contentXPath
	 */
	public void setContentXPath(String path) {
		this.contentXPath = path;
	}
	
	/**
	 * Sets RepositoryUrl
	 */
	public void setRepositoryUrl(String path) {
		this.url = path;
	}
	
	/**
	 * Sets a titleXPath
	 */
	public void setTitleXPath(String path) {
		this.titleXPath = path;
	}
	
	/**
	 * Adds an alternativesXPath
	 */
	public void addAlternativesXPath(String... path) {
		if (path != null)
			alternativesXPath.addAll(asList(path));
	}
	
	/**
	 * Adds a MetadataFormat
	 */
	public void setMetadataFormat(String metadataFormat) {
		this.metadataFormat = metadataFormat;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;	
		result = prime * result + ((sets == null) ? 0 : sets.hashCode());
		result = prime * result	+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((alternativesXPath == null) ? 0 : alternativesXPath.hashCode());
		result = prime * result + ((contentXPath == null) ? 0 : contentXPath.hashCode());
		result = prime * result + ((titleXPath == null) ? 0 : titleXPath.hashCode());
		result = prime * result + ((metadataFormat == null) ? 0 : metadataFormat.hashCode());
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
		Request other = (Request) obj;

		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		
		if (alternativesXPath == null) {
			if (other.alternativesXPath != null)
				return false;
		} else if (!alternativesXPath.equals(other.alternativesXPath))
			return false;
		
		if (contentXPath == null) {
			if (other.contentXPath != null)
				return false;
		} else if (!contentXPath.equals(other.contentXPath))
			return false;
		
		if (titleXPath == null) {
			if (other.titleXPath != null)
				return false;
		} else if (!titleXPath.equals(other.titleXPath))
			return false;
		
		if (metadataFormat == null) {
			if (other.metadataFormat != null)
				return false;
		} else if (!metadataFormat.equals(other.metadataFormat))
			return false;		
		
		if (sets == null) {
			if (other.sets != null)
				return false;
		} else if (!sets.equals(other.sets))
			return false;
		
		return true;
	}

	
}
