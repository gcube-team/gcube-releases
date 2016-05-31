package org.gcube.common.core.resources.node;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.core.contexts.GHNContext.Status;
import org.gcube.common.core.resources.common.PlatformDescription;

public class Description {

	public static enum Type {Static,Dynamic,Selfcleaning};
	private String name,uptime;
	private Status status = null;
	//private String managementURL;
	private Type type= Type.Dynamic;
	private boolean securityEnabled;
	private Architecture architecture;
	private OperatingSystem os;
	private List<Processor> processors = new ArrayList<Processor>();
	private List<NetworkAdapter> adapters = new ArrayList<NetworkAdapter>();
	private Benchmark benchmark;
	private RuntimeEnvironment runtime;
	private Map<String,StorageDevice> storageDevices = new HashMap<String,StorageDevice>();
	private Map<String,StoragePartition> storagePartitions = new HashMap<String,StoragePartition>();
	private Map<String,FileSystem> localFS = new HashMap<String,FileSystem>();
	private Map<String,FileSystem> remoteFS = new HashMap<String,FileSystem>();
	private List<PlatformDescription> platforms = new ArrayList<PlatformDescription>();
	private Load load;
	private HistoricalLoad historicalLoad;
	private Memory memory;
	private Long LocalAvailableSpace;
	private Calendar lastUpdate;	
	private Calendar activation;
	private String message;
	private SecurityData data;

	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public void setStatus(Status status, String ... message) {
		this.status = status;
		if (message != null && message.length>0)
			this.message = message[0];
		else
			message = null;
	}	
	public Status getStatus() {return this.status;}
	public void setStatusMessage(String message) {this.message=message;}
	public String getStatusMessage() {return this.message;}
	public String getUptime() {return uptime;}
	public void setUptime(String uptime) {this.uptime = uptime;}
	public Type getType() {return type;}
	public void setType(Type type) {this.type = type;}
	//public String getManagementURL() {return managementURL;}
	//public void setManagementURL(String managementURL) {this.managementURL = managementURL;}
	public boolean isSecurityEnabled() {return securityEnabled;}
	public void setSecurityEnabled(boolean securityEnabled) {this.securityEnabled = securityEnabled;}
	public Architecture getArchitecture() {return architecture;}
	public void setArchitecture(Architecture architecture) {this.architecture = architecture;}
	public OperatingSystem getOS() {return os;}
	public void setOS(OperatingSystem os) {this.os = os;}
	public Benchmark getBenchmark() {return benchmark;}
	public void setBenchmark(Benchmark benchmark) {this.benchmark = benchmark;}
	public RuntimeEnvironment getRuntime() {return runtime;}
	public void setRuntime(RuntimeEnvironment runtime) {this.runtime = runtime;}
	public Load getLoad() {return load;}
	public void setLoad(Load load) {this.load = load;}
	public HistoricalLoad getHistoricalLoad() {return historicalLoad;}
	public void setHistoricalLoad(HistoricalLoad historicalLoad) {this.historicalLoad = historicalLoad;}
	public Memory getMemory() {return memory;}
	public void setMemory(Memory memory) {this.memory = memory;}
	public Long getLocalAvailableSpace() {return LocalAvailableSpace;}
	public void setLocalAvailableSpace(Long localAvailableSpace) {LocalAvailableSpace = localAvailableSpace;}
	public Calendar getLastUpdate() {return lastUpdate;}
	public void setLastUpdate(Calendar lastUpdate) {this.lastUpdate = lastUpdate;}
	public Calendar getActivationTime() {return this.activation;}
	public void setActivationTime(Calendar activation) {this.activation = activation;}
	
