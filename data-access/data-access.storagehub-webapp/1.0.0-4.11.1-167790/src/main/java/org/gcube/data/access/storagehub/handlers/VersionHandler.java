package org.gcube.data.access.storagehub.handlers;

import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class VersionHandler {

	private static final Logger logger = LoggerFactory.getLogger(VersionHandler.class);

	public void makeVersionableContent(Node node, Session session){
		try {
			Node contentNode = node.getNode("jcr:content");
			contentNode.addMixin(JcrConstants.MIX_VERSIONABLE);
		}catch(Exception e ) {
			logger.warn("cannot create versioned content node",e);
		}
	}

	public void checkinContentNode(Node node, Session session){
		try {
			Node contentNode = node.getNode("jcr:content");
			VersionManager versionManager = session.getWorkspace().getVersionManager();
			Version version = versionManager.checkin(contentNode.getPath());
		}catch(Exception e ) {
			logger.warn("cannotcheckinNode content node",e);
		}
	}

}
