package org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors;

import java.util.ArrayList;
import java.util.List;

public class EnvelopeSet {

	private List<Envelope> envelopes;
	private String envelopeString;
	
	public EnvelopeSet(){
		envelopes = new ArrayList<Envelope>();
		envelopeString = "";
	}

	public void setEnvelopeString(String envelopeString) {
		this.envelopeString = envelopeString;
	}

	public String getEnvelopeString() {
		return envelopeString;
	}

	public void setEnvelopes(List<Envelope> envelopes) {
		this.envelopes = envelopes;
	}

	public List<Envelope> getEnvelopes() {
		return envelopes;
	}
	
	public void addEnvelope(Envelope e){
		envelopes.add(e);
	}
	
	
}