	public List<Processor> getProcessors() {return processors;}	
	public List<NetworkAdapter> getNetworkAdapters() {return adapters;}
	public void addStorageDevice(StorageDevice sd) {this.storageDevices.put(sd.getName(), sd);}
	public void removeStorageDevice(StorageDevice sd) {this.storageDevices.remove(sd);}
	public StorageDevice getStorageDevice(String name) {return this.storageDevices.get(name);}
	public Collection<StorageDevice> getStorageDevices() {return Collections.unmodifiableCollection(this.storageDevices.values());}
	public void addLocalFileSystem(FileSystem fs) {this.localFS.put(fs.getName(), fs);}
	public void removeLocalFileSystem(FileSystem fs) {this.localFS.remove(fs);}
	public FileSystem getLocalFileSystem(String name) {return this.localFS.get(name);}
	public Collection<FileSystem> getLocalFileSystems() {return Collections.unmodifiableCollection(this.localFS.values());}
	public void addRemoteFileSystem(FileSystem fs) {this.remoteFS.put(fs.getName(), fs);}
	public void removeRemoteFileSystem(FileSystem fs) {this.remoteFS.remove(fs);}
	public FileSystem getRemoteFileSystem(String name) {return this.remoteFS.get(name);}
	public Collection<FileSystem> getRemoteFileSystems() {return Collections.unmodifiableCollection(this.remoteFS.values());}
	public void addStoragePartition(StoragePartition sp) {this.storagePartitions.put(sp.getName(), sp);}
	public void removeStoragePartition(StoragePartition sp) {this.storagePartitions.remove(sp);}
	public Collection<StoragePartition> getStoragePartitions() {return Collections.unmodifiableCollection(this.storagePartitions.values());}
	public StoragePartition getStoragePartition(String name) {return this.storagePartitions.get(name);}
	public void addSecurityData(SecurityData data) {this.data  = data;}
	public SecurityData getSecurityData() { return this.data;}
	/**
	 * @return the platforms
	 */
	public List<PlatformDescription> getAvailablePlatforms() {
		return platforms;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		final Description other = (Description) obj;
		
		if (adapters == null) {
			if (other.adapters != null)
				return false;
		} else if (! adapters.equals(other.adapters))
			return false;
		
		if (processors == null) {
			if (other.processors != null)
				return false;
		} else if (! processors.equals(other.processors))
			return false;
		
		if (uptime == null) {
			if (other.uptime != null)
				return false;
		} else if (! uptime.equals(other.uptime))
			return false;
		
		if (securityEnabled != other.securityEnabled) return false;
		
		
		if (architecture == null) {
			if (other.architecture != null)
				return false;
		} else if (! architecture.equals(other.architecture))
			return false;
		
		if (os == null) {
			if (other.os != null)
				return false;
		} else if (! os.equals(other.os))
			return false;
		
		if (benchmark == null) {
			if (other.benchmark != null)
				return false;
		} else if (! benchmark.equals(other.benchmark))
			return false;
		
		if (storageDevices == null) {
			if (other.storageDevices != null)
				return false;
		} else if (! storageDevices.equals(other.storageDevices))
			return false;
		
		if (storagePartitions == null) {
			if (other.storagePartitions != null)
				return false;
		} else if (! storagePartitions.equals(other.storagePartitions))
			return false;
		
		if (localFS == null) {
			if (other.localFS != null)
				return false;
		} else if (! localFS.equals(other.localFS))
			return false;
		
		if (remoteFS == null) {
			if (other.remoteFS != null)
				return false;
		} else if (! remoteFS.equals(other.remoteFS))
			return false;
		
		if (load == null) {
			if (other.load != null)
				return false;
		} else if (! load.equals(other.load))
			return false;
		
		if (historicalLoad == null) {
			if (other.historicalLoad != null)
				return false;
		} else if (! historicalLoad.equals(other.historicalLoad))
			return false;
		
		if (memory == null) {
			if (other.memory != null)
				return false;
		} else if (! memory.equals(other.memory))
			return false;
		
		if (LocalAvailableSpace == null) {
			if (other.LocalAvailableSpace != null)
				return false;
		} else if (! LocalAvailableSpace.equals(other.LocalAvailableSpace))
			return false;
		
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (! lastUpdate.equals(other.lastUpdate))
			return false;
		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (! name.equals(other.name))
			return false;
		
		if (runtime == null) {
			if (other.runtime != null)
				return false;
		} else if (! runtime.equals(other.runtime))
			return false;
		
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (! type.equals(other.type))
			return false;
		
		
		return true;
	}
	
	public static class Architecture {
		
		private String platformType;
		private long SMPSize, SMTSize;
		
		public String getPlatformType() {return platformType;}
		public void setPlatformType(String platformType) {this.platformType = platformType;}
		public long getSMPSize() {return SMPSize;}
		public void setSMPSize(long size) {SMPSize = size;}
		public long getSMTSize() {return SMTSize;}
		public void setSMTSize(long size) {SMTSize = size;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final Architecture other = (Architecture) obj;
			
			if (platformType == null) {
				if (other.platformType != null)
					return false;
			} else if (! platformType.equals(other.platformType))
				return false;
			
			if (SMPSize != other.SMPSize) return false;
			
			if (SMTSize != other.SMTSize) return false;
			
			
			return true;
		}
	}
	
	public static class OperatingSystem {
		
		private String name, release, version;

