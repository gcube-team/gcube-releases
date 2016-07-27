
package org.gcube.vomanagement.occi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

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


public class OcciConnectorTest {

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
    provider.setEndpoint("https://carach5.ics.muni.cz:11443"); // OK
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
  private String generateSecondLevelProxy() {
    return X509CredentialManager.createProxy("/tmp/x509up_u1000", ""); // 2nd
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
      connector.setTrustStore("/etc/grid-security/certificates/");
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
      
      //tester.listResourceTemplatesURIs();
      //tester.createVM("test", "", "", "");
      tester.destroyAllVMs();
      // tester.listOSTemplates();
      //tester.listResourceTemplates();
      // tester.listOSTemplatesURIs();
      // tester.listResourceTemplatesURIs();
      // tester.listVMURIs();
      tester.createVM("test-vm", 
    		  "http://occi.carach5.ics.muni.cz/occi/infrastructure/os_tpl#uuid_gcubesmartexecutor_fedcloud_warg_139",
    		  "http://fedcloud.egi.eu/occi/compute/flavour/1.0#mem_small",
    		  new URL("https://appdb.egi.eu/storage/cs/vapp/15819120-7ee4-4b85-818a-d9bd755a61f0/devsec-init"));
      tester.listResourceTemplates();
      // tester.listOSTemplates();
      //tester.listVMURIs();
      // tester.stopAllVMs();
     
      //tester.destroyAllVMs();
      //tester.listVMs();
      //tester.listNetworks();
      // tester.listOSTemplates();
  
      System.out.println("**********************");
    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }

}
