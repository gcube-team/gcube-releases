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
 * Filename: ResourceCommands.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.server;

import java.util.List;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resourcemanagement.support.client.utils.CurrentStatus;
import org.gcube.resourcemanagement.support.shared.util.Assertion;
import org.gcube.resourcemanagement.support.server.managers.resources.CollectionManager;
import org.gcube.resourcemanagement.support.server.managers.resources.GHNManager;
import org.gcube.resourcemanagement.support.server.managers.resources.GenericResourceManager;
import org.gcube.resourcemanagement.support.server.managers.resources.RuntimeResourceManager;
import org.gcube.resourcemanagement.support.server.types.AllowedResourceTypes;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;
import org.gcube.resourcemanagement.support.shared.exceptions.InvalidParameterException;
import org.gcube.resourcemanagement.support.shared.operations.SupportedOperations;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDescriptor;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ResourceCommands {
	private static final String LOG_PREFIX = "[RES-CMDS]";

	@SuppressWarnings("deprecation")
	public static void doOperation(
			final CurrentStatus status,
			final SupportedOperations opCode,
			final String scope,
			final List<ResourceDescriptor> resources)
	throws Exception {
		ServerConsole.debug(LOG_PREFIX, "[RES-COMMANDS-DO] Required operation: " + opCode);

		Assertion<InvalidParameterException> checker = new Assertion<InvalidParameterException>();
		checker.validate(opCode != null, new InvalidParameterException("Invalid operation required"));
		checker.validate(scope != null && scope.length() > 0,
				new InvalidParameterException("Invalid scope."));
		checker.validate(resources != null && resources.size() > 0,
				new InvalidParameterException("Invalid resource descriptor."));
		// Checks permissions
		checker.validate(opCode.isAllowed(status.getCredentials()),
				new InvalidParameterException("The current user is not allowed to execute the operation."));

		ScopeBean gscope = new ScopeBean(scope);

		/**********************************************************
		 * GHN
		 *********************************************************/

		// SHUTDOWN
		if (opCode.equals(SupportedOperations.GHN_SHUTDOWN)) {
			for (ResourceDescriptor resource : resources) {
				checker.validate(
						resource.getType().equals(AllowedResourceTypes.GHN.name()),
						new InvalidParameterException("Invalid type. GHN required"));
				new GHNManager(resource.getID(), resource.getName()).shutDown(gscope, false, false);
			}
		}

		// RESTART
		if (opCode.equals(SupportedOperations.GHN_RESTART) || opCode.equals(SupportedOperations.GHN_CLEAN_RESTART)) {
			for (ResourceDescriptor resource : resources) {
				checker.validate(
						resource.getType().equals(AllowedResourceTypes.GHN.name()),
						new InvalidParameterException("Invalid type. GHN required"));
				new GHNManager(resource.getID(), resource.getName()).shutDown(gscope,
						// restart
						true,
						// to clean?
						(opCode.equals(SupportedOperations.GHN_CLEAN_RESTART) ? true : false));
			}
		}

		// DELETE
		if (opCode.equals(SupportedOperations.GHN_DELETE)) {
			for (ResourceDescriptor resource : resources) {
				checker.validate(
						resource.getType().equals(AllowedResourceTypes.GHN.name()),
						new InvalidParameterException("Invalid type. GHN required"));
				new GHNManager(resource.getID()).delete(gscope);
			}
		}

		// DELETE
		if (opCode.equals(SupportedOperations.GHN_FORCE_DELETE)) {
			for (ResourceDescriptor resource : resources) {
				checker.validate(
						resource.getType().equals(AllowedResourceTypes.GHN.name()),
						new InvalidParameterException("Invalid type. GHN required"));
				new GHNManager(resource.getID()).forceDelete(gscope);
			}
		}


		/**********************************************************
		 * GENERIC RESOURCE
		 *********************************************************/
		// DELETE
		if (opCode.equals(SupportedOperations.GENERIC_RESOURCE_DELETE)) {
			for (ResourceDescriptor resource : resources) {
				checker.validate(
						resource.getType().equals(AllowedResourceTypes.GenericResource.name()),
						new InvalidParameterException("Invalid type. " + AllowedResourceTypes.GenericResource.name() + " required"));
				new GenericResourceManager(resource.getID()).delete(gscope);
			}
		}
		// DELETE
		if (opCode.equals(SupportedOperations.GENERIC_RESOURCE_FORCE_DELETE)) {
			for (ResourceDescriptor resource : resources) {
				checker.validate(
						resource.getType().equals(AllowedResourceTypes.GenericResource.name()),
						new InvalidParameterException("Invalid type. " + AllowedResourceTypes.GenericResource.name() + " required"));
				new GenericResourceManager(resource.getID()).forceDelete(gscope);
			}
		}
		
		/**********************************************************
		 * RUNTIME RESOURCE
		 *********************************************************/
		// DELETE
		if (opCode.equals(SupportedOperations.RUNTIME_RESOURCE_DELETE)) {
			System.out.println("DELETING RUNTIME RESOURCE");
			for (ResourceDescriptor resource : resources) {
				checker.validate(
						resource.getType().equals(AllowedResourceTypes.RuntimeResource.name()),
						new InvalidParameterException("Invalid type. " + AllowedResourceTypes.RuntimeResource.name() + " required"));
				new RuntimeResourceManager(resource.getID()).delete(gscope);
			}
		}
		// DELETE
		if (opCode.equals(SupportedOperations.RUNTIME_RESOURCE_FORCE_DELETE)) {
			for (ResourceDescriptor resource : resources) {
				checker.validate(
						resource.getType().equals(AllowedResourceTypes.RuntimeResource.name()),
						new InvalidParameterException("Invalid type. " + AllowedResourceTypes.RuntimeResource.name() + " required"));
				new RuntimeResourceManager(resource.getID()).forceDelete(gscope);
			}
		}

		/**********************************************************
		 * COLLECTION
		 *********************************************************/
		// DELETE
		if (opCode.equals(SupportedOperations.COLLECTION_DELETE)) {
			for (ResourceDescriptor resource : resources) {
				checker.validate(
						resource.getType().equals(AllowedResourceTypes.Collection.name()),
						new InvalidParameterException("Invalid type. " + AllowedResourceTypes.Collection.name() + " required"));
				new CollectionManager(resource.getID()).delete(gscope);
			}
		}
		// DELETE
		if (opCode.equals(SupportedOperations.COLLECTION_FORCE_DELETE)) {
			for (ResourceDescriptor resource : resources) {
				checker.validate(
						resource.getType().equals(AllowedResourceTypes.Collection.name()),
						new InvalidParameterException("Invalid type. " + AllowedResourceTypes.Collection.name() + " required"));
				new CollectionManager(resource.getID()).forceDelete(gscope);
			}
		}

		/**********************************************************
		 * VIEW
		 *********************************************************/
//		// DELETE
//		if (opCode.equals(SupportedOperations.VIEW_DELETE)) {
//			for (ResourceDescriptor resource : resources) {
//				checker.validate(
//						resource.getType().equals(AllowedResourceTypes.VIEW.name()),
//						new InvalidParameterException("Invalid type. " + AllowedResourceTypes.VIEW.name() + " required"));
//				new ViewManager(resource.getID()).delete(gscope);
//			}
//		}
//		// DELETE
//		if (opCode.equals(SupportedOperations.VIEW_FORCE_DELETE)) {
//			for (ResourceDescriptor resource : resources) {
//				checker.validate(
//						resource.getType().equals(AllowedResourceTypes.VIEW.name()),
//						new InvalidParameterException("Invalid type. " + AllowedResourceTypes.VIEW.name() + " required"));
//				new ViewManager(resource.getID()).forceDelete(gscope);
//			}
//		}

		/**********************************************************
		 * RUNNING INSTANCE
		 *********************************************************/
		if (opCode.equals(SupportedOperations.RUNNING_INSTANCE_UNDEPLOY)) {
			for (ResourceDescriptor resource : resources) {
				ServerConsole.debug(LOG_PREFIX, "Undeploying RI: " + resource.getID());
				// FIXME per massi: togli questi commenti per abilitare undeploy
				// new RunningInstanceManager(resource.getID()).undeploy(gscope);
			}
		}
	}
}
