package org.gcube.data.spd.model.products;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.annotations.IsValid;
import org.gcube.common.validator.annotations.NotEmpty;
import org.gcube.common.validator.annotations.NotNull;
import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.model.util.ElementProperty;



@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TaxonomyItem implements ResultElement, TaxonomyInterface, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotEmpty
	@NotNull	
	@XmlAttribute
	private String id;
	
	@XmlAttribute
	private String rank;
	
	@NotEmpty
	@NotNull
	@XmlAttribute
	private String scientificName;
	
	@NotEmpty
	@NotNull
	@XmlAttribute
	private String citation;
	
	@XmlAttribute
	public Calendar modified;
	
	@XmlAttribute
	private String scientificNameAuthorship;
	
	@XmlAttribute
	private String credits;
	
	@XmlAttribute
	private String lsid;
	
	@IsValid
	@XmlElement
	private TaxonomyItem parent;
	
	@XmlElement
	private List<CommonName> commonNames;
	
	@NotNull
	@XmlElement(required=true)
	private TaxonomyStatus status;
	
	@XmlAttribute
	private String provider;
	
	@XmlElement
	private List<ElementProperty> properties = new ArrayList<ElementProperty>() ;
	
	TaxonomyItem(){}
	
	public TaxonomyItem(String id){
		this.id = id;
		this.commonNames = Collections.emptyList();
	}
	
	
	
	
	public TaxonomyItem getParent() {
		return parent;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public String getCitation() {
		return citation;
	}

	public void setCitation(String citation) {
		this.citation = citation;
	}

	public void setParent(TaxonomyItem parent) {
		this.parent = parent;
	}

	public List<CommonName> getCommonNames() {
		return commonNames;
	}

	public void setCommonNames(List<CommonName> commonNames) {
		this.commonNames = commonNames;
	}

	public TaxonomyStatus getStatus() {
		return status;
	}

	public void setStatus(TaxonomyStatus status) {
		this.status = status;
	}
	
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	
	
	public Calendar getModified() {
		return modified;
	}

	public void setModified(Calendar modified) {
		this.modified = modified;
	}
			
		
	/**
	 * @return the scientificNameAuthorship
	 */
	public String getScientificNameAuthorship() {
		return scientificNameAuthorship;
	}

	/**
	 * @param scientificNameAuthorship the scientificNameAuthorship to set
	 */
	public void setScientificNameAuthorship(String scientificNameAuthorship) {
		this.scientificNameAuthorship = scientificNameAuthorship;
	}

	public String getCredits() {
		return credits;
	}

	public void setCredits(String credits) {
		this.credits = credits;
	}

	public String getLsid() {
		return lsid;
	}

	public void setLsid(String lsid) {
		this.lsid = lsid;
	}

	public ResultType getType() {
		return ResultType.TAXONOMY;
	}
	
	public void addProperty(ElementProperty property){
		this.properties.add(property);
	}
	
	public void resetProperties(){
		this.properties = new ArrayList<ElementProperty>();
	}
	
	public List<ElementProperty> getProperties() {
		return Collections.unmodifiableList(properties);
	}
	
	
	@Override
	public String toString() {
		return "TaxonomyItem [id=" + id + ", rank=" + rank
				+ ", scientificName=" + scientificName + ", citation="
				+ citation + ", modified=" + modified 
				+ ", commonNames=" + commonNames + ", status=" + status
				+ ", provider=" + provider + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		TaxonomyItem other = (TaxonomyItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	

	
	
}
