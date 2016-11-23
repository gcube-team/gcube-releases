package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;

public abstract class AquaMapsObjectExecutionRequest implements GenerationRequest{

	private Submitted object;

	private Set<Area> selectedArea;
	private BoundingBox bb;
	
	
	
	protected AquaMapsObjectExecutionRequest(Submitted object,
			Set<Area> selectedArea, BoundingBox bb) {
		super();
		this.object = object;
		this.selectedArea = selectedArea;
		this.bb = bb;
	}

	public void setObject(Submitted object) {
		this.object = object;
	}

	public Submitted getObject() {
		return object;
	}

	public Set<Area> getSelectedArea() {
		return selectedArea;
	}

	public void setSelectedArea(Set<Area> selectedArea) {
		this.selectedArea = selectedArea;
	}

	public BoundingBox getBb() {
		return bb;
	}

	public void setBb(BoundingBox bb) {
		this.bb = bb;
	}
	
	
}
