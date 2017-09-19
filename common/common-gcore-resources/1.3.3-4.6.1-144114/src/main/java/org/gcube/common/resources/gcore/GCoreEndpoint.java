package org.gcube.common.resources.gcore;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.gcube.common.resources.gcore.common.AnyWrapper;
import org.gcube.common.resources.gcore.common.DateWrapper;
import org.gcube.common.resources.gcore.common.GHNReference;
import org.gcube.common.resources.gcore.common.Identity;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.common.resources.gcore.common.ServiceReference;
import org.gcube.common.resources.gcore.utils.Group;
import org.w3c.dom.Element;

/**
 * Describes endpoints of gCore services.
 * 
 */
@XmlRootElement(name = "Resource")
@XmlType(propOrder={"profile"})
public class GCoreEndpoint extends Resource {

	public GCoreEndpoint() {
		this.type(Type.GCOREENDPOINT);
	}
	
	@XmlElementRef
	private Profile profile = new Profile();

	public Profile profile() {
		return profile;
	};

	@XmlType (propOrder = { "description", "version", "ghn", "service",
			"serviceName", "serviceClass", "security", "platform",
			"deploymentData", "functions", "accessPoint",
			"specificData", "accountings" })
	@XmlRootElement(name="Profile")
	public static class Profile {

		@XmlElement(name = "Description")
		private String description;

		@XmlElement(name = "Version")
		private String version;

		@XmlElement(name = "GHN")
		private GHNReference ghn;

		@XmlElement(name = "Service")
		private ServiceReference service;

		@XmlElement(name = "ServiceName")
		private String serviceName;

		@XmlElement(name = "ServiceClass")
		private String serviceClass;

		@XmlElementRef
		@XmlElementWrapper(name = "RunningInstanceSecurity")
		private List<Security> security = new ArrayList<Security>();

		@XmlElementRef
		private Platform platform;

		@XmlElementRef
		private DeploymentData deploymentData;

		@XmlElementWrapper(name = "RIEquivalenceFunctions")
		@XmlElementRef
		private List<Function> functions = new ArrayList<Function>();

		@XmlElementRef
		private AccessPoint accessPoint = new AccessPoint();

		@XmlElementRef
		private SpecificData specificData = new SpecificData();

		@XmlElementWrapper(name = "Accounting")
		@XmlElementRef
		private List<ScopedAccounting> accountings = new ArrayList<ScopedAccounting>();
		
		//before serialisation, we null the optional fields
	   void beforeMarshal(Marshaller marshaller) {
	    	if (security!=null && security.isEmpty())
	    		security=null;
	    	if (functions!=null && functions.isEmpty())
	    		functions=null;
	    	if (accountings!=null && accountings.isEmpty())
	    		accountings=null;
	    	if (accessPoint!=null && accessPoint.endpoints.isEmpty())
	    		accessPoint=null;
	    	if (specificData!=null && specificData().getChildNodes().getLength()==0)
	    		specificData=null;
	    }
	    
	    //after serialisation, we reinitialise them
	    void afterMarshal(Marshaller marshaller) {
	    	if (security==null)
	    		security=new ArrayList<Security>();
	    	if (functions==null)
	    		functions=new ArrayList<Function>();
	    	if (accountings==null)
	    		accountings=new ArrayList<ScopedAccounting>();
	    	if (specificData==null)
	    		specificData=new SpecificData();
	    	if (accessPoint==null)
	    		accessPoint = new AccessPoint();
	    }

		public String description() {
			return description;
		}
		
		public Profile description(String description) {
			this.description = description;
			return this;
		}

		public String version() {
			return version;
		}
		
		public Profile version(String version) {
			this.version = version;
			return this;
		}

		public String ghnId() {
			return ghn==null?null:ghn.id;
		}
		
		public Profile ghnId(String id) {
			ghn=new GHNReference();
			ghn.id=id;
			return this;
		}

