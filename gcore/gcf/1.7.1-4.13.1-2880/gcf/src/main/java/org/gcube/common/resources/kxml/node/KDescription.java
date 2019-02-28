package org.gcube.common.resources.kxml.node;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.util.Calendar;

import org.gcube.common.core.contexts.GHNContext.Status;
import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.resources.node.Description;
import org.gcube.common.core.resources.node.Description.Architecture;
import org.gcube.common.core.resources.node.Description.Benchmark;
import org.gcube.common.core.resources.node.Description.FileSystem;
import org.gcube.common.core.resources.node.Description.HistoricalLoad;
import org.gcube.common.core.resources.node.Description.Load;
import org.gcube.common.core.resources.node.Description.Memory;
import org.gcube.common.core.resources.node.Description.NetworkAdapter;
import org.gcube.common.core.resources.node.Description.OperatingSystem;
import org.gcube.common.core.resources.node.Description.Processor;
import org.gcube.common.core.resources.node.Description.RuntimeEnvironment;
import org.gcube.common.core.resources.node.Description.SecurityData;
import org.gcube.common.core.resources.node.Description.StorageDevice;
import org.gcube.common.core.resources.node.Description.StoragePartition;
import org.gcube.common.core.resources.node.Description.Type;
import org.gcube.common.core.resources.node.Description.RuntimeEnvironment.Variable;
import org.gcube.common.resources.kxml.KGCUBEResource;
import org.gcube.common.resources.kxml.common.KPlatform;
import org.gcube.common.resources.kxml.utils.KBoolean;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

public class KDescription {

	//static final DateFormat dateAndTime=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
	
