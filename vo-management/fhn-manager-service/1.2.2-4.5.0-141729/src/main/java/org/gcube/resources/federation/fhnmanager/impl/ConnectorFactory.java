package org.gcube.resources.federation.fhnmanager.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.resources.federation.fhnmanager.api.type.OccopusInfrastructureTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;

import org.gcube.vomanagement.occi.FHNConnector;
import org.gcube.vomanagement.occi.OcciConnector;
import org.gcube.vomanagement.occi.exceptions.UnsupportedCredentialsTypeException;
import org.gcube.vomanagement.occi.utils.ScriptUtil;
import org.gcube.vomanagement.occi.utils.X509CredentialManager;

public class ConnectorFactory {

	private Map<String, FHNConnector> connectors = new HashMap<>();

	public FHNConnector getConnector(VMProvider vmp) {

		String vmpId = vmp.getId();

		if (!connectors.containsKey(vmpId)) {
			connectors.put(vmpId, this.createConnector(vmp));
		}
		return connectors.get(vmpId);
	}

	public String adaptIS(String a) {
		String ab = a.replace(" ", "é");
		// System.out.println(ab);
		return ab;
	}

	public String adaptCert(String b) {
		String bc = b.replace("é", "\n");
		// System.out.println(bc);
		return bc;
	}

	// case 1: use userkey.pem and usercert.pem
	// not applicable in d4science.org cause error "Exception: problem parsing
	// ENCRYPTED PRIVATE KEY: java.lang.SecurityException: JCE cannot
	// authenticate the provider BC"
	// public String removeHeader(String c) {
	// String cd = c.replace("-----BEGIN" + "\n" + "CERTIFICATE-----",
	// "-----BEGIN CERTIFICATE-----");
	// String ce = cd.replace("-----END" + "\n" + "CERTIFICATE-----", "-----END
	// CERTIFICATE-----");
	// String cf = ce.replace("-----BEGIN" + "\n" + "RSA" + "\n" + "PRIVATE" +
	// "\n" + "KEY-----",
	// "-----BEGIN RSA PRIVATE KEY-----");
	// String cg = cf.replace("-----END" + "\n" + "RSA" + "\n" + "PRIVATE" +
	// "\n" + "KEY-----",
	// "-----END RSA PRIVATE KEY-----");
	// return cg;
	// }

	// case 2: usercred.p12
	// applicable in d4science.org
	public String removeHeader(String c) {
		String cd = c.replace("-----BEGIN" + "\n" + "CERTIFICATE-----", "-----BEGIN CERTIFICATE-----");
		String ce = cd.replace("-----END" + "\n" + "CERTIFICATE-----", "-----END CERTIFICATE-----");
		String cf = ce.replace("-----BEGIN" + "\n" + "PRIVATE" + "\n" + "KEY-----", "-----BEGIN PRIVATE KEY-----");
		String cg = cf.replace("-----END" + "\n" + "PRIVATE" + "\n" + "KEY-----", "-----END PRIVATE KEY-----");
		return cg;
	}

	public String generateSecondLevelProxy(String path, String vo) {
		return X509CredentialManager.createProxy(path, "", vo); // 2nd
																// level
																// proxy
	}

	public FHNConnector createConnector(VMProvider vmp) {
		// works fine with proxy from file
		// System.out.println(vmp.getCredentials().getEncodedCredentails());
		// try {
		// vmp.getCredentials().setEncodedCredentails(ScriptUtil.getScriptFromFile(new
		// File(StringEncrypter.getEncrypter().decrypt(vmp.getCredentials().getEncodedCredentails()))));
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// to create long proxy if no more valid
		// 1. put own p12 certificate extracted from browser on interested vm
		// (prod, preprd, dev)) --> folder .globus --> naming: usercred.p12 --> grid pwd: Newuser1
		// 2. voms-proxy-init --rfc --valid 20000:00 (valid until jun 30 2017
		// - this must be on vm as /tmp/x509up_u1000 and put the content on the
		// service endpoint IS resource by the vre
		// manager - it is a clean certificate without voms attribute - just
		// infn identity)
		// (to create locally and to store content manually in IS
		// (ServiceEndpoint - CLoud - FHN-VMProvider- Property - value of
		// encodedcrednetial)
		// 3. delete own certificate .p12 staying on vm and eventually other
		// keys
		// 4. delete /tmp/x509up_u1000
		// 5. save content of point2 to file /tmp/x509up_u1000 (querying IS -
		// done by code)
		// 6. create short lived cert using voms-proxy-init --cert
		// /tmp/x509up_u1000 -voms fedcloud.egi.eu
		// --rfc -noregen -dont-verify-ac (done by code)

		// IS version

		try {
			// keys go in /tomcat/lib
			String certPath = StringEncrypter.getEncrypter().decrypt(vmp.getCredentials().getEncodedCredentails());
			certPath = this.adaptIS(certPath);
			certPath = this.adaptCert(certPath);
			certPath = this.removeHeader(certPath);

			//Dec 15 2017 - fixed since before the content was put manually in /tmp/x509up_u1000
			//after put the content of usercred.p12 on remote vm
			//in this way the cert is populated directly from is and if the vm is restarted
			//is not necessary to deploy again the proxy
			File f = new File("/tmp/x509up_u1000");
			
			//fixed the below line
			//File f = new File("/tmp/x509up_u1000" + UUID.randomUUID());
			
			
			
			// to commit - branch - revNumber - build conf of 1.2.0 service with
			// new revNumber on etics in order to be deployed in preprod and
			// prod
			// File f = new File("/tmp/x509up_u1000");
			if (!f.exists()) {
				f.createNewFile();
				// Clear all permissions for all users
				f.setReadable(false, false);
				f.setWritable(false, false);
				f.setExecutable(false, false);

				f.setReadable(true, true); // Only the owner can read
				f.setWritable(true, true); // Only the owner can write

			}
			FileWriter fw = new FileWriter(f.getPath());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(certPath);
			bw.flush();
			bw.close();

			String c = this.generateSecondLevelProxy(f.getPath(), vmp.getCredentials().getVo());
			File d = new File(c);
			vmp.getCredentials().setEncodedCredentails(ScriptUtil.getScriptFromFile(d));
			
			d.delete();
			
			//f maybe to comment cause when create x509up_1000 proxy, others pem are created automatically
			f.delete();
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// endIS version

		FHNConnector connector = null;
		try {
			connector = new OcciConnector(vmp);
			connector.setTrustStore("/etc/grid-security/certificates");
		} catch (UnsupportedCredentialsTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connector;
	}
}