package org.gcube.data.access.storagehub.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.core.version.VersionManagerImplBase;
import org.gcube.common.storagehub.model.NodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class VersionHandler {

	private static final Logger logger = LoggerFactory.getLogger(VersionHandler.class);

	public void makeVersionableContent(Node node, Session session){
		try {
			Node contentNode = node.getNode(NodeConstants.CONTENT_NAME);
			contentNode.addMixin(JcrConstants.MIX_VERSIONABLE);
		}catch(Exception e ) {
			logger.warn("cannot create versioned content node",e);
		}
	}

	public void checkinContentNode(Node node, Session session){
		try {
			Node contentNode = node.getNode(NodeConstants.CONTENT_NAME);
			VersionManager versionManager = session.getWorkspace().getVersionManager();
			versionManager.checkin(contentNode.getPath());
		}catch(Exception e ) {
			logger.warn("cannotcheckinNode content node",e);
		}
	}

	public void checkoutContentNode(Node node, Session session){
		try {
			Node contentNode = node.getNode(NodeConstants.CONTENT_NAME);
			VersionManager versionManager = session.getWorkspace().getVersionManager();
			versionManager.checkout(contentNode.getPath());
		}catch(Exception e ) {
			logger.warn("cannot checkoutNode content node",e);
		}
	}
	
	public List<Version> getContentVersionHistory(Node node, Session session) {
		try {
			Node contentNode = node.getNode(NodeConstants.CONTENT_NAME);
			VersionManager versionManager = session.getWorkspace().getVersionManager();
			VersionHistory history = versionManager.getVersionHistory(contentNode.getPath());
			VersionIterator iterator = history.getAllVersions();
			iterator.skip(1);
			List<Version> versions = new ArrayList<>();
			while (iterator.hasNext()) {
				Version version = iterator.nextVersion();
				versions.add(version);
				logger.debug("version name {} with nodeType {}",version.getName(),version.getPrimaryNodeType().getName());
			}
			return versions;
		}catch(Exception e ) {
			logger.warn("cannot get version history content node",e);
			return Collections.emptyList();
		}
	}
	
}
