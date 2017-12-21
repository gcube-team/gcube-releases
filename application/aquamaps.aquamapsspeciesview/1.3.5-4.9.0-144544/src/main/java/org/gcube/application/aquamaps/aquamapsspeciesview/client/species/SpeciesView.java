package org.gcube.application.aquamaps.aquamapsspeciesview.client.species;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.Reloadable;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.button.Button;

public interface SpeciesView extends Reloadable{

	public void bindToSelection(final Button toBind);
	public List<ModelData> getSelection();
}
