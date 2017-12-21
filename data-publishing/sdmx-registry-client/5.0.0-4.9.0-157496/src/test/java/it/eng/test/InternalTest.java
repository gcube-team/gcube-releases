package it.eng.test;

import java.net.Authenticator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryInterfaceType;
import org.gcube.datapublishing.sdmx.api.model.versioning.Version;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.Detail;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.References;
import org.gcube.datapublishing.sdmx.impl.model.SDMXRegistryDescriptorImpl;
import org.gcube.datapublishing.sdmx.impl.registry.FusionRegistryClient;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.base.AgencyBean;
import org.sdmxsource.sdmx.api.model.beans.base.AgencySchemeBean;
import org.sdmxsource.sdmx.api.model.beans.base.IdentifiableBean;
import org.sdmxsource.sdmx.api.model.beans.base.SDMXBean;
import org.sdmxsource.sdmx.api.model.beans.base.TextTypeWrapper;
import org.sdmxsource.sdmx.api.model.beans.conceptscheme.ConceptSchemeBean;
import org.sdmxsource.sdmx.api.model.beans.reference.CrossReferenceBean;
import org.sdmxsource.sdmx.api.model.mutable.base.AgencyMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.base.AgencySchemeMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.base.TextTypeWrapperMutableBean;
import org.sdmxsource.sdmx.sdmxbeans.model.beans.base.TextTypeWrapperImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.AgencyMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.TextTypeWrapperMutableBeanImpl;

public class InternalTest {
	public static void main(String[] args) throws Exception{
	ProxyAuthenticator authenticator = new ProxyAuthenticator();
	authenticator.setProxyUserName("cirformi");
	authenticator.setProxyPassword("sys6473880");
	authenticator.configure();
	if (authenticator.isActive()) Authenticator.setDefault(authenticator);
	
	SDMXRegistryDescriptorImpl descriptor = new SDMXRegistryDescriptorImpl();
	descriptor.setUrl(SDMXRegistryInterfaceType.RESTV2_1, "http://node8.d.d4science.research-infrastructures.eu:8080/FusionRegistry/ws/rest/");
	//SDMXRegistryClient registryClient = new FusionRegistryClient(descriptor);
	SDMXRegistryClient registryClient = new FusionRegistryClient(descriptor);
	SdmxBeans beans = registryClient.getAgencyScheme(AgencySchemeBean.DEFAULT_SCHEME, "", Version.ALL, Detail.full, References.none);
	
	Set<AgencySchemeBean> agencySchemes = beans.getAgenciesSchemes();
	
	AgencySchemeBean agencyScheme = null;
	
	if (agencySchemes.size()>0) agencyScheme = agencySchemes.iterator().next();

	System.out.println(agencyScheme.getAgencyId());
	Iterator<AgencyBean> composite = agencyScheme.getItems().iterator();
	
	while (composite.hasNext())
	{
		System.out.println("**************  "+ composite.next());
		
	}
	
	AgencySchemeMutableBean mutableScheme = agencyScheme.getMutableInstance();
	mutableScheme.removeItem("RD_LAB");
	mutableScheme.removeItem("Prova");
	registryClient.publish(mutableScheme.getImmutableInstance());

	
	
}

}
