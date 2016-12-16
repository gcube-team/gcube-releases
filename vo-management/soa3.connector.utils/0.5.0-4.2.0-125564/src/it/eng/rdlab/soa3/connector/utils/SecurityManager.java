package it.eng.rdlab.soa3.connector.utils;

import it.eng.rdlab.soa3.connector.utils.security.InheritableTLSSLSocketFactory;

import java.io.File;
import java.io.FileReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Manages the security features of the connection with the Policy Manager: the class accepts the paths to certificate, private key and truststore directory
 * it also accepts the extension of the trust files.
 * 
 * Default values are:
 * 
 * 	certificate /etc/grid-security/hostcert.pem
 *	private key /etc/grid-security/hostkey.pem
 *	trust store directory /etc/grid-security/certificates
 * 	trust files extension .0
 * 
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class SecurityManager 
{
	private Logger log;
	
	public static final String KEYSTORE_PWD = "changeit";
	
	private static SecurityManager instance;
	public static final String DEFAULT_CERT_FILE = "/etc/grid-security/hostcert.pem";
	public static final String DEFAULT_KEY_FILE = "/etc/grid-security/hostkey.pem";
	public static final String DEFAULT_TRUST_DIR = "/etc/grid-security/certificates/";
	public static final String DEFAULT_TRUST_FILE_EXTENSION = ".0";

	private KeyStore keyStore;

	private String 	certFile,
					keyFile,
					trustDir,
					trustExt;
	
	private InternalPasswordFinder internalPasswordFinder;
	
	
	private boolean invalidateTrustDirectory;
	
	private List<String> trustedCertificates;
	
	private class InternalPasswordFinder implements PasswordFinder 
	{
		
		
		char [] privateKeyPassword = null;
		
	     @Override
	        public char[] getPassword() {
	            return privateKeyPassword;
	      }
	}
	
	private SecurityManager ()
	{
		this.log = LoggerFactory.getLogger(this.getClass());
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		certFile = DEFAULT_CERT_FILE;
		keyFile = DEFAULT_KEY_FILE;
		trustDir = DEFAULT_TRUST_DIR;
		trustExt = DEFAULT_TRUST_FILE_EXTENSION;
		this.internalPasswordFinder = new InternalPasswordFinder();
		this.invalidateTrustDirectory = false;
		this.trustedCertificates = new ArrayList<String>();
		this.keyStore = null;
	}
	
	
	/**
	 * 
	 * @return a singleton instance
	 */
	public static SecurityManager getInstance ()
	{
		if (instance == null) instance = new SecurityManager();
		
		return instance;
	}
	

	/**
	 * 
	 * Sets the certificate
	 * 
	 * @param certFile the complete path to the certificate PEM file
	 */
	public void setCertFile(String certFile) {
		
		if (certFile != null)  this.certFile = certFile;
	}

	public void setPrivateKeyPassword (char [] password)
	{
		this.internalPasswordFinder.privateKeyPassword = password;
	}

	/**
	 * 
	 * Sets the key file
	 * 
	 * @param keyFile the complete path to the key PEM file
	 */
	public void setKeyFile(String keyFile) {
		
		if (keyFile != null)  this.keyFile = keyFile;
	}


	/**
	 * 
	 * Sets the trust directory
	 * 
	 * @param trustDir the path to the trust dir
	 */
	public void setTrustDir(String trustDir) 
	{
		if (trustDir != null)
		{
			if (!trustDir.endsWith("/")) trustDir = trustDir+"/";
			
			this.trustDir = trustDir;
		}
	}


	/**
	 * 
	 * The trust extension
	 * 
	 * @param trustExt the trust files extension
	 */
	public void setTrustExt(String trustExt) 
	{
		if (trustExt != null) this.trustExt = trustExt;

	}

	/**
	 * 
	 * @return
	 */
	public String getCertFile() {
		return certFile;
	}


	/**
	 * 
	 * @return
	 */
	public String getKeyFile() {
		return keyFile;
	}


	/**
	 * 
	 * @return
	 */
	public String getTrustDir() {
		return trustDir;
	}


	/**
	 * 
	 * If true is passed, the trust dir won't be taken into account
	 * 
	 * @param invalidate
	 */
	public void invalidateTrustedDir (boolean invalidate)
	{
		this.invalidateTrustDirectory = invalidate;
	}

	/**
	 * 
	 * Adds a single trusted certificate
	 * 
	 * @param file the complete path to the trusted certificate PEM file
	 */
	public void addTrustedCert (String file)
	{
		this.trustedCertificates.add(file);
	}
	
	
	private KeyStore generateKeyStore () throws Exception
	{
	    // Get the certificate      
		log.debug("Getting keystore");
		log.debug("Cert file "+certFile);
		FileReader reader = new FileReader(certFile);
		PEMReader pem = new PEMReader (reader);
	    X509Certificate cert = (X509Certificate)pem.readObject();
	    pem.close();
	    reader.close();
	    log.debug("Cert file loaded");
		// Get the key
	    log.debug("Key file "+keyFile);
		reader = new FileReader(keyFile);
		pem = new PEMReader(reader,internalPasswordFinder);
	    PrivateKey key = ((KeyPair)pem.readObject()).getPrivate();
	    pem.close();
	    reader.close();
	    log.debug("Key file loaded");
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(null);
		keystore.setKeyEntry("certs", key,KEYSTORE_PWD.toCharArray(), new java.security.cert.Certificate[]{cert});
		
		List<String> trustedCertFiles = getTrustedCerts();
		this.log.debug ("trusted files");
		
		for (int i=0;i<trustedCertFiles.size();i++)
		{
			this.log.debug (trustedCertFiles.get(i));
		    reader = new FileReader(trustedCertFiles.get(i));
		    pem = new PEMReader(reader);
		    X509Certificate trustCert = (X509Certificate)pem.readObject();
		    pem.close();
		    reader.close();
		    keystore.setCertificateEntry("trust"+i, trustCert);
		}

		return keystore;
	}
	
	private List<String> getTrustedCerts ()
	{
		List<String> response = new ArrayList<String>(this.trustedCertificates);
		
		if (!this.invalidateTrustDirectory)
		{
			this.log.debug("Reading the trust directory");
			File dir = new File(this.trustDir);
			String [] fileNames = dir.list();
			this.log.debug("Files in the trust directory");
			
			for (String fileName : fileNames)
			{
				this.log.debug(fileName);
				if (fileName.endsWith(trustExt)) response.add(this.trustDir+fileName);
			}
		}
		
		return response;
		
	}
	
	/**
	 * 
	 * Loads the configured certificates
	 * 
	 * 
	 * @throws Exception
	 */
	
	public void loadCertificate () throws Exception
	{
		loadCertificate(true);
	}
	
	/**
	 * 
	 * @param threadLocal
	 * @throws Exception
	 */
	public void loadCertificate (boolean threadLocal) throws Exception
	{
	
		log.debug("Loading certificates...");
//		KeyStore keystore = KeyStore.getInstance("pkcs12");
//		keystore.load(new FileInputStream("/home/ciro/Varie/host.p12"),"changeit".toCharArray());
		log.debug("Loading keystore...");
		this.keyStore = generateKeyStore();
		log.debug("Keystore loaded");
		KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyFactory.init(this.keyStore,KEYSTORE_PWD.toCharArray());
		log.debug("Initializing trust manager");
		TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		trustFactory.init(this.keyStore);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		//sslContext.init(keyFactory.getKeyManagers(),new TrustManager [] {new NullTrustManager(keystore)}, new SecureRandom());
		sslContext.init(keyFactory.getKeyManagers(),trustFactory.getTrustManagers(), new SecureRandom());
		
		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
		
		if (threadLocal)
		{
			log.debug("Generating inheritable thread local ssl factory");
			InheritableTLSSLSocketFactory.getInstance().setSSLSocketFactory(sslSocketFactory);
			sslSocketFactory = InheritableTLSSLSocketFactory.getInstance();
		}
		else log.debug("Generated not thread local ssl factory");
		
		
		HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
		log.debug("Operation completed");
		
	}
	
	/**
	 * 
	 * Removes the current credentials from the SSL Socket factory restoring what were set in the ancestor threads
	 * 
	 */
	public void removeCertificate ()
	{
		InheritableTLSSLSocketFactory.getInstance().reset();
	}

	/**
	 * 
	 * @return
	 */
	public KeyStore getKeyStore ()
	{
		return this.keyStore;
	}
	
//	public static void main(String[] args) throws Exception {
////		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
////		SecurityManager.getInstance().loadCertificate();
//		
//		System.out.println("Thread "+Thread.currentThread().getId()+" "+InheritableTLSSLSocketFactory.getInstance().getCurrentSSLSocketFactory());
//		
//		new Thread (){
//			public void run() {
//				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//				try {
//					SecurityManager.getInstance().loadCertificate();
//					System.out.println("Thread "+Thread.currentThread().getId()+" "+InheritableTLSSLSocketFactory.getInstance().getCurrentSSLSocketFactory());
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			};
//		}.start();
//		Thread.sleep(1000);
//		
//		System.out.println("Thread "+Thread.currentThread().getId()+" "+InheritableTLSSLSocketFactory.getInstance().getCurrentSSLSocketFactory());
//
//		
//	}
	
	public static void main(String[] args) throws Exception{
		System.out.println(new Date());
		SecurityManager menager = new SecurityManager();
		menager.setTrustDir("/home/ciro/certs");
		menager.loadCertificate();
		
	}
}
