package org.gcube.portlets.user.td.gwtservice.server.uriresolver;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.uriresolver.UriResolverSession;
import org.gcube.portlets.user.uriresolvermanager.UriResolverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for retrieve link 
 * 
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class UriResolverTDClient {
	private static Logger logger = LoggerFactory
			.getLogger(UriResolverTDClient.class);

	public UriResolverTDClient() {

	}

	public String resolve(UriResolverSession uriResolverSession,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		try {

			String link = "";

			logger.debug("Get uri from resolver: " + uriResolverSession);

			UriResolverManager resolver;
			Map<String, String> params = new HashMap<String, String>();

			switch (uriResolverSession.getApplicationType()) {
			case GIS:
				resolver = new UriResolverManager(uriResolverSession
						.getApplicationType().toString());
				params.put("gis-UUID", uriResolverSession.getUuid());
				params.put("scope", serviceCredentials.getScope());
				logger.debug("Uri Resolver params: " + params);
				link = resolver.getLink(params, true); // true, link is shorted
														// otherwise none
				break;
			case SMP:
				resolver = new UriResolverManager(uriResolverSession
						.getApplicationType().toString());
				params.put("smp-uri", uriResolverSession.getUuid());
				if (uriResolverSession.getFileName() == null) {
					params.put("fileName", "");
				} else {
					params.put("fileName", uriResolverSession.getFileName());
				}
				if (uriResolverSession.getMimeType() == null) {
					params.put("contentType", "");
				} else {
					params.put("contentType", uriResolverSession.getMimeType());
				}
				logger.debug("Uri Resolver params: " + params);
				link = resolver.getLink(params, true); // true, link is shorted
														// otherwise none
				break;
			case SMP_ID:
				resolver = new UriResolverManager(uriResolverSession
						.getApplicationType().toString());
				params.put("smp-id", uriResolverSession.getUuid());
				if (uriResolverSession.getFileName() == null) {
					params.put("fileName", "");
				} else {
					params.put("fileName", uriResolverSession.getFileName());
				}
				if (uriResolverSession.getMimeType() == null) {
					params.put("contentType", "");
				} else {
					params.put("contentType", uriResolverSession.getMimeType());
				}
				logger.debug("Uri Resolver params: " + params);
				link = resolver.getLink(params, true); // true, link is shorted
														// otherwise none
				break;

			default:
				logger.debug("No resolver enable on this application type");
				throw new TDGWTServiceException(
						"Error retrieving uri from resolver:"
								+ " No resolver enable on this application type");

			}

			logger.debug("Retrieved Link: " + link);
			return link;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error retrieving uri from resolver: "
							+ e.getLocalizedMessage());
		}

	}

}
