/**
 * 
 */
package org.gcube.application.aquamaps.aquamapsportlet.servlet.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.AreaFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.LocalObjectFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientFieldType;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientField;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AreaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtil {
	
	private static final Logger log = LoggerFactory.getLogger(DBSession.class);
	

	public static String getOuterName(String dbType){
		if(dbType.equalsIgnoreCase("Class")) return SpeciesFields.classcolumn+"";
		if(dbType.equalsIgnoreCase("Kingdom")) return SpeciesFields.kingdom+"";
		if(dbType.equalsIgnoreCase("Phylum")) return SpeciesFields.phylum+"";
		if(dbType.equalsIgnoreCase("Order_column")) return SpeciesFields.ordercolumn+"";
		if(dbType.equalsIgnoreCase("Order_table")) return SpeciesFields.ordercolumn+"";
		if(dbType.equalsIgnoreCase("Family")) return SpeciesFields.familycolumn+"";

		for(String specField:speciesFields)
			if(dbType.equalsIgnoreCase(specField)) return specField;

		return dbType.toLowerCase();
	}

	public static String toJSon(ResultSet resultSet ) throws SQLException
	{
		try{
		StringBuilder json = new StringBuilder();

		json.append("{\""+Tags.DATA+"\":[");

		ResultSetMetaData metaData = null;
		int numberOfColumns=0;

		int row = 0;
		while(resultSet.next()){


			if(metaData==null){
				metaData=resultSet.getMetaData();
				numberOfColumns = metaData.getColumnCount();
			}

			if (row>0) json.append(",{");
			else json.append('{');

			for (int column = 1; column <=numberOfColumns; column++){
				if (column>1) json.append(',');					
				json.append(quote(getOuterName(metaData.getColumnName(column))));
				json.append(':');
				json.append(quote(resultSet.getString(column)));
			}

			json.append('}');

			row++;
		}

		json.append("],\""+Tags.TOTAL_COUNT+"\":");
		json.append(row);
		json.append("}");

		return json.toString();
		}catch(Exception e){
			log.error("Result Set Might Be Empty ... returning empty JSON String...");
			log.trace(""+e);
			return Tags.EMPTY_JSON;
		}

	}

	/**
	 * Produce a string in double quotes with backslash sequences in all the
	 * right places. A backslash will be inserted within </, allowing JSON
	 * text to be delivered in HTML. In JSON text, a string cannot contain a
	 * control character or an unescaped quote or backslash.
	 * @param string A String
	 * @return  A String correctly formatted for insertion in a JSON text.
	 */
	protected static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char         b;
		char         c = 0;
		int          i;
		int          len = string.length();
		StringBuffer sb = new StringBuffer(len + 4);
		String       t;

		sb.append('"');
		for (i = 0; i < len; i += 1) {
			b = c;
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				if (b == '<') {
					sb.append('\\');
				}
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
						(c >= '\u2000' && c < '\u2100')) {
					t = "000" + Integer.toHexString(c);
					sb.append("\\u" + t.substring(t.length() - 4));
				} else {
					sb.append(c);
				}
			}
		}
		sb.append('"');
		return sb.toString();
	}

	public static String toJSon(ResultSet resultSet, int start, int end) throws SQLException
	{
		try{
		StringBuilder json = new StringBuilder();

		json.append("{\""+Tags.DATA+"\":[");

		ResultSetMetaData metaData = null;
		int numberOfColumns = 0;

		int row = 0;

		while(resultSet.next()){

			if(metaData==null){
				metaData=resultSet.getMetaData();
				numberOfColumns = metaData.getColumnCount();
			}
			
			if (row>=start && row <= end){

				if (row-start>0) json.append(",{");
				else json.append('{');

				for (int column = 1; column <=numberOfColumns; column++){
					if (column>1) json.append(',');
					json.append(quote(getOuterName(metaData.getColumnName(column))));
					json.append(':');
					json.append(quote(resultSet.getString(column)));
				}

				json.append('}');
			}
			row++;
		}

		json.append("],\""+Tags.TOTAL_COUNT+"\":");
		json.append(row);
		json.append("}");

		return json.toString();
		}catch(Exception e){
			log.error("Result Set Might Be Empty ... returning empty JSON String...");
			log.trace(""+e);
			return Tags.EMPTY_JSON;
		}

	}

	protected static final String[] speciesFields=new String[]{
			SpeciesFields.speciesid+"", 
			SpeciesFields.genus+"", 
			SpeciesFields.species+"", 
			SpeciesFields.speccode+"", 
			SpeciesFields.fbname+"", 
			SpeciesFields.scientific_name+"",
			SpeciesFields.occurrecs+"", 
			SpeciesFields.occurcells+"", 
			SpeciesFields.classcolumn+"",
			SpeciesFields.familycolumn+"",
			SpeciesFields.kingdom+"",
			SpeciesFields.ordercolumn+"", 
			SpeciesFields.phylum+"", 
			SpeciesFields.map_beforeafter+"", 
			SpeciesFields.map_seasonal+"", 
			SpeciesFields.with_gte_5+"", 
			SpeciesFields.with_gte_6+"", 
			SpeciesFields.with_gt_66+"", 
			SpeciesFields.no_of_cells_3+"",
			SpeciesFields.no_of_cells_5+"", 
			SpeciesFields.no_of_cells_0+"", 
			SpeciesFields.database_id+"", 
			SpeciesFields.picname+"", 
			SpeciesFields.authname+"", 
			SpeciesFields.entered+"", 
			SpeciesFields.total_native_csc_cnt+"", 
			SpeciesFields.deepwater+"", 
			SpeciesFields.m_mammals+"", 
			SpeciesFields.angling+"", 
			SpeciesFields.diving+"", 
			SpeciesFields.dangerous+"", 
			SpeciesFields.m_invertebrates+"", 
			SpeciesFields.algae+"", 
			SpeciesFields.seabirds+"", 
			SpeciesFields.timestampcolumn+"", 
			SpeciesFields.pic_source_url+"", 
			SpeciesFields.freshwater+"",
	};
	protected static final int[] speciesFieldsType=new int[]{
			java.sql.Types.VARCHAR, 
			java.sql.Types.VARCHAR, 
			java.sql.Types.VARCHAR, 
			java.sql.Types.INTEGER, 
			java.sql.Types.VARCHAR, 
			java.sql.Types.VARCHAR,
			java.sql.Types.INTEGER, 
			java.sql.Types.INTEGER, 
			java.sql.Types.VARCHAR,
			java.sql.Types.VARCHAR,
			java.sql.Types.VARCHAR,
			java.sql.Types.VARCHAR, 
			java.sql.Types.VARCHAR, 
			java.sql.Types.DOUBLE, 
			java.sql.Types.DOUBLE, 
			java.sql.Types.SMALLINT, 
			java.sql.Types.SMALLINT, 
			java.sql.Types.SMALLINT, 
			java.sql.Types.INTEGER,
			java.sql.Types.INTEGER, 
			java.sql.Types.INTEGER, 
			java.sql.Types.INTEGER, 
			java.sql.Types.VARCHAR, 
			java.sql.Types.VARCHAR, 
			java.sql.Types.INTEGER, 
			java.sql.Types.INTEGER, 
			java.sql.Types.SMALLINT, 
			java.sql.Types.SMALLINT, 
			java.sql.Types.SMALLINT, 
			java.sql.Types.SMALLINT, 
			java.sql.Types.SMALLINT, 
			java.sql.Types.SMALLINT, 
			java.sql.Types.SMALLINT, 
			java.sql.Types.SMALLINT, 
			java.sql.Types.TIMESTAMP, 
			java.sql.Types.VARCHAR, 
			java.sql.Types.SMALLINT,
	};		


	static String getAreaQuery(String table,boolean includeFAO,boolean includeEEZ, boolean includeLME){
		String areaFilter=(includeFAO)?" type = '"+AreaType.FAO+"' ":"";		
		if(includeEEZ) areaFilter=areaFilter+((includeFAO)?" OR ":"")+" type = '"+AreaType.EEZ+"' ";
		if(includeLME) areaFilter=areaFilter+((includeFAO||includeEEZ)?" OR ":"")+" type = '"+AreaType.LME+"' ";
		return "SELECT * FROM "+table+" WHERE "+areaFilter;
	}
	
	static List<Area> loadAreas(ResultSet rs)throws Exception{
		List<Area> toReturn= new ArrayList<Area>();
		try{
		while(rs.next()) 
			toReturn.add(new Area(AreaType.valueOf(rs.getString(AreaFields.type+"")),rs.getString(AreaFields.code+""),rs.getString(AreaFields.name+"")));
		}catch(Exception e){
			log.error("Result Set Might Be Empty ... returning empty Area list ...");
			log.trace(""+e);
		}
		return toReturn;
	}
	
	static List<ClientObject> loadObjects(ResultSet rs) throws Exception{
		List<ClientObject> toReturn=new ArrayList<ClientObject>();
		try{
			while(rs.next()){
				ClientObject obj= new ClientObject();
				obj.setAuthor(rs.getString(DBCostants.userID));
				obj.getBoundingBox().parse(rs.getString(LocalObjectFields.bbox+""));
				obj.setGis(rs.getInt(LocalObjectFields.gis+"")==1);
				obj.setName(rs.getString(LocalObjectFields.title+""));
				obj.setType(ClientObjectType.valueOf(rs.getString(LocalObjectFields.type+"")));
				obj.setThreshold(rs.getFloat(LocalObjectFields.threshold+""));
				obj.setSelectedSpecies(new ClientField(LocalObjectFields.species+"",rs.getString(LocalObjectFields.species+""),
						obj.getType().equals(ClientObjectType.Biodiversity)?ClientFieldType.INTEGER: ClientFieldType.STRING));
				toReturn.add(obj);
			}
			}catch(Exception e){
				log.error("Result Set Might Be Empty ... returning empty Object list ...");
				log.trace(""+e);
			}
			return toReturn;
	}
//	public static String toJSon(ResultSet resultSet, Long count) throws SQLException
//	{
//		
//		StringBuilder json = new StringBuilder();
//
//		json.append("{\"data\":[");
//
//		ResultSetMetaData metaData = resultSet.getMetaData();
//		int numberOfColumns = metaData.getColumnCount();
//
//		int row = 0;
//
//		while(resultSet.next()){
//
//			
//				
//				if (row>0) json.append(",{");
//				else json.append('{');
//
//				for (int column = 1; column <=numberOfColumns; column++){
//					if (column>1) json.append(',');
//					json.append(quote(metaData.getColumnName(column)));
//					json.append(':');
//					json.append(quote(resultSet.getString(column)));
//				}
//
//				json.append('}');
//			
//			row++;
//		}
//
//		json.append("],\"totalcount\":");
//		json.append(count);
//		json.append("}");
//
//		return json.toString();
//
//	}
	
}
