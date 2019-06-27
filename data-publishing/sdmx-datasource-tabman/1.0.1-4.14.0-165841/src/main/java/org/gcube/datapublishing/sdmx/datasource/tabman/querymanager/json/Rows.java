package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParser;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.exception.NoDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rows extends JSonModel {

	private List<RowModel> rows;
	private Logger logger;
	private List<String> columnIdList;
	
	public Rows(List<String> columnIdList) {
		this.logger = LoggerFactory.getLogger(Rows.class);
		this.rows = new ArrayList<>();
		this.columnIdList = columnIdList;
	}
	
	@Override
	protected void fromJson(JsonParser jp) throws Exception {
		super.fromJson(jp);
		this.logger.debug("Generating row list");
		List<Object> objectRows = this.arrayDataMap.get("rows");
		
		
		if (objectRows == null || objectRows.isEmpty()) throw new NoDataException();
		
		for (Object objectRow : objectRows)
		{
			logger.debug("Adding new row "+objectRow);
			this.rows.add((RowModel) objectRow);
			
		}
	}
	

	@Override
	protected int checkKey(String key) 
	{
		if (key.equals("rows")) return KeyType.OBJECT_ARRAY.getValue();
		else return 0;
	}
	
	public List<RowModel> getRows ()
	{
		return this.rows;
	}

	@Override
	protected JSonModel generateInternalObject(String key) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected JSonModel generateInternalArrayObject(String key, int element) 
	{
		return new RowModel(columnIdList);
	}
	

	
	public static void main(String[] args) throws Exception{
		String jsonString = "{"+
			"\"rows\": ["+
				"[2003, 3, \"Chile\"],"+
				"[2008, 12, \"Argentina\"],"+
				"[2008, 36, \"Brazil\"],"+
				"[2008, 34, \"Brazil\"],"+
				"[2008, 735, \"Venezuela\"],"+
				"[2008, 422, \"uruguay\"],"+
				"[2009, 15, \"Argentina\"],"+
				"[2009, 34, \"Brazil\"],"+
				"[2009, 23, \"Brazil\"],"+
				"[2009, 754, \"Venezuela\"],"+
				"[2009, 432, \"uruguay\"],"+
				"[2010, 14, \"Argentina\"],"+
				"[2010, 24, \"Brazil\"],"+
				"[2010, 754, \"Venezuela\"],"+
				"[2010, 14, \"Chile\"],"+
				"[2010, 432, \"uruguay\"],"+
				"[2011, 15, \"Argentina\"],"+
				"[2011, 43, \"Brazil\"],"+
				"[2011, 788, \"Venezuela\"],"+
				"[2011, 42, \"Chile\"],"+
				"[2011, 432, \"uruguay\"],"+
				"[2012, 14, \"Argentina\"],"+
				"[2012, 23, \"Brazil\"],"+
				"[2012, 745, \"Venezuela\"],"+
				"[2012, 34, \"Chile\"],"+
				"[2012, 432, \"uruguay\"],"+
				"[2013, 11, \"Argentina\"],"+
				"[2013, 34, \"Brazil\"],"+
				"[2013, 754, \"Venezuela\"],"+
				"[2013, 54, \"Chile\"],"+
				"[2013, 432, \"uruguay\"],"+
				"[2014, 13, \"Argentina\"],"+
				"[2014, 23, \"Brazil\"],"+
				"[2014, 745, \"Venezuela\"],"+
				"[2014, 345, \"Chile\"],"+
				"[2014, 457, \"uruguay\"]]}";
			
		Rows rows = new Rows(null);
		rows.fromJson(jsonString.getBytes());
		List<RowModel> rowsList = rows.getRows();
		System.out.println(rowsList.get(0).getElement(2));
	}
	
}
