package org.gcube.common.core.contexts.ghn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.contexts.GHNContext.Type;
import org.gcube.common.core.resources.GCUBEHostingNode.Site;
import org.gcube.common.core.resources.GCUBEResource.InvalidScopeException;
import org.gcube.common.core.resources.node.Description;
import org.gcube.common.core.resources.node.Description.Architecture;
import org.gcube.common.core.resources.node.Description.FileSystem;
import org.gcube.common.core.resources.node.Description.Load;
import org.gcube.common.core.resources.node.Description.Memory;
import org.gcube.common.core.resources.node.Description.NetworkAdapter;
import org.gcube.common.core.resources.node.Description.OperatingSystem;
import org.gcube.common.core.resources.node.Description.Processor;
import org.gcube.common.core.resources.node.Description.RuntimeEnvironment;
import org.gcube.common.core.resources.node.Description.SecurityData;
import org.gcube.common.core.resources.node.Description.RuntimeEnvironment.Variable;
import org.gcube.common.core.security.utils.HostCertificateReader;
import org.gcube.common.core.security.utils.HostCertificateReader.ExpiredCredentialsException;
import org.gcube.common.core.security.utils.HostCertificateReader.IndefiniteLifetimeCredentialsException;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

/**
 * GHN builder: build and update a GHN resource
 * 
 * @author Manuele Simi (CNR-ISTI)
 * 
 */

public class Builder {

    static GCUBELog logger = new GCUBELog(Builder.class);

    /**
     * Initialises the GHN resource within a given GHNContext
     * 
     * @param context  the context.
     * @throws InvalidScopeException if no valid scope is found for the GHN.
     */
    public synchronized static void createGHNResource(GHNContext context) throws InvalidScopeException {

		logger.setContext(context);
	
		//we are about to create the latest version of the GHN resource
		context.getGHN().setResourceVersion(context.getGHN().getLastResourceVersion());
		//INFRASTRUCTURE
		context.getGHN().setInfrastructure((String) context.getProperty(GHNContext.INFRASTRUCTURE_NAME, true));
		// SITE
		Site site = new Site();
		site.setCountry((String) context.getProperty(GHNContext.COUNTRY_JNDI_NAME, false));
		site.setLocation((String) context.getProperty(GHNContext.LOCATION_JNDI_NAME, false));
		String[] coordinates = ((String) context.getProperty(GHNContext.COORDINATES_JNDI_NAME, false)).split(",");
		site.setLongitude(coordinates[1]);
		site.setLatitude(coordinates[0]);
		try {site.setDomain(context.getPublishedHostDomain());} catch (IOException e) {site.setDomain("unable-to-detect");}
		context.getGHN().setSite(site);
		// ADAPTER
		NetworkAdapter adapter = new NetworkAdapter();
		adapter.setInboundIP("");
		adapter.setMTU(0);
		adapter.setOutboundIP("");
		adapter.setName("local-adapter");
		adapter.setIPAddress(context.getIP());
		// OS
		Properties prop = System.getProperties();
		OperatingSystem os = new OperatingSystem();
		os.setName(prop.getProperty("os.name"));
		os.setVersion(prop.getProperty("os.version"));
		os.setRelease("");
		// ARCHITECTURE
		Architecture architecture = new Architecture();
		architecture.setPlatformType(prop.getProperty("os.arch"));
		architecture.setSMPSize(0);
		architecture.setSMTSize(0);
		// DESCRIPTION
		Description description = new Description();
		description.setArchitecture(architecture);
		description.setName(context.getPublishedHostnameAndPort());
		description.getNetworkAdapters().add(adapter);
		description.setOS(os);
		// host credentials expiration
		if (((Boolean) context.getProperty(GHNContext.SECURITY_JNDI_NAME, false)) 
				&& (! GHNContext.getContext().isClientMode())) {
			description.setSecurityEnabled(true);
			SecurityData data = new SecurityData();
			GregorianCalendar calendar = new GregorianCalendar();
		    calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		    try {
				calendar.add(GregorianCalendar.SECOND, HostCertificateReader.getHostCertificateLifetime());
				data.setCredentianlsExpireOn(calendar);
			} catch (ExpiredCredentialsException e) {
				calendar.set(GregorianCalendar.YEAR, 1970);
				data.setCredentianlsExpireOn(calendar);
			} catch (IndefiniteLifetimeCredentialsException e) {
				calendar.set(GregorianCalendar.YEAR, 2010);		
				data.setCredentianlsExpireOn(calendar);
			} catch (Exception e) {
				//just skip the setting
			} 	
			
			try {
				data.setCredentialsDistinguishedName(HostCertificateReader.getHostCertificateDN());
			}catch (Exception e) {
				//just skip the setting
			} 	
			
			try {
				data.setCA(HostCertificateReader.getHostCertificateCA());
			}catch (Exception e) {
				//just skip the setting
			}
			description.addSecurityData(data);
		}
		// CPU
		ArrayList<HashMap<String, String>> CPU = context.getCPUInfo();
		List<Processor> procs = description.getProcessors();
		for (HashMap<String, String> map : CPU) {
		    Processor p = new Processor();
		    p.setBogomips(new Double(map.get("bogomips")));
		    p.setClockSpeedMHZ(new Double(map.get("cpu_MHz")));
		    p.setFamily(map.get("cpu_family"));
		    p.setModelName(map.get("model_name"));
		    p.setModel(map.get("model"));
		    p.setVendor(map.get("vendor_id"));
		    p.setCacheL1(new Long(map.get("cache_size")));
		    p.setCacheL1D(0);
		    p.setCacheL1I(0);
		    p.setCacheL2(0);
		    procs.add(p);
		}
	
		RuntimeEnvironment runtime = new RuntimeEnvironment();
		// ENV vars
		compileRuntime(context, runtime);
		description.setRuntime(runtime);
	
		// type
		String type = (String) context.getProperty(GHNContext.GHN_TYPE, false);
		if (type.compareToIgnoreCase(Type.DYNAMIC.toString()) == 0) description.setType(Description.Type.Dynamic);
		else if (type.compareToIgnoreCase(Type.STATIC.toString()) == 0)   description.setType(Description.Type.Static);
		else if (type.compareToIgnoreCase(Type.SELFCLEANING.toString()) == 0)   description.setType(Description.Type.Selfcleaning);
	
		// file system
		FileSystem fs = new FileSystem();
		fs.setName("");
		fs.setRoot(context.getLocation());
		fs.setReadOnly(false);
		
		context.getGHN().setNodeDescription(description);
		//complete the dynamic sections
		Builder.updateGHNResource(context, true);
		}

