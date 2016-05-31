/**
 * 
 */
package org.cotrix.gcube.extension;

import javax.enterprise.event.Observes;

import org.cotrix.action.events.CodelistActionEvents.Import;
import org.cotrix.action.events.CodelistActionEvents.Publish;
import org.cotrix.action.events.CodelistActionEvents.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public class GCubePublisher {

	private Logger logger = LoggerFactory.getLogger(GCubePublisher.class);

	public void onImportEvent(@Observes Import importEvent) {
		logger.trace("onImportEvent event: {}", importEvent);
		try {
			PortalProxy portalProxy = importEvent.session.get(PortalProxy.class);
			portalProxy.publish(importEvent.codelistName+" version "+ importEvent.codelistVersion+" now available.");
		} catch(Exception e) {
			logger.error("Failed news propagation", e);
		}
	}

	public void onPublishEvent(@Observes Publish publishEvent) {
		logger.trace("onPublishEvent event: {}", publishEvent);
		try {
			PortalProxy portalProxy = publishEvent.session.get(PortalProxy.class);
			portalProxy.publish(publishEvent.codelistName+" version "+ publishEvent.codelistVersion+" has just been published to "+publishEvent.repository+".");
		} catch(Exception e) {
			logger.error("Failed news propagation", e);
		}
	}

	public void onVersionEvent(@Observes Version versionEvent) {
		logger.trace("onVersionEvent event: {}", versionEvent);
		try {
			PortalProxy portalProxy = versionEvent.session.get(PortalProxy.class);
			portalProxy.publish("version "+versionEvent.codelistVersion+" of "+versionEvent.codelistName+" now available.");
		} catch(Exception e) {
			logger.error("Failed news propagation", e);
		}
	}



}
