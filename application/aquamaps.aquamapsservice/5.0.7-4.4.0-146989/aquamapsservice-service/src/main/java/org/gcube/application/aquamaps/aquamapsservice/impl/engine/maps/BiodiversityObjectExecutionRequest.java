package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.AquaMapsObjectExecutionRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Perturbation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.EnvelopeFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;

public class BiodiversityObjectExecutionRequest extends
		AquaMapsObjectExecutionRequest {

	private float threshold;
	private Set<Species> selectedSpecies;
	private Map<String,Map<String,Perturbation>> envelopeCustomization=new HashMap<String, Map<String,Perturbation>>();
	private Map<String,Map<EnvelopeFields,Field>> envelopeWeights=new HashMap<String, Map<EnvelopeFields,Field>>();
	
	public BiodiversityObjectExecutionRequest(Submitted object,
			Set<Area> selectedArea, BoundingBox bb, float threshold,
			Set<Species> selectedSpecies,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights) {
		super(object, selectedArea, bb);
		this.threshold = threshold;
		this.selectedSpecies = selectedSpecies;
		this.envelopeCustomization = envelopeCustomization;
		this.envelopeWeights = envelopeWeights;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public Set<Species> getSelectedSpecies() {
		return selectedSpecies;
	}

	public void setSelectedSpecies(Set<Species> selectedSpecies) {
		this.selectedSpecies = selectedSpecies;
	}

	public Map<String, Map<String, Perturbation>> getEnvelopeCustomization() {
		return envelopeCustomization;
	}

	public void setEnvelopeCustomization(
			Map<String, Map<String, Perturbation>> envelopeCustomization) {
		this.envelopeCustomization = envelopeCustomization;
	}

	public Map<String, Map<EnvelopeFields, Field>> getEnvelopeWeights() {
		return envelopeWeights;
	}

	public void setEnvelopeWeights(
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights) {
		this.envelopeWeights = envelopeWeights;
	}
	
	
}