    /**
     * Used internally to update the GHN resource within a given GHNContext.
     * 
     * @param context the context
     * @param startup states if it is the first update
     */
    public synchronized static void updateGHNResource(GHNContext context, boolean ... startup) {

		logger.setContext(context);		
		
		Description description = context.getGHN().getNodeDescription();
		if (description==null) {
			logger.error("GHN description in the node profile is NULL");
			return;
		}
		
		if ((startup != null) && (startup.length > 0) && startup[0]) { //first update
			description.setActivationTime(new GregorianCalendar());
			description.setName(context.getPublishedHostnameAndPort());
			
			// recalculate ENV vars
			RuntimeEnvironment runtime = new RuntimeEnvironment();
			compileRuntime(context, runtime);
			description.setRuntime(runtime);
			
			// Recalculate SITE
			Site site = new Site();
			site.setCountry((String) context.getProperty(GHNContext.COUNTRY_JNDI_NAME, false));
			site.setLocation((String) context.getProperty(GHNContext.LOCATION_JNDI_NAME, false));
			String[] coordinates = ((String) context.getProperty(GHNContext.COORDINATES_JNDI_NAME, false)).split(",");
			site.setLongitude(coordinates[1]);
			site.setLatitude(coordinates[0]);
			try {site.setDomain(context.getPublishedHostDomain());} catch (IOException e) {site.setDomain("unable-to-detect");}
			context.getGHN().setSite(site);
		}
				
		description.setStatus(context.getStatus());
		
		// memory
		Map<String, Long> mem = context.getMemoryUsage();
		Memory memory = new Memory();
		memory.setAvailable(mem.get("MemoryAvailable"));
		memory.setSize(mem.get("MemoryTotalSize"));
		memory.setVirtualAvailable(mem.get("VirtualAvailable"));
		memory.setVirtualSize(mem.get("VirtualSize"));
		description.setMemory(memory);
		// free space
	
		description.setLocalAvailableSpace(context.getFreeSpace(context.getLocation()));
	
		// uptime
		description.setUptime(context.getUptime());
	
		// last update
		description.setLastUpdate(new GregorianCalendar());
		
		
		// host credentials expiration
		if (((Boolean) context.getProperty(GHNContext.SECURITY_JNDI_NAME, false)) 
				&& (! GHNContext.getContext().isClientMode())) {
			description.setSecurityEnabled(true);
			SecurityData data = new SecurityData();
			GregorianCalendar calendar = new GregorianCalendar();
		    calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		    try {
				calendar.add(GregorianCalendar.SECOND, HostCertificateReader.getHostCertificateLifetime());
				data.setCredentianlsExpireOn(calendar);
			} catch (ExpiredCredentialsException e) {
				calendar.set(GregorianCalendar.YEAR, 1970);
				data.setCredentianlsExpireOn(calendar);
			} catch (IndefiniteLifetimeCredentialsException e) {
				calendar.set(GregorianCalendar.YEAR, 2010);		
				data.setCredentianlsExpireOn(calendar);
			} catch (Exception e) {
				//just skip the setting
			} 
			
			try {
				data.setCredentialsDistinguishedName(HostCertificateReader.getHostCertificateDN());
			}catch (Exception e) {
				//just skip the setting
			} 	
			
			try {

				data.setCA(HostCertificateReader.getHostCertificateCA());
			}catch (Exception e) {
				//just skip the setting
			}
			
			description.addSecurityData(data);
		}
		
		
		Map<String, Double> loads = context.getLoadStatistics();
		Load load = new Load();
		load.setLast1min(loads.get("1min"));
		load.setLast5min(loads.get("5mins"));		
		load.setLast15min(loads.get("15mins"));
		description.setLoad(load);				
		
		context.getGHN().setNodeDescription(description);

    }

