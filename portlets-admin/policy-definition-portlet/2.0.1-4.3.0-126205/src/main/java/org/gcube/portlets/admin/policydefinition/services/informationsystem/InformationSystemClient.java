package org.gcube.portlets.admin.policydefinition.services.informationsystem;

import java.io.IOException;
import java.util.List;

//import javax.wsdl.WSDLException;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.util.portlet.PortletProps;

public class InformationSystemClient {

	private static Logger logger = LoggerFactory.getLogger(InformationSystemClient.class);
	private static InformationSystemClient instance;
//	private WSDLReader wsdlReader;
	private DiscoveryClient<GCoreEndpoint> serviceEndpointClient;
	private DiscoveryClient<String> simpleClient;
	
	private final static String SOA3_ENTRY_NAME_PARAMETER_NAME = "soa3.endpoint.entry.name";
	private final static String SOA3_PROFILE_NAME_PARAMETER_NAME = "soa3.profile.name";
	
	private static final String XQUERY_DISTINCT_SERVICENAME_SERVICE_CLASS = 
			"declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry'; "+
			"let $services := collection('/db/Profiles/RunningInstance')//Document/Data/ic:Profile/Resource "+
			"for $service in $services "+
			"where $service "+
			"order by $service/Profile/ServiceName, $service/Profile/ServiceClass "+
			"return "+
				"if ($service is ($services)[Profile/ServiceName = $service/Profile/ServiceName and Profile/ServiceClass = $service/Profile/ServiceClass][1]) "+
				"then $service "+
	        	"else ()";
	
	private static final String XQUERY_COUNT_DISTINCT_SERVICENAME_SERVICE_CLASS = 
			"declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry'; "+
			"let $count := count("+
				"let $services := collection('/db/Profiles/RunningInstance')//Document/Data/ic:Profile/Resource "+
				"for $service in $services "+
				"where $service "+
				"return "+
					"if ($service is ($services)[Profile/ServiceName = $service/Profile/ServiceName and Profile/ServiceClass = $service/Profile/ServiceClass][1]) "+
					"then $service "+
		        	"else ()"+
			") return $count";
	
	private static final String XQUERY_DISTINCT_SERVICENAME_SERVICE_CLASS_PAGINATED = 
			"declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry'; "+
			"let $result := ("+
				"let $services := collection('/db/Profiles/RunningInstance')//Document/Data/ic:Profile/Resource "+
				"for $service in $services "+
				"where $service "+
//				"order by $service/Profile/ServiceName, $service/Profile/ServiceClass "+
				"${orderByClause}"+
				"return "+
					"if ($service is ($services)[Profile/ServiceName = $service/Profile/ServiceName and Profile/ServiceClass = $service/Profile/ServiceClass][1]) "+
					"then $service "+
		        	"else ()"+
			") return subsequence($result, ${start}, ${length})";
	
//	public static void main(String[] args) {
//		ScopeProvider.instance.set("/testing");
//		DiscoveryClient<String> client = ICFactory.client();
//		List<String> submit = client.submit(new QueryBox(XQUERY_DISTINCT_SERVICENAME_SERVICE_CLASS_PAGINATED.replace("${start}", "1").replace("${length}", "1")));
//		System.out.println(submit);
//	}
	
	private InformationSystemClient(){// throws WSDLException{
		serviceEndpointClient = ICFactory.clientFor(GCoreEndpoint .class);
		simpleClient = ICFactory.client();
		logger.debug("Setted scope: "+ScopeProvider.instance.get());
//		WSDLFactory factory = WSDLFactory.newInstance();
//		wsdlReader = factory.newWSDLReader();
	}
	
	/**
	 * Return the singleton instance of {@link InformationSystemClient}
	 * @return {@link InformationSystemClient}
	 * @throws IOException
	 * @throws WSDLException
	 */
	public synchronized static InformationSystemClient getInstance(){// throws IOException, WSDLException{
		if(instance == null)
			instance = new InformationSystemClient();
		return instance;
	}
	
