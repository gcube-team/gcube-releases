package org.gcube.data.spd.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.data.spd.caching.QueryCacheFactory;
import org.gcube.data.spd.manager.AppInitializer;
import org.gcube.data.spd.manager.OccurrenceWriterManager;
import org.gcube.data.spd.manager.ResultItemWriterManager;
import org.gcube.data.spd.manager.TaxonomyItemWriterManager;
import org.gcube.data.spd.manager.search.Search;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Condition.Operator;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.Constants;
import org.gcube.data.spd.model.Coordinate;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.service.exceptions.QueryNotValidException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedPluginException;
import org.gcube.data.spd.model.service.types.MultiLocatorResponse;
import org.gcube.data.spd.model.service.types.PluginDescriptions;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.PluginUtils;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.Searchable;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.gcube.data.spd.utils.ResultWrapperMantainer;
import org.gcube.data.spd.utils.Utils;
import org.gcube.dataaccess.spql.ParserException;
import org.gcube.dataaccess.spql.SPQLQueryParser;
import org.gcube.dataaccess.spql.model.Query;
import org.gcube.dataaccess.spql.model.ret.ReturnType;
import org.gcube.dataaccess.spql.model.where.ParserCoordinate;
import org.gcube.dataaccess.spql.model.where.ParserDate;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.annotations.ManagedBy;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBy(AppInitializer.class)
@Path(Constants.MANAGER_PATH)
public class Manager {

	Logger logger = LoggerFactory.getLogger(Manager.class);

	AppInitializer initializer = (AppInitializer)ApplicationManagerProvider.get();

	private ApplicationContext ctx = ContextProvider.get();

	/**
	 * 
	 * @param query a SpQL query
	 * @return 
	 * @throws GCUBEFault
	 */
	@GET
	@Path("search")
	public Response search(@QueryParam("query") String query) throws QueryNotValidException, UnsupportedPluginException, UnsupportedCapabilityException {

		Query  result;
		logger.trace("submitted query is "+query);
		try{
			result = SPQLQueryParser.parse(query);
		}catch (ParserException e) {
			StringBuilder builder = new StringBuilder();
			builder.append("syntax error on query ("+query+") : ");
			for (String error : e.getErrors())
				builder.append(error).append(" ; ");
			logger.error(builder.toString());
			throw new QueryNotValidException(builder.toString());
		}

		String locator;

		try{

			boolean selectedAllSupportedPlugin = result.getDatasources().size()==0;

			Collection<AbstractPlugin> plugins=!selectedAllSupportedPlugin?PluginUtils.getPluginsSubList(result.getDatasources(), initializer.getPluginManager().plugins()):
				initializer.getPluginManager().plugins().values();

			Condition[] conditions = new Condition[0];

			if (result.getWhereExpression() != null)
				conditions= evaluateConditions(result.getWhereExpression().getConditions());

			ReturnType returnType = result.getReturnType();
			if (returnType == null) returnType = ReturnType.PRODUCT;


			logger.trace("RETUN TYPE IS {} ",returnType);

			switch (returnType) {

			case OCCURRENCE:{

				Set<AbstractPlugin> pluginsPerCapability = initializer.getPluginManager().getPluginsPerCapability(Capabilities.Occurrence, plugins);
				logger.trace("searching in plugins {} ",pluginsPerCapability);
				if (pluginsPerCapability.size()==0) throw new UnsupportedCapabilityException();

				Map<String, Searchable<OccurrencePoint>> searchableMapping = new HashMap<String, Searchable<OccurrencePoint>>();
				for (AbstractPlugin plugin: pluginsPerCapability)
					searchableMapping.put(plugin.getRepositoryName(), plugin.getOccurrencesInterface());

				ResultWrapper<OccurrencePoint> wrapper = ResultWrapperMantainer.getWrapper(OccurrencePoint.class);
				locator = wrapper.getLocator();

				Search<OccurrencePoint> search =new Search<OccurrencePoint>(wrapper, initializer.getPluginManager().plugins(), OccurrenceWriterManager.class, new QueryCacheFactory<OccurrencePoint>(ctx.configuration().persistence().location()));
				search.search(searchableMapping, result, conditions);
				break;
			}
			case PRODUCT:{
				logger.trace("searching in plugins {} ",plugins);
				Map<String, Searchable<ResultItem>> searchableMapping = new HashMap<String, Searchable<ResultItem>>();
				for (AbstractPlugin plugin: plugins)
					searchableMapping.put(plugin.getRepositoryName(), plugin);

				ResultWrapper<ResultItem> wrapper = ResultWrapperMantainer.getWrapper(ResultItem.class);
				locator = wrapper.getLocator();			
				Search<ResultItem> search = new Search<ResultItem>(wrapper,  initializer.getPluginManager().plugins(), ResultItemWriterManager.class, new QueryCacheFactory<ResultItem>(ctx.configuration().persistence().location()));
				search.search(searchableMapping, result, conditions);
				break;
			}
			case TAXON:{
				Set<AbstractPlugin> pluginsPerCapability = initializer.getPluginManager().getPluginsPerCapability(Capabilities.Classification, plugins);
				logger.trace("searching in plugins {} ",pluginsPerCapability);
				if (pluginsPerCapability.size()==0) throw new UnsupportedCapabilityException();

				Map<String, Searchable<TaxonomyItem>> searchableMapping = new HashMap<String, Searchable<TaxonomyItem>>();
				for (AbstractPlugin plugin: pluginsPerCapability)
					searchableMapping.put(plugin.getRepositoryName(), plugin.getClassificationInterface());

				ResultWrapper<TaxonomyItem> wrapper = ResultWrapperMantainer.getWrapper(TaxonomyItem.class);
				locator = wrapper.getLocator();

				Search<TaxonomyItem> search = new Search<TaxonomyItem>(wrapper, initializer.getPluginManager().plugins(), TaxonomyItemWriterManager.class, new QueryCacheFactory<TaxonomyItem>(ctx.configuration().persistence().location()));
				search.search(searchableMapping, result, conditions);		
				break;
			}
			default:
				throw new Exception("unexpected behaviour");
			}
		}catch (UnsupportedCapabilityException e) {
			logger.error("unsupported capability error",e);
			throw e;
		}catch (UnsupportedPluginException e) {
			logger.error("unsupported plugin error",e);
			throw e;
		}catch (Exception e) {
			logger.error("error submitting search",e);
			throw new RuntimeException("error submitting search", e);
		}

		// the output will be probably returned even before
		// a first chunk is written by the new thread
		StringBuilder redirectUri = new StringBuilder();
		redirectUri.append("http://").append(ctx.container().configuration().hostname()).append(":").append(ctx.container().configuration().port());
		redirectUri.append(ctx.application().getContextPath()).append(Constants.APPLICATION_ROOT_PATH).append("/").append(Constants.RESULTSET_PATH).append("/").append(locator);
		logger.trace("redirect uri is {} ",redirectUri.toString());
		try{
			MultiLocatorResponse multiLocatorResponse = new MultiLocatorResponse(locator, null, ctx.profile(GCoreEndpoint.class).id());
			return Response.temporaryRedirect(new URI(redirectUri.toString())).entity(multiLocatorResponse).build();
		}catch(Exception e){
			logger.error("invalid redirect uri created",e);
			return Response.serverError().build();
		}
	}