    /**
     * Used internally to populate the Runtime section of the GHN resource.
     * 
     * @param context
     *                the GHNContext
     * @param runtime
     *                the Runtime section.
     */
    private static void compileRuntime(GHNContext context, RuntimeEnvironment runtime) {

	Map<String, String> envvars = new HashMap<String, String>();
	DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

	try {
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	    Document doc = docBuilder.parse(context.getFile((String) context.getProperty(GHNContext.CUSTOMLABELS_JNDI_NAME, true)));

	    doc.getDocumentElement().normalize();
	    doc.getDocumentElement().getNodeName();
	    NodeList listOfVariables = doc.getElementsByTagName("Variable");

	    for (int s = 0; s < listOfVariables.getLength(); s++) {
			Node varNode = listOfVariables.item(s);
			if (varNode.getNodeType() == Node.ELEMENT_NODE) {
			    Element varElement = (Element) varNode;
	
			    // get the var name
			    NodeList keyList = varElement.getElementsByTagName("Key");
			    Element element = (Element) keyList.item(0);
	
			    NodeList textList = element.getChildNodes();
			    String key = ((Node) textList.item(0)).getNodeValue().trim();
	
			    // get the var value
			    keyList = varElement.getElementsByTagName("Value");
			    element = (Element) keyList.item(0);
	
			    textList = element.getChildNodes();
			    String value = ((Node) textList.item(0)).getNodeValue().trim();
	
			    // populate the map
			    envvars.put(key, value);
			}
	    }

	} catch (SAXParseException err) {
	    logger.error("Parsing error" + ", line " + err.getLineNumber()
		    + ", uri " + err.getSystemId());
	} catch (Exception e) {
	    logger.error("Parsing error ", e);
	}

	// append the list of ENV variables too
	envvars.putAll(System.getenv());

	List<Variable> vars = runtime.getVariables();
	for (String varname : envvars.keySet()) {
	    // a bit of filtering
	    if ((varname.compareToIgnoreCase("CLASSPATH") == 0)
		    || (varname.compareToIgnoreCase("PATH") == 0)
		    || (varname.contains("SSH")) 
		    || (varname.contains("MAIL"))
		    || (varname.compareToIgnoreCase("LS_COLORS") == 0))
		continue;
	    
	    vars.add(newVariable(varname, envvars.get(varname)));	    
	}
	
	//calculate and add the JAVA Version	
    vars.add(newVariable("Java",System.getProperty("java.version")));
    
    //gCore/gcf Version
    vars.add(newVariable("gCF-version",GHNContext.GCF_VERSION));
    
    //for back compatibility, to remove later 
    vars.add(newVariable("gCore-version",GHNContext.GCF_VERSION)); 
    		    
    //Update time 
    vars.add(newVariable("ghn-update-interval-in-secs", ((Long)context.getProperty(GHNContext.UPDATEINTERVAL_JNDI_NAME,false)).toString()));        
    
  }
    
   private static Variable newVariable(String key, String value) {
    	Variable var = new Variable();
        var.setKey(key);
        var.setValue(value);
        return var;
    }
}
