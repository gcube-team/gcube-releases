//package org.gcube.data.spd.flora;
//
//import java.io.IOException;
//import java.net.ConnectException;
//import java.net.MalformedURLException;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import org.gcube.common.core.utils.logging.GCUBELog;
//import org.gcube.data.spd.flora.dbconnection.Database;
//
//public class refill {
//	static GCUBELog logger = new GCUBELog(refill.class);
//
//
//
//	/**
//	 * @param args
//	 * @throws Exception 
//	 */
//	public static void main(String[] args) throws Exception {
//		try {
//			fill();
//		} catch (ConnectException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//	}
//
//	/**
//	 * Parse every path looking for synonyms
//	 * @throws IOException 
//	 * @throws MalformedURLException 
//	 */
//	protected static void fill() throws SQLException, MalformedURLException, IOException {		
//
//
//		ResultSet res = null;
//		Database database = null;  
//
//		try {
//			database = new Database();  			
//			database.connect();
//
//			//						logger.trace("select id, name, id_parent, path from " + FloraPlugin.tableName + " where citation=''");
//			//			res = database.get("select id, name, id_parent, path from " + FloraPlugin.tableName + " where citation=''");
//
//			logger.trace("select path, id, scientific_name from " + FloraPlugin.tableName + " where id_parent='32' and path > 'Angiospermas_/Loranthaceae_JUS' and path < 'Angiospermas_/Oleaceae_' order by path limit 24" );
//			res = database.get("select path, id, scientific_name from " + FloraPlugin.tableName + " where id_parent='32' and path > 'Angiospermas_/Loranthaceae_JUS' and path < 'Angiospermas_/Oleaceae_' order by path limit 24" );
//
//
//			//			res = database.get("select a.id, a.name, a.rank, b.name, c.name, a.id_parent from " + FloraPlugin.tableName + " as a join flora as b on (a.rank = 'SubSpecies' or a.rank = 'Species' or a.rank = 'Genus') and a.id_parent = b.id join flora as c on b.id_parent=c.id where a.status = 'Accepted name' ORDER BY a.id");
//
//			while(res.next()) {
//
//				String path = res.getString(1);
//				String id_parent = res.getString(2);
//				//				if (id_parent.equals(176))
//				//					System.exit(0);
//				String sName = res.getString(3);
//				//				logger.trace("http://checklist.florabrasil.net/service/TREE/VERSION/2012/FORMAT/xml/LANG/en/PATH/" + path);
//				Utils.discoverPath("http://checklist.florabrasil.net/service/TREE/VERSION/2012/FORMAT/xml/LANG/en/PATH/" + path, id_parent, sName, null,  true);		   	
//
//				//				String id = res.getString(1);
//				//				String name = res.getString(2);
//				//				String id_parent = res.getString(3);
//				//				String path = res.getString(4);
//				//				
//				//				Map<String, String> values = Utils.findTags(id);
//				//				ArrayList<String> terms = new ArrayList<String>();
//				//				
//				//				terms.add(id);
//				//				terms.add(values.get("rank"));
//				//				terms.add(name);
//				//				terms.add(values.get("plain_name"));
//				//				terms.add(id_parent);
//				//				terms.add(values.get("author"));
//				//				terms.add(values.get("status"));
//				//				terms.add(path);
//				//				terms.add(values.get("qualifier"));
//				//
//				//				String stmt = "";
//				//				stmt = ("update "+ FloraPlugin.tableName + " set id = ?, rank = ?, name = ?, scientific_name = ?, id_parent = ?, citation = ?, status = ?, path = ?, qualifier = ? where id = '" + id + "'");
//				//
//				//
//				//				if (!database.preStatement(stmt, terms)){
//				//					logger.trace("error");
//				//				}
//				//				terms.clear();
//				//				values.clear();
//
//
//			}			
//		} catch (SQLException sqlExcept) {
//
//			logger.error("SQL Exception", sqlExcept);
//		}finally{
//			if (res != null)
//				res.close();
//
//			database.shutDown();
//		}
//
//	}
//
//}
