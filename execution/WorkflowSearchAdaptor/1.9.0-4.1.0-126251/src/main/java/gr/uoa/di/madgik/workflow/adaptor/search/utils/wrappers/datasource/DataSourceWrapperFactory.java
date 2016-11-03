package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

import java.net.URL;
import java.util.Properties;

public class DataSourceWrapperFactory 
{
	private DataSourceWrapperFactoryConfig cfg = null;

	public DataSourceWrapperFactory() throws Exception
	{
		URL configResource = null;
		if((configResource = Thread.currentThread().getContextClassLoader().getResource("datasourceWrapperFactoryConfig.properties")) == null)
		{
			cfg= DataSourceWrapperFactoryConfig.newInstance();
			cfg.add(DataSourceWrapper.Type.FullTextIndexNode, "gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.gcube.GCubeFullTextIndexNodeWrapper");
			cfg.add(DataSourceWrapper.Type.OpenSearchDataSource, "gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.gcube.GCubeOpenSearchDataSourceServiceWrapper");
			
			
			cfg.add(DataSourceWrapper.Type.SruConsumer, "gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.gcube.GCubeSruConsumerServiceWrapper");
			
			cfg.validate();
		}else
		{
			Properties props = new Properties();
			props.load(configResource.openStream());
			cfg = DataSourceWrapperFactoryConfig.newInstance();
			
			for(String key : props.stringPropertyNames())
				cfg.add(DataSourceWrapper.Type.valueOf(key), props.getProperty(key));
			cfg.validate();
		}
	}
	
	public DataSourceWrapperFactory(DataSourceWrapperFactoryConfig cfg)
	{
		this.cfg = cfg;
	}
	
	public FullTextIndexNodeWrapper newFullTextIndexNodeWrapper(String serviceEndpoint, EnvHintCollection hints) throws Exception
	{
		return (FullTextIndexNodeWrapper)Class.forName(cfg.get(DataSourceWrapper.Type.FullTextIndexNode)).
				getConstructor(String.class, EnvHintCollection.class).newInstance(serviceEndpoint, hints);
	}
	
	public OpenSearchDataSourceServiceWrapper newOpenSearchDataSourceServiceWrapper(String serviceEndpoint, EnvHintCollection hints) throws Exception
	{
		return (OpenSearchDataSourceServiceWrapper)Class.forName(cfg.get(DataSourceWrapper.Type.OpenSearchDataSource)).
				getConstructor(String.class, EnvHintCollection.class).newInstance(serviceEndpoint, hints);
	}
	
	
	public SruConsumerServiceWrapper newSruConsumerServiceWrapper(String serviceEndpoint, EnvHintCollection hints) throws Exception
	{
		return (SruConsumerServiceWrapper)Class.forName(cfg.get(DataSourceWrapper.Type.SruConsumer)).
				getConstructor(String.class, EnvHintCollection.class).newInstance(serviceEndpoint, hints);
	}
}

