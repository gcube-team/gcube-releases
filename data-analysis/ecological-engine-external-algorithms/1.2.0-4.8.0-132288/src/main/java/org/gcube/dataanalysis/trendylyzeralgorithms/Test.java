package org.gcube.dataanalysis.trendylyzeralgorithms;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.utils.DynamicEnum;
import org.gcube.dataanalysis.trendylyzeralgorithms.Util;


enum MEOWEnumType {}
enum LMEenumType {}
class MEOWenum {	
	public Field[] getFields() {
		
		Field[] fields = MEOWEnumType.class.getDeclaredFields();
		return fields;
	}}
 class LMEenum extends DynamicEnum{
	public Field[] getFields() {
		Field[] fields = LMEenumType.class.getDeclaredFields();
		return fields;
	}
}
public class Test {
	private static Hashtable<String, String> areaTable= new Hashtable<String , String>();
	private static LMEenum enuArea=new LMEenum();
	
	 public static void main(String [] args)
		{try {
		 Connection connection = DriverManager.getConnection(
				   "jdbc:postgresql://obis2.i-marine.research-infrastructures.eu/obis","postgres", "0b1s@d4sc13nc3");
			
			AnalysisLogger.getLogger().debug("call queryArea");
			String query= "select upper(lme_name)as lme_name from geo.lme";
			Statement stmt;
			
				stmt = connection.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				
				String area=rs.getString("lme_name");
				String formatArea=Util.formatAreaName(area);
				areaTable.put(formatArea, area);
				enuArea.addEnum(LMEenumType.class, Util.formatAreaName(area));
				LMEenumType selectEnumArea =LMEenumType.valueOf(Util.formatAreaName(area));
				String selectedAreaName=(String) areaTable.get(selectEnumArea.toString());
				System.out.print(selectedAreaName);
				System.out.println();
			}
			connection.close();
	 }catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	 }
}
