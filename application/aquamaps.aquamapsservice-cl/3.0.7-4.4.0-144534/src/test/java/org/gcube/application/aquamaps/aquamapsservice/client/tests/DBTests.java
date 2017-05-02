package org.gcube.application.aquamaps.aquamapsservice.client.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;

public class DBTests {

	private static String connectionUrl="jdbc:postgresql://dbtest.research-infrastructures.eu:5432/aquamapsorgupdated";
	private static String user="utente";
	private static String password="d4science";
	private static Connection conn;
	
	
	public static void main(String[] args) throws Exception{
		
		
		
		
		conn=DriverManager.getConnection(connectionUrl,user,password);		
		int[] searchids=new int[]{
			325,125,121,124
		};
		PreparedStatement ps=conn.prepareStatement("Select * from meta_sources where searchid=?");
		
		for(int id:searchids){
			try{
				ps.setInt(1, id);
				ResultSet rs=ps.executeQuery();
				if(rs.next()){
					Resource res=new Resource(rs);
					System.out.println(res);
				}else System.out.println("ID "+id+" not found");
			}catch(Throwable t){t.printStackTrace();}
		}
		
		
		
		
//		for(List<Field> row:Field.loadResultSet(query("Select * from meta_sources where searchid=325")))
//			System.out.println("row1 : "+row);
	}
	
	
	
	private static ResultSet query(String query)throws Exception{
		return conn.createStatement().executeQuery(query);		
	}
	
	
	
	
}
