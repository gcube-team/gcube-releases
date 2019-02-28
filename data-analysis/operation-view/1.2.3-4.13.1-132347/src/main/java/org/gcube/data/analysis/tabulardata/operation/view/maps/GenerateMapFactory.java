package org.gcube.data.analysis.tabulardata.operation.view.maps;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.exceptions.NoSuchColumnException;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.scopes.TableScopedWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.BooleanParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Singleton
public class GenerateMapFactory extends TableScopedWorkerFactory<ResourceCreatorWorker> {

	private static final OperationId OPERATION_ID = new OperationId(1010);
	private static Logger logger = LoggerFactory.getLogger(GenerateMapFactory.class);
	private CubeManager cubeManager;
	private DatabaseConnectionProvider connProvider;

	@Inject
	public GenerateMapFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connProvider) {
		super();
		this.cubeManager = cubeManager;
		this.connProvider = connProvider;
	}

	@Override
	public ResourceCreatorWorker createWorker(OperationInvocation arg0)
			throws InvalidInvocationException {
		performBaseChecks(arg0, cubeManager);
		checkParameters(arg0);
		GeoPublishingConfiguration geoConfig=null;
		try{			
			geoConfig=GeoPublishingConfiguration.get();
		}catch(Exception e){			
			logger.error("Wrong GIS environement (Scope : "+ScopeProvider.instance.get()+")",e);
			throw new InvalidInvocationException(arg0,"Environment doesn't support GIS features");
		}
		return new GenerateMapWorker(arg0, cubeManager, connProvider, geoConfig);
	}


	private void checkParameters(OperationInvocation arg0)throws InvalidInvocationException{
		Table targetTable=cubeManager.getTable(arg0.getTargetTableId());

		boolean useView=OperationHelper.getParameter(GenerateMapFactory.useView, arg0);
		if(useView && targetTable.contains(DatasetViewTableMetadata.class)){
			DatasetViewTableMetadata dsMeta = targetTable.getMetadata(DatasetViewTableMetadata.class);
			targetTable = cubeManager.getTable(dsMeta.getTargetDatasetViewTableId());
		}

		if(!arg0.getParameterInstances().containsKey(toUseGeometry.getIdentifier())){
			// detecting geometry
			boolean foundGeom=false;
			for(Column col:targetTable.getColumnsExceptTypes(IdColumnType.class,ValidationColumnType.class)){
				if(col.getDataType() instanceof GeometryType){
					if(!foundGeom) foundGeom=true;
					else 											
						throw new InvalidInvocationException(arg0, "Multiple geometry columns found in current table");
				}
			}
			if(!foundGeom) throw new InvalidInvocationException(arg0, "No Geometry column found");
		}else{
			//check selected geometry
			ColumnReference geomRef=OperationHelper.getParameter(toUseGeometry, arg0);
			Column geom=targetTable.getColumnById(geomRef.getColumnId());
			if(!(geom.getDataType() instanceof GeometryType)) throw new InvalidInvocationException(arg0, "Invalid selected geometry column");
		}
		// all column references must belong to targetTable 
		Object toRepresentColumn=arg0.getParameterInstances().get(GenerateMapFactory.toCreateFeatureTypes.getIdentifier());
		if(toRepresentColumn instanceof ColumnReference){			
			try{
				targetTable.getColumnById(((ColumnReference)toRepresentColumn).getColumnId());
			}catch(NoSuchColumnException e){
				throw new InvalidInvocationException(arg0, "Wrong parameter "+GenerateMapFactory.toCreateFeatureTypes.getName()+".Column not found");
			}
			
		}else
			for(ColumnReference ref:(Iterable<ColumnReference>)toRepresentColumn)
				try{
					targetTable.getColumnById(ref.getColumnId());
				}catch(NoSuchColumnException e){
					throw new InvalidInvocationException(arg0, "Wrong parameter "+GenerateMapFactory.toCreateFeatureTypes.getName()+".Column not found");
				}

	}

	// PARAMETER DEFINITION

	public static SimpleStringParameter mapName=new SimpleStringParameter("mapName", "Map Name", "Name by which publish the map.", Cardinality.ONE);
	public static TargetColumnParameter toCreateFeatureTypes=new TargetColumnParameter("feature", "Feature", "To create feature", new Cardinality(1, Integer.MAX_VALUE));
	public static final BooleanParameter useView=new BooleanParameter("useView", "Use View", "Use view columns values instead of dimensions", Cardinality.ONE);
	public static TargetColumnParameter toUseGeometry=new TargetColumnParameter("geom", "Geometry", "To use Geometry", Cardinality.OPTIONAL);
	// METADATA Parameters

	public static SimpleStringParameter metaAbstract=new SimpleStringParameter("metaAbstract","Abstract","Abstract to publish the layer with",Cardinality.ONE);
	public static SimpleStringParameter metaPurpose=new SimpleStringParameter("metaPurpose","Purpose","Purpose of the layer",Cardinality.ONE);

	/*mandatory*/ public static SimpleStringParameter user=new SimpleStringParameter("User","User","Author of the map",Cardinality.ONE);

	public static SimpleStringParameter metaCredits=new SimpleStringParameter("metaCredits","Credits","Credits",Cardinality.ONE);
	public static SimpleStringParameter	keywords=new SimpleStringParameter("metaKeywords", "Metadata keywords", "Keywords to associate to this layer", new Cardinality(0,Integer.MAX_VALUE));

	private static final List<Parameter> params=Arrays.asList((Parameter)mapName,toUseGeometry,toCreateFeatureTypes,useView,metaAbstract,metaPurpose,user,metaCredits,keywords);

	
	@Override
	public Class<ResourceCreatorWorker> getWorkerType() {
		return ResourceCreatorWorker.class;
	}

	@Override
	protected String getOperationDescription() {
		return "Generate a GIS layer from the table.";
	}

	@Override
	protected String getOperationName() {
		return "Generate Map";
	}

	@Override
	protected OperationType getOperationType() {
		return OperationType.RESOURCECREATOR;
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}

	@Override
	protected List<Parameter> getParameters() {
		return params;
	}

	@Override
	public String describeInvocation(OperationInvocation toDescribeInvocation)
			throws InvalidInvocationException {
		performBaseChecks(toDescribeInvocation, cubeManager);
		checkParameters(toDescribeInvocation);
		Table target=cubeManager.getTable(toDescribeInvocation.getTargetTableId());
		String features=OperationHelper.getColumnLabelsSnippet(getSelectedFeatureTypes(toDescribeInvocation, target,cubeManager));
		return "Generate "+features+" GIS features";
	}


	static List<Column> getSelectedFeatureTypes(OperationInvocation arg0,Table targetTable,CubeManager cubeManager){
		boolean useView=OperationHelper.getParameter(GenerateMapFactory.useView, arg0);
		if(useView && targetTable.contains(DatasetViewTableMetadata.class)){
			DatasetViewTableMetadata dsMeta = targetTable.getMetadata(DatasetViewTableMetadata.class);
			targetTable = cubeManager.getTable(dsMeta.getTargetDatasetViewTableId());
		}
		
		
		ArrayList<Column> toReturn=new ArrayList<>();
		Object toRepresentColumn=arg0.getParameterInstances().get(GenerateMapFactory.toCreateFeatureTypes.getIdentifier());
		if(toRepresentColumn instanceof ColumnReference)
			toReturn.add(getColumn((ColumnReference)toRepresentColumn, targetTable, useView));
		else
			for(ColumnReference ref:(Iterable<ColumnReference>)toRepresentColumn)
				toReturn.add(getColumn(ref, targetTable, useView));

		return toReturn;
	}
	
	private static Column getColumn(ColumnReference ref,Table table,boolean useView){
		Column col=table.getColumnById(ref.getColumnId());
		if(useView && col.getColumnType() instanceof DimensionColumnType){
			ColumnLocalId referredId=col.getRelationship().getTargetColumnId();
			return table.getColumnById(referredId);
		}else return col;
	}
	
	public static org.gcube.spatial.data.gis.symbology.GeometryType getGeometryType(Table table,Column geometryColumn,DatabaseConnectionProvider connProvider)throws SQLException,NotSupportedGeometryShapeException{
		Connection conn=null;
		Statement stmt=null;
		ResultSet rs=null;
		try{
			conn=connProvider.getConnection();
			stmt=conn.createStatement();
			rs=stmt.executeQuery(String.format("SELECT %s from %s limit 1", geometryColumn.getName(),table.getName()));
			rs.next();
			PGgeometry geom=(PGgeometry) rs.getObject(1);
			int geoType=geom.getGeoType();
			if(geoType==Geometry.POINT) return org.gcube.spatial.data.gis.symbology.GeometryType.POINT;
			else if(geoType==Geometry.POLYGON) return org.gcube.spatial.data.gis.symbology.GeometryType.POLYGON;
			else throw new NotSupportedGeometryShapeException("Found geometry type "+Geometry.getTypeString(geoType));
		}finally{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}
}
