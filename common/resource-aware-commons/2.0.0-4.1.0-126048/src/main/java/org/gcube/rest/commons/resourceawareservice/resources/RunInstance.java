package org.gcube.rest.commons.resourceawareservice.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.rest.commons.helpers.XMLConverter;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@XmlRootElement(name = "RunInstance")
public class RunInstance extends GeneralResource {
	private String id;
	private Set<String> scopes;
	private Profile profile;
	
	@XmlElement(name = "Type")
	private String type = "RunningInstance";
	
	private RunInstance() {
	}

	/**
	 * @param endpoints
	 * @param id
	 * @param scopes
	 * @param specificData
	 */
	public RunInstance(String id, Set<String> scopes, Profile profile) {
		super();
		this.id = id;
		this.scopes = scopes;
		this.profile = profile;
	}

	/**
	 * @return the id
	 */
	@XmlElement(name = "ID")
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the scopes
	 */
	@XmlElement(name = "Scope")
	@XmlElementWrapper(name = "Scopes")
	public Set<String> getScopes() {
		return scopes;
	}

	/**
	 * @param scopes
	 *            the scopes to set
	 */
	public void setScopes(Set<String> scopes) {
		this.scopes = scopes;
	}

	@XmlElement(name = "Profile")
	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@XmlRootElement(name = "Profile")
	public static class Profile {
		private Profile() {
		}

		public Profile(String description, String version, String ghnId, String serviceId, String serviceName, String serviceClass, Date activationTime,
				String status, Map<String, URI> accessPoint, Node specificData) {
			super();
			this.description = description;
			this.version = version;
			this.ghn.ghnId = ghnId;
			this.service.serviceId = serviceId;
			this.serviceName = serviceName;
			this.serviceClass = serviceClass;
			this.deploymentData.activationTime.value = activationTime != null ?DateFormatter.formatDate(activationTime) : "";
			this.deploymentData.status = RunInstanceStatus.valueOf(status);
			this.accessPoint.runningInstanceInterfaces = accessPoint;
			this.specificData.root = specificData;
		}

		@XmlElement(name = "Description")
		public String description;

		@XmlElement(name = "Version")
		public String version;

		@XmlElement(name = "GHN")
		public GHN ghn = new GHN();

		public static class GHN {
			@XmlAttribute(name = "UniqueID")
			public String ghnId;
		}

		@XmlElement(name = "Service")
		public Service service = new Service();

		public static class Service {
			@XmlAttribute(name = "UniqueID")
			public String serviceId;
		}

		@XmlElement(name = "ServiceName")
		public String serviceName;

		@XmlElement(name = "ServiceClass")
		public String serviceClass;

		@XmlElement(name = "DeploymentData")
		public DeploymentData deploymentData = new DeploymentData();

		public static class DeploymentData {
			@XmlElement(name = "ActivationTime")
			public ActivationTime activationTime = new ActivationTime();

			public static class ActivationTime {
				@XmlAttribute(name = "value")
				public String value;
			}

			@XmlElement(name = "Status")
			public RunInstanceStatus status;
		}

		@XmlElement(name = "AccessPoint")
		public AccessPoint accessPoint = new AccessPoint();

		public static class AccessPoint {

			@XmlJavaTypeAdapter(MapAdapter.class)
			@XmlElement(name = "RunningInstanceInterfaces")
			public Map<String, URI> runningInstanceInterfaces = new HashMap<String, URI>();

			public static class MapAdapter extends XmlAdapter<MyMap, Map<String, URI>> {
				@Override
				public Map<String, URI> unmarshal(MyMap value) throws Exception {
					Map<String, URI> map = new HashMap<String, URI>();
					for (MapElements entry : value.entries) {
						map.put(entry.key, entry.value);
					}
					return map;
				}

				@Override
				public MyMap marshal(Map<String, URI> value) throws Exception {
					MyMap map = new MyMap();
					map.entries = new ArrayList<MapElements>();
					for (Entry<String, URI> entry : value.entrySet()) {
						map.entries.add(new MapElements(entry.getKey(), entry.getValue()));
					}
					return map;
				}
			}

			public static class MyMap {
				@XmlElement(name = "Endpoint")
				public List<MapElements> entries;
			}

			public static class MapElements {
				@XmlAttribute(name = "EntryName")
				public String key;
				@XmlValue
				public URI value;

				private MapElements() {
				} // Required by JAXB

				public MapElements(String key, URI value) {
					this.key = key;
					this.value = value;
				}
			}
		}

		@XmlElement(name = "SpecificData")
		public SpecificData specificData = new SpecificData();

		@XmlRootElement
		public static class SpecificData {
			@XmlAnyElement
			public Node root;
		}
	}
	
	
	public static class DateFormatter {
		private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") {
			private static final long serialVersionUID = 1L;
			public StringBuffer format(Date date, StringBuffer toAppendTo, java.text.FieldPosition pos) {
				StringBuffer toFix = super.format(date, toAppendTo, pos);
				return toFix.insert(toFix.length() - 2, ':');
			};
		};
		public static String formatDate(Date date) {
			return dateFormat.format(date).toString();
		}

		public static Date stringToDate(String dateString) throws ParseException {
			StringBuffer toFix = new StringBuffer(dateString);
			return dateFormat.parse(toFix.deleteCharAt(dateString.length() - 3).toString());
		}

	}

//	public static void main(String[] args) throws ParseException {
//		String str = RunInstance.DateFormatter.formatDate(new Date());
//		System.out.println(str);
//		
//		System.out.println(RunInstance.DateFormatter.stringToDate(str));
//	}

	//	public static void main(String[] args) throws JAXBException {
