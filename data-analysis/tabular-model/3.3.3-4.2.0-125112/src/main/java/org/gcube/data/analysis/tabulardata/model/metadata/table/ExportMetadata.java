package org.gcube.data.analysis.tabulardata.model.metadata.table;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.metadata.DataDependentMetadata;

@XmlRootElement(name = "ExportMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExportMetadata implements DataDependentMetadata {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4552165080272546576L;
	
	private String destinationType;
	private String url;
	private Date exportDate;

	@SuppressWarnings("unused")
	private ExportMetadata() {
	}

	public ExportMetadata(String destinationType, String url, Date exportDate) {
		this.destinationType = destinationType;
		this.url = url;
		this.exportDate = exportDate;
	}

	public String getDestinationType() {
		return destinationType;
	}

	public String getUri() {
		return url;
	}

	public Date getExportDate() {
		return exportDate;
	}


	public boolean isInheritable() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destinationType == null) ? 0 : destinationType.hashCode());
		result = prime * result + ((exportDate == null) ? 0 : exportDate.hashCode());
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
		ExportMetadata other = (ExportMetadata) obj;
		if (destinationType == null) {
			if (other.destinationType != null)
				return false;
		} else if (!destinationType.equals(other.destinationType))
			return false;
		if (exportDate == null) {
			if (other.exportDate != null)
				return false;
		} else if (!exportDate.equals(other.exportDate))
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
		builder.append("ExportMetadata [destinationType=");
		builder.append(destinationType);
		builder.append(", url=");
		builder.append(url);
		builder.append(", exportDate=");
		builder.append(exportDate);
		builder.append("]");
		return builder.toString();
	}

}