		public String serviceId() {
			return service==null?null:service.id;
		}
		
		public Profile serviceId(String id) {
			service = new ServiceReference();
			service.id=id;
			return this;
		}

		public String serviceName() {
			return serviceName;
		}
		
		public Profile serviceName(String serviceName) {
			this.serviceName = serviceName;
			return this;
		}

		public String serviceClass() {
			return serviceClass;
		}
		
		public Profile serviceClass(String serviceClass) {
			this.serviceClass = serviceClass;
			return this;
		}

		public Group<Security> security() {
			return new Group<Security>(security,Security.class);
		}
		
		public Map<String, Security> securityMap(){
			Map<String, Security> map=new HashMap<String, Security>();
			for (Security s: security){
				if(s.name()!=null)
			       map.put(s.name(),s);
			}
			return map;
		}

		public DeploymentData deploymentData() {
			return deploymentData;
		}
		
		public DeploymentData newDeploymentData() {
			return deploymentData = new DeploymentData();
		}

		public Group<Function> functions() {
			return new Group<Function>(functions,Function.class);
		}

		public Map<String, Function> functionMap(){
			Map<String, Function> map=new HashMap<String, Function>();
			for (Function f: functions){
				if(f.name()!=null)
			       map.put(f.name(),f);
			}
			return map;
		}
		
		public Platform platform() {
			return platform;
		}
		
		public boolean hasPlatform() {
			return platform!=null;
		}
		
		public Platform newPlatform() {
			return platform = new Platform();
		}
		
		public Group<Endpoint> endpoints() {
			return new Group<Endpoint>(accessPoint.endpoints,Endpoint.class);
		}

		public Map<String, Endpoint> endpointMap(){
			Map<String, Endpoint> map=new HashMap<String, Endpoint>();
			for (Endpoint e: accessPoint.endpoints){
				if(e.name()!=null)
			       map.put(e.name(),e);
			}
			return map;
		}
		
		public Element specificData() {
			return specificData.root();
		}

		public Group<ScopedAccounting> accountings() {
			return new Group<ScopedAccounting>(accountings,ScopedAccounting.class);
		}
		
		
		public Map<String, ScopedAccounting> accountingsMap(){
			Map<String, ScopedAccounting> map=new HashMap<String, ScopedAccounting>();
			for (ScopedAccounting sa: accountings){
				if(sa.scope()!=null)
			       map.put(sa.scope(),sa);
			}
			return map;
		}
		

