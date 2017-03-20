package gr.uoa.di.madgik.hive.representation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class TableDesc {
	private String name;
	private String source;
	private LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>();
	private String delimiter;

	public TableDesc(String name, String delimiter) {
		this.name = name;
		this.delimiter = delimiter;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Add a column to existing table
	 * 
	 * @param name
	 *            Name of the column
	 * @param type
	 *            Type of the column
	 * @return false if column already exists
	 */
	public boolean addColumn(String name, String type) {
		if (columns.get(name) != null)
			return false;

		columns.put(name, type);
		return true;
	}

	/**
	 * @return the columns
	 */
	public LinkedHashMap<String, String> getColumns() {
		return columns;
	}
	
	/**
	 * @return the column names in an ordered list
	 */
	public List<String> getColumnNames() {
		List<String> list = new ArrayList<String>();
		list.addAll(columns.keySet());
		
		return list;
	}

	@Override
	public String toString() {
		String str = name;
		str += "[";
		for (Entry<String, String> el : columns.entrySet())
			str += el.getKey() + ", ";

		if (!columns.isEmpty())
			str = str.substring(0, str.length() - 2);

		return str + "]";
	}
}
