/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: SingletonResourceStorage.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.resources;

import java.util.HashMap;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.utils.console.PrettyFormatter;

/**
 * Consists of the {@link GCUBEWSResource} used by {@link org.gcube.vremanagement.resourcebroker.impl.services.BrokerService}
 * to store and handle the plans for deploying requests
 * coming from VREManagerService.
 * Instantiated in a singleton way stores all the data needed to keep
 * the {@link org.gcube.vremanagement.resourcebroker.impl.services.BrokerService} state persistent.
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class SingletonResourceStorage
	extends GCUBEWSResource {
	private GCUBELog logger = new GCUBELog(this, BrokerConfiguration.getProperty("LOGGING_PREFIX"));
	private HashMap<String, Object> state = null;

	@Override
	protected final synchronized void initialise(final Object... arg0) throws Exception {
		logger.debug("[RESOURCE-INIT] " + this.getClass().getSimpleName() + ": creating the singleton resource storage. The service is now " + PrettyFormatter.bold("[READY]"));
		this.state = new HashMap<String, Object>();
	}

	public final synchronized Object getElem(final String key) {
		if (key != null) {
			return this.state.get(key.trim());
		}
		return null;
	}

	public final synchronized void addElement(final String key, final Object value) {
		logger.debug("[RESOURCE-ADD] adding elem: " + key);
		if (key != null) {
			this.state.put(key.trim(), value);
		}
	}

	public final synchronized void deleteElement(final String key) {
		logger.debug("[RESOURCE-DEL] removing elem: " + key);
		if (key != null) {
			this.state.remove(key.trim());
		}
	}

	public final synchronized boolean containsKey(final String key) {
		if (this.state != null && key != null) {
			return this.state.containsKey(key.trim());
		}
		return false;
	}

	public final synchronized boolean containsValue(final Object value) {
		return this.state.containsValue(value);
	}

	/**
	 * @deprecated for internal use only
	 */
	public final synchronized HashMap<String, Object> getState() {
		return this.state;
	}
}
