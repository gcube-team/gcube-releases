package org.gcube.vomanagement.vomsapi.util;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.security.auth.Subject;

import org.apache.log4j.Logger;
import org.gcube.common.core.security.utils.ProxyUtil;
import org.glite.security.util.FileCertReader;
import org.glite.security.voms.BasicVOMSTrustStore;
import org.glite.security.voms.FQAN;
import org.glite.security.voms.VOMSAttribute;
import org.glite.security.voms.VOMSValidator;
import org.globus.gsi.gssapi.GSSConstants;
import org.globus.wsrf.impl.security.authentication.Constants;
import org.gridforum.jgss.ExtendedGSSContext;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.ietf.jgss.GSSException;

/**
 * This class provides methods to access VOMS extensions contained in a VOMS
 * proxy certificate. It allows to retrieve VOMS attributes associated to the
 * certificate subject.
 * 
 * @author Alessandro Tomei
 */
public class VOMSAttributesReader {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(VOMSAttributesReader.class);

	/**
	 * the properties file
	 */
	private static final String PROPERTIES_FILE = "VOMSAttributeReader.properties";

	/**
	 * the default TrustStore List
	 */
	public static final String DEFAULT_TRUST_STORE_LISTING = "/etc/grid-security/vomsdir/*.pem";

	/**
	 * the default refresh Period
	 */
	public static final long REFRESH_PERIOD = 0;

	/**
	 * the TrustStore List
	 */
	private String defaultTrustStoreListing = DEFAULT_TRUST_STORE_LISTING;

	/**
	 * the refresh Period
	 */
	private long refreshPeriod = REFRESH_PERIOD;

	/**
	 * the validator
	 */
	private VOMSValidator validator;

