package org.gcube.vomanagement.vomsapi.util;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.gcube.common.core.security.utils.constants.DelegationConstants;
import org.gcube.common.core.security.utils.constants.ProxyTypeConstants;
import org.glite.voms.contact.VOMSProxyBuilder;
import org.glite.voms.contact.VOMSProxyInit;
import org.glite.voms.contact.VOMSRequestOptions;
import org.glite.voms.contact.VOMSServerInfo;
import org.globus.gsi.GSIConstants;

/**
 * This class generates proxy certificates containing VOMS Attribute
 * Certificates. It wraps the voms-proxy-init command that must be installed
 * locally.
 * <p>
 * 
 * @author Paolo Roccetti, Ciro Formisano
 */
public class VOMSProxyFactory implements ProxyTypeConstants, DelegationConstants
{

    /**
     * the logger
     */
    private static Logger logger = Logger.getLogger(VOMSProxyFactory.class);

    /**
     * the command name
     */
    protected String commandName = "voms-proxy-init";

    /**
     * the certificate authorities directory
     */
    private File cadir;

    /**
     * the configuration file
     */
    private File confile;

    /**
     * the output file
     */
    private File output;

    /**
     * the proxy lifetime
     */
    private Integer hours;

    /**
     * the attribute lifetime
     */
    private Integer vomslife;

    /**
     * if the proxy is limited or not
     */
    private boolean limited;

    /**
     * if the commnad is verbose or quiet
     */
    private boolean quiet;

    /**
     * if the proxy must be verified or not
     */
    private boolean verify;

    /**
     * proxy type 
     */
    private String proxyType;
    
    /**
     * delegation type 
     */
    private String delegationType;
    
    /**
     * policy type 
     */
    private String policyType;

    /**
     * VOMS Server list
     */
    private List<VOMSServerInfo> vomsServerList  = new ArrayList<VOMSServerInfo>();
    
    /**
     * the set of VOMS roles to be added
     */
    private Set<VOMSFQANInfo> vomsInfo = new HashSet<VOMSFQANInfo>();

    
    /**
     * Constructor
     */
    public VOMSProxyFactory() {

    }

    /**
     * Constructor
     * 
     * @param cadir
     *                the certificate authorities directory
     * @param confile
     *                the configuration file
     * @param output
     *                the output file
     * @param hours
     *                the proxy lifetime
     * @param vomslife
     *                the attribute lifetime
     * @param limited
     *                if the proxy is limited or not
     * @param quiet
     *                if the commnad is verbose or quiet
     * @param verify
     *                if the proxy must be verified or not
     * @param vomsRoles
     *                the set of VOMS roles to be added
     */
    public VOMSProxyFactory(File cadir, File confile, File output,
	    Integer hours, Integer vomslife, boolean limited, boolean quiet,
	    boolean verify, Set<VOMSFQANInfo> vomsInfo) {
	super();
	this.cadir = cadir;
	this.confile = confile;
	this.output = output;
	this.hours = hours;
	this.vomslife = vomslife;
	this.limited = limited;
	this.quiet = quiet;
	this.verify = verify;
	this.vomsInfo = vomsInfo;
    }

