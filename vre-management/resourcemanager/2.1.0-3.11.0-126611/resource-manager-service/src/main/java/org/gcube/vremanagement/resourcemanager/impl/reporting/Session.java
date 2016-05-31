package org.gcube.vremanagement.resourcemanager.impl.reporting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.reporting.DeployerReport.DeployedRunningInstance;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource.STATUS;
import org.gcube.vremanagement.resourcemanager.impl.resources.software.Dependency;
import org.gcube.vremanagement.resourcemanager.impl.resources.software.DeployedDependency;
import org.gcube.vremanagement.resourcemanager.impl.state.ProfileDate;
import org.kxml2.io.KXmlSerializer;

/**
 * Session for each service operation. 
 * It holds all the information related to the activities performed to
 * satisfy the caller's request.
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class Session implements Serializable {

	protected final GCUBELog logger = new GCUBELog(this, ServiceContext.getContext());
	
	final static String NS = "";		
	
	/**	 */
	private static final long serialVersionUID = 1180822699138069365L;
	
	private static final String reportDir = "reports";
	
	/** report last update timestamp */
	private Calendar lastUpdate = new GregorianCalendar();
	
	private String id = "";
	
	private OPERATION operation;
	
	private GCUBEScope scope = null;
	
	private boolean brokerWasSuccessful, brokerReportAvailable = false;
	
	private String brokerMessage = "";
	
	/** ghn id -> deployer report map*/
	private Map<String, DeployerReport> node2report = Collections.synchronizedMap(new HashMap<String, DeployerReport>());
	
	/** Status of a dependency resolver request */
	public enum DEPSTATUS {SUCCESS, FAILED};
	
	public enum OPERATION {Create, AddResources, UpdateResources, RemoveResources, Dispose};
	
	private Set<ScopedDeployedSoftware> services = Collections.synchronizedSet(new HashSet<ScopedDeployedSoftware>());
	
	private Set<ScopedResource> resources = Collections.synchronizedSet(new HashSet<ScopedResource>());

	/** the instances deployed by the services included in this report*/
	private Set<DeployedRunningInstance> instances = new HashSet<DeployedRunningInstance>();

	private String deploymentPlan;
	
	private Timer timer; 
	
	boolean forcedToBeClosed = false;

	String sessionErrorMessage = "";
	
	/** internally used by {@link Session#loadAsString(String)}*/
	private Session () {}
	
	/**
	 * Builds a new empty report
	 * @param id the session ID assigned to the operation
	 */
	public Session(String id, OPERATION operation, GCUBEScope ... scope) {
		this.id = id;
		this.operation = operation;
		if ((scope == null)|| (scope[0] == null))
			this.scope = ServiceContext.getContext().getInstance().getScopes().values().iterator().next();
		else
			this.scope = scope[0];			
		timer = new Timer("SessionTimer"+id);
	}

	/**
	 * Adds a Deployer Report to the Resource Report
	 * @param report the string representation of the report, as sent by a Deployer service
	 * @throws Exception if a problem in the report parsing occurs
	 */
	synchronized public void addGHNReport(DeployerReport report) throws Exception {
		node2report.put(report.getGHNName(), report);
		this.lastUpdate = new GregorianCalendar();
	}
	
	synchronized public void addResource(ScopedResource resource) {	
		this.resources.add(resource);
		this.lastUpdate = new GregorianCalendar();
	}

	synchronized  public void addService(ScopedDeployedSoftware service) {
		this.services.add(service);
		this.lastUpdate = new GregorianCalendar();
	} 
	/**
	 * 
	 */
	public String toXML() throws IOException {
		StringWriter report = new StringWriter(); // serialises to a temporary writer first		
		KXmlSerializer serializer = new KXmlSerializer();
		serializer.setOutput(report);
		//serializer.setProperty(XmlSerializer, "\t");		
		try {
			serializer.startDocument("UTF-8", true);
			serializer.startTag(NS,"ResourceReport");
			serializer.startTag(NS,"ID").text(this.id).endTag(NS, "ID");
			if (this.forcedToBeClosed) {
				serializer.startTag(NS,"Status").text("CLOSED").endTag(NS, "Status");
				serializer.startTag(NS,"SessionExitStatus").text("FAILED").endTag(NS, "SessionExitStatus");
				serializer.startTag(NS,"SessionErrorMessage").text(this.sessionErrorMessage).endTag(NS, "SessionErrorMessage");
			} else {
				if (this.isSessionClosed()) {
					serializer.startTag(NS,"Status").text("CLOSED").endTag(NS, "Status");
					serializer.startTag(NS,"SessionExitStatus").text("SUCCESS").endTag(NS, "SessionExitStatus");
				} else
					serializer.startTag(NS,"Status").text("OPEN").endTag(NS, "Status");

			}
			serializer.startTag(NS,"Operation").text(this.operation.toString()).endTag(NS, "Operation");
			serializer.startTag(NS,"LastUpdate").text(ProfileDate.toXMLDateAndTime(this.lastUpdate.getTime())).endTag(NS,"LastUpdate");
			serializer.startTag(NS,"TargetScope").text(this.scope.toString()).endTag(NS,"TargetScope");
			
			//resources section
			serializer.startTag(NS,"Resources");
			for (ScopedResource resource : resources) {
				serializer.startTag(NS,"Resource");
				serializer.startTag(NS,"ID").text(resource.getId()).endTag(NS,"ID");
				serializer.startTag(NS,"Type").text(resource.getType()).endTag(NS,"Type");
				if (resource.isSuccess()) {
					if (this.operation == OPERATION.AddResources)
						serializer.startTag(NS,"Status").text("ADDED").endTag(NS,"Status");
					else 
						serializer.startTag(NS,"Status").text("REMOVED").endTag(NS,"Status");
					//serializer.startTag(NS,"ErrorDescription").text("-").endTag(NS,"ErrorDescription");
				} else {
					serializer.startTag(NS,"Status").text("FAILED").endTag(NS,"Status");
					serializer.startTag(NS,"ErrorDescription").text(resource.getErrorMessage()).endTag(NS,"ErrorDescription");
				}					
				serializer.endTag(NS,"Resource");
			}
			
			serializer.endTag(NS,"Resources");
			//broker section
			if (brokerReportAvailable) {
				serializer.startTag(NS,"DeploymentPlanCreation");
				serializer.startTag(NS,"Status").text(this.brokerWasSuccessful?"SUCCESS":"FAILED").endTag(NS, "Status");
				serializer.startTag(NS,"Message").text(this.brokerMessage).endTag(NS, "Message");
				serializer.endTag(NS,"DeploymentPlanCreation");				
			}
			//services
			serializer.startTag(NS,"Software");			
			for (ScopedDeployedSoftware service : this.services) {
				serializer.startTag(NS,"Service");
				serializer.startTag(NS,"ID").text(service.getId()).endTag(NS, "ID");
				serializer.startTag(NS,"Class").text(service.getSourcePackage().getClazz()).endTag(NS, "Class");
				serializer.startTag(NS,"Name").text(service.getSourcePackage().getName()).endTag(NS, "Name");
				serializer.startTag(NS,"Version").text(service.getSourcePackage().getVersion()).endTag(NS, "Version");
				if (service.getSourcePackage().getPackageName() != null)
					serializer.startTag(NS,"PackageName").text(service.getSourcePackage().getPackageName()).endTag(NS, "PackageName");
				if (service.getSourcePackage().getPackageVersion() != null)
					serializer.startTag(NS,"PackageVersion").text(service.getSourcePackage().getPackageVersion()).endTag(NS, "PackageVersion");
				
				if (((this.operation == OPERATION.AddResources) ||(this.operation == OPERATION.Create)) 
						&& (service.getStatus() != STATUS.LOST) && (this.brokerWasSuccessful)) {
					if ( (service.getMissingDependencies(service.getTargetNodeName()).size() > 0) || (service.getErrorMessage().length() > 0) ) { 
						serializer.startTag(NS,"DependenciesResolutionStatus").text(DEPSTATUS.FAILED.name()).endTag(NS, "DependenciesResolutionStatus");
						serializer.startTag(NS,"DeployedOn").text("not deployed").endTag(NS, "DeployedOn");
						serializer.startTag(NS,"ErrorDescription").text(service.getErrorMessage()).endTag(NS, "ErrorDescription");				
					}else { 
						serializer.startTag(NS,"DependenciesResolutionStatus").text(DEPSTATUS.SUCCESS.name()).endTag(NS, "DependenciesResolutionStatus");
						serializer.startTag(NS,"DeployedOn").text(service.getTargetNodeName()).endTag(NS, "DeployedOn");
						serializer.startTag(NS,"ErrorDescription").text("-").endTag(NS, "ErrorDescription");
					}
					serializer.startTag(NS,"DependenciesResolution");
					//resolved dependencies
					serializer.startTag(NS,"ResolvedDependencies");
					for (Dependency dep : service.getLastResolvedDependencies()) {
						serializer.startTag(NS,"Dependency");
						serializer.startTag(NS,"ServiceClass").text(dep.getService().getClazz()).endTag(NS, "ServiceClass");
						serializer.startTag(NS,"ServiceName").text(dep.getService().getName()).endTag(NS, "ServiceName");
						serializer.startTag(NS,"ServiceVersion").text(dep.getService().getVersion()).endTag(NS, "ServiceVersion");
						serializer.startTag(NS,"PackageName").text(dep.getName()).endTag(NS, "PackageName");
						serializer.startTag(NS,"PackageVersion").text(dep.getVersion()).endTag(NS, "PackageVersion");					
						serializer.endTag(NS,"Dependency");
					}
					serializer.endTag(NS,"ResolvedDependencies");				
					//missing dependencies
					serializer.startTag(NS,"MissingDependencies");
					for (Dependency dep : service.getLastMissingDependencies()) {
						serializer.startTag(NS,"Dependency");
						serializer.startTag(NS,"ServiceClass").text(dep.getService().getClazz()).endTag(NS, "ServiceClass");
						serializer.startTag(NS,"ServiceName").text(dep.getService().getName()).endTag(NS, "ServiceName");
						serializer.startTag(NS,"ServiceVersion").text(dep.getService().getVersion()).endTag(NS, "ServiceVersion");
						serializer.startTag(NS,"PackageName").text(dep.getName()).endTag(NS, "PackageName");
						serializer.startTag(NS,"PackageVersion").text(dep.getVersion()).endTag(NS, "PackageVersion");					
						serializer.endTag(NS,"Dependency");
					}
					serializer.endTag(NS,"MissingDependencies");
					serializer.endTag(NS,"DependenciesResolution");
				}
				//add the deployment report if it is available
				String targetNodeName = service.getTargetNodeName();
				if (this.operation == OPERATION.AddResources)
					serializer.startTag(NS,"DeploymentActivity");
				else 
					serializer.startTag(NS,"UndeploymentActivity");
				if (node2report.keySet().contains(targetNodeName)) {					
					serializer.startTag(NS,"GHN");
					//serializer.startTag(NS,"LogicalName").text(service.getTargetNodeName()).endTag(NS, "LogicalName");
					serializer.startTag(NS,"Host").text(node2report.get(targetNodeName).getHost()).endTag(NS,"Host");				
					serializer.startTag(NS,"LastReportReceivedOn").text(ProfileDate.toXMLDateAndTime(node2report.get(targetNodeName).getLastUpdate())).endTag(NS,"LastReportReceivedOn");
					serializer.startTag(NS,"LastReportReceived");
					serializer.startTag(NS,"Status").text(node2report.get(targetNodeName).getStatus()).endTag(NS, "Status");
					this.addGHNReport(serializer, node2report.get(targetNodeName), service);
					serializer.endTag(NS, "LastReportReceived");
					serializer.endTag(NS,"GHN");					
					DeployedRunningInstance instance = this.getInstanceForService(service);
					if ((this.operation == OPERATION.AddResources) 
							|| (this.operation == OPERATION.Create)){
						if (instance != null) {
								serializer.startTag(NS,"RelatedRunningInstance");
								if (instance.isAlive())
									serializer.startTag(NS, "ID").text(instance.getRIID()).endTag(NS, "ID");
								serializer.startTag(NS,"Status").text(instance.isAlive()? "SUCCESS" : "FAILED").endTag(NS,"Status");
								serializer.startTag(NS,"Message").text(instance.getMessage()).endTag(NS,"Message");							
								serializer.endTag(NS,"RelatedRunningInstance");
							
						} else if (node2report.get(targetNodeName).getStatus().compareToIgnoreCase("CLOSED") == 0 ) {
							serializer.startTag(NS,"RelatedRunningInstance");
							serializer.startTag(NS,"Status").text("FAILED").endTag(NS,"Status");
							serializer.startTag(NS,"Message").text("The Deployer service did not detect any new instance of this service on the target gHN").endTag(NS,"Message");
							serializer.endTag(NS,"RelatedRunningInstance");			
						}
					}
				} else {
					if (service.isSuccess() && (this.brokerReportAvailable)) 
						serializer.text("The report is still not available for this service");
					else
						serializer.text("No report");
				}
				if (this.operation == OPERATION.AddResources) {
					serializer.endTag(NS,"DeploymentActivity");					
				}
				else 
					serializer.endTag(NS,"UndeploymentActivity");
				
				serializer.endTag(NS,"Service");
			}			
			serializer.endTag(NS,"Software");					
			serializer.endTag(NS,"ResourceReport");
			serializer.endDocument();
		}
		catch (Exception e) {
			logger.error("The Resource Report does not have a valid serialisation", e);
			throw new IOException("The Resource Report does not have a valid serialisation "+ e.getMessage());
		}
		finally {
			report.close();
		}		
		return report.toString();		
	}

	/**
	 * Gets the deployed instance (if any) for the given service
	 * @param service the service
	 * @return the instance or null if no instances was found
	 */
	private DeployedRunningInstance getInstanceForService(ScopedDeployedSoftware service) {		
		for (DeployedRunningInstance instance : this.instances) {			
			if ( (instance.getServiceClass().compareToIgnoreCase(service.getSourcePackage().getClazz()) == 0)
					&& (instance.getServiceName().compareToIgnoreCase(service.getSourcePackage().getName()) == 0)) {
					return instance;
			}
		}
		return null;
	}

	/**
	 * @return the lastUpdate
	 */
	public Calendar getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @return the resource report identifier
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the scope this report belongs to
	 */
	public GCUBEScope getScope() {
		return scope;
	}
	
	/**
	 * Saves the report on the local file system
	 * 
	 * @throws IOException if the saving fails
	 */
	synchronized public void save() throws IOException {
		FileWriter file = new FileWriter(getReportFile(this.id));
		file.write(this.toXML());
		file.close();
	}
	
	/**
	 * Loads the report from the file system
	 * 
	 * @param id the report ID
	 * @return the report
	 * @throws IOException if the report does not have a valid serialization
	 */
	public static Session load(String id) throws IOException {		
		Session report = new Session();
		// load the  report from its serialization
		
		return report;
	}
	
	/**
	 * Loads the report from the file system
	 * 
	 * @param id the report ID
	 * @return the string representation of the report
	 * @throws IOException if the report does not have a valid serialization
	 */
	public static String loadAsString(String id) throws IOException {		
		
		// load the report serialization
		
		File f = getReportFile(id);
		if (! f.exists())			
			throw new IOException("Unable to find a serialized report with ID=" + id);
				
		BufferedReader br = new BufferedReader(new FileReader(f));
		StringBuilder report = new StringBuilder();
		String s;
		while((s = br.readLine()) != null) {
			report.append(s);
		}
		 
		br.close();
		return report.toString();
	}
	
	private static File getReportFile(String id) throws IOException {
		return new File(ServiceContext.getContext().getConfigurationFileAbsolutePath(reportDir) + File.separator + id + ".xml");
		
	}
	
	private void addGHNReport(KXmlSerializer serializer, DeployerReport report, ScopedDeployedSoftware service) throws Exception {		
		serializer.startTag(NS, "Packages");
		for (DeployedDependency dep: report.getDependencies()) {
			if (this.isDepOfService(dep, service)) {
				serializer.startTag(NS,"Package");
				serializer.startTag(NS,"ServiceClass").text(dep.getService().getClazz()).endTag(NS, "ServiceClass");
				serializer.startTag(NS,"ServiceName").text(dep.getService().getName()).endTag(NS, "ServiceName");
				serializer.startTag(NS,"ServiceVersion").text(dep.getService().getVersion()).endTag(NS, "ServiceVersion");
				serializer.startTag(NS,"PackageName").text(dep.getName()).endTag(NS, "PackageName");
				serializer.startTag(NS,"PackageVersion").text(dep.getVersion()).endTag(NS, "PackageVersion");
				serializer.startTag(NS,"Status").text(dep.getStatus()).endTag(NS, "Status");													
				serializer.startTag(NS,"Message").text(dep.getMessage()).endTag(NS, "Message");
				serializer.endTag(NS,"Package");
			}
		}
		serializer.endTag(NS, "Packages");
		
	}

	public synchronized void reportBrokerWork(boolean wasSuccessful, String brokerMessage) {
		this.brokerReportAvailable = true;
		this.brokerWasSuccessful = wasSuccessful;
		this.brokerMessage = brokerMessage;
		
	}

	/**
	 * Adds newly deployed instances to the report. These instances are reported to be activated by the Deployer following a deployment
	 * request
	 *  
	 * @param instances the new instances
	 */
	public void addDeployedInstances(Set<DeployedRunningInstance> instances) {
		if ((instances == null) || (instances.size() == 0))
			return;
		this.instances.addAll(instances);
	}
	
	/**
	 * Checks if a dependency belongs to a specific service
	 * @param dep the dependency to check
	 * @param service the potential owner service 
	 * @return true if the dependency belongs the service
	 */
	private boolean isDepOfService(DeployedDependency dep, ScopedDeployedSoftware service) {		
		for (Dependency resolvedDependency : service.getLastResolvedDependencies()) {			
			if (dep.equals((Dependency)resolvedDependency)) return true;			
		}
		return false;
	}
	
	/**
	 * Checks whether the report is closed or no
	 * @return true if the report is closed, false otherwise
	 */
	public boolean isSessionClosed() {
		
		for ( ScopedResource resource  : this.resources) {
			if ((resource.getStatus() == STATUS.ADDREQUESTED) || (resource.getStatus() == STATUS.REMOVEREQUESTED))
				return false;//the resource still need to be managed
		}
		
		for (ScopedDeployedSoftware service : this.services) {
			String reportHostName = service.getTargetNodeName();
			if (service.isSuccess() && (this.brokerWasSuccessful)) {
				//check the availability of the report and its status			
				if (! node2report.keySet().contains(reportHostName)) {	
					return false;
				} else if (node2report.get(reportHostName).getStatus().compareToIgnoreCase("CLOSED") != 0){
					return false;
				} 
			}
		}		
		return true;
	}

	public Set<ScopedDeployedSoftware> getServices() {
		return this.services;
	}

	/**
	 * Gets all the GHN reports received
	 * @return a map where the key is the GHN ID and the value is the related report
	 */
	public  Map<String, DeployerReport> getAllGHNReports() {
		return node2report;
	}

	/**
	 * @return the {@link OPERATION} related to this report
	 */
	public OPERATION getOperation() {
		return operation;
	}

	public void setDeploymentPlan(String plan) {
		this.deploymentPlan = plan;
	}
	
	public String getDeploymentPlan() throws IOException {
		if (this.deploymentPlan == null)
			 throw new IOException("No Deployment Plan is available");
		return this.deploymentPlan;
	}
	
	/**
	 * Starts the session checker
	 */
	public void startChecker() {
		logger.debug("Starting the Session Checker");
		try {
			this.timer.schedule(new SessionChecker(), 600000);
		} catch (Exception e) {logger.warn("Session Checker was not started (timer already cancelled)");}
	}

	class SessionChecker extends TimerTask {

		@Override
		public void run() {
			Session.this.logger.debug("Session Checker woke up");
			//check if there is nothing to do
			if ((Session.this.isSessionClosed())||(Session.this.forcedToBeClosed)) {
				try {this.cancel(); return;} catch (Exception e) {}
			}
			for ( ScopedResource resource  : Session.this.resources) {
				if ((resource.getStatus() == STATUS.ADDREQUESTED) 
						|| (resource.getStatus() == STATUS.REMOVEREQUESTED)) {
					//the resource has not been managed
					resource.setStatus(STATUS.LOST);
					resource.setErrorMessage("The resource was not managed properly in a reasonable time (10 mins)");
				}
			}
			
			for (ScopedDeployedSoftware service : Session.this.services) {
				String reportHostName = service.getTargetNodeName();
				if (service.isSuccess() && (Session.this.brokerWasSuccessful)) {
					//check the availability of the report and its status			
					if (! node2report.keySet().contains(reportHostName)) {	
						Session.this.forcedToBeClosed = true;
						Session.this.sessionErrorMessage = "Node " + reportHostName + " did not send any report on time";
						Session.this.logger.debug("Session Checker forced the session to close:" + Session.this.sessionErrorMessage);;
					} else if (node2report.get(reportHostName).getStatus().compareToIgnoreCase("CLOSED") != 0){
						Session.this.forcedToBeClosed = true;
						Session.this.sessionErrorMessage = "Report from node " + reportHostName + " was not closed on time";
						Session.this.logger.debug("Session Checker forced the session to close:" + Session.this.sessionErrorMessage);;

					} 
				}
			}
			try {
				Session.this.save();//save the new report
				logger.debug("Cancelling the Session Checker");
				this.cancel(); //cancel only if we saved
			} catch (Exception e) {}
		}
		
	}
}