		@Override
		public String toString() {
			return "[description=" + description + ", version=" + version
					+ ", ghn=" + ghn + ", service=" + service
					+ ", serviceName=" + serviceName + ", serviceClass="
					+ serviceClass + ", runningInstanceInterfaces=" + security
					+ ", platform=" + platform + ", deploymentData="
					+ deploymentData + ", functions=" + functions
					+ ", endpoints=" + accessPoint.endpoints + ", specificData="
					+ specificData() + ", accountings=" + accountings + "]";
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((accessPoint == null) ? 0 : accessPoint.hashCode());
			result = prime * result + ((accountings == null) ? 0 : accountings.hashCode());
			result = prime * result + ((deploymentData == null) ? 0 : deploymentData.hashCode());
			result = prime * result + ((description == null) ? 0 : description.hashCode());
			result = prime * result + ((functions == null) ? 0 : functions.hashCode());
			result = prime * result + ((ghn == null) ? 0 : ghn.hashCode());
			result = prime * result + ((platform == null) ? 0 : platform.hashCode());
			result = prime * result + ((security == null) ? 0 : security.hashCode());
			result = prime * result + ((service == null) ? 0 : service.hashCode());
			result = prime * result + ((serviceClass == null) ? 0 : serviceClass.hashCode());
			result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
			result = prime * result + ((specificData == null) ? 0 : specificData.hashCode());
			result = prime * result + ((version == null) ? 0 : version.hashCode());
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
			Profile other = (Profile) obj;
			if (accessPoint == null) {
				if (other.accessPoint != null)
					return false;
			} else if (!accessPoint.equals(other.accessPoint))
				return false;
			if (accountings == null) {
				if (other.accountings != null)
					return false;
			} else if (!accountings.equals(other.accountings))
				return false;
			if (deploymentData == null) {
				if (other.deploymentData != null)
					return false;
			} else if (!deploymentData.equals(other.deploymentData))
				return false;
			if (description == null) {
				if (other.description != null)
					return false;
			} else if (!description.equals(other.description))
				return false;
			if (functions == null) {
				if (other.functions != null)
					return false;
			} else if (!functions.equals(other.functions))
				return false;
			if (ghn == null) {
				if (other.ghn != null)
					return false;
			} else if (!ghn.equals(other.ghn))
				return false;
			if (platform == null) {
				if (other.platform != null)
					return false;
			} else if (!platform.equals(other.platform))
				return false;
			if (security == null) {
				if (other.security != null)
					return false;
			} else if (!security.equals(other.security))
				return false;
			if (service == null) {
				if (other.service != null)
					return false;
			} else if (!service.equals(other.service))
				return false;
			if (serviceClass == null) {
				if (other.serviceClass != null)
					return false;
			} else if (!serviceClass.equals(other.serviceClass))
				return false;
			if (serviceName == null) {
				if (other.serviceName != null)
					return false;
			} else if (!serviceName.equals(other.serviceName))
				return false;
			if (specificData == null) {
				if (other.specificData != null)
					return false;
			} else if (!specificData.equals(other.specificData))
				return false;
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (!version.equals(other.version))
				return false;
			return true;
		}


		@XmlRootElement(name="SpecificData")
		public static class SpecificData extends AnyWrapper{}


		@XmlType(propOrder={"identity"})
		@XmlRootElement(name = "RunningInstanceInterface")
		public static class Security {

			@XmlElementRef
			private Identity identity;

			@XmlAttribute(name = "EntryName")
			private String name;


			public boolean hasIdentity() {
				return identity!=null;
			}
			
			public Identity newIdentity() {
				return identity = new Identity();
			}

			public String name() {
				return name;
			}
			
			public Security name(String name) {
				this.name = name;
				return this;
			}

			@Override
			public String toString() {
				return "Security [identity=" + identity + ", name="
						+ name + "]";
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((name == null) ? 0 : name.hashCode());
				result = prime * result + ((identity == null) ? 0 : identity.hashCode());
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
				Security other = (Security) obj;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				if (identity == null) {
					if (other.identity != null)
						return false;
				} else if (!identity.equals(other.identity))
					return false;
				return true;
			}
			
			

		}

		@XmlType(propOrder={"name","path","activationTime","terminationTime","status","statusMsg","plugins"})
		@XmlRootElement(name = "DeploymentData")
		public static class DeploymentData {

			@XmlElement(name = "InstanceName")
			private String name;

			@XmlElement(name = "LocalPath")
			private String path;

			@XmlElement(name = "ActivationTime")
			private DateWrapper activationTime = new DateWrapper();

			@XmlElement(name = "TerminationTime")
			private DateWrapper terminationTime = new DateWrapper();

			@XmlElement(name = "Status")
			private String status;

			@XmlElement(name = "MessageState")
			private String statusMsg;

			@XmlElementWrapper(name = "AvailablePlugins")
			@XmlElement(name = "Plugin")
			private List<Plugin> plugins = new ArrayList<Plugin>();

			//before serialisation, we null the optional fields
		    void beforeMarshal(Marshaller marshaller) {
		    	if (terminationTime!=null && terminationTime.value==null)
		    		terminationTime=null;
		    	if (plugins!=null && plugins.isEmpty())
		    		plugins=null;
		    }
		    
