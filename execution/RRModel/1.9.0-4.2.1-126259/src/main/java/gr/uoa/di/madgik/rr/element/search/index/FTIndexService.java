package gr.uoa.di.madgik.rr.element.search.index;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;

import java.util.List;

public class FTIndexService extends DataSourceService
{
	public FTIndexService() throws ResourceRegistryException
	{
		super(FTIndexServiceDao.class);
		setType(Type.FullTextIndex);
	}
	
	@Override
	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("FTIndexService - ID : "+this.getID()+"\n");
		buf.append("FTIndexService - Endpoint: "+this.getEndpoint()+"\n");
		buf.append("FTIndexService - Hosting Node: "+this.getHostingNode()+"\n");
		buf.append("FTIndexService - Functionality : "+this.getFunctionality()+"\n");
		buf.append("FTIndexService - Scopes : ");
		for(String scope : this.getScopes()) buf.append(scope+" "); 
		return buf.toString();
	}
	
	@Override
	public boolean exists(RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return exists(FTIndexServiceDao.class, persistencyType);
	}

	
	public static List<DataSourceService> getAll(boolean loadDetails) throws ResourceRegistryException
	{
		return DataSourceService.getAll(FTIndexServiceDao.class, DatastoreType.LOCAL, loadDetails);
	}

	public static List<DataSourceService> getAll(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		return DataSourceService.getAll(FTIndexServiceDao.class, persistencyType, loadDetails);
	}
	
	public static DataSourceService getById(boolean loadDetails, String id) throws ResourceRegistryException 
	{
		FTIndexService f = new FTIndexService();
		f.setID(id);
		return f.load(loadDetails) == true ? f : null;
	}
}
