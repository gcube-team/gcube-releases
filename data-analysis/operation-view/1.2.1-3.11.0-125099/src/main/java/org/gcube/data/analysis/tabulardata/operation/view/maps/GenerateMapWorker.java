package org.gcube.data.analysis.tabulardata.operation.view.maps;

import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;

import java.awt.Color;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.leaf.Range;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.mapping.PostgreSQLModelMapper;
import org.gcube.data.analysis.tabulardata.model.mapping.SQLModelMapper;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.iso.GcubeISOMetadata;
import org.gcube.spatial.data.geonetwork.iso.Thesaurus;
import org.gcube.spatial.data.gis.GISInterface;
import org.gcube.spatial.data.gis.is.GeoServerDescriptor;
import org.gcube.spatial.data.gis.model.report.PublishResponse;
import org.gcube.spatial.data.gis.model.report.Report;
import org.gcube.spatial.data.gis.symbology.ClassStyleDef;
import org.gcube.spatial.data.gis.symbology.StyleUtils;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.spatial.GeometricObjectType;
import org.opengis.metadata.spatial.TopologyLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class GenerateMapWorker extends ResourceCreatorWorker{


	private static Logger logger = LoggerFactory.getLogger(GenerateMapWorker.class);

	private static SQLModelMapper sqlModelMapper = new PostgreSQLModelMapper();
	
	
	private CubeManager cubeManager;
	private DatabaseConnectionProvider connProvider;
	private GISInterface gisInterface;
	private GeoPublishingConfiguration geoConfig;

	
	
	public GenerateMapWorker(OperationInvocation sourceInvocation,
			CubeManager cubeManager, DatabaseConnectionProvider connProvider,
			GeoPublishingConfiguration geoConfig) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connProvider = connProvider;
		this.geoConfig = geoConfig;
		this.gisInterface=geoConfig.getGis();
	}

	private Table targetTable;
	private Column geometryColumn;
	private org.gcube.spatial.data.gis.symbology.GeometryType geometryType;
	
	private String layerTitle;
	private List<String> publishedStyles=new ArrayList<>();
	private List<Column> toCreateFeatures=new ArrayList<>();
	private String remoteTable=null;


	private Map<ColumnLocalId,String> fixedColumnLabels;
	
	
	private PublishResponse publishResponse;

	// Metadata parameters
	private String metaAbstract="Layer generated via Tabular Data Management Service.";
	private String metaPurpose="The layer has been generated in order to exploit TDM features";
	private String user;
	private String metaCredits="The map has been generated via gCube infrastructure.";
	private List<String> metaKeywords=new ArrayList<>();


	@Override
	protected ResourcesResult execute() throws WorkerException,OperationAbortedException {
		boolean removeTable=false;
		boolean removeStyles=false;
		boolean removeLayer=false;
		try{			
			updateProgress(0.1f, "Initializing");			
			initParameters();
			updateProgress(0.2f, "Creating table on remote GIS DB");
			checkAborted();
			transferTable();
			removeTable=true;
			updateProgress(0.7f, "Creating style(s)");
			checkAborted();
			createStyles();
			removeStyles=true;
			updateProgress(0.8f, "Publishing layer");
			checkAborted();
			createLayer();
			if(publishResponse.getDataOperationResult().equals(Report.OperationState.ERROR)) {
				logger.error("Unable to publish layer "+publishResponse);
				throw new WorkerException("Unable to create feature type");
			}
			removeLayer=true;
			if(publishResponse.getMetaOperationResult().equals(Report.OperationState.ERROR)){
				logger.error("Unable to publish layer metadata "+publishResponse);
				throw new WorkerException("Unable to create layer metadata");
			}
			updateProgress(0.9f,"Finalizing");
			String metaUUID=publishResponse.getPublishedMetadata().getFileIdentifier();
			return new ResourcesResult(new ImmutableURIResult(new InternalURI(new URI(metaUUID)), layerTitle+" layer", "GIS representation of this TR.", ResourceType.MAP));
		}catch(WorkerException e){
			cleanup(removeTable, removeStyles, removeLayer);
			throw e;
		}catch(OperationAbortedException e){
			cleanup(removeTable, removeStyles, removeLayer);
			throw e;
		}catch (Throwable t){
			cleanup(removeTable, removeStyles, removeLayer);
			logger.error("Unexpected exception",t);
			logger.error("Current Geoserver : "+gisInterface.getCurrentGeoServerDescriptor());
			try{
				logger.error("Current Geonetwork : "+gisInterface.getGeoNetworkReader().getConfiguration());
			}catch(Exception e){
				// nothing to do
			}
			logger.error("Current configuration : "+geoConfig);
			throw new WorkerException(t.getMessage(),t);
		}
	}



	private void initParameters() throws WorkerException{
		targetTable=cubeManager.getTable(getSourceInvocation().getTargetTableId());
		boolean useView=OperationHelper.getParameter(GenerateMapFactory.useView, getSourceInvocation());
		if(useView && targetTable.contains(DatasetViewTableMetadata.class)){
			DatasetViewTableMetadata dsMeta = targetTable.getMetadata(DatasetViewTableMetadata.class);
			targetTable = cubeManager.getTable(dsMeta.getTargetDatasetViewTableId());
		}
		
		fixedColumnLabels=curateLabels(targetTable);
		

		Map<String,Object> params=getSourceInvocation().getParameterInstances();

		layerTitle=OperationHelper.getParameter(GenerateMapFactory.mapName, getSourceInvocation());


		toCreateFeatures.addAll(GenerateMapFactory.getSelectedFeatureTypes(getSourceInvocation(), 
				cubeManager.getTable(getSourceInvocation().getTargetTableId()),cubeManager)); // needs the original target table

		
		if(params.containsKey(GenerateMapFactory.toUseGeometry.getIdentifier())){
			geometryColumn=targetTable.getColumnById(OperationHelper.getParameter(GenerateMapFactory.toUseGeometry, getSourceInvocation()).getColumnId());
		}else{
			//auto detect geometry
			for(Column col:targetTable.getColumnsExceptTypes(IdColumnType.class,ValidationColumnType.class))
				if(col.getDataType() instanceof GeometryType){
					geometryColumn=col;
					break;
				}
		}
		
		try {
			geometryType=GenerateMapFactory.getGeometryType(targetTable, geometryColumn, connProvider);
		} catch (SQLException e) {
			throw new WorkerException("Unable to detect geometry type");
		} catch(NotSupportedGeometryShapeException e){
			throw new WorkerException(e.getMessage());
		}
		//metadata
		metaAbstract=OperationHelper.getParameter(GenerateMapFactory.metaAbstract, getSourceInvocation());
		metaPurpose=OperationHelper.getParameter(GenerateMapFactory.metaPurpose, getSourceInvocation());
		user=OperationHelper.getParameter(GenerateMapFactory.user, getSourceInvocation());
		metaCredits=OperationHelper.getParameter(GenerateMapFactory.metaCredits, getSourceInvocation());
		if(params.containsKey(GenerateMapFactory.keywords.getIdentifier())){
			Object toAddKeys=params.get(GenerateMapFactory.keywords.getIdentifier());
			if(toAddKeys instanceof String)metaKeywords.add((String) toAddKeys);
			else for(String key:(Iterable<String>)toAddKeys)
				metaKeywords.add(key);			
		}
		metaKeywords.addAll(geoConfig.getParams().getKeywords());
	}


	private void transferTable() throws WorkerException{
		Connection localConn=null;
		Statement localStmt=null;
		ResultSet rs=null;
		Connection postgisConn=null;
		Statement postgisStmt=null;
		PreparedStatement psInsert=null;
		try{
			// create remote table
			String newTableName=randomizeString("tdm");
			String createTableStmt=getCreateTableStmt(newTableName, targetTable,geoConfig.getParams().getGeometryFieldName(),geometryColumn,fixedColumnLabels);
			
			postgisConn=DriverManager.getConnection(geoConfig.getPostgisUrl(), 
					geoConfig.getPostgisUser(), geoConfig.getPostgisPwd());
			postgisConn.setAutoCommit(false);
			postgisStmt=postgisConn.createStatement();
			logger.debug("Create table command : "+createTableStmt);
			postgisStmt.execute(createTableStmt);
			
			localConn = connProvider.getConnection();
			localStmt= localConn.createStatement();			
			// iterate over and copy target table rows
			List<Column> cols=targetTable.getColumnsExceptTypes(IdColumnType.class,ValidationColumnType.class);
			rs=localStmt.executeQuery(
					String.format("Select %s from %s",OperationHelper.getColumnNamesSnippet(cols),targetTable.getName()));
			String insertStmt=getInsertStmt(newTableName, targetTable,geoConfig.getParams().getGeometryFieldName(),geometryColumn,fixedColumnLabels);
			logger.debug("Insert stmt : "+insertStmt);
			psInsert=postgisConn.prepareStatement(insertStmt);
			
			
			while(rs.next()){
				for(int i=0;i<cols.size();i++) // copy values from rs
					psInsert.setObject(i+1, rs.getObject(i+1));					
				psInsert.executeUpdate();
			}
			postgisConn.commit();
			remoteTable=newTableName;
		}catch(Exception e){
			logger.error("Unable to transfer table, configuration is "+geoConfig,e);
			throw new WorkerException("Unable to copy table to Geoserver's DB");
		}finally{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(localStmt);
			DbUtils.closeQuietly(localConn);			
			DbUtils.closeQuietly(postgisStmt);
			DbUtils.closeQuietly(psInsert);
			DbUtils.closeQuietly(postgisConn);
		}
		
	}

	private void createStyles() throws WorkerException{
		for(Column col:toCreateFeatures){
			try{
				String styleName=randomizeString(OperationHelper.retrieveColumnLabel(col)).replaceAll("[^a-zA-Z0-9]", "_");
				String attribute=fixedColumnLabels.get(col.getLocalId());
				Color c1=Color.red;
				Color c2=Color.yellow;
				String sld=null;			
				if(col.getDataType() instanceof NumericType){
					Range range=getMinMax(col);
					Float min=((TDNumeric)range.getMinimum()).getValue().floatValue();
					Float max=((TDNumeric)range.getMaximum()).getValue().floatValue();
					sld=StyleUtils.createStyle(styleName, attribute, 5, c1, c2, Float.class, max, min,geometryType);
				}else if (col.getDataType() instanceof IntegerType){
					Range range=getMinMax(col);
					Integer min=((TDInteger)range.getMinimum()).getValue();
					Integer max=((TDInteger)range.getMaximum()).getValue();
					sld=StyleUtils.createStyle(styleName, attribute, 5, c1, c2, Integer.class, max, min,geometryType);
				}else {
					ArrayList<ClassStyleDef> classes=getDiscreteClasses(col);
					sld=StyleUtils.createStyle(styleName, attribute, classes, c1, c2,geometryType);
				}
				PublishResponse resp=gisInterface.publishStyle(sld, styleName);
				if (resp.getDataOperationResult().equals(Report.OperationState.COMPLETE)){
					publishedStyles.add(styleName);
				}else throw new Exception("Error while publishing style : "+resp.getDataOperationMessages());
			}catch(Exception e){
				logger.debug("Error while generating styles.",e);
			}
		}
		if(publishedStyles.size()==0) throw new WorkerException("No styles generated");
	}

	private void createLayer() throws WorkerException{
		try{
			GSFeatureTypeEncoder fte=new GSFeatureTypeEncoder();
			fte.setEnabled(true);
			fte.setLatLonBoundingBox(-180.0, -90.0, 180.0, 90.0, geoConfig.getParams().getCrs());
			fte.setName(remoteTable);
			fte.setNativeCRS(geoConfig.getParams().getCrs());



			GSLayerEncoder le=new GSLayerEncoder();
			le.setDefaultStyle(publishedStyles.get(0));		
			le.setEnabled(true);

			GcubeISOMetadata meta=fillMeta();

			publishResponse=gisInterface.publishDBTable(
					geoConfig.getParams().getWorkspace(), geoConfig.getParams().getDatastore(), fte, le, meta.getMetadata(), 
					new GNInsertConfiguration(
							gisInterface.getGeoNetworkReader().getConfiguration().getScopeGroup()+"", 
							geoConfig.getParams().getgNCategory(), geoConfig.getParams().getgNStyleSheet(), true), 
							LoginLevel.DEFAULT);
		}catch(Exception e){
			throw new WorkerException("Unable to publish GIS Resources",e);
		}
	}


	private GcubeISOMetadata fillMeta() throws Exception{ 
		// TODO define
		GcubeISOMetadata meta=new GcubeISOMetadata();

		meta.setAbstractField(metaAbstract);
		meta.setCreationDate(new Date(System.currentTimeMillis()));
		meta.setExtent((DefaultExtent) DefaultExtent.WORLD);
		meta.setGeometricObjectType(GeometricObjectType.SURFACE);
		meta.setPresentationForm(PresentationForm.MAP_DIGITAL);
		meta.setPurpose(metaPurpose);
		//		meta.setResolution(0.5d);
		meta.setTitle(layerTitle);
		meta.setTopologyLevel(TopologyLevel.GEOMETRY_ONLY);
		meta.setUser(user);		


		meta.addCredits(metaCredits);

		Thesaurus generalThesaurus=meta.getConfig().getThesauri().get("General");
		for(String keyword:metaKeywords)
			meta.addKeyword(keyword, generalThesaurus);

		meta.addTopicCategory(TopicCategory.BIOTA);
		return meta;
	}

	private Range getMinMax(Column toCheckColumn) throws SQLException{
		Connection conn=null;
		Statement stmt=null;
		try{
			conn = connProvider.getConnection();
			stmt= conn.createStatement();			
			ResultSet rs= stmt.executeQuery(
					String.format("SELECT max(%1$s) as max,min(%1$s) as min from %2$s",
							toCheckColumn.getName(),targetTable.getName()));
			rs.next();
			if(toCheckColumn.getDataType() instanceof NumericType)
				return new Range(new TDNumeric(rs.getFloat("min")),new TDNumeric(rs.getFloat("max")));
			else return new Range(new TDInteger(rs.getInt("min")), new TDInteger(rs.getInt("max")));
		}finally{
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}

	private ArrayList<ClassStyleDef> getDiscreteClasses(Column toCheckColumn) throws SQLException{
		Connection conn=null;
		Statement stmt=null;
		try{
			ArrayList<ClassStyleDef> toReturn= new ArrayList<>();
			conn = connProvider.getConnection();
			stmt= conn.createStatement();			
			ResultSet rs= stmt.executeQuery(
					String.format("SELECT distinct(%s) as values from %s",
							toCheckColumn.getName(),targetTable.getName()));
			while(rs.next())
				toReturn.add(new ClassStyleDef(rs.getString(1)));

			return toReturn;
		}finally{
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}

	private void cleanup(boolean removeTable,boolean removeStyles, boolean removeLayer){
		if(removeTable){
			Connection conn=null;
			try{
				conn=DriverManager.getConnection(geoConfig.getPostgisUrl(), 
						geoConfig.getPostgisUser(), geoConfig.getPostgisPwd());
				conn.createStatement().execute("DROP "+remoteTable);
			}catch(Exception e){
				logger.error("Unable to delete table "+remoteTable,e);				
			}finally{
				DbUtils.closeQuietly(conn);
			}
			
			
			if(removeStyles){
				GeoServerDescriptor currentDesc=gisInterface.getCurrentGeoServerDescriptor();
				for(String style:publishedStyles)
					gisInterface.deleteStyle(style, currentDesc);
				
				if(removeLayer){					
					gisInterface.deleteLayer(geoConfig.getParams().getWorkspace(), remoteTable, null, currentDesc, LoginLevel.DEFAULT);
				}				
			}			
		}
	}
	
	
	private static String randomizeString(String prefix){
		return prefix+UUID.randomUUID().toString().replace("-", "");
	}
	
	
	
	
	
	private static String getCreateTableStmt(String newTableName,Table t,String geomName,Column geometryCol,Map<ColumnLocalId,String> toUseLabels){		
		StringBuilder toReturn=new StringBuilder("CREATE TABLE "+newTableName+" (");
		for(Column col:t.getColumnsExceptTypes(IdColumnType.class,ValidationColumnType.class)){
			// geometry must be called the_geom for compatibility with Style making logic
			String toSetColName=(col.getLocalId().equals(geometryCol.getLocalId()))?geomName:toUseLabels.get(col.getLocalId());
			String dataDefinition=(col.getDataType() instanceof GeometryType)?"geometry":
				sqlModelMapper.translateDataTypeToSQL(col.getDataType());
			
			toReturn.append(String.format("%s %s,", toSetColName,dataDefinition));
			
		}
		toReturn.append(")");
		toReturn.deleteCharAt(toReturn.lastIndexOf(","));
		return toReturn.toString();
	}
	
	private static String getInsertStmt(String tableName,Table t,String geomName,Column geometryCol,Map<ColumnLocalId,String> toUseLabels){
		StringBuilder toReturn=new StringBuilder("INSERT INTO "+tableName+" (");
		StringBuilder valuePlaceholders=new StringBuilder(" VALUES (");
		for(Column col:t.getColumnsExceptTypes(IdColumnType.class,ValidationColumnType.class)){
			// geometry must be called the_geom for compatibility with Style making logic
			toReturn.append(col.getLocalId().equals(geometryCol.getLocalId())?geomName:toUseLabels.get(col.getLocalId()));
			toReturn.append(",");
			valuePlaceholders.append("?,");			
		}
		toReturn.append(")");
		toReturn.deleteCharAt(toReturn.lastIndexOf(","));
		valuePlaceholders.deleteCharAt(valuePlaceholders.lastIndexOf(","));
		return toReturn.toString()+valuePlaceholders.toString()+")";
	}
	
	private static Map<ColumnLocalId,String> curateLabels(Table table){
		HashMap<ColumnLocalId,String> toReturn=new HashMap<ColumnLocalId,String>();
		HashMap<String,Integer> clashCounter=new HashMap<String,Integer>();
		for(Column col:table.getColumnsExceptTypes(IdColumnType.class,ValidationColumnType.class)){
			String originalLabel=OperationHelper.retrieveColumnLabel(col);
			String fixed=originalLabel.replaceAll("\\W", "_").toLowerCase();
			if(clashCounter.containsKey(fixed)){
				clashCounter.put(fixed, clashCounter.get(fixed)+1);
				fixed=fixed+"_"+clashCounter.get(fixed);
			}else clashCounter.put(fixed, 1);
			toReturn.put(col.getLocalId(), fixed);
		}
		return toReturn;
	}
}