    /**
     * Creates a new proxy certificate from an EEC and an encrypted private
     * key.
     * 
     * @param certificate
     *                the file containing the End Entity Certificate
     * 
     * @param key
     *                the file containing the private key
     * 
     * @param password
     *                the password to decrypt the private key
     * 
     * @throws IOException
     *                 If an exception occurs during creation
     * 
     * @throws InterruptedException
     *                 If an exception occurs during creation
     * 
     */
    public synchronized void createProxy(File certificate, File key,
	    String password) throws IOException, InterruptedException {

	// Check parameters
	if (certificate == null)
	    throw new IllegalArgumentException("The certificate file is null");
	if (!certificate.exists())
	    throw new IllegalArgumentException("The certificate file "
		    + certificate.getPath() + " does not exists");
	if (!certificate.isFile())
	    throw new IllegalArgumentException("The certificate file "
		    + certificate.getPath() + " is not a regular file");

	if (key != null) {
	    if (!key.exists()) {
		throw new IllegalArgumentException("The key file "
			+ key.getPath() + " does not exists");
	    }
	    if (!key.isFile()) {
		throw new IllegalArgumentException("The key file "
			+ key.getPath() + " is not a regular file");
	    }
	}

	// TOBETESTED
	if(key != null)
	    setupUserCredentials(certificate.getPath(), key.getPath());
	else
	    setupUserCredentials(certificate.getPath(), certificate.getPath());





	this.buildProxy(certificate, key, password);

	/*
	 * String chmod = "chmod 600 "; Runtime runtimeC = Runtime.getRuntime();
	 * Process processC = runtimeC.exec(chmod +
	 * certificate.getAbsolutePath());
	 *  // wait for the command process to exit int resultC =
	 * processC.waitFor();
	 *  // check if the command returned correctly if (resultC != 0) {
	 *  // write error message on logger BufferedReader reader = new
	 * BufferedReader(new InputStreamReader( processC.getErrorStream()));
	 * String errorStr = "chmod 600 error stream:\n"; String errorLine;
	 * while ((errorLine = reader.readLine()) != null) { errorStr = errorStr +
	 * errorLine + "\n"; }
	 * 
	 * logger.debug(errorStr);
	 *  // throw Exception throw new IOException("Cannot change
	 * permission"); }
	 * 
	 * 
	 * ProcessBuilder pb = new ProcessBuilder();
	 *  // Create Command List<String> commandList =
	 * this.createPBCommand(certificate, key ); pb.command(commandList);
	 * 
	 * Map<String, String> env = pb.environment();
	 * env.put("X509_USER_CERT", certificate.getPath()); if(key != null)
	 * env.put("X509_USER_KEY", key.getPath() ); else
	 * //env.put("X509_USER_KEY", "");
	 * env.put("X509_USER_KEY",certificate.getPath());
	 * 
	 * Process process = pb.start(); Writer writer = new
	 * OutputStreamWriter(new BufferedOutputStream(process
	 * .getOutputStream())); if (password != null) { // Provide password if
	 * required writer.write(password + "\n"); } else { // In any case
	 * provide a return character to avoid deadlocks writer.write("\n"); }
	 * writer.flush();
	 *  // wait for the command process to exit int result =
	 * process.waitFor();
	 * 
	 * logger.debug("voms-proxy-init exit code: " + result);
	 *  // TODO_ROCCIA : add a check for the result of the process as soon
	 * as // the voms 1.7.21 will be available
	 *  // write error message on logger BufferedReader errorReader = new
	 * BufferedReader(new InputStreamReader( process.getErrorStream()));
	 * String errorStream = "voms-proxy-init error stream:\n"; String
	 * errorLine; while ((errorLine = errorReader.readLine()) != null) {
	 * errorStream = errorStream + errorLine + "\n"; }
	 * 
	 * logger.debug(errorStream);
	 *  // write output message on logger BufferedReader outputReader = new
	 * BufferedReader(new InputStreamReader( process.getInputStream()));
	 * String outputStream = "voms-proxy-init output stream:\n"; String
	 * outputLine; while ((outputLine = outputReader.readLine()) != null) {
	 * outputStream = outputStream + outputLine + "\n"; }
	 * 
	 * logger.debug(outputStream);
	 */
    }

//    /**
//     * Generates the voms command to be executed
//     * 
//     * @param certificate
//     *                the file containing the End Entity Certificate
//     * 
//     * @param key
//     *                the file containing the private key
//     * 
//     * @return the voms command to be executed
//     */
//    private synchronized List<String> createPBCommand(File certificate, File key) {
//
//	List<String> commandList = new ArrayList<String>();
//	commandList.add(commandName);
//	commandList.add("-pwstdin");
//
//	// set certificate
//	commandList.add("-cert");
//	commandList.add(certificate.getPath());
//
//	// test if the original certificate is already a proxy
//	if (key != null) {
//	    commandList.add("-key");
//	    commandList.add(key.getPath());
//	}
//
//	// set other options
//	if (this.output != null) {
//	    commandList.add("-out");
//	    commandList.add(this.output.getPath());
//	}
//
//	if (this.cadir != null) {
//	    commandList.add("-cadir");
//	    commandList.add(this.cadir.getPath());
//	}
//
//	if (this.confile != null) {
//	    commandList.add("-confile");
//	    commandList.add(this.confile.getPath());
//	}
//
//	if (this.hours != null) {
//	    commandList.add("-hours");
//	    commandList.add(this.hours.toString());
//	}
//
//	if (this.vomslife != null) {
//	    commandList.add("-vomslife");
//	    commandList.add(this.vomslife.toString() + ":0");
//	}
//
//	if (this.limited) {
//	    commandList.add("-limited");
//	}
//
//	if (this.quiet) {
//	    commandList.add("-quiet");
//	}
//
//	if (this.verify) {
//	    commandList.add("-verify");
//	}
//
//	if (!this.vomsInfo.isEmpty()) {
//	    commandList.add("-voms");
//	}
//	for (VOMSFQANInfo vomsInfo : this.vomsInfo) {
//	    commandList.add(vomsInfo.getString());
//	}
//
//	String result= "";
//	for (Iterator iter = commandList.iterator(); iter.hasNext();) {
//	    result = result + iter.next() + " ";
//	}
//
//	logger.debug("voms command line to execute: " + result);
//	return commandList;
//    }	

//    /**
//     * Generates the voms command to be executed
//     * 
//     * @param certificate
//     *                the file containing the End Entity Certificate
//     * 
//     * @param key
//     *                the file containing the private key
//     * 
//     * @return the voms command to be executed
//     */
//    private synchronized String createCommand(File certificate, File key) {
//	String command = commandName + " -pwstdin";
//
//	// set certificate
//	command = command + " -cert " + certificate.getAbsolutePath();
//
//	// test if the original certificate is already a proxy
//	if (key != null) {
//	    command = command + " -key " + key.getAbsolutePath();
//	}
//
//	// set other options
//	if (this.output != null) {
//	    command = command + " -out " + this.output;
//	}
//
//	if (this.cadir != null) {
//	    command = command + " -cadir " + this.cadir;
//	}
//
//	if (this.confile != null) {
//	    command = command + " -confile " + this.confile;
//	}
//
//	if (this.hours != null) {
//	    command = command + " -hours " + this.hours.intValue();
//	}
//
//	if (this.vomslife != null) {
//	    command = command + " -vomslife " + this.vomslife.intValue() + ":0";
//	}
//
//	if (this.limited) {
//	    command = command + " -limited";
//	}
//
//	if (this.quiet) {
//	    command = command + " -quiet";
//	}
//
//	if (this.verify) {
//	    command = command + " -verify";
//	}
//
//	for (VOMSFQANInfo vomsInfo : this.vomsInfo) {
//	    command = command + " -voms " + vomsInfo.getString();
//	}
//
//	logger.debug("voms command line to execute: " + command);
//
//	return command;
//    }

