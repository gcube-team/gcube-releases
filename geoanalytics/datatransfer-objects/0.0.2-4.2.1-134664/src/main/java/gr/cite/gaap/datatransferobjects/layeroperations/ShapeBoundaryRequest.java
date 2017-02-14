package gr.cite.gaap.datatransferobjects.layeroperations;

import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.gaap.datatransferobjects.PrincipalMessenger;

public class ShapeBoundaryRequest {
	private TaxonomyTermMessenger layerTerm = null;
	private TaxonomyTermMessenger boundaryTerm = null;
	private PrincipalMessenger principalMessenger = null;
	
	public ShapeBoundaryRequest() { }
	
	public ShapeBoundaryRequest(TaxonomyTermMessenger layerTerm, TaxonomyTermMessenger boundaryTerm, PrincipalMessenger principalMessenger) {
		this.layerTerm = layerTerm;
		this.boundaryTerm = boundaryTerm;
		this.principalMessenger = principalMessenger;
	}
	
	public TaxonomyTermMessenger getLayerTerm() {
		return layerTerm;
	}

	public void setLayerTerm(TaxonomyTermMessenger layerTerm) {
		this.layerTerm = layerTerm;
	}

	public TaxonomyTermMessenger getBoundaryTerm() {
		return boundaryTerm;
	}

	public void setBoundaryTerm(TaxonomyTermMessenger boundaryTerm) {
		this.boundaryTerm = boundaryTerm;
	}

	public PrincipalMessenger getUserMessenger() {
		return principalMessenger;
	}

	public void setUserMessenger(PrincipalMessenger principalMessenger) {
		this.principalMessenger = principalMessenger;
	}
}