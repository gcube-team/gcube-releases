package org.gcube.dataanalysis.dataminer.poolmanager.clients.configurations;

import javax.xml.bind.annotation.XmlElement;


public abstract class AbstractConfiguration 
{

	private String 	host;
	private String algorithmsList;
	private String softwareRepo;
	private String ghostRepo;
	private String depsLinuxCompiled;
	private String depsPreInstalled;
	private String depsRBlackbox;
	private String depsR;
	private String depsJava;
	private String depsKnimeWorkflow;
	private String depsOctave;
	private String depsPython;
	private String depsWindowsCompiled;
	
	
	
	
	
	@XmlElement (name="host")
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	
	@XmlElement (name="algorithms-list")
	public String getAlgorithmsList() {
		return algorithmsList;
	}
	public void setAlgorithmsList(String algorithmsList) {
		this.algorithmsList = algorithmsList;
	}
	
	@XmlElement (name="software-repo")
	public String getSoftwareRepo() {
		return softwareRepo;
	}
	public void setSoftwareRepo(String softwareRepo) {
		this.softwareRepo = softwareRepo;
	}
	
	@XmlElement (name="ghost-repo")
	public String getGhostRepo() {
		return ghostRepo;
	}
	
	
	public void setGhostRepo(String ghostRepo) {
		this.ghostRepo = ghostRepo;
	}
	
	
	@XmlElement (name="deps-linux-compiled")
	public String getDepsLinuxCompiled() {
		return depsLinuxCompiled;
	}
	
	
	public void setDepsLinuxCompiled(String depsLinuxCompiled) {
		this.depsLinuxCompiled = depsLinuxCompiled;
	}
	
	@XmlElement (name="deps-pre-installed")
	public String getDepsPreInstalled() {
		return depsPreInstalled;
	}
	public void setDepsPreInstalled(String depsPreInstalled) {
		this.depsPreInstalled = depsPreInstalled;
	}
	
	@XmlElement (name="deps-r-blackbox")
	public String getDepsRBlackbox() {
		return depsRBlackbox;
	}
	public void setDepsRBlackbox(String depsRBlackbox) {
		this.depsRBlackbox = depsRBlackbox;
	}
	
	@XmlElement (name="deps-r")
	public String getDepsR() {
		return depsR;
	}
	public void setDepsR(String depsR) {
		this.depsR = depsR;
	}
	
	@XmlElement (name="deps-java")
	public String getDepsJava() {
		return depsJava;
	}
	public void setDepsJava(String depsJava) {
		this.depsJava = depsJava;
	}
	
	@XmlElement (name="deps-knime-workflow")
	public String getDepsKnimeWorkflow() {
		return depsKnimeWorkflow;
	}
	public void setDepsKnimeWorkflow(String depsKnimeWorkflow) {
		this.depsKnimeWorkflow = depsKnimeWorkflow;
	}
	
	@XmlElement (name="deps-octave")
	public String getDepsOctave() {
		return depsOctave;
	}
	public void setDepsOctave(String depsOctave) {
		this.depsOctave = depsOctave;
	}
	
	@XmlElement (name="deps-python")
	public String getDepsPython() {
		return depsPython;
	}
	public void setDepsPython(String depsPython) {
		this.depsPython = depsPython;
	}
	
	@XmlElement (name="deps-windows-compiled")
	public String getDepsWindowsCompiled() {
		return depsWindowsCompiled;
	}
	public void setDepsWindowsCompiled(String depsWindowsCompiled) {
		this.depsWindowsCompiled = depsWindowsCompiled;
	}
	
	

	protected String getXML (String type)
	{
		return "<"+type+"><host>{$resource/Profile/Body/"+type+"/ghost/text()}</host>"+
				"<algorithms-list>{$resource/Profile/Body/"+type+"/algorithms-list/text()}</algorithms-list>"+
				" <software-repo>{$resource/Profile/Body/"+type+"/software.repo/text()}</software-repo>"+
				"<ghost-repo>{$resource/Profile/Body/"+type+"/algo.ghost.repo/text()}</ghost-repo>"+
				"<deps-linux-compiled>{$resource/Profile/Body/"+type+"/deps-linux-compiled/text()}</deps-linux-compiled>"+
				"<deps-pre-installed>{$resource/Profile/Body/"+type+"/deps-pre-installed/text()}</deps-pre-installed>"+
				"<deps-r-blackbox>{$resource/Profile/Body/"+type+"/deps-r-blackbox/text()}</deps-r-blackbox>"+
			    "<deps-r>{$resource/Profile/Body/"+type+"/deps-r/text()}</deps-r>"+	        
			    "<deps-java>{$resource/Profile/Body/"+type+"/deps-java/text()}</deps-java>"+		        
			    "<deps-knime-workflow>{$resource/Profile/Body/"+type+"/deps-knime-workflow/text()}</deps-knime-workflow >"+
			    "<deps-octave>{$resource/Profile/Body/"+type+"/deps-octave/text()}</deps-octave>"+    
			    "<deps-python>{$resource/Profile/Body/"+type+"/deps-python/text()}</deps-python>"+
			    "<deps-windows-compiled>{$resource/Profile/Body/"+type+"/deps-windows-compiled/text()}</deps-windows-compiled></"+type+">";    
	}
	
	abstract public String getXMLModel ();
	
	abstract public String getType ();
	
}