    /**
     * Creates a new proxy certificate from another proxy certificate
     * 
     * @param proxyFile
     *                the file containing the Proxy Certificate
     * 
     * @throws IOException
     *                 If an exception occurs during creation
     * 
     * @throws InterruptedException
     *                 If an exception occurs during creation
     */
    public void createProxy(File proxyFile) throws IOException,
    InterruptedException {
	this.createProxy(proxyFile, proxyFile, null);
    }

    /**
     * Creates a new proxy certificate from an EEC and an unencrypted private
     * key.
     * 
     * @param certificate
     *                the file containing the End Entity Certificate
     * @param key
     *                the file containing the unencrypted private key
     * 
     * @throws IOException
     *                 If an exception occurs during creation
     * 
     * @throws InterruptedException
     *                 If an exception occurs during creation
     */
    public void createProxy(File certificate, File key) throws IOException,
    InterruptedException {
	this.createProxy(certificate, key, null);
    }

    /**
     * Gets the CA certificates directory
     * 
     * @return the CA certificates directory
     */
    public synchronized File getCAdir() {
	return this.cadir;
    }

    /**
     * Sets the CA certificate directory
     * 
     * @param cadir the CA certificate directory
     */
    public synchronized void setCertdir(File cadir) {
	if (!cadir.exists() || !cadir.isDirectory())
	    throw new IllegalArgumentException("The certificate directory "
		    + cadir.getPath() + " is not a valid directory");
	this.cadir = cadir;
    }