		    //after serialisation, we reinitialise them
		    void afterMarshal(Marshaller marshaller) {
		    	if (terminationTime==null)
		    		terminationTime=new DateWrapper();
		    	if (plugins==null)
		    		plugins=new ArrayList<Plugin>();
		    }
		    
			public String name() {
				return name;
			}
			
			public DeploymentData name(String name) {
				this.name = name;
				return this;
			}

			public String path() {
				return path;
			}
			
			public DeploymentData path(String path) {
				this.path = path;
				return this;
			}

			public Calendar activationTime() {
				return activationTime.value;
			}
			
			public DeploymentData activationTime(Calendar activationTime) {
				this.activationTime.value=activationTime;
				return this;
			}

			public Calendar terminationTime() {
				return terminationTime.value;
			}
			
			public DeploymentData terminationTime(Calendar terminationTime) {
				this.terminationTime.value=terminationTime;
				return this;
			}

			public String status() {
				return status;
			}
			
			public DeploymentData status(String status) {
				this.status = status;
				return this;
			}

			public String statusMessage() {
				return statusMsg;
			}
			
			public DeploymentData statusMessage(String statusMsg) {
				this.statusMsg = statusMsg;
				return this;
			}

			public Group<Plugin> plugins() {
				return new Group<Plugin>(plugins,Plugin.class);
			}
			

			@Override
			public String toString() {
				return "[instanceName=" + name + ", localPath=" + path
						+ ", activationTime=" + activationTime
						+ ", terminationTime=" + terminationTime + ", status="
						+ status + ", messageState=" + statusMsg + ", plugins="
						+ plugins + "]";
			}
			
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((activationTime == null) ? 0 : activationTime.hashCode());
				result = prime * result + ((name == null) ? 0 : name.hashCode());
				result = prime * result + ((path == null) ? 0 : path.hashCode());
				result = prime * result + ((plugins == null) ? 0 : plugins.hashCode());
				result = prime * result + ((statusMsg == null) ? 0 : statusMsg.hashCode());
				result = prime * result + ((status == null) ? 0 : status.hashCode());
				result = prime * result + ((terminationTime == null) ? 0 : terminationTime.hashCode());
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
				DeploymentData other = (DeploymentData) obj;
				if (activationTime == null) {
					if (other.activationTime != null)
						return false;
				} else if (!activationTime.equals(other.activationTime))
					return false;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				if (path == null) {
					if (other.path != null)
						return false;
				} else if (!path.equals(other.path))
					return false;
				if (plugins == null) {
					if (other.plugins != null)
						return false;
				} else if (!plugins.equals(other.plugins))
					return false;
				if (statusMsg == null) {
					if (other.statusMsg != null)
						return false;
				} else if (!statusMsg.equals(other.statusMsg))
					return false;
				if (status == null) {
					if (other.status != null)
						return false;
				} else if (!status.equals(other.status))
					return false;
				if (terminationTime == null) {
					if (other.terminationTime != null)
						return false;
				} else if (!terminationTime.equals(other.terminationTime))
					return false;
				return true;
			}



			@XmlType(propOrder={"service","package_","version"})
			public static class Plugin {

				@XmlElement(name = "Service")
				private Service service = new Service();

				@XmlElement(name = "Package")
				private String package_;

				@XmlElement(name = "Version")
				private String version;

				public Service service() {
					return service;
				}
				
				public Plugin service(Service service) {
					this.service = service;
					return this;
				}
				
				public Plugin service(String serviceClass,String serviceName,String version) {
					this.service.clazz =serviceClass;
					this.service.name=serviceName;
					this.service.version=version;
					return this;
				}

				public String pluginPackage() {
					return package_;
				}
				
				public Plugin pluginPackage(String pluginPackage) {
					this.package_=pluginPackage;
					return this;

				}

				public String version() {
					return version;
				}
				
				public Plugin version(String version) {
					this.version = version;
					return this;
				}

