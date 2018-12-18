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
 * Filename: RuntimeResourceManager.java
 ****************************************************************************
 * @author <a href="mailto:assante@isti.cnr.it">Massimiliano Assante</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.resources;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resourcemanagement.support.server.exceptions.AbstractResourceException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceAccessException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceOperationException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceParameterException;
import org.gcube.resourcemanagement.support.server.types.AllowedResourceTypes;
import org.gcube.resourcemanagement.support.server.utils.Assertion;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;
import org.gcube.vremanagement.resourcemanager.client.RMReportingLibrary;



/**
 * @author Massimiliano Assante (ISTI-CNR)
 *
 */
public class RuntimeResourceManager extends AbstractResourceManager {
	// Used internally to RuntimeResourceManager static functionalities (e.g. deploy).
	private static RuntimeResourceManager singleton = null;
	private static final String LOG_PREFIX = "[RR-MGR]";
	
	static {
		if (RuntimeResourceManager.singleton == null) {
			try {
				RuntimeResourceManager.singleton = new RuntimeResourceManager("dummyservice", "dummyservice");
			} catch (Exception e) {
				ServerConsole.error(LOG_PREFIX, e);
			}
		}
	}

	/**
	 * @deprecated discouraged use. With no ID some operations cannot be accessed.
	 */
	public RuntimeResourceManager()
	throws ResourceParameterException, ResourceAccessException {
		super(AllowedResourceTypes.RuntimeResource);
	}

	public RuntimeResourceManager(final String id)
	throws ResourceParameterException, ResourceAccessException {
		super(id, AllowedResourceTypes.RuntimeResource);
	}

	public RuntimeResourceManager(final String id, final String name)
	throws ResourceParameterException, ResourceAccessException {
		super(id, name, AllowedResourceTypes.RuntimeResource);
	}

	public RuntimeResourceManager(final String id, final String name, final String subType)
	throws ResourceParameterException, ResourceAccessException {
		super(id, name, AllowedResourceTypes.RuntimeResource, subType);
	}


	public final String checkDeployStatus(final ScopeBean scope, final String deployID)
	throws AbstractResourceException {
		Assertion<ResourceParameterException> checker = new Assertion<ResourceParameterException>();
		checker.validate(scope != null,
				new ResourceParameterException("Invalid scope passed"));
		checker.validate(deployID != null && deployID.trim().length() > 0,
				new ResourceParameterException("Invalid reportID passed"));

		RMReportingLibrary vreManagerPortType = this.getReportResourceManager(scope.name());

		try {
			return vreManagerPortType.getReport(deployID);
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
			throw new ResourceOperationException("Cannot retrieve the report: " + deployID + " " + e.getMessage());
		}
	}

	@Override
	protected final Resource buildResource(final String xmlRepresentation) throws AbstractResourceException {
		try {
			JAXBContext ctx = JAXBContext.newInstance(ServiceEndpoint.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			StringReader reader = new StringReader(xmlRepresentation);
			ServiceEndpoint deserialised = (ServiceEndpoint) unmarshaller.unmarshal(reader);
			return deserialised;
		} catch (Exception e) {
			throw new ResourceAccessException("Cannot load the stub for resource " + this.getType(), e);
		}			

	}
}