    /**
     * Gets the configuration file to use
     * 
     * @return the configuration file used
     */
    public synchronized File getConfile() {
	return confile;
    }

    /**
     * Sets the configuration file to use
     * 
     * @param confile
     *                the configuration file to use
     */
    public synchronized void setConfile(File confile) {
	if (!confile.exists() || !confile.isFile())
	    throw new IllegalArgumentException("The configuration file "
		    + confile.getPath() + " is not a valid configuration file");
	this.confile = confile;
    }

    /**
     * Gets the number of hours of validity of the proxy certificate to
     * create
     * 
     * @return the number of hours of validity of the proxy certificate to
     *         create
     */
    public synchronized Integer getHours() {
	return hours;
    }

    /**
     * Sets the number of hours of validity of the proxy certificate to
     * create
     * 
     * @param hours
     *                the number of hours of validity of the proxy
     *                certificate to create
     */
    public synchronized void setHours(Integer hours) {
	this.hours = hours;
    }

    /**
     * Gets the limitation of proxy to create
     * 
     * @return the limitation of proxy to create
     */
    public synchronized boolean isLimited() {
	return limited;
    }

    /**
     * Sets the limitation of proxy to create
     * 
     * @param limited
     *                true if the proxy should be limited, false otherwise
     */
    public synchronized void setLimited(boolean limited) {
	this.limited = limited;
    }

    /**
     * Gets the value of the quiet option (verbosity of command)
     * 
     * @return the value of the quiet option
     */
    public synchronized boolean isQuiet() {
	return quiet;
    }

    /**
     * Sets the value of the quiet option (verbosity of command)
     * 
     * @param quiet
     *                the value of the quiet option
     */
    public synchronized void setQuiet(boolean quiet) {
	this.quiet = quiet;
    }

    /**
     * Gets the value of the verify option (check certificate content after
     * creation)
     * 
     * @return the value of the verify option
     */
    public synchronized boolean isVerify() {
	return verify;
    }

    /**
     * Sets the value of the verify option (check certificate content after
     * creation)
     * 
     * @param verify
     *                the value of the verify option
     */
    public synchronized void setVerify(boolean verify) {
	this.verify = verify;
    }

    /**
     * Gets the list of contact string to use during creation
     * 
     * @return the list of contact string to use during creation
     */
    @Deprecated
    public synchronized Set<String> getVoms() {
	Set<String> vomses = new HashSet<String>();

	for (VOMSFQANInfo vomsInfo : this.vomsInfo) {
	    vomses.add(vomsInfo.getString());
	}
	return vomses;
    }

    /**
     * Sets the list of contact string to use during creation
     * 
     * @param vomses
     *                the list of contact string to use during creation
     * 
     * @throws Exception
     *                 if one or more contact string is not well formed
     * 
     * 
     */
    @Deprecated
    public synchronized void setVomses(Set<String> vomses) throws Exception {
	Set<VOMSFQANInfo> vomsInfo = new HashSet<VOMSFQANInfo>();

	for (String voms : vomses) {
		vomsInfo.add(VOMSFQANFactory.generateVOMSFQAN(voms));
	}

	this.vomsInfo = vomsInfo;
    }

    /**
     * Adds a contact string to the list of contacts to use during creation
     * 
     * @param voms
     *                the contact string to add
     * @throws Exception
     *                 if the contact string is not well formed
     */
    @Deprecated
    public synchronized void setVoms(String voms) throws Exception {
	this.vomsInfo.add(VOMSFQANFactory.generateVOMSFQAN(voms));
    }

