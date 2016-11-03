package org.gcube.data.spd.classification;

import static org.gcube.data.streams.dsl.Streams.convert;
import static org.gcube.data.streams.dsl.Streams.pipe;
import static org.gcube.data.streams.dsl.Streams.publish;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.types.VOID;
import org.gcube.common.scope.impl.ScopedTasks;
import org.gcube.data.spd.context.ServiceContext;
import org.gcube.data.spd.exception.MaxRetriesReachedException;
import org.gcube.data.spd.manager.TaxonomyItemWriterManager;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.MethodNotSupportedException;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.exceptions.StreamNonBlockingException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.gcube.data.spd.stubs.ClassificationPortType;
import org.gcube.data.spd.stubs.IdNotValidFault;
import org.gcube.data.spd.stubs.UnsupportedCapabilityFault;
import org.gcube.data.spd.stubs.UnsupportedPluginFault;
import org.gcube.data.spd.utils.JobRetryCall;
import org.gcube.data.spd.utils.QueryRetryCall;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.delegates.PipedStream;
import org.gcube.data.streams.publishers.RecordFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassificationPT extends GCUBEPortType implements ClassificationPortType{

	private static Logger logger = LoggerFactory.getLogger(ClassificationPT.class);

	/**{@inheritDoc}*/
	@Override	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	@Override
	public String retrieveTaxonChildrenByTaxonId(String key) throws IdNotValidFault, UnsupportedPluginFault, UnsupportedCapabilityFault, GCUBEFault {
		try{
			logger.trace("calling get taxon childs by id");
			PluginManager manager = PluginManager.get();
			String pluginName = Util.getProviderFromKey(key);
			String id = Util.getIdFromKey(key);
			if (!manager.plugins().containsKey(pluginName))
				throw new UnsupportedPluginFault();
			AbstractPlugin plugin = manager.plugins().get(pluginName);	
			if (!plugin.getSupportedCapabilities().contains(Capabilities.Classification)) throw new UnsupportedCapabilityFault();
			try {
				logger.trace("retirievng list of taxon item");
				List<TaxonomyItem> taxonChilds = plugin.getClassificationInterface().retrieveTaxonChildrenByTaxonId(id);
				logger.trace("taxon item found are "+taxonChilds.size());
				Stream<TaxonomyItem> taxonStream =convert(taxonChilds);
				PipedStream<TaxonomyItem, String> pipedTaxa = pipe(taxonStream).through(new TaxonomyItemWriterManager(plugin.getRepositoryName()));
				return publish(pipedTaxa).using(new RecordFactory<String>() {
					
					@Override
					public Record newRecord(String element) throws 
							RuntimeException {
						GenericRecord gr=new GenericRecord();
						gr.setFields(new StringField[]{new StringField(element)});
						return gr;
					}
					
					@Override
					public RecordDefinition[] definitions() {
						StringFieldDefinition fieldDefinition = new StringFieldDefinition("result");
						return new RecordDefinition[]{          //A gRS can contain a number of different record definitions
						        new GenericRecordDefinition((new FieldDefinition[] { //A record can contain a number of different field definitions
						        		fieldDefinition				        //The definition of the field
						      }))
						    };
					}
				}).withDefaults().toString();

			} catch (IdNotValidException e) {
				logger.error("the id "+id+" is not valid",e );
				throw new IdNotValidFault();
			}
		}catch (Throwable e) {
			logger.error("error getting TaxonByid",e);
			throw new GCUBEFault(e);
		}
	}

	@Override
	public String retrieveChildrenTreeById(final String key) throws  IdNotValidFault, UnsupportedPluginFault, UnsupportedCapabilityFault, GCUBEFault{
		PluginManager manager = PluginManager.get();

		try{
			String pluginName = Util.getProviderFromKey(key);
			final String id = Util.getIdFromKey(key);
			if (!manager.plugins().containsKey(pluginName))
				throw new UnsupportedPluginFault();
			final AbstractPlugin plugin = manager.plugins().get(pluginName);	
			if (!plugin.getSupportedCapabilities().contains(Capabilities.Classification)) throw new UnsupportedCapabilityFault();

			final ResultWrapper<TaxonomyItem> wrapper = new ResultWrapper<TaxonomyItem>();
			final TaxonomyItem taxon= plugin.getClassificationInterface().retrieveTaxonById(id);
			ServiceContext.getContext().getSearchThreadPool().execute(new Runnable() {
				@Override
				public void run(){
					Writer<TaxonomyItem> writer  = new Writer<TaxonomyItem>(wrapper,  new TaxonomyItemWriterManager(plugin.getRepositoryName()));
					writer.register();
					ClassificationPT.retrieveTaxaTree(writer, taxon, plugin);
					writer.close();
				}
			});
			return wrapper.getLocator();
		}catch (IdNotValidException e) {
			logger.error("error retrieving children tree by id",e);
			throw new IdNotValidFault();
		}catch (GCUBEFault fault) {
			logger.error("error retrieving children tree by id",fault);
			throw fault;
		}catch (Exception e1) {
			logger.error("error retrieving children tree by id",e1);
			throw new GCUBEFault(e1);
		}

		
	}
	
	@Override
	public String retrieveSynonymsById(String key) throws IdNotValidFault, UnsupportedPluginFault, UnsupportedCapabilityFault, GCUBEFault{
		try{
			PluginManager manager = PluginManager.get();
			String pluginName = Util.getProviderFromKey(key);
			final String id = Util.getIdFromKey(key);
			if (!manager.plugins().containsKey(pluginName))
				throw new UnsupportedPluginFault();
			final AbstractPlugin plugin = manager.plugins().get(pluginName);	
			if (!plugin.getSupportedCapabilities().contains(Capabilities.Classification)) throw new UnsupportedCapabilityFault();

			final ResultWrapper<TaxonomyItem> wrapper = new ResultWrapper<TaxonomyItem>();
			ServiceContext.getContext().getSearchThreadPool().execute(new Runnable() {
				@Override
				public void run(){
					Writer<TaxonomyItem> writer  = new Writer<TaxonomyItem>(wrapper,  new TaxonomyItemWriterManager(plugin.getRepositoryName()));
					writer.register();
					try {
						plugin.getClassificationInterface().getSynonymnsById(writer, id);
					} catch (MethodNotSupportedException e) {
						logger.error("error retrieving synonyms "+e);
					}  catch (Exception e) {
						logger.error("error retrieving synonyms "+e);
					}finally{
						writer.close();
					}
				}
			});
			return wrapper.getLocator();
		}catch (IdNotValidException e) {
			logger.error("error retrieving children tree by id",e);
			throw new IdNotValidFault();
		}catch (GCUBEFault fault) {
			logger.error("error retrieving children tree by id",fault);
			throw fault;
		}catch (Exception e1) {
			logger.error("error retrieving children tree by id",e1);
			throw new GCUBEFault(e1);
		}
	}
	
	
	@Override
	public String getTaxaByIds(String IdsLocator) throws GCUBEFault {
		try{
			logger.trace("calling get taxon by id");
			System.out.println(IdsLocator);
			Stream<String> reader = convert(URI.create(IdsLocator)).ofStrings().withDefaults();
			ResultWrapper<TaxonomyItem> wrapper = new ResultWrapper<TaxonomyItem>();
			logger.trace("starting the thread");
			ServiceContext.getContext().getSearchThreadPool().execute(ScopedTasks.bind(new RunnableTaxonomySearch(reader, wrapper)));
			return wrapper.getLocator();
		}catch (Exception e) {
			throw new GCUBEFault(e);
		}
	}
	
	
	protected static void retrieveTaxaTree(final ObjectWriter<TaxonomyItem> writer, final TaxonomyItem taxon, final AbstractPlugin plugin) {
		try {
			new JobRetryCall<VOID, IdNotValidException>() {

				@Override
				protected VOID execute()
						throws ExternalRepositoryException, IdNotValidException {
					writer.write(taxon);
					List<TaxonomyItem> items = plugin.getClassificationInterface().retrieveTaxonChildrenByTaxonId(taxon.getId());
					for(TaxonomyItem item : items){
						item.setParent(taxon);
						retrieveTaxaTree(writer, item, plugin);
					}
					return new VOID();
				}
				
			}.call();
		
		} catch (IdNotValidException e) {
			writer.write(new StreamNonBlockingException(plugin.getRepositoryName(), taxon.getId()));
		} catch (MaxRetriesReachedException e) {
			logger.error("blocking error retrieving taxa tree",e);
			writer.write(new StreamBlockingException(plugin.getRepositoryName()));
		}
		
	}
	
	
	public class RunnableTaxonomySearch implements Runnable{

		Stream<String> reader;
		ResultWrapper<TaxonomyItem> wrapper;

		public RunnableTaxonomySearch(Stream<String> reader,
				ResultWrapper<TaxonomyItem> wrapper) {
			super();
			this.reader = reader;
			this.wrapper = wrapper;
		}

		public void run(){
			Map<String, Writer<String>> pluginMap= new HashMap<String, Writer<String>>();
			while (reader.hasNext()){
				String key = reader.next();
				try{
					final String provider = Util.getProviderFromKey(key);
					String id = Util.getIdFromKey(key);
					if (!pluginMap.containsKey(provider)){
						final LocalWrapper<String> localWrapper = new LocalWrapper<String>();
						pluginMap.put(provider, new Writer<String>(localWrapper));
						ServiceContext.getContext().getSearchThreadPool().execute(new Runnable(){
							public void run(){
								final AbstractPlugin plugin = PluginManager.get().plugins().get(provider);
								final Writer<TaxonomyItem> writer =new Writer<TaxonomyItem>(wrapper, new TaxonomyItemWriterManager(plugin.getRepositoryName()));
								writer.register();

								try {
									new QueryRetryCall() {

										@Override
										protected VOID execute()
												throws ExternalRepositoryException {
											plugin.getClassificationInterface().retrieveTaxonByIds(new LocalReader<String>(localWrapper), writer);
											return new VOID();
										}

									}.call();
								} catch (MaxRetriesReachedException e) {
									writer.write(new StreamBlockingException(plugin.getRepositoryName()));
								}
							}
						});
					}
					pluginMap.get(provider).write(id);
				}catch (IdNotValidException e) {
					logger.warn("the key "+key+" is not valid");
				}
			}
			for (Writer<String> writer : pluginMap.values())
				writer.close();
		}
		
	}

}
