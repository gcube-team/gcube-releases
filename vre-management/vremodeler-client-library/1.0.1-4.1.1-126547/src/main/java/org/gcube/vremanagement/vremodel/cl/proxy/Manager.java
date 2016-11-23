package org.gcube.vremanagement.vremodel.cl.proxy;

import java.util.Calendar;
import java.util.List;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityNodes;
import org.gcube.vremanagement.vremodel.cl.stubs.types.SelectedResourceDescriptionType;
import org.gcube.vremanagement.vremodel.cl.stubs.types.VREDescription;
import org.gcube.vremanagement.vremodeler.utils.reports.DeployReport;

public interface Manager {

	void setDescription(String name, String description, String designer, String manager, Calendar startTime, Calendar endTime);

	VREDescription getDescription();

	void setUseCloud(boolean useCloud);

	void setCloudVMs(int vms);

	int getCloudVMs();

	boolean isUseCloud();

	String getQuality();

	void setQuality(String quality);

	List<FunctionalityItem> getFunctionalities();

	void setFunctionality(List<Integer> functionalityIds, List<SelectedResourceDescriptionType> resourceDescriptions);

	FunctionalityNodes getFunctionalityNodes();

	void setGHNs(List<String> ghns);

	void setVREtoPendingState();

	void deployVRE();

	void undeployVRE();

	void renewVRE(Calendar untilDate);

	DeployReport checkStatus();

	
}
