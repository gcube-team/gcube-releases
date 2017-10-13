package org.gcube.resourcemanagement.whnmanager.api.types;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)
public class ShutdownOptions{
//	@XmlElement
	public boolean restart;
	
//	@XmlElement
	public boolean clean;
	
	protected ShutdownOptions(){}
	public ShutdownOptions(boolean restart, boolean clean){
		super();
		this.restart=restart;
		this.clean=clean;
		
	}

	public boolean isRestart() {
		return restart;
	}

//	public void setRestart(boolean restart) {
//		this.restart = restart;
//	}

	public boolean isClean() {
		return clean;
	}

//	public void setClean(boolean clean) {
//		this.clean = clean;
//	}
	

}