		public String getName() {return name;}
		public void setName(String name) {this.name = name;}
		public String getRelease() {return release;}
		public void setRelease(String release) {this.release = release;}
		public String getVersion() {return version;}
		public void setVersion(String version) {this.version = version;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final OperatingSystem other = (OperatingSystem) obj;
			
			if (release == null) {
				if (other.release != null)
					return false;
			} else if (! release.equals(other.release))
				return false;
			
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (! name.equals(other.name))
				return false;
			
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (! version.equals(other.version))
				return false;
			
			
			return true;
		}
		
	}
	
	public static class Processor {
		
		private String vendor, model, modelName, family;
		private long cacheL1, cacheL1I, cacheL1D, cacheL2;
		private double clockSpeedMHZ,bogomips;
		
		public double getClockSpeedMHZ() {return clockSpeedMHZ;}
		public void setClockSpeedMHZ(double clockSpeedMHZ) {this.clockSpeedMHZ = clockSpeedMHZ;}
		public long getCacheL1() {return cacheL1;}
		public void setCacheL1(long chacheL1) {this.cacheL1 = chacheL1;}
		public long getCacheL1I() {return cacheL1I;}
		public void setCacheL1I(long cacheL1I) {this.cacheL1I = cacheL1I;}
		public long getCacheL1D() {return cacheL1D;}
		public void setCacheL1D(long chacheL1D) {this.cacheL1D = chacheL1D;}
		public long getCacheL2() {return cacheL2;}
		public void setCacheL2(long cacheL2) {this.cacheL2 = cacheL2;}
		public double getBogomips() {return bogomips;}
		public void setBogomips(double bogomips) {this.bogomips = bogomips;}
		public String getVendor() {return vendor;}
		public void setVendor(String vendor) {this.vendor = vendor;}
		public String getModel() {return model;}
		public void setModel(String model) {this.model = model;}
		public String getModelName() {return modelName;}
		public void setModelName(String modelName) {this.modelName = modelName;}
		public String getFamily() {return family;}
		public void setFamily(String family) {this.family = family;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final Processor other = (Processor) obj;
			
			if (clockSpeedMHZ != other.clockSpeedMHZ) return false;
			
			if (cacheL1 != other.cacheL1) return false;
			
			if (cacheL1I != other.cacheL1I) return false;
			
			if (cacheL1D != other.cacheL1D) return false;
			
			if (cacheL2 != other.cacheL2) return false;
			
			if (bogomips != other.bogomips) return false;
			
			if (vendor == null) {
				if (other.vendor != null)
					return false;
			} else if (! vendor.equals(other.vendor))
				return false;
			
			if (modelName == null) {
				if (other.modelName != null)
					return false;
			} else if (! modelName.equals(other.modelName))
				return false;
			
			if (family == null) {
				if (other.family != null)
					return false;
			} else if (! family.equals(other.family))
				return false;
			
			if (model == null) {
				if (other.model != null)
					return false;
			} else if (! model.equals(other.model))
				return false;
			
			
			return true;
		}
	}	
	
	
	public static class NetworkAdapter {
		
		private String inboundIP, outboundIP, name, IPaddress;
		private long MTU;

		public String getInboundIP() {return inboundIP;}
		public void setInboundIP(String inboundIP) {this.inboundIP = inboundIP;}
		public String getOutboundIP() {return outboundIP;}
		public void setOutboundIP(String outboundIP) {this.outboundIP = outboundIP;}
		public String getName() {return name;}
		public void setName(String name) {this.name = name;}
		public String getIPAddress() {return IPaddress;}
		public void setIPAddress(String paddress) {IPaddress = paddress;}
		public long getMTU() {return MTU;}
		public void setMTU(long mtu) {MTU = mtu;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final NetworkAdapter other = (NetworkAdapter) obj;
			
			if (inboundIP == null) {
				if (other.inboundIP != null)
					return false;
			} else if (! inboundIP.equals(other.inboundIP))
				return false;
			
			if (outboundIP == null) {
				if (other.outboundIP != null)
					return false;
			} else if (! outboundIP.equals(other.outboundIP))
				return false;
			
			if (IPaddress == null) {
				if (other.IPaddress != null)
					return false;
			} else if (! IPaddress.equals(other.IPaddress))
				return false;
			
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (! name.equals(other.name))
				return false;
			
			if (MTU != other.MTU) return false;
			
			
			return true;
		}
		
	}
	
	public static class Benchmark {
		
		private long SI00,SF00;

