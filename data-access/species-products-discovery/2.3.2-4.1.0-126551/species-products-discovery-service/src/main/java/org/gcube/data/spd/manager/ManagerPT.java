package org.gcube.data.spd.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.types.VOID;
import org.gcube.data.spd.context.ServiceContext;
import org.gcube.data.spd.manager.search.Search;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Condition.Operator;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.Coordinate;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.PluginUtils;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.Searchable;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.AbstractWrapper;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.gcube.data.spd.stubs.GetSupportedPluginsResponse;
import org.gcube.data.spd.stubs.QueryNotValidFault;
import org.gcube.data.spd.stubs.UnsupportedCapabilityFault;
import org.gcube.data.spd.stubs.UnsupportedPluginFault;
import org.gcube.dataaccess.spql.ParserException;
import org.gcube.dataaccess.spql.SPQLQueryParser;
import org.gcube.dataaccess.spql.model.Query;
import org.gcube.dataaccess.spql.model.error.QueryError;
import org.gcube.dataaccess.spql.model.ret.ReturnType;
import org.gcube.dataaccess.spql.model.where.ParserCoordinate;
import org.gcube.dataaccess.spql.model.where.ParserDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.thoughtworks.xstream.XStream;


public class ManagerPT extends GCUBEPortType {
	
	Logger logger = LoggerFactory.getLogger(ManagerPT.class);

	/**{@inheritDoc}*/
	@Override	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}


	/**
	 * 
	 * @param query a SpQL query
	 * @return a gRS locator
	 * @throws GCUBEFault
	 */
	public <T extends ResultElement> String search(String query) throws UnsupportedPluginFault, QueryNotValidFault, UnsupportedCapabilityFault, GCUBEFault {
		Query  result;
		logger.trace("submitted query is "+query);
		try{
			result = SPQLQueryParser.parse(query);
		}catch (ParserException e) {
			StringBuilder builder = new StringBuilder();
			builder.append("syntax error on query ("+query+") : ");
			for (QueryError error : e.getErrors())
				builder.append(error.getErrorMessage());
			logger.error(builder.toString());
			throw new QueryNotValidFault(builder.toString());
		}
				
		String locator;
		try{
			
			boolean selectedAllSupportedPlugin = result.getDatasources().size()==0;
			
			Collection<AbstractPlugin> plugins=!selectedAllSupportedPlugin?PluginUtils.getPluginsSubList(result.getDatasources(), PluginManager.get().plugins()):
				PluginManager.get().plugins().values();
									
			Condition[] conditions = new Condition[0];
			
			if (result.getWhereExpression() != null)
				conditions= evaluateConditions(result.getWhereExpression().getConditions());
			
			
			ReturnType returnType = result.getReturnType();
			if (returnType == null) returnType = ReturnType.PRODUCT;
			
			switch (returnType) {

			case OCCURRENCE:{
				Set<AbstractPlugin> pluginsPerCapability = PluginManager.get().getPluginsPerCapability(Capabilities.Occurrence, plugins);
				if (pluginsPerCapability.size()==0) throw new UnsupportedCapabilityFault();
				
				Map<String, Searchable<OccurrencePoint>> searchableMapping = new HashMap<String, Searchable<OccurrencePoint>>();
				for (AbstractPlugin plugin: pluginsPerCapability)
					searchableMapping.put(plugin.getRepositoryName(), plugin.getOccurrencesInterface());
				
				AbstractWrapper<OccurrencePoint> wrapper = new ResultWrapper<OccurrencePoint>();
				new Search<OccurrencePoint>(wrapper, ServiceContext.getContext().getSearchThreadPool(), ServiceContext.getContext().getCacheManager(), OccurrenceWriterManager.class).search(searchableMapping, result, conditions);
				locator = wrapper.getLocator();
				break;
			}
			case PRODUCT:{
				Map<String, Searchable<ResultItem>> searchableMapping = new HashMap<String, Searchable<ResultItem>>();
				for (AbstractPlugin plugin: plugins)
					searchableMapping.put(plugin.getRepositoryName(), plugin);
				AbstractWrapper<ResultItem> wrapper = new ResultWrapper<ResultItem>();
				new Search<ResultItem>(wrapper, ServiceContext.getContext().getSearchThreadPool(), ServiceContext.getContext().getCacheManager(),ResultItemWriterManager.class).search(searchableMapping, result, conditions);
				locator = wrapper.getLocator();
				break;
			}
			case TAXON:{
				Set<AbstractPlugin> pluginsPerCapability = PluginManager.get().getPluginsPerCapability(Capabilities.Classification, plugins);
				if (pluginsPerCapability.size()==0) throw new UnsupportedCapabilityFault();
				
				Map<String, Searchable<TaxonomyItem>> searchableMapping = new HashMap<String, Searchable<TaxonomyItem>>();
				for (AbstractPlugin plugin: pluginsPerCapability)
						searchableMapping.put(plugin.getRepositoryName(), plugin.getClassificationInterface());
				
				AbstractWrapper<TaxonomyItem> wrapper = new ResultWrapper<TaxonomyItem>();
				new Search<TaxonomyItem>(wrapper, ServiceContext.getContext().getSearchThreadPool(), ServiceContext.getContext().getCacheManager(),TaxonomyItemWriterManager.class).search(searchableMapping, result, conditions);
				locator = wrapper.getLocator();
				break;
			}
			default:
				throw new Exception("unexpected behaviour");
			}
		}catch (UnsupportedCapabilityFault e) {
			logger.error("unsupported capability error",e);
			throw e;
		}catch (UnsupportedPluginFault e) {
			logger.error("unsupported plugin error",e);
			throw e;
		}catch (Exception e) {
			logger.error("error submitting search",e);
			throw new GCUBEFault(e,"error submitting search");
		}
		logger.trace("returning locator "+locator);
		return locator;
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

	
	public GetSupportedPluginsResponse getSupportedPlugins(VOID request){
		PluginManager pluginManager = PluginManager.get();
		String[] descriptions = new String[pluginManager.plugins().values().size()];
		int i=0;
		for (AbstractPlugin plugin : pluginManager.plugins().values())
			descriptions[i++]=new XStream().toXML(Manager.getPluginDescription(plugin));

		logger.trace("returning "+descriptions.length+" descriptions");
		return new GetSupportedPluginsResponse(descriptions);
	}


}




