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
 * Filename: ResourceFactory.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.resources;

import org.gcube.resourcemanagement.support.server.exceptions.ResourceOperationException;
import org.gcube.resourcemanagement.support.server.types.AllowedResourceTypes;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;

/**
 * For lazy developers here given the facilities to instantiate the proper
 * resource manager of a resource given its identifier and type.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ResourceFactory {
	private static final String LOG_PREFIX = "[RES-MGR-FACTORY]";

	public static final AbstractResourceManager createResourceManager(final AllowedResourceTypes type)
	throws ResourceOperationException {
		return createResourceManager(type, null);
	}

	/**
	 *
	 * @param type
	 * @param id if null the default constructor (empty params) will be used
	 * @return
	 * @throws ResourceOperationException
	 */
	public static final AbstractResourceManager createResourceManager(final AllowedResourceTypes type, final String id)
	throws ResourceOperationException {
		ServerConsole.info(LOG_PREFIX, "ResourceFactory building the " + type.name() + "Manager");
		String classToLoad =
			// It is supposed that the other classes are in this package
			ResourceFactory.class.getPackage().getName() + "."
			+ type.name() + "Manager";
		try {
			if (id == null) {
				return (AbstractResourceManager) Class.forName(classToLoad).newInstance();
			}
			return (AbstractResourceManager) Class.forName(classToLoad).getConstructor(String.class).newInstance(id);
		} catch (Exception e) {
			throw new ResourceOperationException(e);
		}
	}

	public static final void main(final String[] args) throws Exception {
		for (AllowedResourceTypes res : AllowedResourceTypes.values()) {
			try {
				ServerConsole.info(LOG_PREFIX,
						"Loaded " + res.name() + " with ID: " +
						ResourceFactory.createResourceManager(res, "HelloID").getID());
			} catch (Exception e) {
				ServerConsole.error(LOG_PREFIX, "[ERR] Failed to load: " + res.name());
			}
		}
	}
}
