package gr.uoa.di.madgik.rr.element.search.index;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistryException;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SruConsumer extends DataSource {

	private static final Logger logger = LoggerFactory
			.getLogger(SruConsumer.class);
	
	public SruConsumer() throws ResourceRegistryException
	{
		super(SruConsumerDao.class, SruConsumerService.class);
		super.setType(Type.SruConsumer);
	}
	
	@Override
	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("SruConsumer - ID : "+this.getID()+"\n");
		buf.append("SruConsumer - Functionality : "+this.getFunctionality()+"\n");
		buf.append("SruConsumer - Scopes : ");
		for(String scope : this.getScopes()) buf.append(scope+" "); 
		buf.append("\nSruConsumer - Capabilities : ");
		for(String cap : this.getCapabilities()) buf.append(cap+" "); 
		buf.append("\nSruConsumer - Fields : "+"\n");
		for(FieldIndexContainer cap : this.getFieldInfo()) buf.append(cap.deepToString()+"\n"); 
		return buf.toString();
	}
	
	@Override
	public boolean exists(RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return exists(SruConsumerDao.class, persistencyType);
	}

	public static List<DataSource> getAll(boolean loadDetails) throws ResourceRegistryException
	{
		return DataSource.getAll(SruConsumerDao.class, DatastoreType.LOCAL, loadDetails);
	}
	
	public static List<DataSource> getAll(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		return DataSource.getAll(SruConsumerDao.class, persistencyType, loadDetails);
	}

	@Override
	public List<DataSourceService> getDataSourceServices() throws ResourceRegistryException
	{
		if(this.boundDataSourceServices == null)
		{
			if(this.item.getBoundDataSourceServices() != null)
			{
				logger.info("item.getBoundDataSourceServices : " + item.getBoundDataSourceServices());
				List<DataSourceService> ret = new ArrayList<DataSourceService>();
				for(String ds : this.item.getBoundDataSourceServices())
				{
					DataSourceService s = SruConsumerService.getById(true, ds);
					if(s != null) ret.add(s);
					else {
						logger.warn("Could not find bound sru datasource service with id " + ds);
						List<DataSourceService> services = SruConsumerService.getAll(true);
						
						logger.warn("all datasource ids are : ");
						for (DataSourceService service : services){
							logger.warn(" ~> service id : " + service.getID());
						}
						
					}
				}
				this.boundDataSourceServices = ret;
				return ret;
			}else
			{
				return SruConsumerService.getAll(true);
			}
		}else
			return this.boundDataSourceServices;
	}
	
	public static DataSource getById(boolean loadDetails, String id) throws ResourceRegistryException 
	{
		SruConsumer f = new SruConsumer();
		f.setID(id);
		return f.load(loadDetails) == true ? f : null;
	}
}
