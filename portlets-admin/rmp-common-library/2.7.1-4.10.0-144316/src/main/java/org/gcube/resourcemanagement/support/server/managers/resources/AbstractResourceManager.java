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
 * Filename: ResourceManager.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.resources;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.informationsystem.publisher.AdvancedPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resourcemanagement.support.server.exceptions.AbstractResourceException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceAccessException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceOperationException;
import org.gcube.resourcemanagement.support.server.exceptions.ResourceParameterException;
import org.gcube.resourcemanagement.support.server.managers.report.ReportBuilder;
import org.gcube.resourcemanagement.support.server.managers.report.ReportEntry;
import org.gcube.resourcemanagement.support.server.managers.report.ReportOperation;
import org.gcube.resourcemanagement.support.server.types.AllowedResourceTypes;
import org.gcube.resourcemanagement.support.server.utils.Assertion;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.vremanagement.resourcemanager.client.RMBinderLibrary;
import org.gcube.vremanagement.resourcemanager.client.RMReportingLibrary;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.AddResourcesParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.RemoveResourcesParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.ResourceItem;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.ResourceList;
import org.gcube.vremanagement.resourcemanager.client.proxies.Proxies;

/**
 * The minimal interface all the resource managers must implement.
 * Here is implemented the greatest part of the operations exposed in the
 * <a href="https://gcube.wiki.gcube-system.org/gcube/index.php/Programmatic_Administration_Interface">
 * official wiki</a>.
 * @author Daniele Strollo (ISTI-CNR)
 * @author Massimiliano Assante (ISTI-CNR)
 */
public abstract class AbstractResourceManager {
	private String id = null;
	private String name = null;
	private AllowedResourceTypes type = null;
	private String subType = null;
	private RegistryPublisher publisher = null;
	private static final String LOG_PREFIX = "[AbstractResMgr]";

	/**
	 * @deprecated discouraged use. With no ID some operations cannot be accessed. For internal use only.
	 */
	public AbstractResourceManager(final AllowedResourceTypes type)
			throws ResourceParameterException, ResourceAccessException {
		Assertion<ResourceParameterException> checker = new Assertion<ResourceParameterException>();
		checker.validate(type != null, new ResourceParameterException("Invalid Parameter type"));

		this.type = type;

		try {
			this.publisher = RegistryPublisherFactory.create();
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
		}
	}

	/**
	 * Instantiate the handler of a resource given its identifier and its type.
	 * Notice that this constructor is implicitly invoked when instantiating the
	 * concrete resource manager (e.g. new GHNManager(id)).
	 * @param id the identifier of the resource the manage
	 * @param type the type (GHN, RI, ...).
	 * @throws ResourceParameterException
	 * @throws ResourceAccessException
	 */
	public AbstractResourceManager(String id, AllowedResourceTypes type) throws ResourceParameterException, ResourceAccessException {
		this(type);

		Assertion<ResourceParameterException> checker = new Assertion<ResourceParameterException>();
		checker.validate(id != null && id.trim().length() > 0, new ResourceParameterException("Invalid Parameter id"));

		this.id = id.trim();
	}

	public AbstractResourceManager(String id, String name, AllowedResourceTypes type) throws ResourceParameterException, ResourceAccessException {
		this(id, type);
		Assertion<ResourceParameterException> checker = new Assertion<ResourceParameterException>();
		checker.validate(name != null && name.trim().length() > 0, new ResourceParameterException("Invalid Parameter name"));
		this.name = name;
	}

	public AbstractResourceManager(String id, String name, AllowedResourceTypes type, String subtype) throws ResourceParameterException, ResourceAccessException {
		this(id, name, type);
		if (subtype != null) {
			this.subType = subtype.trim();
		}
	}

	/**
	 * The singleton ISPublisher instance is preferred.
	 * All resource managers can internally access this instance
	 * to interact with ISPublisher to handle resources.
	 * @return
	 */
	public final RegistryPublisher getRegistryPublisher() {
		return publisher;
	}

	/**
	 * All resources must be identifiable through an unique ID.
	 * <br/>
	 * <b>This field is mandatory</b>
	 * @return
	 */
	public final String getID() {
		return this.id;
	}

	public final void setID(final String id) {
		this.id = id;
	}

	/**
	 * All resources must have a valid name.
	 * </br>
	 * <b>This field is mandatory</b>
	 * @return
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * All resources have a type.
	 * </br>
	 * <b>This field is mandatory</b>
	 * @return
	 */
	public final AllowedResourceTypes getType() {
		return this.type;
	}

