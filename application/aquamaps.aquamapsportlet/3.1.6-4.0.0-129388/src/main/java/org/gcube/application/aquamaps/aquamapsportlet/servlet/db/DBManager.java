package org.gcube.application.aquamaps.aquamapsportlet.servlet.db;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.sf.csv4j.CSVReaderProcessor;

import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.AreaFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.LocalObjectFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientObject;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.Utils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportOperation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceStatus;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBManager implements DBInterface{



	private static final Logger log = LoggerFactory.getLogger(DBManager.class);


	protected static Map<String,DBManager> instanceMap=new ConcurrentHashMap<String, DBManager>();

	protected DBSession session;

	protected String associatedScope;

	private long lastUpdateTime;
	
	public static Set<String> getInitializedScopes(){
		return instanceMap.keySet();
	}

	
	public static synchronized void deleteDb(String currentScope)throws Exception{
		String scope=Utils.removeVRE(currentScope);
		if(instanceMap.containsKey(scope)) instanceMap.remove(scope);
		DBSession.dropDataBase(scope);
	}
	
	public static synchronized DBInterface getInstance(String currentScopeString) throws Exception
	{
		String scope=Utils.removeVRE(currentScopeString);
		if (!instanceMap.containsKey(scope) || (instanceMap.containsKey(scope) && instanceMap.get(scope) == null)) instanceMap.put(scope, new DBManager(scope));
		instanceMap.get(scope).checkSession();
		return instanceMap.get(scope);
	}

	protected DBManager(String scope) throws Exception {
		associatedScope=scope;
		checkSession();
		log.debug("Checking DB connection ...");
		session.executeQuery("SELECT * from "+Tables.Species);
		retrieveSpeciesFields();
		fetchSpecies();
		//		speciesInsertionQuery=speciesInsertQuery();
		//		speciesUpdateQuery=speciesUpdateQuery();
	}

	protected void checkSession() throws Exception{
		session=DBSession.getInstance(associatedScope);
		session.disableAutoCommit(); 
	}

	/*public void loadJSONAreaType(Area.Type type,String JSON)throws Exception{
		log.debug("Executing importing of "+type.toString()+" areas");
		JSONArray array;
		String sql="INSERT into ? VALUES (?,?)";
		PreparedStatement ps = connection.prepareStatement(sql);
		try {			
			array= new JSONArray(JSON);
			ps.setString(1, type.toString());
			for(int i=0;i<array.length();i++){
				JSONObject obj= array.getJSONObject(i);
				ps.setString(2, obj.getString("name"));
				ps.setString(2, obj.getString("code"));
				ps.execute();				
			}
		//	int count=
		} catch (JSONException e) {
			log.error("Invalid JSON",e);
			throw new Exception("Invalid JSON");
		}		
	}*/


	@Override
	public boolean isUpToDate() {
		//5*60*1000 = 5 Minutes
		return (System.currentTimeMillis()-lastUpdateTime)<5*60*1000;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getAssociatedScope() {
		return associatedScope;
	}
	@Override
	public int fetchSpecies() throws Exception{
		File csvFile=null;
		try{
			ScopeProvider.instance.set(associatedScope.toString());
		csvFile=dataManagement().withTimeout(2, TimeUnit.MINUTES).build().exportTableAsCSV("speciesoccursum",null,null,null,ExportOperation.TRANSFER);
		int toReturn = importSpeciesOccursumCSV(csvFile);	
		setLastUpdateTime(System.currentTimeMillis());
		return toReturn;
		}finally{
			try{if(csvFile!=null&&csvFile.exists())FileUtils.forceDelete(csvFile);}
			catch(Exception e1){log.error("Unable to delete csv File "+csvFile.getAbsolutePath(),e1);}
		}
	}
	
	
	@Override
	public String getPhylogenyJSON(String level) throws Exception{
		log.debug("get Phylogeny for lvel : "+level);
		ResultSet rs=null;
		try{	
			String table=level;
			if(level.equalsIgnoreCase(SpeciesFields.ordercolumn+""))
				table=Tables.order_table+"";
			if(level.equalsIgnoreCase(SpeciesFields.classcolumn+""))
				table=Tables.class_table+"";
			if(level.equalsIgnoreCase(SpeciesFields.familycolumn+""))
				table=Tables.family_table+"";
			rs=session.executeQuery("SELECT * FROM "+table);
			return DBUtil.toJSon(rs);
		} catch (Exception e) {
			log.error("Exception while retrieving philogeny level "+level, e);
			throw e;
		}finally{if(rs!=null)rs.close();}
	}


	@Override
	public String getJSONAreasByType(boolean includeFAO,boolean includeEEZ, boolean includeLME, PagedRequestSettings settings)throws Exception{
		if(includeFAO||includeEEZ||includeLME){
			String statement=DBUtil.getAreaQuery(Tables.Area+"",includeFAO, includeEEZ, includeLME)+" order by "+settings.orderField()+" "+settings.orderDirection();
			ResultSet rs= null;
			try {
				log.debug("to submit query : "+statement);
				rs=session.executeQuery(statement);
				return DBUtil.toJSon(rs,settings.offset(),settings.offset()+settings.limit());
			} catch (Exception e) {
				log.error("Exception while retrieving areas", e);
				throw new Exception();
			}finally{if(rs!=null)rs.close();}
		}
		else return Tags.EMPTY_JSON;
	}

	@Override
	public List<Area> getAreasByType(boolean includeFAO, boolean includeEEZ,
			boolean includeLME) throws Exception {
		ResultSet rs=null;
		try{
			rs=session.executeQuery(DBUtil.getAreaQuery(Tables.Area+"",includeFAO, includeEEZ, includeLME));
			return DBUtil.loadAreas(rs);
		}catch(Exception e){throw e;}
		finally{if(rs!=null)rs.close();}
	}


	@Override
	public String getUserJSONBasket(String userName,int start, int limit,String sortColumn,String sortDirection) throws Exception{
		log.debug("Getting selected species for user : "+userName);
		ResultSet rsPage=null;
		try{
			String pagedQuery="Select "+Tables.Species+".*,"+Tables.Basket+"."+SpeciesFields.customized+" from "+Tables.Species+","+Tables.Basket+" where "
			+Tables.Basket+"."+SpeciesFields.speciesid+" = "+Tables.Species+"."+SpeciesFields.speciesid+" AND "+Tables.Basket+"."+DBCostants.userID+" = '"+userName+
			"' ORDER BY "+Tables.Species+"."+sortColumn+" "+sortDirection;
			rsPage=session.executeQuery(pagedQuery);			
			return DBUtil.toJSon(rsPage,start,limit+start);

		}catch(Exception e ){throw e;}
		finally{
			if(rsPage!=null) rsPage.close();
		}
	}
	@Override
	public int removeFromBasket(List<String> speciesIds, String userName) throws Exception{
		try{
			int count=0;
			if(speciesIds==null) {
				List<Field> row=new ArrayList<Field>();
				row.add(new Field(DBCostants.userID,userName,FieldType.STRING));
				count+=session.deleteOperation(Tables.Basket+"", row);
				session.deleteOperation(Tables.Objects_Basket+"",row);
			}else for(String id:speciesIds){
				List<Field> row=new ArrayList<Field>();
				row.add(new Field(SpeciesOccursumFields.speciesid+"",id,FieldType.STRING));
				row.add(new Field(DBCostants.userID,userName,FieldType.STRING));
				count+=session.deleteOperation(Tables.Basket+"", row);
				session.deleteOperation(Tables.Objects_Basket+"",row);
			}
			updateObjectsBasketsReferences(userName);
			session.commit();
			return count;
		}catch(Exception e ){session.connection.rollback();throw e;}
	}

	private void updateObjectsBasketsReferences(String userName)throws Exception{
		ResultSet rsObj=null;
		try{
			List<Field> userCondition=new ArrayList<Field>();
			userCondition.add(new Field(DBCostants.userID,userName,FieldType.STRING));

			rsObj=session.executeFilteredQuery(userCondition, Tables.Objects+"", DBCostants.userID, Tags.ASC);
			List<List<Field>> toUpdateRows=new ArrayList<List<Field>>();
			List<List<Field>> toUpdateValues=new ArrayList<List<Field>>();
			while(rsObj.next()){
				List<Field> userAndTitleCondition=new ArrayList<Field>();
				userAndTitleCondition.add(new Field(DBCostants.userID,userName,FieldType.STRING));
				userAndTitleCondition.add(new Field(SubmittedFields.title+"",rsObj.getString(SubmittedFields.title+""),FieldType.STRING));

				int count=session.getCount(Tables.Objects_Basket+"", userAndTitleCondition);

				String type=rsObj.getString(LocalObjectFields.type+"");
				if(type.equalsIgnoreCase(ClientObjectType.SpeciesDistribution.toString())){
					if(count==0) 
						session.deleteOperation(Tables.Objects+"", userAndTitleCondition);
				}else{
					toUpdateRows.add(userAndTitleCondition);
					List<Field> row=new ArrayList<Field>();
					row.add(new Field(LocalObjectFields.species+"",count+"",FieldType.STRING));
					toUpdateValues.add(row);
				}
			}
			if(toUpdateRows.size()>0)
				session.updateOperation(Tables.Objects+"", toUpdateRows, toUpdateValues);
			session.commit();
		}catch(Exception e ){session.connection.rollback();throw e;}
		finally{if (rsObj!=null)rsObj.close();}
	}
	@Override
	public int addToUserBasket(List<String> speciesIds, String userName)throws Exception{
		PreparedStatement ps= null;		
		try{
			Field userField=new Field(DBCostants.userID+"",userName,FieldType.STRING);
			Field customizedField=new Field(SpeciesFields.customized+"","0",FieldType.INTEGER);
			Field perturbationField=new Field(DBCostants.perturbations,Tags.EMPTY_JSON,FieldType.STRING);

			int count=0;
			for(String id: speciesIds){
				List<Field> values=new ArrayList<Field>();
				values.add(userField);
				values.add(new Field(SpeciesOccursumFields.speciesid+"",id,FieldType.STRING));
				values.add(customizedField);
				values.add(perturbationField);
				if(ps==null) ps=session.getPreparedStatementForInsert(values, Tables.Basket+"");
				try{
					count+=session.fillParameters(values,0, ps).executeUpdate();
				}catch(Exception e){
					//				log.warn("Species already inserted "+speciesIds,e);
				}
			}
			session.commit();
			return count;
		}catch(Exception e ){session.connection.rollback();throw e;}
		finally{if(ps!=null)ps.close();}
	}
	@Override
	public int addToObjectBasket(List<String> speciesIds,String userName,String title)throws Exception{
		return addToObjectBasket(speciesIds, userName, title, true);
	}



	private int addToObjectBasket(List<String> speciesIds, String userName,String title, boolean updateBasketsReferences)throws Exception{
		PreparedStatement psInsert=null;
		try{
			List<List<Field>> rows=loadSelection(speciesIds,userName,title);
			psInsert=session.getPreparedStatementForInsert(rows.get(0), Tables.Objects_Basket+"");
			int count=0;
			for(List<Field> row:rows){
				try{
					count+=session.fillParameters(row,0, psInsert).executeUpdate();				
				}catch(Exception e){
					//Already existing species
				}
			}
			if(updateBasketsReferences)updateObjectsBasketsReferences(userName);
			session.commit();
			return count;
		}catch(Exception e ){session.connection.rollback();throw e;}
		finally{if(psInsert!=null)psInsert.close();}
	}

	/** returns rows title,user,speciesid
	 * 
	 * @param speciesIds
	 * @param userName
	 * @param title
	 * @return
	 * @throws Exception
	 */



	private List<List<Field>> loadSelection(List<String> speciesIds,String userName,String title) throws Exception{
		List<List<Field>> rows= new ArrayList<List<Field>>();
		Field userField=new Field(DBCostants.userID,userName,FieldType.STRING);
		Field titleField=new Field(SubmittedFields.title+"",title,FieldType.STRING);
		if((speciesIds==null||speciesIds.size()==0)){
			//load user basket
			List<Field> userCondition=new ArrayList<Field>();
			userCondition.add(userField);
			ResultSet rsSpecs=session.executeFilteredQuery(userCondition, Tables.Basket+"", SpeciesFields.speciesid+"", Tags.ASC);
			while (rsSpecs.next()){
				List<Field> row=new ArrayList<Field>();
				row.add(titleField);
				row.add(userField);
				row.add(new Field(SpeciesFields.speciesid+"",rsSpecs.getString(SpeciesFields.speciesid+""),FieldType.STRING));
				rows.add(row);
			}
			rsSpecs.close();
		}else {
			for(String id:speciesIds){
				List<Field> row=new ArrayList<Field>();
				row.add(titleField);
				row.add(userField);
				row.add(new Field(SpeciesFields.speciesid+"",id,FieldType.STRING));
				rows.add(row);
			}
		}
		return rows;
	}

	@Override
	public int removeFromObjectBasket(List<String> speciesIds, String userName,String title) throws Exception{
		try{
			int count=0;
			for(List<Field> row: loadSelection(speciesIds, userName, title))
				count+=session.deleteOperation(Tables.Objects_Basket+"", row);
			updateObjectsBasketsReferences(userName);
			session.commit();
			return count;
		}catch(Exception e ){session.connection.rollback();throw e;}
	}



	private void addObject(String userName, String title, ClientObjectType type, String bbox, double threshold,String species, boolean gis) throws Exception {
		try{
			log.debug("Creating object "+title+" for user "+userName);	
			List<List<Field>> rows=new ArrayList<List<Field>>();
			List<Field> row = new ArrayList<Field>();
			row.add(new Field(DBCostants.userID,userName,FieldType.STRING));
			row.add(new Field(LocalObjectFields.title+"",title,FieldType.STRING));
			row.add(new Field(LocalObjectFields.type+"",type+"",FieldType.STRING));
			row.add(new Field(LocalObjectFields.bbox+"",bbox,FieldType.STRING));
			row.add(new Field(LocalObjectFields.threshold+"",threshold+"",FieldType.DOUBLE));
			row.add(new Field(LocalObjectFields.species+"",species+"",FieldType.STRING));
			row.add(new Field(LocalObjectFields.gis+"",gis+"",FieldType.BOOLEAN));
			rows.add(row);
			session.insertOperation(Tables.Objects+"", rows);
			session.commit();
		}catch(Exception e ){session.connection.rollback();throw e;}
	}
	//	@Override
	//	public int addSession(String userName) throws Exception {
	//		log.debug("Creating entry for user "+userName);
	//		List<List<Field>> rows=new ArrayList<List<Field>>();
	//		List<Field> row = new ArrayList<Field>();
	//		row.add(new Field(DBCostants.userID,userName,FieldType.STRING));
	//		rows.add(row);
	//		return session.insertOperation(Tables.Sessions+"", rows).size();
	//	}
	@Override
	public String getObjectJSONBasket(String userName, String title,int start, int limit,String sortColumn,String sortDirection)
	throws Exception {
		ResultSet rsPage=null;
		try{			
			String pagedQuery="Select "+Tables.Species+".*,"+Tables.Objects_Basket+"."+SpeciesFields.customized+" from "+Tables.Species+","+Tables.Objects_Basket+" where "+
			Tables.Objects_Basket+"."+SpeciesFields.speciesid+" = "+Tables.Species+"."+SpeciesFields.speciesid+" AND "+Tables.Objects_Basket+"."+DBCostants.userID+" = '"+userName+"' AND "+Tables.Objects_Basket+"."+LocalObjectFields.title+" = '"+
			title+"' ORDER BY "+Tables.Species+"."+sortColumn+" "+sortDirection;
				rsPage=session.executeQuery(pagedQuery);			
				return DBUtil.toJSon(rsPage,start,limit+start);
			
		}catch(Exception e ){throw e;}
		finally{
			if(rsPage!=null) rsPage.close();
		}
	}

	@Override
	public List<String> getObjectBasketIds(String userName, String title)
	throws Exception {
		List<Field> filter=new ArrayList<Field>();
		filter.add(new Field(DBCostants.userID,userName,FieldType.STRING));
		filter.add(new Field(LocalObjectFields.title+"",title,FieldType.STRING));
		List<String> toReturn=new ArrayList<String>();
		ResultSet rs=null;
		try{
			rs=session.executeFilteredQuery(filter, Tables.Objects_Basket+"", SpeciesFields.speciesid+"", Tags.ASC);
			while (rs.next()){
				toReturn.add(rs.getString(SpeciesFields.speciesid+""));
			}
		}catch(Exception e){}
		finally{if(rs!=null)rs.close();}
		return toReturn;
	}

	@Override
	public List<String> getUserBasketIds(String userName) throws Exception {
		List<Field> filter=new ArrayList<Field>();
		filter.add(new Field(DBCostants.userID,userName,FieldType.STRING));
		List<String> toReturn=new ArrayList<String>();
		ResultSet rs=null;
		try{
			rs=session.executeFilteredQuery(filter, Tables.Basket+"", SpeciesFields.speciesid+"", Tags.ASC);
			while (rs.next()){
				toReturn.add(rs.getString(SpeciesFields.speciesid+""));
			}
		}catch(Exception e){}
		finally{if(rs!=null)rs.close();}
		return toReturn;
	}

	@Override
	public int removeObjectByTitle(String userName, String title) throws Exception {
		try{
			List<Field> row=new ArrayList<Field>();
			row.add(new Field(DBCostants.userID,userName,FieldType.STRING));
			row.add(new Field(LocalObjectFields.title+"",title,FieldType.STRING));
			session.deleteOperation(Tables.Objects_Basket+"", row);
			int toReturn= session.deleteOperation(Tables.Objects+"", row);
			session.commit();
			return toReturn;
		}catch(Exception e ){session.connection.rollback();throw e;}
	}
	@Override
	public int removeSession(String userName) throws Exception {
		try{
			log.debug("Removing user "+userName);
			List<Field> row=new ArrayList<Field>();
			row.add(new Field(DBCostants.userID,userName,FieldType.STRING));
			int count=session.deleteOperation(Tables.Objects_Basket+"", row);
			count+=session.deleteOperation(Tables.Objects+"", row);
			count+=session.deleteOperation(Tables.Basket+"", row);
			session.commit();
			return count;
		}catch(Exception e ){session.connection.rollback();throw e;}
	}

	@Override
	public int updateObject(String userName,String oldTitle, String title, ClientObjectType type, String bbox, float threshold,boolean gis)
	throws Exception {
		try{
			List<List<Field>> rows=new ArrayList<List<Field>>();
			List<Field> value= new ArrayList<Field>();
			value.add(new Field(LocalObjectFields.title+"",title,FieldType.STRING));
			value.add(new Field(LocalObjectFields.type+"",type+"",FieldType.STRING));
			value.add(new Field(LocalObjectFields.bbox+"",bbox,FieldType.STRING));
			value.add(new Field(LocalObjectFields.threshold+"",threshold+"",FieldType.DOUBLE));
			value.add(new Field(LocalObjectFields.gis+"",(gis?1:0)+"",FieldType.INTEGER));

			rows.add(value);
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> key= new ArrayList<Field>();
			key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
			key.add(new Field(LocalObjectFields.title+"",oldTitle,FieldType.STRING));
			keys.add(key);
			int toReturn= session.updateOperation(Tables.Objects+"", keys, rows);
			session.commit();
			return toReturn;
		}catch(Exception e ){session.connection.rollback();throw e;}
	}
	@Override
	public List<ClientObject> getObjects(String userName)throws Exception{
		ResultSet rs=null;
		try{
			log.debug("Retrieving objects for user : "+userName);
			List<Field> filter=new ArrayList<Field>();
			filter.add(new Field(DBCostants.userID,userName,FieldType.STRING));
			rs=session.executeFilteredQuery(filter, Tables.Objects+"", DBCostants.userID, Tags.ASC);
			return DBUtil.loadObjects(rs);
		}catch(Exception e){throw e;}
		finally{if(rs!=null)rs.close();}

	}








	private void retrieveSpeciesFields() throws SQLException{
		//TODO dynamic retrieval of meta, hard coded solution provided to skip String case issues

		//		ResultSet rs = executeQuery("Select * from "+Species+" Offset 0 ROWS Fetch First 1 ROW Only");
		//		ResultSetMetaData meta=rs.getMetaData();
		//		speciesFields=new String[meta.getColumnCount()];
		//		speciesFieldsType=new int[meta.getColumnCount()];
		//		for(int i=0;i<speciesFields.length;i++){
		//			speciesFields[i]=meta.getColumnName(i+1);
		//			speciesFieldsType[i]=meta.getColumnType(i+1);
		//		}	


	}


	@Override
	public int clearBasket(String userName) throws Exception {
		try{
			List<Field> key=new ArrayList<Field>();
			key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
			session.deleteOperation(Tables.Objects_Basket+"", key);
			session.deleteOperation(Tables.Objects+"", key);
			int toReturn= session.deleteOperation(Tables.Basket+"", key);
			session.commit();
			return toReturn;
		}catch(Exception e ){session.connection.rollback();throw e;}
	}
	@Override
	public int clearObjectBasket(String userName, String title)
	throws Exception {
		try{
			List<Field> key=new ArrayList<Field>();
			key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
			key.add(new Field(LocalObjectFields.title+"",title,FieldType.STRING));
			int toReturn= session.deleteOperation(Tables.Objects_Basket+"", key);
			session.commit();
			return toReturn;
		}catch(Exception e ){session.connection.rollback();throw e;}
	}

	
	@Override
	public String getSpecies() throws Exception {
		ResultSet rs=null;
		try{
			rs=session.executeQuery("SELECT * FROM "+Tables.Species);
			return DBUtil.toJSon(rs);
		}catch(Exception e ){throw e;}
		finally{if(rs!=null)rs.close();}
	}
	@Override
	public int fetchGeneratedObjRelatedSpecies(int objId,
			List<String> speciesIds) throws Exception {
		try{
			deleteFetched(objId+"");
			log.debug("Fetching "+speciesIds.size()+" species for "+objId+" object basket");
			List<List<Field>> rows=new ArrayList<List<Field>>();
			for(String id:speciesIds){
				List<Field> row=new ArrayList<Field>();
				row.add(new Field(DBCostants.objectID,objId+"",FieldType.INTEGER));
				row.add(new Field(SpeciesFields.speciesid+"",id,FieldType.STRING));
				rows.add(row);
			}
			int toReturn= session.insertOperation(Tables.fetchedBasket+"", rows).size();
			session.commit();
			return toReturn;
		}catch(Exception e ){session.connection.rollback();throw e;}
	}
	@Override
	public String getFetchedJSONBasket(int objId, int start, int limit,String sortColumn,String sortDirection)
	throws Exception {
		ResultSet rs=null;
		PreparedStatement ps=null;
		try{
			log.debug("Getting fetched species for objBasket : "+objId);
			ps= session.preparedStatement("Select "+Tables.Species+".* from "+Tables.Species+","+Tables.fetchedBasket+" where "+
					Tables.fetchedBasket+"."+SpeciesFields.speciesid+" = "+Tables.Species+"."+SpeciesFields.speciesid+" AND "+Tables.fetchedBasket+"."+DBCostants.objectID+" = ? ORDER BY "+Tables.Species+"."+sortColumn+" "+sortDirection);
			ps.setInt(1, objId);
			rs =ps.executeQuery();
			return DBUtil.toJSon(rs,start,start+limit);
		}catch(Exception e ){throw e;}
		finally{
			if(rs!=null)rs.close();
			if(ps!=null)ps.close();
		}
	}
	@Override
	public int deleteFetched(String objId) throws Exception {
		try{
			List<Field> row=new ArrayList<Field>();
			row.add(new Field(DBCostants.objectID,objId+"",FieldType.INTEGER));
			int toReturn=session.deleteOperation(Tables.fetchedBasket+"", row);
			session.commit();
			return toReturn;
		}catch(Exception e ){session.connection.rollback();throw e;}
	}
	@Override
	public long getBasketCount(String userName) throws Exception {
		List<Field> key=new ArrayList<Field>();
		key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
		return session.getCount(Tables.Basket+"", key);
	}
	@Override
	public long getObjectBasketCount(String userName, String title)
	throws Exception {
		List<Field> key=new ArrayList<Field>();
		key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
		key.add(new Field(LocalObjectFields.title+"",title,FieldType.STRING));
		return session.getCount(Tables.Objects_Basket+"", key);
	}
	@Override
	public long getObjectCount(String userName) throws Exception {
		List<Field> key=new ArrayList<Field>();
		key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
		return session.getCount(Tables.Objects+"", key);
	}

	@Override
	public long getObjectCountByType(String userName,ClientObjectType type) throws Exception {
		List<Field> key=new ArrayList<Field>();
		key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
		key.add(new Field(LocalObjectFields.type+"",type+"",FieldType.STRING));
		return session.getCount(Tables.Objects+"", key);
	}

	@Override
	public String getJSONObjectsByType(String userName, ClientObjectType type, int start,	int limit, String sortColumn, String sortDirection)	throws Exception {
		ResultSet rs=null;
		try{
			log.debug("Getting objects by Type ("+type+") per user "+userName);
			List<Field> key=new ArrayList<Field>();
			key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
			key.add(new Field(LocalObjectFields.type+"",type+"",FieldType.STRING));
			rs=session.executeFilteredQuery(key, Tables.Objects+"", sortColumn, sortDirection);
			return DBUtil.toJSon(rs,start,start+limit);
		}catch(Exception e){throw e;}
		finally{if(rs!=null)rs.close();}
	}
	@Override
	public String getPerturbation(String speciesId, String userName)
	throws Exception {
		ResultSet rs=null;
		try{
		List<Field> key=new ArrayList<Field>();		
		key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
		key.add(new Field(SpeciesFields.speciesid+"",speciesId,FieldType.STRING));
		rs=session.executeFilteredQuery(key, Tables.Basket+"", DBCostants.userID, Tags.ASC);
		if(rs.next()){
			for(Field f: Field.loadRow(rs))
				if(f.name().equalsIgnoreCase(DBCostants.perturbations)) return f.value();
			return null;
		}else return null;
		}catch(Exception e ){throw e;}
		finally{if(rs!=null)rs.close();}
	}

	@Override
	public void setPerturbation(String speciesIds, String userName,
			String jsonString) throws Exception {
		try{
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> key=new ArrayList<Field>();
			key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
			key.add(new Field(SpeciesFields.speciesid+"",speciesIds,FieldType.STRING));
			keys.add(key);
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> value=new ArrayList<Field>();
			value.add(new Field(DBCostants.perturbations,jsonString,FieldType.STRING));
			value.add(new Field(SpeciesFields.customized+"",1+"",FieldType.INTEGER));
			values.add(value);
			session.updateOperation(Tables.Basket+"", keys, values);
			session.commit();
		}catch(Exception e ){session.connection.rollback();throw e;}
	}

	@Override
	public int removeObjectByType(String userName, ClientObjectType type)
	throws Exception {
		try{
			List<Field> key=new ArrayList<Field>();
			key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
			key.add(new Field(LocalObjectFields.type+"",type+"",FieldType.STRING));
			int toReturn= session.deleteOperation(Tables.Objects+"", key);
			session.commit();
			return toReturn;
		}catch(Exception e ){session.connection.rollback();throw e;}
	}
	@Override
	public int changeGis(String userName, List<String> titles) throws Exception{
		try{
			List<Field> gisFields=new ArrayList<Field>();
			gisFields.add(new Field(LocalObjectFields.gis+"","",FieldType.INTEGER));

			List<Field> key=new ArrayList<Field>();
			key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
			key.add(new Field(LocalObjectFields.title+"",userName,FieldType.STRING));

			PreparedStatement psUpdate=session.getPreparedStatementForUpdate(gisFields, key, Tables.Objects+"");

			PreparedStatement psGet=session.getPreparedStatementForQuery(key, Tables.Objects+"", DBCostants.userID, Tags.ASC);

			psUpdate.setString(2, userName);
			psGet.setString(1, userName);
			int count=0;
			for(String t:titles){
				psUpdate.setString(3, t);
				psGet.setString(2, t);
				ResultSet rs= psGet.executeQuery();
				rs.next();
				int gis=rs.getInt(LocalObjectFields.gis+"");
				psUpdate.setInt(1, (gis==0)?1:0);
				count+=psUpdate.executeUpdate();
			}
			session.commit();
			return count;
		}catch(Exception e ){session.connection.rollback();throw e;}
	}


	@Override
	public int addToAreaSelection(String user, List<Area> selection)
	throws Exception {
		try{
			PreparedStatement ps= null;
			int toReturn=0;
			for(Area a : selection){			
				List<Field> row= new ArrayList<Field>();
				row.add(new Field(DBCostants.userID,user,FieldType.STRING));
				row.add(new Field(AreaFields.code+"",a.getCode(),FieldType.STRING));
				row.add(new Field(AreaFields.type+"",a.getType()+"",FieldType.STRING));
				row.add(new Field(AreaFields.name+"",a.getName(),FieldType.STRING));
				if(ps==null) ps=session.getPreparedStatementForInsert(row, Tables.AreaSelections+"");
				try{
					toReturn+=session.fillParameters(row,0, ps).executeUpdate();
				}catch(Exception e){}
			}
			session.commit();
			return toReturn;
		}catch(Exception e ){session.connection.rollback();throw e;}
	}

	@Override
	public String getJSONAreaSelection(String user,
			PagedRequestSettings settings) throws Exception {
		List<Field> filter= new ArrayList<Field>();
		filter.add(new Field(DBCostants.userID,user,FieldType.STRING));		
		return DBUtil.toJSon(session.executeFilteredQuery(filter, Tables.AreaSelections+"", settings.orderField(), settings.orderDirection()+""),
				settings.offset(), settings.limit()+settings.offset());
	}

	@Override
	public List<Area> getAreaSelection(String user) throws Exception {
		List<Field> filter= new ArrayList<Field>();
		filter.add(new Field(DBCostants.userID,user,FieldType.STRING));	
		ResultSet rs= session.executeFilteredQuery(filter, Tables.AreaSelections+"", DBCostants.userID, Tags.ASC);
		return DBUtil.loadAreas(rs);		
	}
	@Override
	public int removeFromAreaSelection(String user, List<Area> toRemove)
	throws Exception {
		try{
			if((toRemove==null)||(toRemove.size()==0)){
				List<Field> filter=new ArrayList<Field>();
				filter.add(new Field(DBCostants.userID,user,FieldType.STRING));
				int toReturn= session.deleteOperation(Tables.AreaSelections+"", filter);
				session.commit();
				return toReturn;
			}else {
				PreparedStatement ps= null;
				int toReturn=0;
				for(Area a : toRemove){			
					List<Field> row= new ArrayList<Field>();
					row.add(new Field(DBCostants.userID,user,FieldType.STRING));
					row.add(new Field(AreaFields.code+"",a.getCode(),FieldType.STRING));
					row.add(new Field(AreaFields.type+"",a.getType()+"",FieldType.STRING));
					row.add(new Field(AreaFields.name+"",a.getName(),FieldType.STRING));
					if(ps==null) ps=session.getPreparedStatementForDelete(row, Tables.AreaSelections+"");
					toReturn+=session.fillParameters(row,0, ps).executeUpdate();
				}
				session.commit();
				return toReturn;
			}
		}catch(Exception e ){session.connection.rollback();throw e;}
	}


	@Override
	public int createObjectsBySelection(List<String> ids, String title,
			ClientObjectType type, float threshold, String bbox,
			String username) throws Exception{
		PreparedStatement loadSpecies=null;
		ResultSet rsSpec=null;
		try{
			switch(type){
			case Biodiversity : 
				addObject(username, title, type, bbox, threshold,"",true);
				int toReturn= addToObjectBasket(ids, username, title);
				return toReturn;
			default : 

				if(ids==null) ids= getUserBasketIds(username);
				List<Field> speciesRow=null;
				int count=0;
				for(String specId:ids){
					speciesRow=new ArrayList<Field>();
					speciesRow.add(new Field(SpeciesFields.speciesid+"",specId,FieldType.STRING));
					if(loadSpecies==null) loadSpecies=session.getPreparedStatementForQuery(speciesRow, Tables.Species+"", SpeciesFields.speciesid+"", Tags.ASC);
					rsSpec=session.fillParameters(speciesRow,0, loadSpecies).executeQuery();
					if(rsSpec.next()){
						String currentTitle=rsSpec.getString(SpeciesFields.scientific_name+"");
						if(currentTitle==null || currentTitle.equalsIgnoreCase("")) 
							currentTitle=rsSpec.getString(SpeciesFields.genus+"")+"_"+rsSpec.getString(SpeciesFields.species+"");
						try{
							addObject(username,currentTitle,type,bbox,threshold,specId,true);
							List<String> id=new ArrayList<String>();
							id.add(specId);
							addToObjectBasket(id, username, currentTitle,false);
							count++;
						}catch(Exception ex){
							//skipping existing..
						}
					}else log.warn("Cannot find informations for species "+specId+", skipped");
				}
				session.commit();
				return count;
			}
		}catch(Exception e ){session.connection.rollback();throw e;}
		finally{
			if(rsSpec!=null) rsSpec.close();
			if (loadSpecies!=null) loadSpecies.close();
		}
	}

	@Override
	public void clearPerturbation(String speciesId, String userName)
	throws Exception {
		try{
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> key=new ArrayList<Field>();
			key.add(new Field(DBCostants.userID,userName,FieldType.STRING));
			key.add(new Field(SpeciesFields.speciesid+"",speciesId,FieldType.STRING));
			keys.add(key);
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> value=new ArrayList<Field>();
			value.add(new Field(DBCostants.perturbations,Tags.EMPTY_JSON,FieldType.STRING));
			value.add(new Field(SpeciesFields.customized+"",0+"",FieldType.INTEGER));
			values.add(value);
			session.updateOperation(Tables.Basket+"", keys, values);
			session.commit();
		}catch(Exception e ){session.connection.rollback();throw e;}
	}


	
	private int importSpeciesOccursumCSV(File csvFile) throws Exception {
		SpeciesCSVProcessor lineProcessor=null;
		try{
			CSVReaderProcessor processor=new CSVReaderProcessor();
			processor.setDelimiter(',');					
			Reader reader= new InputStreamReader(new FileInputStream(csvFile), Charset.defaultCharset());
			lineProcessor=new SpeciesCSVProcessor(session);
			log.debug("Starting csv file processing, path is "+csvFile.getAbsolutePath());
			processor.processStream(reader , lineProcessor);
			if(lineProcessor.status.equals(ResourceStatus.Completed)){
				log.debug("Complete processing");
				session.commit();
				return session.getCount(Tables.Species+"", new ArrayList<Field>());
			}else {
				throw new Exception("Processor Was Unable to completely import csv");
			}			
		}catch(Exception e){session.getConnection().rollback();throw e;}
		finally{if(lineProcessor!=null)lineProcessor.close();}
	}

}
