package org.gcube.data.analysis.tabulardata.model.metadata.table;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ImportMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportMetadata implements TableMetadata {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7906462589850592374L;

	
	private String sourceType;
	private String url;
	private Date importDate;

	@SuppressWarnings("unused")
	private ImportMetadata() {
	}

	public ImportMetadata(String sourceType, String url, Date importDate) {
		super();
		this.sourceType = sourceType;
		this.url = url;
		this.importDate = importDate;
	}

	
	public String getSourceType() {
		return sourceType;
	}

	
	public String getUri() {
		return url;
	}

	
	public Date getImportDate() {
		return importDate;
	}

	public boolean isInheritable() {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourceType == null) ? 0 : sourceType.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		ImportMetadata other = (ImportMetadata) obj;
		if (sourceType == null) {
			if (other.sourceType != null)
				return false;
		} else if (!sourceType.equals(other.sourceType))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SourceMetadata [sourceType=");
		builder.append(sourceType);
		builder.append(", url=");
		builder.append(url);
		builder.append("]");
		return builder.toString();
	}

}