	private Condition[] evaluateConditions(List<org.gcube.dataaccess.spql.model.where.Condition> conditions){
		List<Condition> props= new ArrayList<Condition>();
		for (org.gcube.dataaccess.spql.model.where.Condition condition :conditions){
			switch (condition.getParameter()) {
			case EVENT_DATE:
				ParserDate parserDate = (ParserDate)condition.getValue();
				Calendar value = parserDate.getValue();
				props.add(new Condition(Conditions.DATE, value, Operator.valueOf(condition.getOperator().name())));
				break;
			case COORDINATE:
				ParserCoordinate parserCoordinate = (ParserCoordinate)condition.getValue();
				Coordinate coordinate = new Coordinate(parserCoordinate.getValue().getLatitude(), parserCoordinate.getValue().getLongitude());
				props.add(new Condition(Conditions.COORDINATE, coordinate, Operator.valueOf(condition.getOperator().name())));
				break;	
			default:
				break;
			}
		}
		return props.toArray(new Condition[props.size()]);
	}

	@GET
	@Path("providers")
	@Produces(MediaType.APPLICATION_XML)
	public PluginDescriptions getSupportedPlugins(){
		logger.trace("calling providers method");
		PluginManager pluginManager = initializer.getPluginManager();
		List<PluginDescription> descriptions = new ArrayList<PluginDescription>();
		try{
			for (AbstractPlugin plugin : pluginManager.plugins().values())
				descriptions.add(Utils.getPluginDescription(plugin));

			logger.trace("returning "+descriptions.size()+" descriptions");
		}catch(Exception e){
			logger.error("error producing descriptions", e);
		}
		return new PluginDescriptions(descriptions);
	}


}




