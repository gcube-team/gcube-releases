package org.gcube.portlets.user.dataminermanager.server.smservice;

import org.gcube.portlets.user.dataminermanager.server.util.ServiceCredential;
import org.gcube.portlets.user.dataminermanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client 4 WPS
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SClient4WPSBuilder extends SClientBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(SClient4WPSBuilder.class);
	private ServiceCredential serviceCredendial;

	public SClient4WPSBuilder(ServiceCredential serviceCredential) {
		this.serviceCredendial= serviceCredential;

	}

	@Override
	public void buildSClient() throws ServiceException {
		try {
			logger.debug("Build SM4WPS");
			logger.debug("ServiceCredential: " + serviceCredendial);
			SClient smClient = new SClient4WPS(serviceCredendial);

			sClientSpec.setSClient(smClient);
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

}
