package org.gcube.portlets.user.td.taskswidget.client.manager;

import java.util.List;

import org.gcube.portlets.user.td.taskswidget.client.panel.result.ResultCollateralTablePanel;
import org.gcube.portlets.user.td.taskswidget.client.panel.result.ResultTabularDataPanel;
import org.gcube.portlets.user.td.taskswidget.shared.TdTableModel;
import org.gcube.portlets.user.td.taskswidget.shared.TdTabularResourceModel;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 20, 2013
 *
 */
public class ResultsManager {
	

	private ResultCollateralTablePanel resCollateralTablePanel;
	private ResultTabularDataPanel resTabularDataPanel;
	private ResultsLoader resultLoader;

	/**
	 * 
	 * @param loader
	 */
	public ResultsManager(ResultsLoader loader) {
		this.resultLoader = loader;
		resCollateralTablePanel = new ResultCollateralTablePanel();
		resTabularDataPanel = new ResultTabularDataPanel();
	}

	public ResultCollateralTablePanel getResCollateralTablePanel() {
		return resCollateralTablePanel;
	}

	public ResultTabularDataPanel getResTabularDataPanel() {
		return resTabularDataPanel;
	}
	
	public void bindLoader(ResultsLoader loader){
		this.resultLoader = loader;
	}

	/**
	 * @param listCollateralTRModel
	 * @param tdTableModel
	 */
	public void updateResults(List<TdTabularResourceModel> listCollateralTRModel, TdTableModel tdTableModel) {
		updateResult(listCollateralTRModel);
		updateResult(tdTableModel);	
	}
	
	/**
	 * @param listCollateralTRModel
	 * @param tdTableModel
	 */
	public void updateResult(List<TdTabularResourceModel> listCollateralTRModel) {
		if(listCollateralTRModel!=null && listCollateralTRModel.size()>0){
			resCollateralTablePanel.upateFormFields(listCollateralTRModel);
			resultLoader.fireCollateralsFieldsUpdated(true);
		}
		else
			resultLoader.fireCollateralsFieldsUpdated(false);
	}
	
	/**
	 * @param listCollateralTRModel
	 * @param tdTableModel
	 */
	public void updateResult(TdTableModel tdTableModel) {
		if(tdTableModel!=null){
			resTabularDataPanel.upateFormFields(tdTableModel);
			resultLoader.fireTabularDataFieldsUpdated(true);
		}else
			resultLoader.fireTabularDataFieldsUpdated(false);
		
	}

}
