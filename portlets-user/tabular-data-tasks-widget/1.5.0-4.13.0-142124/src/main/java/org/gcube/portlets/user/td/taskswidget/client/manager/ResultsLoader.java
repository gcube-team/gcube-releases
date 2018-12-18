/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.manager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Dec 6, 2013
 *
 */
public class ResultsLoader {

	
	private List<ResultsLoaderInterface> listeners;

	/**
	 * 
	 */
	public ResultsLoader() {
		this.listeners = new ArrayList<ResultsLoaderInterface>(1);
	}
	
	public void addListner(ResultsLoaderInterface listner){
		this.listeners.add(listner);
	}
	
	protected void fireTabularDataFieldsUpdated(boolean b)
	{
		for (ResultsLoaderInterface listener:listeners) listener.onResulTabulartUpdated(b);
	}
	
	protected void fireCollateralsFieldsUpdated(boolean b)
	{
		for (ResultsLoaderInterface listener:listeners) listener.onResulCollateralUpdated(b);
	}
}
