package gr.uoa.di.madgik.rr.element.search.index;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;

import java.util.List;

public class OpenSearchDataSourceService extends DataSourceService
{
	public OpenSearchDataSourceService() throws ResourceRegistryException
	{
		super(OpenSearchDataSourceServiceDao.class);
		setType(Type.OpenSearch);
	}
	
	@Override
	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("OpenSearchDataSourceService - ID : "+this.getID()+"\n");
		buf.append("OpenSearchDataSourceService - Endpoint: "+this.getEndpoint()+"\n");
		buf.append("OpenSearchDataSourceService - Hosting Node: "+this.getHostingNode()+"\n");
		buf.append("OpenSearchDataSourceService - Functionality : "+this.getFunctionality()+"\n");
		buf.append("OpenSearchDataSourceService - Scopes : ");
		for(String scope : this.getScopes()) buf.append(scope+" "); 
		return buf.toString();
	}
	
	@Override
	public boolean exists(RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return exists(OpenSearchDataSourceServiceDao.class, persistencyType);
	}

	
	public static List<DataSourceService> getAll(boolean loadDetails) throws ResourceRegistryException
	{
		return DataSourceService.getAll(OpenSearchDataSourceServiceDao.class, DatastoreType.LOCAL, loadDetails);
	}
	
	public static List<DataSourceService> getAll(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		return DataSourceService.getAll(OpenSearchDataSourceServiceDao.class, persistencyType, loadDetails);
	}
	
	public static DataSourceService getById(boolean loadDetails, String id) throws ResourceRegistryException 
	{
		OpenSearchDataSourceService f = new OpenSearchDataSourceService();
		f.setID(id);
		return f.load(loadDetails) == true ? f : null;
	}
}