	/**
	 * Resources can have a subtype (e.g. for GHNs is the domain).
	 * </br>
	 * <b>This field is optional</b>
	 * @return
	 */
	public final String getSubType() {
		return this.subType;
	}

	/**
	 * The resource manager needed to handle the resource in a given scope.
	 * @param scope the scope in which to operate
	 * @return a random chosen manager from the avaiable ones
	 * @throws ResourceAccessException if no manager can be instantiated
	 * @throws ResourceParameterException if the parameters are invalid
	 */
	public final RMBinderLibrary getResourceManager(String scope)	throws AbstractResourceException {

		ScopeBean bscope = new ScopeBean(scope);
		if (bscope.is(Type.VRE)) { 
			scope = bscope.enclosingScope().toString();
		}
		ScopeProvider.instance.set(scope);
		ServerConsole.info(LOG_PREFIX, "Getting Resource Manager in scope [" + scope.toString() + "]");
		RMBinderLibrary rml = Proxies.binderService().build();
		if (rml == null) {			// no managers found
			throw new ResourceAccessException("Unable to find ResourceManagers for resource " + this.getType() + " in scope: " + scope.toString());
		}
		return rml;
	}

	/**
	 * The report resource manager needed to handle the resource in a given scope.
	 * @param scope the scope in which to operate
	 * @return a random chosen manager from the avaiable ones
	 * @throws ResourceAccessException if no manager can be instantiated
	 * @throws ResourceParameterException if the parameters are invalid
	 */
	public final RMReportingLibrary getReportResourceManager(String scope) throws AbstractResourceException {
		ScopeBean bscope = new ScopeBean(scope);
		if (bscope.is(Type.VRE)) { 
			scope = bscope.enclosingScope().toString();
		}
		ScopeProvider.instance.set(scope);

		ServerConsole.info(LOG_PREFIX, "Getting Resource Manager in scope [" + scope.toString() + "]");
		RMReportingLibrary rml = Proxies.reportingService().build();
		if (rml == null) {			// no managers found
			throw new ResourceAccessException("Unable to find ResourceManagers for resource " + this.getType() + " in scope: " + scope.toString());
		}
		return rml;
	}

	/**
	 * Once the conditions for binding a resource to a target scope are satisfied
	 * (see the
	 * <a href="https://gcube.wiki.gcube-system.org/gcube/index.php/Programmatic_Administration_Interface">
	 * official wiki</a>
	 * for scope binding rules) this method is internally called.
	 * @param targetScope
	 * @return
	 * @throws ResourceOperationException
	 */
	private String bindToScope(final String targetScope) throws AbstractResourceException {
		Assertion<AbstractResourceException> checker = new Assertion<AbstractResourceException>();
		checker.validate(targetScope != null, new ResourceParameterException("Invalid parameter targetScope. null not allowed."));
		checker.validate(this.getID() != null, new ResourceOperationException("Invalid resource ID. null not allowed."));

		ServerConsole.trace(
				LOG_PREFIX,
				"[BIND-SCOPE-ENTER] Adding " + this.getType() + " " + this.getID() + " to scope [" +
						targetScope.toString() + "]");

		System.out.println("***\n\n[BIND-SCOPE-ENTER] Adding " + this.getType() + " " + this.getID() + " to scope [" +
				targetScope.toString() + "]");


		String curr = ScopeProvider.instance.get();
		ScopeProvider.instance.set(targetScope.toString());

		AddResourcesParameters addParam = new AddResourcesParameters();
		ResourceItem toAdd = new ResourceItem();
		toAdd.setId(this.getID());
		toAdd.setType(this.getType().name());
		ResourceList r = new ResourceList();

		ArrayList<ResourceItem> temp = new ArrayList<ResourceItem>();
		temp.add(toAdd);
		r.setResource(temp);
		addParam.setResources(r);
		addParam.setTargetScope(targetScope.toString());

		RMBinderLibrary manager = this.getResourceManager(targetScope);

		try {
			ScopeBean scope = new ScopeBean(targetScope);
			if (scope.is(Type.VRE)) {
				ScopeProvider.instance.set(scope.enclosingScope().toString());
			} else
				ScopeProvider.instance.set(targetScope);
			System.out.println("***\n\nCalling manager.addResources(addParam) addParam.toString()=" + addParam.toString());

			String reportID =  manager.addResources(addParam);

			ServerConsole.trace(
					LOG_PREFIX,
					"[BIND-SCOPE-EXIT] Applyed Adding " + this.getType() + " " + this.getID() + " to scope [" +
							targetScope.toString() + "]... reportID: " + reportID);

			System.out.println("***\n\n[BIND-SCOPE-EXIT] Applyed Adding " + this.getType() + " " + this.getID() + " to scope [" +
					targetScope.toString() + "]... reportID: " + reportID);

			RMReportingLibrary pt = this.getReportResourceManager(scope.toString());

			String toReturn = pt.getReport(reportID);
			ScopeProvider.instance.set(curr);
			return toReturn;
		} catch (Exception e) {
			e.printStackTrace();
			ServerConsole.trace(
					LOG_PREFIX,
					"[BIND-SCOPE-EXIT] [FAILURE]");
			System.out.println("***[BIND-SCOPE-EXIT] [FAILURE]" +e.getMessage());
			throw new ResourceOperationException("During resource::addToScope: "+ targetScope +  " Message: \n" + e.getMessage());
		}
	}



