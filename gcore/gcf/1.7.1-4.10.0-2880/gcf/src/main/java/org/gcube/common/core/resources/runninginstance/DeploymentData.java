package org.gcube.common.core.resources.runninginstance;

import java.util.Calendar;

/***
 * 
 * @author   Manuele Simi (ISTI-CNR)
 *
 */
public class DeploymentData {

  
    protected Calendar activationTime;
  
    protected Calendar terminationTime;
  
    public String state;
  
    protected String messageState;
    
    protected AvailablePlugins plugins = new AvailablePlugins();
    
    protected String instanceName;
    
    protected String localPath;
  

    /**
	 * @return the plugins
	 */
	public AvailablePlugins getPlugins() {
		return plugins;
	}

	/**
	 * @param plugins the plugins to set
	 */
	public void setPlugins(AvailablePlugins plugins) {
		this.plugins = plugins;
	}

	/** Gets the value of the activationTime property.
    *
    * @return the time
    *
    */
   public Calendar getActivationTime() {
       return activationTime;
   }

   /**
    * Sets the value of the activationTime property.
    *
    * @param string the time
    *
    */
   public void setActivationTime(Calendar string) {
       this.activationTime = string;
   }

   /**
    * Sets the value of the activationTime property.
    *
    * @param value the time
    *
    */
   public void setTerminationTime(Calendar value) {
       this.terminationTime = value;
   }

   /**
    * Gets the value of the terminationTime property.
    *
    * @return the time
    *
    */
   public Calendar getTerminationTime() {
       return terminationTime;
   }
   /**
    * Gets the value of the state property.
    *
    * @return the state.
    *
    */
   public String getState() {
       return state;
   }

   /**
    * Sets the value of the state property.
    *
    * @param value the state.
    *
    */
   public void setState(String value) {
       this.state = value;
   }
   /**
    * Sets the value of the messageState property.
    *
    * @param value the message state.
    *
    */
   public void setMessageState(String value) {
       this.messageState = value;
   }

   /**
    * Gets the value of the messageState property.
    *
    * @return the message state.
    *
    */
   public String getMessageState() {
       return messageState;
   }
   
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		final DeploymentData other = (DeploymentData) obj;
		
		if (terminationTime == null) {
			if (other.terminationTime != null)
				return false;
		} else if (! terminationTime.equals(other.terminationTime))
			return false;
		
		if (activationTime == null) {
			if (other.activationTime != null)
				return false;
		} else if (! activationTime.equals(other.activationTime))
			return false;
		
		if (messageState == null) {
			if (other.messageState != null)
				return false;
		} else if (! messageState.equals(other.messageState))
			return false;
		
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (! state.equals(other.state))
			return false;
		
		
		return true;
	}

	/**
	 * @return the instanceName
	 */
	public String getInstanceName() {
		return instanceName;
	}

	/**
	 * @param instanceName the instanceName to set
	 */
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	/**
	 * @return the instance local path
	 */
	public String getLocalPath() {
		return localPath;
	}

	/**
	 * @param localPath the instance local path to set
	 */
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}


}
