package org.gcube.datatransformation.adaptors.common.db.tools;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.gcube.datatransformation.adaptors.common.db.exceptions.SourceIDNotFoundException;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBProps;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBSource;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.Edge;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.Table;

public class SourcePropsTools {
	
	public static String getSqlOfTable(DBProps dbProps, String tableName){ 
		for(Table table : dbProps.getTables())
			if(table.getName().equalsIgnoreCase(tableName))
				return table.getSql();
		return "";
	}
	
	public static String getPKeyOfTable(DBProps dbProps, String tableName){ 
		for(Edge edge : dbProps.getEdges())
			if(edge.getParent().equalsIgnoreCase(tableName))
				return edge.getPKeys();
		return "";
	}
	
	public static ArrayList<String> getChildrenOfTable(DBProps dbProps, String tableName){ 
		ArrayList<String> children = new ArrayList<String>();
		for(Edge edge : dbProps.getEdges())
			if(edge.getParent().equalsIgnoreCase(tableName))
				children.add(edge.getChild());
		return children;
	}
	
	public static ArrayList<Edge> getEdges(DBProps dbProps, String parentTableName){
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for(Edge edge : dbProps.getEdges())
			if(edge.getParent().equalsIgnoreCase(parentTableName))
				edges.add(edge);
		return edges;
	}
	
	
	public static DBProps parseSourceProps(String propsXML) throws SourceIDNotFoundException, Exception {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(DBProps.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(propsXML);
			return (DBProps) jaxbUnmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new Exception(new Throwable("Error upon unmarshalling the properties XML",e));
		}
	}
	
	
	public static DBSource parseDBSource(String dbSourceXML) throws SourceIDNotFoundException, Exception {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(DBProps.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(dbSourceXML);
			return (DBSource) jaxbUnmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new Exception(new Throwable("Error upon unmarshalling the properties XML",e));
		}
	}
	
	
	public static String dbPropsToXML(DBProps dbProps) throws SourceIDNotFoundException, Exception {
		Marshaller jaxbMarshaller = JAXBContext.newInstance(DBProps.class).createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter sw = new StringWriter();
		jaxbMarshaller.marshal(dbProps, sw);
		return sw.toString();
	}
	
	
	/**
	 * Returns 'valid' in case it is valid, otherwise it returns a text describing what's wrong
	 * @param dbProps
	 * @return
	 */
	public static String isValid(DBProps dbProps){
		//check if there are more than one heading (root) tables
		int rootNum = 0;
		for(Table table : dbProps.getTables()){
			boolean root = true;
			int numChild = 0;
			for(Edge edge : dbProps.getEdges()){
				if(table.getName().equalsIgnoreCase(edge.getChild())){
					numChild++;
					root = false;
				}
			}
			if(numChild > 1)
				return "Table "+table.getName()+" is child of more than 1 table. That's not allowed.";
			if(root)
				rootNum++;
		}
		if(rootNum !=1 )
			return "Number of root tables is not exactly 1";

		
		return "valid";
	}
	
	
}
