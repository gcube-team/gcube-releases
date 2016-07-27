package org.gcube.vremanagement.resourcemanager.impl.reporting;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResourceFactory;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedRunningInstance;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource.STATUS;
import org.gcube.vremanagement.resourcemanager.impl.resources.software.DeployedDependency;
import org.gcube.vremanagement.resourcemanager.impl.resources.software.GCUBEPackage;
import org.gcube.vremanagement.resourcemanager.impl.state.InstanceState;
import org.gcube.vremanagement.resourcemanager.impl.state.ProfileDate;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Parser for the report sent by the Deployer Service 
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class DeployerReport {

	protected GCUBELog logger = new GCUBELog(this);
	
	protected InstanceState instanceState;
	
	protected GCUBEScope targetScope;
	
	protected String report = "";

	private String status = "";
	
	private String name = "";
	
	private String host = "";
	
	private String type = "";
		
	private Date lastUpdate;
	
	private List<DeployedDependency> dependencies = new ArrayList<DeployedDependency>();
	
	private Set<DeployedRunningInstance> instances = new HashSet<DeployedRunningInstance>();
	
	/**
	 * 
	 * @param instanceState the current instance state 
	 * @param targetScope 
	 * @param report the string representation of the report as sent by a Deployer Service
	 * 
	 */
	public DeployerReport(InstanceState instanceState, GCUBEScope targetScope, String report) throws Exception {
		this.report = report;		
		this.instanceState = instanceState;
		this.targetScope = targetScope;
		try {			
			this.parse();
			
		} catch (XmlPullParserException e) {
			throw new Exception("invalid Deployer Report");
		}
	}
	/**
	 * Gets the GHN ID
	 * @return the id
	 * 
	 * @throws Exception if it is impossible to parse the Deployer Report or the GHNID element is not found
	 */
	public String getGHNName() {		
		//look for GHNID element
		return this.name;
				
	}
	
	public String toString() {
		return this.report;
	}
	
		/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}
	/**
	 * @return the dependencies
	 */
	public List<DeployedDependency> getDependencies() {
		return dependencies;
	}
	
	/**
	 * 
	 * @return the hostname from which the report was sent
	 */
	public String getHost() {
		return this.host;
	}
	
	private void parse() throws Exception {		
		KXmlParser parser = new KXmlParser();
		parser.setInput(new BufferedReader(new StringReader(report)));		
		loop: while (true) {
			try {
				switch (parser.next()) {
				case KXmlParser.START_TAG:
					if (parser.getName().equals("GHN")) this.name = parser.nextText();
					else  if (parser.getName().equals("Type")) this.type = parser.nextText();
					else  if (parser.getName().equals("LastUpdate")) this.lastUpdate = ProfileDate.fromXMLDateAndTime( parser.nextText());
					else  if (parser.getName().equals("Status")) this.status = parser.nextText();
					else  if (parser.getName().equals("Packages")) this.parsePackages(parser);
					else  if (parser.getName().equals("NewInstances")) this.parseInstances(parser);
					break;
				case KXmlParser.END_DOCUMENT: break loop;
				}				
			} catch (Exception e) {				
				logger.error("Unable to parse the Deployer Report",e);
				throw new Exception ("Unable to parse the Deployer Report",e);
			} 
		}
	}
	
	private void parseInstances(KXmlParser parser) throws Exception {
		
		loop: while (true) {
				switch (parser.next()) {
					case KXmlParser.START_TAG:
						if (parser.getName().equals("NewInstance")) {
							DeployedRunningInstance instance = new DeployedRunningInstance();
							innerloop: while (true) {
								switch (parser.next()) {									
									case KXmlParser.START_TAG:
										if (parser.getName().equals("ID")) instance.setRIId(parser.nextText());
										if (parser.getName().equals("ServiceClass")) instance.setServiceClass(parser.nextText());
										if (parser.getName().equals("ServiceName")) instance.setServiceName(parser.nextText());
										if (parser.getName().equals("ServiceVersion")) instance.setServiceVersion(parser.nextText());
										if (parser.getName().equals("PackageVersion")) instance.setPackageVersion(parser.nextText());
										if (parser.getName().equals("PackageName")) instance.setPackageName(parser.nextText());										
										break ;
									case KXmlParser.END_TAG: if (parser.getName().equals("NewInstance")){
											try {
												ScopedRunningInstance ri = (ScopedRunningInstance) ScopedResourceFactory.newResource(this.targetScope,instance.getRIID(), GCUBERunningInstance.TYPE);
												ri.setHostedON(this.host);
												ri.setJointTime(Calendar.getInstance().getTime());
												ri.setStatus(STATUS.PUBLISHED);
												instance.setInstance(ri);
												instance.isAlive = true;
												instance.setMessage("An instance of this service has been correctly activated on " + this.host);
											} catch (Exception e) {
												logger.error("An instance of this service has been activated but it didn't start correctly on " + this.host + ". The expected instance identifier was " + instance.getRIID(), e);
												instance.setMessage("An instance of this service has been activated but it didn't start correctly on " + this.host + ". The expected instance identifier was " + instance.getRIID());
												instance.isAlive = false;
											} finally {
												this.instances.add(instance);
											}
											break innerloop;
										} 
										break;
									case KXmlParser.END_DOCUMENT: throw new Exception ("Parsing failed at NewInstance");	
								}														
							}																					
						}
						break;					
					case KXmlParser.END_TAG: if (parser.getName().equals("NewInstances")) break loop; 
						break;										
					case KXmlParser.END_DOCUMENT: throw new Exception ("Parsing failed at NewInstances");
				}				
		}
		
	}
	
	private void parsePackages(KXmlParser parser) throws Exception {
		loop: while (true) {
			try {
				switch (parser.next()) {
					case KXmlParser.START_TAG:
						if (parser.getName().equals("Package")) {
							DeployedDependency dd = new DeployedDependency();
							dd.setService(new GCUBEPackage());
							innerloop: while (true) {
								switch (parser.next()) {
									case KXmlParser.START_TAG:
										if (parser.getName().equals("ServiceClass")) {dd.getService().setClazz(parser.nextText());}
										else if (parser.getName().equals("ServiceName")) {dd.getService().setName(parser.nextText());}
										else if (parser.getName().equals("ServiceVersion")) {dd.getService().setVersion(parser.nextText());}
										else if (parser.getName().equals("PackageName")) {dd.setName(parser.nextText());}
										else if (parser.getName().equals("PackageVersion")) {dd.setVersion(parser.nextText());}
										else if (parser.getName().equals("Status")) {dd.setStatus(parser.nextText());}
										else if (parser.getName().equals("Host")) {dd.setHost(parser.nextText()); this.host = dd.getHost();}
										else if (parser.getName().equals("Message")) {dd.setMessage(parser.nextText());}
										else parser.nextText();
										break;
									case KXmlParser.END_TAG: if (parser.getName().equals("Package")){ this.dependencies.add(dd); break innerloop;} 
										break;
									case KXmlParser.END_DOCUMENT: throw new Exception ("Parsing failed at Package");	
								}														
							}
						} 
						break;					
					case KXmlParser.END_TAG: if (parser.getName().equals("Packages")) break loop; 
					break;										
					case KXmlParser.END_DOCUMENT: throw new Exception ("Parsing failed at Packages");
				}				
			} catch (Exception e) {
				throw new Exception ("Unable to parse the Deployer Report");
			} 
		}
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return this.status;
	}
	/**
	 * @return the instances
	 */
	public Set<DeployedRunningInstance> getInstances() {
		return this.instances;
	}
	
	public final class DeployedRunningInstance {
		private ScopedRunningInstance instance;
		private String riid;
		private String serviceClass;
		private String serviceName;
		private String serviceVersion;
		private String packageName;
		private String packageVersion;
		private boolean isAlive;
		private String message;
		/**
		 * @return the instance
		 */
		public ScopedRunningInstance getInstance() {
			return instance;
		}
		/**
		 * @param instance the instance to set
		 */
		public void setInstance(ScopedRunningInstance instance) {
			this.instance = instance;
		}
		/**
		 * @return the serviceClass
		 */
		public String getServiceClass() {
			return serviceClass;
		}
		/**
		 * @param serviceClass the serviceClass to set
		 */
		public void setServiceClass(String serviceClass) {
			this.serviceClass = serviceClass;
		}
		/**
		 * @return the serviceName
		 */
		public String getServiceName() {
			return serviceName;
		}
		/**
		 * @param serviceName the serviceName to set
		 */
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
		/**
		 * @return the serviceVersion
		 */
		public String getServiceVersion() {
			return serviceVersion;
		}
		/**
		 * @param serviceVersion the serviceVersion to set
		 */
		public void setServiceVersion(String serviceVersion) {
			this.serviceVersion = serviceVersion;
		}
		/**
		 * @return the packageName
		 */
		public String getPackageName() {
			return packageName;
		}
		/**
		 * @param packageName the packageName to set
		 */
		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}
		/**
		 * @return the packageVersion
		 */
		public String getPackageVersion() {
			return packageVersion;
		}
		/**
		 * @param packageVersion the packageVersion to set
		 */
		public void setPackageVersion(String packageVersion) {
			this.packageVersion = packageVersion;
		}
		/**
		 * @return the riid
		 */
		public String getRIID() {
			return riid;
		}
		/**
		 * @param riid the riid to set
		 */
		public void setRIId(String riid) {
			this.riid = riid;
		}
		/**
		 * @return the isAlive
		 */
		public boolean isAlive() {
			return isAlive;
		}
		/**
		 * @param isAlive the isAlive to set
		 */
		public void setAlive(boolean isAlive) {
			this.isAlive = isAlive;
		}
		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}
		/**
		 * @param message the message to set
		 */
		public void setMessage(String message) {
			this.message = message;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((riid == null) ? 0 : riid.hashCode());
			return result;
		}
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
			DeployedRunningInstance other = (DeployedRunningInstance) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (riid == null) {
				if (other.riid != null)
					return false;
			} else if (!riid.equals(other.riid))
				return false;
			return true;
		}
		private DeployerReport getOuterType() {
			return DeployerReport.this;
		}
	}

}
