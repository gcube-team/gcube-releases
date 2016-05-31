package org.gcube.portlets.user.gisviewer.client.datafeature;

public interface DataPanelHandler {
	
	public void showDataPanel();
	
	public void exportData(boolean force);

	public void dataPanelOpen(boolean isOpen, int panelHeight);
	
}
