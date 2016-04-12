package org.gcube.datatransfer.portlets.user.client.prop;

import org.gcube.datatransfer.portlets.user.client.obj.Outcomes;
import org.gcube.datatransfer.portlets.user.client.obj.Uri;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface OutcomesProperties extends PropertyAccess<Outcomes> {

	@Path("id")
	ModelKeyProvider<Outcomes> key();
	   
	@Path("fileName")
	LabelProvider<Outcomes> nameLabel();
		
	ValueProvider<Outcomes, String> fileName();
	ValueProvider<Outcomes, String> destination();
	ValueProvider<Outcomes, String> success();
	ValueProvider<Outcomes, String> failure();
	ValueProvider<Outcomes, String> transferTime();
	ValueProvider<Outcomes, String> size();
	ValueProvider<Outcomes, String> transferredBytes();
	ValueProvider<Outcomes, String> exception();
	ValueProvider<Outcomes, String> totalMessage();

}