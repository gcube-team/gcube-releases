package org.gcube.datapublishing.sdmx.api.registry;

import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryDescriptor;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.gcube.datapublishing.sdmx.impl.reports.SubmissionReport;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.base.AgencySchemeBean;
import org.sdmxsource.sdmx.api.model.beans.base.DataProviderSchemeBean;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.beans.conceptscheme.ConceptSchemeBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataStructureBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataflowBean;
import org.sdmxsource.sdmx.api.model.beans.registry.ProvisionAgreementBean;
import org.sdmxsource.sdmx.api.model.beans.registry.RegistrationBean;

public interface SDMXRegistryClient {
	
	
	public enum Detail {
		full, allstubs, referencestubs
	};

	public enum References {
		none, parents, parentsandsiblings, children, descendants, all
	}
	
	public void setRegistry(SDMXRegistryDescriptor descriptor);
	
	public SDMXRegistryDescriptor getRegistry();
	
	public SubmissionReport publish(AgencySchemeBean agencyScheme)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(CodelistBean codelist)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(ConceptSchemeBean conceptscheme)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(DataStructureBean datastructure)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(DataflowBean dataflow) 
			throws SDMXRegistryClientException;

	public SubmissionReport publish(DataProviderSchemeBean dataproviderscheme)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(ProvisionAgreementBean provisionagreement)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(RegistrationBean subscription)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(AgencySchemeBean agencyScheme, boolean versionAware)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(CodelistBean codelist, boolean versionAware)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(ConceptSchemeBean conceptscheme, boolean versionAware)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(DataStructureBean datastructure, boolean versionAware)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(DataflowBean dataflow, boolean versionAware) 
			throws SDMXRegistryClientException;

	public SubmissionReport publish(DataProviderSchemeBean dataproviderscheme, boolean versionAware)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(ProvisionAgreementBean provisionagreement, boolean versionAware)
			throws SDMXRegistryClientException;

	public SubmissionReport publish(RegistrationBean subscription, boolean versionAware)
			throws SDMXRegistryClientException;
	
	
	public SdmxBeans getAgencyScheme(String agencyId, String id,
			String version, Detail details, References references)
			throws SDMXRegistryClientException;

	public SdmxBeans getCodelist(String agencyId, String id, String version,
			Detail details, References references) 
					throws SDMXRegistryClientException;

	public SdmxBeans getConceptScheme(String agencyId, String id,
			String version, Detail details, References references)
			throws SDMXRegistryClientException;

	public SdmxBeans getDataStructure(String agencyId, String id,
			String version, Detail details, References references)
			throws SDMXRegistryClientException;

	public SdmxBeans getDataFlow(String agencyId, String id, String version,
			Detail details, References references) throws SDMXRegistryClientException;

	public SdmxBeans getDataProviderScheme(String agencyId, String id,
			String version, Detail details, References references)
			throws SDMXRegistryClientException;

	public SdmxBeans getProvisionAgreement(String agencyId, String id,
			String version, Detail details, References references)
			throws SDMXRegistryClientException;
	
	public SdmxBeans getAllDataSetRegistrations() throws SDMXRegistryClientException;

}