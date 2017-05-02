package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Cell;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HCAF_SFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.gcube_system.namespaces.application.aquamaps.types.PagedRequestSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CellManager {

	public static String HCAF_S="hcaf_s";

	final static Logger logger= LoggerFactory.getLogger(CellManager.class);


	public static Set<Cell> getCells(List<Field> filters, boolean fetchGoodCells, String speciesID, boolean fetchEnvironment, int HCAFId) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			logger.trace("Loading cells by condition..");
			Set<Cell> toReturn=Cell.loadRS(session.executeFilteredQuery(filters, HCAF_S,null,null));
			logger.trace("Found "+toReturn.size()+" cells");
			if(fetchEnvironment)
				toReturn=loadEnvironmentData(HCAFId, toReturn);

			if(fetchGoodCells)				
				toReturn=loadGoodCellsData(speciesID, toReturn);				

			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}


	public static String getJSONCells(PagedRequestSettings settings) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return DBUtils.toJSon(session.executeFilteredQuery(new ArrayList<Field>(), HCAF_S, settings.getOrderField(), settings.getOrderDirection()), settings.getOffset(), settings.getLimit()+settings.getOffset());
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}

	public static String getJSONOccurrenceCells(String speciesId, PagedRequestSettings settings) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter=new ArrayList<Field>();
			filter.add(new Field(SpeciesOccursumFields.speciesid+"",speciesId,FieldType.STRING));
			String HCAF=SourceManager.getSourceName(SourceManager.getDefaultId(ResourceType.HCAF));
			String occurrenceCells=SourceManager.getById(SourceManager.getDefaultId(ResourceType.OCCURRENCECELLS)).getTableName();
			PreparedStatement ps= session.preparedStatement("SELECT * FROM "+occurrenceCells+" as o INNER JOIN "+HCAF+" as h ON " +
					"o."+HCAF_SFields.csquarecode+" = h."+HCAF_SFields.csquarecode+" WHERE o."+SpeciesOccursumFields.speciesid+" = ?");
			return DBUtils.toJSon(session.fillParameters(filter,0, ps).executeQuery(), settings.getOffset(), settings.getLimit()+settings.getOffset());
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}

	public static Set<Cell> getCellsByIds(boolean fetchGoodCells,String speciesID, boolean fetchEnvironment,int HcafId, String[] items) throws Exception{
		DBSession session=null;
		try{
			logger.trace("loading cells by ids...( expected : "+items.length+")");
			session=DBSession.getInternalDBSession();
			Set<Cell> toReturn= new HashSet<Cell>();
			PreparedStatement ps=null;
			for(String code: items){
				List<Field> field= new ArrayList<Field>();
				field.add(new Field(HCAF_SFields.csquarecode+"",code,FieldType.STRING));
				if(ps==null) ps=session.getPreparedStatementForQuery(field, HCAF_S, HCAF_SFields.csquarecode+"", OrderDirection.ASC);
				ResultSet rs=session.fillParameters(field,0, ps).executeQuery();
				if(rs.next())				
					toReturn.add(new Cell(Field.loadRow(rs)));
				else logger.warn("Row not found for cell "+code);
			}
			logger.trace("found "+toReturn.size()+" cells");
			if(fetchEnvironment)
				toReturn=loadEnvironmentData(HcafId, toReturn);

			if(fetchGoodCells)				
				toReturn=loadGoodCellsData(speciesID, toReturn);	
			return toReturn;

		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}


	private static Set<Cell> loadEnvironmentData(int HCAFId, Set<Cell> toUpdate)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();			
			String HCAFName=SourceManager.getSourceName(HCAFId);
			logger.trace("loading environemental data from table "+HCAFName+"("+HCAFId+") for" +toUpdate.size()+" cells" );
			PreparedStatement ps=null;			
			for(Cell c: toUpdate){
				List<Field> filter=new ArrayList<Field>();
				filter.add(new Field(HCAF_SFields.csquarecode+"",c.getCode(),FieldType.STRING));
				if(ps==null)ps=session.getPreparedStatementForQuery(filter, HCAFName,HCAF_SFields.csquarecode+"",OrderDirection.ASC);
				ResultSet rs=session.fillParameters(filter,0, ps).executeQuery();
				if(rs.next())				
					c.getAttributesList().addAll(Field.loadRow(rs));
				else logger.warn("Row not found for cell "+c.getCode());

			}
			return toUpdate;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}

	private static Set<Cell> loadGoodCellsData(String speciesID, Set<Cell> toUpdate)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			logger.trace("loading good Cells data for species "+speciesID+" for" +toUpdate.size()+" cells" );
			PreparedStatement ps= null;
			for(Cell c: toUpdate){
				List<Field> filter=new ArrayList<Field>();
				filter.add(new Field(HCAF_SFields.csquarecode+"",c.getCode(),FieldType.STRING));
				filter.add(new Field(SpeciesOccursumFields.speciesid+"",speciesID,FieldType.STRING));
				String occurrenceCells=SourceManager.getById(SourceManager.getDefaultId(ResourceType.OCCURRENCECELLS)).getTableName();
				if(ps==null)ps=session.getPreparedStatementForQuery(filter, occurrenceCells,HCAF_SFields.csquarecode+"",OrderDirection.ASC);
				ResultSet rs=session.fillParameters(filter,0, ps).executeQuery();
				if(rs.next())				
					c.getAttributesList().addAll(Field.loadRow(rs));
				else logger.warn("Row not found for cell "+c.getCode());
			}
			return toUpdate;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}

	public static Set<Cell> calculateGoodCells(BoundingBox bb, List<Area> areas, String speciesID, int hcafId)throws Exception{
		DBSession session=null;
		try{
			logger.trace("Loading good cells for species "+speciesID);
			session=DBSession.getInternalDBSession();
			List<Field> cellFilter=new ArrayList<Field>();
			cellFilter.add(new Field(SpeciesOccursumFields.speciesid+"",speciesID,FieldType.STRING));
			String occurrenceCells=SourceManager.getById(SourceManager.getDefaultId(ResourceType.OCCURRENCECELLS)).getTableName();
			Set<Cell> cellsInTable = Cell.loadRS(session.executeFilteredQuery(cellFilter, occurrenceCells, HCAF_SFields.csquarecode+"", OrderDirection.ASC));
			logger.trace("Found "+cellsInTable.size()+" occurrence cells, going to filter..");
			Set<Cell> toReturn = new HashSet<Cell>();

			for(Cell c : cellsInTable){
				//Cehcking BB
				try{
					//				double latitude=c.getFieldbyName(OccurrenceCellsFields.centerlat+"").getValueAsDouble(ServiceContext.getContext().getDoubleDefault());
					//				double longitude=c.getFieldbyName(OccurrenceCellsFields.centerlong+"").getValueAsDouble(ServiceContext.getContext().getDoubleDefault());
					//				if((latitude-0.25<bb.getS())||(latitude+0.25>bb.getN())||(longitude-0.25<bb.getE())||(longitude+0.25>bb.getW()))
					//						cellsInTable.remove(c);
					if(isInBoundingBox(c,bb)&&(isInFaoAreas(c, areas)))
						toReturn.add(c);
					//Checking A
					//				Area areaM=new Area(AreaType.FAO,c.getFieldbyName(OccurrenceCellsFields.faoaream+"").getValue());
					//				if((areas.size()>0)&&(!areas.contains(areaM))) cellsInTable.remove(c);
				}catch(Exception e){
					logger.error("Unable to evaluate Cell "+c.getFieldbyName(HCAF_SFields.csquarecode+"").value());
					throw e;
				}
			}
			logger.trace("Calculated "+toReturn.size()+" good cells");

			toReturn=loadEnvironmentData(hcafId, toReturn);
			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}

	private static boolean isInBoundingBox(Cell c,BoundingBox bb){
		//TODO implement check
		return true;
	}

	private static boolean isInFaoAreas(Cell c,List<Area> areas){
		//TODO implement check
		return true;
	}


}
