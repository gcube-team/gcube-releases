
package org.gcube.vomanagement.occi;

import java.awt.RenderingHints.Key;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    // provider.setEndpoint("http://cloud.recas.ba.infn.it:8787"); // KO (404
    // Not Found)
    // provider.setEndpoint("https://okeanos-occi2.hellasgrid.gr:9000"); // KO
    // (405 Method Not Allowed)
    // provider.setEndpoint("https://fsd-cloud.zam.kfa-juelich.de:8787"); // KO
    // (no suitable tenant)
    // provider.setEndpoint("https://cloud.ifca.es:8787/"); // KO (502 bad
    // gateway)
    // provider.setEndpoint("https://prisma-cloud.ba.infn.it:8787"); // KO
    // (connection refused)
    // provider.setEndpoint("http://server4-eupt.unizar.es:8787/"); // KO (400
    // Bad Request)
    // provider.setEndpoint("https://stack-server-01.ct.infn.it:8787/"); // KO
    // (no suitable tenant found)
    // provider.setEndpoint("https://controller.ceta-ciemat.es:8787/"); // KO
    // (405 Method Not Allowed)
    // provider.setEndpoint("http://aurora.ncg.ingrid.pt:8787/"); // KO (401 Not
    // Authorized)
    // provider.setEndpoint("https://sbgcloud.in2p3.fr:8787/"); // KO
    // (Connection refused)
    // provider.setEndpoint("https://occi.hpcc.sztaki.hu:3202/"); // KO (missing
    // 'Www-Authenticate' header)

    // provider.setEndpoint("https://cloud.cesga.es:3202/"); // OK (no
    // smartexecutor there)
    // provider.setEndpoint("https://nebula-server-01.ct.infn.it:9000"); // OK
    // (can't create VM)
    
   // provider.setEndpoint("https://carach5.ics.muni.cz:11443/"); // OK
    provider.setEndpoint("https://nova2.ui.savba.sk:8787"); // OK
    //provider.setEndpoint("https://cloud.cesga.es:3202");
    //provider.setEndpoint("http://cloud.recas.ba.infn.it:8787/occi/");
    // provider.setEndpoint("https://fc-one.i3m.upv.es:11443"); // OK

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
   * First-level proxy is generated with: voms-proxy-init --voms fedcloud.egi.eu
   * --rfc --dont-verify-ac
   */
  public String generateSecondLevelProxy() {
    return X509CredentialManager.createProxy("/tmp/x509up_u1000", "","d4science.org"); // 2nd
                                                                       // level
                                                                       // proxy
  }

  private void initTester() throws CommunicationException,
      UnsupportedCredentialsTypeException {
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
  
  public void printVMInfo(String vmUri) throws URISyntaxException{
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
    

  public void startVM (String id) throws CommunicationException, UnsupportedCredentialsTypeException, cz.cesnet.cloud.occi.api.exception.CommunicationException, URISyntaxException{
	  this.initTester();
	  this.connector.startVM(new URI(id));
  }
  
  
  public void destroyVM (String id) throws CommunicationException, UnsupportedCredentialsTypeException, cz.cesnet.cloud.occi.api.exception.CommunicationException, URISyntaxException{
	  this.initTester();
	  this.connector.destroyVM(new URI(id));
  }
  
  
  public void returnIP(URI uri) throws CommunicationException, UnsupportedCredentialsTypeException, cz.cesnet.cloud.occi.api.exception.CommunicationException, URISyntaxException, UnknownHostException{
	  this.initTester();
	  this.connector.associatePublicIp(uri);
  }
  
  
  private void createVM(String vmName, String osSubstring,
      String resourceSubstring, URL script) throws Exception {
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
      System.out.println("unable to find a OS template containing "
          + osSubstring);
    }
    if (rt == null) {
      System.out.println("unable to find a Resource template containing "
          + resourceSubstring);
    }

    if (vmt != null && rt != null) {
      connector.createVM(vmName, vmt, rt, script);
    }

  }
  
  
  
 
  
/**
   * Test main method.
   * @param args not used
   */
  public static void main(String[] args) {
	 
    try {
      OcciConnectorTest tester = new OcciConnectorTest();
      
      //tester.initTester();
      //tester.startVM("http://cloud.recas.ba.infn.it:8787/occi/compute/f78a9030-a476-42d3-92c7-36d909a8bc0e");
      //tester.printVMInfo("http://cloud.recas.ba.infn.it:8787/occi/compute/ac8efdf3-f390-47de-8df7-8066ea5bffaf");
      //tester.generateSecondLevelProxy();
      //
      //tester.listOSTemplates();
      //tester.listResourceTemplatesURIs();
      //tester.createVM("test", "", "", "");
      //tester.listVMs();
      //tester.destroyVM("https://nova2.ui.savba.sk:8787/compute/052004d2-81fd-4b31-bb6c-56bc35d6f7c2");
      //tester.startVM("https://carach5.ics.muni.cz:11443/compute/76654");
      //tester.startVM("https://carach5.ics.muni.cz:11443/compute/76655");
      //tester.createOcciClientOpenStack();
      //tester.listOSTemplates();
      //tester.listResourceTemplates();
      //tester.listOSTemplates();
      //tester.listOSTemplatesURIs();
      //tester.listVMURIs();
      tester.destroyAllVMs();
     // tester.listOSTemplates();
      //tester.listResourceTemplates();
      //tester.listOSTemplatesURIs();
      //tester.listResourceTemplatesURIs();
//      //tester.listVMURIs();
      //tester.stopAllVMs();
     // tester.listNetworks();
      //tester.listStorages();
      //tester.listNetworkURIs();
//   tester.createVM("test"+UUID.randomUUID(), 
//   		  "http://schemas.openstack.org/template/os#498dd867-2e0a-49f3-a1a7-7f5f4a0b8660",
//		  "http://schemas.openstack.org/template/resource#m1-large",
//		  new URL("http://data.d4science.org/VkRkODJoeGxldHZDZWZucS9UQkJmZStScHZYSFdQVWZHbWJQNStIS0N6Yz0"));
//      //tester.listResourceTemplates();
      // tester.listOSTemplates();
      //tester.listResourceTemplatesURIs();
      //tester.listVMs();
      //tester.listNetworks();
      //tester.stopAllVMs();
     //tester.listStorages();
      //tester.destroyAllVMs();
      //tester.listVMs();
      //tester.listNetworks();
      // tester.listOSTemplates();
  
      
      //tester.returnIP(new URI("https://nova2.ui.savba.sk:8787/compute/289a4202-c410-401a-bd2c-3ccc7173d8a1"));
      
      System.out.println("**********************");
    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }

}
