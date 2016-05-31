package gr.uoa.di.madgik.rr.element.search.index;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistryException;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTIndex extends DataSource {

	private static final Logger logger = LoggerFactory
			.getLogger(FTIndex.class);
	
	
	public FTIndex() throws ResourceRegistryException
	{
		super(FTIndexDao.class, FTIndexService.class);
		setType(Type.FullTextIndex);
	}
	
	@Override
	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("FTIndex - ID : "+this.getID()+"\n");
		buf.append("FTIndex - Functionality : "+this.getFunctionality()+"\n");
		buf.append("FTIndex - Scopes : ");
		for(String scope : this.getScopes()) buf.append(scope+" "); 
		buf.append("\nFTIndex - Capabilities : ");
		for(String cap : this.getCapabilities()) buf.append(cap+" "); 
		buf.append("\nFTIndex - Fields : "+"\n");
		for(FieldIndexContainer cap : this.getFieldInfo()) buf.append(cap.deepToString()+"\n"); 
		return buf.toString();
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
					DataSourceService s = FTIndexService.getById(true, ds);
					if(s != null) ret.add(s);
					else logger.warn("Could not find bound full text index service with id " + ds);
				}
				this.boundDataSourceServices = ret;
				return ret;
			}else
			{
				return FTIndexService.getAll(true);
			}
		}else
			return this.boundDataSourceServices;
	}
	
	@Override
	public boolean exists(RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return exists(FTIndexDao.class, persistencyType);
	}

	public static List<DataSource> getAll(boolean loadDetails) throws ResourceRegistryException
	{
		return DataSource.getAll(FTIndexDao.class, DatastoreType.LOCAL, loadDetails);
	}
	
	public static List<DataSource> getAll(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		return DataSource.getAll(FTIndexDao.class, persistencyType, loadDetails);
	}

	public static DataSource getById(boolean loadDetails, String id) throws ResourceRegistryException 
	{
		FTIndex f = new FTIndex();
		f.setID(id);
		return f.load(loadDetails) == true ? f : null;
	}
}
