package org.gcube.data.access.storagehub.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.WebServiceException;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.expressions.Expression;
import org.gcube.common.storagehub.model.expressions.logical.And;
import org.gcube.common.storagehub.model.expressions.logical.ISDescendant;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.service.ItemList;
import org.gcube.common.storagehub.model.service.ItemWrapper;
import org.gcube.data.access.storagehub.AuthorizationChecker;
import org.gcube.data.access.storagehub.Constants;
import org.gcube.data.access.storagehub.Range;
import org.gcube.data.access.storagehub.Utils;
import org.gcube.data.access.storagehub.handlers.CredentialHandler;
import org.gcube.data.access.storagehub.handlers.ItemHandler;
import org.gcube.data.access.storagehub.handlers.VRE;
import org.gcube.data.access.storagehub.handlers.VREManager;
import org.gcube.data.access.storagehub.query.sql2.evaluators.Evaluators;
import org.gcube.smartgears.utils.InnerMethodName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;


@Path("")
public class WorkspaceManager {

	private static final Logger log = LoggerFactory.getLogger(WorkspaceManager.class);

	@Inject 
	RepositoryInitializer repository;

	@Inject
	Evaluators evaluator;

	@Inject
	AuthorizationChecker authChecker;

	
	@Inject
	ServletContext context;
	
	@Inject
	VREManager vreManager;
	
	@RequestScoped
	@QueryParam("exclude") 
	private List<String> excludes = Collections.emptyList();
	

	@Path("")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getWorkspace(@QueryParam("relPath") String relPath){
		InnerMethodName.instance.set("getWorkspace");
		Session ses = null;
		org.gcube.common.storagehub.model.Path absolutePath;
		if (relPath==null)
			absolutePath = Utils.getHomePath();
		else 	absolutePath = Paths.append(Utils.getHomePath(), relPath);

		Item toReturn = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			long start = System.currentTimeMillis();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			log.trace("time to connect to repo  {}",(System.currentTimeMillis()-start));
			Node node = ses.getNode(absolutePath.toPath());
			authChecker.checkReadAuthorizationControl(ses, node.getIdentifier());
			toReturn = ItemHandler.getItem(node, excludes);
		}catch(Throwable e){
			log.error("error reading the node children of {}",absolutePath,e);
			throw new WebApplicationException("error getting WS folder "+absolutePath.toPath(),e)  ;
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemWrapper<Item>(toReturn);
	}

	private synchronized VRE getVreFolderItem(Session ses) throws Exception{
		org.gcube.common.storagehub.model.Path vrePath = Paths.append(Utils.getHomePath(), Constants.VRE_FOLDER_PARENT_NAME);
		ScopeBean bean = new ScopeBean(ScopeProvider.instance.get());
		if (!bean.is(Type.VRE)) throw new Exception("the current scope is not a VRE");
		String entireScopeName= bean.toString().replaceAll("^/(.*)/?$", "$1").replaceAll("/", "-");
		VRE vre = vreManager.getVRE(entireScopeName);
		if (vre!=null) return vre;
		else {
			String query = String.format("SELECT * FROM [nthl:workspaceItem] As node WHERE node.[jcr:title] like '%s' AND ISDESCENDANTNODE('%s')",entireScopeName, vrePath.toPath());
			Query jcrQuery = ses.getWorkspace().getQueryManager().createQuery(query, Constants.QUERY_LANGUAGE);
			NodeIterator it =  jcrQuery.execute().getNodes();

			if (!it.hasNext()) throw new Exception("vre folder not found for context "+entireScopeName);

			Node folder = it.nextNode();
			Item vreFolder = ItemHandler.getItem(folder, excludes);
			return vreManager.putVRE(vreFolder);
		}	

	}


	@Path("vrefolder")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getVreRootFolder(){
		InnerMethodName.instance.set("getVreRootFolder");
		Session ses = null;
		try {
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			return new ItemWrapper<Item>(getVreFolderItem(ses).getVreFolder());
		}catch(Throwable e){
			log.error("error reading vreNode for context {}",ScopeProvider.instance.get(),e);
			throw new WebApplicationException("error retrieving vre folder",e);
		}finally{
			if (ses!=null)
				ses.logout();
		}	
	}

