package gr.cite.regional.data.collection.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlAccessorType(XmlAccessType.FIELD)
public class TabmanInfoDto {
	
	@JsonProperty("exportDate")
	@XmlElement(name = "exportDate")
	private Date exportDate;
	
	@JsonProperty("exportUri")
	@XmlElement(name = "exportUri")
	private String exportUri;

	@JsonProperty("resourceId")
	@XmlElement(name = "resourceId")
	private long resourceId;
	
	public Date getExportDate() {
		return exportDate;
	}
	
	public void setExportDate(Date exportDate) {
		this.exportDate = exportDate;
	}
	
	public String getExportUri() {
		return exportUri;
	}
	
	public void setExportUri(String exportUri) {
		this.exportUri = exportUri;
	}

	public long getResourceId() { return resourceId; }

	public void setResourceId(long resourceId) { this.resourceId = resourceId; }
}
