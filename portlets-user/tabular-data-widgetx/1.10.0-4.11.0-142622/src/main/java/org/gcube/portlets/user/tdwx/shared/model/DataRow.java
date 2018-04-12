/**
 * 
 */
package org.gcube.portlets.user.tdwx.shared.model;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class DataRow {
	
	protected Object[] data;
	
	public DataRow(int size)
	{
		data = new Object[size];
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(ColumnKey key)
	{
		try{
		return (T) data[key.getIndex()];
		} catch (Exception e)
		{
			System.out.println("key: "+key);
			System.out.println("data size: "+data.length);
			e.printStackTrace();
			return null;
		}
	}
	
	public <T> void set(ColumnKey key, T value)
	{
		data[key.getIndex()] = value;
	}

}
