package org.gcube.datatransfer.portlets.user.client.prop;

import org.gcube.datatransfer.portlets.user.client.obj.TreeOutcomes;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface TreeOutcomesProperties extends PropertyAccess<TreeOutcomes> {

	@Path("id")
	ModelKeyProvider<TreeOutcomes> key();
	   	
	ValueProvider<TreeOutcomes, String> sourceID();
	ValueProvider<TreeOutcomes, String> destID();
	ValueProvider<TreeOutcomes, String> readTrees();
	ValueProvider<TreeOutcomes, String> writtenTrees();
	ValueProvider<TreeOutcomes, String> exception();
	ValueProvider<TreeOutcomes, String> success();
	ValueProvider<TreeOutcomes, String> failure();
	ValueProvider<TreeOutcomes, String> totalMessage();
}