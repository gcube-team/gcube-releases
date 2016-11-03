package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Perturbation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HspenFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.gcube_system.namespaces.application.aquamaps.types.PagedRequestSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeciesManager {

	final static Logger logger= LoggerFactory.getLogger(SpeciesManager.class);
	public static final String speciesOccurSum="speciesoccursum";
	
	public static final String CATALOG_OF_LIFE="CATALOG OF LIFE";
	public static final String FISHBASE="FISHBASE";
	public static final String IRMNG="IRMNG";
	public static final String ITIS="ITIS";
	public static final String THREE_A_CODE="3A Code";
	public static final String WORMS="WORMS";
	public static final String OBIS="OBIS";
	
	
	
	public static Map<String,String> getSpeciesNamesById(String speciesId) throws SQLException, Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filters=new ArrayList<Field>();
			filters.add(new Field(SpeciesOccursumFields.speciesid+"", speciesId, FieldType.STRING));
			ResultSet rs=session.executeFilteredQuery(filters, "namedSpecies", SpeciesOccursumFields.speciesid+"", OrderDirection.ASC);
			HashMap<String,String> toReturn=new HashMap<String, String>();
			if(rs.next()){
				toReturn.put(CATALOG_OF_LIFE, rs.getString("col_id"));
				toReturn.put(FISHBASE, rs.getString("fbname"));
				toReturn.put(IRMNG, rs.getString("irmng_id"));
				toReturn.put(ITIS, rs.getString("itis_id"));
				toReturn.put(THREE_A_CODE, rs.getString("3a_code"));
				toReturn.put(WORMS, rs.getString("worms_id"));
				toReturn.put(OBIS, rs.getString("genus")+" "+rs.getString("species"));
			}else logger.debug("Unable to find species names for "+speciesId);
			return toReturn;
		}finally{if(session!=null) session.close();}
	}
	
	public static Species getSpeciesById(boolean fetchStatic,boolean fetchEnvelope, String id, int hspenId) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filters=new ArrayList<Field>();
			filters.add(new Field(SpeciesOccursumFields.speciesid+"", id, FieldType.STRING));
			Species toReturn=new Species(id);
			if(fetchStatic){
				List<Field> row=Field.loadResultSet(session.executeFilteredQuery(filters, speciesOccurSum,null,null)).get(0);
				if(row!=null) toReturn.getAttributesList().addAll(row);
			}
			if(fetchEnvelope){
				String hspenTable=SourceManager.getSourceName(hspenId);
				List<Field> row=Field.loadResultSet(session.executeFilteredQuery(filters, hspenTable,null,null)).get(0);
				if(row!=null) toReturn.getAttributesList().addAll(row);
			}
			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static Set<Species> getList(List<Field> filters)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return loadRS(session.executeFilteredQuery(filters, speciesOccurSum,null,null));
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	
	public static Set<Species> getList(List<Field> filters,Resource hspen)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			String app=ServiceUtils.generateId("spec", "");
			String query="CREATE TABLE "+app+" AS (SELECT * FROM "+speciesOccurSum+" WHERE "+SpeciesOccursumFields.speciesid+" IN (SELECT "+SpeciesOccursumFields.speciesid+" FROM "+hspen.getTableName()+"))";
			logger.debug("QUERY IS : "+query);
			session.executeUpdate(query);
			Set<Species> toReturn= loadRS(session.executeFilteredQuery(filters, app,null,null));
			session.dropTable(app);
			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	
	
	
	public static String getFilteredHSPEN(String sourceHSPEN, Set<Species> toInsert)throws Exception{
		DBSession session=null;
		String tmpHspen=null;
		logger.trace("Filtering "+sourceHSPEN);
		try{
			session=DBSession.getInternalDBSession();
			tmpHspen=ServiceUtils.generateId("filteredhspen", "");
			session.createLikeTable(tmpHspen,sourceHSPEN);
			logger.trace("going to fill table "+tmpHspen);
			List<Field> condition=new ArrayList<Field>();
			condition.add(new Field(SpeciesOccursumFields.speciesid+"","",FieldType.STRING));
			PreparedStatement ps=session.getPreparedStatementForInsertFromSelect(condition, tmpHspen, sourceHSPEN);
			int count=0;
			for(Species s: toInsert){
				ps.setString(1, s.getId());
				int inserted=ps.executeUpdate();
				if(inserted==0)logger.warn("Species ID : "+s.getId()+" hasn't been inserted");
				else count+=inserted;
			}
			logger.trace("Inserted "+count+"/"+toInsert.size()+" species");
			return tmpHspen;
		}catch(Exception e){
			logger.error("Unable to filter against species selection");
			if(tmpHspen!=null) session.dropTable(tmpHspen);
			throw e;
		}finally{
			if(session!=null) session.close();			
		}
	}
	
	
	public static String getJSONList(PagedRequestSettings settings, List<Filter> genericSearch, List<Filter> specificSearch,int HSPENId)throws Exception{
		String[] queries;
		String selHspen=SourceManager.getSourceName(HSPENId);
		queries=formFilterQueries(genericSearch, specificSearch, selHspen);
		DBSession session=null;		
		try{
			session=DBSession.getInternalDBSession();
			
			ResultSet rs=session.executeQuery(queries[1]);
			rs.next();
			Long totalCount=rs.getLong(1);
			
			return DBUtils.toJSon(session.executeQuery(queries[0]+((settings.getOrderField()!=null)?" order by "+getCompleteName(selHspen, settings.getOrderField())+" "+settings.getOrderDirection():"")+" LIMIT "+
					settings.getLimit()+" OFFSET "+settings.getOffset()), totalCount);
			
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static File getCSVList(List<Filter> genericSearch, List<Filter> specificSearch,int HSPENId)throws Exception{
		String[] queries;
		String selHspen=SourceManager.getSourceName(HSPENId);
		queries=formFilterQueries(genericSearch, specificSearch, selHspen);
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			File out=File.createTempFile("speciesSelection", ".csv");
			
			CSVUtils.resultSetToCSVFile(session.executeQuery(queries[0]),out.getAbsolutePath(),true);
			return out;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	/**
	 *  Forms queries for species search 
	 * 
	 * 
	 * @param ORGroup
	 * @param specificFilters
	 * @return
	 * @throws Exception
	 */
	private static String[] formFilterQueries(List<Filter> ORGroup, List<Filter> specificFilters, String selectedHSPEN)throws Exception{
		StringBuilder orCondition=getCondition(ORGroup,"OR",selectedHSPEN);
		StringBuilder andCondition=getCondition(specificFilters,"AND",selectedHSPEN);
		
		StringBuilder filter=new StringBuilder((orCondition.length()>0)?"( "+orCondition.toString()+" )":"");
		if(andCondition.length()>0) {
			filter.append((filter.length()>0)?" AND ":"");
			filter.append("( "+andCondition.toString()+")");
		}
		
		boolean includeHSPEN=(filter.indexOf(selectedHSPEN)>-1);
		
		
		String fromString = " from "+speciesOccurSum +(includeHSPEN?" INNER JOIN "+selectedHSPEN+" ON "+speciesOccurSum+"."+SpeciesOccursumFields.speciesid+" = "+selectedHSPEN+"."+SpeciesOccursumFields.speciesid:"");
		String query= "Select "+speciesOccurSum+".* "+(includeHSPEN?" , "+selectedHSPEN+"."+HspenFields.pelagic:"")+				
						fromString+" "+((filter.length()>0)?" where ":"")+filter.toString();
		String count= "Select count("+speciesOccurSum+"."+SpeciesOccursumFields.speciesid+") "+fromString+" "+((filter.length()>0)?" where ":"")+filter.toString();
		logger.debug("filterSpecies: "+query);
		logger.debug("filterSpecies: "+count);
		return new String[] {query,count};	
	}
	
	private static StringBuilder getCondition(List<Filter> filters, String operator, String selHSPEN)throws Exception{
		StringBuilder toReturn=new StringBuilder();
		if((filters.size()>0)){
			for(Filter filter:filters)				
				toReturn.append(getCompleteName(selHSPEN, filter.getField().name())+filter.toSQLString()+" "+operator+" ");
			int index=toReturn.lastIndexOf(operator);
			toReturn.delete(index, index+3);
		}
		return toReturn;
	}
	
	public static String getCompleteName(String hspenName,String fieldName)throws Exception{
		try{
			return speciesOccurSum+"."+SpeciesOccursumFields.valueOf(fieldName);
		}catch(IllegalArgumentException e){
			//not a speciesoccursum field
		}
		return hspenName+"."+HspenFields.valueOf(fieldName);		
	}
	
	public static String perturbationUpdate(String hspenTable,Map<String,Perturbation> toSetPerturbations, String speciesId) throws Exception{
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("UPDATE "+hspenTable+" SET ");
		for(Entry<String, Perturbation> settings:toSetPerturbations.entrySet()){
			toReturn.append(settings.getKey()+" = "+settings.getValue().getPerturbationValue()+" , ");
		}
		toReturn.deleteCharAt(toReturn.lastIndexOf(","));
		toReturn.append(" WHERE "+SpeciesOccursumFields.speciesid+"= '"+speciesId+"'");
		
		return toReturn.toString();
	}
	
	
	
	private static Set<Species> loadRS(ResultSet rs) throws SQLException{
		HashSet<Species> toReturn=new HashSet<Species>();
		List<List<Field>> rows=Field.loadResultSet(rs);
		for(List<Field> row:rows){
			Species toAdd=new Species("***");
			toAdd.getAttributesList().addAll(row);
			toAdd.setId(toAdd.getFieldbyName(SpeciesOccursumFields.speciesid+"").value());
			toReturn.add(toAdd);
		}
		return toReturn;
	}
	
	
	public static String getJSONTaxonomy(Field toSelect, List<Field> filters, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			
			return DBUtils.toJSon(session.getDistinct(toSelect, filters, speciesOccurSum, 
					settings.getOrderField(), settings.getOrderDirection()), settings.getOffset(), settings.getLimit()+settings.getOffset());
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static String getCommonTaxonomy(Set<Species> species)throws Exception{
		logger.info("Chcking common taxonomy, to analyze species count : "+species.size());
		logger.info("loading species static info..");		
		long start=System.currentTimeMillis();		
		Set<Species> enrichedSpecies=new HashSet<Species>();
		for(Species s: species)enrichedSpecies.add(getSpeciesById(true, false, s.getId(), 0));
		logger.info("Loaded in "+(System.currentTimeMillis()-start)+" ms");		
		HashMap<SpeciesOccursumFields,String> commonLevels=new HashMap<SpeciesOccursumFields, String>();
		SpeciesOccursumFields[] toCheckValues=new SpeciesOccursumFields[]{
			SpeciesOccursumFields.kingdom,
			SpeciesOccursumFields.phylum,
			SpeciesOccursumFields.classcolumn,
			SpeciesOccursumFields.ordercolumn,
			SpeciesOccursumFields.familycolumn
		};
		boolean continueCheck=true;
		for(SpeciesOccursumFields toCheck:toCheckValues){
			if(continueCheck)for(Species s: enrichedSpecies){
				if(!commonLevels.containsKey(toCheck))commonLevels.put(toCheck, s.getFieldbyName(toCheck+"").value());
				else if(!s.getFieldbyName(toCheck+"").value().equalsIgnoreCase(commonLevels.get(toCheck))){
					continueCheck=false;
					commonLevels.remove(toCheck);
				}
			}
		}
		StringBuilder toReturn=new StringBuilder();
		for(SpeciesOccursumFields level:toCheckValues)
			if(commonLevels.containsKey(level))toReturn.append(commonLevels.get(level)+File.separator);
		if(toReturn.length()>0)toReturn.deleteCharAt(toReturn.lastIndexOf(File.separator));
		logger.info("Found common taxonomy in "+(System.currentTimeMillis()-start)+" ms");
		return toReturn.toString();
	}
}
