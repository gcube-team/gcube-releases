package org.gcube.portlets.user.reportgenerator.client.targets;

import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.Metadata;

import com.google.gwt.user.client.ui.Hidden;

public class ExtHidden extends Hidden {
	public ExtHidden(String name, String value) {
		super(name, value);	
	}

	private List<Metadata> metas;
	
	public List<Metadata> getMetadata() {
		return metas;
	}

	public void setMetadata(List<Metadata> metas) {
		this.metas = metas;
	}

}
