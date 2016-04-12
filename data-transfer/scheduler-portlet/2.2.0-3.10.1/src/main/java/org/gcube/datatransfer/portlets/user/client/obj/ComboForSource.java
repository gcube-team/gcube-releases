package org.gcube.datatransfer.portlets.user.client.obj;

import org.gcube.datatransfer.portlets.user.client.Common.SOURCETYPE;

import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

//now it is only used for storing the source type 
public class ComboForSource extends SimpleComboBox<String>{

	public ComboForSource(LabelProvider<? super String> labelProvider) {
		super(labelProvider);
	}
	public boolean isTreeBased(){
		if(this.getCurrentValue()==null)return false;
		if(this.getCurrentValue().compareTo(SOURCETYPE.TreeBased.toString())==0){
			return true;
		}
		return false;
	}
	public boolean isMongoDB(){
		if(this.getCurrentValue()==null)return false;
		if(this.getCurrentValue().compareTo(SOURCETYPE.MongoDB.toString())==0){
			return true;
		}
		return false;
	}
	public boolean isDatasource(){
		if(this.getCurrentValue()==null)return false;
		if(this.getCurrentValue().compareTo(SOURCETYPE.DataSource.toString())==0){
			return true;
		}
		return false;
	}
	public boolean isWorkspace(){
		if(this.getCurrentValue()==null)return false;
		if(this.getCurrentValue().compareTo(SOURCETYPE.Workspace.toString())==0){
			return true;
		}
		return false;
	}
	public boolean isURI(){
		if(this.getCurrentValue()==null)return false;
		if(this.getCurrentValue().compareTo(SOURCETYPE.URI.toString())==0){
			return true;
		}
		return false;
	}
	public boolean isAgentSource(){
		if(this.getCurrentValue()==null)return false;
		if(this.getCurrentValue().compareTo(SOURCETYPE.AgentSource.toString())==0){
			return true;
		}
		return false;
	}

}
