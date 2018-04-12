package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import org.apache.maven.artifact.DefaultArtifact;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.MavenCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;


/**
 * @author Luca Frosini (ISTI-CNR)
 */
public class ArtifactCoordinates {
	/**
	 * Class logger. 
	 */
	private static final GCUBELog logger = new GCUBELog(ArtifactCoordinates.class);
	
	
	private String groupID; // ServiceClass.ServiceName.ServiceVersion
	private String artifactID; // PackageName
	private String artifactVersion; //PackageVersion
	
	private String artifactDescription = null; // Package Description
	
	private String packaging = ArtifactConstants.DEFAULT_PACKAGING; // tar.gz
	private String classifier = null; // GHN, VRE, VO
	private String scope = ArtifactConstants.DEFAULT_DEPENDENCY_SCOPE; // runtime
	private boolean optional = false;
	
	/**
	 * @param groupID Group ID
	 * @param artifactID Artifact ID
	 * @param artifactVersion Version
	 * @throws Exception if fails
	 */
	public ArtifactCoordinates(String groupID,String artifactID,String artifactVersion, String scope, String classifier) throws Exception {
		if(groupID!=null && groupID.compareTo("")!=0){
			this.groupID = groupID;
		}else{
			IllegalArgumentException e = new IllegalArgumentException();
			logger.debug(e);
			throw e;
		}
		
		if(artifactID!=null && artifactID.compareTo("")!=0){
			this.artifactID = artifactID;
		}else{
			IllegalArgumentException e = new IllegalArgumentException();
			logger.debug(e);
			throw e;
		}
		
		if(artifactVersion!=null && artifactVersion.compareTo("")!=0){
			this.artifactVersion = artifactVersion;
		}else{
			IllegalArgumentException e = new IllegalArgumentException();
			logger.debug(e);
			throw e;
		}
		this.scope=scope;
		this.classifier=classifier;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString(){
		return groupID + ":" + artifactID + ":" + packaging + ":" + (classifier!=null? classifier + ":" : "") + artifactVersion + ":" + scope;
	}
	
	/**
	 * @param rootTag name of the root tag to wrap coordinates tags
	 * @param is 
	 * @return artifact as XML
	 * @throws ServiceNotAvaiableFault 
	 */
	public String toXML(String rootTag) throws ServiceNotAvaiableFault{
		logger.debug("rootTag: "+rootTag);
		logger.debug("rootTag: "+groupID);
		logger.debug("rootTag: "+artifactID);
		logger.debug("rootTag: "+artifactVersion);
		StringBuilder sb = new StringBuilder();
		sb.append("<" + rootTag + ">" + "\n");
		
		sb.append("\t<Service>\n");
		GCubeCoordinates gc=null;
		try {
			MavenCoordinates mc=new MavenCoordinates(groupID, artifactID, artifactVersion);
			gc=mc.getGcubeCoordinates();
		} catch (BadCoordinatesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String serviceClass=gc.getServiceClass();
		String serviceName=gc.getServiceName();
		String serviceVersion=gc.getServiceVersion();//"1.0.0";
		String packageName=gc.getPackageName();
		String packageVersion=gc.getPackageVersion();
/**/		
		sb.append("\t\t<Class>").append(serviceClass).append("</Class>\n");
		sb.append("\t\t<Name>").append(serviceName).append("</Name>\n");
		sb.append("\t\t<Version>").append(serviceVersion).append("</Version>\n");
		sb.append("\t</Service>\n");
		logger.debug("ArtifactID = " + artifactID);
		sb.append("\t<Package>").append(packageName).append("</Package>\n");
		logger.debug("Version = " + artifactVersion);
		sb.append("\t<Version>").append(packageVersion).append("</Version>\n");
		sb.append("</" + rootTag + ">\n");
		return sb.toString();
	}
	
	/**
	 * @return artifactID
	 */
	public String getArtifactID() {
		return artifactID;
	}
	
	/**
	 * @return groupID
	 */
	public String getGroupID() {
		return groupID;
	}
	
	/**
	 * @return version
	 */
	public String getArtifactVersion() {
		return artifactVersion;
	}

	/**
	 * @return the packaging
	 */
	public String getPackaging() {
		return packaging;
	}

	/**
	 * @param packaging the packaging to set
	 */
	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * @return the artifactDescription
	 */
	public String getArtifactDescription() {
		return artifactDescription;
	}

	/**
	 * @param artifactDescription the artifactDescription to set
	 */
	public void setArtifactDescription(String artifactDescription) {
		this.artifactDescription = artifactDescription;
	}

	/**
	 * @param classifier the classifier to set
	 */
	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}
	
	/**
	 * @return Classifier
	 */
	public String getClassifier() {
		return classifier;
	}

	/**
	 * @return the optional
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * @param optional the optional to set
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public String toXML(String rootTag, DefaultArtifact artifact) throws ServiceNotAvaiableFault{
		StringBuilder sb = new StringBuilder();
		sb.append("<" + rootTag + ">" + "\n");
		
		sb.append("\t<Service>\n");
		
		String serviceClass, serviceName, serviceVersion;
		GCubeCoordinates gc=null;
		try {
			MavenCoordinates mc=new MavenCoordinates(groupID, artifactID, artifactVersion);
			gc=mc.getGcubeCoordinates();
		} catch (BadCoordinatesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serviceClass=gc.getServiceClass();
		serviceName=gc.getServiceName();
		serviceVersion=gc.getServiceVersion();//"1.0.0";
		String packageName=gc.getPackageName();
		String packageVersion=gc.getPackageVersion();
		sb.append("\t\t<Class>").append(serviceClass).append("</Class>\n");
		sb.append("\t\t<Name>").append(serviceName).append("</Name>\n");
		sb.append("\t\t<Version>").append(serviceVersion).append("</Version>\n");
		sb.append("\t</Service>\n");
		
		logger.debug("ArtifactID = " + artifactID);
		sb.append("\t<Package>").append(packageName).append("</Package>\n");
		logger.debug("Version = " + artifactVersion);
		sb.append("\t<Package>").append(packageVersion).append("</Package>\n");
		sb.append("</" + rootTag + ">\n");
		return sb.toString();
	}
	
}