		public long getSI00() {return SI00;}
		public void setSI00(long si00) {SI00 = si00;}
		public long getSF00() {return SF00;}
		public void setSF00(long sf00) {SF00 = sf00;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final Benchmark other = (Benchmark) obj;
			
			if (SI00 != other.SI00) return false;
			
			if (SF00 != other.SF00) return false;
			
			
			return true;
		}
		
	}
	
	public static class RuntimeEnvironment {
		
		private List<Variable> variables = new ArrayList<Variable>();
		public List<Variable> getVariables() {return variables;}
		
		//public void addVariable(Variable variable) {variables.add(variable);}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final RuntimeEnvironment other = (RuntimeEnvironment) obj;
			
			if (variables == null) {
				if (other.variables != null)
					return false;
			} else if (! variables.equals(other.variables))
				return false;
			
			
			return true;
		}
		
		public static class Variable {
			private String Key,Value;
			public String getKey() {return Key;}
			public void setKey(String key) {Key = key;}
			public String getValue() {return Value;}
			public void setValue(String value) {Value = value;}
			
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				
				final Variable other = (Variable) obj;
				
				if (Value == null) {
					if (other.Value != null)
						return false;
				} else if (! Value.equals(other.Value))
					return false;
				
				if (Key == null) {
					if (other.Key != null)
						return false;
				} else if (! Key.equals(other.Key))
					return false;
				
				
				return true;
			}
			
		}
	
	}
	
	public static class StorageDevice {
		
		private String name, type;
		private long transferRate,size;
		private Map<String,StoragePartition> partitions = new HashMap<String,StoragePartition>();
		public String getName() {return name;}
		public void setName(String name) {this.name = name;}
		public String getType() {return type;}
		public void setType(String type) {this.type = type;}
		public long getTransferRate() {return transferRate;}
		public void setTransferRate(long transferRate) {this.transferRate = transferRate;}
		public long getSize() {return size;}
		public void setSize(long size) {this.size = size;}
		public Map<String,StoragePartition> getPartitions() {return partitions;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final StorageDevice other = (StorageDevice) obj;
			
			if (partitions == null) {
				if (other.partitions != null)
					return false;
			} else if (! partitions.equals(other.partitions))
				return false;
			
			if (transferRate != other.transferRate) return false;
			
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (! name.equals(other.name))
				return false;
			
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (! type.equals(other.type))
				return false;
			
			if (size != other.size) return false;
			
			
			return true;
		}
	}
	
	public static class StoragePartition {
		
		private String name, size;
		private long readRate,writeRate;
		private Map<String,FileSystem> fileSystems = new HashMap<String,FileSystem>();
		public String getName() {return name;}
		public void setName(String name) {this.name = name;}
		public String getSize() {return size;}
		public void setSize(String size) {this.size = size;}
		public long getReadRate() {return readRate;}
		public void setReadRate(long readRate) {this.readRate = readRate;}
		public long getWriteRate() {return writeRate;}
		public void setWriteRate(long writeRate) {this.writeRate = writeRate;}
		public Map<String,FileSystem> getFileSystems() {return this.fileSystems;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final StoragePartition other = (StoragePartition) obj;
			
			if (fileSystems == null) {
				if (other.fileSystems != null)
					return false;
			} else if (! fileSystems.equals(other.fileSystems))
				return false;
			
			if (readRate != other.readRate) return false;
			
			if (writeRate != other.writeRate) return false;
			
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (! name.equals(other.name))
				return false;
			
			if (size == null) {
				if (other.size != null)
					return false;
			} else if (! size.equals(other.size))
				return false;
			
			
			return true;
		}
	}
	
	public static class FileSystem {
	
		private String name, root, type;
		private long size;
		private boolean readOnly;
		public String getName() {return name;}
		public void setName(String name) {this.name = name;}
		public String getRoot() {return root;}
		public void setRoot(String root) {this.root = root;}
		public String getType() {return type;}
		public void setType(String type) {this.type = type;}
		public long getSize() {return size;}
		public void setSize(long size) {this.size = size;}
		public boolean isReadOnly() {return readOnly;}
		public void setReadOnly(boolean readOnly) {this.readOnly = readOnly;}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final FileSystem other = (FileSystem) obj;
			
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (! name.equals(other.name))
				return false;
			
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (! type.equals(other.type))
				return false;
			
			if (size != other.size) return false;
			
			if (readOnly != other.readOnly) return false;
			
			if (root == null) {
				if (other.root != null)
					return false;
			} else if (! root.equals(other.root))
				return false;
			
			
			return true;
		}
	}
	
	public static class Load {
		
		private Double last1min,last5min,last15min;

		public Double getLast1min() {return last1min;}
		public void setLast1min(Double last1min) {this.last1min = last1min;}
		public Double getLast5min() {return last5min;}
		public void setLast5min(Double last5min) {this.last5min = last5min;}
		public Double getLast15min() {return last15min;}
		public void setLast15min(Double last15min) {this.last15min = last15min;}
	
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final Load other = (Load) obj;
			
			if (last1min == null) {
				if (other.last1min != null)
					return false;
			} else if (! last1min.equals(other.last1min))
				return false;
			
			if (last5min == null) {
				if (other.last5min != null)
					return false;
			} else if (! last5min.equals(other.last5min))
				return false;
			
			if (last15min == null) {
				if (other.last15min != null)
					return false;
			} else if (! last15min.equals(other.last15min))
				return false;
			
			
			return true;
		}
	}
	
	public static class SecurityData {
		private Calendar credentialsDate;
		private String credentialsDistinguishedName;
		private String ca;
		
		public String getCA() {	return ca;}		
		public void setCA(String ca) {this.ca = ca;}
		public Calendar getCredentianlsExpireOn() {return credentialsDate;}
		public void setCredentianlsExpireOn(Calendar credentialsDate) {this.credentialsDate = credentialsDate;}
		
		public String getCredentialsDistinguishedName() {return credentialsDistinguishedName;}	
		public void setCredentialsDistinguishedName(String credentialsDistinguishedName) {this.credentialsDistinguishedName = credentialsDistinguishedName;	}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SecurityData other = (SecurityData) obj;
			if (credentialsDate == null) {
				if (other.credentialsDate != null)
					return false;
			} else if (!credentialsDate.equals(other.credentialsDate))
				return false;
			if (credentialsDistinguishedName == null) {
				if (other.credentialsDistinguishedName != null)
					return false;
			} else if (!credentialsDistinguishedName.equals(other.credentialsDistinguishedName))
				return false;
			
			if (ca == null) {
				if (other.ca != null)
					return false;
			} else if (!ca.equals(other.ca))
				return false;
			
			return true;
		}
	}
	
	public static class HistoricalLoad {
		
		private Double last1Hour,last1Day,last1Week;

		public Double getLast1Hour() {return last1Hour;}
		public void setLast1Hour(Double last1Hour) {this.last1Hour = last1Hour;}
		public Double getLast1Day() {return last1Day;}
		public void setLast1Day(Double last1Day) {this.last1Day = last1Day;}
		public Double getLast1Week() {return last1Week;}
		public void setLast1Week(Double last1Week) {this.last1Week = last1Week;}
	
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final HistoricalLoad other = (HistoricalLoad) obj;
			
			if (last1Hour == null) {
				if (other.last1Hour != null)
					return false;
			} else if (! last1Hour.equals(other.last1Hour))
				return false;
			
			if (last1Day == null) {
				if (other.last1Day != null)
					return false;
			} else if (! last1Day.equals(other.last1Day))
				return false;
			
			if (last1Week == null) {
				if (other.last1Week != null)
					return false;
			} else if (! last1Week.equals(other.last1Week))
				return false;
			
			
			return true;
		}
	}
	
	public static class Memory {
		
		private Long size,virtualSize,available, virtualAvailable;

		public Long getSize() {return size;}
		public void setSize(Long size) {this.size = size;}
		public Long getVirtualSize() {return virtualSize;}
		public void setVirtualSize(Long virtualSize) {this.virtualSize = virtualSize;}
		public Long getAvailable() {return available;}
		public void setAvailable(Long available) {this.available = available;}
		public Long getVirtualAvailable() {return virtualAvailable;}
		public void setVirtualAvailable(Long virtualAvailable) {this.virtualAvailable = virtualAvailable;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final Memory other = (Memory) obj;
			
			if (virtualSize == null) {
				if (other.virtualSize != null)
					return false;
			} else if (! virtualSize.equals(other.virtualSize))
				return false;
			
			if (available == null) {
				if (other.available != null)
					return false;
			} else if (! available.equals(other.available))
				return false;
			
			if (virtualAvailable == null) {
				if (other.virtualAvailable != null)
					return false;
			} else if (! virtualAvailable.equals(other.virtualAvailable))
				return false;
			
			if (size == null) {
				if (other.size != null)
					return false;
			} else if (! size.equals(other.size))
				return false;
			
			
			return true;
		}
	}
	

}
