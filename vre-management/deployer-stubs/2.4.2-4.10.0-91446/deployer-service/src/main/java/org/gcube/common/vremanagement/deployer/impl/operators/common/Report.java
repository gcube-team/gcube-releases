package org.gcube.common.vremanagement.deployer.impl.operators.common;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.stubs.reporting.ReportingPortType;
import org.gcube.vremanagement.resourcemanager.stubs.reporting.SendReportParameters;
import org.gcube.vremanagement.resourcemanager.stubs.reporting.service.ReportingServiceAddressingLocator;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.contexts.ServiceContext;
import org.gcube.common.vremanagement.deployer.stubs.common.FeedbackMessage;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfoStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a report of the deployment operations to send to the
 * DLManagement Service
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class Report {
	
	protected static final long serialVersionUID = -2356724474252349627L;
			
	/**  package status on the report */
	public enum PACKAGESTATUS {
		FAILED, //an error occurred during the operation
		DEPLOYED,		
		STARTED,
		WAITING,
		ALREADYDEPLOYED,
		NOTVERIFIED, //the package has been deployed or undeployed but the operation cannot be verified
		REFERRED,
		UPDATED,
		SKIPPED,
		PATCHED, // the package has been patched
		RUNNING, // the package has successfully generated a running instance
		ACTIVATED, // the package has been deployed and activated after a container restart
		UNDEPLOYED, 
		NOTUNDEPLOYABLE, //the package cannot be undeployed
		REMOVEDFROMSCOPE, //the package has been removed from the undeployment scopes (but it's still in other scopes)
		REGISTERED, //the package has been registered (only for plugin packages)
		ALREADYREGISTERED  //the package was already registered (only for plugin packages)
		
	}
	/** report type */
	public enum TYPE {
		DEPLOY,
		UNDEPLOY,
		PATCH,
		UPDATE
	}
	
	public enum REPORTSTATUS {
		OPEN,
		CLOSED
	}
	
	/** EPR of the VREManager where to send the report*/
	private EndpointReferenceType callbackEPR = null;

	/** session ID to send back*/
	private String callbackID = "";
	
	/** report type*/
	private TYPE type;
	
	private REPORTSTATUS status = REPORTSTATUS.OPEN;

	private Map<String, PackageInfo> riids = new HashMap<String, PackageInfo>();;
	
	/** information about the deployed packages*/
	private PackageInfoStatus[] report_data;
	
	/** report last update timestamp */
	private Date lastUpdate;

	/** when it is false, the deployment has been requested by a VRE manager service*/
	private boolean localDeployment = false;
	
	private GCUBEScope callerScope;
	/**
	 * Object logger.
	 */
    protected final GCUBELog logger = new GCUBELog(this);
    
	/**
	 * Creates a new empty report
	 * 
	 * @param callbackEPR the callback EndpointReference where to send the report
	 * @param callbackID the session ID for the deployment
	 * @param numOfPackages number of packages that compose the report
	 * @param type the report type (deploy/undeploy)
	 */
	public Report(EndpointReferenceType callbackEPR, String callbackID,
			int numOfPackages, TYPE type, GCUBEScope callerScope) {
		
		this.callbackEPR = callbackEPR;
		this.callbackID = callbackID;
		this.callerScope = callerScope;
		if ((callbackEPR == null) || (callbackID == null) ||(callbackID.compareToIgnoreCase("") == 0) )
			this.localDeployment = true;
		
		this.report_data = new PackageInfoStatus[numOfPackages];
		this.type = type;
	}

	private Report() {}
	
	/**
	 * Adds a new package to the report
	 * 
	 * @param deployedpackage the information about the deployed package
	 * @param status the final status of the deployment (started/deployed/failed...)
	 * @param position the position in the report
	 *            
	 */
	public void addPackage(PackageInfo deployedpackage, PACKAGESTATUS status, int position, String ... message) {
		
		PackageInfoStatus pack = new PackageInfoStatus();
		pack.set_package(deployedpackage);
		pack.setStatus(status.name());
		if (message != null && message.length > 0) 
			pack.setMessage(message[0]);
		else
			pack.setMessage("");
		this.report_data[position] = pack;
		this.lastUpdate = new GregorianCalendar().getTime();
	}

	/**
	 * Adds a new RunningInstance ID to the report
	 * 
	 * @param riid the RI identifier
	 * @param pack the package info
	 */
	public void addRI(String riid, PackageInfo pack) {		
		this.riids.put(riid, pack);
	}

	/**
	 * Sends the report to the DLManagement Service
	 * 
	 * @throws IOException if the generation and/or the sending of the report fails 
	 */
	public void send() throws IOException  {

		FeedbackMessage message = new FeedbackMessage();
		message.setPackageInfoStatus(this.report_data);
		message.setType(this.type.name());				
		message.setRunningInstanceIDsList(null);
		message.setCallbackID(this.callbackID);		
		boolean sent = false;
		
		if (this.localDeployment || this.callbackEPR == null) {
			// print out the report
			logger.debug("Local deployment report: " );
			logger.debug(this.toXML());
			sent = true;
		} else {
			logger.debug("Sending deployment report: " );
			logger.debug(this.toXML());
			logger.debug("Trying to send report to: " + this.callbackEPR.toString());
			// try to send the report for 3 times
			int attempt = 0;			
			while ((attempt++ < 3 ) && (!(sent))) { 
				try {
					ReportingPortType pt = GCUBERemotePortTypeContext.getProxy(new ReportingServiceAddressingLocator().getReportingPortTypePort(this.callbackEPR),
					callerScope , 120000, ServiceContext.getContext());

					
					//ResourceManagerPortType pt = GCUBERemotePortTypeContext.getProxy(new ResourceManagerServiceAddressingLocator().getResourceManagerPortTypePort(this.callbackEPR), 
					//		callerScope , 120000, ServiceContext.getContext());
					pt.sendReport(new SendReportParameters(this.callbackID, this.toXML(), callerScope.toString()));
					sent = true;
				} catch (Exception e) {
					logger.error("Error while trying to contact the VREManager to send the deployment report ", e);
				}
			}
		}
		if (sent)
			logger.debug("Report sent");
		else
			logger.error("Unable to sebd back the deployment report");
	}

	/**
	 * Gets the package information in the given position in the report
	 * @param i the position in the report
	 * @return the package information found at (i) position
	 */
	public PackageInfo getPackageInfo(int i) {
		return this.report_data[i].get_package();
	}
	
	/**
	 * Gets the deployment status of the package in the given position in the report
	 * 
	 * @param i the position in the report
	 * @return the status of the package
	 */
	public String getPackageStatus(int i) {
		return this.report_data[i].getStatus();
	}
	
	/**
	 * Closes the report
	 */
	public void close() {
		this.status = REPORTSTATUS.CLOSED;
		this.lastUpdate = new GregorianCalendar().getTime();
	}
	
	private String toXML() throws IOException {
		
		StringWriter report = new StringWriter(); // serialises to a temporary writer first
		final String NS = "";
		KXmlSerializer serializer = new KXmlSerializer();
		serializer.setOutput(report);
	
		try {
			serializer.startDocument("", true);
			serializer.startTag(NS,"Report");
			if ((this.callbackEPR != null) && (this.callbackEPR.getAddress() != null ))//when contacting the service with the test-suite, most likely it is empty
				serializer.startTag(NS,"Caller").text(this.callbackEPR.getAddress().toString()).endTag(NS, "Caller");
			if (this.callerScope != null) //in case the scope is null, the package is forced to be undeployed
				serializer.startTag(NS,"CallerScope").text(this.callerScope.toString()).endTag(NS, "CallerScope");
			
			serializer.startTag(NS,"CallbackID").text(this.callbackID).endTag(NS, "CallbackID");
			serializer.startTag(NS,"GHN").text(GHNContext.getContext().getGHN().getNodeDescription().getName()).endTag(NS, "GHN");
			serializer.startTag(NS,"Type").text(this.type.name()).endTag(NS,"Type");
			serializer.startTag(NS,"Status").text(this.status.name()).endTag(NS,"Status");
			serializer.startTag(NS,"LastUpdate").text(ProfileDate.toXMLDateAndTime(this.lastUpdate)).endTag(NS,"LastUpdate");
			serializer.startTag(NS,"Packages");
			for (PackageInfoStatus p: this.report_data){
				serializer.startTag(NS,"Package");
				serializer.startTag(NS,"ServiceClass").text(p.get_package().getServiceClass()).endTag(NS, "ServiceClass");
				serializer.startTag(NS,"ServiceName").text(p.get_package().getServiceName()).endTag(NS, "ServiceName");
				serializer.startTag(NS,"ServiceVersion").text(p.get_package().getServiceVersion()).endTag(NS, "ServiceVersion");
				serializer.startTag(NS,"PackageName").text(p.get_package().getName()).endTag(NS, "PackageName");
				serializer.startTag(NS,"PackageVersion").text(p.get_package().getVersion()).endTag(NS, "PackageVersion");
				serializer.startTag(NS,"Status");
				if ( (p.getStatus().compareToIgnoreCase(PACKAGESTATUS.DEPLOYED.name()) == 0) 
					&& (this.status == REPORTSTATUS.CLOSED))
					serializer.text(PACKAGESTATUS.ACTIVATED.name());
				else 
					serializer.text(p.getStatus());
				serializer.endTag(NS, "Status");
				serializer.startTag(NS,"Host").text(GHNContext.getContext().getHostnameAndPort()).endTag(NS, "Host");											
				serializer.startTag(NS,"Message").text(p.getMessage()).endTag(NS, "Message");
				serializer.endTag(NS,"Package");
			}
			serializer.endTag(NS,"Packages");
			if (this.type == TYPE.DEPLOY) {
				serializer.startTag(NS,"NewInstances");
				for (String riid: this.riids.keySet()){
					serializer.startTag(NS,"NewInstance");
					serializer.startTag(NS,"ID").text(riid).endTag(NS, "ID");
					serializer.startTag(NS,"ServiceClass").text(this.riids.get(riid).getServiceClass()).endTag(NS, "ServiceClass");
					serializer.startTag(NS,"ServiceName").text(this.riids.get(riid).getServiceName()).endTag(NS, "ServiceName");
					serializer.startTag(NS,"ServiceVersion").text(this.riids.get(riid).getServiceVersion()).endTag(NS, "ServiceVersion");
					serializer.startTag(NS,"PackageName").text(this.riids.get(riid).getName()).endTag(NS, "PackageName");
					serializer.startTag(NS,"PackageVersion").text(this.riids.get(riid).getVersion()).endTag(NS, "PackageVersion");
					serializer.endTag(NS,"NewInstance");
				}
				serializer.endTag(NS,"NewInstances");
			}
			serializer.endTag(NS,"Report");
			serializer.endDocument();
		}
		catch (Exception e) {
			logger.error("The report does not have a valid serialisation ", e);
			throw new IOException("The report does not have a valid serialisation " + e.getMessage());
		}
		finally {
			report.close();
		}		
		return report.toString();
	}
	
	/**
	 * Saves the report on the local file system
	 * 
	 * @throws IOException if the saving fails
	 */
	public synchronized void save() throws IOException {
		FileWriter file = new FileWriter(getReportFile(this.callbackID));
		logger.trace("Saving report to " + getReportFile(this.callbackID));
		file.write(this.toXML());
		file.flush();
		file.close();
	}
	
	
	public synchronized boolean delete()  throws IOException {
		
		return getReportFile(this.callbackID).delete();
		
	}
	private static File getReportFile(String id) throws IOException {
		return new File(Configuration.REPORTDIR + File.separator + id + ".xml");
		
	}

	/**
	 * Loads the deployment report
	 * 
	 * @param id the ID of the report to load
	 * @return the report
	 * @throws IOException if the report cannot be loaded
	 * @throws ReportNotValidException if the string is not a valid report serialization 
	 * @throws Exception if the report is not found 
	 */
	public static Report load(String id) throws ReportNotFoundException, IOException, ReportNotValidException, Exception {
		
		GCUBELog logger = new GCUBELog(Report.class);
		logger.trace("Checking the report ID...");
		if ((id == null) || (id.compareTo("") == 0))
			throw new ReportNotFoundException();
		logger.trace("Opening the report...");
		File f = getReportFile(id);
		logger.trace("Checking the report file at " + f.getAbsolutePath());
		if (! f.exists())			
			throw new ReportNotFoundException();
		logger.trace("Deserializing the report...");		
		BufferedReader br = new BufferedReader(new FileReader(f));
		StringBuilder report = new StringBuilder();
		String s;
		while((s = br.readLine()) != null) {
			report.append(s);
		}
		 
		br.close();		
		return parse(report.toString());
	}

	/**
	 * Parses the input string and creates a new report from its content
	 * 
	 * @param report a deployer report serialization
	 * @return the report
	 * @throws ReportNotValidException if the string is not a valid report serialization
	 */
	private static Report parse(String report) throws ReportNotValidException, Exception {
		GCUBELog logger = new GCUBELog(Report.class);
		logger.debug("Parsing report " + report);
		Report reportobj = new Report();
		KXmlParser parser = new KXmlParser();
		try {
			parser.setInput(new BufferedReader(new StringReader(report)));		
			loop: while (true) {				
					switch (parser.next()) {
					case KXmlParser.START_TAG:
						if (parser.getName().equals("CallbackID")) reportobj.callbackID = parser.nextText();
						else  if (parser.getName().equals("Caller")) {
							reportobj.callbackEPR = new EndpointReferenceType();
							reportobj.callbackEPR.setAddress(new Address(parser.nextText().trim()));
						}
						else  if (parser.getName().equals("CallerScope")) reportobj.callerScope = GCUBEScope.getScope(parser.nextText());
						else  if (parser.getName().equals("Type")) reportobj.type =Report.TYPE.valueOf(parser.nextText());
						else  if (parser.getName().equals("LastUpdate")) reportobj.lastUpdate = ProfileDate.fromXMLDateAndTime(parser.nextText());
						else  if (parser.getName().equals("Status")) reportobj.status = Report.REPORTSTATUS.valueOf(parser.nextText());
						else  if (parser.getName().equals("Packages")) parsePackages(parser,reportobj);
						break;
					case KXmlParser.END_DOCUMENT: break loop;
					}								 
			}
			return reportobj;
		} catch (Exception e ) {logger.error("Unable to parse the Deployer Report ", e); throw e;}
	}
	
	/**
	 * Loads the packages section of a serialized report
	 * 
	 * @param parser a parser over a report serialization
	 * @param report the {@link Report} to fill
	 * @throws Exception if the serialization is not valid
	 */
	private static void parsePackages(KXmlParser parser, Report report) throws Exception {				
		List<PackageInfoStatus> packagelist = new ArrayList<PackageInfoStatus>();
		loop: while (true) {
			try {
				switch (parser.next()) {
					case KXmlParser.START_TAG:
						if (parser.getName().equals("Package")) {														
							PackageInfoStatus pstatus = new PackageInfoStatus();
							pstatus.set_package(new PackageInfo());
							innerloop: while (true) {
								switch (parser.next()) {
									case KXmlParser.START_TAG:										
										if (parser.getName().equals("ServiceClass")) {pstatus.get_package().setServiceClass(parser.nextText());}
										else if (parser.getName().equals("ServiceName")) {pstatus.get_package().setServiceName(parser.nextText());}
										else if (parser.getName().equals("ServiceVersion")) {pstatus.get_package().setServiceVersion(parser.nextText());}
										else if (parser.getName().equals("PackageName")) {pstatus.get_package().setName(parser.nextText());}
										else if (parser.getName().equals("PackageVersion")) {pstatus.get_package().setVersion(parser.nextText());}
										else if (parser.getName().equals("Status")) {pstatus.setStatus(parser.nextText());}										
										else if (parser.getName().equals("Message")) {pstatus.setMessage(parser.nextText());}
										else parser.nextText();//insane, but it skips the end_tags otherwise
										break;
									case KXmlParser.END_TAG:										
										if (parser.getName().equals("Package")){
											packagelist.add(pstatus);											 
											break innerloop;
										}	
										break;
									case KXmlParser.END_DOCUMENT: throw new Exception ("Parsing failed at Package");	
								}														
							}
						} 
						break;					
					case KXmlParser.END_TAG: if (parser.getName().equals("Packages")) break loop; break;										
					case KXmlParser.END_DOCUMENT: throw new Exception ("Parsing failed at Packages");
				}				
			} catch (Exception e) {
				throw e;
			} 
		}
		report.report_data = new PackageInfoStatus[packagelist.size()];
		report.report_data = packagelist.toArray(report.report_data);	
	}

	/**
	 * Updates the status of the given package in the report
	 * @param p the package to update
	 * @param status the new status
	 * @param message the message associated to the new status
	 */
	public void updatePackageStatus(PackageInfo p, PACKAGESTATUS status, String ... message) {		
		for (PackageInfoStatus inpackagestatus : this.report_data) {
			if ( (inpackagestatus.get_package().getName().equalsIgnoreCase(p.getName()))
				&& (inpackagestatus.get_package().getVersion().equalsIgnoreCase(p.getVersion()))
				&& (inpackagestatus.get_package().getServiceClass().equalsIgnoreCase(p.getServiceClass()))
				&& (inpackagestatus.get_package().getServiceName().equalsIgnoreCase(p.getServiceName()))
				&& (inpackagestatus.get_package().getServiceVersion().equalsIgnoreCase(p.getServiceVersion()))) {
				inpackagestatus.setStatus(status.name());
				if (message != null && message.length > 0) 
					inpackagestatus.setMessage(message[0]);
				if (status ==  PACKAGESTATUS.RUNNING) {
					try {
						this.addRI(GHNContext.getContext().getServiceContext(p.getServiceClass(), p.getServiceName()).getInstance().getID(), p);
					} catch (Exception e) {
						logger.error("Unable to add the instance information",e);
						logger.warn("Unable to add the instance information for " + p.getServiceName());
					}
				}
				this.lastUpdate = new GregorianCalendar().getTime();
				logger.debug("package updated");
			}
		}		
	}

	/** ReportNotFoundException exception  */
	public static class ReportNotFoundException  extends Exception{private static final long serialVersionUID = 1L;};
	
	/** ReportNotValidException exception  */
	public static class ReportNotValidException  extends Exception{private static final long serialVersionUID = 1L;};
}
