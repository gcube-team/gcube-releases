package org.gcube.portlets.user.td.tablewidget.client.validation.tree;



import java.util.ArrayList;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TaskSDto  extends FolderDto {

	private static final long serialVersionUID = 4644048540524701598L;
	
	public TaskSDto(){
		
	}
	
	public TaskSDto(String type,String id,String description,  ArrayList<BaseDto> childrens){
		super(type,id,description,childrens);
		
	}	

}
