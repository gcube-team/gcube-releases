package org.gcube.common.core.security.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;

import org.apache.log4j.Logger;
import org.globus.gsi.CertUtil;
import org.globus.myproxy.MyProxy;
import org.globus.myproxy.MyProxyException;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

/**
 * @author Andrea Turli
 * 
 */
public class ProxyUtil {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(ProxyUtil.class);

	/**
	 * Constructor
	 * */
	public ProxyUtil() {
	}

	/**
	 * Utility method to parse proxy credentials. This class provides methods to
	 * access VOMS extensions contained in a VOMS proxy certificate. It permits
	 * to retrieve the roles associated to the certificate subject.
	 * 
	 * @param credentials
	 *            the byte array containing proxy credentials to load
	 * 
	 * @throws GSSException
	 *             If an exception occurs parsing credentials
	 * 
	 * @return an ExtendedGSSCredential object containing parsed credentials
	 * 
	 */
	public static ExtendedGSSCredential loadProxyCredentials(byte[] credentials)
			throws GSSException {

		if (credentials == null)
			throw new NullPointerException(
					"credentials to parse cannot be null");

		ExtendedGSSManager manager = (ExtendedGSSManager) ExtendedGSSManager
				.getInstance();
		ExtendedGSSCredential cred = (ExtendedGSSCredential) manager
				.createCredential(credentials,
						ExtendedGSSCredential.IMPEXP_OPAQUE,
						GSSCredential.DEFAULT_LIFETIME, null, // use default
						// mechanism -
						// GSI
						GSSCredential.INITIATE_AND_ACCEPT);
		return cred;
	}

	/**
	 * Utility method to load proxy credentials from a file.
	 * 
	 * @param fileName
	 *            the absolute or relative path of file containing proxy
	 *            credentials to load
	 * 
	 * @throws IOException
	 *             if an exeption occurs loading credentials from file
	 * 
	 * @throws GSSException
	 *             If an exception occurs parsing credentials
	 * 
	 * @return an ExtendedGSSCredential object containing parsed credentials
	 */
	public static ExtendedGSSCredential loadProxyCredentials(String fileName)
			throws IOException, GSSException {

		if (fileName == null)
			throw new NullPointerException("credentials file cannot be null");

		// credentials
		File f = new File(fileName);
		byte[] data = new byte[(int) f.length()];
		FileInputStream in = new FileInputStream(f);
		// read in the credential data
		in.read(data);
		in.close();

		return loadProxyCredentials(data);
	}

	/**
	 * Utility method to store proxy credentials to a file.
	 * 
	 * @param fileName
	 *            the absolute or relative path of file where to store proxy
	 *            credentials
	 * @param credentials
	 *            Credentials to be stored on file
	 * @throws IOException
	 *             if an exeption occurs storing credentials to file
	 * 
	 * @throws GSSException
	 *             If an exception occurs extracting credentials
	 */
	public static void storeProxyCredentials(String fileName,
			ExtendedGSSCredential credentials) throws GSSException, IOException {

		if (fileName == null)
			throw new NullPointerException("Credentials file cannot be null");
		if (credentials == null)
			throw new NullPointerException(
					"Credentials to be stored cannot be null");

		byte[] data = credentials.export(ExtendedGSSCredential.IMPEXP_OPAQUE);

		FileOutputStream out = new FileOutputStream(fileName);
		out.write(data);
		out.close();
	}

