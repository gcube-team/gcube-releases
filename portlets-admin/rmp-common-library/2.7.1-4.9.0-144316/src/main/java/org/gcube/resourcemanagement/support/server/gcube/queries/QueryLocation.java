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
 * Filename: QueryLocator.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.gcube.queries;

import java.io.InputStream;

/**
 * Keeps the association between an xquery and the path
 * on which it can be retrieved.
 * @author Daniele Strollo 
 * @author Massimiliano Assante (ISTI-CNR)
 */
public enum QueryLocation {
	// These two queries are needed to build the tree of resource types and subtypes
	// for resources different from wsresources.
	GET_TREE_TYPES("getTypes.xq"),
	GET_TREE_SUBTYPES("getSubTypes.xq"),

	// Customized queries to retrieve the relevant data from resources
	// according to their type
	LIST_GHN("resources/GHN.xq"),
	RETURN_GHN("resources/RETURN_GHN.xq"),
	LIST_Collection("resources/Collection.xq"),
	RETURN_Collection("resources/RETURN_Collection.xq"),
	LIST_VIEW("resources/VIEW.xq"),
	LIST_Service("resources/Service.xq"),
	RETURN_Service("resources/RETURN_Service.xq"),
	LIST_GenericResource("resources/GenericResource.xq"),
	RETURN_GenericResource("resources/RETURN_GenericResource.xq"),
	LIST_RunningInstance("resources/RunningInstance.xq"),
	RETURN_RunningInstance("resources/RETURN_RunningInstance.xq"),
	LIST_RuntimeResource("resources/RuntimeResource.xq"),
	RETURN_RuntimeResource("resources/RETURN_RuntimeResource.xq"),

	// To retrieve the list of generic resources publishing plugins
	// to deploy activation records
	GET_GENERIC_RESOURCE_PLUGINS("getPlugins.xq"),
	RETURN_GET_GENERIC_RESOURCE_PLUGINS("RETURN_getPlugins.xq"),
	
	// to deploy activation records for Tree manager
	GET_GENERIC_RESOURCE_TREE_MANAGER_PLUGINS("getTreeManagerPlugins.xq"),

	// Related resources
	LIST_RELATED_GHN("related/GHN.xq"),
	LIST_RELATED_RETURN_GHN("related/RETURN_GHN.xq"),
	LIST_RELATED_RunningInstance("related/RunningInstance.xq"),
	LIST_RELATED_RETURN_RunningInstance("related/RETURN_RunningInstance.xq"),
	LIST_RELATED_Service("related/Service.xq"),
	LIST_RELATED_RETURN_Service("related/RETURN_Service.xq"),

	// Queries for sweeper
	SWEEPER_EXPIRED_GHN("sweeper/expiredGhns.xq"),
	RETURN_SWEEPER_EXPIRED_GHN("sweeper/RETURN_expiredGhns.xq"),
	
	SWEEPER_DEAD_GHN("sweeper/deadGhns.xq"),
	RETURN_SWEEPER_DEAD_GHN("sweeper/RETURN_deadGhns.xq"),
	
	SWEEPER_ORPHAN_RI("sweeper/orphanRI.xq"),
	RETURN_SWEEPER_ORPHAN_RI("sweeper/RETURN_orphanRI.xq"),

	// Used to build the gwt model representation inside dialogs.
	// see getResourceModels inside ISClientRequester.
	GET_RES_DETAILS_BYTYPE("getResourcesDetails.xq"),
	RETURN_GET_RES_DETAILS_BYTYPE("RETURN_getResourcesDetails.xq"),
	GET_RES_DETAILS_BYSUBTYPE("getResourcesDetailsSubtype.xq"),
	RETURN_GET_RES_DETAILS_BYSUBTYPE("RETURN_getResourcesDetailsSubtype.xq"),

	GET_RESOURCE_BYID("getResourceByID.xq"),
	GET_WSRES_TYPES("getWSResourcesTypes.xq"),
	GET_WSRES_DETAILS_BYTYPE("getWSResourcesDetails.xq"),
	GET_WSRES_DETAILS_BYSUBTYPE("getWSResourcesDetailsSubType.xq"),
	RETURN_GET_WSRES_DETAILS_BYSUBTYPE("RETURN_getWSResourcesDetailsSubType.xq"),
	GET_WSRESOURCE_BYID("getWSResourceByID.xq");

	private final String path = "org/gcube/resourcemanagement/support/server/gcube/queries/xquery/";
	private String filename = null;
	QueryLocation(final String filename) {
		this.filename = filename;
	}
	public InputStream getFileName() {
		return this.getClass().getClassLoader().getResourceAsStream(this.path + this.filename);
	}
}