	/**
	 * Add a scope to a Resource 
	 * @param nestingPublication true for resources different from gHN and RI.
	 * @return the reportID generated
	 */
	public final String addToExistingScope(final ScopeBean sourceScope, final ScopeBean targetScope)	throws AbstractResourceException {
		Assertion<AbstractResourceException> checker = new Assertion<AbstractResourceException>();
		checker.validate(sourceScope != null, new ResourceParameterException("Invalid parameter sourceScope. null not allowed."));
		checker.validate(targetScope != null, new ResourceParameterException("Invalid parameter targetScope. null not allowed."));
		checker.validate(this.getID() != null, new ResourceOperationException("Invalid resource ID. null not allowed."));

		String curr = ScopeProvider.instance.get();
		ScopeProvider.instance.set(sourceScope.toString());
		ReportBuilder report = new ReportBuilder();

		ServerConsole.trace(
				LOG_PREFIX,
				"[ADD-ToExistingScope] Adding from scope [" +
						sourceScope.toString() +
						"] to existing scope [" +
						targetScope.toString() +
						"] " + this.getType() + " " + this.getID());

		// If not RI or GHN and the scopes are sibling and VO copyFromToVO
		if (!(this.getType() == AllowedResourceTypes.GHN) &&
				!(this.getType() == AllowedResourceTypes.RunningInstance) &&
				sourceScope.type() == Type.VO && targetScope.type() == Type.VO) {
			return copyFromToVO(sourceScope, targetScope);
		}

		// Add a gCube Resource to
		// (i) a VRE scope from the parent VO or
		// (ii) a VO scope from the infrastructure scope
		if (!targetScope.toString().contains(sourceScope.toString())) {
			throw new ResourceOperationException(
					"You are not allowed to apply to this scope. Target scope is not enclosed in the source one.");
		}

		report.addEntry(new ReportEntry(ReportOperation.AddToScope, this,
				"Added " + this.getType() + " " + this.getID() + " to parent scope " +
						targetScope.toString() + " the remote report ID is: " +
						this.bindToScope(targetScope.toString()), true));


		String toReturn = report.getXML();
		ScopeProvider.instance.set(curr);
		return  toReturn;
	}



	/**
	 * Similar to the {@link AbstractResourceManager#addToExistingScope} method but involves
	 * two scopes of type VO.
	 * Notice that this operation in reserved for resources different from gHN and RI.
	 * See
	 * <a href="https://gcube.wiki.gcube-system.org/gcube/index.php/Programmatic_Administration_Interface#Add_a_gCube_Resource_from_a_VO_to_another_VO">
	 * here</a> for further details.
	 * @param sourceScope
	 * @param targetScope
	 * @return
	 * @throws AbstractResourceException
	 */
	public final String copyFromToVO(final ScopeBean sourceScope, final ScopeBean targetScope)	throws AbstractResourceException {
		Assertion<AbstractResourceException> checker = new Assertion<AbstractResourceException>();
		checker.validate(
				sourceScope != null && sourceScope.type() == Type.VO,
				new ResourceParameterException("The sourceScope is invalid or not of type VO."));
		checker.validate(
				targetScope != null && targetScope.type() == Type.VO,
				new ResourceParameterException("The targetScope is invalid or not of type VO."));
		checker.validate(
				sourceScope.enclosingScope() == targetScope.enclosingScope(),
				new ResourceParameterException("The sourceScope and targetScope must be children of the same root VO."));
		checker.validate(this.getType() != AllowedResourceTypes.GHN && this.getType() != AllowedResourceTypes.RunningInstance,
				new ResourceAccessException("Operation not allowed for RI and GHNs."));
		checker.validate(this.getID() != null,
				new ResourceAccessException("Operation not allowed on resources with no ID."));

		// Phase 1. retrieve the resource to copy
		Resource resStub = this.getResource(sourceScope);

		// Phase 2. Before registering the resource, the scope must be
		// bound to the local GCUBEResource
		this.bindToScope(targetScope.toString());

		// Phase 3. Register to the new VO through the ISPublisher

		String currentScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(targetScope.toString());
		Resource toReturn = null;
		try {
			toReturn = this.getRegistryPublisher().create(resStub);
		} catch (Exception e) {
			throw new ResourceAccessException(e.getMessage());
		}
		ScopeProvider.instance.set(currentScope);
		return toReturn.id();
	}

