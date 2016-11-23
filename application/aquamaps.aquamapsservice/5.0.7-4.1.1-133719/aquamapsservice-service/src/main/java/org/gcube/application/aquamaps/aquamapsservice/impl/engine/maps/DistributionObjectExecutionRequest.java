package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

import java.util.HashSet;
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
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType;

public class DistributionObjectExecutionRequest extends
		AquaMapsObjectExecutionRequest {

	
	private Set<Species> selectedSpecies=new HashSet<Species>();
	private Map<String,Perturbation> envelopeCustomization;
	private Map<EnvelopeFields,Field> envelopeWeights;
	private AlgorithmType algorithm=AlgorithmType.SuitableRange;
	
	
	public DistributionObjectExecutionRequest(Submitted object,
			Set<Area> selectedArea, BoundingBox bb,
			Set<Species> selectedSpecies,
			Map<String, Perturbation> envelopeCustomization,
			Map<EnvelopeFields, Field> envelopeWeights,AlgorithmType algorithm) {
		super(object, selectedArea, bb);
		this.selectedSpecies = selectedSpecies;
		this.envelopeCustomization = envelopeCustomization;
		this.envelopeWeights = envelopeWeights;
		this.algorithm=algorithm;
	}

	public Set<Species> getSelectedSpecies() {
		return selectedSpecies;
	}

	public void setSelectedSpecies(Set<Species> selectedSpecies) {
		this.selectedSpecies = selectedSpecies;
	}

	public Map<String, Perturbation> getEnvelopeCustomization() {
		return envelopeCustomization;
	}

	public void setEnvelopeCustomization(
			Map<String, Perturbation> envelopeCustomization) {
		this.envelopeCustomization = envelopeCustomization;
	}

	public Map<EnvelopeFields, Field> getEnvelopeWeights() {
		return envelopeWeights;
	}

	public void setEnvelopeWeights(Map<EnvelopeFields, Field> envelopeWeights) {
		this.envelopeWeights = envelopeWeights;
	}

	public void setAlgorithm(AlgorithmType algorithm) {
		this.algorithm = algorithm;
	}

	public AlgorithmType getAlgorithm() {
		return algorithm;
	}
	
	
}
