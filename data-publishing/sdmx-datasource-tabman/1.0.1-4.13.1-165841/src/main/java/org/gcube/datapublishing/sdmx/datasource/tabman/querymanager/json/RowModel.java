/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json;


import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ciro
 */
public class RowModel extends JSonModel{

	private Logger logger;
	private List<String> elements;
	private List<String> columnIdList;
	
	
	public RowModel(List<String> columnIdList) 
	{
		super ();
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.elements = new ArrayList<>();
		this.columnIdList =columnIdList;
		this.logger.debug("Column ids "+this.columnIdList);
	}
	

	@Override
	protected void fromJson (JsonParser jp)  throws Exception
	{

		this.logger.debug("JSon Array");
        JsonToken tolkein = jp.getCurrentToken();// START_OBJECT
    	while ((tolkein = jp.nextToken())!= JsonToken.END_ARRAY)
    	{
    			String value = jp.getText();
    			this.logger.debug("Value "+value);
    			this.elements.add(value);

    	}

        
	}
	
	@Override
	public String toJson() throws Exception {
		
        StringWriter outBuffer = new StringWriter();
        JsonFactory f = new JsonFactory();
        JsonGenerator g = f.createJsonGenerator(outBuffer);
        g.useDefaultPrettyPrinter();
        g.writeStartArray();
		
        Iterator<String> elementsIterator = this.elements.iterator();
		
		while (elementsIterator.hasNext())
		{
			g.writeString(elementsIterator.next());
	
		}
		
        g.writeEndArray();
        g.flush();
        return outBuffer.toString();
	}
	


	public String getElement (int position)
	{
		return this.elements.get(position);
	}
	



	@Override
	protected int checkKey(String key) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	protected JSonModel generateInternalObject(String key) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected JSonModel generateInternalArrayObject(String key, int element) {
		// TODO Auto-generated method stub
		return null;
	}
	


}