	/**
	 * Given the XML profile representation of a gcube resource, its GCUBEResource is built.
	 * Since it depends on the type of the resource, each concrete implementation of resource
	 * managers must implement it.
	 * @param xmlRepresentation
	 * @return
	 * @throws AbstractResourceException
	 */
	protected abstract Resource buildResource(final String xmlRepresentation) throws AbstractResourceException;

	/**
	 * From a resource retrieves its GCUBEResource depending on the scope in which it is
	 * published.
	 * @param scope
	 * @return
	 * @throws AbstractResourceException
	 */
	public final Resource getResource(final ScopeBean scope) throws AbstractResourceException {
		return this.buildResource(this.getXMLDescription(scope));
	}

	/**
	 * Returns the XML profile of a resource (given its ID that is encapsulated inside the resource
	 * manager).
	 * @param scope
	 * @return
	 * @throws AbstractResourceException
	 */
	protected final String getXMLDescription(final ScopeBean scope) throws AbstractResourceException {
		Assertion<AbstractResourceException> checker = new Assertion<AbstractResourceException>();
		checker.validate(this.getID() != null, new ResourceAccessException("Cannot execute on resources with no ID."));

		ScopeProvider.instance.set(scope.toString());

		// Phase 1. retrieve the resource to copy
		Query query = new QueryBox("for  $resource in  collection('/db/Profiles/" + this.getType().name() + "')//Resource " +
				"where ( $resource/ID/string() eq '" +
				this.getID() +
				"') " +
				"return $resource");

		DiscoveryClient<String> client = client();

		List<String> results = client.submit(query);
		if (results == null || results.isEmpty())
			throw new ResourceAccessException("Cannot retrieve the IS profile for resource: " + this.getID() +
					" in scope: " + scope.toString());

		return results.get(0).toString();
	}

	/**
	 * The first phase of remove from scope.
	 * This is common to all the resources (RI and GHNs).
	 * @param scope
	 * @return
	 * @throws AbstractResourceException
	 */
	private String basicRemoveFromScope(final ScopeBean scope)	throws AbstractResourceException {
		Assertion<AbstractResourceException> checker = new Assertion<AbstractResourceException>();
		checker.validate(scope != null, new ResourceParameterException("Invalid parameter scope. null not allowed."));
		checker.validate(this.getID() != null, new ResourceOperationException("Invalid ID. null not allowed."));

		ServerConsole.trace(LOG_PREFIX, "[REMOVE-FROM-SCOPE] Removing from scope [" + scope.toString() + "] " + this.getType() + " " + this.getID());

		String retval = null;

		RemoveResourcesParameters params = new RemoveResourcesParameters();
		ResourceItem toRemove = new ResourceItem();
		toRemove.setId(this.getID());
		toRemove.setType(this.getType().name());

		ResourceList resourcesToRemove = new ResourceList();
		ArrayList<ResourceItem> temp = new ArrayList<ResourceItem>();
		temp.add(toRemove);
		resourcesToRemove.setResource(temp);
		params.resources = resourcesToRemove;
		params.targetScope = scope.toString();
		ServerConsole.trace(LOG_PREFIX, "[REMOVE-FROM-SCOPE] Sending the Remove Resource request....");
		try {
			RMBinderLibrary manager = this.getResourceManager(scope.toString());
			ScopeProvider.instance.set(scope.toString());
			retval = manager.removeResources(params);
		} catch (Exception e) {
			throw new ResourceOperationException("During removeFrom scope of "
					+ this.getType()
					+ " " + this.getID() + ": " + e.getMessage());
		}
		return retval;
	}

