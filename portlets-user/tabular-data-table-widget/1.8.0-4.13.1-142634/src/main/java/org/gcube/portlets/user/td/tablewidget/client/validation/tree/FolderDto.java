package org.gcube.portlets.user.td.tablewidget.client.validation.tree;



import java.util.ArrayList;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class FolderDto  extends BaseDto {

	private static final long serialVersionUID = 4644048540524701598L;
	protected String description;
	protected ArrayList<BaseDto> childrens;
	protected String type;
	
	public FolderDto(){
		
	}
	
	public FolderDto(String type,String id,String description,  ArrayList<BaseDto> childrens){
		super(id);
		this.type=type;
		this.description=description;
		this.childrens=childrens;
	}
	
	
	
	public ArrayList<BaseDto> getChildrens() {
		return childrens;
	}

	public void setChildrens(ArrayList<BaseDto> childrens) {
		this.childrens = childrens;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public String toString() {
		return description;
	}

	
	

}
