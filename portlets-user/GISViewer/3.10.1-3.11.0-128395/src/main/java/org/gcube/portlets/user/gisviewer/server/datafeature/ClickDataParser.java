package org.gcube.portlets.user.gisviewer.server.datafeature;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.gisviewer.client.commons.beans.DataResult;
import org.gcube.portlets.user.gisviewer.client.commons.beans.ResultColumn;
import org.gcube.portlets.user.gisviewer.client.commons.beans.ResultRow;
import org.gcube.portlets.user.gisviewer.client.commons.beans.ResultTable;

public class ClickDataParser {
	
	public static  List<DataResult> getDataResult(String _url){
		List<DataResult> result = new ArrayList<DataResult>();
		try{
		URL url = new URL(_url);
		BufferedReader in = new BufferedReader(
					new InputStreamReader(
					url.openStream()));

		String inputLine;

		DataResult dr = new DataResult();
		ResultTable rt = new ResultTable();
		List<ResultRow> resultRaws = new ArrayList<ResultRow>();
		List<ResultColumn> rc = new ArrayList<ResultColumn>();
		
		
		while ((inputLine = in.readLine()) != null){
			
			if(inputLine.contains("<table")){
				dr=new DataResult();
				rt=new ResultTable();
				resultRaws=new ArrayList<ResultRow>();
			}
			
			if(inputLine.contains("<caption class=\"featureInfo\">")){
				String title = (inputLine.replace("<caption class=\"featureInfo\">", ""));
				title = title.replace("</caption>", "");
				title = title.replace("</caption>", "");
				title=title.trim();
				dr.setTitle(title);
			}
			
			if(inputLine.contains("<tr")){
				// new row
				rc= new ArrayList<ResultColumn>();
			}
			
			if(inputLine.contains("<td>")||inputLine.contains("<th")){
				
				// add value
				String column_value = inputLine.replace("<td>", "");
				column_value=column_value.replace("</td>", "");
				column_value=column_value.replace("<th>", "");
				column_value=column_value.replace("<th >", "");
				column_value=column_value.replace("</th>", "");
				column_value=column_value.trim();
				ResultColumn column = new ResultColumn(column_value);
				rc.add(column);
			}
			
			if(inputLine.contains("</tr")){
				// add new row to list
				ResultRow resultRow = new ResultRow(rc);
				resultRaws.add(resultRow);
			}
			
			
			if(inputLine.contains("</table")){
				rt.setRows(resultRaws);
				dr.setTable(rt);
				result.add(dr);
			}
			
			
		
		}

		in.close();
		
		
		}catch(IOException e){}
		return result;
	}
}
