package org.gcube.smartgears.handlers.container.lifecycle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.HostingNode.Profile.NodeDescription.GHNType;
import org.gcube.common.resources.gcore.HostingNode.Profile.NodeDescription.Processor;
import org.gcube.common.resources.gcore.HostingNode.Profile.NodeDescription.Variable;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.configuration.library.SmartGearsConfiguration;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.provider.ProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fabio Simeoni
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class ProfileBuilder {

	private static Logger log = LoggerFactory.getLogger(ProfileBuilder.class);

	private ContainerContext context;

	public ProfileBuilder(ContainerContext context) {
		this.context = context;
	}

	public HostingNode create() {

		HostingNode node = new HostingNode();
		
		ContainerConfiguration cfg = context.configuration();
		
		node.newProfile().infrastructure(cfg.infrastructure());

		addSiteTo(node);

		String ip = "not resolved";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.warn("unable to detect the IP address of the host");
		}

		node.profile().newDescription().activationTime(Calendar.getInstance()).name(cfg.hostname() + ":" + cfg.port());

		node.profile().description().networkAdapters().add().mtu(0).name("local-adapter").ipAddress(ip).inboundIP("")
				.outboundIP("");

		node.profile().description().newOperatingSystem().name(System.getProperty("os.name"))
				.version(System.getProperty("os.version")).release("");

		node.profile().description().newArchitecture().platformType(System.getProperty("os.arch")).smpSize(0)
				.smtSize(0);

		ArrayList<HashMap<String, String>> info = cpuInfo();

		Group<Processor> processors = node.profile().description().processors();

		for (HashMap<String, String> map : info)

			processors.add().bogomips(new BigDecimal(map.get("bogomips")))
					.clockSpeedMhz(new BigDecimal(map.get("cpu_MHz"))).family(map.get("cpu_family"))
					.modelName(map.get("model_name")).model(map.get("model")).vendor(map.get("vendor_id"))
					.cacheL1(new Integer(map.get("cache_size"))).cacheL1D(0).cacheL1I(0).cacheL2(0);

		addVariablesTo(node);

		update(node,false);

		node.profile().description().type(GHNType.Static);
		// String type = (String) context.getProperty(GHNContext.GHN_TYPE, false);
		// if (type.compareToIgnoreCase(Type.DYNAMIC.toString()) == 0) description.setType(Description.Type.Dynamic);
		// else if (type.compareToIgnoreCase(Type.STATIC.toString()) == 0) description.setType(Description.Type.Static);
		// else if (type.compareToIgnoreCase(Type.SELFCLEANING.toString()) == 0)
		// description.setType(Description.Type.Selfcleaning);
		//
		// file system
		node.profile().description().localFileSystems().add().name("").type("").readOnly(false)
				.root(cfg.persistence().location());

		return node;
	}

	@SuppressWarnings("all")
	private ArrayList<HashMap<String, String>> cpuInfo() {

		ArrayList<HashMap<String, String>> map = new ArrayList<HashMap<String, String>>();

		File file = new File("/proc/cpuinfo");

		if (!file.exists()) {
			log.warn("cannot acquire CPU info (no /proc/cpuinfo)");
			return map;
		}

		BufferedReader input = null;

		try {
			input = new BufferedReader(new FileReader(file));

			String line = null;

			HashMap<String, String> currentProcessor = null;

			while ((line = input.readLine()) != null) {

				if ((line.startsWith("processor"))) { // add the current processor to the map

					if (currentProcessor != null)
						map.add((HashMap) currentProcessor.clone());

					currentProcessor = new HashMap<String, String>();
				}

				try {
					if (line.contains("vendor_id"))
						currentProcessor.put("vendor_id", line.split(":")[1].trim());
				} catch (Exception ex) {
				}
				try {
					if (line.contains("cpu family"))
						currentProcessor.put("cpu_family", line.split(":")[1].trim());
				} catch (Exception ex) {
				}
				try {
					if ((line.contains("model\t")) || (line.contains("model\b")))
						currentProcessor.put("model", line.split(":")[1].trim());
				} catch (Exception ex) {
				}
				try {
					if (line.contains("model name"))
						currentProcessor.put("model_name", line.split(":")[1].trim());
				} catch (Exception ex) {
				}
				try {
					if (line.contains("cpu MHz"))
						currentProcessor.put("cpu_MHz", line.split(":")[1].trim());
				} catch (Exception ex) {
				}
				try {
					if (line.contains("cache size"))
						currentProcessor.put("cache_size", line.split(":")[1].trim().split(" ")[0]);
				} catch (Exception ex) {
				}
				try {
					if (line.contains("bogomips"))
						currentProcessor.put("bogomips", line.split(":")[1].trim());
				} catch (Exception ex) {
				}
			}

			if (currentProcessor != null)
				map.add(currentProcessor);

		} catch (Exception e) {

			log.warn("unable to acquire CPU info", e);

		} finally {

			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					log.warn("unable to close stream", e);
				}
		}
		return map;
	}

	private long getFreeSpace() {
		long free = 0;
		try {
			free = Files.getFileStore(Paths.get(context.configuration().persistence().location())).getUsableSpace()/1024;
		} catch (IOException ioe) {
			log.warn("unable to detect the free space on the disk", ioe);
		}
		return free;
	}
	
	public void update(HostingNode node,boolean onLoad) {

		ContainerConfiguration cfg = context.configuration();

		if (onLoad) {

			log.info("updating ghn profile");

			node.profile().description().activationTime(Calendar.getInstance()).name(cfg.hostname() + ":" + cfg.port());

			addVariablesTo(node);

			addSiteTo(node);

		}

		node.profile().description().status(context.lifecycle().state().remoteForm());

		Map<String, Long> mem = memoryUsage();

		node.profile().description().newMainMemory().ramAvailable(mem.get("MemoryAvailable"))
				.ramSize(mem.get("MemoryTotalSize")).virtualAvailable(mem.get("VirtualAvailable"))
				.virtualSize(mem.get("VirtualSize"));

		node.profile().description().localAvailableSpace(getFreeSpace());

		node.profile().description().uptime(uptime());

		node.profile().description().lastUpdate(Calendar.getInstance());

		Map<String, Double> loads = loadStatistics();

		node.profile().description().newLoad().lastMin(loads.get("1min") == null ? 0 : loads.get("1min"))
				.last5Mins(loads.get("5mins") == null ? 0 : loads.get("5mins"))
				.last15Mins(loads.get("15mins") == null ? 0 : loads.get("15mins"));

	}

	private void addSiteTo(HostingNode node) {

		ContainerConfiguration cfg = context.configuration();

		node.profile().newSite().country(cfg.site().country()).location(cfg.site().location())
				.latitude(cfg.site().latitude()).longitude(cfg.site().longitude()).domain(domainIn(cfg.hostname()));
	}

	private void addVariablesTo(HostingNode node) {

		ContainerConfiguration cfg = context.configuration();

		Group<Variable> variables = node.profile().description().environmentVariables();
		
		// Cleaning variables to avoid duplicates
		variables.removeAll(node.profile().description().environmentVariables());
		
		Map<String, String> map = new HashMap<String, String>();
		map.putAll(cfg.properties());
		map.putAll(System.getenv());

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String varname = entry.getKey();
			if ((varname.compareToIgnoreCase("CLASSPATH") == 0) || (varname.compareToIgnoreCase("PATH") == 0)
					|| (varname.contains("SSH")) || (varname.contains("MAIL"))
					|| (varname.compareToIgnoreCase("LS_COLORS") == 0))
				continue;
			variables.add().keyAndValue(entry.getKey(), entry.getValue());
		}

		/* The following code is useless can be removed
		Map<String, String> envvars = new HashMap<String, String>();
		for (String varname : envvars.keySet()) {

			// a bit of filtering
			if ((varname.compareToIgnoreCase("CLASSPATH") == 0) || (varname.compareToIgnoreCase("PATH") == 0)
					|| (varname.contains("SSH")) || (varname.contains("MAIL"))
					|| (varname.compareToIgnoreCase("LS_COLORS") == 0))
				continue;

			variables.add().keyAndValue(varname, envvars.get(varname));
		}
		*/

		
		variables.add().keyAndValue("Java", System.getProperty("java.version"));
		
		SmartGearsConfiguration config = ProviderFactory.provider().smartgearsConfiguration();
		variables.add().keyAndValue("SmartGears",config.version());

		variables.add().keyAndValue("ghn-update-interval-in-secs", String.valueOf(cfg.publicationFrequency()));

	}

	public String uptime() {
		String lines = "", linetemp = null;
		try {
			Process p = Runtime.getRuntime().exec("uptime");
			p.waitFor();
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((linetemp = input.readLine()) != null)
				lines += linetemp;
			input.close();
			p.destroy();
			lines = lines.split(",")[0].split("up")[1].trim();
		} catch (Exception e) {
			log.warn("unable to detect the uptime of this machine", e);
			lines = "unable to detect";
		}
		return lines;
	}

	public Map<String, Double> loadStatistics() {

		Map<String, Double> result = new HashMap<String, Double>();
		try {
			File loadadv = new File("/proc/loadavg");
			if (loadadv.exists()) {
				Reader reader = new FileReader(loadadv);
				int c;
				StringBuilder content = new StringBuilder();
				while ((c = reader.read()) != -1)
					content.append((char) c);
				reader.close();
				Pattern p = Pattern.compile("^(.*?)\\s{1}(.*?)\\s{1}(.*?)\\s{1}(.*)$");
				Matcher matcher = p.matcher(content.toString());
				if ((matcher.matches()) && (matcher.groupCount() > 3)) {
					result.put("1min", new Double(matcher.group(1)));
					result.put("5mins", new Double(matcher.group(2)));
					result.put("15mins", new Double(matcher.group(3).split("\\s")[0]));
				}
			}
		} catch (Exception ioe) {
			log.warn("unable to detect the load values of this machine", ioe);
		}
		return result;
	}

	@SuppressWarnings("all")
	public Map<String, Long> memoryUsage() {
		Map<String, Long> map = new HashMap<String, Long>();
		java.lang.management.OperatingSystemMXBean mxbean = java.lang.management.ManagementFactory
				.getOperatingSystemMXBean();
		com.sun.management.OperatingSystemMXBean sunmxbean = (com.sun.management.OperatingSystemMXBean) mxbean;
		long freeMemory = sunmxbean.getFreePhysicalMemorySize() / 1048576; // in MB
		long availableMemory = sunmxbean.getTotalPhysicalMemorySize() / 1048576; // in MB
		map.put("MemoryAvailable", freeMemory);
		map.put("MemoryTotalSize", availableMemory);
		long ramVirtualAvailable = Runtime.getRuntime().freeMemory() / 1048576; // in MB
		long ramVirtualSize = Runtime.getRuntime().totalMemory() / 1048576; // in MB
		map.put("VirtualAvailable", ramVirtualAvailable);
		map.put("VirtualSize", ramVirtualSize);
		return map;
	}
	
	private String domainIn(String hostname) {        
		Pattern pattern = Pattern.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");
	    java.util.regex.Matcher regexMatcher = pattern.matcher(hostname);
	    if (regexMatcher.matches()) //it's an IP address, nothing to trim
	    	return hostname;
		String[] tokens = hostname.split("\\.");
		if (tokens.length < 2) 
			return hostname;
		else 			
			return tokens[tokens.length-2]+ "." + tokens[tokens.length-1];			    		
	}
}
