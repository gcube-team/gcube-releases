package org.gcube.resources.federation.fhnmanager.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.encryption.StringEncrypter;
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


	private String adaptIS(String a) {
		String ab = a.replace(" ", "é");
		// System.out.println(ab);
		return ab;
	}

	private String adaptCert(String b) {
		String bc = b.replace("é", "\n");
		// System.out.println(bc);
		return bc;
	}

	private String removeHeader(String c) {
		String cd = c.replace("-----BEGIN" + "\n" + "CERTIFICATE-----", "-----BEGIN CERTIFICATE-----");
		String ce = cd.replace("-----END" + "\n" + "CERTIFICATE-----", "-----END CERTIFICATE-----");
		String cf = ce.replace("-----BEGIN" + "\n" + "RSA" + "\n" + "PRIVATE" + "\n" + "KEY-----",
				"-----BEGIN RSA PRIVATE KEY-----");
		String cg = cf.replace("-----END" + "\n" + "RSA" + "\n" + "PRIVATE" + "\n" + "KEY-----",
				"-----END RSA PRIVATE KEY-----");
		return cg;
	}

	private String generateSecondLevelProxy(String path) {
		return X509CredentialManager.createProxy(path, ""); // 2nd
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

		// to create long proxy
		// 1. voms-proxy-init --rfc --valid 1500:00 (valid until septembere 2
		// 2016)
		// (to create locally and to store content manually in IS
		// (ServiceEndpoint - CLoud - FHN-VMProvider- Property - value of
		// encodedcrednetial)
		// 2. save content of point2 to file /tmp/x509up_u1000 (querying IS -
		// done by code)
		// 3. voms-proxy-init --cert /tmp/x509up_u1000 -voms fedcloud.egi.eu
		// --rfc -dont-verify-ac (done by code)

		// IS version

		try {
			//keys go in /tomcat/lib
			String certPath = StringEncrypter.getEncrypter().decrypt(vmp.getCredentials().getEncodedCredentails());
			certPath = this.adaptIS(certPath);
			certPath = this.adaptCert(certPath);
			certPath = this.removeHeader(certPath);

			File f = new File("/tmp/x509up_u1000");
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

			String c = this.generateSecondLevelProxy(f.getPath());
			File d = new File(c);
			vmp.getCredentials().setEncodedCredentails(ScriptUtil.getScriptFromFile(d));

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