	@Path("vrefolder/recents")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList getVreFolderRecentsDocument(){
		InnerMethodName.instance.set("getVreFolderRecents");
		Session ses = null;

		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			
			VRE vre = getVreFolderItem(ses);
			log.trace("VRE retrieved {}",vre.getVreFolder().getTitle());
			List<Item> recentItems = vre.getRecents();
			log.trace("recents retrieved {}",vre.getVreFolder().getTitle());
			return new ItemList(recentItems);
		}catch(Throwable e){
			log.error("error reading recents for context {}",ScopeProvider.instance.get(),e);
			throw new WebApplicationException("error reading recents",e);
		}finally{
			if (ses!=null)
				ses.logout();
		}
	}


	@Path("trash")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemWrapper<Item> getTrashRootFolder(){
		InnerMethodName.instance.set("getTrashRootFolder");
		Session ses = null;

		org.gcube.common.storagehub.model.Path trashPath = Paths.append(Utils.getHomePath(), Constants.TRASH_ROOT_FOLDER_NAME);

		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			long start = System.currentTimeMillis();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			log.info("time to connect to repo  {}",(System.currentTimeMillis()-start));



			Node folder = ses.getNode(trashPath.toPath());
			Item item = ItemHandler.getItem(folder, excludes);

			return new ItemWrapper<Item>(item);
		}catch(Throwable e){
			log.error("error reading the node {}",trashPath,e);
			throw new WebApplicationException("error retrieving trash folder",e);
		}finally{
			if (ses!=null)
				ses.logout();
		}

	}

	@Path("vrefolders")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList getVreFolders(){
		InnerMethodName.instance.set("getVreFolders");
		Session ses = null;

		org.gcube.common.storagehub.model.Path vrePath = Paths.append(Utils.getHomePath(), Constants.VRE_FOLDER_PARENT_NAME);
		List<? extends Item> toReturn = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			toReturn = Utils.getItemList(ses.getNode(vrePath.toPath()) , excludes, null, false);
		}catch(Throwable e){
			log.error("error reading the node children of {}",vrePath,e);
			throw new WebApplicationException("error reading the node children of "+vrePath.toPath(),e);
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}

	@Path("vrefolders/paged")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList getVreFoldersPaged(@QueryParam("start") Integer start, @QueryParam("limit") Integer limit){
		InnerMethodName.instance.set("getVreFoldersPaged");
		Session ses = null;

		org.gcube.common.storagehub.model.Path vrePath = Paths.append(Utils.getHomePath(), Constants.VRE_FOLDER_PARENT_NAME);
		List<? extends Item> toReturn = null;
		try{
			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			toReturn = Utils.getItemList(ses.getNode(vrePath.toPath()) , excludes, new Range(start, limit), false);
		}catch(Throwable e){
			log.error("(paged) error reading the node children of {}",vrePath,e);
			throw new WebApplicationException("error reading the node children of "+vrePath.toPath(),e);
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}



	@Path("query")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ItemList searchItems(@QueryParam("n") String node, @QueryParam("e") String jsonExpr, @QueryParam("o") List<String> orderField, @QueryParam("l") Integer limit, @QueryParam("f") Integer offset){
		InnerMethodName.instance.set("searchItems");
		Session ses = null;
		List<? extends Item> toReturn = new ArrayList<>();

		try{

			ObjectMapper mapper = new ObjectMapper();
			Expression<Boolean> expression = mapper.readValue(jsonExpr, Expression.class);
			String stringExpression = evaluator.evaluate(new And(new ISDescendant(Utils.getHomePath()), expression));
			//ADD ALSO LIMIT AND OFFSET

			String orderBy = "";
			if (orderField!=null && orderField.size()>0)
				orderBy= String.format("ORDER BY %s", orderField.stream().collect(Collectors.joining(",")).toString());


			String sql2Query = String.format("SELECT * FROM [%s] AS node WHERE %s %s ",node, stringExpression,orderBy);

			log.info("query sent is {}",sql2Query);


			String login = AuthorizationProvider.instance.get().getClient().getId();
			ses = repository.getRepository().login(CredentialHandler.getAdminCredentials(context));
			Query jcrQuery = ses.getWorkspace().getQueryManager().createQuery(sql2Query, Constants.QUERY_LANGUAGE);

			if (limit!=null && limit!=-1 )
				jcrQuery.setLimit(limit);

			if (offset!=null && offset!=-1 )
				jcrQuery.setOffset(offset);

			QueryResult result = jcrQuery.execute();

			NodeIterator it = result.getNodes();

			while (it.hasNext())
				toReturn.add(ItemHandler.getItem(it.nextNode(), null));			
			
						
		}catch(Throwable e){
			log.error("error executing the query",e);
			throw new WebServiceException("error executing the query", e);
		}finally{
			if (ses!=null)
				ses.logout();
		}

		return new ItemList(toReturn);
	}

}
