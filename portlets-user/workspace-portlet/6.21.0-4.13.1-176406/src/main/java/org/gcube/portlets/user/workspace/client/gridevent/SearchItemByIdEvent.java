package org.gcube.portlets.user.workspace.client.gridevent;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer.WsPortletInitOperation;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 6, 2013
 *
 */
public class SearchItemByIdEvent extends GwtEvent<SearchItemByIdEventHandler> {
	public static Type<SearchItemByIdEventHandler> TYPE = new Type<SearchItemByIdEventHandler>();

	private String itemId;
	
	private WsPortletInitOperation operationParameter = null;
	
	
	public SearchItemByIdEvent(String itemId, String op) {
		this.itemId = itemId;
		
		if(op!=null){
			
			if(op.compareToIgnoreCase(WsPortletInitOperation.sharelink.toString())==0)
				operationParameter = WsPortletInitOperation.sharelink;
			else if(op.compareToIgnoreCase(WsPortletInitOperation.gotofolder.toString())==0)
				operationParameter = WsPortletInitOperation.gotofolder;
			
			else
				operationParameter = ConstantsExplorer.DEFAULT_OPERATION; //DEFAULT OPERATION
			
		}else
		
		if(operationParameter==null)
			operationParameter = ConstantsExplorer.DEFAULT_OPERATION; //DEFAULT OPERATION
		
	}

	@Override
	public Type<SearchItemByIdEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SearchItemByIdEventHandler handler) {
		handler.onSearchItemById(this);

	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public WsPortletInitOperation getOperationParameter() {
		return operationParameter;
	}

	public void setOperationParameter(WsPortletInitOperation operationParameter) {
		this.operationParameter = operationParameter;
	}

}