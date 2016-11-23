package org.gcube.datatransformation.adaptors.db;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class Test {

	
	public static void main (String[] args) throws IOException, XMLStreamException {
		
		
		
		
//		stax();
//		try {
//			dom();
//		} catch (SQLException e) {e.printStackTrace();}
		
	}

	
	private static void dom() throws SQLException{		
//		DBDataDom dbData = new DBDataDom("john.gerbesiotis","src/main/resources/sqlDBMappings.xml");
	}
	
	
	
	private static void stax() throws IOException, XMLStreamException{
//		DBDataStax dbDataStax = new DBDataStax("john.gerbesiotis","src/main/resources/sqlDBMappings.xml");
//		dbDataStax.writeUserDataTest(new PrintWriter(System.out));

	}
	
	
}