	public static Description load(KXmlParser parser) throws Exception {
		
		Description d = new Description();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG : 
					if (parser.getName().equals("Name")) d.setName(parser.nextText());
					if (parser.getName().equals("ActivationTime")) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.nextText()));
						d.setActivationTime(cal);
					}
					if (parser.getName().equals("Status")) d.setStatus(Status.valueOf(parser.nextText().toUpperCase()));
					if (parser.getName().equals("StatusMessage")) d.setStatusMessage(parser.nextText());
					if (parser.getName().equals("Type")) d.setType(Type.valueOf(parser.nextText()));					
					if (parser.getName().equals("AvailablePlatforms")) {
						inner: while (true) {
							switch (parser.next()){			
								case KXmlParser.START_TAG : 
									if (parser.getName().equals("Platform")) d.getAvailablePlatforms().add(KPlatform.load(parser,parser.getName()));
									break;
								case KXmlParser.END_TAG:
									if (parser.getName().equals("AvailablePlatforms"))	break inner;
									break;
								case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at AvailablePlatforms");
							}
						}
					}
					if (parser.getName().equals("SecurityEnabled")) d.setSecurityEnabled(KBoolean.load(parser));
					if (parser.getName().equals("SecurityData")) d.addSecurityData(KSecurityData.load(parser));
					if (parser.getName().equals("Architecture")) d.setArchitecture(KArchitecture.load(parser));
					if (parser.getName().equals("OperatingSystem")) d.setOS(KOS.load(parser));
					if (parser.getName().equals("Processor")) d.getProcessors().add(KProcessor.load(parser));
					if (parser.getName().equals("NetworkAdapter")) d.getNetworkAdapters().add(KAdapter.load(parser));
					if (parser.getName().equals("Benchmark")) d.setBenchmark(KBenchmark.load(parser));
					if (parser.getName().equals("RunTimeEnv")) d.setRuntime(KRuntime.load(parser));
					if (parser.getName().equals("StorageDevice")) d.addStorageDevice(KDevice.load(parser));
					if (parser.getName().equals("StoragePartition")) d.addStoragePartition(KPartition.load(parser));
					if (parser.getName().equals("LocalFileSystem")) d.addLocalFileSystem(KFileSystem.load("LocalFileSystem",parser));
					if (parser.getName().equals("RemoteFileSystem")) d.addRemoteFileSystem(KFileSystem.load("RemoteFileSystem",parser));
					if (parser.getName().equals("StorageDevice2StoragePartition")) {
						Pair p = KDevice2Partition.load(parser);
						StorageDevice sd = d.getStorageDevice(p.getFirst());
						if (sd==null) throw new Exception("Storage Device "+p.getFirst()+" is unknown");
						StoragePartition sp = d.getStoragePartition(p.getSecond());
						if (sp==null) throw new Exception("Storage Partition "+p.getSecond()+" is unknown");
						sd.getPartitions().put(sp.getName(),sp);
					}
					if (parser.getName().equals("StoragePartition2FileSystem")) {
						Pair p = KPartition2FS.load(parser);
						StoragePartition sp = d.getStoragePartition(p.getFirst());
						if (sp==null) throw new Exception("Storage Partition "+p.getSecond()+" is unknown");						
						FileSystem fs = d.getLocalFileSystem(p.getSecond());
						if (fs==null) {
							fs = d.getRemoteFileSystem(p.getSecond());
							if (fs == null) throw new Exception("File System "+p.getFirst()+" is unknown");
						}
						sp.getFileSystems().put(fs.getName(),fs);
					}
					if (parser.getName().equals("Uptime")) d.setUptime(parser.nextText());
					if (parser.getName().equals("Load")) d.setLoad(KLoad.load(parser));
					if (parser.getName().equals("HistoricalLoad")) d.setHistoricalLoad(KHistoricalLoad.load(parser));
					if (parser.getName().equals("MainMemory")) d.setMemory(KMemory.load(parser));
					if (parser.getName().equals("LocalAvailableSpace")) d.setLocalAvailableSpace(Long.valueOf(parser.nextText()));
					if (parser.getName().equals("LastUpdate")) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.nextText()));
						d.setLastUpdate(cal);
					}
					
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("GHNDescription"))	break loop;
					break;
				case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at GHNDescription");
			}
		}
		return d;
	} 
	
	public static void store(Description component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		serializer.startTag(NS,"GHNDescription");
		if (component.getName()!=null) serializer.startTag(NS, "Name").text(component.getName()).endTag(NS, "Name");
		if (component.getActivationTime()!=null) serializer.startTag(NS, "ActivationTime").text(KGCUBEResource.toXMLDateAndTime(component.getActivationTime().getTime())).endTag(NS,"ActivationTime");
		if (component.getStatus()!=null) serializer.startTag(NS, "Status").text(component.getStatus().toString()).endTag(NS, "Status");
		if (component.getStatusMessage()!=null) serializer.startTag(NS, "StatusMessage").text(component.getStatusMessage()).endTag(NS, "StatusMessage");
		if (component.getType()!=null) serializer.startTag(NS, "Type").text(component.getType().toString()).endTag(NS, "Type");
		if (component.getAvailablePlatforms().size() > 0) {
			serializer.startTag(NS, "AvailablePlatforms");
			for (PlatformDescription p : component.getAvailablePlatforms()) KPlatform.store(p, serializer, "Platform");
			serializer.endTag(NS, "AvailablePlatforms");	
		}
		serializer.startTag(NS, "SecurityEnabled"); KBoolean.store(component.isSecurityEnabled(),serializer);serializer.endTag(NS, "SecurityEnabled");
		KSecurityData.store(component.getSecurityData(), serializer);
		KArchitecture.store(component.getArchitecture(),serializer);
		KOS.store(component.getOS(),serializer);
		for (Processor p : component.getProcessors()) KProcessor.store(p,serializer);
		for (NetworkAdapter a : component.getNetworkAdapters()) KAdapter.store(a,serializer);
		KBenchmark.store(component.getBenchmark(),serializer);
		KRuntime.store(component.getRuntime(),serializer);
		for (StorageDevice s : component.getStorageDevices()) KDevice.store(s,serializer);
		for (StoragePartition p : component.getStoragePartitions()) KPartition.store(p,serializer);
		for (FileSystem fs : component.getLocalFileSystems()) KFileSystem.store("LocalFileSystem",fs,serializer);
		for (FileSystem fs : component.getRemoteFileSystems()) KFileSystem.store("RemoteFileSystem",fs,serializer);
		for (StorageDevice device: component.getStorageDevices()) {
			for (StoragePartition partition : device.getPartitions().values()) {
				Pair p =  new Pair();
				p.setFirst(device.getName());p.setSecond(partition.getName());
				KDevice2Partition.store(p,serializer);
			}
		}
		for (StoragePartition partition: component.getStoragePartitions()) {
			for (FileSystem fs : partition.getFileSystems().values()) {
				Pair p =  new Pair();
				p.setFirst(partition.getName());p.setSecond(fs.getName());
				KDevice2Partition.store(p,serializer);
			}
		} 
		if (component.getUptime()!=null) serializer.startTag(NS, "Uptime").text(component.getUptime()).endTag(NS, "Uptime");
		KLoad.store(component.getLoad(), serializer);
		KHistoricalLoad.store(component.getHistoricalLoad(), serializer);
		KMemory.store(component.getMemory(),serializer);
				
		if (component.getLocalAvailableSpace()!=null) serializer.startTag(NS, "LocalAvailableSpace").text(component.getLocalAvailableSpace()+"").endTag(NS,"LocalAvailableSpace");		
		if (component.getLastUpdate()!=null) serializer.startTag(NS, "LastUpdate").text(KGCUBEResource.toXMLDateAndTime(component.getLastUpdate().getTime())).endTag(NS,"LastUpdate");		
		serializer.endTag(NS,"GHNDescription");
	}
	
	public static class KArchitecture {

		public static Architecture load(KXmlParser parser) throws Exception {
			
			Architecture a = new Architecture();
			a.setPlatformType(parser.getAttributeValue(NS, "PlatformType"));
			a.setSMPSize(Long.valueOf(parser.getAttributeValue(NS, "SMPSize")));
			a.setSMPSize(Long.valueOf(parser.getAttributeValue(NS, "SMTSize")));
			return a;
		} 
		
		public static void store(Architecture component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"Architecture");
			if (component.getPlatformType()!=null) serializer.attribute(NS, "PlatformType",component.getPlatformType());
			serializer.attribute(NS, "SMPSize",component.getSMPSize()+"");
			serializer.attribute(NS, "SMTSize",component.getSMTSize()+"");
			serializer.endTag(NS,"Architecture");
		}
	}
	
	public static class KSecurityData {

		public static SecurityData load(KXmlParser parser) throws Exception {
			
			SecurityData data = new SecurityData();						
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG : 
						if (parser.getName().equals("CredentialsExpireOn")) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(KGCUBEResource.fromXMLDateAndTime(parser.nextText()));
							data.setCredentianlsExpireOn(cal);
						}
						if (parser.getName().equals("CredentialsDistinguishedName")) {data.setCredentialsDistinguishedName(parser.nextText());}
						if (parser.getName().equals("CA")) {data.setCA(parser.nextText());}
					case KXmlParser.END_TAG:
						if (parser.getName().equals("SecurityData")) break loop;
						break;
					case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at SecurityData");
				}
			}			
			return data;
		} 
		
		public static void store(SecurityData component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"SecurityData");
			if (component.getCredentialsDistinguishedName()!=null) serializer.startTag(NS, "CA").text(component.getCA()).endTag(NS,"CA");
			if (component.getCredentialsDistinguishedName()!=null) serializer.startTag(NS, "CredentialsDistinguishedName").text(component.getCredentialsDistinguishedName()).endTag(NS,"CredentialsDistinguishedName");
			if (component.getCredentianlsExpireOn()!=null) serializer.startTag(NS, "CredentialsExpireOn").text(KGCUBEResource.toXMLDateAndTime(component.getCredentianlsExpireOn().getTime())).endTag(NS,"CredentialsExpireOn");
			serializer.endTag(NS,"SecurityData");
		}
	}
	

	public static class KOS {

		public static OperatingSystem load(KXmlParser parser) throws Exception {
			
			OperatingSystem os = new OperatingSystem();
			os.setName(parser.getAttributeValue(NS, "Name"));
			os.setRelease(parser.getAttributeValue(NS, "Release"));
			os.setVersion(parser.getAttributeValue(NS, "Version"));
			return os;
		} 
		
		public static void store(OperatingSystem component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"OperatingSystem");
			if (component.getName()!=null) serializer.attribute(NS, "Name",component.getName());
			if (component.getRelease()!=null) serializer.attribute(NS, "Release",component.getRelease()+"");
			if (component.getVersion()!=null) serializer.attribute(NS, "Version",component.getVersion()+"");
			serializer.endTag(NS,"OperatingSystem");
		}
	}
	
	public static class KProcessor {

		public static Processor load(KXmlParser parser) throws Exception {
			
			Processor p = new Processor();
			p.setVendor(parser.getAttributeValue(NS, "Vendor"));
			p.setModel(parser.getAttributeValue(NS, "Model"));
			p.setModelName(parser.getAttributeValue(NS, "ModelName"));
			p.setFamily(parser.getAttributeValue(NS, "Family"));
			p.setClockSpeedMHZ(Double.valueOf(parser.getAttributeValue(NS, "ClockSpeedMhz")));
			p.setBogomips(Double.valueOf(parser.getAttributeValue(NS, "Bogomips")));
			p.setCacheL1(Long.valueOf(parser.getAttributeValue(NS, "CacheL1")));
			p.setCacheL1I(Long.valueOf(parser.getAttributeValue(NS, "CacheL1I")));
			p.setCacheL1D(Long.valueOf(parser.getAttributeValue(NS, "CacheL1D")));
			p.setCacheL2(Long.valueOf(parser.getAttributeValue(NS, "CacheL2")));
			return p;
		} 
		
		public static void store(Processor component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"Processor");
			if (component.getVendor()!=null) serializer.attribute(NS, "Vendor",component.getVendor());
			if (component.getModel()!=null) serializer.attribute(NS, "Model",component.getModel());
			if (component.getModelName()!=null) serializer.attribute(NS, "ModelName",component.getModelName());
			if (component.getFamily()!=null) serializer.attribute(NS, "Family",component.getFamily());
			serializer.attribute(NS, "ClockSpeedMhz",component.getClockSpeedMHZ()+"");
			serializer.attribute(NS, "Bogomips",component.getBogomips()+"");
			serializer.attribute(NS, "CacheL1",component.getCacheL1()+"");
			serializer.attribute(NS, "CacheL1I",component.getCacheL1I()+"");
			serializer.attribute(NS, "CacheL1D",component.getCacheL1D()+"");
			serializer.attribute(NS, "CacheL2",component.getCacheL2()+"");			
			serializer.endTag(NS,"Processor");
		}
	}
	
	public static class KAdapter {

		public static NetworkAdapter load(KXmlParser parser) throws Exception {
			
			NetworkAdapter a = new NetworkAdapter();
			a.setInboundIP(parser.getAttributeValue(NS, "InboundIP"));
			a.setOutboundIP(parser.getAttributeValue(NS, "OutboundIP"));
			a.setName(parser.getAttributeValue(NS, "Name"));
			a.setIPAddress(parser.getAttributeValue(NS, "IPAddress"));
			a.setMTU(Long.valueOf(parser.getAttributeValue(NS, "MTU")));
			return a;
		} 
		
		public static void store(NetworkAdapter component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"NetworkAdapter");
			if (component.getInboundIP()!=null) serializer.attribute(NS, "InboundIP",component.getInboundIP());
			if (component.getOutboundIP()!=null) serializer.attribute(NS, "OutboundIP",component.getOutboundIP());
			if (component.getName()!=null) serializer.attribute(NS, "Name",component.getName());
			if (component.getIPAddress()!=null) serializer.attribute(NS, "IPAddress",component.getIPAddress());
			serializer.attribute(NS, "MTU",component.getMTU()+"");
			serializer.endTag(NS,"NetworkAdapter");
		}
	}	
	
	public static class KBenchmark {

		public static Benchmark load(KXmlParser parser) throws Exception {
			
			Benchmark b = new Benchmark();
			b.setSI00(Long.valueOf(parser.getAttributeValue(NS, "SI00")));
			b.setSI00(Long.valueOf(parser.getAttributeValue(NS, "SF00")));						
			return b;
		} 
		
		public static void store(Benchmark component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"Benchmark");
			serializer.attribute(NS, "SI00",component.getSI00()+"");
			serializer.attribute(NS, "SF00",component.getSI00()+"");
			serializer.endTag(NS,"Benchmark");
		}
	}
	
	
	public static class KRuntime {

		public static RuntimeEnvironment load(KXmlParser parser) throws Exception {
			
			RuntimeEnvironment rt = new RuntimeEnvironment();
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG : 
						if (parser.getName().equals("Variable")) rt.getVariables().add(KVariable.load(parser));
						break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("RunTimeEnv"))	break loop;
						break;
					case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at RunTimeEnv");
				}
			}
			return rt;
		} 
		
		public static void store(RuntimeEnvironment component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"RunTimeEnv");
			if (component.getVariables().size()!=0) for (Variable v : component.getVariables()) KVariable.store(v,serializer); 
			serializer.endTag(NS,"RunTimeEnv");
		}
		
		public static class KVariable {

			public static Variable load(KXmlParser parser) throws Exception {
				
				Variable var = new Variable();
				loop: while (true) {
					switch (parser.next()){			
						case KXmlParser.START_TAG : 
							if (parser.getName().equals("Key")) var.setKey(parser.nextText());
							if (parser.getName().equals("Value")) var.setValue(parser.nextText());
							break;
						case KXmlParser.END_TAG:
							if (parser.getName().equals("Variable"))	break loop;
							break;
						case KXmlParser.END_DOCUMENT :	throw new Exception("Parsing failed at Variable");
					}
				}
				return var;
			} 
			
			public static void store(Variable component, KXmlSerializer serializer) throws Exception {
				if (component==null) return;
				serializer.startTag(NS,"Variable");
				if (component.getKey()!=null) serializer.startTag(NS,"Key").text(component.getKey()).endTag(NS,"Key");
				if (component.getValue()!=null) serializer.startTag(NS,"Value").text(component.getValue()).endTag(NS,"Value");
				serializer.endTag(NS,"Variable");
			}
		}
		
	}
	
	public static class KDevice {

		public static StorageDevice load(KXmlParser parser) throws Exception {
			
			StorageDevice var = new StorageDevice();
			var.setName(parser.getAttributeValue(NS, "Name"));
			var.setType(parser.getAttributeValue(NS, "Type"));
			var.setTransferRate(Long.valueOf(parser.getAttributeValue(NS, "TransferRate")));
			var.setSize(Long.valueOf(parser.getAttributeValue(NS, "Size")));
			return var;
		} 
		
		public static void store(StorageDevice component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"StorageDevice");
			if (component.getName()!=null) serializer.attribute(NS,"Name",component.getName());
			if (component.getType()!=null) serializer.attribute(NS,"Type",component.getType());
			serializer.attribute(NS,"TransferRate",component.getTransferRate()+"");
			serializer.attribute(NS,"Size",component.getSize()+"");
			serializer.endTag(NS,"StorageDevice");
		}
	}

	public static class KPartition {

		public static StoragePartition load(KXmlParser parser) throws Exception {
			
			StoragePartition p = new StoragePartition();
			p.setName(parser.getAttributeValue(NS, "Name"));
			p.setSize(parser.getAttributeValue(NS, "Size"));
			p.setReadRate(Long.valueOf(parser.getAttributeValue(NS, "ReadRate")));
			p.setWriteRate(Long.valueOf(parser.getAttributeValue(NS, "WriteRate")));
			return p;
		} 
		
		public static void store(StoragePartition component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"StoragePartition");
			if (component.getName()!=null) serializer.attribute(NS,"Name",component.getName());
			if (component.getSize()!=null) serializer.attribute(NS,"Size",component.getSize());
			serializer.attribute(NS,"ReadRate",component.getReadRate()+"");
			serializer.attribute(NS,"WriteRate",component.getWriteRate()+"");
			serializer.endTag(NS,"StoragePartition");
		}
	}
	

	public static class KFileSystem {

		public static FileSystem load(String rootTag, KXmlParser parser) throws Exception {
			
			FileSystem fs = new FileSystem();
			fs.setName(parser.getAttributeValue(NS, "Name"));
			fs.setRoot(parser.getAttributeValue(NS, "Root"));
			fs.setSize(Long.valueOf(parser.getAttributeValue(NS,"Size")));
			fs.setReadOnly(Boolean.valueOf(parser.getAttributeValue(NS, "ReadOnly")));
			fs.setType(parser.getAttributeValue(NS,"Type"));
			return fs;
		} 
		
		public static void store(String rootTag,FileSystem component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,rootTag);
			if (component.getName()!=null) serializer.attribute(NS,"Name",component.getName());
			if (component.getRoot()!=null) serializer.attribute(NS,"Root",component.getRoot());
			serializer.attribute(NS,"Size",component.getSize()+"");
			serializer.attribute(NS,"ReadOnly",component.isReadOnly()+"");
			if (component.getType()!=null) serializer.attribute(NS,"Type",component.getType());
			serializer.endTag(NS,rootTag);
		}
	}
	
	static class Pair {
		
		private String first,second; 
		String getFirst() {return first;}
		String getSecond() {return second;}
		void setFirst(String first) {this.first=first;}
		void setSecond(String second) {this.second=second;}
		
	}
	
	public static class KDevice2Partition {

		public static Pair load(KXmlParser parser) throws Exception {
			
			Pair p = new Pair();
			p.setFirst(parser.getAttributeValue(NS, "StorageDeviceName"));
			p.setSecond(parser.getAttributeValue(NS, "StoragePartitionName"));
			return p;
		} 
		
		public static void store(Pair component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"StorageDevice2StoragePartition");
			if (component.getFirst()!=null) serializer.attribute(NS,"StorageDeviceName",component.getFirst());
			if (component.getSecond()!=null) serializer.attribute(NS,"StoragePartitionName",component.getSecond());
			serializer.endTag(NS,"StorageDevice2StoragePartition");
		}
		
	}
	
	public static class KPartition2FS {

		public static Pair load(KXmlParser parser) throws Exception {
			
			Pair p = new Pair();
			p.setFirst(parser.getAttributeValue(NS, "StoragePartitionName"));
			p.setSecond(parser.getAttributeValue(NS, "FileSystemName"));
			return p;
		} 
		
		public static void store(Pair component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"StoragePartition2FileSystem");
			if (component.getFirst()!=null) serializer.attribute(NS,"StoragePartitionName",component.getFirst());
			if (component.getSecond()!=null) serializer.attribute(NS,"FileSystemName",component.getSecond());
			serializer.endTag(NS,"StoragePartition2FileSystem");
		}
		
	}	
	

	public static class KLoad {

		public static Load load(KXmlParser parser) throws Exception {
			
			Load l = new Load();
			if (parser.getAttributeValue(NS, "Last1Min")!=null) l.setLast1min(Double.valueOf(parser.getAttributeValue(NS, "Last1Min")));
			if (parser.getAttributeValue(NS, "Last5Min")!=null) l.setLast5min(Double.valueOf(parser.getAttributeValue(NS, "Last5Min")));
			if (parser.getAttributeValue(NS, "Last15Min")!=null) l.setLast15min(Double.valueOf(parser.getAttributeValue(NS, "Last15Min")));
			return l;
		} 
		
		public static void store(Load component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"Load");
			if (component.getLast1min()!=null) serializer.attribute(NS, "Last1Min", component.getLast1min().toString());
			if (component.getLast5min()!=null) serializer.attribute(NS, "Last5Min", component.getLast5min().toString());
			if (component.getLast15min()!=null) serializer.attribute(NS, "Last15Min", component.getLast15min().toString());
			serializer.endTag(NS,"Load");
		}
		
	}
	
	public static class KHistoricalLoad {

		public static HistoricalLoad load(KXmlParser parser) throws Exception {
			
			HistoricalLoad l = new HistoricalLoad();
			if (parser.getAttributeValue(NS, "Last1H")!=null) l.setLast1Hour(Double.valueOf(parser.getAttributeValue(NS, "Last1H")));
			if (parser.getAttributeValue(NS, "Last1Day")!=null) l.setLast1Day(Double.valueOf(parser.getAttributeValue(NS, "Last1Day")));
			if (parser.getAttributeValue(NS, "Last1Week")!=null) l.setLast1Week(Double.valueOf(parser.getAttributeValue(NS, "Last1Week")));
			return l;
		} 
		
		public static void store(HistoricalLoad component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"HistoricalLoad");
			if (component.getLast1Hour()!=null) serializer.attribute(NS, "Last1H", component.getLast1Hour().toString());
			if (component.getLast1Day()!=null) serializer.attribute(NS, "Last1Day", component.getLast1Day().toString());
			if (component.getLast1Week()!=null) serializer.attribute(NS, "Last1Week", component.getLast1Week().toString());
			serializer.endTag(NS,"HistoricalLoad");
		}
		
	}

	public static class KMemory {

		public static Memory load(KXmlParser parser) throws Exception {
			
			Memory l = new Memory();
			if (parser.getAttributeValue(NS, "RAMSize")!=null) l.setSize(Long.valueOf(parser.getAttributeValue(NS, "RAMSize")));
			if (parser.getAttributeValue(NS, "VirtualSize")!=null) l.setVirtualSize(Long.valueOf(parser.getAttributeValue(NS, "VirtualSize")));
			if (parser.getAttributeValue(NS, "RAMAvailable")!=null) l.setAvailable(Long.valueOf(parser.getAttributeValue(NS, "RAMAvailable")));
			if (parser.getAttributeValue(NS, "VirtualAvailable")!=null) l.setVirtualAvailable(Long.valueOf(parser.getAttributeValue(NS, "VirtualAvailable")));
			return l;
		} 
		
		public static void store(Memory component, KXmlSerializer serializer) throws Exception {
			if (component==null) return;
			serializer.startTag(NS,"MainMemory");
			if (component.getSize()!=null) serializer.attribute(NS, "RAMSize", component.getSize().toString());
			if (component.getVirtualSize()!=null) serializer.attribute(NS, "VirtualSize", component.getVirtualSize().toString());
			if (component.getAvailable()!=null) serializer.attribute(NS, "RAMAvailable", component.getAvailable().toString());
			if (component.getVirtualAvailable()!=null) serializer.attribute(NS, "VirtualAvailable", component.getVirtualAvailable().toString());
			serializer.endTag(NS,"MainMemory");
		}
		
	}
	
}
