package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;


public class IOWPSInformation {

	private String name;
	private String abstractStr;
	private String allowed;
	private String defaultVal;
	private String localMachineContent;
	private String content;
	private String mimetype; 
	private String classname;
	
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAbstractStr() {
		return abstractStr;
	}
	public void setAbstractStr(String abstractStr) {
		this.abstractStr = abstractStr;
	}
	public String getAllowed() {
		return allowed;
	}
	public void setAllowed(String allowed) {
		this.allowed = allowed;
	}
	public String getDefaultVal() {
		return defaultVal;
	}
	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}
	public String getContent() {
		return content;
		/*
		try{
			return URLEncoder.encode(content,"UTF-8");
		}catch(Exception e){
			return content;
		}
		*/
	}
	public void setContent(String content) {
		if (content!=null && content.startsWith("http"))
			content = content.replace(" ", "%20");
		
		this.content = content;
	}
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public String getLocalMachineContent() {
		return localMachineContent;
	}
	public void setLocalMachineContent(String localMachineContent) {
		this.localMachineContent = localMachineContent;
	}
	
	
}
