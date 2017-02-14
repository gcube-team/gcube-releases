package org.gcube.datapublishing.sdmx.impl.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="TimeseriesRegistration")
public class TimeseriesRegistration {
	
	@XmlElement(name="TimeSeriesScope", required=true)
	private String timeseriesScope;
	
	@XmlElement(name="TimeSeriesID", required=true)
	private String timeseriesId;
	
	@XmlElement(name="FlowAgencyID", required=true)
	private String flowAgencyId;
	
	@XmlElement(name="FlowID", required=true)
	private String flowId;
	
	@XmlElement(name="FlowVersion", required=true)
	private String flowVersion;
	
	@XmlElement(name="ProviderAgencyID", required=true)
	private String providerAgencyId;
	
	@XmlElement(name="ProviderID", required=true)
	private String providerId;
	
	@XmlElement(name="RegistryScope", required=true)
	private String registryScope;
	
}
