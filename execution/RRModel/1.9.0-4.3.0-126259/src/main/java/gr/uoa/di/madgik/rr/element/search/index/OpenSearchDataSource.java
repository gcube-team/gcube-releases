package gr.uoa.di.madgik.rr.element.search.index;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistryException;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSearchDataSource extends DataSource {

	private static final Logger logger = LoggerFactory
			.getLogger(OpenSearchDataSource.class);
	
	public OpenSearchDataSource() throws ResourceRegistryException
	{
		super(OpenSearchDataSourceDao.class, OpenSearchDataSourceService.class);
		super.setType(Type.OpenSearch);
	}
	
	@Override
	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("OpenSearchDataSource - ID : "+this.getID()+"\n");
		buf.append("OpenSearchDataSource - Functionality : "+this.getFunctionality()+"\n");
		buf.append("OpenSearchDataSource - Scopes : ");
		for(String scope : this.getScopes()) buf.append(scope+" "); 
		buf.append("\nOpenSearchDataSource - Capabilities : ");
		for(String cap : this.getCapabilities()) buf.append(cap+" "); 
		buf.append("\nOpenSearchDataSource - Fields : "+"\n");
		for(FieldIndexContainer cap : this.getFieldInfo()) buf.append(cap.deepToString()+"\n"); 
		return buf.toString();
	}
	
	@Override
	public boolean exists(RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return exists(OpenSearchDataSourceDao.class, persistencyType);
	}

	public static List<DataSource> getAll(boolean loadDetails) throws ResourceRegistryException
	{
		return DataSource.getAll(OpenSearchDataSourceDao.class, DatastoreType.LOCAL, loadDetails);
	}
	
	public static List<DataSource> getAll(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		return DataSource.getAll(OpenSearchDataSourceDao.class, persistencyType, loadDetails);
	}

	@Override
	public List<DataSourceService> getDataSourceServices() throws ResourceRegistryException
	{
		if(this.boundDataSourceServices == null)
		{
			if(this.item.getBoundDataSourceServices() != null)
			{
				List<DataSourceService> ret = new ArrayList<DataSourceService>();
				for(String ds : this.item.getBoundDataSourceServices())
				{
					DataSourceService s = OpenSearchDataSourceService.getById(true, ds);
					if(s != null) ret.add(s);
					else logger.warn("Could not find bound open search datasource service with id " + ds);
				}
				this.boundDataSourceServices = ret;
				return ret;
			}else
			{
				return OpenSearchDataSourceService.getAll(true);
			}
		}else
			return this.boundDataSourceServices;
	}
	
	public static DataSource getById(boolean loadDetails, String id) throws ResourceRegistryException 
	{
		OpenSearchDataSource f = new OpenSearchDataSource();
		f.setID(id);
		return f.load(loadDetails) == true ? f : null;
	}
}