//		String str = "<RunInstance version=\"0.4.x\">\n   <ID>b459222e-4b0a-4b06-adcd-b9bbb421d131</ID>\n   <Type>RunningInstance</Type>\n   <Scopes>\n      <Scope>/gcube/devNext</Scope>\n      <Scope>/gcube</Scope>\n   </Scopes>\n   <Profile>\n      <Description>RESTful Version of ExecutionEngine</Description>\n      <Version>2.0.0-SNAPSHOT</Version>\n      <GHN UniqueID=\"02b014ea-4ed6-4703-ad97-d36981d40aa2\" />\n      <Service UniqueID=\"ExecutionEngineServiceExecution2.0.0-SNAPSHOT\" />\n      <ServiceName>ExecutionEngineService</ServiceName>\n      <ServiceClass>Execution</ServiceClass>\n      <DeploymentData>\n         <ActivationTime value=\"2014-04-28T15:19:42+02:00\" />\n         <Status>ready</Status>\n      </DeploymentData>\n      <AccessPoint>\n         <RunningInstanceInterfaces>\n            <Endpoint EntryName=\"ExecutionEngineService-remote-management\">http://dl14.di.uoa.gr:8080/executionengineservice-2.0.0-SNAPSHOT/gcube/resource</Endpoint>\n            <Endpoint EntryName=\"resteasy-servlet\">http://dl14.di.uoa.gr:8080/executionengineservice-2.0.0-SNAPSHOT</Endpoint>\n         </RunningInstanceInterfaces>\n      </AccessPoint>\n      <SpecificData>\n         <doc>\n            <element id=\"b638d900-54d6-4346-b667-a06304a742bc\">\n               <dynamic>\n                  <entry key=\"pe2ng.port\">4000</entry>\n                  <entry key=\"hostname\">dl14.di.uoa.gr</entry>\n               </dynamic>\n            </element>\n         </doc>\n      </SpecificData>\n   </Profile>\n</RunInstance>";
//
//		System.out.println(str + "\n\n");
//
//		RunInstance asd = XMLConverter.fromXML(str, RunInstance.class);
////		asd.getProfile().accessPoint.runningInstanceInterfaces.put("ExecutionEngineService-remote-management2", "http://dl14.di.uoa.gr:8080/executionengineservice-2.0.0-SNAPSHOT/gcube/resource2");
////		asd.getProfile().accessPoint.runningInstanceInterfaces.put("resteasy-servlet2", "http://dl14.di.uoa.gr:8080/executionengineservice-2.0.0-SNAPSHOT2");
//		System.out.println(XMLConverter.convertToXML(asd, true));
//	}
	
//	public static void main(String[] args) throws JAXBException, SAXException, IOException, ParserConfigurationException {
//		String str = "<Resource version=\"0.4.x\">\n" + 
//				"	<ID>3e8597fa-7dbf-4a43-b036-52f3f1788494</ID>\n" + 
//				"	<Type>RunningInstance</Type>\n" + 
//				"	<Scopes>\n" + 
//				"		<Scope>/d4science.research-infrastructures.eu</Scope>\n" + 
//				"		<Scope>/d4science.research-infrastructures.eu/FARM</Scope>\n" + 
//				"	</Scopes>\n" + 
//				"	<Profile>\n" + 
//				"		<Description>RESTful Version of ExecutionEngine</Description>\n" + 
//				"		<Version>2.0.0-SNAPSHOT</Version>\n" + 
//				"		<GHN UniqueID=\"80fb42f5-3089-41d8-8728-ee4b6fc1f0ae\" />\n" + 
//				"		<Service UniqueID=\"ExecutionEngineServiceExecution2.0.0-SNAPSHOT\" />\n" + 
//				"		<ServiceName>ExecutionEngineService</ServiceName>\n" + 
//				"		<ServiceClass>Execution</ServiceClass>\n" + 
//				"		<DeploymentData>\n" + 
//				"			<ActivationTime value=\"2014-05-27T09:10:55+02:00\" />\n" + 
//				"			<Status>ready</Status>\n" + 
//				"		</DeploymentData>\n" + 
//				"		<AccessPoint>\n" + 
//				"			<RunningInstanceInterfaces>\n" + 
//				"				<Endpoint EntryName=\"ExecutionEngineService-remote-management\">http://dl17.di.uoa.gr:8080/executionengineservice-2.0.1-3.1.0/gcube/resource</Endpoint>\n" + 
//				"				<Endpoint EntryName=\"resteasy-servlet\">http://dl17.di.uoa.gr:8080/executionengineservice-2.0.1-3.1.0</Endpoint>\n" + 
//				"			</RunningInstanceInterfaces>\n" + 
//				"		</AccessPoint>\n" + 
//				"		<SpecificData>\n" + 
//				"			<doc>\n" + 
//				"				<element id=\"26c2a9a6-0880-409b-9e42-0fe0ef6cd22e\">\n" + 
//				"					<dynamic>\n" + 
//				"						<entry key=\"pe2ng.port\">4000</entry>\n" + 
//				"						<entry key=\"hostname\">dl17.di.uoa.gr</entry>\n" + 
//				"					</dynamic>\n" + 
//				"				</element>\n" + 
//				"			</doc>\n" + 
//				"		</SpecificData>\n" + 
//				"	</Profile>\n" + 
//				"</Resource>";
//		Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(str.getBytes())).getDocumentElement();
//		node.getOwnerDocument().renameNode(node, null, "RunInstance");
//
//		System.out.println(XMLConverter.nodeToString(node));
//		
////		System.out.println(str);
////		
////		String str2 = XMLConverter.convertToXML(XMLConverter.fromXML(str, RunInstance.class), true);
////		System.out.println(str2);
////		
////		System.out.println(str.equals(str2));
//	}
}