	/**
	 * 
	 * This method allows to retrieve credentials from a myproxy repository.
	 * 
	 * @param myProxyHost
	 *            host of the myproxy repository
	 * @param myProxyPort
	 *            port of the myproxy repository
	 * @param username
	 *            username of the account to use
	 * @param password
	 *            password of the account to use
	 * @param hours
	 *            lifetime of delegated credentials in hours
	 * @return the credentials
	 * @throws MyProxyException
	 *             if credentials cannot be retrieved
	 */
	public static ExtendedGSSCredential getCredentialsFromMyproxy(
			String myProxyHost, int myProxyPort, String username,
			String password, int hours) throws MyProxyException {

		// Check parameters
		if (myProxyHost == null)
			throw new NullPointerException("The MyProxy host cannot be null");
		if (username == null)
			throw new NullPointerException("The username cannot be null");

		logger.debug("Using MyProxy on host " + myProxyHost + " and port "
				+ myProxyPort + " to get credentials for account " + username
				+ " (lifetime " + hours + " hours)");

		// Get credentials from MyProxy
		MyProxy myproxy = new MyProxy(myProxyHost, myProxyPort);

		ExtendedGSSCredential delegatedCredentials = null;
		try {
			delegatedCredentials = (ExtendedGSSCredential) myproxy.get(
					username, password, hours * 3600);
		} catch (MyProxyException e) {
			logger.error(
					"Cannot retrieve credentials from MyProxy at address (host: '"
							+ myProxyHost + "', port: '" + myProxyPort
							+ "') to get delegated credentials for " + username
							+ " with lifetime of " + hours + " hours. ", e);
			throw e;
		}

		// Check if credentials are not null
		if (delegatedCredentials == null)
			throw new MyProxyException(
					"Null credentials retrieved from MyProxy at address (host: '"
							+ myProxyHost + "', port: '" + myProxyPort
							+ "') to get delegated credentials for " + username
							+ " with lifetime of " + hours + "hours. ");

		return delegatedCredentials;
	}
	
	/**
	 * This method return the DN of credentials passed as parameter in the OSG format.
	 * 
	 * @throws Exception if the DN cannot be retrieved
	 * 
	 * @return the DN in the OSG format
	 * */
	public static String getDN(ExtendedGSSCredential credentials) throws Exception {
		
		//TODO_ROCCIA : clean this method
		
		if (credentials == null) throw new NullPointerException("Credentials cannot be null");
		
		X509Certificate[] orderCerts = null;
		try {
			byte[] array = credentials.export(ExtendedGSSCredential.IMPEXP_OPAQUE);
			ByteArrayInputStream byteArray = new ByteArrayInputStream(array);
			
			//get first proxy in the chain
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			List<X509Certificate> certs = new ArrayList<X509Certificate>();
			
			X509Certificate c1 = (X509Certificate) cf.generateCertificate(byteArray);
			certs.add(c1);
			logger.debug("Added certificate to the chain for DN: " + c1.getSubjectDN().getName());
			
			//discard private key
			int red = -1;
			String keyStr = "";
			while ((red = byteArray.read()) != -1) {
				keyStr = keyStr + (char) red;
				if (keyStr.endsWith("-----END RSA PRIVATE KEY-----")) break;
			}
			byteArray.read();
			
			//get remaining proxies
			try {
				while(true) {
					X509Certificate cert = (X509Certificate) cf.generateCertificate(byteArray);
					certs.add(cert);
					logger.debug("Added certificate to the chain for DN: " + cert.getSubjectDN().getName());
				}
			} catch (Exception e) {
				//When I get this error the stream ran out of certificates
				logger.error("Certificate chain is composed by " + certs.size() + " certificates\n");
			}
			
			orderCerts = orderChain(certs.toArray(new X509Certificate[certs.size()]));			
		} catch (Exception e) {
			logger.error("Cannot retrieve the DN from credentials", e);
			throw new Exception("Cannot retrieve the DN from credentials", e);
		}

		
		if (orderCerts != null && orderCerts.length > 0) {
			
//			DN dn = DNHandler.getSubject(orderCerts[0]);
//
//			String x500dn = dn.getX500();
			
			X500Principal principal =  orderCerts[0].getIssuerX500Principal();
			String x500dn = getDNOnlineRepresentation(principal.getName());
			
			return x500dn;
		} else throw new Exception("The certificate chain is empty, cannot retrieve the DN");
		
	}
	
