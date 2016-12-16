package org.gcube.data.analysis.dataminermanagercl.server.dmservice;


import org.gcube.data.analysis.dataminermanagercl.server.util.ServiceCredentials;
import org.gcube.data.analysis.dataminermanagercl.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder of Client 4 WPS Service
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SClient4WPSBuilder extends SClientBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(SClient4WPSBuilder.class);
	private ServiceCredentials serviceCredendials;

	public SClient4WPSBuilder(ServiceCredentials serviceCredentials) {
		this.serviceCredendials= serviceCredentials;

	}

	@Override
	public void buildSClient() throws ServiceException {
		try {
			logger.debug("Build SC4WPS");
			logger.debug("ServiceCredentials: " + serviceCredendials);
			SClient sClient = new SClient4WPS(serviceCredendials);

			sClientSpec.setSClient(sClient);
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(),e);
		}
	}

}
