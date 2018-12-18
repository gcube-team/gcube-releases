package org.gcube.data.analysis.tabulardata.model.metadata.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.metadata.table.TableMetadata;

@XmlRootElement(name = "TableDescriptorMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class TableDescriptorMetadata implements TableMetadata {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8086337680639459530L;
	
	private String name;
	private String version;
	private String agency;
	private long refId;
	
	@SuppressWarnings("unused")
	private TableDescriptorMetadata(){}

	public TableDescriptorMetadata(String name, String version, long refId) {
		this (name,version,null, refId);
	}
	
	public TableDescriptorMetadata(String name, String version, String agency, long refId) {
		super();
		this.name = name;
		this.version = version;
		this.agency = agency;
		this.refId = refId;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}
	
	public long getRefId() {
		return refId;
	}
	
	public String getAgency ()
	{
		return this.agency;
	}

	@Override
	public boolean isInheritable() {
		return false;
	}

	@Override
	public String toString() {
		return "TableDescriptorMetadata [name=" + name + ", version=" + version
				+ (this.agency == null ? "":", agency="+agency)+", refId=" + refId + "]";
	}
	
}
