package org.gcube.datatransfer.portlets.user.client.obj;

import org.gcube.datatransfer.portlets.user.client.Common.DESTTYPE;
import org.gcube.datatransfer.portlets.user.client.Common.SOURCETYPE;

import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

//now it is only used for storing the source type 
public class ComboForDestination extends SimpleComboBox<String>{

	public ComboForDestination(LabelProvider<? super String> labelProvider) {
		super(labelProvider);
	}
	public boolean isTreeBased(){
		if(this.getCurrentValue()==null)return false;
		if(this.getCurrentValue().compareTo(DESTTYPE.TreeBased.toString())==0){
			return true;
		}
		return false;
	}
	public boolean isMongoDBStorage(){
		if(this.getCurrentValue()==null)return false;
		if(this.getCurrentValue().compareTo(DESTTYPE.MongoDBStorage.toString())==0){
			return true;
		}
		return false;
	}
	public boolean isAgentDest(){
		if(this.getCurrentValue()==null)return false;
		if(this.getCurrentValue().compareTo(DESTTYPE.AgentDest.toString())==0){
			return true;
		}
		return false;
	}
	public boolean isDataStorage(){
		if(this.getCurrentValue()==null)return false;
		if(this.getCurrentValue().compareTo(DESTTYPE.DataStorage.toString())==0){
			return true;
		}
		return false;
	}

}
