package org.gcube.application.datamanagementfacilityportlet.client.rpc.data;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ClientTinyResource extends BaseModelData implements IsSerializable{

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4960233015364889156L;
	public ClientTinyResource() {
		// TODO Auto-generated constructor stub
	}
	public ClientTinyResource(Integer id,String type,String title,String sourceHcafIds){
		setId(id);
		setTitle(title);
		setType(type);
		setSourceHCAFIds(sourceHcafIds);
	}
	
	public String getTitle(){return get(ClientResource.TITLE);}
	public String getType(){return get(ClientResource.TYPE);}
	public Integer getId(){return get(ClientResource.SEARCH_ID);}
	public void setTitle(String title){set(ClientResource.TITLE,title);}
	public void setType(String type){set(ClientResource.TYPE,type);}
	public void setId(Integer id){set(ClientResource.SEARCH_ID,id);}
	public void setSourceHCAFIds(String ids){set(ClientResource.SOURCE_HCAF,ids);}
	public String getSourceHcafIds(){return get(ClientResource.SOURCE_HCAF);}
	
}
