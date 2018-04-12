package org.gcube.contentmanagement.timeseries.geotools.gisconnectors;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.geoserverinterface.GeoCaller;
import org.gcube.common.geoserverinterface.bean.CswRecord;
import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class GISGroupInformation {

	private String groupName;
	private String templateGroupName;
	private boolean isTemplateGroup;

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setTemplateGroupName(String templateGroupName) {
		this.templateGroupName = templateGroupName;
	}

	public String getTemplateGroupName() {
		return templateGroupName;
	}

	public void setTemplateGroup(boolean isTemplateGroup) {
		this.isTemplateGroup = isTemplateGroup;
	}

	public boolean isTemplateGroup() {
		return isTemplateGroup;
	}

	public static boolean checkGroup(GISInformation gisInfo, String groupName, int maxtries) throws Exception {
		boolean urlcoherence = false;
		GISOperations operations = new GISOperations();
		
		
		GeoCaller geoCaller = operations.getGeoCaller(gisInfo);
		
		String geourl = geoCaller.getGeoServerForGroup(groupName);
		
		String checkurl = geourl + "/rest/layergroups/" + groupName + ".json";

			for (int i = 0; i < maxtries; i++) {
				AnalysisLogger.getLogger().debug("checkGroup->Checking Group on geoserver " + checkurl);
				int result = HttpRequest.checkUrl(checkurl, gisInfo.getGisUserName(), gisInfo.getGisPwd());
				AnalysisLogger.getLogger().debug("checkGroup->Cached Group Checking " + result);
				if (result == 200) {
					urlcoherence = true;
					AnalysisLogger.getLogger().debug("checkGroup->GROUP PRESENT IN " + geourl);
					break;
				}
			}
		
		// System.exit(0);
		return urlcoherence;
	}

	
	public static boolean checkLayers(GISInformation gisInfo, List<String> layerNames, int maxtries) throws Exception {
		boolean urlcoherence = false;
		GISOperations operations = new GISOperations();
		
		GeoCaller geoCaller = operations.getGeoCaller(gisInfo);
		for (String layerName:layerNames) {
		String geourl = geoCaller.getGeoServerForLayer(layerName);
		
		String checkurl = geourl + "/rest/layers/" + layerName + ".json";

			for (int i = 0; i < maxtries; i++) {
				AnalysisLogger.getLogger().debug("checkLayers->Checking layer on geoserver " + checkurl);
				int result = HttpRequest.checkUrl(checkurl, gisInfo.getGisUserName(), gisInfo.getGisPwd());
				AnalysisLogger.getLogger().debug("checkLayers->Cached Layers Checking " + result);
				if (result == 200) {
					urlcoherence = true;
					AnalysisLogger.getLogger().debug("checkLayers->Layers PRESENT IN " + geourl);
					break;
				}
				else{
					urlcoherence = false;
				}
			}
		}
		// System.exit(0);
		return urlcoherence;
	}
}
