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
 * Filename: ServiceManager.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.resources;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Software;
import org.gcube.resourcemanagement.support.server.exceptions.AbstractResourceException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceAccessException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceParameterException;
import org.gcube.resourcemanagement.support.server.types.AllowedResourceTypes;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ServiceManager extends AbstractResourceManager {

	/**
	 * @deprecated discouraged use. With no ID some operations cannot be accessed.
	 */
	public ServiceManager() throws ResourceParameterException, ResourceAccessException {
		super(AllowedResourceTypes.Service);
	}

	public ServiceManager(final String id)
	throws ResourceParameterException, ResourceAccessException {
		super(id, AllowedResourceTypes.Service);
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @throws ResourceParameterException
	 * @throws ResourceAccessException
	 */
	public ServiceManager(final String id, final String name)
	throws ResourceParameterException, ResourceAccessException {
		super(id, name, AllowedResourceTypes.Service);
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param subtype
	 * @throws ResourceParameterException
	 * @throws ResourceAccessException
	 */
	public ServiceManager(final String id, final String name, final String subtype)
	throws ResourceParameterException,
	ResourceAccessException {
		super(id, name, AllowedResourceTypes.Service, subtype);
	}

	@Override
	protected final Resource buildResource(final String xmlRepresentation) throws AbstractResourceException {
		try {
			JAXBContext ctx = JAXBContext.newInstance(Software.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			StringReader reader = new StringReader(xmlRepresentation);
			Software deserialised = (Software) unmarshaller.unmarshal(reader);
			return deserialised;
		} catch (Exception e) {
			throw new ResourceAccessException("Cannot load the stub for resource " + this.getType(), e);
		}			

	}
}
