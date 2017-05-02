package org.gcube.resources.federation.fhnmanager.occopus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.print.attribute.HashAttributeSet;

import org.apache.commons.io.IOUtils;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.Software.Profile.ServicePackage;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.api.type.Node;
import org.gcube.resources.federation.fhnmanager.api.type.NodeTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;
import org.gcube.resources.federation.fhnmanager.impl.FHNManagerImpl;
import org.gcube.resources.federation.fhnmanager.is.ISProxyImpl;
import org.gcube.resources.federation.fhnmanager.is.ISProxyInterface;
import org.gcube.resources.federation.fhnmanager.is.ISProxyLocalYaml;
import org.gcube.resources.federation.fhnmanager.utils.Props;
import org.gcube.vomanagement.occi.OcciConnector;
import org.gcube.vomanagement.occi.datamodel.cloud.VMNetwork;
import org.gcube.vomanagement.occi.utils.X509CredentialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import ch.qos.logback.classic.net.SyslogAppender;

/**
 * Created by ggiammat on 9/6/16.
 */
public class OccopusNodeDefinitionImporter {

	public static String node_def;
	public static String type;
	public static String endpoint;
	public static String os_tpl;
	public static String resource_tpl;
	public static String script;

	static Props a = new Props();

	private static final String NODES_STORAGE_PATH = a.getPathOccopusNodes();
	private static final String NODES_STORAGE_FILE = "node_definitions_test.yaml";
	private static final String NODES_ENRICHED = "node_enriched.yaml";

	private static final Logger LOGGER = LoggerFactory.getLogger(OccopusNodeDefinitionImporter.class);

	// must be done with the updateis

	public void dumpNodes(String ag) throws IOException {

		File file = new File(NODES_STORAGE_PATH + File.separator + NODES_STORAGE_FILE);
		FileWriter writer = new FileWriter(file);
		writer.write(ag);
		LOGGER.debug("Nodes stored to " + file.getAbsolutePath());
		writer.close();

	}

	
	
	public void enrichNode(String ag) throws IOException {

		File file2 = new File(NODES_STORAGE_PATH + File.separator + NODES_ENRICHED);
		FileWriter writer2 = new FileWriter(file2);
		writer2.write(ag);
		LOGGER.debug("Enriching info stored to " + file2.getAbsolutePath());
		writer2.close();

	}

	
	
	
	
	
	private void init() {
		File path = new File(NODES_STORAGE_PATH);
		path.mkdirs();
	}