    /**
     * Gets the number of hours of validity of Attribute Certificates
     * contained in the proxy to create
     * 
     * @return the number of hours of validity of Attribute Certificates
     *         contained in the proxy to create
     */
    public synchronized Integer getVomslife() {
	return vomslife;
    }

    /**
     * Sets the number of hours of validity of Attribute Certificates
     * contained in the proxy to create
     * 
     * @param vomslife
     *                the number of hours of validity of Attribute
     *                Certificates contained in the proxy to create
     */
    public synchronized void setVomslife(Integer vomslife) {
	this.vomslife = vomslife;
    }

    /**
     * Gets the output file where to put the proxy certificate to create
     * 
     * @return the output file where to put the proxy certificate to create
     */
    public synchronized File getOutput() {
	return output;
    }

    /**
     * Sets the output file where to put the proxy certificate to create
     * 
     * @param output
     *                the output file where to put the proxy certificate to
     *                create
     */
    public synchronized void setOutput(File output) {
	this.output = output;
    }

    /**
     * Gets VOMS FQAN info to be added to the generated proxy
     * 
     * @return the voms info
     */
    public Set<VOMSFQANInfo> getVomsFQANInfo() {
	return vomsInfo;
    }

    /**
     * Sets VOMS info to be added to the generated proxy
     * 
     * @param vomsInfo
     *                the VOMSFQANInfo to set
     */
    public void setVomsRoles(Set<VOMSFQANInfo> vomsInfo) {
	this.vomsInfo = vomsInfo;
    }

    /**
     * Adds a VOMS FQAN info to the set
     * 
     * @param vomsInfo
     *                the vomsInfo to add
     */
    public void addVomsFQANInfo(VOMSFQANInfo vomsInfo) 
    {
    	this.vomsInfo.add(vomsInfo);
    }


    protected void setupUserCredentials(String userCert,String userKey){
	System.setProperty( "X509_USER_CERT", userCert);
	System.setProperty( "X509_USER_KEY", userKey);

    }

    protected void setupVomsesPath(String vomsesPath){

	System.setProperty( "VOMSES_LOCATION", vomsesPath);

    }

    protected void setupVomsdir(String vomsdir){

	System.setProperty("VOMSDIR",vomsdir);

    }

    protected void setupCaDir(String caDir)
    {
	System.setProperty("CADIR",caDir);

    }

    public void addVomsServer (String hostName, String hostDN, int hostPort, String voName)
    {
		VOMSServerInfo info = new VOMSServerInfo();
		info.setHostDn(hostDN);
		info.setHostName(hostName);
		info.setPort(hostPort);
		info.setVoName(voName);
		this.vomsServerList.add(info);
    }


