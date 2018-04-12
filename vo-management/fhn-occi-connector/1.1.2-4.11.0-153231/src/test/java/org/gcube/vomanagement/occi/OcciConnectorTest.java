
package org.gcube.vomanagement.occi;

import java.awt.RenderingHints.Key;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.naming.CommunicationException;

import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;
import org.gcube.resources.federation.fhnmanager.api.type.VMProviderCredentials;
import org.gcube.vomanagement.occi.datamodel.cloud.Network;
import org.gcube.vomanagement.occi.datamodel.cloud.OSTemplate;
import org.gcube.vomanagement.occi.datamodel.cloud.Storage;
import org.gcube.vomanagement.occi.datamodel.cloud.VM;
import org.gcube.vomanagement.occi.exceptions.UnsupportedCredentialsTypeException;
import org.gcube.vomanagement.occi.utils.PrettyPrinter;
import org.gcube.vomanagement.occi.utils.ScriptUtil;
import org.gcube.vomanagement.occi.utils.X509CredentialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import cz.cesnet.cloud.occi.api.Authentication;
import cz.cesnet.cloud.occi.api.Client;
import cz.cesnet.cloud.occi.api.http.HTTPClient;
import cz.cesnet.cloud.occi.api.http.HTTPConnection;
import cz.cesnet.cloud.occi.api.http.auth.HTTPAuthentication;
import cz.cesnet.cloud.occi.api.http.auth.KeystoneAuthentication;
import cz.cesnet.cloud.occi.infrastructure.NetworkInterface;

