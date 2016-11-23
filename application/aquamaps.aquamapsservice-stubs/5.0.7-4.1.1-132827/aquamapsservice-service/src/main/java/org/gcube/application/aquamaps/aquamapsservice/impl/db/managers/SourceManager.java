package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads.SourceImporter;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.ConfigurationManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.DBDescriptor;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ExportCSVSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ImportResourceRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.MetaSourceFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.gcube_system.namespaces.application.aquamaps.types.PagedRequestSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceManager {

	final static Logger logger= LoggerFactory.getLogger(SourceManager.class);
	
	private static final String sourcesTable="meta_sources";
	
	
	public static int getDefaultId(ResourceType type)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Field> filter=new ArrayList<Field>();
			filter.add(new Field(MetaSourceFields.type+"",type+"",FieldType.STRING));
			filter.add(new Field(MetaSourceFields.defaultsource+"",true+"",FieldType.BOOLEAN));
			Set<Resource> found=loadRS(session.executeFilteredQuery(filter, sourcesTable, MetaSourceFields.searchid+"", OrderDirection.ASC));
			if(found.isEmpty()) throw new Exception("No Default Found for type "+type);
			else return found.iterator().next().getSearchId();
		}catch(Exception e){throw e;}
		finally{if(session!=null)if(session!=null) session.close();}
	}
	
	
	
	public static Resource registerSource(Resource toRegister)throws Exception{
		DBSession session=null;
		logger.trace("registering source "+toRegister);
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> rows= new ArrayList<List<Field>>();
			ArrayList<Field> row= new ArrayList<Field>();
			for(MetaSourceFields field:MetaSourceFields.values())
				if(!field.equals(MetaSourceFields.searchid))
					row.add(toRegister.getField(field));
			
			
			rows.add(row);
			List<List<Field>> ids = session.insertOperation(sourcesTable, rows);
			for(Field f: ids.get(0)) 
				if(f.name().equals(MetaSourceFields.searchid+"")) toRegister.setSearchId(f.getValueAsInteger());
			logger.trace("registered source with id : "+toRegister.getSearchId());
			return toRegister;
		}catch(Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	
	public static void deleteSource(int id,boolean deleteTable) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			Resource toDelete=getById(id);
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(MetaSourceFields.searchid+"",id+"",FieldType.INTEGER));
			session.deleteOperation(sourcesTable, filter);
			
			if(deleteTable) session.dropTable(toDelete.getTableName());
		}catch(Exception e){
			throw e;			
		}finally{
			if(session!=null) session.close();
		}
	}
	
	public static String getSourceName(int id)throws Exception{
		return (String) getField(id, MetaSourceFields.tablename);
	}
	
	public static String getSourceTitle(int id)throws Exception{
		return (String) getField(id,MetaSourceFields.title);
	}
	
	
	
	private static Object getField(int id, MetaSourceFields field)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(MetaSourceFields.searchid+"",id+"",FieldType.INTEGER));
			ResultSet rs= session.executeFilteredQuery(filter, sourcesTable, MetaSourceFields.searchid+"", OrderDirection.ASC);
			if(rs.next())
				return rs.getObject(field+"");
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	private static int updateField(int id, MetaSourceFields field, FieldType objectType,Object value)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(MetaSourceFields.searchid+"",id+"",FieldType.INTEGER));
			keys.add(filter);
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> valueList=new ArrayList<Field>();
			valueList.add(new Field(field+"",value+"",objectType));
			values.add(valueList);
			return session.updateOperation(sourcesTable, keys, values);
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	
	
	public static void setTableTitle(int id, String tableTitle)throws Exception{
		updateField( id, MetaSourceFields.title, FieldType.STRING,tableTitle);
	}
	public static void setCountRow(int id, Long count)throws Exception{
		updateField( id, MetaSourceFields.rowcount, FieldType.INTEGER,count);
	}
	public static Set<Resource> getList(List<Field> filter)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return loadRS((session.executeFilteredQuery(filter, sourcesTable, MetaSourceFields.searchid+"", OrderDirection.ASC)));
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	public static String getJsonList(List<Field> filter, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return DBUtils.toJSon(session.executeFilteredQuery(filter, sourcesTable, settings.getOrderField(), settings.getOrderDirection()), settings.getOffset(), settings.getLimit()+settings.getOffset());
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	private static Set<Resource> loadRS(ResultSet rs) throws Exception{
		HashSet<Resource> toReturn=new HashSet<Resource>();
		while(rs.next()){
			toReturn.add(new Resource(rs));
		}
		return toReturn;
	}
	
	
	
	public static Resource getById(int id)throws Exception{
		if(id==0) return null;
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filters=new ArrayList<Field>();
			filters.add(new Field(MetaSourceFields.searchid+"",id+"",FieldType.INTEGER));
			return loadRS(session.executeFilteredQuery(filters, sourcesTable, MetaSourceFields.searchid+"", OrderDirection.ASC)).iterator().next();
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	
	public static int update(Resource toUpdate)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			session.disableAutoCommit();
			if(toUpdate.getDefaultSource()){
				List<List<Field>> values=new ArrayList<List<Field>>();
				List<Field> toSet=new ArrayList<Field>();
				toSet.add(new Field(MetaSourceFields.defaultsource+"",false+"",FieldType.BOOLEAN));
				values.add(toSet);
				List<List<Field>> keys=new ArrayList<List<Field>>();
				List<Field> key=new ArrayList<Field>();
				key.add(toUpdate.getField(MetaSourceFields.type));
				keys.add(key);
				session.updateOperation(sourcesTable, keys, values);
			}
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> value=new ArrayList<Field>();
			for(MetaSourceFields field:MetaSourceFields.values())
				if(!field.equals(MetaSourceFields.searchid))
					value.add(toUpdate.getField(field));
			
			values.add(value);
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> key=new ArrayList<Field>();
			key.add(toUpdate.getField(MetaSourceFields.searchid));
			keys.add(key);
			int rows=session.updateOperation(sourcesTable, keys, values);
			session.commit();
			return rows;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static Integer importFromCSVFile(ImportResourceRequestType request)throws Exception{
		DBSession session=null;
		try{
			ResourceType type=ResourceType.valueOf(request.getResourceType());
			session=DBSession.getInternalDBSession();
			final String tableName=ServiceUtils.generateId(type+"", "").toLowerCase();			
			session.createLikeTable(tableName, getById(getDefaultId(type)).getTableName());			
			Resource toRegister=new Resource(type, 0);
			toRegister.setAuthor(request.getUser());
			toRegister.setDefaultSource(false);
			toRegister.setGenerationTime(System.currentTimeMillis());
			toRegister.setDescription("Imported csv file ");
			toRegister.setTableName(tableName);
			toRegister.setTitle("Import_"+request.getUser());
			toRegister.setStatus(ResourceStatus.Importing);
			toRegister.setRowCount(0l);
			toRegister=registerSource(toRegister);
			ExportCSVSettings settings=request.getCsvSettings();
			SourceImporter t=new SourceImporter(request.getRsLocator(), toRegister,getDefaultId(type),settings.getDelimiter().charAt(0),
					settings.getFieldsMask(),settings.isHasHeader(),settings.getEncoding());
			t.start();
			return toRegister.getSearchId();
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static void checkTables()throws Exception{
		DBSession session =null;
		Set<Resource> list=getList(new ArrayList<Field>());
		try{
			session=DBSession.getInternalDBSession();
		for(Resource r:list){
			try{
				//check table Existance
				logger.trace("Checking "+r);
				boolean existing=true;
				
				try{
					session.executeQuery("SELECT * FROM "+r.getTableName()+" LIMIT 1 OFFSET 0");
				}catch(Exception e){
					logger.trace("Unable to detect table "+r.getTableName()+", going to delete resource");
					deleteSource(r.getSearchId(), false);
					existing=false;
				}
				
				if(existing){
					if(r.getRowCount()==0){
						logger.trace("Updateing row count");
						r.setRowCount(session.getCount(r.getTableName(), new ArrayList<Field>()));
					}
					HashMap<ResourceType,ArrayList<Integer>> sourcesLists=new HashMap<ResourceType, ArrayList<Integer>>();
					sourcesLists.put(ResourceType.HCAF, new ArrayList<Integer>(r.getSourceHCAFIds()));
					sourcesLists.put(ResourceType.HSPEN, new ArrayList<Integer>(r.getSourceHSPENIds()));
					sourcesLists.put(ResourceType.HSPEC, new ArrayList<Integer>(r.getSourceHSPECIds()));
					sourcesLists.put(ResourceType.OCCURRENCECELLS, new ArrayList<Integer>(r.getSourceOccurrenceCellsIds()));
					
					boolean checkTableNames=false;
					for(ResourceType type:ResourceType.values()){
						for(Integer id:sourcesLists.get(type)){
							Resource toCheck=getById(id);
							if(toCheck==null) {
								logger.trace("Unable to find source , id was "+id);
								r.removeSourceId(id);
								checkTableNames=true;
							}else{
								r.removeSource(toCheck);
								r.addSource(toCheck);
							}							
						}
					}
					
					if(checkTableNames){
						HashMap<ResourceType,ArrayList<String>> tableLists=new HashMap<ResourceType, ArrayList<String>>();
						tableLists.put(ResourceType.HCAF, new ArrayList<String>(r.getSourceHCAFTables()));
						tableLists.put(ResourceType.HSPEN, new ArrayList<String>(r.getSourceHSPENTables()));
						tableLists.put(ResourceType.HSPEC, new ArrayList<String>(r.getSourceHSPECTables()));
						tableLists.put(ResourceType.OCCURRENCECELLS, new ArrayList<String>(r.getSourceOccurrenceCellsTables()));
						
						for(ResourceType type:ResourceType.values()){
							for(String table:tableLists.get(type)){
								if(!session.checkTableExist(table)){
									logger.trace("Found non existing table reference : "+table);
									r.removeSourceTableName(table);
								}															
							}
						}
					}
					update(r);
				}
			}catch (Exception e){
				logger.warn("Unable to check resource "+r.getSearchId(),e);
			}
		}
		}catch(Exception e){throw e;}
		finally{if(session!=null)session.close();}
	}
	
	public static final String getToUseTableStore()throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();			
			ResultSet rs=session.executeFilteredQuery(new ArrayList<Field>(), sourcesTable, MetaSourceFields.searchid+"", OrderDirection.DESC);
			int lastId=0;
			if(rs.next()){
				lastId=rs.getInt(MetaSourceFields.searchid+"");
				rs.close();
			}
			DBDescriptor dbDescr=ConfigurationManager.getVODescriptor().getInternalDB();
			int numTableSpaces=Integer.parseInt(dbDescr.getProperty(DBDescriptor.TABLESPACE_COUNT));
			int toUseTableSpace=((lastId+1) % numTableSpaces)+1;
			String toReturn=dbDescr.getProperty(DBDescriptor.TABLESPACE_PREFIX)+toUseTableSpace;
			logger.debug("TableSpace to use : "+toReturn);
			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	
	public static final String getMaxMinTable(Resource Hspen)throws Exception{
		if(!Hspen.getType().equals(ResourceType.HSPEN)) throw new Exception("Passed Resource is not HSPEN, resource was "+Hspen);
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			String candidateMaxMin="maxminlat_"+Hspen.getTableName();
			if(session.checkTableExist(candidateMaxMin)) {
				logger.debug("Found "+candidateMaxMin+" for HSPEN table "+Hspen.getTableName());
				return candidateMaxMin;
			}
			else {
				Resource defaultHSPEN=getById(getDefaultId(ResourceType.HSPEN));
				if(defaultHSPEN==null) throw new Exception("Unable to evaluate default HSPEN Table");
				candidateMaxMin="maxminlat_"+defaultHSPEN.getTableName();
				if(session.checkTableExist(candidateMaxMin)) {
					logger.debug("Found "+candidateMaxMin+" (From Default HSPEN Table ID : "+defaultHSPEN.getSearchId()+")for HSPEN table "+Hspen.getTableName());
					return candidateMaxMin;
				}
				else throw new Exception("Unable to find default Max Min Hspen Table ");
			}			
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
}