	/**
	 * This method return the CA of credentials passed as parameter in the OSG format.
	 * 
	 * @throws Exception if the CA cannot be retrieved
	 * 
	 * @return the CA in the OSG format
	 * */
	public static String getCA(ExtendedGSSCredential credentials) throws Exception {
		
		//TODO_ROCCIA : clean this method
		
		if (credentials == null) throw new NullPointerException("Credentials cannot be null");
		
		
		X509Certificate[] orderCerts = null;
		try {
			byte[] array = credentials.export(ExtendedGSSCredential.IMPEXP_OPAQUE);
			ByteArrayInputStream byteArray = new ByteArrayInputStream(array);
			
			//get first proxy in the chain
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			List<X509Certificate> certs = new ArrayList<X509Certificate>();
			
			X509Certificate c1 = (X509Certificate) cf.generateCertificate(byteArray);
			certs.add(c1);
			logger.debug("Added certificate to the chain for DN: " + c1.getSubjectDN().getName());
			
			//discard private key
			int red = -1;
			String keyStr = "";
			while ((red = byteArray.read()) != -1) {
				keyStr = keyStr + (char) red;
				if (keyStr.endsWith("-----END RSA PRIVATE KEY-----")) break;
			}
			byteArray.read();
			
			//get remaining proxies
			try {
				while(true) {
					X509Certificate cert = (X509Certificate) cf.generateCertificate(byteArray);
					certs.add(cert);
					logger.debug("Added certificate to the chain for DN: " + cert.getSubjectDN().getName());
				}
			} catch (Exception e) {
				//When I get this error the stream ran out of certificates
				logger.error("Certificate chain is composed by " + certs.size() + " certificates\n");
			}
			
			orderCerts = orderChain(certs.toArray(new X509Certificate[certs.size()]));			
		} catch (Exception e) {
			logger.error("Cannot retrieve the CA from credentials", e);
			throw new Exception("Cannot retrieve the CA from credentials", e);
		}

		
		if (orderCerts != null && orderCerts.length > 0) {
			
			//DN ca = DNHandler.getIssuer(orderCerts[0]);

			X500Principal principal =  orderCerts[0].getIssuerX500Principal();
			
			//String x500ca = ca.getX500();
			String x500ca = getDNOnlineRepresentation(principal.getName());
			System.out.println("OLD NAME "+x500ca);
			logger.info("the root CA for this certificate is " + x500ca);
			
			return x500ca;
		} else throw new Exception("The certificate chain is empty, cannot retrieve the CA");
		
	}
	
	/**
	 *  This methods returns the certificate index belonging to the array having as issuer a certificate
	 * 	not present in the array
	 * 
	 * @param certs the certificate chain to search
	 */
	public static int getEndUserCertificateindex(X509Certificate[] certs) {
		int index = -1;

		for (int i = 0; i < certs.length; i++) {
			boolean hasIssuer = false;
			for (int k = 0; k < certs.length; k++) {
				if (i != k)
					if ((certs[i].getIssuerX500Principal().equals(certs[k].getSubjectX500Principal())))
						hasIssuer = true;
			}
			if (!hasIssuer)
				index = i;
		}

		return index;
	}
	
	/**
	 *  This methods remove the CA certificate from the certificate chain passed.
	 *  
	 *  @param certs the certificate chain to search
	 *  @return the certificate chain without the CA certificate
	 * */
	public static X509Certificate[] removeCACertificateFromArray(
			X509Certificate[] certs) {
		X509Certificate[] resultArray = certs;
		int numCA = 0;
		for (int i = 0; i < certs.length; i++) {
			if (certs[i].getIssuerX500Principal().equals(
					certs[i].getSubjectX500Principal()))
				numCA++;
		}

		if (numCA > 0) {
			resultArray = new X509Certificate[certs.length - numCA];
			int idx = 0;
			for (int k = 0; k < certs.length; k++) {
				if (!(certs[k].getIssuerX500Principal().equals(certs[k]
						.getSubjectX500Principal()))) {
					resultArray[idx] = certs[k];
					idx++;
				}
			}
		}

		return resultArray;
	}
	