public class OcciConnectorTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(OcciConnectorTest.class);

	private FHNConnector connector;

	private VMProvider generateProvider() {
		VMProvider provider = new VMProvider();

		// provider.setEndpoint("https://fc.scai.fraunhofer.de:8787"); // KO (No
		// location found)
		// provider.setEndpoint("http://cloud.recas.ba.infn.it:8787"); // KO
		// (404
		// Not Found)
		// provider.setEndpoint("https://okeanos-occi2.hellasgrid.gr:9000"); //
		// KO
		// (405 Method Not Allowed)
		// provider.setEndpoint("https://fsd-cloud.zam.kfa-juelich.de:8787"); //
		// KO
		// (no suitable tenant)
		// provider.setEndpoint("https://cloud.ifca.es:8787/"); // KO (502 bad
		// gateway)
		// provider.setEndpoint("https://prisma-cloud.ba.infn.it:8787"); // KO
		// (connection refused)
		// provider.setEndpoint("http://server4-eupt.unizar.es:8787/"); // KO
		// (400
		// Bad Request)
		// provider.setEndpoint("https://stack-server-01.ct.infn.it:8787/"); //
		// KO
		// (no suitable tenant found)
		// provider.setEndpoint("https://controller.ceta-ciemat.es:8787/"); //
		// KO
		// (405 Method Not Allowed)
		// provider.setEndpoint("http://aurora.ncg.ingrid.pt:8787/"); // KO (401
		// Not
		// Authorized)
		// provider.setEndpoint("https://sbgcloud.in2p3.fr:8787/"); // KO
		// (Connection refused)
		// provider.setEndpoint("https://occi.hpcc.sztaki.hu:3202/"); // KO
		// (missing
		// 'Www-Authenticate' header)

		// provider.setEndpoint("https://cloud.cesga.es:3202/"); // OK (no
		// smartexecutor there)
		// provider.setEndpoint("https://nebula-server-01.ct.infn.it:9000"); //
		// OK
		// (can't create VM)

		//provider.setEndpoint("https://carach5.ics.muni.cz:11443/"); // OK

		provider.setEndpoint("https://nova.ui.savba.sk:8787/occi1.1"); // OK
		//provider.setEndpoint("https://cloud.cesga.es:3202");
		//provider.setEndpoint("http://cloud.recas.ba.infn.it:8787/occi/");
		//provider.setEndpoint("https://fc-one.i3m.upv.es:11443"); // OK
		//provider.setEndpoint("https://occi.cloud.gwdg.de:3100");

		try {
			// String slp = this.generateSecondLevelProxy();
			// System.out.println(slp);
			String slp = "/tmp/x509up_u1000";
			VMProviderCredentials creds = new VMProviderCredentials();
			creds.setType("x509");
			creds.setEncodedCredentails(ScriptUtil.getScriptFromFile(new File(slp)));
			provider.setCredentials(creds);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return provider;
	}

	/*
	 * Return the path to the generated file
	 * 
	 * First-level proxy is generated with: voms-proxy-init --voms
	 * fedcloud.egi.eu --rfc --dont-verify-ac
	 */
	public String generateSecondLevelProxy() {
		return X509CredentialManager.createProxy("/tmp/x509up_u1000", "", "d4science.org"); // 2nd
		// level
		// proxy
	}

	private void initTester() throws CommunicationException, UnsupportedCredentialsTypeException {
		if (this.connector == null) {
			// create a provider
			VMProvider provider = this.generateProvider();
			// create a connector for the given provider
			this.connector = new OcciConnector(provider);
			connector.setTrustStore("/etc/grid-security/certificates");
			try {
				connector.connect();
			} catch (cz.cesnet.cloud.occi.api.exception.CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void listVMURIs() throws Exception {
		this.initTester();
		for (URI uri : connector.listVMURIs()) {
			System.out.println(uri);
		}
	}

	private void stopAllVMs() throws Exception {
		this.initTester();
		for (URI uri : connector.listVMURIs()) {
			connector.stopVM(uri);
		}
	}

	public void printVMInfo(String vmUri) throws URISyntaxException {
		System.out.println(this.connector.getVM(new URI(vmUri)));

	}

	private void destroyAllVMs() throws Exception {
		this.initTester();
		for (URI uri : connector.listVMURIs()) {
			connector.destroyVM(uri);
		}
	}

	private void listVMs() throws Exception {
		this.initTester();
		for (VM vm : connector.listVM()) {
			System.out.println(new PrettyPrinter().print(vm));
		}
	}

	private void listStorageURIs() throws Exception {
		this.initTester();
		for (URI uri : connector.listStorageURIs()) {
			System.out.println(uri);
		}
	}

	private void listStorages() throws Exception {
		this.initTester();
		for (Storage storage : connector.listStorages()) {
			System.out.println(new PrettyPrinter().print(storage));
		}
	}

	private void listNetworkURIs() throws Exception {
		this.initTester();
		for (URI uri : connector.listNetworkURIs()) {
			System.out.println(uri);
		}
	}

	private void listNetworks() throws Exception {
		this.initTester();
		for (Network network : connector.listNetworks()) {
			System.out.println(new PrettyPrinter().print(network));
		}
	}

	private void listOSTemplatesURIs() throws Exception {
		this.initTester();
		for (URI uri : connector.listOSTemplatesURIs()) {
			System.out.println(uri);
		}
	}

	private void listOSTemplates() throws Exception {
		this.initTester();
		for (OSTemplate template : connector.listOSTemplates()) {
			System.out.println(new PrettyPrinter().print(template));
		}
	}

	private void listResourceTemplatesURIs() throws Exception {
		this.initTester();
		for (URI uri : connector.listResourceTemplatesURIs()) {
			System.out.println(uri);
		}
	}

	private void listResourceTemplates() throws Exception {
		this.initTester();
		for (ResourceTemplate template : connector.listResourceTemplates()) {
			System.out.println(new PrettyPrinter().print(template));
		}
	}

	public void startVM(String id) throws CommunicationException, UnsupportedCredentialsTypeException,
			cz.cesnet.cloud.occi.api.exception.CommunicationException, URISyntaxException {
		this.initTester();
		this.connector.startVM(new URI(id));
	}

	public void destroyVM(String id) throws CommunicationException, UnsupportedCredentialsTypeException,
			cz.cesnet.cloud.occi.api.exception.CommunicationException, URISyntaxException {
		this.initTester();
		this.connector.destroyVM(new URI(id));
	}

	public void returnIP(URI uri) throws CommunicationException, UnsupportedCredentialsTypeException,
			cz.cesnet.cloud.occi.api.exception.CommunicationException, URISyntaxException, UnknownHostException {
		this.initTester();
		this.connector.associatePublicIp(uri);
	}

	private void createVM(String vmName, String osSubstring, String resourceSubstring, URL script) throws Exception {
		this.initTester();
		OSTemplate vmt = null;
		for (OSTemplate t : connector.listOSTemplates()) {
			if (t.getId().toLowerCase().equals(osSubstring.toLowerCase())) {
				vmt = t;
				break;
			}
		}

		ResourceTemplate rt = null;
		for (ResourceTemplate t : connector.listResourceTemplates()) {
			if (t.getId().toLowerCase().equals(resourceSubstring.toLowerCase())) {
				rt = t;
				break;
			}
		}

		if (vmt == null) {
			System.out.println("unable to find a OS template containing " + osSubstring);
		}
		if (rt == null) {
			System.out.println("unable to find a Resource template containing " + resourceSubstring);
		}

		if (vmt != null && rt != null) {
			connector.createVM(vmName, vmt, rt, script);
		}

	}

	/**
	 * Test main method.
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {

		try {
			OcciConnectorTest tester = new OcciConnectorTest();

			
			//tester.stopAllVMs();
		
			// tester.initTester();
			// tester.startVM("http://cloud.recas.ba.infn.it:8787/occi/compute/f78a9030-a476-42d3-92c7-36d909a8bc0e");
			// tester.printVMInfo("http://cloud.recas.ba.infn.it:8787/occi/compute/ac8efdf3-f390-47de-8df7-8066ea5bffaf");
			// tester.generateSecondLevelProxy();
			//
			// tester.listOSTemplates();
			// tester.listResourceTemplatesURIs();
			// tester.createVM("test", "", "", "");
			//tester.listVMs();
			tester.startVM("https://nova.ui.savba.sk:8787/occi1.1/compute/2951bb4b-0d2b-461f-a399-bad0999e0755");
			tester.listVMs();
			//tester.returnIP(new URI("https://fc-one.i3m.upv.es:11443/compute/26125"));
			//tester.startVM("https://fc-one.i3m.upv.es:11443/compute/26125");
			// tester.destroyVM("https://nova2.ui.savba.sk:8787/compute/052004d2-81fd-4b31-bb6c-56bc35d6f7c2");
			// tester.startVM("https://carach5.ics.muni.cz:11443/compute/76654");
			// tester.startVM("https://carach5.ics.muni.cz:11443/compute/76655");
			// tester.createOcciClientOpenStack();
			// tester.listOSTemplates();
			// tester.listResourceTemplates();
			// tester.listOSTemplates();
			// tester.listOSTemplatesURIs();
			// tester.listVMURIs();
			// tester.returnIP(new
			// URI("https://fc-one.i3m.upv.es:11443/compute/24359"));
			// tester.returnIP(new
			// URI("https://fc-one.i3m.upv.es:11443/compute/24429"));
			// tester.destroyAllVMs();
			// tester.listVMs();
			//tester.destroyAllVMs();
			// tester.listOSTemplates();
			// tester.listResourceTemplates();
			// tester.listOSTemplatesURIs();
			// tester.listResourceTemplatesURIs();
			// //tester.listVMURIs();
			// tester.stopAllVMs();
			// tester.listNetworks();
			// tester.listStorages();
			// tester.listNetworkURIs();
			//tester.destroyAllVMs();
			//tester.listVMs();
			// create on iisas
//			tester.createVM("test"+UUID.randomUUID(),
//			 "http://schemas.openstack.org/template/os#b63fac5f-edf3-453b-8821-c3d38209fab3",
//			 "http://schemas.openstack.org/template/resource#5",
//			 new
//			 URL("http://data.d4science.org/ZXF5b3ZwcUZaRzlHV05FbExrRDJjejdSN3oyYnZKUVBHbWJQNStIS0N6Yz0"));

			//tester.listVMs();
			tester.listVMs();
			//tester.listResourceTemplates();
			//tester.destroyAllVMs();
			//create on upv
			
//			tester.createVM("test"+UUID.randomUUID(),
//			 "http://occi.fc-one.i3m.upv.es/occi/infrastructure/os_tpl#uuid_image_for_dataminer_ubuntu1204virtualbox_im398_208",
//			 "http://fedcloud.egi.eu/occi/compute/flavour/1.0#large",
//			 new
//			 URL("http://data.d4science.org/V3F1eHdnSzJyaHpIdjJVR2gxMkRpYmdLTE1JbjM4Q0JHbWJQNStIS0N6Yz0"));
////			//

			// create cesga
			 //tester.createVM("test",
			 //"http://occi.cloud.cesga.es/occi/infrastructure/os_tpl#uuid_dataminer_520",
			 //"http://schema.fedcloud.egi.eu/occi/infrastructure/resource_tpl#large",
			 //new
			// URL("http://data.d4science.org/V3F1eHdnSzJyaHpIdjJVR2gxMkRpYmdLTE1JbjM4Q0JHbWJQNStIS0N6Yz0"));
////			//tester.listResourceTemplates();
			//tester.destroyAllVMs();
			//tester.listVMs();
			//tester.destroyAllVMs();
			//tester.listResourceTemplates();
			//tester.destroyVM("http://cloud.recas.ba.infn.it:8787/occi/compute/672c0292-2bd8-49ad-941a-e1d7086cebb7");
			//tester.destroyVM("672c0292-2bd8-49ad-941a-e1d7086cebb7");

			// tester.returnIP(new
			// URI("https://fc-one.i3m.upv.es:11443/compute/24445"));
			//tester.destroyAllVMs();
			//tester.listVMs();
			//tester.listOSTemplates();
			//tester.listVMs();
			//tester.listResourceTemplates();
			//
			//

			//tester.destroyAllVMs();
			//tester.destroyVM("http://cloud.recas.ba.infn.it:8787/occi/compute/aee9adba-cc2b-4abf-9427-cf8282051268");
			//tester.destroyVM("http://cloud.recas.ba.infn.it:8787/occi/compute/51fd0e67-1717-4482-a4a9-5fcb76cc5e0c");			
			//tester.destroyVM("http://cloud.recas.ba.infn.it:8787/occi/compute/421a07ac-fe27-47d6-b56d-416fa4c0d60e");

			//tester.listOSTemplates();
		
			//tester.startVM("e32e8209-5373-4dbc-b98d-4400878ec7fa");
			//tester.listVMs();
			//tester.startVM("https://cloud.cesga.es:3202/compute/65248");
			//tester.listVMs();
			
			
			//tester.listResourceTemplates();
			//tester.listResourceTemplates();
			//tester.destroyVM("http://cloud.recas.ba.infn.it:8787/occi/compute/276c7802-510b-4b81-97fb-6cd6ec186ea5");
			//tester.destroyVM("923aaab7-29f9-4390-9c48-22a077a78cea");

			//tester.startVM("ce760dd2-f79b-47bc-8bd1-a106d0ca03a6");
			//tester.destroyVM("ff4e34ed-023b-4568-94d8-f2e01d5c7f62");
			//tester.destroyVM("1648cfa7-0bca-4c2f-8b51-d2398eef528d");
			// tester.startVM("e5e6b310-a0ad-46f1-b450-9c00e02ed025");
			// creATE BARI
			
			//tester.destroyVM("b98ad8c3-320e-4a91-8383-42c8cdcf824e");
			//tester.listVMs();
//			tester.createVM("test",
//			 "http://schemas.openstack.org/template/os#51a2b55d-1939-43f4-b303-048bb62f4bcc",
//			 "http://schemas.openstack.org/template/resource#9", new
//			URL("http://data.d4science.org/ZXF5b3ZwcUZaRzlHV05FbExrRDJjejdSN3oyYnZKUVBHbWJQNStIS0N6Yz0"));
			//tester.destroyAllVMs();
			//tester.listVMs();
			//tester.startVM("ce875b2e-9eca-496c-b7b4-61d70f58b3a5");
			// tester.listResourceTemplates();
			// tester.listOSTemplates();
			// tester.listResourceTemplatesURIs();
			//tester.destroyVM("http://cloud.recas.ba.infn.it:8787/occi/compute/1a3d9bab-a30b-4908-80a7-713584fc0fc3");
			//tester.listVMs();
			//tester.startVM("http://cloud.recas.ba.infn.it:8787/occi/compute/1aa59076-834f-4dd1-a893-442ba666f236");
			//tester.startVM("1aa59076-834f-4dd1-a893-442ba666f236");
			//tester.destroyVM("1aa59076-834f-4dd1-a893-442ba666f236");
			// tester.listNetworks();
			// tester.stopAllVMs();
			// tester.listStorages();
			//tester.destroyAllVMs();
			//tester.listVMs();
			//tester.destroyVM("http://cloud.recas.ba.infn.it:8787/occi/compute/cdcb78b0-1f60-4642-9ac6-39725feeb3b8");
			// tester.listNetworks();
			// tester.listOSTemplates();

			// tester.returnIP(new
			// URI("https://nova2.ui.savba.sk:8787/compute/289a4202-c410-401a-bd2c-3ccc7173d8a1"));

			System.out.println("**********************");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
