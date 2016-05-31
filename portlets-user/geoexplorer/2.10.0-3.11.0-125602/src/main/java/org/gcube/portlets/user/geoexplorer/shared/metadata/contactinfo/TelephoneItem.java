package org.gcube.portlets.user.geoexplorer.shared.metadata.contactinfo;

import java.util.Collection;

public class TelephoneItem {
	
	 /**
     * Telephone numbers by which individuals can speak to the responsible organization or
     * individual.
     */
    private Collection<String> voices;

    /**
     * Telephone numbers of a facsimile machine for the responsible organization or individual.
     */
    private Collection<String> facsimiles;

    /**
     * Constructs a default telephone.
     */
    public TelephoneItem() {
    }

	public TelephoneItem(Collection<String> voices, Collection<String> facsimiles) {
		this.voices = voices;
		this.facsimiles = facsimiles;
	}

	public Collection<String> getVoices() {
		return voices;
	}

	public void setVoices(Collection<String> voices) {
		this.voices = voices;
	}

	public Collection<String> getFacsimiles() {
		return facsimiles;
	}

	public void setFacsimiles(Collection<String> facsimiles) {
		this.facsimiles = facsimiles;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TelephoneItem [voices=");
		builder.append(voices);
		builder.append(", facsimiles=");
		builder.append(facsimiles);
		builder.append("]");
		return builder.toString();
	}

}