	/** Starting from "endUserCertificate" (the certificate retreived with getRootCertificateindex) 
	 * this method builds an array containing all descendants certificates.
	 * This array is ordered in this way:
	 * 	certs[0] = certificato utente (o rootCertificate)
	 * 	certs[1] = certificato utente/proxy/proxy/proxy
	 * 	certs[1] = certificato utente/proxy/proxy
	 * 	certs[1] = certificato utente/proxy
	 * 
	 *  @param certs the certificate chain to order
	 *  @return the ordered certificate chain
	 */
	public static X509Certificate[] orderChain(X509Certificate[] certs)
			throws Exception {

		if (certs.length == 0) return certs;

		certs = removeCACertificateFromArray(certs);
		
		X509Certificate[] orderedArray = new X509Certificate[certs.length];
		int index = getEndUserCertificateindex(certs);
	
		if (index == -1) {
			String msg = "Cannot find rootCertificate in file";
			logger.error(msg);
			throw new Exception(msg);
		}

		if (index > -1) {
			Vector<X509Certificate> tempVector = new Vector<X509Certificate>();
			tempVector.add(certs[index]);

			for (int i = 1; i < certs.length; i++)
				for (int k = 0; k < certs.length; k++) {
					X509Certificate tempCertificate = (X509Certificate) tempVector.get(i - 1);
					if (tempCertificate.getSubjectX500Principal().equals(
							certs[k].getIssuerX500Principal()))
						tempVector.add(certs[k]);
				}

			X509Certificate[] tempArray = (X509Certificate[]) tempVector.toArray(new X509Certificate[0]);
			orderedArray = new X509Certificate[tempArray.length];
			orderedArray[0] = tempArray[0];
			int idx = 1;
			for (int i = tempArray.length - 1; i >= 1; i--) {
				orderedArray[idx] = tempArray[i];
				idx++;
			}
		}
		return orderedArray;
	}
	
	/** 
	 * Order the certificate chain according to the GSI Requirements
	 * @throws Exception 
	 */
	public static ExtendedGSSCredential orderCredentials(ExtendedGSSCredential credentials) throws Exception {
	
		//TODO_ROCCIA : clean this method
		
		if (credentials == null) throw new NullPointerException("Credentials cannot be null");

		X509Certificate[] orderedCerts = null;
		ByteArrayOutputStream outupCredentialsStream = new ByteArrayOutputStream();
		try {
			byte[] originalCredentials = credentials.export(ExtendedGSSCredential.IMPEXP_OPAQUE);
			ByteArrayInputStream stream = new ByteArrayInputStream(originalCredentials);
			
			//copy first cert and private key
			int red = -1;
			String keyStr = "";
			while ((red = stream.read()) != -1) {
				outupCredentialsStream.write(red);
				keyStr = keyStr + (char) red;
				if (keyStr.endsWith("-----END RSA PRIVATE KEY-----\n")) break;
			}
						
			//get remaining proxies
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			List<X509Certificate> certs = new ArrayList<X509Certificate>();
			
			try {
				while(true) {
					X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
					certs.add(cert);
					logger.debug("Added certificate to the chain for DN: " + cert.getSubjectDN().getName());
				}
			} catch (Exception e) {
				//When I get this error the stream ran out of certificates (also counting the first one)
				logger.error("Certificate chain is composed by " + (certs.size() + 1) + " certificates\n");
			}
		
		//order chain
		X509Certificate[] tempCerts = orderChain(certs.toArray(new X509Certificate[certs.size()]));
		
		
		orderedCerts = new X509Certificate[tempCerts.length];
		if (orderedCerts.length > 0) {
			orderedCerts[orderedCerts.length - 1] = tempCerts[0];
			for (int i = 1; i < tempCerts.length; i++) {
				orderedCerts[i-1] = tempCerts[i];
			}			
		}


		String orderedDNStr = "";
		for (int i = 0; i < orderedCerts.length; i++) {
			orderedDNStr = orderedDNStr + orderedCerts[i].getSubjectDN().getName() + "\n";
		}
		
		logger.debug("The ordered certificate chain (without the last delegated certificate) is composed by:\n" + orderedDNStr);
		
		//write certs to output array
		for (int i = 0; i < orderedCerts.length; i++) {
			CertUtil.writeCertificate(outupCredentialsStream, orderedCerts[i]);
		}
		
		byte[] orderedCredentials = outupCredentialsStream.toByteArray();
				
		//Convert to credentials
		return loadProxyCredentials(orderedCredentials);	
				
		} catch (Exception e) {
			logger.error("Cannot order the certificate chain", e);
			throw new Exception("Cannot order the certificate chain", e);
		}
	}	
	
	
	public static String getDNOnlineRepresentation (String rfc2253Format)
	{
		String [] fields = rfc2253Format.split(",");
		StringBuilder response = new StringBuilder();
		
		for (int i = fields.length-1;i>=0;i--)
		{	
			response.append('/').append(fields[i].trim());
		}
		
		return response.toString();
	}

}
