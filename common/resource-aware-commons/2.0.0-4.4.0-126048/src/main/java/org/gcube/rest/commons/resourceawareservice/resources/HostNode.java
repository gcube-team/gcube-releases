package org.gcube.rest.commons.resourceawareservice.resources;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.helpers.XPathEvaluator;

@XmlRootElement(name = "HostNode")
public class HostNode extends GeneralResource {
	private String id;
	private List<String> scopes;
	private Profile profile;
	private XPathEvaluator evaluator;
	
	private HostNode() {
	}
	/**
	 * @param id
	 * @param scope
	 * @param profile
	 * @throws ParserConfigurationException 
	 * @throws JAXBException 
	 */
	public HostNode(String id, List<String> scope, Profile profile) {
		super();
		this.id = id;
		this.scopes = scope;
		this.profile = profile;
		try {
			this.evaluator = new XPathEvaluator(XMLConverter.convertToXMLNode(profile));
		} catch (JAXBException | ParserConfigurationException e) {
			e.printStackTrace();
		}
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
	 * @return the scope
	 */
	@XmlElement(name = "Scope")
	public List<String> getScopes() {
		return scopes;
	}

	/**
	 * @param scope
	 *            the scope to set
	 */
	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	@XmlElement(name = "Profile")
	public Profile getProfile() {
		return profile;
	}
	
	public List<String> evaluate(String expression) {
		return evaluator.evaluate(expression);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((profile == null) ? 0 : profile.hashCode());
		result = prime * result + ((scopes == null) ? 0 : scopes.hashCode());
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
		HostNode other = (HostNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (profile == null) {
			if (other.profile != null)
				return false;
		} else if (!profile.equals(other.profile))
			return false;
		if (scopes == null) {
			if (other.scopes != null)
				return false;
		} else if (!scopes.equals(other.scopes))
			return false;
		return true;
	}


	@XmlRootElement(name = "Profile")
	public static class Profile {
		@XmlElement(name = "Infrastructure")
		private String infrastructure;

		@XmlElementRef
		private NodeDescription ghn;

		@XmlElementRef
		private Site site;

		@XmlElementWrapper(name = "DeployedPackages")
		@XmlElementRef
		private Set<DeployedPackage> packages;

		// before serialisation, we null the optional fields
		void beforeMarshal(Marshaller marshaller) {
			if (packages!=null && packages.isEmpty())
				packages = null;
		}

		// after serialisation, we reinitialise them
		void afterMarshal(Marshaller marshaller) {
			if (packages == null)
				packages = new LinkedHashSet<DeployedPackage>();
		}

		public String infrastructure() {
			return infrastructure;
		}

		public Profile infrastructure(String infrastructure) {
			this.infrastructure = infrastructure;
			return this;
		}

		public NodeDescription description() {
			return ghn;
		}

		public NodeDescription newDescription() {
			return ghn = new NodeDescription();
		}

		public Site site() {
			return site;
		}

		public Site newSite() {
			return site = new Site();
		}

		public Set<DeployedPackage> packages() {
			return packages;
		}

		public Set<DeployedPackage> newPackages() {
			return packages = new LinkedHashSet<DeployedPackage>();
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((ghn == null) ? 0 : ghn.hashCode());
			result = prime * result + ((infrastructure == null) ? 0 : infrastructure.hashCode());
			result = prime * result + ((packages == null) ? 0 : packages.hashCode());
			result = prime * result + ((site == null) ? 0 : site.hashCode());
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
			if (ghn == null) {
				if (other.ghn != null)
					return false;
			} else if (!ghn.equals(other.ghn))
				return false;
			if (infrastructure == null) {
				if (other.infrastructure != null)
					return false;
			} else if (!infrastructure.equals(other.infrastructure))
				return false;
			if (packages == null) {
				if (other.packages != null)
					return false;
			} else if (!packages.equals(other.packages))
				return false;
			if (site == null) {
				if (other.site != null)
					return false;
			} else if (!site.equals(other.site))
				return false;
			return true;
		}

		@XmlRootElement(name = "GHNDescription")
		public static class NodeDescription {

			@XmlElement(name = "Name")
			private String name;

			@XmlElement(name = "ActivationTime")
			private Calendar activationTime;

			@XmlElement(name = "Status")
			private String status;

			@XmlElement(name = "StatusMessage")
			private String statusMessage;

			@XmlElement(name = "Type")
			private GHNType ghnType = GHNType.Dynamic;

//			@XmlElementWrapper(name = "AvailablePlatforms")
//			@XmlElementRef
//			private Set<Platform> platforms = new LinkedHashSet<Platform>();

			@XmlElement(name = "SecurityEnabled")
			private Boolean securityEnabled = new Boolean(false);

			@XmlElementRef
			private Security securityData;

			@XmlElementRef
			private Architecture architecture;

			@XmlElementRef
			private OperatingSystem operatingSystem;

			@XmlElementRef
			private Set<Processor> processors;

			@XmlElementRef
			private Set<NetworkAdapter> networkAdapters;

			@XmlElementRef
			private Benchmark benchmark;

			@XmlElementWrapper(name = "RunTimeEnv")
			@XmlElementRef
			private Set<Variable> runtimeEnvironment;

			@XmlElementRef
			private Set<StorageDevice> storageDevices = new LinkedHashSet<StorageDevice>();

			@XmlElementRef
			private Set<StoragePartition> storagePartitions = new LinkedHashSet<StoragePartition>();

			@XmlElementRef
			private Set<LocalFileSystem> localFileSystems = new LinkedHashSet<LocalFileSystem>();

			@XmlElementRef
			private Set<RemoteFileSystem> remoteFileSystems = new LinkedHashSet<RemoteFileSystem>();

			@XmlElementRef
			private Set<DevicePartition> devicePartitionMap = new LinkedHashSet<DevicePartition>();

			@XmlElementRef
			private Set<FileSystemPartition> partitionFilesystemMap = new LinkedHashSet<FileSystemPartition>();

			@XmlElement(name = "Uptime")
			private String uptime;

			@XmlElementRef
			private Load load;

			@XmlElementRef
			private HistoricalLoad historicalLoad;

			@XmlElementRef
			private MainMemory mainMemory;

			@XmlElement(name = "LocalAvailableSpace")
			private Integer localAvailableStorage;

			@XmlElement(name = "LastUpdate")
			private Calendar lastUpdate;

//			// before serialisation, we null the optional fields
//			void beforeMarshal(Marshaller marshaller) {
//				if (platforms!=null && platforms.isEmpty())
//					platforms = null;
//				if (runtimeEnvironment!=null &&  runtimeEnvironment.isEmpty())
//					runtimeEnvironment = null;
//			}

//			// after serialisation, we reinitialise them
//			void afterMarshal(Marshaller marshaller) {
//				if (platforms == null)
//					platforms = new LinkedHashSet<Platform>();
//				if (runtimeEnvironment == null)
//					runtimeEnvironment = new LinkedHashSet<Variable>();
//			}

			public String name() {
				return name;
			}

			public NodeDescription name(String name) {
				this.name = name;
				return this;
			}

			public Calendar activationTime() {
				return activationTime;
			}

			public NodeDescription activationTime(Calendar time) {
				this.activationTime = time;
				return this;
			}

			public String status() {
				return status;
			}

			public NodeDescription status(String status) {
				this.status = status;
				return this;
			}

			public String statusMessage() {
				return statusMessage;
			}

			public NodeDescription statusMessage(String message) {
				this.statusMessage = message;
				return this;
			}

			public GHNType type() {
				return ghnType;
			}

			public NodeDescription type(GHNType type) {
				this.ghnType = type;
				return this;
			}

//			public List<Platform> platforms() {
//				return new ArrayList<Platform>(platforms, Platform.class);
//			}
			
			public boolean isSecurityEnabled() {
				return securityEnabled.booleanValue();
			}

			public NodeDescription enableSecurity(boolean enabled) {
				securityEnabled = new Boolean(enabled);
				return this;
			}

			public Security security() {
				return securityData;
			}

			public Security newSecurity() {
				return securityData = new Security();
			}

			public Architecture architecture() {
				return architecture;
			}

			public Architecture newArchitecture() {
				return architecture = new Architecture();
			}

			public OperatingSystem operatingSystem() {
				return operatingSystem;
			}

			public OperatingSystem newOperatingSystem() {
				return operatingSystem = new OperatingSystem();
			}

			public Set<Processor> processors() {
				return processors;
			}

			public Set<Processor> newProcessors() {
				return processors = new HashSet<Processor>();
			}

			public Set<NetworkAdapter> networkAdapters() {
				return networkAdapters;
			}

			public Set<NetworkAdapter> newNetworkAdapters() {
				return networkAdapters = new HashSet<NetworkAdapter>();
			}

			public Benchmark benchmark() {
				return benchmark;
			}

			public Benchmark newBenchmark() {
				return benchmark = new Benchmark();
			}

			public List<Variable> environmentVariables() {
				return new ArrayList<Variable>();
			}
			
			public Map<String, Variable> variablesMap(){
				Map<String, Variable> map=new HashMap<String, Variable>();
				for (Variable v: runtimeEnvironment){
					if(v.key()!=null)
				       map.put(v.key(),v);
				}
				return map;
			}

			public List<StorageDevice> storageDevices() {
				return new ArrayList<StorageDevice>();
			}
			

			public List<StoragePartition> storagePartitions() {
				return new ArrayList<StoragePartition>();
			}
			

			public List<LocalFileSystem> localFileSystems() {
				return new ArrayList<LocalFileSystem>();
			}

			public List<RemoteFileSystem> remoteFileSystems() {
				return new ArrayList<RemoteFileSystem>();
			}

			public List<DevicePartition> devicePartitions() {
				return new ArrayList<DevicePartition>();
			}

			public List<FileSystemPartition> fileSystemPartitions() {
				return new ArrayList<FileSystemPartition>();
			}

			public String uptime() {
				return uptime;
			}

			public NodeDescription uptime(String uptime) {
				this.uptime = uptime;
				return this;
			}

			public Load load() {
				return load;
			}
			
			public Load newLoad() {
				return load = new Load();
			}

			public HistoricalLoad historicalLoad() {
				return historicalLoad;
			}
			
			public HistoricalLoad newHistoricalLoad() {
				return historicalLoad = new HistoricalLoad();
			}

			public MainMemory mainMemory() {
				return mainMemory;
			}

			public MainMemory newMainMemory() {
				return this.mainMemory = new MainMemory();
			}

			public Integer localAvailableStorage() {
				return localAvailableStorage;
			}
			
			public NodeDescription localAvailableStorage(int amount) {
				this.localAvailableStorage=amount;
				return this;
			}

			public Calendar lastUpdate() {
				return lastUpdate;
			}

			public NodeDescription lastUpdate(Calendar time) {
				this.lastUpdate = time;
				return this;
			}

			/**
			 * http://stackoverflow.com/questions/5806923/jaxb-element-of-type- enum
			 **/
			@XmlType(name = "GHNType")
			@XmlEnum
			public enum GHNType {
				Dynamic, Static, SelfCleaning;

				public String value() {
					return toString();
				}

				public static GHNType fromValue(String v) {
					return valueOf(v);
				}
			}

			@XmlRootElement(name = "SecurityData")
			public static class Security {

				@XmlElement(name = "CA")
				private String certificationAuthority;

				@XmlElement(name = "CredentialsDistinguishedName")
				private String credentialDistinguishedName;

				@XmlElement(name = "CredentialsExpireOn")
				private Calendar credentialsExpirationDate;

				public String authority() {
					return certificationAuthority;
				}

				public Security authority(String authority) {
					this.certificationAuthority = authority;
					return this;
				}

				public String distinguishedName() {
					return credentialDistinguishedName;
				}

				public Security distinguishedName(String dn) {
					this.credentialDistinguishedName = dn;
					return this;
				}

				public Calendar expirationDate() {
					return credentialsExpirationDate;
				}

				public Security expirationDate(Calendar date) {
					this.credentialsExpirationDate = date;
					return this;
				}

				@Override
				public String toString() {
					return "SecurityData [certificationAuthority=" + certificationAuthority
							+ ", credentialDistinguishedName=" + credentialDistinguishedName
							+ ", credentialsExpirationDate=" + credentialsExpirationDate + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result
							+ ((certificationAuthority == null) ? 0 : certificationAuthority.hashCode());
					result = prime * result
							+ ((credentialDistinguishedName == null) ? 0 : credentialDistinguishedName.hashCode());
					result = prime * result
							+ ((credentialsExpirationDate == null) ? 0 : credentialsExpirationDate.hashCode());
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
					if (certificationAuthority == null) {
						if (other.certificationAuthority != null)
							return false;
					} else if (!certificationAuthority.equals(other.certificationAuthority))
						return false;
					if (credentialDistinguishedName == null) {
						if (other.credentialDistinguishedName != null)
							return false;
					} else if (!credentialDistinguishedName.equals(other.credentialDistinguishedName))
						return false;
					if (credentialsExpirationDate == null) {
						if (other.credentialsExpirationDate != null)
							return false;
					} else if (!credentialsExpirationDate.equals(other.credentialsExpirationDate))
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "Architecture")
			public static class Architecture {

				@XmlAttribute(name = "PlatformType")
				private String platformType;

				@XmlAttribute(name = "SMPSize")
				private int smpSize;

				@XmlAttribute(name = "SMTSize")
				private int smtSize;

				public String platformType() {
					return platformType;
				}

				public Architecture platformType(String type) {
					this.platformType = type;
					return this;
				}

				public int smpSize() {
					return smpSize;
				}

				public Architecture smpSize(int size) {
					this.smpSize = size;
					return this;
				}

				public int smtSize() {
					return smtSize;
				}

				public Architecture smtSize(int size) {
					this.smtSize = size;
					return this;
				}

				@Override
				public String toString() {
					return "Architecture [platformType=" + platformType + ", smpSize=" + smpSize + ", smtSize="
							+ smtSize + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((platformType == null) ? 0 : platformType.hashCode());
					result = prime * result + smpSize;
					result = prime * result + smtSize;
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
					Architecture other = (Architecture) obj;
					if (platformType == null) {
						if (other.platformType != null)
							return false;
					} else if (!platformType.equals(other.platformType))
						return false;
					if (smpSize != other.smpSize)
						return false;
					if (smtSize != other.smtSize)
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "OperatingSystem")
			public static class OperatingSystem {

				@XmlAttribute(name = "Name")
				private String name;

				@XmlAttribute(name = "Release")
				private String release;

				@XmlAttribute(name = "Version")
				private String version;

				public String name() {
					return name;
				}

				public OperatingSystem name(String name) {
					this.name = name;
					return this;
				}

				public String release() {
					return release;
				}

				public OperatingSystem release(String release) {
					this.release = release;
					return this;
				}

				public String version() {
					return version;
				}

				public OperatingSystem version(String version) {
					this.version = version;
					return this;
				}

				@Override
				public String toString() {
					return "OperatingSystem [name=" + name + ", release=" + release + ", version=" + version + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((name == null) ? 0 : name.hashCode());
					result = prime * result + ((release == null) ? 0 : release.hashCode());
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
					OperatingSystem other = (OperatingSystem) obj;
					if (name == null) {
						if (other.name != null)
							return false;
					} else if (!name.equals(other.name))
						return false;
					if (release == null) {
						if (other.release != null)
							return false;
					} else if (!release.equals(other.release))
						return false;
					if (version == null) {
						if (other.version != null)
							return false;
					} else if (!version.equals(other.version))
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "Processor")
			public static class Processor {

				@XmlAttribute(name = "Vendor")
				private String vendor;
				@XmlAttribute(name = "Model")
				private String model;
				@XmlAttribute(name = "ModelName")
				private String modelName;
				@XmlAttribute(name = "Family")
				private String family;
				@XmlAttribute(name = "ClockSpeedMhz")
				private BigDecimal clockSpeedMhz;
				@XmlAttribute(name = "Bogomips")
				private BigDecimal bogomips;
				@XmlAttribute(name = "CacheL1")
				private int cacheL1;
				@XmlAttribute(name = "CacheL1I")
				private int cacheL1I;
				@XmlAttribute(name = "CacheL1D")
				private int cacheL1D;
				@XmlAttribute(name = "CacheL2")
				private int cacheL2;

				public String vendor() {
					return vendor;
				}

				public Processor vendor(String vendor) {
					this.vendor = vendor;
					return this;
				}

				public String model() {
					return model;
				}

				public Processor model(String model) {
					this.model = model;
					return this;
				}

				public String modelName() {
					return modelName;
				}

				public Processor modelName(String name) {
					this.modelName = name;
					return this;
				}

				public String family() {
					return family;
				}

				public Processor family(String family) {
					this.family = family;
					return this;
				}

				public BigDecimal clockSpeedMhz() {
					return clockSpeedMhz;
				}

				public Processor clockSpeedMhz(BigDecimal speed) {
					this.clockSpeedMhz = speed;
					return this;
				}

				public BigDecimal bogomips() {
					return bogomips;
				}

				public Processor bogomips(BigDecimal bogomips) {
					this.bogomips = bogomips;
					return this;
				}

				public int cacheL1() {
					return cacheL1;
				}

				public Processor cacheL1(int cache) {
					this.cacheL1 = cache;
					return this;
				}

				public int cacheL1I() {
					return cacheL1I;
				}

				public Processor cacheL1I(int cache) {
					this.cacheL1I = cache;
					return this;
				}

				public int cacheL1D() {
					return cacheL1D;
				}

				public Processor cacheL1D(int cache) {
					this.cacheL1D = cache;
					return this;
				}

				public int cacheL2() {
					return cacheL2;
				}

				public Processor cacheL2(int cache) {
					this.cacheL2 = cache;
					return this;
				}

				@Override
				public String toString() {
					return "Processor [vendor=" + vendor + ", model=" + model + ", modelName=" + modelName
							+ ", family=" + family + ", clockSpeedMhz=" + clockSpeedMhz + ", bogomips=" + bogomips
							+ ", cacheL1=" + cacheL1 + ", cacheL1I=" + cacheL1I + ", cacheL1D=" + cacheL1D
							+ ", cacheL2=" + cacheL2 + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((bogomips == null) ? 0 : bogomips.hashCode());
					result = prime * result + cacheL1;
					result = prime * result + cacheL1D;
					result = prime * result + cacheL1I;
					result = prime * result + cacheL2;
					result = prime * result + ((clockSpeedMhz == null) ? 0 : clockSpeedMhz.hashCode());
					result = prime * result + ((family == null) ? 0 : family.hashCode());
					result = prime * result + ((model == null) ? 0 : model.hashCode());
					result = prime * result + ((modelName == null) ? 0 : modelName.hashCode());
					result = prime * result + ((vendor == null) ? 0 : vendor.hashCode());
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
					Processor other = (Processor) obj;
					if (bogomips == null) {
						if (other.bogomips != null)
							return false;
					} else if (!bogomips.equals(other.bogomips))
						return false;
					if (cacheL1 != other.cacheL1)
						return false;
					if (cacheL1D != other.cacheL1D)
						return false;
					if (cacheL1I != other.cacheL1I)
						return false;
					if (cacheL2 != other.cacheL2)
						return false;
					if (clockSpeedMhz == null) {
						if (other.clockSpeedMhz != null)
							return false;
					} else if (!clockSpeedMhz.equals(other.clockSpeedMhz))
						return false;
					if (family == null) {
						if (other.family != null)
							return false;
					} else if (!family.equals(other.family))
						return false;
					if (model == null) {
						if (other.model != null)
							return false;
					} else if (!model.equals(other.model))
						return false;
					if (modelName == null) {
						if (other.modelName != null)
							return false;
					} else if (!modelName.equals(other.modelName))
						return false;
					if (vendor == null) {
						if (other.vendor != null)
							return false;
					} else if (!vendor.equals(other.vendor))
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "NetworkAdapter")
			public static class NetworkAdapter {

				@XmlAttribute(name = "InboundIP")
				private String inboundIP;
				@XmlAttribute(name = "OutboundIP")
				private String outboundIP;
				@XmlAttribute(name = "Name")
				private String name;
				@XmlAttribute(name = "IPAddress")
				private String ipAddress;
				@XmlAttribute(name = "MTU")
				private int mtu;

				public String inboundIP() {
					return inboundIP;
				}

				public NetworkAdapter inboundIP(String ip) {
					this.inboundIP = ip;
					return this;
				}

				public String outboundIP() {
					return outboundIP;
				}

				public NetworkAdapter outboundIP(String ip) {
					this.outboundIP = ip;
					return this;
				}

				public String name() {
					return name;
				}

				public NetworkAdapter name(String name) {
					this.name = name;
					return this;
				}

				public String ipAddress() {
					return ipAddress;
				}

				public NetworkAdapter ipAddress(String address) {
					this.ipAddress = address;
					return this;
				}

				public int mtu() {
					return mtu;
				}

				public NetworkAdapter mtu(int mtu) {
					this.mtu = mtu;
					return this;
				}

				@Override
				public String toString() {
					return "NetworkAdapter [inboundIP=" + inboundIP + ", outboundIP=" + outboundIP + ", name=" + name
							+ ", ipAddress=" + ipAddress + ", mtu=" + mtu + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((inboundIP == null) ? 0 : inboundIP.hashCode());
					result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
					result = prime * result + mtu;
					result = prime * result + ((name == null) ? 0 : name.hashCode());
					result = prime * result + ((outboundIP == null) ? 0 : outboundIP.hashCode());
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
					NetworkAdapter other = (NetworkAdapter) obj;
					if (inboundIP == null) {
						if (other.inboundIP != null)
							return false;
					} else if (!inboundIP.equals(other.inboundIP))
						return false;
					if (ipAddress == null) {
						if (other.ipAddress != null)
							return false;
					} else if (!ipAddress.equals(other.ipAddress))
						return false;
					if (mtu != other.mtu)
						return false;
					if (name == null) {
						if (other.name != null)
							return false;
					} else if (!name.equals(other.name))
						return false;
					if (outboundIP == null) {
						if (other.outboundIP != null)
							return false;
					} else if (!outboundIP.equals(other.outboundIP))
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "Benchmark")
			public static class Benchmark {
				@XmlAttribute(name = "SI00")
				private int si00;
				@XmlAttribute(name = "SF00")
				private int sf00;

				public int si00() {
					return si00;
				}

				public Benchmark si(int si) {
					this.si00=si;
					return this;
				}
				
				public int sf00() {
					return sf00;
				}

				public Benchmark sf(int sf) {
					this.sf00=sf;
					return this;
				}
				
				@Override
				public String toString() {
					return "Benchmark [si00=" + si00 + ", sf00=" + sf00 + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + sf00;
					result = prime * result + si00;
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
					Benchmark other = (Benchmark) obj;
					if (sf00 != other.sf00)
						return false;
					if (si00 != other.si00)
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "Variable")
			public static class Variable {

				@XmlElement(name = "Key")
				private String key;

				@XmlElement(name = "Value")
				private String value;

				public String key() {
					return key;
				}

				public String value() {
					return value;
				}

				public void keyAndValue(String key, String value) {
					this.key = key;
					this.value = value;
				}

				@Override
				public String toString() {
					return "Variable [key=" + key + ", value=" + value + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((key == null) ? 0 : key.hashCode());
					result = prime * result + ((value == null) ? 0 : value.hashCode());
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
					Variable other = (Variable) obj;
					if (key == null) {
						if (other.key != null)
							return false;
					} else if (!key.equals(other.key))
						return false;
					if (value == null) {
						if (other.value != null)
							return false;
					} else if (!value.equals(other.value))
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "StorageDevice")
			public static class StorageDevice {

				@XmlAttribute(name = "Name")
				private String name;
				@XmlAttribute(name = "Type")
				private String type;
				@XmlAttribute(name = "TransferRate")
				private int transferRate;
				@XmlAttribute(name = "Size")
				private int size;

				public String name() {
					return name;
				}

				public StorageDevice name(String name) {
					this.name=name;
					return this;
				}
				
				public String type() {
					return type;
				}

				public StorageDevice type(String type) {
					this.type=type;
					return this;
				}
				
				public int transferRate() {
					return transferRate;
				}

				
				public StorageDevice transferRate(int rate) {
					this.transferRate=rate;
					return this;
				}
				public int size() {
					return size;
				}

				public StorageDevice size(int size) {
					this.size=size;
					return this;
				}
				
				@Override
				public String toString() {
					return "StorageDevice [name=" + name + ", type=" + type + ", transferRate=" + transferRate
							+ ", size=" + size + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((name == null) ? 0 : name.hashCode());
					result = prime * result + size;
					result = prime * result + transferRate;
					result = prime * result + ((type == null) ? 0 : type.hashCode());
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
					StorageDevice other = (StorageDevice) obj;
					if (name == null) {
						if (other.name != null)
							return false;
					} else if (!name.equals(other.name))
						return false;
					if (size != other.size)
						return false;
					if (transferRate != other.transferRate)
						return false;
					if (type == null) {
						if (other.type != null)
							return false;
					} else if (!type.equals(other.type))
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "StoragePartition")
			public static class StoragePartition {

				@XmlAttribute(name = "Name")
				private String name;
				@XmlAttribute(name = "Size")
				private String size;
				@XmlAttribute(name = "ReadRate")
				private int readRate;
				@XmlAttribute(name = "WriteRate")
				private int writeRate;

				public String name() {
					return name;
				}

				public StoragePartition name(String name) {
					this.name=name;
					return this;
				}
				
				public String size() {
					return size;
				}

				public StoragePartition size(String size) {
					this.size=size;
					return this;
				}
				
				public int readRate() {
					return readRate;
				}

				public StoragePartition readRate(int rate) {
					this.readRate=rate;
					return this;
				}
				
				public int writeRate() {
					return writeRate;
				}

				public StoragePartition writeRate(int rate) {
					this.writeRate=rate;
					return this;
				}
				
				@Override
				public String toString() {
					return "StoragePartition [name=" + name + ", size=" + size + ", readRate=" + readRate
							+ ", writeRate=" + writeRate + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((name == null) ? 0 : name.hashCode());
					result = prime * result + readRate;
					result = prime * result + ((size == null) ? 0 : size.hashCode());
					result = prime * result + writeRate;
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
					StoragePartition other = (StoragePartition) obj;
					if (name == null) {
						if (other.name != null)
							return false;
					} else if (!name.equals(other.name))
						return false;
					if (readRate != other.readRate)
						return false;
					if (size == null) {
						if (other.size != null)
							return false;
					} else if (!size.equals(other.size))
						return false;
					if (writeRate != other.writeRate)
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "LocalFileSystem")
			public static class LocalFileSystem extends FileSystemType {}

			@XmlRootElement(name = "RemoteFileSystem")
			public static class RemoteFileSystem extends FileSystemType {}

			@XmlType(name = "FileSystemType")
			public static class FileSystemType {

				@XmlAttribute(name = "Name")
				private String name;
				@XmlAttribute(name = "Root")
				private String root;
				@XmlAttribute(name = "Size")
				private int size;
				@XmlAttribute(name = "ReadOnly")
				private boolean readonly;
				@XmlAttribute(name = "Type")
				private String type;

				public String name() {
					return name;
				}
				
				public FileSystemType name(String name){
					this.name=name;
					return this;
				}
				
				public String root() {
					return root;
				}
				
				public FileSystemType root(String root){
					this.root=root;
					return this;
				}

				public int size() {
					return size;
				}

				public FileSystemType size(int size){
					this.size=size;
					return this;
				}
				
				public boolean isReadOnly() {
					return readonly;
				}

				public FileSystemType readOnly(boolean value){
					this.readonly=value;
					return this;
				}
				
				public String type() {
					return type;
				}
				
				public FileSystemType type(String type) {
					this.type=type;
					return this;
				}

				@Override
				public String toString() {
					return "FileSystemType [name=" + name + ", root=" + root + ", size=" + size + ", readonly="
							+ readonly + ", type=" + type + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((name == null) ? 0 : name.hashCode());
					result = prime * result + (readonly ? 1231 : 1237);
					result = prime * result + ((root == null) ? 0 : root.hashCode());
					result = prime * result + size;
					result = prime * result + ((type == null) ? 0 : type.hashCode());
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
					FileSystemType other = (FileSystemType) obj;
					if (name == null) {
						if (other.name != null)
							return false;
					} else if (!name.equals(other.name))
						return false;
					if (readonly != other.readonly)
						return false;
					if (root == null) {
						if (other.root != null)
							return false;
					} else if (!root.equals(other.root))
						return false;
					if (size != other.size)
						return false;
					if (type == null) {
						if (other.type != null)
							return false;
					} else if (!type.equals(other.type))
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "StorageDevice2StoragePartition")
			public static class DevicePartition {

				@XmlAttribute(name = "StorageDeviceName")
				private String storageDeviceName;
				@XmlAttribute(name = "StoragePartitionName")
				private String storagePartitionName;

				public String device() {
					return storageDeviceName;
				}

				public DevicePartition device(String name) {
					this.storageDeviceName=name;
					return this;
				}
				
				public String name() {
					return storagePartitionName;
				}
				
				public DevicePartition name(String name) {
					this.storagePartitionName=name;
					return this;
				}

				
				@Override
				public String toString() {
					return "DevicePartitionMapping [storageDeviceName=" + storageDeviceName + ", storagePartitionName="
							+ storagePartitionName + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((storageDeviceName == null) ? 0 : storageDeviceName.hashCode());
					result = prime * result + ((storagePartitionName == null) ? 0 : storagePartitionName.hashCode());
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
					DevicePartition other = (DevicePartition) obj;
					if (storageDeviceName == null) {
						if (other.storageDeviceName != null)
							return false;
					} else if (!storageDeviceName.equals(other.storageDeviceName))
						return false;
					if (storagePartitionName == null) {
						if (other.storagePartitionName != null)
							return false;
					} else if (!storagePartitionName.equals(other.storagePartitionName))
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "StoragePartition2FileSystem")
			public static class FileSystemPartition {

				@XmlAttribute(name = "StoragePartitionName")
				private String storagePartitionName;
				@XmlAttribute(name = "FileSystemName")
				private String fileSystemName;

				public String storagePartitionName() {
					return storagePartitionName;
				}

				public FileSystemPartition storageName(String name) {
					this.storagePartitionName=name;
					return this;
				}
				public String fsName() {
					return fileSystemName;
				}

				public FileSystemPartition fsName(String name) {
					this.fileSystemName=name;
					return this;
				}
				@Override
				public String toString() {
					return "PartitionFilesystemMapping [storagePartitionName=" + storagePartitionName
							+ ", fileSystemName=" + fileSystemName + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((fileSystemName == null) ? 0 : fileSystemName.hashCode());
					result = prime * result + ((storagePartitionName == null) ? 0 : storagePartitionName.hashCode());
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
					FileSystemPartition other = (FileSystemPartition) obj;
					if (fileSystemName == null) {
						if (other.fileSystemName != null)
							return false;
					} else if (!fileSystemName.equals(other.fileSystemName))
						return false;
					if (storagePartitionName == null) {
						if (other.storagePartitionName != null)
							return false;
					} else if (!storagePartitionName.equals(other.storagePartitionName))
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "Load")
			public static class Load {

				@XmlAttribute(name = "Last1Min")
				private Double last1Min;
				@XmlAttribute(name = "Last5Min")
				private Double last5Min;
				@XmlAttribute(name = "Last15Min")
				private Double last15Min;

				public Double lastMin() {
					return last1Min;
				}
				
				public Load lastMin(double load)  {
					this.last1Min=load;
					return this;
				}

				public Double last5Mins() {
					return last5Min;
				}

				public Load last5Mins(double load)  {
					this.last5Min=load;
					return this;
				}
				
				public Double last15Mins() {
					return last15Min;
				}

				public Load last15Mins(double load)  {
					this.last15Min=load;
					return this;
				}
				
				@Override
				public String toString() {
					return "[last1Min=" + last1Min + ", last5Min=" + last5Min + ", last15Min=" + last15Min + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + ((last15Min == null) ? 0 : last15Min.hashCode());
					result = prime * result + ((last1Min == null) ? 0 : last1Min.hashCode());
					result = prime * result + ((last5Min == null) ? 0 : last5Min.hashCode());
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
					Load other = (Load) obj;
					if (last15Min == null) {
						if (other.last15Min != null)
							return false;
					} else if (!last15Min.equals(other.last15Min))
						return false;
					if (last1Min == null) {
						if (other.last1Min != null)
							return false;
					} else if (!last1Min.equals(other.last1Min))
						return false;
					if (last5Min == null) {
						if (other.last5Min != null)
							return false;
					} else if (!last5Min.equals(other.last5Min))
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "HistoricalLoad")
			public static class HistoricalLoad {

				@XmlAttribute(name = "Last1H")
				private Double last1H;
				@XmlAttribute(name = "Last1Day")
				private Double last1Day;
				@XmlAttribute(name = "Last1Week")
				private Double last1Week;

				public Double lastHour() {
					return last1H;
				}
				
				public HistoricalLoad lastHour(double load) {
					this.last1H=load;
					return this;
				}

				public Double lastDay() {
					return last1Day;
				}

				public HistoricalLoad lastDay(double load) {
					this.last1Day=load;
					return this;
				}
				
				public Double lastWeek() {
					return last1Week;
				}

				public HistoricalLoad lastWeek(double load) {
					this.last1Week=load;
					return this;
				}
				
				@Override
				public String toString() {
					return "HistoricalLoad [last1H=" + last1H + ", last1Day=" + last1Day + ", last1Week=" + last1Week
							+ "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					long temp;
					temp = Double.doubleToLongBits(last1Day);
					result = prime * result + (int) (temp ^ (temp >>> 32));
					temp = Double.doubleToLongBits(last1H);
					result = prime * result + (int) (temp ^ (temp >>> 32));
					temp = Double.doubleToLongBits(last1Week);
					result = prime * result + (int) (temp ^ (temp >>> 32));
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
					HistoricalLoad other = (HistoricalLoad) obj;
					if (Double.doubleToLongBits(last1Day) != Double.doubleToLongBits(other.last1Day))
						return false;
					if (Double.doubleToLongBits(last1H) != Double.doubleToLongBits(other.last1H))
						return false;
					if (Double.doubleToLongBits(last1Week) != Double.doubleToLongBits(other.last1Week))
						return false;
					return true;
				}

			}

			@XmlRootElement(name = "MainMemory")
			public static class MainMemory {

				@XmlAttribute(name = "RAMSize")
				private long ramSize;
				@XmlAttribute(name = "VirtualSize")
				private long virtualSize;
				@XmlAttribute(name = "RAMAvailable")
				private long ramAvailable;
				@XmlAttribute(name = "VirtualAvailable")
				private long virtualAvailable;

				public Long ramSize() {
					return ramSize;
				}

				public MainMemory ramSize(long size) {
					this.ramSize = size;
					return this;
				}

				public long virtualSize() {
					return virtualSize;
				}

				public MainMemory virtualSize(long size) {
					this.virtualSize = size;
					return this;
				}

				public Long ramAvailable() {
					return ramAvailable;
				}

				public MainMemory ramAvailable(long amount) {
					this.ramAvailable = amount;
					return this;
				}

				public Long virtualAvailable() {
					return virtualAvailable;
				}

				public MainMemory virtualAvailable(long amount) {
					this.virtualAvailable = amount;
					return this;
				}

				@Override
				public String toString() {
					return "MainMemory [ramSize=" + ramSize + ", virtualSize=" + virtualSize + ", ramAvailable="
							+ ramAvailable + ", virtualAvailable=" + virtualAvailable + "]";
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result + (int) (ramAvailable ^ (ramAvailable >>> 32));
					result = prime * result + (int) (ramSize ^ (ramSize >>> 32));
					result = prime * result + (int) (virtualAvailable ^ (virtualAvailable >>> 32));
					result = prime * result + (int) (virtualSize ^ (virtualSize >>> 32));
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
					MainMemory other = (MainMemory) obj;
					if (ramAvailable != other.ramAvailable)
						return false;
					if (ramSize != other.ramSize)
						return false;
					if (virtualAvailable != other.virtualAvailable)
						return false;
					if (virtualSize != other.virtualSize)
						return false;
					return true;
				}

				

			}

			@Override
			public String toString() {
				return "GHNDescriptor [name=" + name + ", activationTime=" + activationTime
						+ ", status=" + status + ", statusMessage=" + statusMessage + ", ghnType=" + ghnType
						/*+ ", platforms=" + platforms */+ ", securityEnabled=" + securityEnabled.booleanValue() + ", securityData="
						+ securityData + ", architecture=" + architecture + ", operatingSystem=" + operatingSystem
						+ ", processors=" + processors + ", networkAdapters=" + networkAdapters + ", benchmark="
						+ benchmark + ", runtimeEnvironment=" + runtimeEnvironment + ", storageDevices="
						+ storageDevices + ", storagePartitions=" + storagePartitions + ", localFileSystems="
						+ localFileSystems + ", remoteFileSystems=" + remoteFileSystems + ", devicePartitionMap="
						+ devicePartitionMap + ", partitionFilesystemMap=" + partitionFilesystemMap + ", uptime="
						+ uptime + ", load=" + load + ", historicalLoad=" + historicalLoad + ", mainMemory="
						+ mainMemory + ", localAvailableStorage=" + localAvailableStorage + ", lastUpdate="
						+ lastUpdate + "]";
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((activationTime == null) ? 0 : activationTime.hashCode());
				result = prime * result + ((architecture == null) ? 0 : architecture.hashCode());
				result = prime * result + ((benchmark == null) ? 0 : benchmark.hashCode());
				result = prime * result + ((devicePartitionMap == null) ? 0 : devicePartitionMap.hashCode());
				result = prime * result + ((ghnType == null) ? 0 : ghnType.hashCode());
				result = prime * result + ((historicalLoad == null) ? 0 : historicalLoad.hashCode());
				result = prime * result + ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
				result = prime * result + ((load == null) ? 0 : load.hashCode());
				result = prime * result + ((localAvailableStorage == null) ? 0 : localAvailableStorage.hashCode());
				result = prime * result + ((localFileSystems == null) ? 0 : localFileSystems.hashCode());
				result = prime * result + ((mainMemory == null) ? 0 : mainMemory.hashCode());
				result = prime * result + ((name == null) ? 0 : name.hashCode());
				result = prime * result + ((networkAdapters == null) ? 0 : networkAdapters.hashCode());
				result = prime * result + ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
				result = prime * result + ((partitionFilesystemMap == null) ? 0 : partitionFilesystemMap.hashCode());
//				result = prime * result + ((platforms == null) ? 0 : platforms.hashCode());
				result = prime * result + ((processors == null) ? 0 : processors.hashCode());
				result = prime * result + ((remoteFileSystems == null) ? 0 : remoteFileSystems.hashCode());
				result = prime * result + ((runtimeEnvironment == null) ? 0 : runtimeEnvironment.hashCode());
				result = prime * result + ((securityData == null) ? 0 : securityData.hashCode());
				result = prime * result + ((securityEnabled == null) ? 0 : securityEnabled.hashCode());
				result = prime * result + ((status == null) ? 0 : status.hashCode());
				result = prime * result + ((statusMessage == null) ? 0 : statusMessage.hashCode());
				result = prime * result + ((storageDevices == null) ? 0 : storageDevices.hashCode());
				result = prime * result + ((storagePartitions == null) ? 0 : storagePartitions.hashCode());
				result = prime * result + ((uptime == null) ? 0 : uptime.hashCode());
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
				NodeDescription other = (NodeDescription) obj;
				if (activationTime == null) {
					if (other.activationTime != null)
						return false;
				} else if (!activationTime.equals(other.activationTime))
					return false;
				if (architecture == null) {
					if (other.architecture != null)
						return false;
				} else if (!architecture.equals(other.architecture))
					return false;
				if (benchmark == null) {
					if (other.benchmark != null)
						return false;
				} else if (!benchmark.equals(other.benchmark))
					return false;
				if (devicePartitionMap == null) {
					if (other.devicePartitionMap != null)
						return false;
				} else if (!devicePartitionMap.equals(other.devicePartitionMap))
					return false;
				if (ghnType != other.ghnType)
					return false;
				if (historicalLoad == null) {
					if (other.historicalLoad != null)
						return false;
				} else if (!historicalLoad.equals(other.historicalLoad))
					return false;
				if (lastUpdate == null) {
					if (other.lastUpdate != null)
						return false;
				} else if (!lastUpdate.equals(other.lastUpdate))
					return false;
				if (load == null) {
					if (other.load != null)
						return false;
				} else if (!load.equals(other.load))
					return false;
				if (localAvailableStorage == null) {
					if (other.localAvailableStorage != null)
						return false;
				} else if (!localAvailableStorage.equals(other.localAvailableStorage))
					return false;
				if (localFileSystems == null) {
					if (other.localFileSystems != null)
						return false;
				} else if (!localFileSystems.equals(other.localFileSystems))
					return false;
				if (mainMemory == null) {
					if (other.mainMemory != null)
						return false;
				} else if (!mainMemory.equals(other.mainMemory))
					return false;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				if (networkAdapters == null) {
					if (other.networkAdapters != null)
						return false;
				} else if (!networkAdapters.equals(other.networkAdapters))
					return false;
				if (operatingSystem == null) {
					if (other.operatingSystem != null)
						return false;
				} else if (!operatingSystem.equals(other.operatingSystem))
					return false;
				if (partitionFilesystemMap == null) {
					if (other.partitionFilesystemMap != null)
						return false;
				} else if (!partitionFilesystemMap.equals(other.partitionFilesystemMap))
					return false;
//				if (platforms == null) {
//					if (other.platforms != null)
//						return false;
//				} else if (!platforms.equals(other.platforms))
//					return false;
				if (processors == null) {
					if (other.processors != null)
						return false;
				} else if (!processors.equals(other.processors))
					return false;
				if (remoteFileSystems == null) {
					if (other.remoteFileSystems != null)
						return false;
				} else if (!remoteFileSystems.equals(other.remoteFileSystems))
					return false;
				if (runtimeEnvironment == null) {
					if (other.runtimeEnvironment != null)
						return false;
				} else if (!runtimeEnvironment.equals(other.runtimeEnvironment))
					return false;
				if (securityData == null) {
					if (other.securityData != null)
						return false;
				} else if (!securityData.equals(other.securityData))
					return false;
				if (securityEnabled == null) {
					if (other.securityEnabled != null)
						return false;
				} else if (!securityEnabled.equals(other.securityEnabled))
					return false;
				if (status == null) {
					if (other.status != null)
						return false;
				} else if (!status.equals(other.status))
					return false;
				if (statusMessage == null) {
					if (other.statusMessage != null)
						return false;
				} else if (!statusMessage.equals(other.statusMessage))
					return false;
				if (storageDevices == null) {
					if (other.storageDevices != null)
						return false;
				} else if (!storageDevices.equals(other.storageDevices))
					return false;
				if (storagePartitions == null) {
					if (other.storagePartitions != null)
						return false;
				} else if (!storagePartitions.equals(other.storagePartitions))
					return false;
				if (uptime == null) {
					if (other.uptime != null)
						return false;
				} else if (!uptime.equals(other.uptime))
					return false;
				return true;
			}

		}

		@XmlRootElement(name = "Site")
		public static class Site {

			@XmlElement(name = "Location")
			private String location;
			@XmlElement(name = "Country")
			private String country;
			@XmlElement(name = "Latitude")
			private String latitude;
			@XmlElement(name = "Longitude")
			private String longitude;
			@XmlElement(name = "Domain")
			private String domain;

			public String location() {
				return location;
			}

			public Site location(String location) {
				this.location = location;
				return this;
			}

			public String country() {
				return country;
			}

			public Site country(String country) {
				this.country = country;
				return this;
			}

			public String latitude() {
				return latitude;
			}

			public Site latitude(String latitude) {
				this.latitude = latitude;
				return this;
			}

			public String longitude() {
				return longitude;
			}

			public Site longitude(String longitude) {
				this.longitude = longitude;
				return this;
			}

			public String domain() {
				return domain;
			}

			public Site domain(String domain) {
				this.domain = domain;
				return this;
			}

			@Override
			public String toString() {
				return "Site [location=" + location + ", country=" + country + ", latitude=" + latitude
						+ ", longitude=" + longitude + ", domain=" + domain + "]";
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((country == null) ? 0 : country.hashCode());
				result = prime * result + ((domain == null) ? 0 : domain.hashCode());
				result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
				result = prime * result + ((location == null) ? 0 : location.hashCode());
				result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
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
				Site other = (Site) obj;
				if (country == null) {
					if (other.country != null)
						return false;
				} else if (!country.equals(other.country))
					return false;
				if (domain == null) {
					if (other.domain != null)
						return false;
				} else if (!domain.equals(other.domain))
					return false;
				if (latitude == null) {
					if (other.latitude != null)
						return false;
				} else if (!latitude.equals(other.latitude))
					return false;
				if (location == null) {
					if (other.location != null)
						return false;
				} else if (!location.equals(other.location))
					return false;
				if (longitude == null) {
					if (other.longitude != null)
						return false;
				} else if (!longitude.equals(other.longitude))
					return false;
				return true;
			}

		}

		@XmlRootElement(name = "Package")
		public static class DeployedPackage {

			@XmlElement(name = "PackageName")
			private String packageName;

			@XmlElement(name = "PackageVersion")
			private String packageVersion;

			@XmlElement(name = "ServiceName")
			private String serviceName;
			@XmlElement(name = "ServiceClass")
			private String serviceClass;

			//@XmlJavaTypeAdapter(value = VersionAdapter.class, type = Version.class)
			@XmlElement(name = "ServiceVersion")
			private String serviceVersion;

			public String name() {
				return packageName;
			}
			
			public DeployedPackage name(String name) {
				this.packageName=name;
				return this;
			}

			public String packageVersion() {
				return packageVersion;
			}
			
			public DeployedPackage packageVersion(String version) {
				this.packageVersion=version;
				return this;
			}

			public String serviceName() {
				return serviceName;
			}
			
			public DeployedPackage serviceName(String name) {
				this.serviceName=name;
				return this;
			}

			public String serviceClass() {
				return serviceClass;
			}

			public DeployedPackage serviceClass(String serviceClass) {
				this.serviceClass=serviceClass;
				return this;
			}
			
			public String serviceVersion() {
				return serviceVersion;
			}

			public DeployedPackage serviceVersion(String version) {
				this.serviceVersion=version;
				return this;
			}
			
			@Override
			public String toString() {
				return "InstalledPackage [packageName=" + packageName + ", packageVersion=" + packageVersion()
						+ ", serviceName=" + serviceName + ", serviceClass=" + serviceClass + ", serviceVersion="
						+ serviceVersion() + "]";
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
				result = prime * result + ((packageVersion == null) ? 0 : packageVersion.hashCode());
				result = prime * result + ((serviceClass == null) ? 0 : serviceClass.hashCode());
				result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
				result = prime * result + ((serviceVersion == null) ? 0 : serviceVersion.hashCode());
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
				DeployedPackage other = (DeployedPackage) obj;
				if (packageName == null) {
					if (other.packageName != null)
						return false;
				} else if (!packageName.equals(other.packageName))
					return false;
				if (packageVersion == null) {
					if (other.packageVersion != null)
						return false;
				} else if (!packageVersion.equals(other.packageVersion))
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
				if (serviceVersion == null) {
					if (other.serviceVersion != null)
						return false;
				} else if (!serviceVersion.equals(other.serviceVersion))
					return false;
				return true;
			}
		}

		@Override
		public String toString() {
			return "Profile [infrastructure=" + infrastructure + ", ghn=" + ghn + ", site=" + site + ", packages="
					+ packages + "]";
		}
	}
}
