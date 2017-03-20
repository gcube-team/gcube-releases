/**
 * 
 */
package org.gcube.informationsystem.cache;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Class that initiates the JMX server and provides access to the MBean
 * (un)registration operations.
 * 
 * @author UoA
 * 
 */
public class ISCacheMM {

	/** MBean Server */
	private static MBeanServer mbs = null;

	/** The Logger this class uses */
	private static GCUBELog log = new GCUBELog(ISCacheMM.class);

	/**
	 * MBean registration operation.
	 * 
	 * @param name
	 *            object name string
	 * @param beanObj
	 *            MBean object that needs to be registered
	 * @throws InstanceAlreadyExistsException
	 *             self-explanatory
	 * @throws MBeanRegistrationException
	 *             self-explanatory
	 * @throws NotCompliantMBeanException
	 *             self-explanatory
	 * @throws MalformedObjectNameException
	 *             self-explanatory
	 */
	public static void registerISMBean(String name, Object beanObj)
			throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException {
		ObjectName nameObj;
		try {
			nameObj = new ObjectName(name);
			mbs.registerMBean(beanObj, nameObj);
		} catch (MalformedObjectNameException e) {
			log.error(e);
			throw new MalformedObjectNameException(e.getMessage());
		} catch (NullPointerException e) {
			log.error(e);
			throw new NullPointerException(e.getMessage());
		}
	}

	/**
	 * MBean unregistration operation
	 * 
	 * @param name
	 *            object name string
	 * @throws InstanceAlreadyExistsException
	 *             self-explanatory
	 * @throws MBeanRegistrationException
	 *             self-explanatory
	 * @throws NotCompliantMBeanException
	 *             self-explanatory
	 * @throws MalformedObjectNameException
	 *             self-explanatory
	 * @throws InstanceNotFoundException
	 *             self-explanatory
	 */
	public static void unregisterISMBean(String name)
			throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException,
			InstanceNotFoundException {
		ObjectName nameObj;
		try {
			nameObj = new ObjectName(name);
			mbs.unregisterMBean(nameObj);
		} catch (MalformedObjectNameException e) {
			log.error(e);
			throw new MalformedObjectNameException(e.getMessage());
		} catch (InstanceNotFoundException e) {
			log.error(e);
			throw new InstanceNotFoundException(e.getMessage());
		}
	}

	/*
	 * internal JMX initialization method
	 */
	private static void initMBeanServer() {
		mbs = ManagementFactory.getPlatformMBeanServer();
	}

	/**
	 * JMX initialization method. It can be called many times but only operates
	 * on the first invocation.
	 * 
	 */
	public static void init() {
		if (mbs == null)
			ISCacheMM.initMBeanServer();
	}

}
