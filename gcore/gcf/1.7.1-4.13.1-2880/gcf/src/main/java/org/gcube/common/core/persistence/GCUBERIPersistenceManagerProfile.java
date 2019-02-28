package org.gcube.common.core.persistence;

import org.globus.wsrf.jndi.Initializable;

/**
 * Models the configuration of a {@link GCUBERIPersistenceManager}.
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBERIPersistenceManagerProfile implements Initializable {

	/**Default monitoring interval.*/
	public static final int DEFAULT_MONITORING_INTERVAL = 300;
	/**List of exclude directives.*/
	private String excludes;
	/** Fully qualified name of the subclass of {@link GCUBERIPersistenceManager} to be configured. */
	private String className;
	/**Monitoring interval.*/
	private Integer monitoringInterval=DEFAULT_MONITORING_INTERVAL;
	
	/**Initialise the instance.*/
	public void initialize() throws Exception {
		//makes sure the manager class has been configured. further expectations must be checked by subclasses.
        if (this.className == null) throw new Exception("persistence manager class is missing");
	}
	
	/**
	 * Returns the fully qualified name of the subclass of {@link GCUBERIPersistenceManager} to be configured.
	 * @return the name.
	 */
	public String getClassName() {return className;}
	/**
	 * Sets the fully qualified name of the subclass of {@link GCUBERIPersistenceManager} to be configured.
	 * @param className the name.
	 */
	public void setClassName(String className) {this.className = className;}
	/**
	 * Returns the monitoring interval.
	 * @return the interval.
	 */
	public Integer getMonitoringInterval() {return monitoringInterval;}
	/**
	 * Sets the monitoring interval.
	 * @param monitoringInterval the interval.
	 */
	public void setMonitoringInterval(Integer monitoringInterval) {this.monitoringInterval = monitoringInterval;}
	/**
	 * Sets the list of exclude directives as a comma-separated string.
	 * @param excludes the list.
	 */
	public void setExcludes(String excludes) {this.excludes=excludes;}
	/**
	 * Returns the list of exclude directives.
	 * @return the list.
	 */
	public String getExcludes() {return this.excludes;}
}
