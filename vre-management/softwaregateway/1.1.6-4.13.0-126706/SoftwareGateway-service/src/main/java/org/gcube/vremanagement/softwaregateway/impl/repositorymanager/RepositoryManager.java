package org.gcube.vremanagement.softwaregateway.impl.repositorymanager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.Coordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.MavenCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;

public abstract class RepositoryManager{
	
	public static String [] servers;
	public static final String SERVICE_ARCHIVE_IDENTIFIER="servicearchive";
	private static GCUBELog logger= new GCUBELog(RepositoryManager.class);
	protected boolean cacheEnabled = true;
	
	
	public RepositoryManager(String [] mavenServerList){
		servers= mavenServerList;
		if(mavenServerList!=null){
			logger.debug("server added:");
			for(String s : mavenServerList){
				logger.debug("srv: "+s);
			}
		}else{
			logger.debug("no server added:");
		}

	}

	
	/**
	 * Return the URL for download a maven object (pom, tar.gz, jar) that corresponds to the maven coordinates in input
	 * @param mavenC: maven Coordinates
	 * @param extension: specify the kind of object that want to be returned
	 * @return
	 * @throws MalformedURLException
	 * @throws ServiceNotAvaiableFault 
	 */
    public abstract String get(Object mavenC, String extension, String classifier) throws MalformedURLException, ServiceNotAvaiableFault;

	/**
	 * 
	 * @param url the url of the remote pom 
	 * @param is 
	 * @return
	 * @throws BadCoordinatesException
	 * @throws Exception
	 */
	public abstract String  extractDepsFromMavenEmb(String url) throws ServiceNotAvaiableFault;
	
	
	
	/**
	 * Get location artifact
	 * @param baseUrl
	 * @param groupName
	 * @param artifact
	 * @param extension
	 * @param ver
	 * @param pom
	 * @param classifier
	 * @return
	 * @throws MalformedURLException
	 */
	public abstract String searchArtifact(String baseUrl, String groupName, String artifact, String extension, String ver, boolean pom, String classifier) throws MalformedURLException;

	/**
	 * Get location of software archive
	 * @param tmpTargetDirectory
	 * @param mcList
	 * @param coordinates
	 * @return
	 * @throws MalformedURLException
	 * @throws ServiceNotAvaiableFault
	 * @throws IOException
	 * @throws Exception
	 */
	public abstract String getSALocation(File tmpTargetDirectory,
			List<MavenCoordinates> mcList, Coordinates coordinates)
			throws MalformedURLException, ServiceNotAvaiableFault, IOException,
			Exception ;
	
}