	public void importer() throws IOException {

		init();
		
		ISProxyInterface isProxy = new ISProxyImpl();
		FHNManagerImpl b = new FHNManagerImpl();

		String ag = "";
		String af = "";
		String azz = "";

		for (VMProvider vmp : isProxy.getAllVMProviders()) {
			try{
			Collection<ResourceTemplate> resourceTemplates = b.findResourceTemplate(vmp.getId());
			for (NodeTemplate nt : isProxy.getVMProviderNodeTemplates(vmp.getId())) {
				for (ResourceTemplate rt : resourceTemplates) {
					//azz = nodematch(rt, vmp, nt);
					ag = ag + "\n" + "\n" + formatNodeDefinition(rt, vmp, nt);
					af = af + "\n" + "\n" + formatContext(nt);
					azz = azz + nodematch(rt,vmp,nt);
				}
			}
		try {
			dumpNodes(ag);
			enrichNode(azz);
			//enrichnode(azz);
			loadNodeDefinition(NODES_STORAGE_PATH + File.separator + NODES_STORAGE_FILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
			catch (Exception aa){
				continue;
		}
	}
	}

	
	
	
	public Object loadNodes() {
		YamlReader reader = null;
		Map contact = null;

		try {
			reader = new YamlReader(new FileReader(NODES_STORAGE_PATH + File.separator + NODES_ENRICHED));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			try {
				contact = (Map) reader.read();
			} catch (YamlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (contact == null) break;

			System.out.println(contact);
		}
		
		return contact;
		


	}
	
	
	
	
		// 1. getAllServiceProfile
		// 2. for each serviceProfile
		// for (ServiceProfile c : isProxy.getAllServiceProfiles()) {
		//
		// // 3. getNodeTemplate, getVMProvider
		// for (NodeTemplate nt : isProxy.getAllNodeTemplates()) {
		// if (nt.getServiceProfile().getRefId().equals(c.getId())) {
		//
		// LOGGER.debug("Importing node template " + nt.getId() + " wiht service
		// profile " + c.getId());
		//
		// // 4. find resourceTemplate for provider
		// for (VMProvider vmp : isProxy.getAllVMProviders()) {
		// if
		// (vmp.getNodeTemplates().iterator().next().getRefId().equals(nt.getId()))
		// {
		// coll =
		// b.findResourceTemplate(b.findVMProviders(c.getId()).iterator().next().getId());
		// for (ResourceTemplate rtt : coll) {
		// ag = ag + "\n" + "\n" + formatNodeDefinition(rtt, vmp, nt);
		// af = af + "\n" + "\n" + formatContext(nt);
		//
		// }
		//
		// }
		// }
		//
		//
		// }
		// }
		// }

	

	public String formatContext(NodeTemplate nt) throws IOException {
		File file = new File(a.getPathOccopusNodes() + File.separator + nt.getId() + ".yaml");

		FileWriter writer = new FileWriter(file);

		String context = "#cloud-config" + "\n" + "system_info:" + "\n" + " default_user:" + "\n" + "  name: d4science"
				+ "\n" + "  sudo: ALL=(ALL) NOPASSWD:ALL" + "\n" + "runcmd:" + "\n" + " - curl -L " + nt.getScript()
				+ " | sudo /bin/bash -" + "\n";

		try {
			writer.write(context);
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.debug("Context stored to " + file.getAbsolutePath());

		return nt.getId() + ".yaml";

	}
	
	
	
	public String setPublicNet(VMProvider vmp){
		if (vmp.getEndpoint().equals("https://nova.ui.savba.sk:8787/occi1.1") || vmp.getEndpoint().equals("https://fc-one.i3m.upv.es:11443"))
			return "/network/public";
		else return "";
	}
	
	

	public String formatNodeDefinition(ResourceTemplate rtt, VMProvider vmp, NodeTemplate nt) throws IOException {
		String nodeDefinition = "'node_def:" + "occi_" + rtt.getName().trim().replace(":", "") + "_" + nt.getId() +"_" +vmp.getCredentials().getVo()+ "':" + "\n -\n"
				+ "  resource:" + "\n" + "   type: occi \n" + "   endpoint: " + vmp.getEndpoint() + "\n" + "   os_tpl: "
				+ nt.getOsTemplateId() + "\n" + "   resource_tpl: " + rtt.getId() + "\n" + "   link: "+ "\n" + "    -"+  "\n" +"     "+this.setPublicNet(vmp)+ "\n"+"  contextualisation:" + "\n"
				+ "   type: cloudinit" + "\n" + "   context_template: !text_import" + "\n" + "    url: file://"
				+ formatContext(nt) + "\n";
		System.out.println(nodeDefinition);
		return nodeDefinition;

	}
	
	public String nodematch(ResourceTemplate a, VMProvider b, NodeTemplate c){
		String nodeDefinition2 = 
				"---"+"\n"+
				"node_def: " + "occi_" + a.getName().trim().replace(":", "") + "_" + c.getId() +"_" +b.getCredentials().getVo()+ "\n"
				+ "vmproviderId: " + b.getId() + "\n"
				+ "nodetemplateId: " + c.getId() + "\n"
				+ "resourceTemplateId: " + a.getId() + "\n";
		System.out.println(nodeDefinition2);
		return nodeDefinition2;
	}
	
	
	
	
	

	public void loadNodeDefinition(String nodedef) {
		try {
			StringBuilder importoccopus = new StringBuilder();
			importoccopus.append("virtualenv occopus  ");
			importoccopus.append("&&");
			importoccopus.append("source " + a.getOccopusDIR() + "activate");
			importoccopus.append("&&");
			importoccopus.append("occopus-import " + nodedef);

			String[] command = { "/bin/bash", "-c", importoccopus.toString() };
			System.out.println(command);
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);
			Process proc = pb.start();
			System.out.println("Process started !");
			String line;
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			proc.destroy();
			System.out.println("Process ended !");

		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
