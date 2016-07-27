package org.gcube.application.aquamaps.aquamapsspeciesview.servlet.db;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.sf.csv4j.CSVReaderProcessor;

import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportOperation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.CompoundMapItem;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsspeciesview.servlet.utils.Utils;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBManager implements DBInterface{


	private static final Logger log = LoggerFactory.getLogger(DBManager.class);




	protected DBSession session;

	private String associatedScope;


	private long lastUpdateTime;


	public static Set<String> getInitializedScopes(){
		return ConnectionProvider.initializedScopes();
	}

	public static void deleteDb(String currentScope)throws Exception{
		log.debug("Delete dbinterface for scope "+currentScope);
		String scope=Utils.removeVRE(currentScope);		
		ConnectionProvider.dropDataBase(scope);
	}


	public static DBInterface getInstance(String currentScopeString) throws Exception
	{
		log.debug("getDBInterface for scope : "+currentScopeString);
		String scope=Utils.removeVRE(currentScopeString);		
		return new DBManager(scope);
	}

	protected DBManager(String scope) throws Exception {
		associatedScope=scope;
		session=new DBSession(scope);
		log.debug("Checking DB connection ...");
		session.executeQuery("SELECT * from "+Tables.kingdom);
		retrieveSpeciesFields();
//		fetchSpecies();
		//		speciesInsertionQuery=speciesInsertQuery();
		//		speciesUpdateQuery=speciesUpdateQuery();
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
	public String getMaps(String mapRequestId, PagedRequestSettings settings)
			throws Exception {
		ResultSet rs=null;
		try{
			log.debug("Getting maps by request id  ("+mapRequestId+")");
			List<Field> key=new ArrayList<Field>();
			key.add(new Field(DBCostants.MAPS_request_id,mapRequestId,FieldType.STRING));
			rs=session.executeFilteredQuery(key, Tables.MAPS+"", settings.orderField(), settings.orderDirection()+"");
			return DBUtil.toJSon(rs,settings.offset(),settings.offset()+settings.limit());
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
	public Integer putMaps(String mapRequestId, List<CompoundMapItem> items)
			throws Exception {
		PreparedStatement ps=null;
		try{
			int count=0;
			for(CompoundMapItem item:items){
				List<Field> values=new ArrayList<Field>();
				values.add(new Field(CompoundMapItem.ALGORITHM,item.getAlgorithm(),FieldType.STRING));
				values.add(new Field(CompoundMapItem.AUTHOR,item.getAuthor(),FieldType.STRING));
				values.add(new Field(CompoundMapItem.COVERAGE,item.getCoverage(),FieldType.STRING));
				values.add(new Field(CompoundMapItem.CREATION_DATE,item.getCreationDate()+"",FieldType.LONG));
				values.add(new Field(CompoundMapItem.DATA_GENERATION_TIME,item.getDataGenerationTime()+"",FieldType.LONG));
				values.add(new Field(CompoundMapItem.FILESET_ID,item.getFileSetId(),FieldType.STRING));
				values.add(new Field(CompoundMapItem.GIS,item.isGis()+"",FieldType.BOOLEAN));
				values.add(new Field(CompoundMapItem.IMAGE_COUNT,item.getImageCount()+"",FieldType.INTEGER));
				values.add(new Field(CompoundMapItem.IMAGE_LIST,item.getImageList(),FieldType.STRING));
				values.add(new Field(CompoundMapItem.LAYER_ID,item.getLayerId(),FieldType.STRING));
				values.add(new Field(CompoundMapItem.LAYER_PREVIEW,item.getLayerPreview(),FieldType.STRING));
				values.add(new Field(CompoundMapItem.LAYER_URL,item.getLayerUrl(),FieldType.STRING));
				values.add(new Field(CompoundMapItem.RESOURCE_ID,item.getResourceId()+"",FieldType.INTEGER));
				values.add(new Field(CompoundMapItem.SPECIES_LIST,item.getSpeciesList(),FieldType.STRING));
				values.add(new Field(CompoundMapItem.THUMBNAIL,item.getImageThumbNail(),FieldType.STRING));
				values.add(new Field(CompoundMapItem.TITLE,item.getTitle(),FieldType.STRING));
				values.add(new Field(CompoundMapItem.TYPE,item.getType(),FieldType.STRING));
				values.add(new Field(CompoundMapItem.CUSTOM,item.isCustom()+"",FieldType.BOOLEAN));
				values.add(new Field(DBCostants.MAPS_request_id,mapRequestId,FieldType.STRING));
				if(ps==null) ps=session.getPreparedStatementForInsert(values, Tables.MAPS+"");
				try{
					count+=session.fillParameters(values,0, ps).executeUpdate();
				}catch(Exception e){
					e.printStackTrace();
					log.warn("Unable to insert map item "+item,e);
				}
			}
			session.commit();
			return count;
		}catch(Exception e){session.getConnection().rollback();throw e;}
		finally{if(ps!=null)ps.close();}
	}

	@Override
	public void cleanMaps(String requestId) throws Exception {
		try{
			List<Field> row=new ArrayList<Field>();
			row.add(new Field(DBCostants.MAPS_request_id,requestId,FieldType.STRING));
			session.deleteOperation(Tables.MAPS+"", row);
			session.commit();
		}catch(Exception e ){session.connection.rollback();throw e;}
	}

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
	public void fetchSpecies() throws RemoteException, Exception{
		File csvFile=null;
		try{
			ScopeProvider.instance.set(associatedScope.toString());
			csvFile=dataManagement().withTimeout(2, TimeUnit.MINUTES).build().exportTableAsCSV("speciesoccursum",null,null,null,ExportOperation.TRANSFER);
			CSVReaderProcessor processor=new CSVReaderProcessor();
			processor.setDelimiter(',');
			processor.setHasHeader(true);
			Reader reader= new InputStreamReader(new FileInputStream(csvFile), Charset.defaultCharset());

			SpeciesCSVProcessor lineProcessor=new SpeciesCSVProcessor(session);

			log.debug("Starting file processing");
			processor.processStream(reader , lineProcessor);
			log.debug("Complete processing");
			setLastUpdateTime(System.currentTimeMillis());
		}finally{
			try{if(csvFile!=null&&csvFile.exists())FileUtils.forceDelete(csvFile);}
			catch(Exception e){log.error("Unable to delete csv File "+csvFile.getAbsolutePath());}
		}
	}

}