	/**
	 * Retrieve services
	 * @return {@link List} of {@link GCoreEndpoint}
	 */
	public List<GCoreEndpoint> retrieveServices(){		
		return serviceEndpointClient.submit(new QueryBox(XQUERY_DISTINCT_SERVICENAME_SERVICE_CLASS));
	}
	
	/**
	 * Retrieve services paginated
	 * @param start position
	 * @param length
	 * @return @return {@link List} of {@link GCoreEndpoint}
	 */
	public List<GCoreEndpoint> retrieveServices(int start, int length, String orderByClause){		
		return serviceEndpointClient.submit(
				new QueryBox(XQUERY_DISTINCT_SERVICENAME_SERVICE_CLASS_PAGINATED
						.replace("${start}", String.valueOf(++start))
						.replace("${length}", String.valueOf(length))
						.replace("${orderByClause}", orderByClause)));
	}
	
	/**
	 * Count services
	 * @return number of services
	 */
	public int countServices(){		
		List<String> submit = simpleClient.submit(new QueryBox(XQUERY_COUNT_DISTINCT_SERVICENAME_SERVICE_CLASS));
		if(submit == null || submit.size() == 0) return 0;
		return new Integer(submit.get(0));
	}
	
	/**
	 * Retrieve host by serviceName and serviceClass.
	 * @param serviceName
	 * @param serviceClass
	 * @return {@link List} of {@link String} representing hosts
	 */
	public List<String> retrieveHosts(String serviceName, String serviceClass){
		SimpleQuery query = ICFactory.queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceName/text() eq '"+serviceName+"' and $resource/Profile/ServiceClass/text() eq '"+serviceClass+"'")
		   .setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint/text()");		
		return simpleClient.submit(query);
	}
	
	/**
	 * Retrieve SOA3 base URL.
	 * @return {@link String} representing SOA3 base URL
	 * @throws InformationSystemResponseException 
	 */
	public String retrieveSoa3Url() throws InformationSystemResponseException{
		SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name eq '"+PortletProps.get(SOA3_PROFILE_NAME_PARAMETER_NAME)+"'")
		   .setResult("$resource/Profile/AccessPoint/Interface/Endpoint[@EntryName='"+PortletProps.get(SOA3_ENTRY_NAME_PARAMETER_NAME)+"']/text()");		
		List<String> soa3url = simpleClient.submit(query);
		if(soa3url == null || soa3url.size() == 0) 
			throw new InformationSystemResponseException("Unable to retrieve ["+PortletProps.get(SOA3_PROFILE_NAME_PARAMETER_NAME)+":"+PortletProps.get(SOA3_ENTRY_NAME_PARAMETER_NAME)+"] Endpoint URL"); //return "http://192.168.125.71:8080";
		return soa3url.get(0);
	}
	
	/**
	 * Retrieve the WSLD defined operations from a given URL.
	 * @param endpoint
	 * @return {@link List} of {@link String} operations
	 * @throws WSDLException
	 */
//	public List<String> retrieveOperation(String endpoint) throws WSDLException{
//		List<String> operations = new ArrayList<String>();
//		Definition wsdlInstance = wsdlReader.readWSDL(endpoint+"?wsdl");
//
//		@SuppressWarnings("unchecked")
//		Map<String, Service> services = (Map<String, Service>) wsdlInstance.getServices();
//		Collection<Service> values = services.values();
//		for (Service service : values) {
//			@SuppressWarnings("unchecked")
//			Map<String, Port> ports = (Map<String, Port>) service.getPorts();
//			Collection<Port> values2 = ports.values();
//			for (Port port : values2) {
//				@SuppressWarnings("unchecked")
//				List<BindingOperation> bindingOperations = port.getBinding().getBindingOperations();
//				for (BindingOperation operation : bindingOperations) {
//					operations.add(operation.getName());
//				}
//
//			}
//		}
//		return operations;
//	}

}