				@Override
				public String toString() {
					return "[service=" + service + ", package_=" + package_
							+ ", version=" + version + "]";
				}
				
				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((package_ == null) ? 0 : package_.hashCode());
					result = prime * result + ((service == null) ? 0 : service.hashCode());
					result = prime * result + ((version == null) ? 0 : version.hashCode());
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
					Plugin other = (Plugin) obj;
					if (package_ == null) {
						if (other.package_ != null)
							return false;
					} else if (!package_.equals(other.package_))
						return false;
					if (service == null) {
						if (other.service != null)
							return false;
					} else if (!service.equals(other.service))
						return false;
					if (version == null) {
						if (other.version != null)
							return false;
					} else if (!version.equals(other.version))
						return false;
					return true;
				}



				public static class Service {

					@XmlElement(name = "Class")
					private String clazz;

					@XmlElement(name = "Name")
					private String name;

					@XmlElement(name = "Version")
					private String version;

					public String serviceClass() {
						return clazz;
					}

					public String serviceName() {
						return name;
					}

					public String version() {
						return version;
					}

					@Override
					public String toString() {
						return "[class=" + clazz + ", name=" + name
								+ ", version=" + version + "]";
					}

					@Override
					public int hashCode() {
						final int prime = 31;
						int result = 1;
						result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
						result = prime * result + ((name == null) ? 0 : name.hashCode());
						result = prime * result + ((version == null) ? 0 : version.hashCode());
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
						Service other = (Service) obj;
						if (clazz == null) {
							if (other.clazz != null)
								return false;
						} else if (!clazz.equals(other.clazz))
							return false;
						if (name == null) {
							if (other.name != null)
								return false;
						} else if (!name.equals(other.name))
							return false;
						if (version == null) {
							if (other.version != null)
								return false;
						} else if (!version.equals(other.version))
							return false;
						return true;
					}

					
				}
			}

		}

		@XmlType(propOrder={"name","parameters"})
		@XmlRootElement(name = "Function")
		public static class Function {

			@XmlElement(name = "Name")
			private String name;

			@XmlElementWrapper(name = "ActualParameters")
			@XmlElement(name = "Param")
			private List<Parameter> parameters = new ArrayList<Parameter>();

			public String name() {
				return name;
			}
			
			public Function name(String name) {
				this.name = name;
				return this;
			}

			public Group<Parameter> parameters() {
				return new Group<Parameter>(parameters,Parameter.class);
			}

			public Map<String, Parameter> parametersMap(){
				Map<String, Parameter> map=new HashMap<String, Parameter>();
				for (Parameter p: parameters){
					if(p.name()!=null)
				       map.put(p.name(),p);
				}
				return map;
			}
			
			@Override
			public String toString() {
				return "[name=" + name + ", parameters=" + parameters + "]";
			}
			

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((name == null) ? 0 : name.hashCode());
				result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
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
				Function other = (Function) obj;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				if (parameters == null) {
					if (other.parameters != null)
						return false;
				} else if (!parameters.equals(other.parameters))
					return false;
				return true;
			}
			

			@XmlType(propOrder={"name","values"})
			public static class Parameter {

				@XmlElement(name = "Name")
				String name;

				@XmlElement(name = "Value")
				List<String> values = new ArrayList<String>();

				public String name() {
					return name;
				}

				public List<String> values() {
					return values;
				}
				
				public void nameAndValues(String name,String value, String ... values) {
					this.name=name;
					this.values.add(value);
					this.values.addAll(Arrays.asList(values));
				}

				@Override
				public String toString() {
					return "[name=" + name + ", values=" + values + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((name == null) ? 0 : name.hashCode());
					result = prime * result + ((values == null) ? 0 : values.hashCode());
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
					Parameter other = (Parameter) obj;
					if (name == null) {
						if (other.name != null)
							return false;
					} else if (!name.equals(other.name))
						return false;
					if (values == null) {
						if (other.values != null)
							return false;
					} else if (!values.equals(other.values))
						return false;
					return true;
				}
				
				

			}
		}

		@XmlRootElement(name = "AccessPoint")
		public static class AccessPoint {

			@XmlElementWrapper(name = "RunningInstanceInterfaces")
			@XmlElement(name = "Endpoint")
			private Set<Endpoint> endpoints = new LinkedHashSet<Endpoint>();

			@Override
			public String toString() {
				return "[endpoints=" + endpoints + "]";
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((endpoints == null) ? 0 : endpoints.hashCode());
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
				AccessPoint other = (AccessPoint) obj;
				if (endpoints == null) {
					if (other.endpoints != null)
						return false;
				} else if (!endpoints.equals(other.endpoints))
					return false;
				return true;
			}
			
		}

		public static class Endpoint {

			@XmlAttribute(name = "EntryName")
			private String name;

			@XmlValue
			private URI uri;

			public String name() {
				return name;
			}
			
			public Endpoint nameAndAddress(String name,URI uri) {
				this.name = name;
				this.uri=uri;
				return this;
			}

			public URI uri() {
				return uri;
			}

			@Override
			public String toString() {
				return "Endpoint [name=" + name + ", uri=" + uri + "]";
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((name == null) ? 0 : name.hashCode());
				result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
				Endpoint other = (Endpoint) obj;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				if (uri == null) {
					if (other.uri != null)
						return false;
				} else if (!uri.equals(other.uri))
					return false;
				return true;
			}
			
			

		}

		@XmlType(propOrder={"incomingCalls","averageIncomingCalls","averageInvocationTime","topCallerGHN"})
		@XmlRootElement(name = "ScopedAccounting")
		public static class ScopedAccounting {

			@XmlAttribute
			private String scope;
			
			@XmlElement(name = "TotalINCalls")
			private long incomingCalls;

			@XmlElement(name = "AverageINCalls")
			private List<AverageType> averageIncomingCalls = new ArrayList<AverageType>();

			@XmlElement(name = "AverageInvocationTime")
			private List<AverageType> averageInvocationTime = new ArrayList<AverageType>();

			@XmlElement(name = "TopCallerGHN")
			private TopCaller topCallerGHN;

			public String scope() {
				return scope;
			}
			
			public ScopedAccounting scope(String scope) {
				this.scope = scope;
				return this;
			}
			
			public long incomingCalls() {
				return incomingCalls;
			}
			
			public ScopedAccounting incomingCalls(long incomingCalls) {
				this.incomingCalls = incomingCalls;
				return this;
			}

			public Group<AverageType> averageInCalls() {
				return new Group<AverageType>(averageIncomingCalls,AverageType.class);
			}

			public Group<AverageType> averageInvocationTime() {
				return new Group<AverageType>(averageInvocationTime,AverageType.class);
			}

			public TopCaller topCaller() {
				return topCallerGHN;
			}
			
			public TopCaller newTopCaller() {
				return topCallerGHN = new TopCaller();
			}
			

			@Override
			public String toString() {
				return "ScopedAccounting [scope="+scope+", incomingCalls=" + incomingCalls
						+ ", averageincomingCalls=" + averageIncomingCalls
						+ ", averageInvocationTime=" + averageInvocationTime
						+ ", topCaller=" + topCallerGHN + "]";
			}
			
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((averageIncomingCalls == null) ? 0 : averageIncomingCalls.hashCode());
				result = prime * result + ((averageInvocationTime == null) ? 0 : averageInvocationTime.hashCode());
				result = prime * result + (int) (incomingCalls ^ (incomingCalls >>> 32));
				result = prime * result + ((scope == null) ? 0 : scope.hashCode());
				result = prime * result + ((topCallerGHN == null) ? 0 : topCallerGHN.hashCode());
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
				ScopedAccounting other = (ScopedAccounting) obj;
				if (averageIncomingCalls == null) {
					if (other.averageIncomingCalls != null)
						return false;
				} else if (!averageIncomingCalls.equals(other.averageIncomingCalls))
					return false;
				if (averageInvocationTime == null) {
					if (other.averageInvocationTime != null)
						return false;
				} else if (!averageInvocationTime.equals(other.averageInvocationTime))
					return false;
				if (incomingCalls != other.incomingCalls)
					return false;
				if (scope == null) {
					if (other.scope != null)
						return false;
				} else if (!scope.equals(other.scope))
					return false;
				if (topCallerGHN == null) {
					if (other.topCallerGHN != null)
						return false;
				} else if (!topCallerGHN.equals(other.topCallerGHN))
					return false;
				return true;
			}



			public static class AverageType {

				@XmlAttribute(name = "interval")
				private Long interval;

				@XmlAttribute(name = "average")
				private Double average;

				public Long interval() {
					return interval;
				}

				public Double average() {
					return average;
				}
				
				public void intervalAndAverage(Long interval, Double average) {
					this.interval=interval;
					this.average=average;
				}

				@Override
				public String toString() {
					return "[interval=" + interval + ", average=" + average
							+ "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((average == null) ? 0 : average.hashCode());
					result = prime * result + ((interval == null) ? 0 : interval.hashCode());
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
					AverageType other = (AverageType) obj;
					if (average == null) {
						if (other.average != null)
							return false;
					} else if (!average.equals(other.average))
						return false;
					if (interval == null) {
						if (other.interval != null)
							return false;
					} else if (!interval.equals(other.interval))
						return false;
					return true;
				}
				
				

			}

			public static class TopCaller {

				@XmlElement(name = "GHNName")
				private String name;

				@XmlAttribute(name = "avgHourlyCalls")
				private Double avgHourlyCalls;

				@XmlAttribute(name = "avgDailyCalls")
				private Double avgDailyCalls;

				@XmlAttribute(name = "totalCalls")
				private Long totalCalls;

				public String name() {
					return name;
				}
				
				public TopCaller name(String name) {
					this.name = name;
					return this;
				}
				
				public TopCaller calls(Double hourlyAverage,Double dailyAverage,Long total) {
					this.totalCalls=total;
					this.avgDailyCalls = dailyAverage;
					this.avgHourlyCalls = hourlyAverage;
					return this;
				}

				public Double avgHourlyCalls() {
					return avgHourlyCalls;
				}

				public Double avgDailyCalls() {
					return avgDailyCalls;
				}

				public Long totalCalls() {
					return totalCalls;
				}

				@Override
				public String toString() {
					return "[ghnName=" + name + ", avgHourlyCalls="
							+ avgHourlyCalls + ", avgDailyCalls="
							+ avgDailyCalls + ", totalCalls=" + totalCalls
							+ "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((avgDailyCalls == null) ? 0 : avgDailyCalls.hashCode());
					result = prime * result + ((avgHourlyCalls == null) ? 0 : avgHourlyCalls.hashCode());
					result = prime * result + ((name == null) ? 0 : name.hashCode());
					result = prime * result + ((totalCalls == null) ? 0 : totalCalls.hashCode());
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
					TopCaller other = (TopCaller) obj;
					if (avgDailyCalls == null) {
						if (other.avgDailyCalls != null)
							return false;
					} else if (!avgDailyCalls.equals(other.avgDailyCalls))
						return false;
					if (avgHourlyCalls == null) {
						if (other.avgHourlyCalls != null)
							return false;
					} else if (!avgHourlyCalls.equals(other.avgHourlyCalls))
						return false;
					if (name == null) {
						if (other.name != null)
							return false;
					} else if (!name.equals(other.name))
						return false;
					if (totalCalls == null) {
						if (other.totalCalls != null)
							return false;
					} else if (!totalCalls.equals(other.totalCalls))
						return false;
					return true;
				}
				

			}

		}

	}
}
