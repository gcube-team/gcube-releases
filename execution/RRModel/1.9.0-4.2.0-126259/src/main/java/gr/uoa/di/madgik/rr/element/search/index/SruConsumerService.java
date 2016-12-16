package gr.uoa.di.madgik.rr.element.search.index;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistryException;

import java.util.List;

public class SruConsumerService extends DataSourceService
{
	public SruConsumerService() throws ResourceRegistryException
	{
		super(SruConsumerServiceDao.class);
		setType(Type.SruConsumer);
	}
	
	@Override
	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("SruConsumerService - ID : "+this.getID()+"\n");
		buf.append("SruConsumerService - Endpoint: "+this.getEndpoint()+"\n");
		buf.append("SruConsumerService - Hosting Node: "+this.getHostingNode()+"\n");
		buf.append("SruConsumerService - Functionality : "+this.getFunctionality()+"\n");
		buf.append("SruConsumerService - Scopes : ");
		for(String scope : this.getScopes()) buf.append(scope+" "); 
		return buf.toString();
	}
	
	@Override
	public boolean exists(RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return exists(SruConsumerServiceDao.class, persistencyType);
	}

	
	public static List<DataSourceService> getAll(boolean loadDetails) throws ResourceRegistryException
	{
		return DataSourceService.getAll(SruConsumerServiceDao.class, DatastoreType.LOCAL, loadDetails);
	}
	
	public static List<DataSourceService> getAll(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		return DataSourceService.getAll(SruConsumerServiceDao.class, persistencyType, loadDetails);
	}
	
	public static DataSourceService getById(boolean loadDetails, String id) throws ResourceRegistryException 
	{
		SruConsumerService f = new SruConsumerService();
		f.setID(id);
		return f.load(loadDetails) == true ? f : null;
	}
}