    private void buildProxy(File certificate, File key,String keyPassword)
    {
    	VOMSProxyInit proxyInit = null;
		String proxyOutput = null;
		// set other options
		if (this.output != null) 
		{
		    proxyOutput = this.output.getAbsolutePath();
		}
	
		String[] fqans = null;
		String ordering = null;
		if (keyPassword != null) proxyInit = VOMSProxyInit.instance(keyPassword);
		else
		{
		    logger.warn( "No password given to decrypt the openssl private key..." );
		    proxyInit = VOMSProxyInit.instance();
		}
		
		// CIRO
		
//		VOMSServerInfo info = new VOMSServerInfo();
//		info.setHostDn("/C=IT/O=INFN/OU=Host/L=NMIS-ISTI/CN=voms.research-infrastructures.eu");
//		info.setHostName("voms.research-infrastructures.eu");
//		info.setPort(15000);
//		info.setVoName("d4science.research-infrastructures.eu");
//		proxyInit.addVomsServer(info);
		// CIRO
	
		for (VOMSServerInfo serverInfo : this.vomsServerList)
		{
			proxyInit.addVomsServer(serverInfo);
		}
		
		if (proxyOutput != null) proxyInit.setProxyOutputFile( proxyOutput );
	
		if (this.proxyType != null)
		{
		    int type = VOMSProxyBuilder.GT2_PROXY;
	
		    if (this.proxyType.equals( GT2_PROXY )) type = VOMSProxyBuilder.GT2_PROXY;
		    else if (this.proxyType.equals( GT3_PROXY )) type = VOMSProxyBuilder.GT3_PROXY;
		    else if (this.proxyType.equals( GT4_PROXY )) type = VOMSProxyBuilder.GT4_PROXY;
		    else logger.warn( "Unsupported proxy type specified! The default value will be used." );
	
		    proxyInit.setProxyType( type );
	
		}
	
		if (this.policyType != null) proxyInit.setPolicyType( this.policyType );
	
		if (this.delegationType != null)
		{
		    int type = VOMSProxyBuilder.DEFAULT_DELEGATION_TYPE;
	
		    if (this.delegationType.equals( NONE )) type = GSIConstants.DELEGATION_NONE;
		    else if (this.delegationType.equals( LIMITED )) type = GSIConstants.DELEGATION_LIMITED;
		    else if (this.delegationType.equals( FULL )) type = GSIConstants.DELEGATION_FULL;
		    else logger.warn( "Unsupported delegation type specified! The default value will be used." );
	
		    proxyInit.setDelegationType( type );
		}
	
		if(this.vomsInfo.isEmpty()) 
		{
	//		VOMSRequestOptions o  = new VOMSRequestOptions();
	//		o.setVoName("d4science.research-infrastructures.eu");
	//		Map options = new HashMap();
	//		options.put("d4science.research-infrastructures.eu",o);
	//		proxyInit.getVomsProxy( options.values());
			
		    proxyInit.getVomsProxy();
		} 
		else 
		{
		    Map<String, VOMSRequestOptions> options = new HashMap<String, VOMSRequestOptions>();
		    
		    for (Iterator<VOMSFQANInfo> iter = this.vomsInfo.iterator(); iter.hasNext();) 
		    {
		    	VOMSFQANInfo vomsInfo = iter.next();
		    	String voName = vomsInfo.getVoName();
		    	
	//		String element = (String) iter.next().toString();
	//
	//		String[] opts = element.split( ":" );
	
	//		if (opts.length != 2)
	//		    throw new VOMSException("Voms FQANs must be specified according to the <voName>:<fqan> syntax (e.g., cms:/cms/Role=lcgadmin).");
	
	//		String voName = opts[0];
	
			VOMSRequestOptions o;
	
			if (options.containsKey( voName ))
			    o = options.get( voName );
			else{
	
			    o = new VOMSRequestOptions();
			    o.setVoName( voName );
			    options.put(voName,o);
			}
	
			String fqan = vomsInfo.getFQAN();
			
			if (fqan != null) o.addFQAN( fqan );
	
			if (ordering != null)
			    o.setOrdering( ordering );
	
		    }
		    proxyInit.getVomsProxy( options.values());
		}
	
	
		logger.debug("fqans:"+ToStringBuilder.reflectionToString( fqans ));
		/*
		if (fqans == null)
		    proxyInit.getVomsProxy();
		else{
	
		    Map options = new HashMap();
	
		    for ( int i = 0; i < fqans.length; i++ ) {
	
			String[] opts = fqans[i].split( ":" );
	
			if (opts.length != 2)
			    throw new VOMSException("Voms FQANs must be specified according to the <voName>:<fqan> syntax (e.g., cms:/cms/Role=lcgadmin).");
	
			String voName = opts[0];
	
			VOMSRequestOptions o;
	
			if (options.containsKey( voName ))
			    o = (VOMSRequestOptions) options.get( voName );
			else{
	
			    o = new VOMSRequestOptions();
			    o.setVoName( voName );
			    options.put(voName,o);
	
			}
	
			o.addFQAN( opts[1] );
	
			if (ordering != null)
			    o.setOrdering( ordering );
	
		    }
	
		    proxyInit.getVomsProxy( options.values());
		}
		*/

    }

	public void setProxyType(String proxyType) {
		this.proxyType = proxyType;
	}

	public void setDelegationType(String delegationType) {
		this.delegationType = delegationType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}
    
    
}