	/**
	 * Removes the current resource from the scope.
	 * @param nestingRemoval true for resources different from gHN and RI
	 */
	public final String removeFromScope(final ScopeBean scope) throws AbstractResourceException {
		ServerConsole.trace(LOG_PREFIX, "[BASIC-FROM-SCOPE] Removing from scope [" + scope.toString() + "] " + this.getType() + " " + this.getID());
		Assertion<AbstractResourceException> checker = new Assertion<AbstractResourceException>();
		checker.validate(scope != null, new ResourceParameterException("Invalid parameter scope. null not allowed."));
		checker.validate(this.getID() != null, new ResourceOperationException("Invalid ID. null not allowed."));

		ScopeProvider.instance.set(scope.toString());
		String retval = this.basicRemoveFromScope(scope);

		return retval;
	}

	/**
	 * Adds to scopes the required maps (if needed).
	 * Internally used to ensure that the scopes at Infrastructure or VO level
	 * have correctly setup the maps.
	 * @param scopes
	 * @return
	 */
	protected List<ScopeBean> validateScopes(final String[] scopes) {
		List<ScopeBean> retval = new Vector<ScopeBean>();
		for (int i = 0; i < scopes.length; i++) {
			retval.add(new ScopeBean(scopes[i]));
		}
		return retval;
	}


	/**
	 * @deprecated you must be sure before requiring this operation... take care
	 * @param scope where the resource is bound
	 * @throws AbstractResourceException
	 */
	public final void forceDelete(ScopeBean scope) throws AbstractResourceException {
		ServerConsole.trace(LOG_PREFIX, "[FORCE DELETE] [DELETE-BRANCH] deleting resource from scope " + scope);
		String currScope = 	ScopeProvider.instance.get();	

		Resource toDelete = this.getResource(scope);

		while (scope.enclosingScope() != null)
			scope =  scope.enclosingScope();

		ScopeProvider.instance.set(scope.toString());		
		AdvancedPublisher advancedPublisher = new AdvancedPublisher(this.getRegistryPublisher());
		advancedPublisher.forceRemove(toDelete);
		ServerConsole.trace(LOG_PREFIX, "[FORCE DELETE] [DELETE-BRANCH] deleted resource from scope " + scope);
		ScopeProvider.instance.set(currScope);		
	}

	/**
	 * Removes a resource.
	 * According to the
	 * <a href="https://gcube.wiki.gcube-system.org/gcube/index.php/Programmatic_Administration_Interface">
	 * official wiki</a> the resource is deleted only if is not bound in other scopes. Otherwise this
	 * operation simply corresponds to remove from scope.
	 * @param scope
	 * @throws ResourceOperationException
	 */
	public final String delete(final ScopeBean scope) throws AbstractResourceException {
		Assertion<AbstractResourceException> checker = new Assertion<AbstractResourceException>();
		checker.validate(scope != null, new ResourceParameterException("Invalid parameter scope. null not allowed."));
		checker.validate(this.getID() != null, new ResourceOperationException("Invalid ID. null not allowed."));

		System.out.println("DELETING TYPE: "+ this.getType());
		String currScope = 	ScopeProvider.instance.get();		
		ScopeProvider.instance.set(scope.toString());			
		Resource resStub = this.getResource(scope);

		List<ScopeBean> boundedScopes = this.validateScopes(resStub.scopes().toArray(new String[0]));

		ServerConsole.trace(LOG_PREFIX, "[DELETE] " + this.getType() + " " + this.getID() + " in scope [" + scope + "]");

		ServerConsole.trace(LOG_PREFIX, "[DELETE] " + this.getType() + " " + this.getID() + " is bound to (" + boundedScopes.size() + ") scopes");
		String toReturn = "";
		if (boundedScopes.size() > 1) {
			ServerConsole.trace(LOG_PREFIX, "[DELETE] [DELETE-BRANCH] deleting resource is a remove from scope since more than 1 scope is present " + resStub.id());
			toReturn = this.basicRemoveFromScope(scope);
		}
		else if (boundedScopes.size() == 1) {
			ServerConsole.trace(LOG_PREFIX, "[DELETE] [DELETE-BRANCH] deleting resource since is bound to 1 scope only " + resStub.id());
			try {
				this.getRegistryPublisher().remove(resStub); 
				toReturn = "NO REPORT because with only one resource the RegistryPublisher().remove was called";
			} catch (Exception e) {
				ScopeProvider.instance.set(currScope);		
			}
		}
		ScopeProvider.instance.set(currScope);
		return toReturn;
	}

}
