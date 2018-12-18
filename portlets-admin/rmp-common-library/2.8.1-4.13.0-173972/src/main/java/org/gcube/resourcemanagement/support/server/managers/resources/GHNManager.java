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
 * Filename: GHNManager.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.resources;

import java.io.StringReader;

import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.vremanagement.ghnmanager.client.GHNManagerLibrary;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.AddScopeInputParams;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.ShutdownOptions;
import org.gcube.common.vremanagement.ghnmanager.client.proxies.Proxies;
import org.gcube.resourcemanagement.support.server.exceptions.AbstractResourceException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceAccessException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceOperationException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceParameterException;
import org.gcube.resourcemanagement.support.server.types.AllowedResourceTypes;
import org.gcube.resourcemanagement.support.server.utils.Assertion;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;


/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class GHNManager extends AbstractResourceManager {

	private static final String LOG_PREFIX = "[GHN-MGR]";


	/**
	 * @deprecated discouraged use. With no ID some operations cannot be accessed.
	 */
	public GHNManager()
	throws ResourceParameterException, ResourceAccessException {
		super(AllowedResourceTypes.GHN);
	}

	/**
	 * @param id the identifier of wrapper resource.
	 * @throws ResourceParameterException
	 * @throws ResourceAccessException
	 */
	public GHNManager(final String id)
	throws ResourceParameterException, ResourceAccessException {
		super(id, AllowedResourceTypes.GHN);
	}

	/**
	 * @param id
	 * @param name
	 * @throws ResourceParameterException
	 * @throws ResourceAccessException
	 */
	public GHNManager(final String id, final String name)
	throws ResourceParameterException, ResourceAccessException {
		super(id, name, AllowedResourceTypes.GHN);
	}

	/**
	 * @param id
	 * @param name
	 * @param subtype
	 * @throws ResourceParameterException
	 * @throws ResourceAccessException
	 */
	public GHNManager(final String id, final String name, final String subtype)
	throws ResourceParameterException, ResourceAccessException {
		super(id, name, AllowedResourceTypes.GHN, subtype);
	}

	/**
	 * Add a scope to a gHN and the related Service Map that is not available on the gHN.
	 * <p>
	 * <b>Required information:</b>
	 * <br/>
	 * The <i>ID</i> of the resource must be specified and valid.</p>
	 * @param scope
	 * @param scopeMap
	 * @return the generated reportID
	 * @throws ResourceParameterException
	 * @throws ResourceOperationException
	 */
	public final String addToNewScope(final ScopeBean sourceScope, final ScopeBean targetScope,	final String scopeMap)
	throws AbstractResourceException {
		Assertion<AbstractResourceException> checker = new Assertion<AbstractResourceException>();
		checker.validate(sourceScope != null, new ResourceParameterException("Parameter sourceScope null not allowed."));
		checker.validate(targetScope != null, new ResourceParameterException("Parameter targetScope null not allowed."));
		checker.validate(scopeMap != null && scopeMap.trim().length() > 0,  new ResourceParameterException("Invalid scopeMap parameter."));
		checker.validate(this.getID() != null,  new ResourceOperationException("This operation cannot be applied to resources with no ID."));

		if (!sourceScope.toString().contains(targetScope.toString())) {
			throw new ResourceOperationException(
			"You are not allowed to apply to this scope. Target scope is not enclosed in the source one.");
		}
		ServerConsole.trace(
				LOG_PREFIX,
				"Adding from scope " +
				sourceScope.toString() +
				"Adding to existing scope " +
				targetScope.toString() +
				" " + this.getType() + " " + this.getID());

		AddScopeInputParams params = new AddScopeInputParams();
		params.setScope(sourceScope.toString());
		params.setMap(scopeMap.trim()); //eventually, set here the new Service Map
		try {
			this.getGHNManager(sourceScope).addScope(params);
		} catch (Exception e) {
			throw new ResourceOperationException(
					"Failed to add the new scope to the gHN " + this.getID() + ": " + e.getMessage());
		}
		return this.addToExistingScope(sourceScope, targetScope);
	}
	
	/**
	 * <p>
	 * <b>Required information:</b>
	 * <br/>
	 * The <i>name</i> of the resource must be specified and valid. It is used to retrieve the GHN manager URL.</p>
	 * @param scope the scope in which the manager is bound.
	 * @return
	 * @throws AbstractResourceException
	 */
	public final GHNManagerLibrary getGHNManager(final ScopeBean scope) throws AbstractResourceException {
		Assertion<AbstractResourceException> checker = new Assertion<AbstractResourceException>();
		checker.validate(scope != null, new ResourceParameterException("Invalid scope"));
		checker.validate(this.getName() != null,  new ResourceOperationException("This operation cannot be applied to resources with no name."));
		
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope.toString());
		ServerConsole.info(LOG_PREFIX, "Getting Resource Manager in scope [" + scope.toString() + "]");
		GHNManagerLibrary ghnMan = Proxies.service().build();
		if (ghnMan == null) {			// no managers found
			throw new ResourceAccessException("Unable to find GHNManagers for resource " + this.getType() + " in scope: " + scope.toString());
		}
		ScopeProvider.instance.set(currScope);
		return ghnMan;
	}

	/**
	 * Implements all the three possible shutdown policies according to
	 * restart and clean parameters.
	 * <p>
	 * <b>Required information:</b>
	 * <br/>
	 * The <i>ID</i> of the resource must be specified and valid.</p>
	 * @param scope
	 * @param restart
	 * @param clean
	 * @throws ResourceOperationException
	 * @throws ResourceAccessException
	 * @throws ResourceParameterException
	 */
	public final void shutDown(final ScopeBean scope, final boolean restart, final boolean clean)
	throws AbstractResourceException {
		Assertion<ResourceAccessException> checker = new Assertion<ResourceAccessException>();
		checker.validate(this.getID() != null,  new ResourceAccessException("This operation cannot be applied to resources with no ID."));

		GHNManagerLibrary ghnManager = this.getGHNManager(scope);

		ServerConsole.trace(LOG_PREFIX, "Shutting down " + scope.toString() + " " + this.getType() + " " + this.getID());

		ShutdownOptions options = new ShutdownOptions();
		options.setRestart(restart);
		options.setClean(clean);
		try {
			ghnManager.shutdown(options);
		} catch (Exception e) {
			throw new ResourceOperationException("Cannot shutdown ghn: " + this.getID());
		}
	}

	@Override
	protected final Resource buildResource(final String xmlRepresentation) throws AbstractResourceException {
		try {
			HostingNode deserialised = Resources.unmarshal(HostingNode.class, new StringReader(xmlRepresentation));
			return deserialised;
		} catch (Exception e) {
			throw new ResourceAccessException("Cannot load the stub for resource " + this.getType(), e);
		}
	}

}