	/**
	 * the set of attributes contained in the certificate
	 */
	private VOMSAttribute[] vomsAttributes = new VOMSAttribute[0];

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 *             if the properties file cannot be read
	 */
	protected VOMSAttributesReader() throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader()
				.getResourceAsStream(PROPERTIES_FILE);
		if (is != null) {
			Properties prop = new Properties();
			prop.load(is);
			is.close();

			Set keySet = prop.keySet();
			if (keySet.contains("DEFAULT_TRUST_STORE_LISTING"))
				this.defaultTrustStoreListing = prop
						.getProperty("DEFAULT_TRUST_STORE_LISTING");
			if (keySet.contains("REFRESH_PERIOD"))
				this.refreshPeriod = Long.parseLong(prop
						.getProperty("REFRESH_PERIOD"));
		}
	}

	/**
	 * Instantiates a VOMSProxyCertificate object
	 * 
	 * @param certificateChain
	 *            array containing the certificate chain. The certificate chain
	 *            is assumed to already be validated. It is also assumed to be
	 *            sorted in TLS order, that is certificate issued by trust
	 *            anchor first and client certificate last.
	 * 
	 * @throws Exception
	 *             if attributes cannot be retrieved from the certificate chain
	 * 
	 * @throws IOException
	 *             if the properties file cannot be read
	 */
	public VOMSAttributesReader(X509Certificate[] certificateChain)
			throws Exception, IOException {
		this();
		extractVOMSAttributes(certificateChain, defaultTrustStoreListing,
				refreshPeriod);
	}

	/**
	 * Instantiates a VOMSProxyCertificate object
	 * 
	 * @param certificateChain
	 *            array containing the certificate chain. The certificate chain
	 *            is assumed to already be validated. It is also assumed to be
	 *            sorted in TLS order, that is certificate issued by trust
	 *            anchor first and client certificate last.
	 * @param trustStoreListing
	 *            directory listing containing trusted VOMS certificates.
	 * @param refresh
	 *            refresh period in milliseconds
	 * 
	 * @throws Exception
	 *             if attributes cannot be retrieved from the certificate chain
	 */
	public VOMSAttributesReader(X509Certificate[] certificateChain,
			String trustStoreListing, long refresh) throws Exception {
		extractVOMSAttributes(certificateChain, trustStoreListing, refresh);
	}

	/**
	 * Instantiates a VOMSProxyCertificate object
	 * 
	 * @param proxyPath
	 *            the proxy certificate file path
	 * 
	 * @throws Exception
	 *             if attributes cannot be retrieved from the certificate chain
	 * 
	 * @throws IOException
	 *             if the properties file cannot be read
	 */
	public VOMSAttributesReader(String proxyPath) throws Exception,
			IOException {
		this();
		init(proxyPath, defaultTrustStoreListing, refreshPeriod);
	}

	/**
	 * Instantiates a VOMSProxyCertificate object
	 * 
	 * @param proxyPath
	 *            the proxy certificate file path
	 * @param trustStoreListing
	 *            directory listing containing trusted VOMS certificates
	 * @param refresh
	 *            refresh period in milliseconds
	 * 
	 * @throws Exception
	 *             if attributes cannot be retrieved from the certificate chain
	 * 
	 */
	public VOMSAttributesReader(String proxyPath, String trustStoreListing,
			long refresh) throws Exception {
		init(proxyPath, trustStoreListing, refresh);
	}

	/**
	 * Instantiates a VOMSProxyCertificate object starting from the Suject of
	 * invocation and the message context.
	 * 
	 * @param peerSubject
	 *            the subject of service invocation
	 * @param context
	 *            the message context
	 * @throws Exception
	 *             if attributes cannot be retrieved from the certificate chain
	 * 
	 * @throws IOException
	 *             if the properties file cannot be read
	 */
	public VOMSAttributesReader(Subject peerSubject,
			javax.xml.rpc.handler.MessageContext context) throws Exception,
			IOException {
		this();
		init(peerSubject, context, defaultTrustStoreListing, refreshPeriod);
	}

	/**
	 * Instantiates a VOMSProxyCertificate object starting from the Suject of
	 * invocation and the message context.
	 * 
	 * @param peerSubject
	 *            the subject of service invocation
	 * @param context
	 *            the message context
	 * @param trustStoreListing
	 *            directory listing containing trusted VOMS certificates
	 * @param refresh
	 *            refresh period in milliseconds
	 * @throws Exception
	 *             if attributes cannot be retrieved from the certificate chain
	 * 
	 */
	public VOMSAttributesReader(Subject peerSubject,
			javax.xml.rpc.handler.MessageContext context,
			String trustStoreListing, long refresh) throws Exception {
		init(peerSubject, context, trustStoreListing, refresh);
	}

	/**
	 * Instantiates a VOMSProxyCertificate object starting from an
	 * ExtendedGSSCredentials
	 * 
	 * @param cred
	 *            the credentials to parse
	 * @throws GSSException
	 *             if credentials cannot be parsed
	 * @throws CertificateException
	 *             if credentials cannot be parsed
	 * @throws IOException
	 *             if credentials cannot be parsed
	 * 
	 * @throws Exception
	 *             if attributes cannot be retrieved from the certificate chain
	 */
	public VOMSAttributesReader(ExtendedGSSCredential cred)
			throws Exception, GSSException, CertificateException,
			IOException {
		this();
		init(cred, defaultTrustStoreListing, refreshPeriod);
	}

	/**
	 * Instantiates a VOMSProxyCertificate object starting from an
	 * ExtendedGSSCredentials
	 * 
	 * @param cred
	 *            the credentials to parse
	 * @param trustStoreListing
	 *            directory listing containing trusted VOMS certificates
	 * @param refresh
	 *            refresh period in milliseconds
	 * @throws GSSException
	 *             if credentials cannot be parsed
	 * @throws CertificateException
	 *             if credentials cannot be parsed
	 * @throws IOException
	 *             if credentials cannot be parsed
	 * @throws Exception
	 *             if attributes cannot be retrieved from the certificate chain
	 */
	public VOMSAttributesReader(ExtendedGSSCredential cred,
			String trustStoreListing, long refresh) throws Exception,
			GSSException, CertificateException, IOException {
		init(cred, trustStoreListing, refresh);
	}

	/**
	 * This method extract the set of VOMS attributes from a certificate chain.
	 * 
	 * @param certs
	 *            the certificate chain
	 * @param defaultTrustStoreListing
	 *            directory listing containing trusted VOMS certificates
	 * @param refreshPeriod
	 *            refresh period in milliseconds
	 * @throws Exception
	 *             if attributes cannot be retrieved from the certificate chain
	 */
	private void extractVOMSAttributes(X509Certificate[] certs,
			String defaultTrustStoreListing, long refreshPeriod)
			throws Exception {

		X509Certificate[] orderCerts = ProxyUtil.orderChain(certs);

		// Log certificates
		String certsStr = "Certificates to parse: \n";
		int i = 0;
		for (X509Certificate certificate : orderCerts) {
			certsStr = certsStr + "\nCertificate number " + i + ":\n"
					+ certificate + "\n\n";
			i++;
		}
		logger.debug(certsStr);

		try {
			// extract attributes
			BasicVOMSTrustStore store = new BasicVOMSTrustStore(
					defaultTrustStoreListing, refreshPeriod);
			VOMSValidator.setTrustStore(store);
			VOMSValidator validator = new VOMSValidator(orderCerts);
			validator = validator.validate();

			vomsAttributes = (VOMSAttribute[]) validator.getVOMSAttributes()
					.toArray(new VOMSAttribute[0]);

			// Log attributes
			String vomsACStr = "Attributes found: ";
			for (VOMSAttribute attribute : vomsAttributes) {
				vomsACStr = vomsACStr + attribute + "\n";
			}
			logger.debug(vomsACStr);
		} catch (Exception e) {
			String msg = "Problems in reading VOMS Attributes information from the certificate chain";
			logger.error(msg, e);
			throw new Exception(msg, e);
		}

	}

	/**
	 * Initializes the reader
	 * 
	 * @param certificatePath
	 *            the certificate path
	 * @param defaultTrustStoreListing
	 *            directory listing containing trusted VOMS certificates
	 * @param refreshPeriod
	 *            refresh period in milliseconds
	 * @param defaultTrustStoreListing
	 * @param refreshPeriod
	 * @throws Exception
	 *             if attributes cannot be retrieved from the certificate chain
	 */
	private void init(String certificatePath, String defaultTrustStoreListing,
			long refreshPeriod) throws Exception {
		try {
			FileCertReader certReader = new FileCertReader();
			Vector vector = certReader.readCerts(certificatePath);
			if (vector.size() > 0) {
				X509Certificate[] certs = new X509Certificate[vector.size()];

				for (int i = 0; i < vector.size(); i++)
					certs[i] = (X509Certificate) vector.get(i);

				extractVOMSAttributes(certs, defaultTrustStoreListing,
						refreshPeriod);
			}
		} catch (Exception e) {
			String msg = "Error in reading the certificate file";
			logger.error(msg, e);
			throw new Exception(msg, e);
		}
	}

	/**
	 * Initializes the reader
	 * 
	 * @param peerSubject
	 *            the subject of service invocation
	 * @param context
	 *            the message context
	 * @param defaultTrustStoreListing
	 *            directory listing containing trusted VOMS certificates
	 * @param refreshPeriod
	 *            refresh period in milliseconds
	 * @throws Exception
	 *             if attributes cannot be retrieved from the certificate chain
	 */
	private void init(Subject peerSubject,
			javax.xml.rpc.handler.MessageContext context,
			String defaultTrustStoreListing, long refreshPeriod)
			throws Exception {
		/*
		 * Spudoratamente copiato da
		 * http://viewcvs.globus.org/viewcvs.cgi/playground/java/gridshib/gt/ \
		 * interceptors/java/source/src/org/globus/wsrf/impl/security/authorization/ \
		 * ShibbolethPIP.java?rev=HEAD&content-type=text/vnd.viewcvs-markup
		 */

		X509Certificate[] certs = null;
		org.apache.axis.MessageContext axisMessageContext = (org.apache.axis.MessageContext) context;

		Object o;
		Set credset = peerSubject.getPublicCredentials();
		Iterator creds = credset.iterator();
		while (creds.hasNext()) {
			o = creds.next();
			if (o instanceof X509Certificate[]) {
				certs = (X509Certificate[]) o;
				break; // first credset is primary
			}
		}

		if (certs == null) {
			/*
			 * If call only used transport security, it needs to get
			 * certificates in a different way than msg or conv.
			 */
			ExtendedGSSContext gsscontext = (ExtendedGSSContext) axisMessageContext
					.getProperty(Constants.TRANSPORT_SECURITY_CONTEXT);
			if (gsscontext != null) {
				try {
					o = gsscontext.inquireByOid(GSSConstants.X509_CERT_CHAIN);
				} catch (GSSException e) {
					/*
					 * if not present, GlobusGSSContextImpl will currently
					 * return null, which will be dealt with later
					 */
					String msg = "Cannot get peerSubject credentials";
					logger.error(msg, e);
					throw new Exception(msg, e);
				}

				if (o instanceof X509Certificate[])
					certs = (X509Certificate[]) o;
			}
		}

		if (certs == null) {
			String msg = "Cannot get peerSubject credentials";
			logger.error(msg);
			throw new Exception(msg);
		}

		extractVOMSAttributes(certs, defaultTrustStoreListing, refreshPeriod);
	}

	/**
	 * Initializes the reader
	 * 
	 * @param cred
	 *            the credentials to parse
	 * @param defaultTrustStoreListing
	 *            directory listing containing trusted VOMS certificates
	 * @param refreshPeriod
	 *            refresh period in milliseconds
	 * @throws GSSException
	 *             if credentials cannot be parsed
	 * @throws CertificateException
	 *             if credentials cannot be parsed
	 * @throws Exception
	 *             if credentials cannot be parsed
	 */
	private void init(ExtendedGSSCredential cred,
			String defaultTrustStoreListing, long refreshPeriod)
			throws GSSException, CertificateException, Exception {
		byte[] array = cred.export(ExtendedGSSCredential.IMPEXP_OPAQUE);
		ByteArrayInputStream byteArray = new ByteArrayInputStream(array);

		// get first proxy in the chain
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		List<X509Certificate> certs = new ArrayList<X509Certificate>();

		X509Certificate c1 = (X509Certificate) cf
				.generateCertificate(byteArray);
		certs.add(c1);
		logger.debug("Added certificate to the chain for DN: "
				+ c1.getSubjectDN().getName());

		// discard private key
		int red = -1;
		String keyStr = "";
		while ((red = byteArray.read()) != -1) {
			keyStr = keyStr + (char) red;
			if (keyStr.endsWith("-----END RSA PRIVATE KEY-----"))
				break;
		}
		byteArray.read();

		// get remaining proxies
		try {
			while (true) {
				X509Certificate cert = (X509Certificate) cf
						.generateCertificate(byteArray);
				certs.add(cert);
				logger.debug("Added certificate to the chain for DN: "
						+ cert.getSubjectDN().getName());
			}
		} catch (Exception e) {
			// When I get this error the stream ran out of certificates
			logger.error("Certificate chain is composed by " + certs.size()
					+ " certificates\n");
		}

		// extract attributes
		extractVOMSAttributes(certs.toArray(new X509Certificate[certs.size()]),
				defaultTrustStoreListing, refreshPeriod);

	}

	/**
	 * Returns a list of all roles attributed to a VOMS group
	 * 
	 * @param group
	 *            the group name
	 * 
	 * @return array of role names
	 * 
	 * 
	 */
	public String[] getRoles(String group) {
		HashSet<String> roles = new HashSet<String>();
		for (int i = 0; i < vomsAttributes.length; i++) {
			List listFQAN = vomsAttributes[i].getFullyQualifiedAttributes();
			if (!(listFQAN.isEmpty())) {
				for (Iterator it = listFQAN.iterator(); it.hasNext();) {
					FQAN fqan = new FQAN((String) it.next());
					String fqanGroup = fqan.getGroup();
					if (fqanGroup.equals(group)) {
						if (!fqan.getRole().equals("NULL"))
							roles.add(fqan.getRole());
					}
				}
			}
		}
		return (String[]) roles.toArray(new String[0]);
	}

	/**
	 * This method returns the parent of a given group name. It returns a void
	 * string if the group is the "root" group.
	 * 
	 * @param group
	 *            the group name
	 * @return the parent group name of the specified group
	 */
	public String getParentGroup(String group) {
		return group.substring(0, group.lastIndexOf("/"));
	}

	/**
	 * This method returns the childs of a given group name. It returns a void
	 * string if the group has not childs
	 * 
	 * @param group
	 *            the parent group
	 * @return the names of childs of the specified group
	 */
	public String[] getChildGroups(String group) {
		String[] vo = null;
		Vector<String> selectedVo = new Vector<String>();
		String root = getRootGroup(group);
		vo = getGroups(root);

		// select only the group with one level more than "group"
		int j = 0;
		for (int i = 0; i < vo.length; i++) {
			if (vo[i].matches(group + "/[^/]+")) {
				selectedVo.add(vo[i]);
				j++;
			}
		}
		return selectedVo.toArray(new String[0]);
	}

	/**
	 * test if the user has the roles specified in the given group
	 * 
	 * 
	 * @param role
	 *            the role name
	 * @param group
	 *            the group name
	 * 
	 * @return whether the role is attributed to the group in this certificate
	 */
	public boolean hasRole(String role, String group) {
		boolean hasRole = false;
		for (int i = 0; i < vomsAttributes.length; i++) {
			List listFQAN = vomsAttributes[i].getFullyQualifiedAttributes();
			if (!(listFQAN.isEmpty())) {
				for (Iterator it = listFQAN.iterator(); it.hasNext();) {
					FQAN fqan = new FQAN((String) it.next());
					String fqanGroup = fqan.getGroup();
					String fqanRole = fqan.getRole();
					if (fqanGroup.equals(group))
						if (fqanRole.equals(role))
							hasRole = true;
				}
			}
		}
		return hasRole;
	}

	/**
	 * Returns a list of all groups attributed to the certificate subject
	 * 
	 * @return array of group names
	 */
	public String[] getGroups() {
		HashSet<String> groups = new HashSet<String>();
		for (int i = 0; i < vomsAttributes.length; i++) {
			List listFQAN = vomsAttributes[i].getFullyQualifiedAttributes();
			if (!(listFQAN.isEmpty())) {
				for (Iterator it = listFQAN.iterator(); it.hasNext();) {
					FQAN fqan = new FQAN((String) it.next());
					groups.add(fqan.getGroup());
				}
			}
		}
		return (String[]) groups.toArray(new String[0]);
	}

	/**
	 * Returns a list of all groups attributed to the certificate subject
	 * 
	 * 
	 * @param vo
	 *            the VO name
	 * @return array of group names
	 */
	public String[] getGroups(String vo) {
		HashSet<String> groups = new HashSet<String>();
		for (int i = 0; i < vomsAttributes.length; i++) {
			if (vomsAttributes[i].getVO().equals(vo)) {
				List listFQAN = vomsAttributes[i].getFullyQualifiedAttributes();
				if (!(listFQAN.isEmpty()))
					for (Iterator it = listFQAN.iterator(); it.hasNext();) {
						FQAN fqan = new FQAN((String) it.next());
						groups.add(fqan.getGroup());
					}
			}
		}
		return (String[]) groups.toArray(new String[0]);
	}

	/**
	 * Returns a list of all DL specified in the certificate subject, with the
	 * extended notation e.g.:/diligent/Arte/DL1
	 * 
	 * @return array of DL names
	 */
	public String[] getAbsoluteDLNames() {
		HashSet<String> dls = new HashSet<String>();
		for (int i = 0; i < vomsAttributes.length; i++) {
			List listFQAN = vomsAttributes[i].getFullyQualifiedAttributes();
			if (!(listFQAN.isEmpty())) {
				for (Iterator it = listFQAN.iterator(); it.hasNext();) {
					FQAN fqan = new FQAN((String) it.next());
					dls.add(fqan.getGroup());
				}
			}
		}
		return (String[]) dls.toArray(new String[0]);
	}

	/**
	 * Returns a list of all groups in the given VO attributed to the
	 * certificate subject with the extended notation e.g.:/diligent/Arte/DL1
	 * 
	 * @return array of group names
	 * @param vo
	 *            the VO name
	 */
	public String[] getGroupsNames(String vo) {
		HashSet<String> groups = new HashSet<String>();
		for (int i = 0; i < vomsAttributes.length; i++) {
			if (vomsAttributes[i].getVO().equals(vo)) {
				List listFQAN = vomsAttributes[i].getFullyQualifiedAttributes();
				if (!(listFQAN.isEmpty()))
					for (Iterator it = listFQAN.iterator(); it.hasNext();) {
						FQAN fqan = new FQAN((String) it.next());
						groups.add(fqan.getGroup());
					}
			}
		}
		return (String[]) groups.toArray(new String[0]);
	}

	/**
	 * Get the list of VO names in these credentials
	 * 
	 * @return the VO names
	 */
	public String[] getVONames() {
		HashSet<String> voNames = new HashSet<String>();
		for (int i = 0; i < vomsAttributes.length; i++)
			voNames.add(vomsAttributes[i].getVO());
		return (String[]) voNames.toArray(new String[0]);
	}

	/**
	 * Get the list of root groups in these credentials
	 * 
	 * @return the root group names in full format
	 */
	public String[] getRootGroups() {
		HashSet<String> voNames = new HashSet<String>();
		for (int i = 0; i < vomsAttributes.length; i++)
			voNames.add("/" + vomsAttributes[i].getVO());
		return (String[]) voNames.toArray(new String[0]);
	}

	/**
	 * Given a group it returns the root of this group
	 * 
	 * @param group
	 *            the group name
	 * @return the root group name in full format
	 */
	private String getRootGroup(String group) {
		if (group.lastIndexOf("/") != 0) {
			String modGroup = group.substring(1);
			;
			return modGroup.substring(0, modGroup.indexOf("/"));
		}

		else
			return group.substring(1);
	}

	/**
	 * Get the full name of the DL where these credentials are valid
	 * 
	 * @return the full name of the DL where these credentials are valid
	 */
	public String getAbsoluteDLName() {
		String[] dls = getAbsoluteDLNames();
		String runningVO = "";
		int i = 0;
		while (i < dls.length) {
			if (this.getRoles(dls[i]).length != 0) {
				runningVO = dls[i];
				break;
			}
			i++;
		}
		return runningVO;
	}

	/**
	 * Get the set of local names of DLs where these credentials are valid
	 * 
	 * @return the set of local names of DLs where these credentials are valid
	 */
	public String[] getLocalDLNames() {
		HashSet<String> dls = new HashSet<String>();
		for (int i = 0; i < vomsAttributes.length; i++) {
			List listFQAN = vomsAttributes[i].getFullyQualifiedAttributes();
			if (!(listFQAN.isEmpty())) {
				for (Iterator it = listFQAN.iterator(); it.hasNext();) {
					FQAN fqan = new FQAN((String) it.next());
					dls.add(getLocalGroupName(fqan.getGroup()));
				}
			}
		}
		return (String[]) dls.toArray(new String[0]);
	}

	/**
	 * Get the local name of the DL where these credentials are valid
	 * 
	 * @return the local name of the DL where these credentials are valid
	 */
	public String getLocalDLName() {
		String[] dls = getAbsoluteDLNames();
		String runningVO = "";
		int i = 0;
		while (i < dls.length) {
			if (this.getRoles(dls[i]).length != 0) {
				runningVO = dls[i];
				break;
			}
			i++;
		}
		return getLocalGroupName(runningVO);
	}

	/**
	 * Get the local group name of a full group name
	 * 
	 * @param group
	 *            the full group name
	 * @return the local group name
	 */
	private String getLocalGroupName(String group) {
		return group.split("/")[group.split("/").length - 1];
	}

	/**
	 * Get the default TrustStore List
	 * 
	 * @return the default TrustStore List
	 */
	public static String getDEFAULT_TRUST_STORE_LISTING() {
		return DEFAULT_TRUST_STORE_LISTING;
	}

	/**
	 * Get the properties file
	 * 
	 * @return the properties file
	 */
	public static String getPROPERTIES_FILE() {
		return PROPERTIES_FILE;
	}

	/**
	 * Get the default refresh Period
	 * 
	 * @return the default refresh Period
	 */
	public static long getREFRESH_PERIOD() {
		return REFRESH_PERIOD;
	}

	/**
	 * Get the default TrustStore List
	 * 
	 * @return the default TrustStore List
	 */
	public String getDefaultTrustStoreListing() {
		return defaultTrustStoreListing;
	}

	/**
	 * Set the default TrustStore List
	 * 
	 * @param defaultTrustStoreListing
	 *            the default TrustStore List
	 */
	public void setDefaultTrustStoreListing(String defaultTrustStoreListing) {
		this.defaultTrustStoreListing = defaultTrustStoreListing;
	}

	/**
	 * Get the refresh Period
	 * 
	 * @return the refresh Period
	 */
	public long getRefreshPeriod() {
		return refreshPeriod;
	}

	/**
	 * Set the refresh Period
	 * 
	 * @param refreshPeriod
	 *            the refresh Period
	 */
	public void setRefreshPeriod(long refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
	}

	/**
	 * Get the validator
	 * 
	 * @return the validator
	 */
	public VOMSValidator getValidator() {
		return validator;
	}

	/**
	 * Set the validator
	 * 
	 * @param validator
	 *            the validator
	 */
	public void setValidator(VOMSValidator validator) {
		this.validator = validator;
	}
}