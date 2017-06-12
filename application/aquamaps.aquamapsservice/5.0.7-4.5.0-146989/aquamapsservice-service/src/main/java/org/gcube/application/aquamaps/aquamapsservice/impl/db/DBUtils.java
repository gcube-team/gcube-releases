package org.gcube.application.aquamaps.aquamapsservice.impl.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DBUtils {
	public static String toJSon(ResultSet resultSet ) throws SQLException
	{
		
		StringBuilder json = new StringBuilder();

		json.append("{\"data\":[");

		ResultSetMetaData metaData = resultSet.getMetaData();
		int numberOfColumns = metaData.getColumnCount();

		int row = 0;
		while(resultSet.next()){

			
				
				if (row>0) json.append(",{");
				else json.append('{');

				for (int column = 1; column <=numberOfColumns; column++){
					if (column>1) json.append(',');					
					json.append(quote(metaData.getColumnName(column)));
					json.append(':');
					json.append(quote(resultSet.getString(column)));
				}

				json.append('}');
			
			row++;
		}

		json.append("],\"totalcount\":");
		json.append(row);
		json.append("}");

		return json.toString();

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
		StringBuilder json = new StringBuilder();

		json.append("{\"data\":[");

		ResultSetMetaData metaData = resultSet.getMetaData();
		int numberOfColumns = metaData.getColumnCount();

		int row = 0;

		while(resultSet.next()){

			if (row>=start && row <= end){
				
				if (row-start>0) json.append(",{");
				else json.append('{');

				for (int column = 1; column <=numberOfColumns; column++){
					if (column>1) json.append(',');
					json.append(quote(metaData.getColumnName(column)));
					json.append(':');
					json.append(quote(resultSet.getString(column)));
				}

				json.append('}');
			}
			row++;
		}

		json.append("],\"totalcount\":");
		json.append(row);
		json.append("}");

		return json.toString();

	}
	
	public static String toJSon(ResultSet resultSet, Long count) throws SQLException
	{
		
		StringBuilder json = new StringBuilder();

		json.append("{\"data\":[");

		ResultSetMetaData metaData = resultSet.getMetaData();
		int numberOfColumns = metaData.getColumnCount();

		int row = 0;

		while(resultSet.next()){

			
				
				if (row>0) json.append(",{");
				else json.append('{');

				for (int column = 1; column <=numberOfColumns; column++){
					if (column>1) json.append(',');
					json.append(quote(metaData.getColumnName(column)));
					json.append(':');
					json.append(quote(resultSet.getString(column)));
				}

				json.append('}');
			
			row++;
		}

		json.append("],\"totalcount\":");
		json.append(count);
		json.append("}");

		return json.toString();

	}
	
	
	
}
