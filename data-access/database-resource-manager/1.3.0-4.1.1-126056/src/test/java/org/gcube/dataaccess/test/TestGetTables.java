package org.gcube.dataaccess.test;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataaccess.databases.utils.DatabaseManagement;



/** Class that tests the code for the recovery of the tables */
public class TestGetTables {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Specifica il nome del file di configurazione");
		
		String configurationfile=sc.next();
		
		//TODO: decommentare per la corretta esecuzione della classe
//		try {
//			DatabaseManagement obj=new DatabaseManagement("./cfg/", configurationfile);
//			
//			
//          //Retrieve the schema for the postgres database
//			
//			List <String> schemas=new ArrayList<String>();
//			
//			schemas=obj.getSchemas();
//			
//			if (schemas!=null){
//			
//			//test Print 
//			for (int i=0;i<schemas.size();i++)
//			{
//				
//			System.out.println("Schema's name: "+ schemas.get(i));	
//											
//			}
//			
//								
//			}
//			else{
//				
//				
//				System.out.println("il database nn ha schemi");
//			}
//			
//			
//			
//			
//			
//			
//			
//			List <String> tables=new ArrayList<String>();
//			
//			
//			//Retrieve the table's names of the database
//			tables=obj.getTables();
//			
//			
//			//test print
//			for (int i=0;i<tables.size();i++)
//			{
//				
//			System.out.println(tables.get(i));	
//											
//			}
//			
//			
//			
//			
//			//Retrieve the "Create Table" statement
//			System.out.println("Specifica il nome della tabella");
//        	String tablename=sc.next();
//			
//        	String createtable=obj.getCreateTable(tablename);
//
//        	
//        	
//        	//View number of rows
//          	try {
//        		
//        	 	BigInteger rows=obj.getNumberOfRows(tablename);
//            	
//            	System.out.println("il numero di righe è: " + rows);
//            	
//				
//			} catch (Exception e) {
//				// TODO: handle exception
//				
//				System.out.println("The table does not exist.");
//				
//			}
//       
//        	
//			
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
////			e.printStackTrace();
//		}
		
	}

}
