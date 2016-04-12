package org.gcube.datatransfer.portlets.user.client.prop;

import org.gcube.datatransfer.portlets.user.client.obj.AgentStat;
import org.gcube.datatransfer.portlets.user.client.obj.Uri;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface AgentStatProperties extends PropertyAccess<Uri> {

	  @Path("id")
	  ModelKeyProvider<AgentStat> key();
	   
	  @Path("endpoint")
	  LabelProvider<AgentStat> nameLabel();
		
	ValueProvider<AgentStat, String> endpoint();
	ValueProvider<AgentStat, String> ongoing();
	ValueProvider<AgentStat, String> failed();
	ValueProvider<AgentStat, String> succesful();
	ValueProvider<AgentStat, String> canceled();
	ValueProvider<AgentStat, String> total();
}