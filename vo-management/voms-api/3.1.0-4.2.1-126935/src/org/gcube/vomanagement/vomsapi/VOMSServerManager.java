package org.gcube.vomanagement.vomsapi;

import java.util.List;

import org.gcube.vomanagement.vomsapi.impl.utils.VOMSServerBean;

public interface VOMSServerManager 
{
	public void addServer (VOMSServerBean serverBean);
	
	public void setServerList (List<VOMSServerBean> serverList);
}
