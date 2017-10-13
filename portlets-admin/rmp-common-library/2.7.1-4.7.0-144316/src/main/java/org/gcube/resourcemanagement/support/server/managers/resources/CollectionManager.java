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
 * Filename: CollectionManager.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.resources;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.resourcemanagement.support.server.exceptions.AbstractResourceException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceAccessException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceParameterException;
import org.gcube.resourcemanagement.support.server.types.AllowedResourceTypes;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class CollectionManager extends AbstractResourceManager {
	/**
	 * @deprecated discouraged use. With no ID some operations cannot be accessed.
	 */
	public CollectionManager()
	throws ResourceParameterException, ResourceAccessException {
		super(AllowedResourceTypes.Collection);
	}

	public CollectionManager(final String id)
	throws ResourceParameterException, ResourceAccessException {
		super(id, AllowedResourceTypes.Collection);
	}

	/**
	 * @param id
	 * @param name
	 * @throws ResourceParameterException
	 * @throws ResourceAccessException
	 */
	public CollectionManager(final String id, final String name)
	throws ResourceParameterException, ResourceAccessException {
		super(id, name, AllowedResourceTypes.Collection);
	}

	/**
	 * @param id
	 * @param name
	 * @param subtype
	 * @throws ResourceParameterException
	 * @throws ResourceAccessException
	 */
	public CollectionManager(final String id, final String name, final String subtype)
	throws ResourceParameterException, ResourceAccessException {
		super(id, name, AllowedResourceTypes.Collection, subtype);
	}

	

	@Override
	protected Resource buildResource(String xmlRepresentation)
			throws AbstractResourceException {
		// TODO Auto-generated method stub
		return null;
	}

}
